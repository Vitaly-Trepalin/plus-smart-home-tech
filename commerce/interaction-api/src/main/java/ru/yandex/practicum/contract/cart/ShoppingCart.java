package ru.yandex.practicum.contract.cart;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    @GetMapping()
    ShoppingCartDto getShoppingCart(@RequestParam @NotNull String username);

    @PutMapping()
    ShoppingCartDto addProductToCart(@RequestParam @NotNull String username,
                                     @RequestBody @NotEmpty Map<@NotBlank String, @Positive Long> productList);

    @DeleteMapping()
    String deleteShoppingCart(@RequestParam @NotNull String username);

    @PostMapping("/remove")
    ShoppingCartDto removeProductsFromCart(@RequestParam @NotNull String username,
                                           @RequestBody @NotEmpty List<@NotBlank String> productId);

    @PostMapping("/change-quantity")
    ShoppingCartDto changeProductsFromCart(@RequestParam @NotNull String username,
                                           @RequestBody @Valid ChangeProductQuantityRequest changeProductQuantityRequest);
}