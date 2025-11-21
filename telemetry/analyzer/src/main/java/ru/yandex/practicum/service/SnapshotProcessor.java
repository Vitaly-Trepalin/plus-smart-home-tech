package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SnapshotProcessor {
    private final Consumer<String, SpecificRecordBase> snapshotConsumer;
    private Map<TopicPartition, OffsetAndMetadata> currentOffset = new HashMap<>();
    @Value(value = "${kafka.config.consume-attempt-timeout}")
    private long consumeAttemptTimeout;
    @Value(value = "${kafka.config.topic-snapshots}")
    private String topicSnapshots;
    private final SnapshotService service;

    public void start() {
        snapshotConsumer.subscribe(List.of(topicSnapshots));
        Runtime.getRuntime().addShutdownHook(new Thread(snapshotConsumer::wakeup));

        try {
            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = snapshotConsumer.poll(Duration.ofMillis(consumeAttemptTimeout));

                int count = 0;
                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    SensorsSnapshotAvro snapshot = (SensorsSnapshotAvro) record.value();
                    log.info("Получен снапшот от aggregator: {}", snapshot);

                    try {
                        service.executingSnapshot(snapshot);
                    } catch (Exception e) {
                        log.warn("При обработке входящего сообщения от hub возникло исключение: {}", e.getMessage());
                    }

                    manageOffsets(record, count);
                    count++;
                }
                snapshotConsumer.commitAsync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Произошла ошибка при обработке снапшотов {}", e.getMessage());
        } finally {
            try {
                snapshotConsumer.commitSync(currentOffset);
            } finally {
                snapshotConsumer.close();
                log.info("Закрываем consumer");
            }
        }
    }

    private void manageOffsets(ConsumerRecord<String, SpecificRecordBase> record, int count) {
        currentOffset.put(new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1));

        if (count % 10 == 0) {
            snapshotConsumer.commitAsync(currentOffset, (offset, exception) -> {
                if (exception != null) {
                    log.error("Ошибка фиксации смещений {}", offset, exception);
                }
            });
        }
    }
}