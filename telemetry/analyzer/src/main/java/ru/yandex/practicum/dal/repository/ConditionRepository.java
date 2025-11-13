package ru.yandex.practicum.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.dal.entity.Condition;

public interface ConditionRepository extends JpaRepository<Condition, Long> {
}