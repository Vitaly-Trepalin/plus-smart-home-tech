package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;

import java.util.List;
import java.util.Map;

public interface CartService {
    ShoppingCartDto getShoppingCart(String username);

    ShoppingCartDto addProductToCart(String username, Map<String, Long> productList);

    String deleteShoppingCart(String username);

    ShoppingCartDto removeProductsFromCart(String username, List<String> productId);

    ShoppingCartDto changeProductsFromCart(String username, ChangeProductQuantityRequest changeProductQuantityRequest);
}