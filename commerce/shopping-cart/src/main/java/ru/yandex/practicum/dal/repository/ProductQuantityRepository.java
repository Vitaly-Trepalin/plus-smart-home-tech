package ru.yandex.practicum.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.entity.ProductQuantity;

public interface ProductQuantityRepository extends JpaRepository<ProductQuantity, Long> {
}