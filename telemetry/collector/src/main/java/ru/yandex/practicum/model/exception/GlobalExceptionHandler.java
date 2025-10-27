package ru.yandex.practicum.model.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.warn(e.getClass() + "             " + e.getMessage());
        return ApiError.builder()
                .status(ErrorStatus.BAD_REQUEST.name())
                .reason("Запрос поступил с некорректными параметрами")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(final Exception e) {
        log.warn(e.getClass() + "             " + e.getMessage());
        return ApiError.builder()
                .status(ErrorStatus.INTERNAL_SERVER_ERROR.name())
                .reason("Неизвестная ошибка на стороне сервера статистики")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }
}