package ru.yandex.practicum.dal.mapper;

import ru.yandex.practicum.dal.entity.ProductQuantity;
import ru.yandex.practicum.dal.entity.ShoppingCart;
import ru.yandex.practicum.dto.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Mapper {

    public static List<ProductQuantity> mapToProductQuantity(Map<String, Long> productList,
                                                             ShoppingCart shoppingCart) {
        return productList.entrySet().stream()
                .map(x -> ProductQuantity.builder()
                        .productId(x.getKey())
                        .quantity(x.getValue())
                        .shoppingCart(shoppingCart)
                        .build())
                .toList();
    }

    public static ShoppingCartDto mapToShoppingCartDto(String shoppingCartId, List<ProductQuantity> productQuantityList) {
        Map<String, Long> products = productQuantityList.stream()
                .collect(Collectors.toMap(ProductQuantity::getProductId, ProductQuantity::getQuantity));
        return new ShoppingCartDto(shoppingCartId, products);
    }
}