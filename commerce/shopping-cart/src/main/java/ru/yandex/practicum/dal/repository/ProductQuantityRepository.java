package ru.yandex.practicum.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.dal.entity.ProductQuantity;
import ru.yandex.practicum.dal.entity.ShoppingCart;

import java.util.List;

public interface ProductQuantityRepository extends JpaRepository<ProductQuantity, Long> {
    List<ProductQuantity> findAllByProductIdIn(List<String> productIds);

    List<ProductQuantity> findAllByShoppingCart(ShoppingCart shoppingCart);
}