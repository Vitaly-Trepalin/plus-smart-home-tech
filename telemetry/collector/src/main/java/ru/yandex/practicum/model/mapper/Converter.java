package ru.yandex.practicum.model.mapper;

import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionOperationProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

public class Converter {
    public static DeviceTypeAvro mapToDeviceTypeAvro(DeviceTypeProto deviceType) {
        return switch (deviceType) {
            case DeviceTypeProto.CLIMATE_SENSOR -> DeviceTypeAvro.CLIMATE_SENSOR;
            case DeviceTypeProto.LIGHT_SENSOR -> DeviceTypeAvro.LIGHT_SENSOR;
            case DeviceTypeProto.MOTION_SENSOR -> DeviceTypeAvro.MOTION_SENSOR;
            case DeviceTypeProto.SWITCH_SENSOR -> DeviceTypeAvro.SWITCH_SENSOR;
            case DeviceTypeProto.TEMPERATURE_SENSOR -> DeviceTypeAvro.TEMPERATURE_SENSOR;
            case DeviceTypeProto.UNRECOGNIZED -> throw new IllegalArgumentException("Нет такого типа устройств");
        };
    }

    public static ConditionTypeAvro mapToConditionTypeAvro(ConditionTypeProto conditionType) {
        return switch (conditionType) {
            case ConditionTypeProto.CO2LEVEL -> ConditionTypeAvro.CO2LEVEL;
            case ConditionTypeProto.HUMIDITY -> ConditionTypeAvro.HUMIDITY;
            case ConditionTypeProto.LUMINOSITY -> ConditionTypeAvro.LUMINOSITY;
            case ConditionTypeProto.MOTION -> ConditionTypeAvro.MOTION;
            case ConditionTypeProto.SWITCH -> ConditionTypeAvro.SWITCH;
            case ConditionTypeProto.TEMPERATURE -> ConditionTypeAvro.TEMPERATURE;
            case ConditionTypeProto.UNRECOGNIZED -> throw new IllegalArgumentException("Нет такого типа условия");
        };
    }

    public static ConditionOperationAvro mapToConditionOperationAvro(ConditionOperationProto conditionOperation) {
        return switch (conditionOperation) {
            case ConditionOperationProto.EQUALS -> ConditionOperationAvro.EQUALS;
            case ConditionOperationProto.GREATER_THAN -> ConditionOperationAvro.GREATER_THAN;
            case ConditionOperationProto.LOWER_THAN -> ConditionOperationAvro.LOWER_THAN;
            case ConditionOperationProto.UNRECOGNIZED ->
                    throw new IllegalArgumentException("Нет такого типа оператора");
        };
    }

    public static ActionTypeAvro mapToActionTypeAvro(ActionTypeProto actionType) {
        return switch (actionType) {
            case ActionTypeProto.ACTIVATE -> ActionTypeAvro.ACTIVATE;
            case ActionTypeProto.DEACTIVATE -> ActionTypeAvro.DEACTIVATE;
            case ActionTypeProto.INVERSE -> ActionTypeAvro.INVERSE;
            case ActionTypeProto.SET_VALUE -> ActionTypeAvro.SET_VALUE;
            case ActionTypeProto.UNRECOGNIZED -> throw new IllegalArgumentException("Нет такого типа действия");
        };
    }
}