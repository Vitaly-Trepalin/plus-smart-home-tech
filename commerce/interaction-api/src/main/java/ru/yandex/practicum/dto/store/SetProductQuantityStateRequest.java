package ru.yandex.practicum.dto.store;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SetProductQuantityStateRequest(
        @NotBlank(message = "Отсутствует идентификатор товара в БД")
        @Size(max = 255, message = "Идентификатор товара превысил 255 символов")
        String productId,
        @NotNull
        QuantityState quantityState
) {
}