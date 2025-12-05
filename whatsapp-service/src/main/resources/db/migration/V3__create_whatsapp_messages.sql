CREATE TABLE whatsapp_service.whatsapp_messages
(
    id                  BIGSERIAL PRIMARY KEY,
    whatsapp_account_id BIGINT NOT NULL,
    whatsapp_chat_id    BIGINT,
    direction           TEXT   NOT NULL,
    sender_wa_id        TEXT,
    receiver_wa_id      TEXT,
    message_id          TEXT,
    type                TEXT,
    text_body           TEXT,
    raw_payload         JSONB,
    created_date        TIMESTAMP NOT NULL,
    last_modified_date  TIMESTAMP NOT NULL
);

ALTER TABLE whatsapp_service.whatsapp_messages
    add CONSTRAINT fk_messages_account
        FOREIGN KEY (whatsapp_account_id) REFERENCES whatsapp_service.whatsapp_credentials (id) ON DELETE CASCADE;

ALTER TABLE whatsapp_service.whatsapp_messages
    ADD CONSTRAINT fk_messages_chat
        FOREIGN KEY (whatsapp_chat_id) REFERENCES whatsapp_service.whatsapp_chats (id) ON DELETE SET NULL;

CREATE INDEX idx_whatsapp_messages_account_id
    ON whatsapp_service.whatsapp_messages (whatsapp_account_id);
CREATE INDEX idx_whatsapp_messages_chat_id
    ON whatsapp_service.whatsapp_messages (whatsapp_chat_id);

ALTER TABLE whatsapp_service.whatsapp_messages
    ADD CONSTRAINT chk_messages_direction
        CHECK (direction IN ('INBOUND', 'OUTBOUND'));

ALTER TABLE whatsapp_service.whatsapp_messages
    ADD CONSTRAINT chk_raw_payload_is_object
        CHECK (raw_payload IS NULL OR jsonb_typeof(raw_payload) = 'object');

ALTER TABLE whatsapp_service.whatsapp_messages
    ADD CONSTRAINT chk_messages_type
        CHECK (type IN ('TEXT', 'IMAGE', 'AUDIO', 'VIDEO', 'DOCUMENT', 'OTHER'));