package ru.yandex.practicum.runner;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.service.AggregationStarter;

@Component
@RequiredArgsConstructor
public class AggregatorRunner implements CommandLineRunner {
    final AggregationStarter aggregator;

    @Override
    public void run(String... args) throws Exception {
        aggregator.start();
    }
}