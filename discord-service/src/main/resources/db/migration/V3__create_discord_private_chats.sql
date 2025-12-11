CREATE TABLE discord_service.discord_private_chats
(
    id                 BIGSERIAL PRIMARY KEY,
    bot_id             BIGINT    NOT NULL,
    channel_id         TEXT      NOT NULL,
    user_id            TEXT      NOT NULL,
    user_name          TEXT      NOT NULL,
    user_avatar_url    TEXT,
    last_message_time  TIMESTAMP NOT NULL,
    last_message_id    TEXT,
    message_count      INTEGER DEFAULT 0,
    created_date       TIMESTAMP,
    last_modified_date TIMESTAMP
);

CREATE INDEX idx_chats_bot_time ON discord_service.discord_private_chats(bot_id, last_message_time DESC);
CREATE INDEX idx_chats_bot_user ON discord_service.discord_private_chats(bot_id, user_id);
CREATE INDEX idx_chats_channel ON discord_service.discord_private_chats(channel_id);

ALTER TABLE discord_service.discord_private_chats
    ADD CONSTRAINT fk_bot_id FOREIGN KEY (bot_id)
        REFERENCES discord_service.discord_credentials(id) ON DELETE CASCADE;

ALTER TABLE discord_service.discord_private_chats
    ADD CONSTRAINT unique_chat UNIQUE(bot_id, channel_id);

ALTER TABLE discord_service.discord_private_chats
    ADD CONSTRAINT unique_user_chat UNIQUE(bot_id, user_id)