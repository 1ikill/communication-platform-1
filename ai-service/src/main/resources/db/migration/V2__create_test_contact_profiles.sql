INSERT INTO ai_service.contact_profiles
(user_id, contact_name, relationship_type, tone_style, formality_level, preferred_greeting, platform, chat_identifier, created_date, last_modified_date)
VALUES
    (1, 'Charlie', 'FRIEND', 'FRIENDLY', 3, 'Hi', 'WHATSAPP', 'contact_1', now(), now()),
    (2, 'Dave', 'SUPERVISOR', 'FORMAL', 5, 'Hello', 'TELEGRAM', 'contact_2', now(), now());