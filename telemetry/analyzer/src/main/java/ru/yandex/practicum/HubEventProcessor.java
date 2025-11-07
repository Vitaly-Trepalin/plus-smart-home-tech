package ru.yandex.practicum;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.repository.SensorRepository;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class HubEventProcessor implements Runnable {
    private final Consumer<String, SpecificRecordBase> consumer;
    private final String hubTopic;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    private final SensorRepository sensorRepository;

    public HubEventProcessor(@Qualifier(value = "consumerHub") Consumer<String, SpecificRecordBase> consumer,
                             @Value(value = "${analyzer.kafka.collector-topic-hub}") String hubTopic,
                             SensorRepository sensorRepository) {
        this.consumer = consumer;
        this.hubTopic = hubTopic;
        this.sensorRepository = sensorRepository;
    }

    @Override
    public void run() {
        try {
            consumer.subscribe(List.of(hubTopic));
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(100));

                int count = 0;
                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    HubEventAvro hubEventAvro = (HubEventAvro) record.value();

                    log.info("Анализатор получил пользовательский сценарий от хаба " + hubEventAvro.getHubId() +
                            hubEventAvro.getTimestamp() +
                            hubEventAvro.getPayload());

                    manageOffsets(record, count, consumer);
                    count++;
                }
                consumer.commitAsync();
            }
        } catch (WakeupException ignore) {
        } catch (Exception e) {
            log.error("Ошибка во время обработки снапшотов ", e);
        } finally {
            try {
                consumer.commitSync(currentOffsets);
            } finally {
                log.info("Закрываем consumer");
                consumer.close();
            }
        }
    }

    private void manageOffsets(ConsumerRecord<String, SpecificRecordBase> record, int count, Consumer<String,
            SpecificRecordBase> consumer) {
        currentOffsets.put(new TopicPartition(hubTopic, record.partition()),
                new OffsetAndMetadata(record.offset() + 1));
        if (count % 10 == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.warn("Ошибка во время фиксации оффсетов: {}", offsets, exception);
                }
            });
        }
    }
}
