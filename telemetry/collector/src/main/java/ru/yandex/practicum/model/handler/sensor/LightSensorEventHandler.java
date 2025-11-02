package ru.yandex.practicum.model.handler.sensor;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.model.producer.CollectorTopics;
import ru.yandex.practicum.model.producer.EventProducer;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class LightSensorEventHandler implements SensorEventHandler {
    private final EventProducer producer;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR;
    }


    @Override
    public void handle(SensorEventProto request) {
        LightSensorProto lightSensorProto = request.getLightSensor();

        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                CollectorTopics.TELEMETRY_SENSORS_V1,
                null,
                Instant.now().toEpochMilli(),
                request.getHubId(),
                new SensorEventAvro(
                        request.getId(),
                        request.getHubId(),
                        Instant.ofEpochSecond(request.getTimestamp().getSeconds(), request.getTimestamp().getNanos()),
                        new LightSensorAvro(lightSensorProto.getLinkQuality(),
                                lightSensorProto.getLuminosity()
                        )
                )
        );
        producer.getProducer().send(record);
    }
}