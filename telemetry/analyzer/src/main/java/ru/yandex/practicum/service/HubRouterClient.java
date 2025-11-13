package ru.yandex.practicum.service;

import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;

@Service
@Slf4j
public class HubRouterClient {
    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public HubRouterClient(@GrpcClient("hub-router")
                           HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient) {
        this.hubRouterClient = hubRouterClient;
    }

    public void sendDeviceAction(DeviceActionRequest request) {
        try {
            hubRouterClient.handleDeviceAction(request);
            log.info("Действие для датчика {} успешно отправлено", request);
        } catch (
                StatusRuntimeException e) {
            log.error("Не удалось отправить действие устройства на hub router. Status: {}, Description: {}",
                    e.getStatus(), e.getStatus().getDescription(), e);
            throw new RuntimeException("Не удалось отправить действие на датчики: " + e.getStatus(), e);
        }
    }
}