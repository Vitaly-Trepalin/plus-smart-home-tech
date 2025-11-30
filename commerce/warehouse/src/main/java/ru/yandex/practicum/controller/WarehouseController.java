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
import ru.yandex.practicum.http.Warehouse;
import ru.yandex.practicum.service.WarehouseService;

@RestController
@RequestMapping("/api/v1/warehouse")
@Slf4j
@RequiredArgsConstructor
public class WarehouseController implements Warehouse {
    private final WarehouseService service;

    @Override
    @PutMapping
    public String addProduct(@RequestBody @Valid NewProductInWarehouseRequest newProductInWarehouseRequest) {
        log.info("Запущен метод addProduct(NewProductInWarehouseRequest newProductInWarehouseRequest = {})",
                newProductInWarehouseRequest);
        return service.addProduct(newProductInWarehouseRequest);
    }

    @Override
    @PostMapping("/check")
    public BookedProductsDto sufficiencyCheck(@RequestBody @Valid ShoppingCartDto shoppingCartDto) {
        log.info("Запущен метод sufficiencyСheck(ShoppingCartDto shoppingCartDto = {})", shoppingCartDto);
        return service.sufficiencyCheck(shoppingCartDto);
    }

    @Override
    @PostMapping("/add")
    public void addProductToWarehouse(@RequestBody @Valid ProductQuantityDto productQuantityDto) {
        log.info("Запущен метод addProductToWarehouse(ProductQuantityDto productQuantityDto = {})", productQuantityDto);
        service.addProductToWarehouse(productQuantityDto);
    }

    @Override
    @GetMapping("/address")
    public AddressDto getAddress() {
        log.info("Запущен метод AddressDto getAddress()");
        return service.getAddress();
    }
}