INSERT INTO whatsapp_service.whatsapp_credentials
(user_id, display_name, waba_id, phone_number_id, access_token, webhook_verify_token, created_date, last_modified_date)
VALUES
    (1, 'Alice WA', 'waba_1', 'phone_id_1', 'access_token_1', 'verify_token_1', now(), now()),
    (2, 'Bob WA', 'waba_2', 'phone_id_2', 'access_token_2', 'verify_token_2', now(), now());

INSERT INTO whatsapp_service.whatsapp_chats
(whatsapp_account_id, contact_wa_id, contact_display, last_message_date, created_date, last_modified_date)
VALUES
    (1, 'contact_1', 'Charlie', now(), now(), now()),
    (2, 'contact_2', 'Dave', now(), now(), now());

INSERT INTO whatsapp_service.whatsapp_messages
(whatsapp_account_id, whatsapp_chat_id, direction, sender_wa_id, receiver_wa_id, message_id, type, text_body, created_date, last_modified_date)
VALUES
    (1, 1, 'OUTBOUND', '+1111111111', '+3333333333', 'msg_1', 'TEXT', 'Hello Charlie', now(), now()),
    (2, 2, 'INBOUND', '+4444444444', '+2222222222', 'msg_2', 'TEXT', 'Hi Bob', now(), now());