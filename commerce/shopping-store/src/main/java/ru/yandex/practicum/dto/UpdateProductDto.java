package ru.yandex.practicum.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import ru.yandex.practicum.entity.ProductCategory;
import ru.yandex.practicum.entity.ProductState;
import ru.yandex.practicum.entity.QuantityState;

public record UpdateProductDto (
    @Size(max = 255, message = "Идентификатор товара превысил 255 символов")
    String productId,
    @Size(max = 255, message = "Наименование товара превысило 255 символов")
    String productName,
    @Size(max = 1000, message = "Описание превысило 1000 символов")
    String description,
    String imageSrc,
    QuantityState quantityState,
    ProductState productState,
    ProductCategory productCategory,
    @DecimalMin(value = "1.0", inclusive = false, message = "Цена товара не может быть меньше 1")
    @Positive(message = "Цена товара не может быть отрицательной")
    Double price
) {}