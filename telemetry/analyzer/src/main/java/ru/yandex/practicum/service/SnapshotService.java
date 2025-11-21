package ru.yandex.practicum.service;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dal.entity.Action;
import ru.yandex.practicum.dal.entity.Condition;
import ru.yandex.practicum.dal.entity.ConditionOperation;
import ru.yandex.practicum.dal.entity.ConditionType;
import ru.yandex.practicum.dal.entity.Scenario;
import ru.yandex.practicum.dal.mapper.Converter;
import ru.yandex.practicum.dal.repository.ScenarioRepository;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class SnapshotService {
    private final ScenarioRepository scenarioRepository;
    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public SnapshotService(ScenarioRepository scenarioRepository,
                           @GrpcClient("hub-router") HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient) {
        this.scenarioRepository = scenarioRepository;
        this.hubRouterClient = hubRouterClient;
    }

    public void executingSnapshot(SensorsSnapshotAvro sensorsSnapshotAvro) {
        List<Scenario> scenarios = scenarioRepository.findAllByHubId(sensorsSnapshotAvro.getHubId()); // получаем все сценарии
        Map<String, SensorStateAvro> sensorStateAvros = sensorsSnapshotAvro.getSensorsState(); // получаем набор состояний из снапщота

        for (Scenario scenario : scenarios) {
            Map<String, Condition> conditionsScenario = new HashMap<>(scenario.getConditions()); // получаем все условия сценариев
            Set<String> sensorIdsFromScenario = conditionsScenario.keySet();

            boolean isCompleted = sensorIdsFromScenario
                    .stream()
                    .allMatch(sensorId -> isScenarioCompliant(conditionsScenario.get(sensorId),
                            sensorStateAvros.get(sensorId)));

            if (isCompleted) {
                Map<String, Action> actions = scenario.getActions();
                actions.forEach((sensorId, action) -> sendingCommand(scenario, sensorId, action));
            }
        }
    }

    public void sendingCommand(Scenario scenario, String sensorId, Action action) {
        DeviceActionProto deviceAction;
        if (action.getValue() == null) {
            deviceAction = DeviceActionProto.newBuilder()
                    .setSensorId(sensorId)
                    .setType(Converter.mapToActionTypeProto(action.getType()))
                    .build();
        } else {
            deviceAction = DeviceActionProto.newBuilder()
                    .setSensorId(sensorId)
                    .setType(Converter.mapToActionTypeProto(action.getType()))
                    .setValue(action.getValue())
                    .build();
        }

        Instant timestamp = Instant.now();

        DeviceActionRequest deviceActionRequest = DeviceActionRequest.newBuilder()
                .setHubId(scenario.getHubId())
                .setScenarioName(scenario.getName())
                .setAction(deviceAction)
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(timestamp.getEpochSecond())
                        .setNanos(timestamp.getNano()))
                .build();

        hubRouterClient.handleDeviceAction(deviceActionRequest);
        log.info("На датчик отправлена команда {}", deviceActionRequest);
    }

    private boolean isScenarioCompliant(Condition condition, SensorStateAvro sensorStateAvro) {
        if (sensorStateAvro == null) {
            return false;
        } else {
            boolean result = true;
            switch (condition.getType()) {
                case ConditionType.MOTION -> {
                    MotionSensorAvro motionSensor = (MotionSensorAvro) sensorStateAvro.getData();
                    if (condition.getValue() == 1 && motionSensor.getMotion()) {
                        log.info("Условие датчика движения выполнено");
                    } else if (condition.getValue() == 0 && motionSensor.getMotion()) {
                        log.info("Условие датчика движения выполнено");
                    } else {
                        log.info("Условие датчика движения не выполнено");
                        return false;
                    }
                }
                case ConditionType.LUMINOSITY -> {
                    LightSensorAvro lightSensorAvro = (LightSensorAvro) sensorStateAvro.getData();
                    if (condition.getOperation() == ConditionOperation.EQUALS &&
                            condition.getValue() == lightSensorAvro.getLuminosity()) {
                        log.info("Условие датчика света выполнено");
                    } else if (condition.getOperation() == ConditionOperation.GREATER_THAN &&
                            condition.getValue() < lightSensorAvro.getLuminosity()) {
                        log.info("Условие датчика света выполнено");
                    } else if (condition.getOperation() == ConditionOperation.LOWER_THAN &&
                            condition.getValue() > lightSensorAvro.getLuminosity()) {
                        log.info("Условие датчика света выполнено");
                    } else {
                        log.info("Условие датчика света не выполнено");
                        return false;
                    }
                }
                case ConditionType.SWITCH -> {
                    SwitchSensorAvro switchSensorAvro = (SwitchSensorAvro) sensorStateAvro.getData();
                    if (condition.getValue() == 1 && switchSensorAvro.getState()) {
                        log.info("Условие переключателя выполнено");
                    } else if (condition.getValue() == 0 && switchSensorAvro.getState()) {
                        log.info("Условие переключателя выполнено");
                    } else {
                        return false;
                    }
                }
                case ConditionType.TEMPERATURE -> {
                    if (sensorStateAvro.getData() instanceof TemperatureSensorAvro) {
                        TemperatureSensorAvro temperatureSensorAvro = (TemperatureSensorAvro) sensorStateAvro.getData();
                        if (condition.getOperation() == ConditionOperation.EQUALS &&
                                condition.getValue() == temperatureSensorAvro.getTemperatureC()) {
                            log.info("Условие датчика температуры выполнено");
                        } else if (condition.getOperation() == ConditionOperation.GREATER_THAN &&
                                condition.getValue() < temperatureSensorAvro.getTemperatureC()) {
                            log.info("Условие датчика температуры выполнено");
                        } else if (condition.getOperation() == ConditionOperation.LOWER_THAN &&
                                condition.getValue() > temperatureSensorAvro.getTemperatureC()) {
                            log.info("Условие датчика температуры выполнено");
                        } else {
                            log.info("Условие датчика температуры не выполнено");
                            return false;
                        }
                    } else {
                        ClimateSensorAvro climateSensorAvro = (ClimateSensorAvro) sensorStateAvro.getData();
                        if (condition.getOperation() == ConditionOperation.EQUALS &&
                                condition.getValue() == climateSensorAvro.getTemperatureC()) {
                            log.info("Условие климатического датчика выполнено");
                        } else if (condition.getOperation() == ConditionOperation.GREATER_THAN &&
                                condition.getValue() < climateSensorAvro.getTemperatureC()) {
                            log.info("Условие климатического датчика выполнено");
                        } else if (condition.getOperation() == ConditionOperation.LOWER_THAN &&
                                condition.getValue() > climateSensorAvro.getTemperatureC()) {
                            log.info("Условие климатического датчика выполнено");
                        } else {
                            log.info("Условие климатического датчика не выполнено");
                            return false;
                        }
                    }
                }
                case ConditionType.CO2LEVEL -> {
                    ClimateSensorAvro climateSensorAvro = (ClimateSensorAvro) sensorStateAvro.getData();
                    if (condition.getOperation() == ConditionOperation.EQUALS &&
                            condition.getValue() == climateSensorAvro.getCo2Level()) {
                        log.info("Условие климатического датчика выполнено");
                    } else if (condition.getOperation() == ConditionOperation.GREATER_THAN &&
                            condition.getValue() < climateSensorAvro.getCo2Level()) {
                        log.info("Условие климатического датчика выполнено");
                    } else if (condition.getOperation() == ConditionOperation.LOWER_THAN &&
                            condition.getValue() > climateSensorAvro.getCo2Level()) {
                        log.info("Условие климатического датчика выполнено");
                    } else {
                        log.info("Условие климатического датчика не выполнено");
                        return false;
                    }
                }
                case ConditionType.HUMIDITY -> {
                    ClimateSensorAvro climateSensorAvro = (ClimateSensorAvro) sensorStateAvro.getData();
                    if (condition.getOperation() == ConditionOperation.EQUALS &&
                            condition.getValue() == climateSensorAvro.getHumidity()) {
                        log.info("Условие климатического датчика выполнено");
                    } else if (condition.getOperation() == ConditionOperation.GREATER_THAN &&
                            condition.getValue() < climateSensorAvro.getHumidity()) {
                        log.info("Условие климатического датчика выполнено");
                    } else if (condition.getOperation() == ConditionOperation.LOWER_THAN &&
                            condition.getValue() > climateSensorAvro.getHumidity()) {
                        log.info("Условие климатического датчика выполнено");
                    } else {
                        log.info("Условие климатического датчика не выполнено");
                        return false;
                    }
                }
                default -> {
                    throw new IllegalArgumentException("Нет такого типа условия " + condition.getType());
                }
            }
            return result;
        }
    }
}