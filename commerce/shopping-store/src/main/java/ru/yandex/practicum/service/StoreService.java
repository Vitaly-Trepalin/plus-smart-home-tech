package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.store.PageDto;
import ru.yandex.practicum.dto.store.Pageable;
import ru.yandex.practicum.dto.store.ProductCategory;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.dto.store.UpdateProductDto;

public interface StoreService {
    PageDto getAllByType(ProductCategory category, Pageable pageable);

    ProductDto addProduct(ProductDto productDto);

    ProductDto updateProduct(UpdateProductDto productDto);

    String removeProduct(String productId);

    String setQuantityState(SetProductQuantityStateRequest stateRequest);

    ProductDto getProduct(String productId);
}