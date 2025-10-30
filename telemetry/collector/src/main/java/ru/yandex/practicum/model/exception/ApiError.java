package ru.yandex.practicum.model.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    private List<String> errors;
    private String message;
    private String reason;
    private String status;
    private LocalDateTime timestamp;
}