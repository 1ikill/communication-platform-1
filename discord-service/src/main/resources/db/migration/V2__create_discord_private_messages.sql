CREATE TABLE discord_service.discord_private_messages
(
    id                 BIGSERIAL PRIMARY KEY,
    bot_id             BIGINT  NOT NULL,
    discord_message_id TEXT    NOT NULL,
    channel_id         TEXT    NOT NULL,
    author_id          TEXT    NOT NULL,
    author_name        TEXT    NOT NULL,
    content            TEXT,
    is_from_bot        BOOLEAN NOT NULL DEFAULT false,
    timestamp          TIMESTAMP,
    created_date       TIMESTAMP,
    last_modified_date TIMESTAMP
);

ALTER TABLE discord_service.discord_private_messages
    ADD CONSTRAINT fk_bot_id FOREIGN KEY (bot_id)
        REFERENCES discord_service.discord_credentials(id) ON DELETE CASCADE;

ALTER TABLE discord_service.discord_private_messages
    ADD CONSTRAINT unique_discord_message UNIQUE(bot_id, discord_message_id);

CREATE INDEX idx_dm_bot_channel_time
    ON discord_service.discord_private_messages (bot_id, channel_id, timestamp DESC);

CREATE INDEX idx_dm_bot_author
    ON discord_service.discord_private_messages (bot_id, author_id);

CREATE INDEX idx_dm_timestamp
    ON discord_service.discord_private_messages (timestamp DESC);