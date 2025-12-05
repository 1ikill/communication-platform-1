CREATE TABLE IF NOT EXISTS gmail_service.gmail_credentials (
                                id BIGSERIAL PRIMARY KEY,
                                user_id BIGINT,
                                email_address TEXT,
                                client_id TEXT NOT NULL,
                                client_secret TEXT NOT NULL,
                                refresh_token TEXT,
                                access_token TEXT,
                                token_expiry TIMESTAMP,
                                scopes TEXT,
                                created_date TIMESTAMP NOT NULL,
                                last_modified_date TIMESTAMP NOT NULL
);

ALTER table gmail_service.gmail_credentials
    ADD CONSTRAINT fk_telegram_credential_user_id FOREIGN KEY (user_id)
        REFERENCES user_service.users(id) ON DELETE CASCADE;

CREATE INDEX idx_gmail_credentials_user_id
    ON gmail_service.gmail_credentials (user_id);
