package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.PageDto;
import ru.yandex.practicum.dto.Pageable;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.dto.UpdateProductDto;
import ru.yandex.practicum.entity.ProductCategory;

public interface StoreService {
    PageDto getAllByType(ProductCategory category, Pageable pageable);

    ProductDto addProduct(ProductDto productDto);

    ProductDto updateProduct(UpdateProductDto productDto);

    String removeProduct(String productId);

    String setQuantityState(SetProductQuantityStateRequest stateRequest);

    ProductDto getProduct(String productId);
}