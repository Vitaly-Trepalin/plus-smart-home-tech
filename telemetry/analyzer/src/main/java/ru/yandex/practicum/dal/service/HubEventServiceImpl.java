package ru.yandex.practicum.dal.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dal.entity.Action;
import ru.yandex.practicum.dal.entity.Condition;
import ru.yandex.practicum.dal.entity.Scenario;
import ru.yandex.practicum.dal.entity.Sensor;
import ru.yandex.practicum.dal.mapper.Converter;
import ru.yandex.practicum.dal.repository.ActionRepository;
import ru.yandex.practicum.dal.repository.ConditionRepository;
import ru.yandex.practicum.dal.repository.ScenarioRepository;
import ru.yandex.practicum.dal.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class HubEventServiceImpl implements HubEventService {
    private final ScenarioRepository scenarioRepository;
    private final SensorRepository sensorRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;

    @Override
    @Transactional
    public void addSensor(DeviceAddedEventAvro deviceAddedEvent, String hubId) {
        sensorRepository.save(Sensor.builder()
                .id(deviceAddedEvent.getId())
                .hubId(hubId)
                .build());
    }

    @Override
    @Transactional
    public void removeSensor(DeviceRemovedEventAvro deviceRemovedEvent) {
        sensorRepository.deleteById(deviceRemovedEvent.getId());
    }

    @Override
    @Transactional
    public void addScenario(ScenarioAddedEventAvro scenarioAddedEventAvro, String hubId) {
        if (scenarioAddedEventAvro.getConditions() == null || scenarioAddedEventAvro.getConditions().isEmpty() ||
                scenarioAddedEventAvro.getActions() == null || scenarioAddedEventAvro.getActions().isEmpty()) {
            log.info("Сценарий не может быть без условий или действий. Сценарий {} от hub {} не добавлен",
                    scenarioAddedEventAvro.getName(), hubId);
            return;
        }

        List<ScenarioConditionAvro> scenarioConditionAvros = scenarioAddedEventAvro.getConditions(); // блок для добавления событий в БД
        List<Condition> listConditions = new ArrayList<>();
        List<String> conditionSensorIds = new ArrayList<>();
        for (ScenarioConditionAvro scenarioConditionAvro : scenarioConditionAvros) {
            conditionSensorIds.add(scenarioConditionAvro.getSensorId());
            listConditions.add(Condition.builder()
                    .type(Converter.mapToConditionType(scenarioConditionAvro.getType()))
                    .operation(Converter.mapToConditionOperation(scenarioConditionAvro.getOperation()))
                    .value(Converter.mapToIntegerValue(scenarioConditionAvro.getValue()))
                    .build());
        }
        List<Condition> savedConditions = conditionRepository.saveAll(listConditions);

        List<DeviceActionAvro> deviceActionAvros = scenarioAddedEventAvro.getActions(); // блок для добавления действий в БД
        List<Action> listActions = new ArrayList<>();
        List<String> actionSensorIds = new ArrayList<>();
        for (DeviceActionAvro deviceActionAvro : deviceActionAvros) {
            actionSensorIds.add(deviceActionAvro.getSensorId());
            listActions.add(Action.builder()
                    .type(Converter.mapToActionType(deviceActionAvro.getType()))
                    .value(Converter.mapToIntegerValue(deviceActionAvro.getValue()))
                    .build());
        }
        List<Action> savedActions = actionRepository.saveAll(listActions);

        String name = scenarioAddedEventAvro.getName(); // блок добавления в сценария в БД
        Map<String, Condition> conditions = IntStream.range(0, savedConditions.size()).boxed()
                .collect(Collectors.toMap(conditionSensorIds::get, savedConditions::get));
        Map<String, Action> actions = IntStream.range(0, savedActions.size()).boxed()
                .collect(Collectors.toMap(actionSensorIds::get, savedActions::get));
        scenarioRepository.save(Scenario.builder()
                .hubId(hubId)
                .name(name)
                .conditions(conditions)
                .actions(actions)
                .build());
    }

    @Override
    @Transactional
    public void removedScenario(ScenarioRemovedEventAvro scenarioRemovedEventAvro, String hubId) {
        String scenarioName = scenarioRemovedEventAvro.getName();
        Scenario scenario = scenarioRepository.findByHubIdAndName(hubId, scenarioName)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Сценарий %s не найден", scenarioName)));
        Collection<Condition> conditions = scenario.getConditions().values();
        Collection<Action> actions = scenario.getActions().values();
        scenarioRepository.deleteByHubIdAndName(hubId, scenarioName);
        conditionRepository.deleteAll(conditions);
        actionRepository.deleteAll(actions);
    }
}