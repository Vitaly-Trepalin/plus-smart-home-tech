package ru.yandex.practicum.http;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;

import java.util.List;
import java.util.Map;

public interface ShoppingCart {
    @GetMapping("/api/v1/shopping-cart")
    ShoppingCartDto getShoppingCart(@RequestParam String username);

    @PutMapping("/api/v1/shopping-cart")
    ShoppingCartDto addProductToCart(@RequestParam String username, @RequestBody Map<String, Long> productList);

    @DeleteMapping("/api/v1/shopping-cart")
    String deleteShoppingCart(@RequestParam String username);

    @PostMapping("/api/v1/shopping-cart/remove")
    ShoppingCartDto removeProductsFromCart(@RequestParam String username, @RequestBody @NotNull List<String> productId);

    @PostMapping("/api/v1/shopping-cart/change-quantity")
    ShoppingCartDto changeProductsFromCart(@RequestParam String username,
                                           @RequestBody @NotNull
                                           ChangeProductQuantityRequest changeProductQuantityRequest);
}