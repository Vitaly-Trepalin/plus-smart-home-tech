package ru.yandex.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("Исключение: {} Сообщение: {} ", e.getClass().getSimpleName(), e.getMessage());
        return new ApiError(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn("Исключение: {} Сообщение: {} ", e.getClass().getSimpleName(), e.getMessage());
        return new ApiError(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiError handleMethodHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("Исключение: {} Сообщение: {} ", e.getClass().getSimpleName(), e.getMessage());
        return new ApiError(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiError handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.warn("Исключение: {} Сообщение: {} ", e.getClass().getSimpleName(), e.getMessage());
        return new ApiError(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
    }

    @ExceptionHandler(PropertyReferenceException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiError handlePropertyReferenceException(PropertyReferenceException e) {
        log.warn("Исключение: {} Сообщение: {} ", e.getClass().getSimpleName(), e.getMessage());
        return new ApiError(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
    }

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ProductNotFoundException handleProductNotFoundException(ProductNotFoundException e) {
        log.warn("Исключение: {} Сообщение: {} ", e.getClass().getSimpleName(), e.getMessage());
        return new ProductNotFoundException(e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.warn("Исключение: {} Сообщение: {} ", e.getClass().getSimpleName(), e.getMessage());
        return new ApiError(e.getMessage(), HttpStatus.CONFLICT, LocalDateTime.now());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(Exception e) {
        log.warn("Исключение: {} Сообщение: {} ", e.getClass().getSimpleName(), e.getMessage());
        return new ApiError(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now());
    }
}