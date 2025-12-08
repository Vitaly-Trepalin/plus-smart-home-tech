package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record ShoppingCartDto(
        @NotNull(message = "Идентификатор корзины в БД не может быть пустым или null")
        String shoppingCartId,

        @NotNull(message = "Отображение идентификатора товара на отобранное количество не может быть null")
        Map<String, Long> products
) {
}