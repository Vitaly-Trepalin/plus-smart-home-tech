package ru.yandex.practicum.dal.mapper;

import ru.yandex.practicum.dal.entity.ActionType;
import ru.yandex.practicum.dal.entity.ConditionOperation;
import ru.yandex.practicum.dal.entity.ConditionType;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;

public class Converter {
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

    public static Integer mapToIntegerValue(Object value) {
        if (value instanceof Boolean) {
            return (boolean) value ? 1 : 0;
        } else if (value instanceof Integer) {
            return (int) value;
        } else {
            return null;
        }
    }
}