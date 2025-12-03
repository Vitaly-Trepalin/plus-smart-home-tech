package ru.yandex.practicum.dto.cart;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChangeProductQuantityRequest(
        @NotBlank(message = "Идентификатор товара не может быть null или пустым")
        String productId,
        @NotNull(message = "Новое количество товара не может быть null")
        Long newQuantity
) {
}