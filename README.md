# Communication Platform

A unified communication platform that aggregates multiple messaging channels (Telegram, Discord, Gmail) into a single backend system with AI-powered message personalization.

## Overview

This project is a microservices-based communication platform that enables users to manage and send messages across multiple communication channels from a centralized system. The platform integrates with Telegram, Discord, and Gmail, providing a unified API for messaging operations with optional AI-powered message personalization based on contact profiles.

### Key Features

- **Multi-Channel Messaging**: Send and receive messages across Telegram, Discord, and Gmail
- **Unified Authentication**: JWT-based authentication system for all services
- **AI Message Personalization**: Customize messages based on contact relationship, tone, and formality level
- **Broadcast Messaging**: Send personalized messages to multiple recipients across different platforms simultaneously
- **Account Management**: Support for multiple accounts per platform per user
- **File Operations**: Handle images, videos, documents, and attachments across all platforms
- **OAuth Integration**: Secure OAuth2 flow for Gmail integration
- **Real-time Updates**: Telegram integration via TDLib for real-time message updates

---

## Architecture

The platform consists of 6 microservices built with Spring Boot and deployed using Docker:

```
┌──────────────────────────────────────────────────────────────────────┐
│                        Communication Platform                        │
└──────────────────────────────────────────────────────────────────────┘

┌─────────────────┐
│  User Service   │  ←── JWT Authentication Provider
│   Port: 8082    │      User management, registration, login
└────────┬────────┘
         │
         │ JWT Validation
         │
    ┌────┴────────────────────────────────────────────────┐
    │                                                     │
    ▼                                                     ▼
┌─────────────────┐                              ┌─────────────────┐
│  Main Service   │  ←── BFF Layer               │   AI Service    │
│   Port: 8083    │      Message orchestration   │   Port: 8085    │
└────────┬────────┘                              └────────┬────────┘
         │                                                │
         │ Orchestrates messaging                         │ Message
         │                                                │ Personalization
         │                                                │
    ┌────┴────────┬──────────────┬──────────────┐         │
    ▼             ▼              ▼              ▼         │
┌─────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐     │
│Telegram │  │ Discord  │  │  Gmail   │  │ Contact  │◄────┘
│ Service │  │ Service  │  │ Service  │  │ Profiles │
│8084     │  │8088      │  │8086      │  └──────────┘
└─────────┘  └──────────┘  └──────────┘

         ▼              ▼              ▼
    ┌─────────────────────────────────────┐
    │     PostgreSQL Database             │
    │     Port: 5432                      │
    │     Schema per service              │
    └─────────────────────────────────────┘
```

### Microservices

| Service | Port | Description |
|---------|------|-------------|
| **User Service** | 8082 | Authentication, authorization, user management |
| **Main Service** | 8083 | Backend-for-frontend, message orchestration, broadcast functionality |
| **Telegram Service** | 8084 | Telegram integration using TDLib, message management |
| **AI Service** | 8085 | Contact profile management, AI-powered message personalization |
| **Gmail Service** | 8086 | Gmail integration via OAuth2, email sending/reading |
| **Discord Service** | 8088 | Discord bot management, messaging, file operations |

### Technology Stack

- **Backend**: Java 17, Spring Boot 3.x
- **Database**: PostgreSQL 16 with Flyway migrations
- **Security**: JWT (JSON Web Tokens), BCrypt password hashing
- **API Documentation**: SpringDoc OpenAPI 3 (Swagger)
- **Containerization**: Docker, Docker Compose
- **Build Tool**: Maven
- **External APIs**: 
  - Telegram TDLib
  - Discord JDA (Java Discord API)
  - Google Gmail API
  - OpenAI API (GPT-4o-mini, GPT-4.1-mini)

---

## Getting Started

### Prerequisites

