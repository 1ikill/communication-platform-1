CREATE TABLE whatsapp_service.whatsapp_credentials
(
    id                   BIGSERIAL PRIMARY KEY,
    user_id              BIGINT    NOT NULL,
    display_name         TEXT,
    waba_id              TEXT      NOT NULL,
    phone_number_id      TEXT      NOT NULL,
    access_token         TEXT      NOT NULL,
    webhook_verify_token TEXT      NOT NULL,
    is_active            BOOLEAN   NOT NULL DEFAULT TRUE,
    deleted              BOOLEAN   NOT NULL DEFAULT FALSE,
    created_date         TIMESTAMP NOT NULL,
    last_modified_date   TIMESTAMP NOT NULL
);

ALTER TABLE whatsapp_service.whatsapp_credentials
    ADD CONSTRAINT uq_user_phone_number_id UNIQUE (user_id, phone_number_id);

ALTER TABLE whatsapp_service.whatsapp_credentials
    ADD CONSTRAINT fk_whatsapp_credentials_user_id FOREIGN KEY (user_id)
        REFERENCES user_service.users(id) ON DELETE CASCADE;

CREATE INDEX idx_whatsapp_credentials_user_id
    ON whatsapp_service.whatsapp_credentials (user_id);