package ru.yandex.practicum.service;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.contract.warehouse.Warehouse;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse", fallbackFactory = WarehouseClientFallbackFactory.class)
public interface WarehouseClient extends Warehouse {
}