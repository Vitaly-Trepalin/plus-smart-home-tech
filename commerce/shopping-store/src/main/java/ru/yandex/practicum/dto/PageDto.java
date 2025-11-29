package ru.yandex.practicum.dto;

import java.util.List;

public record PageDto(
        List<ProductDto> content,
        List<SortOrder> sort
) {
}