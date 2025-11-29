package ru.yandex.practicum.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import ru.yandex.practicum.entity.ProductCategory;
import ru.yandex.practicum.entity.ProductState;
import ru.yandex.practicum.entity.QuantityState;

@Builder
public record ProductDto(
        @Size(max = 255, message = "Идентификатор товара превысил 255 символов")
        String productId,
        @NotBlank(message = "Отсутствует наименование товара")
        @Size(max = 255, message = "Наименование товара превысило 255 символов")
        String productName,
        @NotBlank(message = "Отсутствует описание товара")
        @Size(max = 1000, message = "Описание превысило 1000 символов")
        String description,
        String imageSrc,
        @NotNull(message = "Отсутствует статус, перечисляющий состояние остатка как свойства товара")
        QuantityState quantityState,
        @NotNull(message = "Отсутствует статус товара")
        ProductState productState,
        ProductCategory productCategory,
        @NotNull(message = "Отсутствует цена товара")
        @DecimalMin(value = "1.0", inclusive = false, message = "Цена товара не может быть меньше 1")
        @Positive(message = "Цена товара не может быть отрицательной")
        Double price) {
}