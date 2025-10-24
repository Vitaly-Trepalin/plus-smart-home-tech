package ru.yandex.practicum.model.hub;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioAddedEvent extends HubEvent {
    @NotBlank(message = "Название события не должно быть null или пустым")
    @Size(max = 2147483647, min = 3, message = "Название события должно быть в диапазоне от 3 " +
            "до 2147483647 символов")
    private String name;
    @NotNull(message = "Список условий, которые связаны со сценарием. Не может быть пустым.")
    private List<ScenarioCondition> conditions;
    @NotNull(message = "Список действий, которые должны быть выполнены в рамках сценария. Не может быть пустым.")
    private List<DeviceAction> action;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}