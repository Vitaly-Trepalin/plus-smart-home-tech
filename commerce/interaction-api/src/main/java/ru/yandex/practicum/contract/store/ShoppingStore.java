package ru.yandex.practicum.contract.store;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @GetMapping
    PageDto getAllByType(@RequestParam @NotNull ProductCategory category,
                         @ModelAttribute Pageable pageable);

    @PutMapping
    ProductDto addProduct(@RequestBody @Valid ProductDto productDto);

    @PostMapping
    ProductDto updateProduct(@RequestBody @Valid UpdateProductDto productDto);

    @PostMapping("/removeProductFromStore")
    String removeProduct(@RequestBody @NotBlank String productId);

    @PostMapping("/quantityState")
    String setQuantityState(@ModelAttribute @Valid SetProductQuantityStateRequest stateRequest);

    @GetMapping("/{productId}")
    ProductDto getProduct(@PathVariable @NotBlank String productId);
}