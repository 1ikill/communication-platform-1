CREATE TABLE discord_service.discord_credentials
(
    id                 BIGSERIAL PRIMARY KEY,
    user_id            BIGINT NOT NULL,
    bot_token          TEXT   NOT NULL,
    bot_user_id        TEXT   NOT NULL,
    bot_username       TEXT   NOT NULL,
    is_active          BOOLEAN DEFAULT true,
    created_date       TIMESTAMP,
    last_modified_date TIMESTAMP
);