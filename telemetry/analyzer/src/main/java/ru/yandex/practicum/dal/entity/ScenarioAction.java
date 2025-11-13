package ru.yandex.practicum.dal.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "scenario_actions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ScenarioAction {

    @EmbeddedId
    private ScenarioActionId scenarioActionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scenario_id")
    @MapsId(value = "scenarioId")
    @ToString.Exclude
    private Scenario scenario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id")
    @MapsId(value = "sensorId")
    @ToString.Exclude
    private Sensor sensor;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "action_id")
    @MapsId(value = "actionId")
    @ToString.Exclude
    private Action action;
}