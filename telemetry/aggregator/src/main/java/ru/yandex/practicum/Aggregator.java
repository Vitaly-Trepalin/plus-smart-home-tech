package ru.yandex.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class Aggregator {
    public static void main(String[] args) {
        log.info("Запуск модуля Aggregator");
        SpringApplication.run(Aggregator.class, args);
    }
}