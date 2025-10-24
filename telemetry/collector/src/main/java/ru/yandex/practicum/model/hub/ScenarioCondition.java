package ru.yandex.practicum.model.hub;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.model.sensor.SensorEventType;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioCondition {
    private String sensorId;
    private SensorEventType type;
    private Operation operation;
    private Integer value;
}