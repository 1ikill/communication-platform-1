# User Guide
## User Guide

This section provides instructions for end users on how to use the application.

### Contents

- [Features Walkthrough](features.md)
- [FAQ & Troubleshooting](faq.md)

### Getting Started

#### System Requirements

| Requirement | Minimum | Recommended |
|-------------|---------|-------------|
| **Browser** | Chrome 90+, Firefox 88+, Safari 14+ | Latest version |
| **Docker** | Docker 20.10+, Docker Compose 2.0+ | Latest version |
| **Java** | Java 17 (for local development) | Java 17 |
| **Internet** | Required | - |
| **Device** | Desktop | - |

#### Accessing the Application

1. Open your web browser
2. Navigate to: **http://localhost:8083** (Main Service API)
3. Access Swagger UI for interactive API testing at **http://localhost:8083/swagger-ui/index.html**

#### First Launch

##### Step 1: User Registration

1. Send POST request to `http://localhost:8083/accounts/users/auth/register`
2. Provide email, username, fullName, and password
3. Receive confirmation response

##### Step 2: Authentication

1. Send POST request to `http://localhost:8083/accounts/users/auth/login` with credentials
2. Save the access token from response
3. Include token in `Authorization: Bearer TOKEN` header for all subsequent requests

##### Step 3: Platform Setup

After authentication, configure platform accounts:
- **Telegram**: POST to `/telegram-credentials/add` with API credentials from https://my.telegram.org/apps, proceed with account authorizatiom via `/telegram/auth/**` endpoints. 
- **Discord**: POST to `/api/discord/accounts/bots` with bot token from Discord Developer Portal
- **Gmail**: Navigate to `/gmail/oauth` endpoint and complete OAuth2 flow

### Quick Start Guide

| Task | How To |
|------|--------|
| Send message to single platform | POST to platform-specific endpoint (e.g., `/telegram/text`) with chat ID and message |
| Broadcast to multiple platforms | POST to `/messages/broadcast` via Main Service with receiver list |
| Personalize messages with AI | Include `"personalize": true` in broadcast request and configure contact profiles |
| Check service health | GET request to `/actuator/health` endpoint for each service |

### User Roles

| Role | Permissions | Access Level |
|------|-------------|--------------|
| **Registered User** | Manage own accounts, send/receive messages, configure contact profiles | Full access to owned resources |
| **Platform Account** | Platform-specific messaging operations (Telegram/Discord/Gmail) | Limited to linked platform |