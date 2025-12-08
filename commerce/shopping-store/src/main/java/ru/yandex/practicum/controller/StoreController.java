package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.store.PageDto;
import ru.yandex.practicum.dto.store.Pageable;
import ru.yandex.practicum.dto.store.ProductCategory;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.dto.store.UpdateProductDto;
import ru.yandex.practicum.contract.store.ShoppingStore;
import ru.yandex.practicum.service.StoreService;
import ru.yandex.practicum.util.Loggable;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/shopping-store")
public class StoreController implements ShoppingStore {
    private final StoreService service;

    @Override
    @GetMapping
    @Loggable
    public PageDto getAllByType(@RequestParam @NotNull ProductCategory category,
                                @ModelAttribute Pageable pageable) {
        return service.getAllByType(category, pageable);
    }

    @Override
    @PutMapping
    @Loggable
    public ProductDto addProduct(@RequestBody @Valid ProductDto productDto) {
        return service.addProduct(productDto);
    }

    @Override
    @PostMapping
    @Loggable
    public ProductDto updateProduct(@RequestBody @Valid UpdateProductDto productDto) {
        return service.updateProduct(productDto);
    }

    @Override
    @PostMapping("/removeProductFromStore")
    @Loggable
    public String removeProduct(@RequestBody @NotBlank String productId) {
        return service.removeProduct(productId);
    }

    @Override
    @PostMapping("/quantityState")
    @Loggable
    public String setQuantityState(@ModelAttribute @Valid SetProductQuantityStateRequest stateRequest) {
        return service.setQuantityState(stateRequest);
    }

    @Override
    @GetMapping("/{productId}")
    @Loggable
    public ProductDto getProduct(@PathVariable @NotBlank String productId) {
        return service.getProduct(productId);
    }
}