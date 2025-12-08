package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductQuantityDto(
        @NotBlank(message = "Идентификатор товара в БД не может быть пустым или null")
        String productId,

        @NotNull(message = "Количество товара не может быть null")
        Long quantity
) {
}