package ru.yandex.practicum.mapper;

public class Mapper {

    public static Integer getValueScenarioCondition(Object value) {
        if (value instanceof Boolean) {
            return (boolean) value ? 1 : 0;
        } else if (value instanceof Integer) {
            return (int) value;
        } else {
            return null;
        }
    }
}