package ru.yandex.practicum.model.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.model.dto.hub.HubEvent;
import ru.yandex.practicum.model.dto.sensor.SensorEvent;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.model.service.ServiceEvent;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class ControllerEvent {
    private final ServiceEvent service;

    @PostMapping("/sensors")
    @ResponseStatus(HttpStatus.OK)
    public void collectSensorEvent(@RequestBody @Valid SensorEvent sensorEvent) {
        log.info("Method launched collectSensorEvent(SensorEvent sensorEvent = {})", sensorEvent);
        service.collectSensorEvent(sensorEvent);
    }

    @PostMapping("/hubs")
    @ResponseStatus(HttpStatus.OK)
    public void collectorsHubEvent(@RequestBody @Valid HubEvent hubEvent) {
        log.info("Method launched collectorsHubEvent(HubEvent hubEvent = {})", hubEvent);
        service.collectorsHubEvent(hubEvent);
    }
}