package ru.yandex.practicum.dto.store;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record Pageable(
        @PositiveOrZero(message = "Страница не может быть отрицательной")
        Integer page,
        @Positive(message = "Размер страницы не может быть отрицательным или равняться нулю")
        Integer size,
        String[] sort
) {
}