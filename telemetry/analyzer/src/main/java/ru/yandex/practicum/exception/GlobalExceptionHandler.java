package ru.yandex.practicum.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

//    public Error handleDataIntegrityViolationException(DataIntegrityViolationException e) {
//        log.warn(e.getClass() + " : " + e.getMessage());
//        return new Error(e.getMessage());
//    }
//
//    public Error handleSqlExceptionHelper(SqlExceptionHelper e) {
//        log.warn(e.getClass() + " : " + e.);
//        return new Error(e.getMessage());
//    }

}
