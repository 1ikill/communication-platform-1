CREATE TABLE whatsapp_service.whatsapp_chats
(
    id                  BIGSERIAL PRIMARY KEY,
    whatsapp_account_id BIGINT NOT NULL,
    contact_wa_id       TEXT   NOT NULL,
    contact_display     TEXT,
    last_message_date     TIMESTAMP,
    created_date        TIMESTAMP NOT NULL,
    last_modified_date  TIMESTAMP NOT NULL
);

ALTER TABLE whatsapp_service.whatsapp_chats
    ADD CONSTRAINT uq_account_contact UNIQUE (whatsapp_account_id, contact_wa_id);

ALTER TABLE whatsapp_service.whatsapp_chats
    ADD CONSTRAINT fk_chats_account
        FOREIGN KEY (whatsapp_account_id) REFERENCES whatsapp_service.whatsapp_credentials (id) ON DELETE CASCADE;

CREATE INDEX idx_whatsapp_chats_account_id
    ON whatsapp_service.whatsapp_chats (whatsapp_account_id);