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
import ru.yandex.practicum.dal.service.HubEventService;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class HubEventProcessor implements Runnable {
    private final Consumer<String, SpecificRecordBase> hubConsumer;
    private Map<TopicPartition, OffsetAndMetadata> currentOffset = new HashMap<>();
    @Value(value = "${kafka.config.topic-hubs}")
    private String topicHub;
    @Value(value = "${kafka.config.consume-attempt-timeout}")
    private long consumeAttemptTimeout;
    private final HubEventService hubEventService;

    @Override
    public void run() {
        hubConsumer.subscribe(List.of(topicHub));
        Runtime.getRuntime().addShutdownHook(new Thread(hubConsumer::wakeup));

        try {
            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = hubConsumer.poll(Duration.ofMillis(consumeAttemptTimeout));

                int count = 0;
                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    HubEventAvro hubEvent = (HubEventAvro) record.value();
                    log.info("Получено сообщение от hub: {}", hubEvent);

                    try {
                        handleEvent(hubEvent);
                    } catch (Exception e) {
                        log.warn("При обработке входящего сообщения от hub возникло исключение: {}", e.getMessage());
                    }

                    manageOffsets(record, count);
                    count++;
                }
                hubConsumer.commitAsync();
            }
        } catch (WakeupException e) {
        } catch (Exception e) {
            log.error("Произошла ошибка при обработке сообщений от hub {}", e.getMessage());
        } finally {
            try {
                hubConsumer.commitSync(currentOffset);
            } finally {
                hubConsumer.close();
                log.info("Закрываем consumer");
            }
        }
    }

    private void manageOffsets(ConsumerRecord<String, SpecificRecordBase> record, int count) {
        currentOffset.put(new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1));
        if (count % 10 == 0) {
            hubConsumer.commitAsync(currentOffset, (offset, exception) -> {
                if (exception != null) {
                    log.error("Ошибка фиксации смещений {}", offset, exception);
                }
            });
        }
    }

    private void handleEvent(HubEventAvro hubEvent) {
        Object payload = hubEvent.getPayload();

        switch (payload) {
            case DeviceAddedEventAvro deviceAddedEvent -> {
                hubEventService.addSensor(deviceAddedEvent, hubEvent.getHubId());
            }
            case DeviceRemovedEventAvro deviceRemovedEvent -> {
                hubEventService.removeSensor(deviceRemovedEvent);
            }
            case ScenarioAddedEventAvro scenarioAddedEvent -> {
                hubEventService.addScenario(scenarioAddedEvent, hubEvent.getHubId());
            }
            case ScenarioRemovedEventAvro scenarioRemovedEvent -> {
                hubEventService.removedScenario(scenarioRemovedEvent, hubEvent.getHubId());
            }
            default -> {
                log.info("Такого сценария не существует: {}", payload.getClass().getSimpleName());
            }
        }
    }
}