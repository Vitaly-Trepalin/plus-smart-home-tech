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
    private final Producer<String, SpecificRecordBase> producer;
    private final Consumer<String, SpecificRecordBase> consumer;
    @Value(value = "${aggregator.kafka.snapshots-topic}")
    private String snapshotsTopic;
    @Value(value = "${aggregator.kafka.collector-topic-sensor}")
    private String telemetrySensors;
    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    private static final Map<String, SensorsSnapshotAvro> sensorsSnapshot = new HashMap<>();

    public void start() {
        try {
            consumer.subscribe(List.of(telemetrySensors));
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(100));

                int count = 0;
                for (ConsumerRecord<String, SpecificRecordBase> record : records) {

                    SensorEventAvro sensorEventAvro = (SensorEventAvro) record.value();
                    log.warn("Получил событие датчика {}, {}", sensorEventAvro.getId(), sensorEventAvro.getPayload());

                    ProducerRecord<String, SpecificRecordBase> producerRecord;

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
                        log.info("Подготовлен снапшот и отправлен в Analyzer " + producerRecord);
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
                log.info("Закрываем consumer");
                consumer.close();
                log.info("Закрываем producer");
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
        if (sensorsSnapshot.containsKey(event.getHubId())) {
            snapshot = sensorsSnapshot.get(event.getHubId());
        } else {
            SensorStateAvro sensorState = new SensorStateAvro(event.getTimestamp(), event.getPayload());

            Map<String, SensorStateAvro> sensorsState = new HashMap<>();
            sensorsState.put(event.getId(), sensorState);

            snapshot = new SensorsSnapshotAvro(
                    event.getHubId(),
                    Instant.now(),
                    sensorsState
            );
            sensorsSnapshot.put(event.getHubId(), snapshot);
            return Optional.of(snapshot);
        }

        if (snapshot.getSensorsState().containsKey(event.getId())) {
            SensorStateAvro oldState = snapshot.getSensorsState().get(event.getId());
            boolean isOutdated = oldState.getTimestamp().isAfter(event.getTimestamp());
            boolean isDuplicate = !oldState.getTimestamp().isAfter(event.getTimestamp())
                    && oldState.getData().equals(event.getPayload());
            if (isOutdated || isDuplicate) {
                return Optional.empty();
            }
        }

        SensorStateAvro newSensorStateAvro = new SensorStateAvro(event.getTimestamp(), event.getPayload());
        Map<String, SensorStateAvro> updatedSensorsState = new HashMap<>(snapshot.getSensorsState());
        updatedSensorsState.put(event.getId(), newSensorStateAvro);
        snapshot.setSensorsState(updatedSensorsState);
        snapshot.setTimestamp(event.getTimestamp());
        return Optional.of(snapshot);
    }
}