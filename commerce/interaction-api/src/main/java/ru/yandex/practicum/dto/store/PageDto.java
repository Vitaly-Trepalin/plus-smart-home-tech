package ru.yandex.practicum.dto.store;

import java.util.List;

public record PageDto(
        List<ProductDto> content,
        List<SortOrder> sort
) {
}