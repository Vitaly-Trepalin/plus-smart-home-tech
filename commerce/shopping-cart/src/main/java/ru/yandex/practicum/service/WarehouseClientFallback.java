package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.ProductQuantityDto;
import ru.yandex.practicum.dto.warehouse.ShoppingCartDto;
import ru.yandex.practicum.exception.WarehouseServiceUnavailableException;

@Slf4j
@Component
public class WarehouseClientFallback implements WarehouseClient {
    @Override
    public void addProduct(NewProductInWarehouseRequest newProductInWarehouseRequest) {
    }

    @Override
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto shoppingCartDto) {
        log.warn("Сервис склада не работает. Невозможно проверить наличие товара");
        throw new WarehouseServiceUnavailableException("Сервис склада не работает. Невозможно проверить наличие товара");
    }

    @Override
    public void addProductToWarehouse(ProductQuantityDto productQuantityDto) {
    }

    @Override
    public AddressDto getAddress() {
        return null;
    }
}