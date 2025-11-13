package ru.yandex.practicum.service;

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
import ru.yandex.practicum.dal.service.AnalyzerService;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
public class SnapshotProcessor {
    private final Consumer<String, SpecificRecordBase> consumer;
    private final String snapshotsTopic; //под вопросом
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    private final AnalyzerService analyzerService;
    private final HubRouterClient hubRouterClient;

    public SnapshotProcessor(@Qualifier(value = "consumerSnapshot") Consumer<String, SpecificRecordBase> consumer,
                             @Value(value = "${analyzer.kafka.snapshots-topic}") String snapshotsTopic,
                             AnalyzerService analyzerService,
                             HubRouterClient hubRouterClient) {
        this.consumer = consumer;
        this.snapshotsTopic = snapshotsTopic;
        this.analyzerService = analyzerService;
        this.hubRouterClient = hubRouterClient;
    }

    public void start() {
        try {
            consumer.subscribe(List.of(snapshotsTopic));
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(100));

                int count = 0;
                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    SensorsSnapshotAvro sensorsSnapshotAvro = (SensorsSnapshotAvro) record.value();

                    log.info("Анализатор получил пользовательский снапшот от хаба " + sensorsSnapshotAvro.getHubId() +
                            sensorsSnapshotAvro.getSensorsState().values());


                    List<DeviceActionRequest> deviceActionRequest = List.of();
                    try {
                        deviceActionRequest = analyzerService.executingSnapshot(sensorsSnapshotAvro);
                    } catch (Exception e) {
                        log.info("Исключение в ходе выполнения обработки исключения " + e.getMessage());
                    }
                    deviceActionRequest.stream()
                            .filter(Objects::nonNull)
                            .forEach(hubRouterClient::sendDeviceAction);

                    manageOffsets(record, count, consumer);
                    count++;
                }
                consumer.commitAsync();
            }
        } catch (WakeupException ignore) {
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий добавления/удаления устройств и сценариев ", e);
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
        currentOffsets.put(new TopicPartition(snapshotsTopic, record.partition()),
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