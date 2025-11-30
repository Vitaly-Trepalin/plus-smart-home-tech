package ru.yandex.practicum.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NoProductsInShoppingCartException extends RuntimeException {
    HttpStatus httpStatus = HttpStatus.NOT_FOUND;
    String userMessage = "Нет искомых товаров в корзине";

    public NoProductsInShoppingCartException(String message) {
        super(message);
    }
}