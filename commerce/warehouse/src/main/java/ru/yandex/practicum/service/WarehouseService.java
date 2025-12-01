package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.ProductQuantityDto;
import ru.yandex.practicum.dto.warehouse.ShoppingCartDto;

public interface WarehouseService {
    void addProduct(NewProductInWarehouseRequest newProductInWarehouseRequest);

    BookedProductsDto sufficiencyCheck(ShoppingCartDto shoppingCartDto);

    void addProductToWarehouse(ProductQuantityDto productQuantityDto);

    AddressDto getAddress();
}