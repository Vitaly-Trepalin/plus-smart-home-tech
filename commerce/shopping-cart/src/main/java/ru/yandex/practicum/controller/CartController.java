package ru.yandex.practicum.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.service.CartService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/shopping-cart")
public class CartController {
    private final CartService service;

    @GetMapping
    public ShoppingCartDto getShoppingCart(@RequestParam String username) {
        log.info("Запущен метод getShoppingCart(String username {})", username);
        return service.getShoppingCart(username);
    }

    @PutMapping
    public ShoppingCartDto addProductToCart(@RequestParam String username,
                                            @RequestBody Map<String, Long> productList) {
        log.info("Запущен метод addProductToCart(String username = {}," +
                "List<ChangeProductQuantityRequest> productList)", username);
        return service.addProductToCart(username, productList);
    }

    @DeleteMapping
    public String deleteShoppingCart(@RequestParam String username) {
        log.info("Запущен метод deleteShoppingCart(String username {})", username);
        return service.deleteShoppingCart(username);
    }

    @PostMapping("/remove")
    public ShoppingCartDto removeProductsFromCart(@RequestParam String username,
                                                  @RequestBody @NotNull List<String> productId) {
        log.info("Запущен метод removeProductsFromCart(String username = {},List<String> productId = {})",
                username, productId);
        return service.removeProductsFromCart(username, productId);
    }

    @PostMapping("/change-quantity")
    public ShoppingCartDto changeProductsFromCart(@RequestParam String username,
                                                  @RequestBody @NotNull ChangeProductQuantityRequest changeProductQuantityRequest) {
        log.info("Запущен метод changeProductsFromCart(String username = {}," +
                        "ChangeProductQuantityRequest changeProductQuantityRequest = {})", username,
                changeProductQuantityRequest);
        return service.changeProductsFromCart(username, changeProductQuantityRequest);
    }
}