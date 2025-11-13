package ru.yandex.practicum.config;

import lombok.Getter;
import lombok.Setter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.Properties;

@Configuration
@ConfigurationProperties(prefix = "analyzer.kafka")
@Getter
@Setter
public class AnalyzerKafkaConfig {
    private String bootstrapServer;
    private String keyDeserializer;
    private String valueSnapshotDeserializer;
    private String valueHubDeserializer;
    private String groupId;
    private String maxPollRecords;
    private String maxPollIntervalMs;
    private String fetchMaxWaitMs;

    @Bean
    @Scope("prototype")
    Consumer<String, SpecificRecordBase> consumerSnapshot() {
        Properties config = new Properties();
        config.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        config.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        config.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueSnapshotDeserializer);
        config.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        config.setProperty(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollIntervalMs);
        config.setProperty(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, fetchMaxWaitMs);
        return new KafkaConsumer<>(config);
    }

    @Bean
    @Scope("prototype")
    Consumer<String, SpecificRecordBase> consumerHub() {
        Properties config = new Properties();
        config.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        config.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        config.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueHubDeserializer);
        config.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        config.setProperty(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollIntervalMs);
        config.setProperty(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, fetchMaxWaitMs);
        return new KafkaConsumer<>(config);
    }
}
