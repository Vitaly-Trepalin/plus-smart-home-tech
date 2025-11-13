package ru.yandex.practicum.dal.service;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.service.HubRouterClient;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.mapper.Converter;
import ru.yandex.practicum.mapper.Mapper;
import ru.yandex.practicum.dal.entity.Action;
import ru.yandex.practicum.dal.entity.ActionType;
import ru.yandex.practicum.dal.entity.Condition;
import ru.yandex.practicum.dal.entity.ConditionOperation;
import ru.yandex.practicum.dal.entity.ConditionType;
import ru.yandex.practicum.dal.entity.Scenario;
import ru.yandex.practicum.dal.entity.ScenarioAction;
import ru.yandex.practicum.dal.entity.ScenarioActionId;
import ru.yandex.practicum.dal.entity.ScenarioCondition;
import ru.yandex.practicum.dal.entity.ScenarioConditionId;
import ru.yandex.practicum.dal.entity.Sensor;
import ru.yandex.practicum.dal.repository.ActionRepository;
import ru.yandex.practicum.dal.repository.ConditionRepository;
import ru.yandex.practicum.dal.repository.ScenarioActionsRepository;
import ru.yandex.practicum.dal.repository.ScenarioConditionRepository;
import ru.yandex.practicum.dal.repository.ScenarioRepository;
import ru.yandex.practicum.dal.repository.SensorRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyzerServiceImpl implements AnalyzerService {
    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;
    private final ScenarioActionsRepository scenarioActionsRepository;
    private final HubRouterClient hubRouterClient;

    @Override
    @Transactional
    public void addDevice(DeviceAddedEventAvro deviceAdded, String hubId) {
        sensorRepository.save(new Sensor(deviceAdded.getId(), hubId));
    }

    @Override
    @Transactional
    public void removeDevice(DeviceRemovedEventAvro deviceRemoved) {
        sensorRepository.deleteById(deviceRemoved.getId());
    }


    @Override
    @Transactional
    public void addScenario(ScenarioAddedEventAvro scenarioAdded, String hubId) {
        Scenario scenario = Scenario.builder() // Сохраняю сценарий
                .hubId(hubId)
                .name(scenarioAdded.getName())
                .build();
        scenarioRepository.save(scenario);

        List<ScenarioConditionAvro> conditions = scenarioAdded.getConditions(); // Собираю условия
        if (!conditions.isEmpty()) {
            List<Condition> newConditions = new ArrayList<>();
            List<ScenarioCondition> scenarioConditions = new ArrayList<>();

            Set<String> conditionSensorIds = conditions.stream()
                    .map(ScenarioConditionAvro::getSensorId)
                    .collect(Collectors.toSet());

            Map<String, Sensor> conditionSensors = sensorRepository.findAllByIdIn(conditionSensorIds) // Получаю все датчики
                    .stream()
                    .collect(Collectors.toMap(Sensor::getId, Function.identity()));

            for (ScenarioConditionAvro condition : conditions) {
                ConditionType type = Converter.mapToConditionType(condition.getType());
                ConditionOperation operation = Converter.mapToConditionOperation(condition.getOperation());
                Integer value = Mapper.getValueScenarioCondition(condition.getValue());

                Condition newCondition = new Condition(0, type, operation, value);
                newConditions.add(newCondition);
            }

            List<Condition> savedConditions = conditionRepository.saveAll(newConditions); // Сохраняю все условия в БД

            for (int i = 0; i < conditions.size(); i++) { // Создаю связи между условиями, сенсорами и сценариями
                ScenarioConditionAvro conditionAvro = conditions.get(i);
                Condition savedCondition = savedConditions.get(i);
                Sensor sensor = conditionSensors.get(conditionAvro.getSensorId());

                if (sensor == null) {
                    log.warn("Сенсор с ID {} не найден для условия", conditionAvro.getSensorId());
                    continue;
                }

                ScenarioConditionId scenarioConditionId = new ScenarioConditionId(
                        scenario.getId(),
                        sensor.getId(),
                        savedCondition.getId());

                ScenarioCondition scenarioCondition = new ScenarioCondition(
                        scenarioConditionId,
                        scenario,
                        sensor,
                        savedCondition);
                scenarioConditions.add(scenarioCondition);
            }
            scenarioConditionRepository.saveAll(scenarioConditions); // Сохраняю связи между условиями, сенсорами и сценариями
        }

        List<DeviceActionAvro> actions = scenarioAdded.getActions(); // Собираю действия
        if (!actions.isEmpty()) {
            List<Action> newActions = new ArrayList<>();
            List<ScenarioAction> scenarioActions = new ArrayList<>();

            Set<String> actionSensorIds = actions.stream()
                    .map(DeviceActionAvro::getSensorId)
                    .collect(Collectors.toSet());


            Map<String, Sensor> actionSensors = sensorRepository.findAllByIdIn(actionSensorIds) // Получаю все датчики
                    .stream()
                    .collect(Collectors.toMap(Sensor::getId, Function.identity()));

            for (DeviceActionAvro action : actions) {
                ActionType type = Converter.mapToActionType(action.getType());
                Integer value = action.getValue();

                Action newAction = Action.builder()
                        .type(type)
                        .value(value)
                        .build();
                newActions.add(newAction);
            }
            List<Action> savedActions = actionRepository.saveAll(newActions); // сохраняю все действия в БД

            for (int i = 0; i < actions.size(); i++) { // Создаю связи между действиями, сенсорами и сценариями
                DeviceActionAvro actionAvro = actions.get(i);
                Action savedAction = savedActions.get(i);
                Sensor sensor = actionSensors.get(actionAvro.getSensorId());

                if (sensor == null) {
                    log.warn("Сенсор с ID {} не найден для действия", actionAvro.getSensorId());
                    continue;
                }

                ScenarioActionId scenarioActionId = new ScenarioActionId(
                        scenario.getId(),
                        sensor.getId(),
                        savedAction.getId()
                );

                ScenarioAction scenarioAction = new ScenarioAction(
                        scenarioActionId,
                        scenario,
                        sensor,
                        savedAction
                );
                scenarioActions.add(scenarioAction);
            }
            scenarioActionsRepository.saveAll(scenarioActions); // Сохраняю все связи между действиями, сенсорами и сценариями
        }
    }

    @Override
    @Transactional
    public void removeScenario(ScenarioRemovedEventAvro scenarioRemoved) {
        scenarioRepository.deleteByName(scenarioRemoved.getName());
    }

    @Override
    @Transactional
    public List<DeviceActionRequest> executingSnapshot(SensorsSnapshotAvro sensorsSnapshotAvro) {
        List<DeviceActionRequest> requests = new ArrayList<>();
        Map<String, SensorStateAvro> sensorsState = sensorsSnapshotAvro.getSensorsState(); // получил все данные датчиков из снапшота

        String hubId = sensorsSnapshotAvro.getHubId();
        List<Scenario> scenarios = scenarioRepository.findByHubId(hubId); // получил весь список сценариев для этого хаба

        for (Scenario scenario : scenarios) {

            List<ScenarioCondition> scenarioConditions = scenario.getScenarioConditions();
            boolean isCompleted = checkingScenarioConditions(scenarioConditions, sensorsState); // выполнены ли все условия сценария

            if (isCompleted) {
                List<ScenarioAction> scenariosActions = scenario.getScenarioActions();

                for (ScenarioAction scenarioAction : scenariosActions) {
                    Sensor sensor = scenarioAction.getSensor();
                    String sensorId = sensor.getId();

                    Action action = scenarioAction.getAction();
                    ActionType actionType = action.getType();
                    Integer value = action.getValue();

                    DeviceActionProto deviceActionProto;
                    if (value == null) {
                        deviceActionProto = DeviceActionProto.newBuilder()
                                .setSensorId(sensorId)
                                .setType(Converter.mapToActionTypeProto(actionType))
                                .build();
                    } else {
                        deviceActionProto = DeviceActionProto.newBuilder()
                                .setSensorId(sensorId)
                                .setType(Converter.mapToActionTypeProto(actionType))
                                .setValue(value)
                                .build();
                    }

                    DeviceActionRequest deviceActionRequest = DeviceActionRequest.newBuilder()
                            .setHubId(hubId)
                            .setScenarioName(scenario.getName())
                            .setAction(deviceActionProto)
                            .setTimestamp(Timestamp.newBuilder()
                                    .setSeconds(Instant.now().getEpochSecond())
                                    .setNanos(Instant.now().getNano())
                                    .build())
                            .build();

                    requests.add(deviceActionRequest);
                }
            }
        }
        return requests;
    }

    private boolean checkingScenarioConditions(List<ScenarioCondition> scenarioConditions,
                                               Map<String, SensorStateAvro> sensorsState) {
        for (ScenarioCondition scenarioCondition : scenarioConditions) {
            String id = scenarioCondition.getSensor().getId(); // id датчика в сценарии и поиск такого в снапшоте

            if (sensorsState.containsKey(id)) {

                Condition condition = scenarioCondition.getCondition();
                ConditionType type = condition.getType();
                ConditionOperation operation = condition.getOperation();
                Integer value = condition.getValue();

                SensorStateAvro sensorStateAvro = sensorsState.get(id);
                Object data = sensorStateAvro.getData();
                if (type.equals(ConditionType.LUMINOSITY)) {
                    LightSensorAvro lightSensorAvro = (LightSensorAvro) data;
                    if (operation.equals(ConditionOperation.EQUALS) && lightSensorAvro.getLuminosity() == value) {
                        log.info("Условие lightSensorAvro.getLuminosity() == value выполнено");
                    } else if (operation.equals(ConditionOperation.GREATER_THAN) &&
                            lightSensorAvro.getLuminosity() > value) {
                        log.info("Условие lightSensorAvro.getLuminosity() > value выполнено");
                    } else if (operation.equals(ConditionOperation.LOWER_THAN) &&
                            lightSensorAvro.getLuminosity() < value) {
                        log.info("Условие lightSensorAvro.getLuminosity() < value выполнено");
                    } else {
                        log.info("Условие не выполнено");
                        return false;
                    }
                } else if (type.equals(ConditionType.MOTION)) {
                    MotionSensorAvro motionSensorAvro = (MotionSensorAvro) data;

                    int motion = 0;
                    if (motionSensorAvro.getMotion()) {
                        motion = 1;
                    }

                    if (operation.equals(ConditionOperation.EQUALS) && motion == value) {
                        log.info("Условие motion == value выполнено");
                    } else {
                        log.info("Условие не выполнено ");
                        return false;
                    }
                } else if (type.equals(ConditionType.SWITCH)) {
                    SwitchSensorAvro switchSensorAvro = (SwitchSensorAvro) data;

                    int switchSensor = 0;
                    if (switchSensorAvro.getState()) {
                        switchSensor = 1;
                    }

                    if (operation.equals(ConditionOperation.EQUALS) && switchSensor == value) {
                        log.info("Условие switchSensor == value выполнено");
                    } else {
                        log.info("Условие не выполнено");
                        return false;
                    }
                } else if (type.equals(ConditionType.CO2LEVEL)) {
                    ClimateSensorAvro climateSensorAvro = (ClimateSensorAvro) data;
                    if (operation.equals(ConditionOperation.EQUALS) && climateSensorAvro.getCo2Level() == value) {
                        log.info("Условие climateSensorAvro.getCo2Level() == value выполнено");
                    } else if (operation.equals(ConditionOperation.GREATER_THAN) &&
                            climateSensorAvro.getCo2Level() > value) {
                        log.info("Условие climateSensorAvro.getCo2Level() > value выполнено");
                    } else if (operation.equals(ConditionOperation.LOWER_THAN) &&
                            climateSensorAvro.getCo2Level() < value) {
                        log.info("Условие climateSensorAvro.getCo2Level() < value выполнено");
                    } else {
                        log.info("Условие не выполнено");
                        return false;
                    }

                } else if (type.equals(ConditionType.HUMIDITY)) {
                    ClimateSensorAvro climateSensorAvro = (ClimateSensorAvro) data;
                    if (operation.equals(ConditionOperation.EQUALS) && climateSensorAvro.getHumidity() == value) {
                        log.info("Условие climateSensorAvro.getHumidity() == value выполнено");
                    } else if (operation.equals(ConditionOperation.GREATER_THAN) &&
                            climateSensorAvro.getHumidity() > value) {
                        log.info("Условие climateSensorAvro.getHumidity() > value выполнено");
                    } else if (operation.equals(ConditionOperation.LOWER_THAN) &&
                            climateSensorAvro.getHumidity() < value) {
                        log.info("Условие climateSensorAvro.getHumidity() < value выполнено");
                    } else {
                        log.info("Условие не выполнено ");
                        return false;
                    }
                } else if (type.equals(ConditionType.TEMPERATURE)) {
                    ClimateSensorAvro climateSensorAvro = (ClimateSensorAvro) data;
                    if (operation.equals(ConditionOperation.EQUALS) && climateSensorAvro.getTemperatureC() == value) {
                        log.info("Условие temperatureSensorAvro.getTemperatureC() == value выполнено");
                    } else if (operation.equals(ConditionOperation.GREATER_THAN) &&
                            climateSensorAvro.getTemperatureC() > value) {
                        log.info("Условие temperatureSensorAvro.getTemperatureC() > value выполнено");
                    } else if (operation.equals(ConditionOperation.LOWER_THAN) &&
                            climateSensorAvro.getTemperatureC() < value) {
                        log.info("Условие temperatureSensorAvro.getTemperatureC() < value выполнено");
                    } else {
                        log.info("Условие не выполнено");
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
        return true;
    }
}