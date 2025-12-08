package ru.yandex.practicum.dto.store;

public record SortOrder(
        String property,
        String direction
) {
}