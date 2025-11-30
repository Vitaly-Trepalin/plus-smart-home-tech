package ru.yandex.practicum.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SpecifiedProductAlreadyInWarehouseException extends RuntimeException {
    HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    String userMessage = "Ошибка, товар с таким описанием уже зарегистрирован на складе";

    public SpecifiedProductAlreadyInWarehouseException(String message) {
        super(message);
    }
}