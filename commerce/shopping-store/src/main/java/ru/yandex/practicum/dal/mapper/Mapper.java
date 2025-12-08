package ru.yandex.practicum.dal.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.yandex.practicum.dto.store.Pageable;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.entity.Product;

public class Mapper {
    public static Product mapToProduct(ProductDto productDto) {
        return Product.builder()
                .productId(productDto.productId())
                .productName(productDto.productName())
                .description(productDto.description())
                .imageSrc(productDto.imageSrc())
                .quantityState(productDto.quantityState())
                .productState(productDto.productState())
                .productCategory(productDto.productCategory())
                .price(productDto.price())
                .build();
    }

    public static ProductDto mapToProductDto(Product product) {
        return ProductDto.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .description(product.getDescription())
                .imageSrc(product.getImageSrc())
                .quantityState(product.getQuantityState())
                .productState(product.getProductState())
                .productCategory(product.getProductCategory())
                .price(product.getPrice())
                .build();
    }

    public static Pageable mapToPageable(String pageable) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(pageable, Pageable.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}