CREATE TABLE IF NOT EXISTS telegram_service.telegram_credentials
(
    id                 BIGSERIAL PRIMARY KEY,
    api_id             TEXT   NOT NULL,
    api_hash           TEXT   NOT NULL,
    account_id         TEXT   NOT NULL,
    account_name       TEXT   NOT NULL,
    phone_number       TEXT   NOT NULL,
    user_id            BIGINT NOT NULL,
    created_date       TIMESTAMP NOT NULL,
    last_modified_date TIMESTAMP NOT NULL
);

ALTER table telegram_service.telegram_credentials
    ADD CONSTRAINT fk_telegram_credential_user_id FOREIGN KEY (user_id)
        REFERENCES user_service.users(id) ON DELETE CASCADE;

CREATE INDEX idx_telegram_credentials_user_id
    ON telegram_service.telegram_credentials (user_id);