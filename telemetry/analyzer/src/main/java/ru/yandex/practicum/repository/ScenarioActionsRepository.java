package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.ScenarioAction;
import ru.yandex.practicum.model.ScenarioActionId;

public interface ScenarioActionsRepository extends JpaRepository<ScenarioAction, ScenarioActionId> {
}
