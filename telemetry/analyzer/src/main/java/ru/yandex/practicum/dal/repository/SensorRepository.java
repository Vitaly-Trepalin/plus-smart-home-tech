package ru.yandex.practicum.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.dal.entity.Sensor;

public interface SensorRepository extends JpaRepository<Sensor, String> {
    void deleteById(String id);
}