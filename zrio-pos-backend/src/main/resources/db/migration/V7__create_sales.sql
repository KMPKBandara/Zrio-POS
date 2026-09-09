CREATE TABLE sales
(
    id              BIGSERIAL PRIMARY KEY,

    cashier_id      BIGINT         NOT NULL,

    status          VARCHAR(20)    NOT NULL DEFAULT 'DRAFT',

    subtotal        NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    discount_total  NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    grand_total     NUMERIC(12, 2) NOT NULL DEFAULT 0.00,

    created_at      TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at    TIMESTAMPTZ,

    CONSTRAINT fk_sales_cashier
        FOREIGN KEY (cashier_id)
        REFERENCES users (id),

    CONSTRAINT chk_sales_status
        CHECK (
            status IN (
                'DRAFT',
                'COMPLETED',
                'CANCELLED'
            )
        ),

    CONSTRAINT chk_sales_subtotal_non_negative
        CHECK (subtotal >= 0),

    CONSTRAINT chk_sales_discount_non_negative
        CHECK (discount_total >= 0),

    CONSTRAINT chk_sales_discount_not_above_subtotal
        CHECK (discount_total <= subtotal),

    CONSTRAINT chk_sales_grand_total_non_negative
        CHECK (grand_total >= 0),

    CONSTRAINT chk_sales_total_calculation
        CHECK (
            grand_total =
            subtotal - discount_total
        )
);


CREATE INDEX idx_sales_cashier_id
    ON sales (cashier_id);

CREATE INDEX idx_sales_status
    ON sales (status);

CREATE INDEX idx_sales_created_at
    ON sales (created_at);