package ru.yandex.practicum.model.grpc;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.model.mapper.Converter;
import ru.yandex.practicum.model.mapper.Mapper;
import ru.yandex.practicum.model.producer.CollectorTopics;
import ru.yandex.practicum.model.producer.EventProducer;

import java.time.Instant;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class EventController extends CollectorControllerGrpc.CollectorControllerImplBase {
    private final EventProducer producer;

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            log.info("Получаю данные {}", request.getAllFields());
            SensorEventProto.PayloadCase payloadCase = request.getPayloadCase();
            ProducerRecord<String, SpecificRecordBase> record;

            record = switch (payloadCase) {
                case LIGHT_SENSOR -> {
                    LightSensorProto lightSensorProto = request.getLightSensor();

                    yield new ProducerRecord<>(
                            CollectorTopics.TELEMETRY_SENSORS_V1,
                            null,
                            Instant.now().toEpochMilli(),
                            request.getHubId(),
                            new SensorEventAvro(request.getId(),
                                    request.getHubId(),
                                    Instant.ofEpochSecond(request.getTimestamp().getSeconds(),
                                            request.getTimestamp().getNanos()),
                                    new LightSensorAvro(lightSensorProto.getLinkQuality(),
                                            lightSensorProto.getLuminosity())));
                }
                case MOTION_SENSOR -> {
                    MotionSensorProto motionSensorProto = request.getMotionSensor();
                    yield new ProducerRecord<>(
                            CollectorTopics.TELEMETRY_SENSORS_V1,
                            null,
                            Instant.now().toEpochMilli(),
                            request.getHubId(),
                            new SensorEventAvro(
                                    request.getId(),
                                    request.getHubId(),
                                    Instant.ofEpochSecond(request.getTimestamp().getSeconds(),
                                            request.getTimestamp().getNanos()),
                                    new MotionSensorAvro(
                                            motionSensorProto.getLinkQuality(),
                                            motionSensorProto.getMotion(),
                                            motionSensorProto.getVoltage()
                                    )));
                }
                case TEMPERATURE_SENSOR -> {
                    TemperatureSensorProto temperatureSensorProto = request.getTemperatureSensor();
                    yield new ProducerRecord<>(
                            CollectorTopics.TELEMETRY_SENSORS_V1,
                            null,
                            Instant.now().toEpochMilli(),
                            request.getHubId(),
                            new SensorEventAvro(
                                    request.getId(),
                                    request.getHubId(),
                                    Instant.ofEpochSecond(request.getTimestamp().getSeconds(),
                                            request.getTimestamp().getNanos()),
                                    new TemperatureSensorAvro(
                                            temperatureSensorProto.getTemperatureC(),
                                            temperatureSensorProto.getTemperatureF()
                                    )));
                }
                case CLIMATE_SENSOR -> {
                    ClimateSensorProto climateSensorProto = request.getClimateSensor();
                    yield new ProducerRecord<>(
                            CollectorTopics.TELEMETRY_SENSORS_V1,
                            null,
                            Instant.now().toEpochMilli(),
                            request.getHubId(),
                            new SensorEventAvro(
                                    request.getId(),
                                    request.getHubId(),
                                    Instant.ofEpochSecond(request.getTimestamp().getSeconds(),
                                            request.getTimestamp().getNanos()),
                                    new ClimateSensorAvro(
                                            climateSensorProto.getTemperatureC(),
                                            climateSensorProto.getHumidity(),
                                            climateSensorProto.getCo2Level()
                                    )
                            )
                    );
                }
                case SWITCH_SENSOR -> {
                    SwitchSensorProto switchSensorProto = request.getSwitchSensor();
                    yield new ProducerRecord<>(
                            CollectorTopics.TELEMETRY_SENSORS_V1,
                            null,
                            Instant.now().toEpochMilli(),
                            request.getHubId(),
                            new SensorEventAvro(
                                    request.getId(),
                                    request.getHubId(),
                                    Instant.ofEpochSecond(request.getTimestamp().getSeconds(),
                                            request.getTimestamp().getNanos()),
                                    new SwitchSensorAvro(
                                            switchSensorProto.getState()
                                    )
                            )
                    );
                }
                default -> throw new IllegalArgumentException("Нет такого датчика");

            };

            producer.getProducer().send(record);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.warn(e.getLocalizedMessage());
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            ProducerRecord<String, SpecificRecordBase> record;
            HubEventProto.PayloadCase payloadCase = request.getPayloadCase();

            record = switch (payloadCase) {
                case DEVICE_ADDED -> {
                    DeviceAddedEventProto deviceAddedEventProto = request.getDeviceAdded();
                    yield new ProducerRecord<>(
                            CollectorTopics.TELEMETRY_HUBS_V1,
                            null,
                            Instant.now().toEpochMilli(),
                            request.getHubId(),
                            new HubEventAvro(
                                    request.getHubId(),
                                    Instant.ofEpochSecond(request.getTimestamp().getSeconds(),
                                            request.getTimestamp().getNanos()),
                                    new DeviceAddedEventAvro(
                                            deviceAddedEventProto.getId(),
                                            Converter.mapToAvro(deviceAddedEventProto.getType())
                                    )
                            )
                    );
                }
                case DEVICE_REMOVED -> {
                    DeviceRemovedEventProto deviceRemovedEventProto = request.getDeviceRemoved();
                    yield new ProducerRecord<>(
                            CollectorTopics.TELEMETRY_HUBS_V1,
                            null,
                            Instant.now().toEpochMilli(),
                            request.getHubId(),
                            new HubEventAvro(
                                    request.getHubId(),
                                    Instant.ofEpochSecond(request.getTimestamp().getSeconds(),
                                            request.getTimestamp().getNanos()),
                                    new DeviceRemovedEventAvro(
                                            deviceRemovedEventProto.getId()
                                    )
                            )
                    );

                }
                case SCENARIO_ADDED -> {
                    ScenarioAddedEventProto scenarioAddedEventProto = request.getScenarioAdded();
                    yield new ProducerRecord<>(
                            CollectorTopics.TELEMETRY_HUBS_V1,
                            null,
                            Instant.now().toEpochMilli(),
                            request.getHubId(),
                            new HubEventAvro(
                                    request.getHubId(),
                                    Instant.ofEpochSecond(request.getTimestamp().getSeconds(),
                                            request.getTimestamp().getNanos()),
                                    new ScenarioAddedEventAvro(
                                            scenarioAddedEventProto.getName(),
                                            Mapper.mapToScenarioConditionAvro(scenarioAddedEventProto.getConditionList()),
                                            Mapper.mapToDeviceActionAvro(scenarioAddedEventProto.getActionList())
                                    )
                            )
                    );
                }
                case SCENARIO_REMOVED -> {
                    ScenarioRemovedEventProto scenarioRemovedEventProto = request.getScenarioRemoved();
                    yield new ProducerRecord<>(
                            CollectorTopics.TELEMETRY_HUBS_V1,
                            null,
                            Instant.now().toEpochMilli(),
                            request.getHubId(),
                            new HubEventAvro(
                                    request.getHubId(),
                                    Instant.ofEpochSecond(request.getTimestamp().getSeconds(),
                                            request.getTimestamp().getNanos()),
                                    new ScenarioRemovedEventAvro(
                                            scenarioRemovedEventProto.getName()
                                    )
                            )
                    );
                }
                case PAYLOAD_NOT_SET -> throw new IllegalArgumentException("Нет события хаба");
            };

            producer.getProducer().send(record);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.warn(e.getLocalizedMessage());
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }
}