CREATE TABLE sale_items
(
    id                    BIGSERIAL PRIMARY KEY,

    sale_id               BIGINT         NOT NULL,
    product_id            BIGINT         NOT NULL,

    item_code_snapshot    VARCHAR(50)    NOT NULL,
    product_name_snapshot VARCHAR(150)   NOT NULL,

    unit_price            NUMERIC(12, 2) NOT NULL,
    quantity              INTEGER        NOT NULL,
    line_total            NUMERIC(12, 2) NOT NULL,

    CONSTRAINT fk_sale_items_sale
        FOREIGN KEY (sale_id)
        REFERENCES sales (id),

    CONSTRAINT fk_sale_items_product
        FOREIGN KEY (product_id)
        REFERENCES products (id),

    CONSTRAINT uq_sale_items_sale_product
        UNIQUE (sale_id, product_id),

    CONSTRAINT chk_sale_items_item_code_not_blank
        CHECK (BTRIM(item_code_snapshot) <> ''),

    CONSTRAINT chk_sale_items_product_name_not_blank
        CHECK (BTRIM(product_name_snapshot) <> ''),

    CONSTRAINT chk_sale_items_unit_price_non_negative
        CHECK (unit_price >= 0),

    CONSTRAINT chk_sale_items_quantity_positive
        CHECK (quantity > 0),

    CONSTRAINT chk_sale_items_line_total_non_negative
        CHECK (line_total >= 0),

    CONSTRAINT chk_sale_items_line_total_calculation
        CHECK (
            line_total = unit_price * quantity
        )
);


CREATE INDEX idx_sale_items_product_id
    ON sale_items (product_id);