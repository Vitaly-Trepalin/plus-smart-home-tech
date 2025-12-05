package ru.yandex.practicum.service;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.api.Warehouse;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse")
public interface WarehouseClient extends Warehouse {
}