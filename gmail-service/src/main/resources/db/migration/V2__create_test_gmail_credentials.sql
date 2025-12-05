INSERT INTO gmail_service.gmail_credentials
(user_id, email_address, client_id, client_secret, refresh_token, access_token, scopes, created_date, last_modified_date)
VALUES
    (1, 'alice@gmail.com', 'client_id_1', 'client_secret_1', 'refresh_token_1', 'access_token_1', 'https://www.googleapis.com/auth/gmail.readonly', now(), now()),
    (2, 'bob@gmail.com', 'client_id_2', 'client_secret_2', 'refresh_token_2', 'access_token_2', 'https://www.googleapis.com/auth/gmail.readonly', now(), now());