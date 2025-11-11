package ru.yandex.practicum.mapper;

import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionOperationProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.model.ActionType;
import ru.yandex.practicum.model.ConditionOperation;
import ru.yandex.practicum.model.ConditionType;

public class Converter {
//    public static DeviceTypeAvro mapToDeviceTypeAvro(DeviceTypeProto deviceType) {
//        return switch (deviceType) {
//            case DeviceTypeProto.CLIMATE_SENSOR -> DeviceTypeAvro.CLIMATE_SENSOR;
//            case DeviceTypeProto.LIGHT_SENSOR -> DeviceTypeAvro.LIGHT_SENSOR;
//            case DeviceTypeProto.MOTION_SENSOR -> DeviceTypeAvro.MOTION_SENSOR;
//            case DeviceTypeProto.SWITCH_SENSOR -> DeviceTypeAvro.SWITCH_SENSOR;
//            case DeviceTypeProto.TEMPERATURE_SENSOR -> DeviceTypeAvro.TEMPERATURE_SENSOR;
//            case DeviceTypeProto.UNRECOGNIZED -> throw new IllegalArgumentException("Нет такого типа устройств");
//        };
//    }

    public static ConditionType mapToConditionType(ConditionTypeAvro conditionType) {
        return switch (conditionType) {
            case ConditionTypeAvro.CO2LEVEL -> ConditionType.CO2LEVEL;
            case ConditionTypeAvro.HUMIDITY -> ConditionType.HUMIDITY;
            case ConditionTypeAvro.LUMINOSITY -> ConditionType.LUMINOSITY;
            case ConditionTypeAvro.MOTION -> ConditionType.MOTION;
            case ConditionTypeAvro.SWITCH -> ConditionType.SWITCH;
            case ConditionTypeAvro.TEMPERATURE -> ConditionType.TEMPERATURE;
        };
    }

    public static ConditionOperation mapToConditionOperation(ConditionOperationAvro conditionOperation) {
        return switch (conditionOperation) {
            case ConditionOperationAvro.EQUALS -> ConditionOperation.EQUALS;
            case ConditionOperationAvro.GREATER_THAN -> ConditionOperation.GREATER_THAN;
            case ConditionOperationAvro.LOWER_THAN -> ConditionOperation.LOWER_THAN;
        };
    }

    public static ActionType mapToActionType(ActionTypeAvro actionType) {
        return switch (actionType) {
            case ActionTypeAvro.ACTIVATE -> ActionType.ACTIVATE;
            case ActionTypeAvro.DEACTIVATE -> ActionType.DEACTIVATE;
            case ActionTypeAvro.INVERSE -> ActionType.INVERSE;
            case ActionTypeAvro.SET_VALUE -> ActionType.SET_VALUE;
        };
    }

    public static ActionTypeProto mapToActionTypeProto(ActionType actionType) {
        return switch (actionType) {
            case ActionType.ACTIVATE -> ActionTypeProto.ACTIVATE;
            case ActionType.DEACTIVATE -> ActionTypeProto.DEACTIVATE;
            case ActionType.INVERSE -> ActionTypeProto.INVERSE;
            case ActionType.SET_VALUE -> ActionTypeProto.SET_VALUE;
        };
    }
}