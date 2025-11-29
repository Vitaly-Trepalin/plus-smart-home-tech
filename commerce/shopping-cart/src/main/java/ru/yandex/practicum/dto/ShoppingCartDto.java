package ru.yandex.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record ShoppingCartDto(
        @NotBlank(message = "Идентификатор корзины в БД не может быть пустым")
        String shoppingCartId,
        @NotNull(message = "Список отображений идентификаторов товара на отобранное количество не может быть null")
        Map<String, Long> products
) {
}