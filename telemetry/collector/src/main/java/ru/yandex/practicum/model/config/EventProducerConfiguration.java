package ru.yandex.practicum.model.config;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.model.producer.EventProducer;

import java.util.Properties;

@Configuration
public class EventProducerConfiguration {

    @Bean
    public EventProducer getEventProducer() {
        return new EventProducer() {
            @Value(value = "${spring.kafka.bootstrap-server}")
            String bootstrapServer;
            @Value(value = "${spring.kafka.producer.key.serializer}")
            String key;
            @Value(value = "${spring.kafka.producer.value.serializer}")
            String value;

            @Override
            public Producer<String, SpecificRecordBase> getProducer() {
                Properties config = new Properties();
                config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
                config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, key);
                config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, value);
                return new KafkaProducer<>(config);
            }
        };
    }
}