CREATE TABLE products
(
    id                  BIGSERIAL PRIMARY KEY,

    category_id         BIGINT         NOT NULL,

    item_code           VARCHAR(50)    NOT NULL,
    name                VARCHAR(150)   NOT NULL,
    barcode             VARCHAR(100),

    cost_price          NUMERIC(12, 2) NOT NULL,
    selling_price       NUMERIC(12, 2) NOT NULL,

    low_stock_threshold INTEGER        NOT NULL DEFAULT 5,

    image_path          VARCHAR(500),

    active              BOOLEAN        NOT NULL DEFAULT TRUE,

    created_at          TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_products_category
        FOREIGN KEY (category_id)
        REFERENCES categories (id),

    CONSTRAINT chk_products_item_code_not_blank
        CHECK (BTRIM(item_code) <> ''),

    CONSTRAINT chk_products_name_not_blank
        CHECK (BTRIM(name) <> ''),

    CONSTRAINT chk_products_cost_price
        CHECK (cost_price >= 0),

    CONSTRAINT chk_products_selling_price
        CHECK (selling_price >= 0),

    CONSTRAINT chk_products_low_stock_threshold
        CHECK (low_stock_threshold >= 0)
);

CREATE UNIQUE INDEX uq_products_item_code_lower
    ON products (LOWER(item_code));

CREATE UNIQUE INDEX uq_products_barcode
    ON products (barcode)
    WHERE barcode IS NOT NULL;

CREATE INDEX idx_products_category_id
    ON products (category_id);