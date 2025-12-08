package ru.yandex.practicum.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NoSpecifiedProductInWarehouseException extends RuntimeException {
    HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    String userMessage = "Нет информации о товаре на складе";

    public NoSpecifiedProductInWarehouseException(String message) {
        super(message);
    }
}