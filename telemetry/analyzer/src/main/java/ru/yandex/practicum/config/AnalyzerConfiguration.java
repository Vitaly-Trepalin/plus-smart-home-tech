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

import java.util.Properties;

@Configuration
@ConfigurationProperties("kafka.config")
@Getter
@Setter
public class AnalyzerConfiguration {
    private String bootstrapServers;
    private String keyDeserializer;
    private String valueSnapshotDeserializer;
    private String valueHubDeserializer;
    private String groupSnapshot;
    private String groupHub;
    private String fetchMaxWaitMs;
    private String maxPollRecordsConfig;
    private String enableAutoCommit;

    @Bean
    public Consumer<String, SpecificRecordBase> snapshotConsumer() {
        Properties config = new Properties();
        config.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        config.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueSnapshotDeserializer);
        config.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupSnapshot);
        config.setProperty(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, fetchMaxWaitMs);
        config.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecordsConfig);
        config.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        return new KafkaConsumer<>(config);
    }

    @Bean
    public Consumer<String, SpecificRecordBase> hubConsumer() {
        Properties config = new Properties();
        config.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        config.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueHubDeserializer);
        config.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupHub);
        config.setProperty(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, fetchMaxWaitMs);
        config.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecordsConfig);
        config.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        return new KafkaConsumer<>(config);
    }
}