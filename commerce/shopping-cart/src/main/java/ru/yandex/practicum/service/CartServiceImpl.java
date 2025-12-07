package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.entity.ProductQuantity;
import ru.yandex.practicum.entity.ShoppingCart;
import ru.yandex.practicum.entity.ShoppingCartState;
import ru.yandex.practicum.dal.mapper.Mapper;
import ru.yandex.practicum.dal.repository.ProductQuantityRepository;
import ru.yandex.practicum.dal.repository.ShoppingCartRepository;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.exception.NoSuchCartException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {
    private final WarehouseClient warehouseClient;
    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductQuantityRepository productQuantityRepository;
    private final TransactionTemplate transactionTemplate;

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("Имя пользователя не должно быть пустым");
        }

        ShoppingCart shoppingCart = shoppingCartRepository.findByUserName(username)
                .orElseThrow(() -> new NoSuchCartException(String.format("Нет корзины пользователя c id = %s",
                        username)));

        return Mapper.mapToShoppingCartDto(shoppingCart.getShoppingCartId(), shoppingCart.getProductQuantities());
    }

    @Override
    public ShoppingCartDto addProductToCart(String username, Map<String, Long> productList) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("Имя пользователя не должно быть пустым");
        }

        warehouseClient.checkProductQuantityEnoughForShoppingCart(
                new ru.yandex.practicum.dto.warehouse.ShoppingCartDto("", productList));

        return transactionTemplate.execute((status) -> {
            ShoppingCart shoppingCart = shoppingCartRepository.findByUserName(username)
                    .orElseGet(() -> shoppingCartRepository.save(createShoppingCart(username)));

            productQuantityRepository.saveAll(Mapper.mapToProductQuantity(productList, shoppingCart));
            return new ShoppingCartDto(shoppingCart.getShoppingCartId(), productList);
        });
    }

    private boolean isFallbackResponse(BookedProductsDto bookedProductsDto) {
        if (bookedProductsDto.deliveryWeight() == -1 || bookedProductsDto.deliveryVolume() == -1) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public String deleteShoppingCart(String username) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("Имя пользователя не должно быть пустым");
        }

        ShoppingCart shoppingCart = shoppingCartRepository.findByUserName(username)
                .orElseThrow(() -> new NoSuchCartException(String.format("Нет корзины товаров пользователя c id = %s",
                        username)));

        if (shoppingCart.getState() == ShoppingCartState.DEACTIVATE) {
            throw new IllegalStateException("Статус корзины товаров уже DEACTIVATE");
        }
        shoppingCart.setState(ShoppingCartState.DEACTIVATE);
        return "OK";
    }

    @Override
    @Transactional
    public ShoppingCartDto removeProductsFromCart(String username, List<String> productId) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("Имя пользователя не должно быть пустым");
        }

        ShoppingCart shoppingCart = shoppingCartRepository.findByUserName(username)
                .orElseThrow(() -> new NoSuchCartException(String.format("Нет корзины товаров пользователя c id = %s",
                        username)));

        List<ProductQuantity> productQuantitiesInCart = shoppingCart.getProductQuantities();

        List<String> productIdsInCart = productQuantitiesInCart.stream().map(ProductQuantity::getProductId).toList();
        if (!(new HashSet<>(productIdsInCart).containsAll(productId))) {
            throw new NoProductsInShoppingCartException("Нет искомых товаров в корзине");
        }

        shoppingCart.getProductQuantities().removeAll(productQuantitiesInCart);

        return Mapper.mapToShoppingCartDto(shoppingCart.getShoppingCartId(), shoppingCart.getProductQuantities());
    }

    @Override
    @Transactional
    public ShoppingCartDto changeProductsFromCart(String username,
                                                  ChangeProductQuantityRequest changeProductQuantityRequest) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("Имя пользователя не должно быть пустым");
        }

        ShoppingCart shoppingCart = shoppingCartRepository.findByUserName(username)
                .orElseThrow(() -> new NoSuchCartException(String.format("Нет корзины товаров пользователя c id = %s",
                        username)));

        shoppingCart.getProductQuantities()
                .stream()
                .filter(productQuantity -> Objects.equals(productQuantity.getProductId(),
                        changeProductQuantityRequest.productId()))
                .findFirst()
                .orElseThrow(() -> new NoProductsInShoppingCartException(String.format("Нет искомых товаров в " +
                        "корзине c id = %s", changeProductQuantityRequest.productId())))
                .setQuantity(changeProductQuantityRequest.newQuantity());


        return Mapper.mapToShoppingCartDto(shoppingCart.getShoppingCartId(), shoppingCart.getProductQuantities());
    }

    private ShoppingCart createShoppingCart(String username) {
        return ShoppingCart.builder()
                .userName(username)
                .state(ShoppingCartState.ACTIVE)
                .build();
    }
}