package ru.yandex.practicum.exception;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
public class ApiError {
    private final String message;
    private final String cause;
}