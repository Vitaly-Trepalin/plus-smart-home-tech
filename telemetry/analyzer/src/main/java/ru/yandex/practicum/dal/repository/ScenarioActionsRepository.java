package ru.yandex.practicum.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.dal.entity.ScenarioAction;
import ru.yandex.practicum.dal.entity.ScenarioActionId;

public interface ScenarioActionsRepository extends JpaRepository<ScenarioAction, ScenarioActionId> {
}