package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record DimensionDto(
        @NotNull(message = "Ширина не может быть null")
        @DecimalMin(value = "1.0", message = "Ширина не может быть меньше 1.0")
        Double width,

        @NotNull(message = "Высота не может быть null")
        @DecimalMin(value = "1.0", message = "Высота не может быть меньше 1.0")
        Double height,

        @NotNull(message = "Толшина не может быть null")
        @DecimalMin(value = "1.0", message = "Толщина не может быть меньше 1.0")
        Double depth
) {
}