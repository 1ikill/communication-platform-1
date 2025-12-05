CREATE TABLE IF NOT EXISTS user_service.users
(
    id                 BIGSERIAL PRIMARY KEY,
    email              TEXT NOT NULL UNIQUE,
    username           TEXT NOT NULL UNIQUE,
    password_hash      TEXT NOT NULL,
    full_name          TEXT,
    role               TEXT        DEFAULT 'USER',
    created_date       TIMESTAMP NOT NULL,
    last_modified_date TIMESTAMP NOT NULL
);

ALTER TABLE user_service.users
    ADD CONSTRAINT chk_role
        CHECK (users.role IN ('USER', 'ADMIN'));