- **Docker** and **Docker Compose** installed
- **Java 17** or higher (for local development)
- **Maven 3.8+** (for local development)
- API credentials for:
  - Telegram (from https://my.telegram.org/apps)
  - Discord Bot (from https://discord.com/developers/applications)
  - Google Cloud Console (for Gmail API)
  - OpenAI API key

### Environment Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd communication-platform-1
   ```

2. **Create environment file**
   
   Create a `.env` file in the project root with the following variables:
   
   ```env
   # Database
   POSTGRES_PASSWORD=your_postgres_password
   APP_DB_PASSWORD=SuperSecureAppPassword890!
   
   # JWT
   JWT_SECRET=your_jwt_secret_key_min_256_bits
   JWT_ACCESS_EXPIRATION=3600000
   JWT_REFRESH_EXPIRATION=604800000
   
   # OpenAI
   OPENAI_API_KEY=your_openai_api_key
   
   # Google OAuth (Gmail)
   GOOGLE_CLIENT_ID=your_google_client_id
   GOOGLE_CLIENT_SECRET=your_google_client_secret
   GOOGLE_REDIRECT_URI=http://localhost:8086/gmail/oauth/callback
   
   # Telegram
   TELEGRAM_MASTER_KEY=your_32_byte_hex_encryption_key
   ```

3. **Build all services**
   
   Build all microservices using Maven:
   ```bash
   # Build all services
   mvn clean package -DskipTests
   ```
   
   Or build individual services:
   ```bash
   cd user-service && mvn clean package -DskipTests
   cd ../telegram-service && mvn clean package -DskipTests
   # ... repeat for other services
   ```

4. **Start the platform**
   
   ```bash
   docker-compose up -d
   ```
   
   This will start all services:
   - PostgreSQL database
   - All 6 microservices with health checks

5. **Verify services are running**
   
   Check the status of all containers:
   ```bash
   docker-compose ps
   ```
   
   Access health endpoints:
   - User Service: http://localhost:8082/actuator/health
   - Main Service: http://localhost:8083/actuator/health
   - Telegram Service: http://localhost:8084/actuator/health
   - AI Service: http://localhost:8085/actuator/health
   - Gmail Service: http://localhost:8086/actuator/health
   - Discord Service: http://localhost:8088/actuator/health

### Initial Setup

1. **Register a user**
   
   ```bash
   curl -X POST http://localhost:8082/users/auth/register \
     -H "Content-Type: application/json" \
     -d '{
       "email": "user@example.com",
       "username": "myusername",
       "fullName": "John Doe",
       "password": "SecurePassword123"
     }'
   ```

2. **Login to get JWT tokens**
   
   ```bash
   curl -X POST http://localhost:8082/users/auth/login \
     -H "Content-Type: application/json" \
     -d '{
       "username": "myusername",
       "password": "SecurePassword123"
     }'
   ```
   
   Save the `access` token for subsequent requests.

3. **Add platform credentials**
   
   **Telegram:**
   ```bash
   curl -X POST http://localhost:8084/telegram-credentials/add \
     -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
     -H "Content-Type: application/json" \
     -d '{
       "apiId": "your_telegram_api_id",
       "apiHash": "your_telegram_api_hash",
       "accountId": "telegram_account_1",
       "accountName": "My Telegram",
       "phoneNumber": "+1234567890"
     }'
   ```
   
   **Discord:**
   ```bash
   curl -X POST http://localhost:8088/api/discord/accounts/bots \
     -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
     -H "Content-Type: application/json" \
     -d '{
       "accountName": "My Discord Bot",
       "token": "your_discord_bot_token"
     }'
   ```
   
   **Gmail:**
   - Navigate to http://localhost:8086/gmail/oauth?accountName=MyGmail
   - Complete the OAuth flow in your browser
   - Authorization code will be exchanged for tokens automatically

---

## API Documentation

Each service exposes Swagger UI for interactive API documentation:

- **User Service**: http://localhost:8082/swagger-ui.index.html#/
- **Main Service**: http://localhost:8083/swagger-ui.index.html#/
- **Telegram Service**: http://localhost:8084/swagger-ui.index.html#/
- **AI Service**: http://localhost:8085/swagger-ui.index.html#/
- **Gmail Service**: http://localhost:8086/swagger-ui.index.html#/
- **Discord Service**: http://localhost:8088/swagger-ui.index.html#/

Detailed API documentation is available in each service directory:
- [User Service API Documentation](docs/USER_SERVICE_API_DOCUMENTATION)
- [Main Service API Documentation](docs/MAIN_SERVICE_API_DOCUMENTATION.md)
- [Telegram Service API Documentation](docs/TELEGRAM_SERVICE_API_DOCUMENTATION.md)
- [AI Service API Documentation](docs/AI_SERVICE_API_DOCUMENTATION.md)
- [Gmail Service API Documentation](docs/GMAIL_SERVICE_API_DOCUMENTATION.md)
- [Discord Service API Documentation](docs/DISCORD_SERVICE_API_DOCUMENTATION.md)

---

## Usage Examples

### Example 1: Send a Simple Message

Send a message to Telegram:
```bash
curl -X POST "http://localhost:8084/telegram/text?chatId=123456789&messageText=Hello&accountId=telegram_account_1" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### Example 2: Broadcast with AI Personalization

Send personalized messages to multiple recipients:
```bash
curl -X POST http://localhost:8083/messages/broadcast \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "receivers": [
      {
        "platform": "TELEGRAM",
        "chatIdentifier": "123456789",
        "message": "Please send the report",
        "accountId": "telegram_account_1"
      },
      {
        "platform": "EMAIL",
        "chatIdentifier": "colleague@example.com",
        "message": "Please send the report",
        "accountId": 1,
        "subject": "Report Request"
      }
    ],
    "personalize": true
  }'
```

