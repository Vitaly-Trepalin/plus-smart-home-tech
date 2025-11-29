package ru.yandex.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(NoSuchCartException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNoSuchBasketException(NoSuchCartException e) {
        log.warn("Исключение : {}", e.getClass().getSimpleName(), e.getMessage());
        return new ApiError(e.getMessage());
    }

    @ExceptionHandler(NoProductsInShoppingCartException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNoProductsInShoppingCartException(NoProductsInShoppingCartException e) {
        log.warn("Исключение : {}", e.getClass().getSimpleName(), e.getMessage());
        return new ApiError(e.getMessage());
    }

    @ExceptionHandler(NotAuthorizedUserException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiError handleNotAuthorizedUserException(NotAuthorizedUserException e) {
        log.warn("Исключение : {}", e.getClass().getSimpleName(), e.getMessage());
        return new ApiError(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(Exception e) {
        log.warn("Исключение : {}", e.getClass().getSimpleName(), e.getMessage());
        return new ApiError(e.getMessage());
    }
}