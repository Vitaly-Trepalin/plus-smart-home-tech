package ru.yandex.practicum.model;

import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;
import org.springframework.stereotype.Service;

@Service
public class ServiceEventImpl implements ServiceEvent {
    @Override
    public void collectSensorEvent(SensorEvent sensorEvent) {

    }

    @Override
    public void collectorsHubEvent(HubEvent hubEvent) {

    }
}