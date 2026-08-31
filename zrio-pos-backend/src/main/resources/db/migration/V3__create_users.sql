CREATE TABLE users
(
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(120) NOT NULL,
    role          VARCHAR(20)  NOT NULL,
    active        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_users_role
        CHECK (role IN ('OWNER', 'CASHIER')),

    CONSTRAINT chk_users_username_not_blank
        CHECK (BTRIM(username) <> ''),

    CONSTRAINT chk_users_full_name_not_blank
        CHECK (BTRIM(full_name) <> '')
);

CREATE UNIQUE INDEX uq_users_username_lower
    ON users (LOWER(username));