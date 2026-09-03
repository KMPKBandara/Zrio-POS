CREATE TABLE categories
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_categories_name_not_blank
        CHECK (BTRIM(name) <> '')
);

CREATE UNIQUE INDEX uq_categories_name_lower
    ON categories (LOWER(name));