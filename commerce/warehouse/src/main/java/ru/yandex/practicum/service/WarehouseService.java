package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.dto.ProductQuantityDto;
import ru.yandex.practicum.dto.ShoppingCartDto;

public interface WarehouseService {
    String addProduct(NewProductInWarehouseRequest newProductInWarehouseRequest);

    BookedProductsDto sufficiencyCheck(ShoppingCartDto shoppingCartDto);

    void addProductToWarehouse(ProductQuantityDto productQuantityDto);

    AddressDto getAddress();
}