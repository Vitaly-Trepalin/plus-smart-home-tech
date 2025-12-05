package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.ProductQuantityDto;
import ru.yandex.practicum.dto.warehouse.ShoppingCartDto;
import ru.yandex.practicum.api.Warehouse;
import ru.yandex.practicum.service.WarehouseService;
import ru.yandex.practicum.util.Loggable;

@RestController
@RequestMapping("/api/v1/warehouse")
@Slf4j
@RequiredArgsConstructor
public class WarehouseController implements Warehouse {
    private final WarehouseService service;

    @Override
    @PutMapping
    @Loggable
    public void addProduct(@RequestBody @Valid NewProductInWarehouseRequest newProductInWarehouseRequest) {
        service.addProduct(newProductInWarehouseRequest);
    }

    @Override
    @PostMapping("/check")
    @Loggable
    public BookedProductsDto sufficiencyCheck(@RequestBody @Valid ShoppingCartDto shoppingCartDto) {
        return service.sufficiencyCheck(shoppingCartDto);
    }

    @Override
    @PostMapping("/add")
    @Loggable
    public void addProductToWarehouse(@RequestBody @Valid ProductQuantityDto productQuantityDto) {
        service.addProductToWarehouse(productQuantityDto);
    }

    @Override
    @GetMapping("/address")
    @Loggable
    public AddressDto getAddress() {
        return service.getAddress();
    }
}