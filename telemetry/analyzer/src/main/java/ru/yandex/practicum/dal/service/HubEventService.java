package ru.yandex.practicum.dal.service;

import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

public interface HubEventService {
    void addSensor(DeviceAddedEventAvro deviceAddedEvent, String hubId);

    void removeSensor(DeviceRemovedEventAvro deviceRemovedEvent);

    void addScenario(ScenarioAddedEventAvro scenarioAddedEvent, String hubId);

    void removedScenario(ScenarioRemovedEventAvro scenarioRemovedEvent, String hubId);
}