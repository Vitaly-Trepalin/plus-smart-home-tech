CREATE SCHEMA IF NOT EXISTS store_schema;

CREATE TABLE IF NOT EXISTS store_schema.products (
    product_id       VARCHAR(255)  PRIMARY KEY,
    product_name     VARCHAR(255)  NOT NULL,
    description      VARCHAR(1000) NOT NULL,
    image_src        VARCHAR(1000),
    quantity_state   VARCHAR(20)   NOT NULL,
    product_state    VARCHAR(20)   NOT NULL,
    product_category VARCHAR(20),
    price            NUMERIC       NOT NULL
);