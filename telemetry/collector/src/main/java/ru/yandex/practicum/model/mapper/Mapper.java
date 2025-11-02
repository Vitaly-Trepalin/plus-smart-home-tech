package ru.yandex.practicum.model.mapper;

import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

import java.util.List;

public class Mapper {

    public static List<ScenarioConditionAvro> mapToScenarioConditionAvro(List<ScenarioConditionProto> scenarioConditions) {
        return scenarioConditions.stream()
                .map(scenarioCondition -> new ScenarioConditionAvro(
                        scenarioCondition.getSensorId(),
                        Converter.mapToConditionTypeAvro(scenarioCondition.getType()),
                        Converter.mapToConditionOperationAvro(scenarioCondition.getOperation()),
                        scenarioCondition.getIntValue()
                ))
                .toList();
    }

    public static List<DeviceActionAvro> mapToDeviceActionAvro(List<DeviceActionProto> deviceActions) {
        return deviceActions.stream()
                .map(deviceAction -> new DeviceActionAvro(
                        deviceAction.getSensorId(),
                        Converter.mapToActionTypeAvro(deviceAction.getType()),
                        deviceAction.getValue()
                ))
                .toList();
    }
}