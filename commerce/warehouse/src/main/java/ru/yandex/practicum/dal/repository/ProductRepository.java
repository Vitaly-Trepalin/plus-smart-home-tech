package ru.yandex.practicum.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.dal.entity.Product;

public interface ProductRepository extends JpaRepository<Product, String> {
}