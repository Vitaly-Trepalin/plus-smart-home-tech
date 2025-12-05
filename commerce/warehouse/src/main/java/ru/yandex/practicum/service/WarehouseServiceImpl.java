package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.entity.Product;
import ru.yandex.practicum.dal.mapper.Mapper;
import ru.yandex.practicum.dal.repository.ProductRepository;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.ProductQuantityDto;
import ru.yandex.practicum.dto.warehouse.ShoppingCartDto;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseServiceImpl implements WarehouseService {
    private final ProductRepository productRepository;
    private static final String[] ADDRESSES = new String[]{"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];

    @Override
    @Transactional
    public void addProduct(NewProductInWarehouseRequest newProductInWarehouseRequest) {
        if (productRepository.existsById(newProductInWarehouseRequest.productId())) {
            throw new SpecifiedProductAlreadyInWarehouseException("Ошибка, товар с таким описанием уже " +
                    "зарегистрирован на складе");
        }

        Product product = Mapper.mapToProduct(newProductInWarehouseRequest);
        productRepository.save(product);
    }

    @Override
    public BookedProductsDto sufficiencyCheck(ShoppingCartDto shoppingCartDto) {
        Map<String, Long> productsInCars = shoppingCartDto.products();
        List<Product> productList = productRepository.findAllById(productsInCars.keySet());

        Set<String> productIds = productList.stream().map(Product::getProductId).collect(Collectors.toSet());
        if (!productIds.containsAll(shoppingCartDto.products().keySet())) {
            throw new NoSpecifiedProductInWarehouseException("На складе нет информации о некоторых товарах");
        }

        double deliveryVolume = 0;
        double deliveryWeight = 0;
        boolean fragile = false;
        List<String> productInShoppingCartLowQuantityInWarehouse = new ArrayList<>();
        for (Product product : productList) {
            long quantity = productsInCars.get(product.getProductId());
            if (product.getQuantity() < quantity) {
                productInShoppingCartLowQuantityInWarehouse.add(product.getProductId());
            }

            deliveryVolume += (product.getWidth() * product.getHeight() * product.getDepth()) * quantity;
            deliveryWeight += product.getWeight() * quantity;
            fragile = product.isFragile() ? true : fragile;
        }

        if (!productInShoppingCartLowQuantityInWarehouse.isEmpty()) {
            throw new ProductInShoppingCartLowQuantityInWarehouse
                    (String.format("Ошибка, эти товары из корзины не находится в требуемом количестве на складе: %s",
                            productInShoppingCartLowQuantityInWarehouse));
        }

        return Mapper.mapToBookedProductsDto(deliveryVolume, deliveryWeight, fragile);
    }

    @Override
    @Transactional
    public void addProductToWarehouse(ProductQuantityDto productQuantityDto) {
        Product product = productRepository.findById(productQuantityDto.productId())
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException(
                        String.format("На складе нет информации о товаре с id = %s", productQuantityDto.productId())));
        long quantity = product.getQuantity();
        product.setQuantity(quantity + productQuantityDto.quantity());
    }

    @Override
    public AddressDto getAddress() {
        return new AddressDto(CURRENT_ADDRESS, CURRENT_ADDRESS, CURRENT_ADDRESS, CURRENT_ADDRESS, CURRENT_ADDRESS);
    }
}