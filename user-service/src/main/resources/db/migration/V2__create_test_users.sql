INSERT INTO user_service.users (email, username, password_hash, full_name, role, created_date, last_modified_date)
VALUES
    ('alice@example.com', 'alice', 'hashed_password_1', 'Alice Smith', 'USER', now(), now()),
    ('bob@example.com', 'bob', 'hashed_password_2', 'Bob Johnson', 'USER', now(), now()),
    ('admin@example.com', 'admin', 'hashed_password_3', 'Admin User', 'ADMIN', now(), now());