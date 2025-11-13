package ru.yandex.practicum.dal.service;

import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.List;

public interface AnalyzerService {
    void addDevice(DeviceAddedEventAvro deviceAdded, String hubId);

    void removeDevice(DeviceRemovedEventAvro deviceRemoved);

    void addScenario(ScenarioAddedEventAvro scenarioAdded, String hub_id);

    void removeScenario(ScenarioRemovedEventAvro scenarioRemoved);

    List<DeviceActionRequest> executingSnapshot(SensorsSnapshotAvro sensorsSnapshotAvro);
}