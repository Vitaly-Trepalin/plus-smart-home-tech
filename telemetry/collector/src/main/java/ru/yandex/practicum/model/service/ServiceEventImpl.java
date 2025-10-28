package ru.yandex.practicum.model.service;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.model.dto.hub.HubEventType;
import ru.yandex.practicum.model.dto.sensor.SensorEventType;
import ru.yandex.practicum.model.mapper.Converter;
import ru.yandex.practicum.model.mapper.Mapper;
import ru.yandex.practicum.model.dto.hub.DeviceAddedEvent;
import ru.yandex.practicum.model.dto.hub.DeviceRemovedEvent;
import ru.yandex.practicum.model.dto.hub.ScenarioAddedEvent;
import ru.yandex.practicum.model.dto.hub.ScenarioRemovedEvent;
import ru.yandex.practicum.model.dto.sensor.ClimateSensorEvent;
import ru.yandex.practicum.model.dto.sensor.MotionSensorEvent;
import ru.yandex.practicum.model.dto.sensor.SwitchSensorEvent;
import ru.yandex.practicum.model.dto.sensor.TemperatureSensorEvent;
import ru.yandex.practicum.model.producer.CollectorTopics;
import ru.yandex.practicum.model.dto.hub.HubEvent;
import ru.yandex.practicum.model.dto.sensor.LightSensorEvent;
import ru.yandex.practicum.model.dto.sensor.SensorEvent;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.producer.EventProducer;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ServiceEventImpl implements ServiceEvent {
    private final EventProducer producer;

    @Override
    public void collectSensorEvent(SensorEvent sensorEvent) {
        SensorEventType sensorEventType = sensorEvent.getType();
        ProducerRecord<String, SpecificRecordBase> record;

        record = switch (sensorEventType) {
            case SensorEventType.LIGHT_SENSOR_EVENT -> {
                LightSensorEvent lightSensorEvent = (LightSensorEvent) sensorEvent;
                yield new ProducerRecord<>(CollectorTopics.TELEMETRY_SENSORS_V1, null,
                        Instant.now().toEpochMilli(), sensorEvent.getHubId(),
                        new SensorEventAvro(sensorEvent.getId(), sensorEvent.getHubId(), sensorEvent.getTimestamp(),
                                new LightSensorAvro(lightSensorEvent.getLinkQuality(),
                                        lightSensorEvent.getLuminosity())));
            }
            case SensorEventType.CLIMATE_SENSOR_EVENT -> {
                ClimateSensorEvent climateSensorEvent = (ClimateSensorEvent) sensorEvent;
                yield new ProducerRecord<>(CollectorTopics.TELEMETRY_SENSORS_V1, null,
                        Instant.now().toEpochMilli(), sensorEvent.getHubId(),
                        new SensorEventAvro(sensorEvent.getId(), sensorEvent.getHubId(), sensorEvent.getTimestamp(),
                                new ClimateSensorAvro(climateSensorEvent.getTemperatureC(),
                                        climateSensorEvent.getHumidity(), climateSensorEvent.getCo2Level())));
            }
            case SensorEventType.MOTION_SENSOR_EVENT -> {
                MotionSensorEvent motionSensorEvent = (MotionSensorEvent) sensorEvent;
                yield new ProducerRecord<>(CollectorTopics.TELEMETRY_SENSORS_V1, null,
                        Instant.now().toEpochMilli(), sensorEvent.getHubId(),
                        new SensorEventAvro(sensorEvent.getId(), sensorEvent.getHubId(), sensorEvent.getTimestamp(),
                                new MotionSensorAvro(motionSensorEvent.getLinkQuality(),
                                        motionSensorEvent.getMotion(), motionSensorEvent.getVoltage())));
            }
            case SensorEventType.SWITCH_SENSOR_EVENT -> {
                SwitchSensorEvent switchSensorEvent = (SwitchSensorEvent) sensorEvent;
                yield new ProducerRecord<>(CollectorTopics.TELEMETRY_SENSORS_V1, null,
                        Instant.now().toEpochMilli(), sensorEvent.getHubId(),
                        new SensorEventAvro(sensorEvent.getId(), sensorEvent.getHubId(), sensorEvent.getTimestamp(),
                                new SwitchSensorAvro(switchSensorEvent.getState())));
            }
            case SensorEventType.TEMPERATURE_SENSOR_EVENT -> {
                TemperatureSensorEvent temperatureSensorEvent = (TemperatureSensorEvent) sensorEvent;
                yield new ProducerRecord<>(CollectorTopics.TELEMETRY_SENSORS_V1, null,
                        Instant.now().toEpochMilli(), sensorEvent.getHubId(),
                        new SensorEventAvro(sensorEvent.getId(), sensorEvent.getHubId(), sensorEvent.getTimestamp(),
                                new TemperatureSensorAvro(temperatureSensorEvent.getTemperatureC(),
                                        temperatureSensorEvent.getTemperatureF())));
            }
        };

        producer.getProducer().send(record);
    }

    @Override
    public void collectorsHubEvent(HubEvent hubEvent) {
        HubEventType eventType = hubEvent.getType();
        ProducerRecord<String, SpecificRecordBase> record;

        record = switch (eventType) {
            case HubEventType.DEVICE_ADDED -> {
                DeviceAddedEvent deviceAddedEvent = (DeviceAddedEvent) hubEvent;
                DeviceTypeAvro deviceTypeAvro = Converter.mapToAvro(deviceAddedEvent.getDeviceType());
                yield new ProducerRecord<>(CollectorTopics.TELEMETRY_HUBS_V1, null,
                        Instant.now().toEpochMilli(), deviceAddedEvent.getHubId(),
                        new HubEventAvro(deviceAddedEvent.getHubId(),
                        deviceAddedEvent.getTimestamp(), new DeviceAddedEventAvro(deviceAddedEvent.getId(),
                        deviceTypeAvro)));
            }
            case HubEventType.DEVICE_REMOVED -> {
                DeviceRemovedEvent deviceRemovedEvent = (DeviceRemovedEvent) hubEvent;
                yield new ProducerRecord<>(CollectorTopics.TELEMETRY_HUBS_V1, null,
                        Instant.now().toEpochMilli(), deviceRemovedEvent.getHubId(),
                        new HubEventAvro(deviceRemovedEvent.getHubId(), deviceRemovedEvent.getTimestamp(),
                                new DeviceRemovedEventAvro(deviceRemovedEvent.getId())));
            }
            case HubEventType.SCENARIO_ADDED -> {
                ScenarioAddedEvent scenarioAddedEvent = (ScenarioAddedEvent) hubEvent;

                yield new ProducerRecord<>(CollectorTopics.TELEMETRY_HUBS_V1, null,
                        Instant.now().toEpochMilli(), scenarioAddedEvent.getHubId(),
                        new HubEventAvro(
                                scenarioAddedEvent.getHubId(),
                                scenarioAddedEvent.getTimestamp(),
                                new ScenarioAddedEventAvro(
                                        scenarioAddedEvent.getName(),
                                        Mapper.mapToScenarioConditionAvro(scenarioAddedEvent.getConditions()),
                                        Mapper.mapToDeviceActionAvro(scenarioAddedEvent.getActions())
                                )
                        )
                );
            }
            case HubEventType.SCENARIO_REMOVED -> {
                ScenarioRemovedEvent scenarioRemovedEvent = (ScenarioRemovedEvent) hubEvent;

                yield new ProducerRecord<>(CollectorTopics.TELEMETRY_HUBS_V1, null,
                        Instant.now().toEpochMilli(), scenarioRemovedEvent.getHubId(),
                        new HubEventAvro(
                                scenarioRemovedEvent.getHubId(),
                                scenarioRemovedEvent.getTimestamp(),
                                new ScenarioRemovedEventAvro(
                                        scenarioRemovedEvent.getName()
                                )
                        )
                );
            }
        };

        producer.getProducer().send(record);
    }
}