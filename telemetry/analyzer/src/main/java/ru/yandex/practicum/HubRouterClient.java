package ru.yandex.practicum;

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
            log.info("Отправка действия устройства на хаб: {}", request);
            hubRouterClient.handleDeviceAction(request);
            log.info("Действие устройства успешно отправлено");
        } catch (
                StatusRuntimeException e) {
            log.error("Не удалось отправить действие устройства на маршрутизатор-концентратор. Status: {}, Description: {}",
                    e.getStatus(), e.getStatus().getDescription(), e);
            // Можно выбросить кастомное исключение или обработать иначе
            throw new RuntimeException("Failed to send device action: " + e.getStatus(), e);
        }

    }
    }