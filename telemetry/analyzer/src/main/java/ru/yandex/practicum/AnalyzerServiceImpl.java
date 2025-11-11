package ru.yandex.practicum;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.mapper.Converter;
import ru.yandex.practicum.mapper.Mapper;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.ActionType;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.ConditionOperation;
import ru.yandex.practicum.model.ConditionType;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.model.ScenarioAction;
import ru.yandex.practicum.model.ScenarioActionId;
import ru.yandex.practicum.model.ScenarioCondition;
import ru.yandex.practicum.model.ScenarioConditionId;
import ru.yandex.practicum.model.Sensor;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioActionsRepository;
import ru.yandex.practicum.repository.ScenarioConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.repository.SensorRepository;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
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
        String id = deviceAdded.getId();
        Sensor sensor = new Sensor(deviceAdded.getId(), hubId);
        sensorRepository.save(sensor);
    }

    @Override
    @Transactional
    public void removeDevice(DeviceRemovedEventAvro deviceRemoved) {
        sensorRepository.deleteById(deviceRemoved.getId());
    }

    @Override
    @Transactional
    public void addScenario(ScenarioAddedEventAvro scenarioAdded, String hubIdd) {
        String hubId = hubIdd;
        String name = scenarioAdded.getName();
        List<ScenarioConditionAvro> conditions = scenarioAdded.getConditions();
        List<DeviceActionAvro> actions = scenarioAdded.getActions();

        Scenario scenario = Scenario.builder()
                .hubId(hubId)
                .name(name)
                .build();
        Scenario savedScenario = scenarioRepository.save(scenario);

        conditions.forEach(condition -> {
            ConditionType type = Converter.mapToConditionType(condition.getType());
            ConditionOperation operation = Converter.mapToConditionOperation(condition.getOperation());
            Integer value = Mapper.getValueScenarioCondition(condition.getValue());
            Condition newCondition = new Condition(
                    0,
                    type,
                    operation,
                    value);
            Condition savedCondition = conditionRepository.save(newCondition);


            Sensor sensor = sensorRepository.findById(condition.getSensorId());

            ScenarioConditionId scenarioConditionId = new ScenarioConditionId(
                    scenario.getId(),
                    sensor.getId(),
                    savedCondition.getId());

            ScenarioCondition scenarioCondition = new ScenarioCondition(
                    scenarioConditionId,
                    scenario,
                    sensor,
                    savedCondition);
            scenarioConditionRepository.save(scenarioCondition);
        });

        actions.forEach(action -> {
            ActionType type = Converter.mapToActionType(action.getType());
            Integer value = action.getValue();
            Action newAction = Action.builder()
                    .type(type)
                    .value(value)
                    .build();
            Action savedAction = actionRepository.save(newAction);


            Sensor sensor = sensorRepository.findById(action.getSensorId());

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
            scenarioActionsRepository.save(scenarioAction);
        });


    }

    @Override
    @Transactional
    public void removeScenario(ScenarioRemovedEventAvro scenarioRemoved) {
        String name = scenarioRemoved.getName();

        scenarioRepository.deleteByName(name);
    }

    @Override
    @Transactional
    public void executingSnapshot(SensorsSnapshotAvro sensorsSnapshotAvro) {
        Map<String, SensorStateAvro> sensorsState = sensorsSnapshotAvro.getSensorsState(); // получил все данные датчиков из снапшота

        String hubId = sensorsSnapshotAvro.getHubId();
        List<Scenario> scenarios = scenarioRepository.findByHubId(hubId); // получил весь список сценариев для этого хаба
        System.out.println("Список всех сценариев хаба: " + scenarios);

        for (Scenario scenario : scenarios) {

            List<ScenarioCondition> scenarioConditions = scenario.getScenarioConditions(); // через этот список объектов добираюсь до условий Condition
            boolean isCompleted = checkingScenarioConditions(scenarioConditions, sensorsState); // выполнены ли все условия сценария
            System.out.println(isCompleted);

            if (isCompleted) {
                List<ScenarioAction> scenariosActions = scenario.getScenarioActions();

                for (ScenarioAction scenarioAction : scenariosActions) {
                    Sensor sensor = scenarioAction.getSensor();
                    String sensorId = sensor.getId();

                    Action action = scenarioAction.getAction();
                    ActionType actionType = action.getType();
                    Integer value = action.getValue();

                    DeviceActionProto deviceActionProto;
                    if(value == null) {
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

                    System.out.println("Отправляется сообщение :" + deviceActionRequest);
                    hubRouterClient.sendDeviceAction(deviceActionRequest);
                }


            }
        }
    }

    private boolean checkingScenarioConditions(List<ScenarioCondition> scenarioConditions, Map<String, SensorStateAvro> sensorsState) {
        for (ScenarioCondition scenarioCondition : scenarioConditions) {
            String id = scenarioCondition.getSensor().getId(); // id датчика в сценарии и поиск такого в снапшоте

            if (sensorsState.containsKey(id)) {

                System.out.println("Есть такой id датчик " + scenarioCondition.getSensor().getId() +
                        ". Теперь проверяется условие");

                Condition condition = scenarioCondition.getCondition(); // полностью разобрал сценарий на переменные
                ConditionType type = condition.getType();
                ConditionOperation operation = condition.getOperation();
                Integer value = condition.getValue();


                SensorStateAvro sensorStateAvro = sensorsState.get(id); // получили по ключу устройства объект почти готовый для проверки
                Object data = sensorStateAvro.getData();
                if (type.equals(ConditionType.LUMINOSITY)) {
                    LightSensorAvro lightSensorAvro = (LightSensorAvro) data;
                    if (operation.equals(ConditionOperation.EQUALS) && lightSensorAvro.getLuminosity() == value) {
                        System.out.println("Условие lightSensorAvro.getLuminosity() == value выполнено ");
                    } else if (operation.equals(ConditionOperation.GREATER_THAN) && lightSensorAvro.getLuminosity() > value) {
                        System.out.println("Условие lightSensorAvro.getLuminosity() > value выполнено ");
                    } else if (operation.equals(ConditionOperation.LOWER_THAN) && lightSensorAvro.getLuminosity() < value) {
                        System.out.println("Условие lightSensorAvro.getLuminosity() < value выполнено ");
                    } else {
                        System.out.println("Условие не выполнено ");
                        return false;
                    }
                } else if (type.equals(ConditionType.MOTION)) {
                    MotionSensorAvro motionSensorAvro = (MotionSensorAvro) data;

                    int motion = 0;
                    if (motionSensorAvro.getMotion()) {
                        motion = 1;
                    }

                    if (operation.equals(ConditionOperation.EQUALS) && motion == value) {
                        System.out.println("Условие motion == value выполнено ");
                    } else {
                        System.out.println("Условие не выполнено ");
                        return false;
                    }
                } else if (type.equals(ConditionType.SWITCH)) {
                    SwitchSensorAvro switchSensorAvro = (SwitchSensorAvro) data;

                    int switchSensor = 0;
                    if (switchSensorAvro.getState()) {
                        switchSensor = 1;
                    }

                    if (operation.equals(ConditionOperation.EQUALS) && switchSensor == value) {
                        System.out.println("Условие switchSensor == value выполнено ");
                    } else {
                        System.out.println("Условие не выполнено ");
                        return false;
                    }
                } else if (type.equals(ConditionType.CO2LEVEL)) {
                    ClimateSensorAvro climateSensorAvro = (ClimateSensorAvro) data;
                    if (operation.equals(ConditionOperation.EQUALS) && climateSensorAvro.getCo2Level() == value) {
                        System.out.println("Условие climateSensorAvro.getCo2Level() == value выполнено ");
                    } else if (operation.equals(ConditionOperation.GREATER_THAN) && climateSensorAvro.getCo2Level() > value) {
                        System.out.println("Условие climateSensorAvro.getCo2Level() > value выполнено ");
                    } else if (operation.equals(ConditionOperation.LOWER_THAN) && climateSensorAvro.getCo2Level() < value) {
                        System.out.println("Условие climateSensorAvro.getCo2Level() < value выполнено ");
                    } else {
                        System.out.println("Условие не выполнено ");
                        return false;
                    }

                } else if (type.equals(ConditionType.HUMIDITY)) {
                    ClimateSensorAvro climateSensorAvro = (ClimateSensorAvro) data;
                    if (operation.equals(ConditionOperation.EQUALS) && climateSensorAvro.getHumidity() == value) {
                        System.out.println("Условие climateSensorAvro.getHumidity() == value выполнено ");
                    } else if (operation.equals(ConditionOperation.GREATER_THAN) && climateSensorAvro.getHumidity() > value) {
                        System.out.println("Условие climateSensorAvro.getHumidity() > value выполнено ");
                    } else if (operation.equals(ConditionOperation.LOWER_THAN) && climateSensorAvro.getHumidity() < value) {
                        System.out.println("Условие climateSensorAvro.getHumidity() < value выполнено ");
                    } else {
                        System.out.println("Условие не выполнено ");
                        return false;
                    }
                } else if (type.equals(ConditionType.TEMPERATURE)) {
                    ClimateSensorAvro climateSensorAvro = (ClimateSensorAvro) data;
                    if (operation.equals(ConditionOperation.EQUALS) && climateSensorAvro.getTemperatureC() == value) {
                        System.out.println("Условие temperatureSensorAvro.getTemperatureC() == value выполнено ");
                    } else if (operation.equals(ConditionOperation.GREATER_THAN) && climateSensorAvro.getTemperatureC() > value) {
                        System.out.println("Условие temperatureSensorAvro.getTemperatureC() > value выполнено ");
                    } else if (operation.equals(ConditionOperation.LOWER_THAN) && climateSensorAvro.getTemperatureC() < value) {
                        System.out.println("Условие temperatureSensorAvro.getTemperatureC() < value выполнено ");
                    } else {
                        System.out.println("Условие не выполнено ");
                        return false;
                    }
//                    else if (type.equals(ConditionType.TEMPERATURE)) {
//                        TemperatureSensorAvro temperatureSensorAvro = (TemperatureSensorAvro) data;
//                        if (operation.equals(ConditionOperation.EQUALS) && temperatureSensorAvro.getTemperatureC() == value) {
//                            System.out.println("Условие temperatureSensorAvro.getTemperatureC() == value выполнено ");
//                        } else if (operation.equals(ConditionOperation.GREATER_THAN) && temperatureSensorAvro.getTemperatureC() > value) {
//                            System.out.println("Условие temperatureSensorAvro.getTemperatureC() > value выполнено ");
//                        } else if (operation.equals(ConditionOperation.LOWER_THAN) && temperatureSensorAvro.getTemperatureC() < value) {
//                            System.out.println("Условие temperatureSensorAvro.getTemperatureC() < value выполнено ");
//                        } else {
//                            System.out.println("Условие не выполнено ");
//                            return false;
//                        }
                }
            } else {
                return false;
            }
        }
        return true;
    }
}