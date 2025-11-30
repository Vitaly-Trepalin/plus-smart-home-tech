package ru.yandex.practicum.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotAuthorizedUserException extends RuntimeException {
    HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
    String userMessage = "Имя пользователя не должно быть пустым";

    public NotAuthorizedUserException(String message) {
        super(message);
    }
}