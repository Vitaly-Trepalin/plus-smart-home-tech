package ru.yandex.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.dto.store.ProductCategory;
import ru.yandex.practicum.entity.Product;

import java.util.List;

public interface StoreRepository extends JpaRepository<Product, String> {
    List<Product> getAllByProductCategory(ProductCategory productCategory);

    List<Product> getAllByProductCategory(ProductCategory productCategory, Pageable pageable);
}