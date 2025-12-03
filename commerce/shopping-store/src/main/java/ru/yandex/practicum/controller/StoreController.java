package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.yandex.practicum.http.ShoppingStore;
import ru.yandex.practicum.service.StoreService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/shopping-store")
public class StoreController implements ShoppingStore {
    private final StoreService service;

    @GetMapping
    public PageDto getAllByType(@RequestParam ProductCategory category,
                                @ModelAttribute Pageable pageable) {
        log.info("Запущен метод getAllByType(ProductCategory category = {}, String pageable = {})", category, pageable);
        return service.getAllByType(category, pageable);
    }

    @PutMapping
    public ProductDto addProduct(@RequestBody @Valid ProductDto productDto) {
        log.info("Запущен метод addProduct(ProductDto productDto = {})", productDto);
        return service.addProduct(productDto);
    }

    @PostMapping
    public ProductDto updateProduct(@RequestBody @Valid UpdateProductDto productDto) {
        log.info("Запущен метод updateProduct(UpdateProductDto productDto = {})", productDto);
        return service.updateProduct(productDto);
    }

    @PostMapping("/removeProductFromStore")
    public String removeProduct(@RequestBody @NotBlank String productId) {
        log.info("Запущен метод removeProduct(String productId = {})", productId);
        return service.removeProduct(productId);
    }

    @PostMapping("/quantityState")
    public String setQuantityState(@ModelAttribute @Valid SetProductQuantityStateRequest stateRequest) {
        log.info("Запущен метод setQuantityState(SetProductQuantityStateRequest stateRequest = {})", stateRequest);
        return service.setQuantityState(stateRequest);
    }

    @GetMapping("/{productId}")
    public ProductDto getProduct(@PathVariable @NotBlank String productId) {
        log.info("Запущен метод getProduct(String productId)  = {})", productId);
        return service.getProduct(productId);
    }
}