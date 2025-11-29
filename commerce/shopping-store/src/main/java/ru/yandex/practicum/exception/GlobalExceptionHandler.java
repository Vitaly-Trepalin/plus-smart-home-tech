package ru.yandex.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ProductNotFoundException handleProductNotFoundException(ProductNotFoundException e) {
        log.warn("Исключение : {}", e.getClass().getSimpleName(), e.getMessage(), e.getCause());
        return new ProductNotFoundException(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(Exception e) {
        log.warn("Исключение : {}", e.getClass().getSimpleName(), e.getMessage(), e.getCause());
        return new ApiError(e.getMessage(), e.getCause().toString());
    }
}