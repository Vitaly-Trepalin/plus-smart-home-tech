package ru.yandex.practicum.model;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
        System.out.println();
        System.out.println(sensorEvent);
        System.out.println(sensorEvent.getClass());
        System.out.println();
        service.collectSensorEvent(sensorEvent);
    }

    @PostMapping("/hubs")
    @ResponseStatus(HttpStatus.OK)
    public void collectorsHubEvent(@RequestBody @Valid HubEvent hubEvent) {
        log.info("Method launched collectorsHubEvent(HubEvent hubEvent = {})", hubEvent);
        System.out.println();
        System.out.println(hubEvent);
        System.out.println(hubEvent.getClass());
        System.out.println();
        service.collectorsHubEvent(hubEvent);
    }
}