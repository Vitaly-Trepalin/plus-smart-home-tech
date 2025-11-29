package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.dto.ProductQuantityDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.service.WarehouseService;

@RestController
@RequestMapping("/api/v1/warehouse")
@Slf4j
@RequiredArgsConstructor
public class WarehouseController {
    private final WarehouseService service;

    @PutMapping
    public String addProduct(@RequestBody @Valid NewProductInWarehouseRequest newProductInWarehouseRequest) {
        log.info("Запущен метод addProduct(NewProductInWarehouseRequest newProductInWarehouseRequest = {})",
                newProductInWarehouseRequest);
        return service.addProduct(newProductInWarehouseRequest);
    }

    @PostMapping("/check")
    public BookedProductsDto sufficiencyCheck(@RequestBody @Valid ShoppingCartDto shoppingCartDto) {
        log.info("Запущен метод sufficiencyСheck(ShoppingCartDto shoppingCartDto = {})", shoppingCartDto);
        return service.sufficiencyCheck(shoppingCartDto);
    }

    @PostMapping("/add")
    public void addProductToWarehouse(@RequestBody @Valid ProductQuantityDto productQuantityDto) {
        log.info("Запущен метод addProductToWarehouse(ProductQuantityDto productQuantityDto = {})", productQuantityDto);
        service.addProductToWarehouse(productQuantityDto);
    }

    @GetMapping("/address")
    public AddressDto getAddress() {
        log.info("Запущен метод AddressDto getAddress()");
        return service.getAddress();
    }
}