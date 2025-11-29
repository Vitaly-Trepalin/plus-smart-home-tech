CREATE TABLE IF NOT EXISTS shopping_cart (
    shopping_cart_id VARCHAR(255) PRIMARY KEY,
    user_name        VARCHAR(255) NOT NULL UNIQUE,
    state            VARCHAR(20)  NOT NULL
);

CREATE TABLE IF NOT EXISTS product_quantity (
    id               BIGSERIAL    PRIMARY KEY,
    product_id       VARCHAR(255) NOT NULL,
    quantity         BIGINT       NOT NULL,
    shopping_cart_id VARCHAR(255) NOT NULL REFERENCES shopping_cart(shopping_cart_id) ON DELETE CASCADE
);