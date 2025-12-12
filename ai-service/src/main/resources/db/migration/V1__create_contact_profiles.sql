CREATE TABLE IF NOT EXISTS ai_service.contact_profiles
(
    id                 BIGSERIAL PRIMARY KEY,
    user_id            BIGINT NOT NULL,
    contact_name       TEXT   NOT NULL,
    relationship_type  TEXT   NOT NULL,
    tone_style         TEXT,
    formality_level    INT CHECK (formality_level BETWEEN 1 AND 5),
    preferred_greeting TEXT,
    platform           TEXT,
    chat_identifier    TEXT,
    created_date       TIMESTAMP NOT NULL,
    last_modified_date TIMESTAMP NOT NULL
);

ALTER TABLE ai_service.contact_profiles
    ADD CONSTRAINT uk_contact_profiles_platform_chat_identifier_user_id
        UNIQUE (platform, chat_identifier, user_id);

ALTER TABLE ai_service.contact_profiles
    ADD CONSTRAINT fk_contact_profiles_user_id FOREIGN KEY (user_id)
        REFERENCES user_service.users(id) ON DELETE CASCADE;

CREATE INDEX idx_contact_profiles_user_id
    ON ai_service.contact_profiles (user_id);

ALTER TABLE ai_service.contact_profiles
    ADD CONSTRAINT chk_relationship_type
        CHECK (contact_profiles.relationship_type IN ('SUPERVISOR','COLLEAGUE','EMPLOYEE','CUSTOMER',
                                                      'LEAD','SUPPLIER','BUSINESS_PARTNER','INVESTOR',
                                                      'FRIEND','CLOSE_FRIEND','FAMILY_MEMBER','MENTOR','TEAM',
                                                      'STUDENT','VIP_CLIENT'));

ALTER TABLE ai_service.contact_profiles
    ADD CONSTRAINT chk_tone_style
        CHECK (contact_profiles.tone_style IN ('PROFESSIONAL','FORMAL','CASUAL','FRIENDLY','WARM',
                                               'DIRECT','CONCISE','ENTHUSIASTIC','EMPATHETIC','CORPORATE',
                                               'RESPECTFUL','ASSERTIVE','DIPLOMATIC','MOTIVATIONAL','SALES_FOCUSED',
                                               'ANALYTICAL'));

ALTER TABLE ai_service.contact_profiles
    ADD CONSTRAINT chk_platform
        CHECK (contact_profiles.platform IN ('TELEGRAM','EMAIL','DISCORD'));