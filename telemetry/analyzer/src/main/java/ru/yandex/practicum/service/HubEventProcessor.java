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
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.dal.repository.SensorRepository;
import ru.yandex.practicum.dal.service.AnalyzerService;

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
    private final AnalyzerService service;

    public HubEventProcessor(@Qualifier(value = "consumerHub") Consumer<String, SpecificRecordBase> consumer,
                             @Value(value = "${analyzer.kafka.collector-topic-hub}") String hubTopic,
                             AnalyzerService service) {
        this.consumer = consumer;
        this.hubTopic = hubTopic;
        this.service = service;
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

                    log.info("Анализатор получил пользовательский сценарий от хаба {} {} {}", hubEventAvro.getHubId(),
                            hubEventAvro.getTimestamp(), hubEventAvro.getPayload());

                    Object payload = hubEventAvro.getPayload();

                    try {
                        switch (payload) {
                            case DeviceAddedEventAvro deviceAdded -> {
                                service.addDevice(deviceAdded, hubEventAvro.getHubId());
                            }
                            case DeviceRemovedEventAvro deviceRemoved -> {
                                service.removeDevice(deviceRemoved);
                            }
                            case ScenarioAddedEventAvro scenarioAdded -> {
                                service.addScenario(scenarioAdded, hubEventAvro.getHubId());
                            }
                            case ScenarioRemovedEventAvro scenarioRemoved -> {
                                service.removeScenario(scenarioRemoved);
                            }
                            default -> {
                                log.info("Такого сценария не существует: {}", payload.getClass().getSimpleName());
                            }
                        }
                    } catch (Exception e) {
                        log.warn("Произошло исключение: " + e.getClass() + " " + e.getMessage());
                    }

                    manageOffsets(record, count, consumer);
                    count++;
                }
                consumer.commitAsync();
            }
        } catch (WakeupException ignore) {
        } catch (Exception e) {
            log.error("Произошла ошибка при обработке пользовательского сценария от хаба ", e);
        } finally {
            try {
                consumer.commitSync(currentOffsets);
            } finally {
                log.info("Закрытие consumer");
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
                    log.warn("Ошибка при фиксации смещений offsets: {}", offsets, exception);
                }
            });
        }
    }
}