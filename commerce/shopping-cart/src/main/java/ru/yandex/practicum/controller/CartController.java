package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.api.ShoppingCart;
import ru.yandex.practicum.service.CartService;
import ru.yandex.practicum.util.Loggable;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/api/v1/shopping-cart")
public class CartController implements ShoppingCart {
    private final CartService service;

    @Override
    @GetMapping
    @Loggable
    public ShoppingCartDto getShoppingCart(@RequestParam @NotNull String username) {
        return service.getShoppingCart(username);
    }

    @Override
    @PutMapping
    @Loggable
    public ShoppingCartDto addProductToCart(@RequestParam @NotNull String username,
                                            @RequestBody Map<String, Long> productList) {
        return service.addProductToCart(username, productList);
    }

    @Override
    @DeleteMapping
    @Loggable
    public String deleteShoppingCart(@RequestParam @NotNull String username) {
        return service.deleteShoppingCart(username);
    }

    @Override
    @PostMapping("/remove")
    @Loggable
    public ShoppingCartDto removeProductsFromCart(@RequestParam @NotNull String username,
                                                  @RequestBody List<String> productId) {
        return service.removeProductsFromCart(username, productId);
    }

    @Override
    @PostMapping("/change-quantity")
    @Loggable
    public ShoppingCartDto changeProductsFromCart(@RequestParam @NotNull String username,
                                                  @RequestBody @Valid
                                                  ChangeProductQuantityRequest changeProductQuantityRequest) {
        return service.changeProductsFromCart(username, changeProductQuantityRequest);
    }
}