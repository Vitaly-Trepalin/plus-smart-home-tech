package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.PageDto;
import ru.yandex.practicum.dto.Pageable;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.dto.SortOrder;
import ru.yandex.practicum.dto.UpdateProductDto;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.mapper.Mapper;
import ru.yandex.practicum.entity.Product;
import ru.yandex.practicum.entity.ProductCategory;
import ru.yandex.practicum.entity.ProductState;
import ru.yandex.practicum.repository.StoreRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreServiceImpl implements StoreService {
    private final StoreRepository storeRepository;

    @Override
    public PageDto getAllByType(ProductCategory category, Pageable customPageable) {
        if (customPageable.page() == null || customPageable.size() == null) {
            List<Product> products = storeRepository.getAllByProductCategory(category);
            List<ProductDto> productDtos = products.stream().map(Mapper::mapToProductDto).toList();
            return new PageDto(productDtos, List.of());
        }

        org.springframework.data.domain.Pageable pageable;
        if (customPageable.sort() == null) {
            pageable = PageRequest.of(customPageable.page(), customPageable.size());
            List<Product> products = storeRepository.getAllByProductCategory(category, pageable);
            List<ProductDto> productDtos = products.stream().map(Mapper::mapToProductDto).toList();
            return new PageDto(productDtos, List.of());
        } else {
            String fieldName = customPageable.sort()[0];
            String sortingDirection = customPageable.sort()[1];

            Sort sort = Objects.equals(sortingDirection, "ASC") ? Sort.by(Sort.Direction.ASC, fieldName) :
                    Sort.by(Sort.Direction.DESC, fieldName);
            pageable = PageRequest.of(customPageable.page(), customPageable.size(), sort);

            List<Product> products = storeRepository.getAllByProductCategory(category, pageable);
            List<ProductDto> productDtos = products.stream().map(Mapper::mapToProductDto).toList();
            return new PageDto(productDtos, List.of(new SortOrder(fieldName, sortingDirection)));
        }
    }

    @Override
    @Transactional
    public ProductDto addProduct(ProductDto productDto) {
        Product product = storeRepository.save(Mapper.mapToProduct(productDto));
        return Mapper.mapToProductDto(product);
    }

    @Override
    @Transactional
    public ProductDto updateProduct(UpdateProductDto productDto) {
        Product product = storeRepository.findById(productDto.productId())
                .orElseThrow(() -> new ProductNotFoundException(String.format("\"Нет товара c id = %s", productDto)));

        Optional.ofNullable(productDto.productName()).filter(x -> !x.isBlank()).ifPresent(product::setProductName);
        Optional.ofNullable(productDto.description()).filter(x -> !x.isBlank()).ifPresent(product::setDescription);
        Optional.ofNullable(productDto.imageSrc()).filter(x -> !x.isBlank()).ifPresent(product::setImageSrc);
        Optional.ofNullable(productDto.quantityState()).ifPresent(product::setQuantityState);
        Optional.ofNullable(productDto.productState()).ifPresent(product::setProductState);
        Optional.ofNullable(productDto.productCategory()).ifPresent(product::setProductCategory);
        Optional.ofNullable(productDto.price()).ifPresent(product::setPrice);

        return Mapper.mapToProductDto(product);
    }

    @Override
    @Transactional
    public String removeProduct(String productId) {
        String id = productId.replaceAll("\"", "");
        Product product = storeRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(String.format("Нет товара c id = %s", productId)));

        if (product.getProductState() == ProductState.DEACTIVATE) {
            return "false";
        }
        product.setProductState(ProductState.DEACTIVATE);
        return "true";
    }

    @Override
    @Transactional
    public String setQuantityState(SetProductQuantityStateRequest stateRequest) {
        Product product = storeRepository.findById(stateRequest.productId())
                .orElseThrow(() -> new ProductNotFoundException(String.format("Нет товара c id = %s",
                        stateRequest.productId())));

        if (product.getQuantityState() == stateRequest.quantityState()) {
            return "false";
        }
        product.setQuantityState(stateRequest.quantityState());
        return "true";
    }

    @Override
    public ProductDto getProduct(String productId) {
        Product product = storeRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(String.format("Нет товара c id = %s", productId)));

        return Mapper.mapToProductDto(product);
    }
}