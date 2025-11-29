package ru.yandex.practicum.dal.mapper;

import ru.yandex.practicum.dal.entity.Product;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.NewProductInWarehouseRequest;

public class Mapper {
    public static Product mapToProduct(NewProductInWarehouseRequest newProductInWarehouseRequest) {
        return Product.builder()
                .productId(newProductInWarehouseRequest.productId())
                .fragile(newProductInWarehouseRequest.fragile())
                .width(newProductInWarehouseRequest.dimension().width())
                .height(newProductInWarehouseRequest.dimension().height())
                .depth(newProductInWarehouseRequest.dimension().depth())
                .weight(newProductInWarehouseRequest.weight())
                .quantity(0)
                .build();
    }

    public static BookedProductsDto mapToBookedProductsDto(double deliveryWeight, double deliveryVolume,
                                                           boolean fragile) {

        return new BookedProductsDto(deliveryWeight, deliveryVolume, fragile);
    }
}