-- database-init/01-init.sql

-- 1. Create core roles
CREATE ROLE app_read NOLOGIN;
CREATE ROLE app_write NOLOGIN;
CREATE ROLE app_admin NOLOGIN;

-- 2. Grant CONNECT privilege FIRST
GRANT CONNECT ON DATABASE communication_platform TO app_read, app_write, app_admin;

-- 3. Set up role inheritance
GRANT app_read TO app_write;
GRANT app_write TO app_admin;

-- 4. Create single application user
CREATE USER app_user WITH PASSWORD 'SuperSecureAppPassword890!' IN ROLE app_write;
CREATE USER db_admin WITH PASSWORD 'SuperSecureAdminPassword123!' IN ROLE app_admin;

-- 5. Create all schemas owned by app_admin
CREATE SCHEMA IF NOT EXISTS user_service AUTHORIZATION app_admin;
CREATE SCHEMA IF NOT EXISTS telegram_service AUTHORIZATION app_admin;
CREATE SCHEMA IF NOT EXISTS gmail_service AUTHORIZATION app_admin;
CREATE SCHEMA IF NOT EXISTS whatsapp_service AUTHORIZATION app_admin;
CREATE SCHEMA IF NOT EXISTS ai_service AUTHORIZATION app_admin;

-- 6. Grant CREATE privilege to app_write role on all schemas
-- This allows Flyway (running as app_user with app_write role) to create tables
GRANT CREATE ON SCHEMA user_service TO app_write;
GRANT CREATE ON SCHEMA telegram_service TO app_write;
GRANT CREATE ON SCHEMA gmail_service TO app_write;
GRANT CREATE ON SCHEMA whatsapp_service TO app_write;
GRANT CREATE ON SCHEMA ai_service TO app_write;

-- 7. Also grant USAGE (already included in CREATE, but explicit is good)
GRANT USAGE ON SCHEMA user_service TO app_read, app_write;
GRANT USAGE ON SCHEMA telegram_service TO app_read, app_write;
GRANT USAGE ON SCHEMA gmail_service TO app_read, app_write;
GRANT USAGE ON SCHEMA whatsapp_service TO app_read, app_write;
GRANT USAGE ON SCHEMA ai_service TO app_read, app_write;

-- 8. Set default privileges for each schema
-- This ensures FUTURE tables created by app_admin get proper permissions
-- user_service
ALTER DEFAULT PRIVILEGES FOR ROLE app_admin IN SCHEMA user_service 
GRANT SELECT ON TABLES TO app_read;

ALTER DEFAULT PRIVILEGES FOR ROLE app_admin IN SCHEMA user_service 
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO app_write;

ALTER DEFAULT PRIVILEGES FOR ROLE app_admin IN SCHEMA user_service 
GRANT USAGE, SELECT ON SEQUENCES TO app_write;

-- Repeat for other schemas...
ALTER DEFAULT PRIVILEGES FOR ROLE app_admin IN SCHEMA telegram_service 
GRANT SELECT ON TABLES TO app_read;

ALTER DEFAULT PRIVILEGES FOR ROLE app_admin IN SCHEMA telegram_service 
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO app_write;

ALTER DEFAULT PRIVILEGES FOR ROLE app_admin IN SCHEMA telegram_service 
GRANT USAGE, SELECT ON SEQUENCES TO app_write;

-- gmail_service
ALTER DEFAULT PRIVILEGES FOR ROLE app_admin IN SCHEMA gmail_service 
GRANT SELECT ON TABLES TO app_read;

ALTER DEFAULT PRIVILEGES FOR ROLE app_admin IN SCHEMA gmail_service 
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO app_write;

ALTER DEFAULT PRIVILEGES FOR ROLE app_admin IN SCHEMA gmail_service 
GRANT USAGE, SELECT ON SEQUENCES TO app_write;

-- whatsapp_service
ALTER DEFAULT PRIVILEGES FOR ROLE app_admin IN SCHEMA whatsapp_service 
GRANT SELECT ON TABLES TO app_read;

ALTER DEFAULT PRIVILEGES FOR ROLE app_admin IN SCHEMA whatsapp_service 
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO app_write;

ALTER DEFAULT PRIVILEGES FOR ROLE app_admin IN SCHEMA whatsapp_service 
GRANT USAGE, SELECT ON SEQUENCES TO app_write;

-- ai_service
ALTER DEFAULT PRIVILEGES FOR ROLE app_admin IN SCHEMA ai_service 
GRANT SELECT ON TABLES TO app_read;

ALTER DEFAULT PRIVILEGES FOR ROLE app_admin IN SCHEMA ai_service 
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO app_write;

ALTER DEFAULT PRIVILEGES FOR ROLE app_admin IN SCHEMA ai_service 
GRANT USAGE, SELECT ON SEQUENCES TO app_write;


-- 9. Security hardening
REVOKE CREATE ON SCHEMA public FROM PUBLIC;