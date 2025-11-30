package ru.yandex.practicum.http;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.dto.store.PageDto;
import ru.yandex.practicum.dto.store.Pageable;
import ru.yandex.practicum.dto.store.ProductCategory;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.dto.store.UpdateProductDto;

public interface ShoppingStore {
    @GetMapping("/api/v1/shopping-store")
    PageDto getAllByType(@RequestParam ProductCategory category,
                         @ModelAttribute Pageable pageable);

    @PutMapping("/api/v1/shopping-store")
    ProductDto addProduct(@RequestBody @Valid ProductDto productDto);

    @PostMapping("/api/v1/shopping-store")
    ProductDto updateProduct(@RequestBody @Valid UpdateProductDto productDto);

    @PostMapping("/api/v1/shopping-store/removeProductFromStore")
    String removeProduct(@RequestBody @NotBlank String productId);

    @PostMapping("/api/v1/shopping-store/quantityState")
    String setQuantityState(@ModelAttribute @Valid SetProductQuantityStateRequest stateRequest);

    @GetMapping("/api/v1/shopping-store/{productId}")
    ProductDto getProduct(@PathVariable @NotBlank String productId);
}