### Example 3: Create Contact Profile for Personalization

```bash
curl -X POST http://localhost:8085/ai-service/profiles/add \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "contactName": "John",
    "relationshipType": "SUPERVISOR",
    "toneStyle": "FORMAL",
    "formalityLevel": 5,
    "preferredGreeting": "Hello",
    "platform": "TELEGRAM",
    "chatIdentifier": "123456789"
  }'
```

---

## Database Schema

The platform uses a single PostgreSQL database with separate schemas per service:

- `user_service` - User accounts, authentication
- `telegram_service` - Telegram accounts, credentials
- `discord_service` - Discord bots, messages
- `gmail_service` - Gmail accounts, OAuth tokens
- `ai_service` - Contact profiles, AI personalization data

Database migrations are handled automatically by Flyway on service startup.

---

## Development

### Running Services Locally

To run a service locally (outside Docker):

1. Ensure PostgreSQL is running and accessible
2. Update `application.yml` with local database connection
3. Run the service:
   ```bash
   cd user-service
   mvn spring-boot:run
   ```

### Viewing Logs

View logs for all services:
```bash
docker-compose logs -f
```

View logs for a specific service:
```bash
docker-compose logs -f user-service
docker-compose logs -f telegram-service
```

### Rebuilding Services

After code changes, rebuild and restart:
```bash
# Rebuild specific service
docker-compose up -d --build user-service

# Rebuild all services
docker-compose up -d --build
```

### Stopping Services

```bash
# Stop all services
docker-compose down

# Stop and remove volumes (clears database)
docker-compose down -v
```

---

## Project Structure

```
communication-platform-1/
├── ai-service/              # AI message personalization service
│   ├── src/
│   ├── Dockerfile
│   ├── pom.xml
│   └── API_DOCUMENTATION.md
├── discord-service/         # Discord integration service
│   ├── src/
│   ├── Dockerfile
│   ├── pom.xml
│   └── API_DOCUMENTATION.md
├── gmail-service/           # Gmail integration service
│   ├── src/
│   ├── Dockerfile
│   ├── pom.xml
│   └── API_DOCUMENTATION.md
├── main-service/            # Message orchestration service
│   ├── src/
│   ├── Dockerfile
│   ├── pom.xml
│   └── API_DOCUMENTATION.md
├── telegram-service/        # Telegram integration service
│   ├── src/
│   ├── Dockerfile
│   ├── pom.xml
│   └── API_DOCUMENTATION.md
├── user-service/            # Authentication and user management
│   ├── src/
│   ├── Dockerfile
│   ├── pom.xml
│   └── API_DOCUMENTATION.md
├── database-init/           # Database initialization scripts
│   └── 01-init.sql
├── docker-compose.yml       # Docker Compose configuration
├── .env                     # Environment variables (not in git)
└── README.md               # This file
```

---

## Security

- **Authentication**: JWT-based authentication via User Service
- **Password Hashing**: BCrypt with automatic salt generation
- **Database Access**: Role-based access control with separate read/write roles
- **API Credentials**: Encrypted storage for Telegram, Discord credentials
- **OAuth2**: Secure Gmail integration with token refresh
- **HTTPS**: Recommended for production deployments

---

## Troubleshooting

### Services Won't Start

**Check Docker logs:**
```bash
docker-compose logs
```

**Ensure all required environment variables are set in `.env`**

### Database Connection Issues

**Check PostgreSQL is running:**
```bash
docker-compose ps postgres
```

**Verify database credentials match between `.env` and `database-init/01-init.sql`**

### Authentication Failures

**Ensure JWT_SECRET is set in `.env` and is at least 256 bits**

**Check token expiration - refresh token if needed**

### Service Health Check Failures

**Wait for services to fully start (can take 1-2 minutes)**

**Check individual service health:**
```bash
curl http://localhost:8082/actuator/health
```

---

## Port Reference

| Service | Port | Health Check |
|---------|------|--------------|
| PostgreSQL | 5432 | N/A |
| User Service | 8082 | http://localhost:8082/actuator/health |
| Main Service | 8083 | http://localhost:8083/actuator/health |
| Telegram Service | 8084 | http://localhost:8084/actuator/health |
| AI Service | 8085 | http://localhost:8085/actuator/health |
| Gmail Service | 8086 | http://localhost:8086/actuator/health |
| Discord Service | 8088 | http://localhost:8088/actuator/health |

---

## License

This project is developed as a diploma project.

---

## Contact

For questions or issues, please refer to the API documentation or check service logs.
