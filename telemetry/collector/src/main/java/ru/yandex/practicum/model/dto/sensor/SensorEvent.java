package ru.yandex.practicum.model.dto.sensor;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ClimateSensorEvent.class, name = "CLIMATE_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = LightSensorEvent.class, name = "LIGHT_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = MotionSensorEvent.class, name = "MOTION_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = SwitchSensorEvent.class, name = "SWITCH_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = TemperatureSensorEvent.class, name = "TEMPERATURE_SENSOR_EVENT")
})
@Getter
@Setter
@ToString
public abstract class SensorEvent {
    @NotBlank(message = "Идентификатор события датчика не может быть null или пустым")
    private String id;
    @NotBlank(message = "Идентификатор хаба, связанного с событием не может быть null или пустым")
    private String hubId;
    private Instant timestamp = Instant.now();

    public abstract SensorEventType getType();
}