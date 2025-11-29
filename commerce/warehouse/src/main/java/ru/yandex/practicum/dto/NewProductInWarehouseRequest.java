package ru.yandex.practicum.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NewProductInWarehouseRequest(
        @NotBlank
        String productId,

        Boolean fragile,

        @NotNull(message = "Размеры товара не могут быть null")
        DimensionDto dimension,

        @NotNull(message = "Вес не может быть null")
        @DecimalMin(value = "1.0", message = "Вес не может быть меньше 1.0")
        Double weight
) {
}