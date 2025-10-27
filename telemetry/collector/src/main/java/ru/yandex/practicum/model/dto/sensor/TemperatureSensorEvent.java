package ru.yandex.practicum.model.dto.sensor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class TemperatureSensorEvent extends SensorEvent {
    @NotNull(message = "Температура в градусах Цельсия не может быть null")
    @Min(value = -273, message = "Температура в градусах Цельсия не может быть меньше минус 273")
    private Integer temperatureC;
    @NotNull(message = "Температура в градусах Фаренгейта не может быть null")
    @Min(value = -459, message = "Температура в градусах Фаренгейта не может быть меньше минус 459")
    private Integer temperatureF;

    @Override
    public SensorEventType getType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }
}