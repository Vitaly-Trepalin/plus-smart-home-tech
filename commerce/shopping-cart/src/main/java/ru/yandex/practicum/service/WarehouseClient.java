package ru.yandex.practicum.service;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.http.Warehouse;

@FeignClient(name = "warehouse")
public interface WarehouseClient extends Warehouse {
}