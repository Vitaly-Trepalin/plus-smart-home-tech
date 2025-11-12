package ru.yandex.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class Analyzer {
    public static void main(String[] args) {
        log.info("Запуск модуля Analyzer");
        SpringApplication.run(Analyzer.class, args);
    }
}