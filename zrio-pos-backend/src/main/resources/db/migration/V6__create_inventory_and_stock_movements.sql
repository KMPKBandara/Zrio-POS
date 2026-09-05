CREATE TABLE inventory
(
    product_id       BIGINT      PRIMARY KEY,
    quantity_on_hand INTEGER     NOT NULL DEFAULT 0,
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_inventory_product
        FOREIGN KEY (product_id)
        REFERENCES products (id),

    CONSTRAINT chk_inventory_quantity_non_negative
        CHECK (quantity_on_hand >= 0)
);


CREATE TABLE stock_movements
(
    id                BIGSERIAL PRIMARY KEY,
    product_id        BIGINT        NOT NULL,
    movement_type     VARCHAR(30)   NOT NULL,
    quantity_change   INTEGER       NOT NULL,
    quantity_after    INTEGER       NOT NULL,
    note              VARCHAR(255),
    created_by        BIGINT,
    created_at        TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_stock_movements_product
        FOREIGN KEY (product_id)
        REFERENCES products (id),

    CONSTRAINT fk_stock_movements_user
        FOREIGN KEY (created_by)
        REFERENCES users (id),

    CONSTRAINT chk_stock_movement_type
        CHECK (
            movement_type IN (
                'STOCK_IN',
                'ADJUSTMENT_IN',
                'ADJUSTMENT_OUT',
                'SALE'
            )
        ),

    CONSTRAINT chk_stock_movement_change_not_zero
        CHECK (quantity_change <> 0),

    CONSTRAINT chk_stock_movement_quantity_after
        CHECK (quantity_after >= 0)
);


CREATE INDEX idx_stock_movements_product_id
    ON stock_movements (product_id);

CREATE INDEX idx_stock_movements_created_at
    ON stock_movements (created_at);


INSERT INTO inventory (
    product_id,
    quantity_on_hand
)
SELECT
    id,
    0
FROM products;