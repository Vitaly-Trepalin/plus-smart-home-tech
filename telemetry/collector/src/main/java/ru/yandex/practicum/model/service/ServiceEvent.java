package ru.yandex.practicum.model.service;

import ru.yandex.practicum.model.dto.hub.HubEvent;
import ru.yandex.practicum.model.dto.sensor.SensorEvent;

public interface ServiceEvent {
    void collectSensorEvent(SensorEvent sensorEvent);

    void collectorsHubEvent(HubEvent hubEvent);
}