package ru.yandex.practicum.mapper;

import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

import java.util.List;

public class Mapper {

//    public static List<ScenarioConditionAvro> mapToScenarioConditionAvro(List<ScenarioConditionProto> scenarioConditions) {
//        return scenarioConditions.stream()
//                .map(scenarioCondition -> new ScenarioConditionAvro(
//                        scenarioCondition.getSensorId(),
//                        Converter.mapToConditionTypeAvro(scenarioCondition.getType()),
//                        Converter.mapToConditionOperationAvro(scenarioCondition.getOperation()),
//                        getValueScenarioCondition(scenarioCondition)
//                ))
//                .toList();
//    }
//
//    public static List<DeviceActionAvro> mapToDeviceActionAvro(List<DeviceActionProto> deviceActions) {
//        return deviceActions.stream()
//                .map(deviceAction -> new DeviceActionAvro(
//                        deviceAction.getSensorId(),
//                        Converter.mapToActionTypeAvro(deviceAction.getType()),
//                        getValueDeviceAction(deviceAction)
//                ))
//                .toList();
//    }

    public static Integer getValueScenarioCondition(Object value) {
        if (value instanceof Boolean) {
            return (boolean) value ? 1 : 0;
        } else if (value instanceof Integer) {
            return (int) value;
        } else {
            return null;
        }
    }

//    public static Integer getValueDeviceAction(DeviceActionProto deviceActionProto) {
//        if (deviceActionProto.hasValue()) {
//            return deviceActionProto.getValue();
//        } else {
//            return null;
//        }
//    }
}