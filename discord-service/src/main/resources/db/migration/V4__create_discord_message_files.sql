CREATE TABLE discord_service.discord_message_files
(
    id                 BIGSERIAL PRIMARY KEY,
    discord_message_id BIGINT NOT NULL,
    file_name          TEXT   NOT NULL,
    file_type          TEXT,
    discord_url        TEXT   NOT NULL,
    created_date       TIMESTAMP
);

ALTER TABLE discord_service.discord_message_files
    ADD CONSTRAINT fk_message_id FOREIGN KEY (discord_message_id)
        REFERENCES discord_service.discord_private_messages (id) ON DELETE CASCADE;

ALTER TABLE discord_service.discord_private_messages
    ADD COLUMN has_attachments BOOLEAN NOT NULL DEFAULT false;