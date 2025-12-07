package ru.yandex.practicum.contract.warehouse;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.ProductQuantityDto;
import ru.yandex.practicum.dto.warehouse.ShoppingCartDto;

public interface Warehouse {
    @PutMapping
    void addProduct(@RequestBody @Valid NewProductInWarehouseRequest newProductInWarehouseRequest);

    @PostMapping("/check")
    BookedProductsDto checkProductQuantityEnoughForShoppingCart(@RequestBody @Valid ShoppingCartDto shoppingCartDto);

    @PostMapping("/add")
    void addProductToWarehouse(@RequestBody @Valid ProductQuantityDto productQuantityDto);

    @GetMapping("/address")
    AddressDto getAddress();
}