package ru.yandex.practicum.exception;

public class NoSuchCartException extends RuntimeException {
    public NoSuchCartException(String message) {
        super(message);
    }
}