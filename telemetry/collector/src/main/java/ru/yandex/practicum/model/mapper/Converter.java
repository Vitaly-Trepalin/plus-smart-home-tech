package ru.yandex.practicum.model.mapper;

import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.model.dto.hub.ActionType;
import ru.yandex.practicum.model.dto.hub.ConditionOperation;
import ru.yandex.practicum.model.dto.hub.ConditionType;
import ru.yandex.practicum.model.dto.hub.DeviceType;

public class Converter {

    public static DeviceTypeAvro mapToAvro(DeviceType deviceType) {
        return switch (deviceType) {
            case DeviceType.CLIMATE_SENSOR -> DeviceTypeAvro.CLIMATE_SENSOR;
            case DeviceType.LIGHT_SENSOR -> DeviceTypeAvro.LIGHT_SENSOR;
            case DeviceType.MOTION_SENSOR -> DeviceTypeAvro.MOTION_SENSOR;
            case DeviceType.SWITCH_SENSOR -> DeviceTypeAvro.SWITCH_SENSOR;
            case DeviceType.TEMPERATURE_SENSOR -> DeviceTypeAvro.TEMPERATURE_SENSOR;
        };
    }

    public static ConditionTypeAvro mapToConditionTypeAvro(ConditionType conditionType) {
        return switch (conditionType) {
            case ConditionType.CO2LEVEL -> ConditionTypeAvro.CO2LEVEL;
            case ConditionType.HUMIDITY -> ConditionTypeAvro.HUMIDITY;
            case ConditionType.LUMINOSITY -> ConditionTypeAvro.LUMINOSITY;
            case ConditionType.MOTION -> ConditionTypeAvro.MOTION;
            case ConditionType.SWITCH -> ConditionTypeAvro.SWITCH;
            case ConditionType.TEMPERATURE -> ConditionTypeAvro.TEMPERATURE;
        };
    }

    public static ConditionOperationAvro mapToConditionOperationAvro(ConditionOperation conditionOperation) {
        return switch (conditionOperation) {
            case ConditionOperation.EQUALS -> ConditionOperationAvro.EQUALS;
            case ConditionOperation.GREATER_THAN -> ConditionOperationAvro.GREATER_THAN;
            case ConditionOperation.LOWER_THAN -> ConditionOperationAvro.LOWER_THAN;
        };
    }

    public static ActionTypeAvro mapToActionTypeAvro(ActionType actionType) {
        return switch (actionType) {
            case ActionType.ACTIVATE -> ActionTypeAvro.ACTIVATE;
            case ActionType.DEACTIVATE -> ActionTypeAvro.DEACTIVATE;
            case ActionType.INVERSE -> ActionTypeAvro.INVERSE;
            case ActionType.SET_VALUE -> ActionTypeAvro.SET_VALUE;
        };
    }
}