CREATE SCHEMA IF NOT EXISTS warehouse_schema;

CREATE TABLE IF NOT EXISTS warehouse_schema.products (
    product_id   VARCHAR(255) PRIMARY KEY,
    fragile      BOOLEAN     NOT NULL,
    width        NUMERIC     NOT NULL,
    height       NUMERIC     NOT NULL,
    depth        NUMERIC     NOT NULL,
    weight       NUMERIC     NOT NULL,
    quantity     BIGINT      NOT NULL
);