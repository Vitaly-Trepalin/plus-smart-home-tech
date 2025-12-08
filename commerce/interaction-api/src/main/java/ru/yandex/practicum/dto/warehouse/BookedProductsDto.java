package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.NotNull;

public record BookedProductsDto(
        @NotNull(message = "Общий вес доставки не может быть null")
        Double deliveryWeight,

        @NotNull(message = "Общий объём доставки не может быть null")
        Double deliveryVolume,

        @NotNull(message = "Информация о хрупкости вещей не может быть null")
        Boolean fragile
) {
}