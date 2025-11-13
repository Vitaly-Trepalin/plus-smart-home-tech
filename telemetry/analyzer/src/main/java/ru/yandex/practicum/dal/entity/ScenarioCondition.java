package ru.yandex.practicum.dal.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "scenario_conditions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ScenarioCondition {
    @EmbeddedId
    private ScenarioConditionId id;

    @ManyToOne
    @JoinColumn(name = "scenario_id")
    @MapsId(value = "scenarioId")
    @ToString.Exclude
    private Scenario scenario;

    @ManyToOne
    @JoinColumn(name = "sensor_id")
    @MapsId(value = "sensorId")
    @ToString.Exclude
    private Sensor sensor;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "condition_id")
    @MapsId(value = "conditionId")
    @ToString.Exclude
    private Condition condition;
}