CREATE TABLE shop_settings
(
    id            SMALLINT PRIMARY KEY DEFAULT 1,
    shop_name     VARCHAR(150) NOT NULL,
    address       VARCHAR(300),
    phone         VARCHAR(30),
    currency_code VARCHAR(3)   NOT NULL DEFAULT 'LKR',
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_shop_settings_single_row
        CHECK (id = 1)
);