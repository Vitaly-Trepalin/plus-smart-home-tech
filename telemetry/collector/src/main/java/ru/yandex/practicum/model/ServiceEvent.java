package ru.yandex.practicum.model;

import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;

public interface ServiceEvent {
    void collectSensorEvent(SensorEvent sensorEvent);

    void collectorsHubEvent(HubEvent hubEvent);
}