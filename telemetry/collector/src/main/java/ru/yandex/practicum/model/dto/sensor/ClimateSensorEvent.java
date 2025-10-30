package ru.yandex.practicum.model.dto.sensor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class ClimateSensorEvent extends SensorEvent {
    @NotNull(message = "Уровень температуры по шкале Цельсия не может быть null")
    @Min(value = -273, message = "Температура в градусах Цельсия не может быть меньше минус 273")
    private Integer temperatureC;
    @NotNull(message = "Влажность не может быть null")
    private Integer humidity;
    @NotNull(message = "Уровень CO2 не может быть null")
    private Integer co2Level;

    @Override
    public SensorEventType getType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }
}