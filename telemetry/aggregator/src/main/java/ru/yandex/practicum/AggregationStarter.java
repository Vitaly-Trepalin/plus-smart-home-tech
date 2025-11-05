package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class AggregationStarter {
    @Value(value = "${aggregator.kafka.snapshots-topic}")
    private String snapshotsTopic;
    @Value(value = "${aggregator.kafka.collector-topic-sensor}")
    private String telemetrySensors;
    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    private static final Map<String, SensorsSnapshotAvro> sensorsSnapshot = new HashMap<>();
    private final Producer<String, SpecificRecordBase> producer;
    private final Consumer<String, SpecificRecordBase> consumer;

    public void start() {
        try {
            consumer.subscribe(List.of(telemetrySensors));
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(100));

                int count = 0;
                for (ConsumerRecord<String, SpecificRecordBase> record : records) {

                    SensorEventAvro sensorEventAvro = (SensorEventAvro) record.value();
                    ProducerRecord<String, SpecificRecordBase> producerRecord = null;

                    Optional<SensorsSnapshotAvro> snapshot = updateState(sensorEventAvro);
                    if (snapshot.isPresent()) {
                        producerRecord = new ProducerRecord<>(
                                snapshotsTopic,
                                null,
                                Instant.now().toEpochMilli(),
                                record.key(),
                                snapshot.get()
                        );

                        manageOffsets(record, count, consumer);
                        producer.send(producerRecord);
                    }
                }
                consumer.commitAsync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            try {
                producer.flush();
                consumer.commitSync(currentOffsets);
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
                producer.close();
            }
        }
    }

    private static void manageOffsets(ConsumerRecord<String, SpecificRecordBase> record, int count,
                                      Consumer<String, SpecificRecordBase> consumer) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if (count % 10 == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.warn("Ошибка во время фиксации оффсетов: {}", offsets, exception);
                }
            });
        }
    }

    private static Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        SensorsSnapshotAvro snapshot;
        if (sensorsSnapshot.containsKey(event.getHubId())) { // проверяем есть ли снимок датчиков
            snapshot = sensorsSnapshot.get(event.getHubId()); // если есть, то присваеваем его переменной
        } else {
            SensorStateAvro sensorState = new SensorStateAvro(event.getTimestamp(), event.getPayload());
            snapshot = new SensorsSnapshotAvro(
                    event.getHubId(),
                    Instant.now(),
                    Map.of(event.getId(), sensorState)
            );
            sensorsSnapshot.put(event.getHubId(), snapshot);
        }
        SensorStateAvro oldState = null;
        if (snapshot.getSensorsState().containsKey(event.getId())) { // есть ли в данном снимке датчиков такой же как нам пришёл
            oldState = snapshot.getSensorsState().get(event.getId()); // достаём старый датчик с таким же id
            if (oldState.getTimestamp().isAfter(event.getTimestamp()) && // сравниваем их по времени и содержимому
                    oldState.getData().equals(event.getPayload())) {
                return Optional.empty(); // если изменения не требуются ,то возвращаем пустой Optional
            }
        }
        SensorStateAvro newSensorStateAvro = new SensorStateAvro(event.getTimestamp(), event.getPayload()); // добавляем новое состояние датчика
        Map<String, SensorStateAvro> updatedSensorsState = new HashMap<>(snapshot.getSensorsState()); // получаем хэш таблицу с датчика
        updatedSensorsState.put(event.getId(), newSensorStateAvro); // и устанавливаем новое состояние датчика
        snapshot.setSensorsState(updatedSensorsState); // обновляем состояние датчиков
        snapshot.setTimestamp(event.getTimestamp()); // обновляем время снимка по времени датчика
        return Optional.of(snapshot);
    }
}