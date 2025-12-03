package ru.yandex.practicum.http;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.ProductQuantityDto;
import ru.yandex.practicum.dto.warehouse.ShoppingCartDto;

public interface Warehouse {
    @PutMapping("/api/v1/warehouse")
    void addProduct(@RequestBody @Valid NewProductInWarehouseRequest newProductInWarehouseRequest);

    @PostMapping("/api/v1/warehouse/check")
    BookedProductsDto sufficiencyCheck(@RequestBody @Valid ShoppingCartDto shoppingCartDto);

    @PostMapping("/api/v1/warehouse/add")
    void addProductToWarehouse(@RequestBody @Valid ProductQuantityDto productQuantityDto);

    @GetMapping("/api/v1/warehouse/address")
    AddressDto getAddress();
}