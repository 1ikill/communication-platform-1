# Main Service API Documentation

## Overview
The Main Service acts as a unified backend-for-frontend (BFF) layer that aggregates functionality from all platform-specific services (Telegram, Discord, Gmail). It provides simplified endpoints for sending messages across multiple communication platforms with optional AI-powered personalization.

**Base URL:** `/messages`

**Authentication:** All endpoints require JWT Bearer token authentication.

---

## Table of Contents
1. [Message Broadcasting](#message-broadcasting)
2. [Platform-Specific Messaging](#platform-specific-messaging)
3. [Data Models](#data-models)
4. [Error Responses](#error-responses)

---

## Message Broadcasting

### 1. Broadcast Messages

**Endpoint:** `POST /messages/broadcast`

**Description:** Broadcast messages

Sends messages to multiple recipients across different communication platforms simultaneously. Supports optional AI personalization for each recipient based on their contact profile.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

#### Request Body

| Field Name | Type | Required | Description |
|------------|------|----------|-------------|
| receivers | Array | Yes | List of message requests for different platforms |
| personalize | Boolean | No | Enable AI personalization for all messages |

**MessageRequestDto Fields (for each receiver):**

| Field Name | Type | Required | Description |
|------------|------|----------|-------------|
| platform | Enum | Yes | Communication platform (TELEGRAM, EMAIL, DISCORD, VIBER, TEAMS) |
| chatIdentifier | String | Yes | Chat/channel/email identifier |
| message | String | Yes | Message content to send |

**TelegramMessageRequestDto (extends MessageRequestDto):**

| Field Name | Type | Required | Description |
|------------|------|----------|-------------|
| accountId | String | Yes | Telegram account identifier |

**DiscordMessageRequestDto (extends MessageRequestDto):**

| Field Name | Type | Required | Description |
|------------|------|----------|-------------|
| accountId | Long | Yes | Discord bot account ID |
| messageType | Enum | Yes | Message type (PRIVATE, CHANNEL) |

**GmailMessageRequestDto (extends MessageRequestDto):**

| Field Name | Type | Required | Description |
|------------|------|----------|-------------|
| accountId | Long | Yes | Gmail account ID |
| subject | String | Yes | Email subject |

#### Request Example
```json
{
  "receivers": [
    {
      "platform": "TELEGRAM",
      "chatIdentifier": "123456789",
      "message": "Hello, this is a test message",
      "accountId": "telegram_account_1"
    },
    {
      "platform": "EMAIL",
      "chatIdentifier": "recipient@example.com",
      "message": "Hello, this is a test email",
      "accountId": 1,
      "subject": "Important Update"
    },
    {
      "platform": "DISCORD",
      "chatIdentifier": "987654321098765432",
      "message": "Hello Discord user",
      "accountId": 1,
      "messageType": "PRIVATE"
    }
  ],
  "personalize": true
}
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Empty

#### Error Responses

**Bad Request (400)**
```json
{
  "status": 400,
  "error": "Unknown discord message type"
}
```

**Unauthorized (401)**
```json
{
  "status": 401,
  "error": "Unauthorized"
}
```

**Bad Request (400) - Service Integration Error**
```json
{
  "status": 400,
  "error": "Error message from underlying service"
}
```

#### Behavior

- **Without personalization (`personalize: false`)**: Sends the same message to all recipients as-is
- **With personalization (`personalize: true`)**: 
  - Calls AI Service to personalize each message based on contact profiles
  - Uses platform and chat identifier to look up contact profile
  - Applies relationship type, tone style, and formality level
  - Falls back to original message if no profile exists

---

## Platform-Specific Messaging

### 2. Send Telegram Message

**Endpoint:** `POST /messages/telegram/text`

**Description:** Send telegram message

Sends a text message via Telegram with optional AI personalization.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| originalMessage | String | Yes | Message content to send |
| chatId | Long | Yes | Telegram chat ID |
| accountId | String | Yes | Telegram account identifier |
| personalize | Boolean | Yes | Enable AI personalization (true/false) |

#### Request Example
```
POST /messages/telegram/text?originalMessage=Please%20send%20the%20report&chatId=123456789&accountId=telegram_account_1&personalize=true
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Empty

#### Error Responses

**Bad Request (400)**
```json
{
  "status": 400,
  "error": "Error message"
}
```

**Unauthorized (401)**
```json
{
  "status": 401,
  "error": "Unauthorized"
}
```

---

### 3. Send Discord Private Message

**Endpoint:** `POST /messages/discord/private`

**Description:** Send discord private message

Sends a private direct message via Discord with optional AI personalization.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| originalMessage | String | Yes | Message content to send |
| chatId | String | Yes | Discord user ID |
| accountId | Long | Yes | Discord bot account ID |
| personalize | Boolean | Yes | Enable AI personalization (true/false) |

#### Request Example
```
POST /messages/discord/private?originalMessage=Hello&chatId=987654321098765432&accountId=1&personalize=false
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Empty

#### Error Responses

Same as "Send Telegram Message" endpoint.

---

### 4. Send Discord Channel Message

**Endpoint:** `POST /messages/discord/channel`

**Description:** Send discord channel message

Sends a message to a Discord guild channel with optional AI personalization.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| originalMessage | String | Yes | Message content to send |
| chatId | String | Yes | Discord channel ID |
| accountId | Long | Yes | Discord bot account ID |
| personalize | Boolean | Yes | Enable AI personalization (true/false) |

#### Request Example
```
POST /messages/discord/channel?originalMessage=Team%20meeting%20at%203pm&chatId=123456789012345678&accountId=1&personalize=false
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Empty

#### Error Responses

Same as "Send Telegram Message" endpoint.

---

### 5. Send Gmail Message

**Endpoint:** `POST /messages/gmail/text`

**Description:** Send gmail message

Sends an email via Gmail with optional AI personalization.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| originalMessage | String | Yes | Email body content |
| chatId | String | Yes | Recipient email address |
| accountId | Long | Yes | Gmail account ID |
| subject | String | Yes | Email subject |
| personalize | Boolean | Yes | Enable AI personalization (true/false) |

#### Request Example
```
POST /messages/gmail/text?originalMessage=Please%20review%20the%20attached%20document&chatId=recipient@example.com&accountId=1&subject=Document%20Review&personalize=true
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Empty

#### Error Responses

Same as "Send Telegram Message" endpoint.

---

## Data Models

### BroadcastMessageRequestDto
Request for broadcasting messages to multiple recipients.

| Field | Type | Description |
|-------|------|-------------|
| receivers | Array | List of MessageRequestDto objects |
| personalize | Boolean | Enable AI personalization for all messages |

### MessageRequestDto (Abstract)
Base class for platform-specific message requests.

| Field | Type | Description |
|-------|------|-------------|
| platform | Enum | Communication platform type |
| chatIdentifier | String | Chat, channel, or email identifier |
| message | String | Message content |

### TelegramMessageRequestDto
Telegram-specific message request.

| Field | Type | Description |
|-------|------|-------------|
| platform | Enum | Set to "TELEGRAM" |
| chatIdentifier | String | Telegram chat ID |
| message | String | Message content |
| accountId | String | Telegram account identifier |

### DiscordMessageRequestDto
Discord-specific message request.

| Field | Type | Description |
|-------|------|-------------|
| platform | Enum | Set to "DISCORD" |
| chatIdentifier | String | Discord user or channel ID |
| message | String | Message content |
| accountId | Long | Discord bot account ID |
| messageType | Enum | PRIVATE or CHANNEL |

### GmailMessageRequestDto
Gmail-specific message request.

| Field | Type | Description |
|-------|------|-------------|
| platform | Enum | Set to "EMAIL" |
| chatIdentifier | String | Recipient email address |
| message | String | Email body content |
| accountId | Long | Gmail account ID |
| subject | String | Email subject |

---

## Enums Reference

### CommunicationPlatformType
Communication platform types.

| Value | Description |
|-------|-------------|
| TELEGRAM | Telegram messenger |
| EMAIL | Email (Gmail) |
| VIBER | Viber messenger |
| DISCORD | Discord platform |
| TEAMS | Microsoft Teams |

**Constants for JSON serialization:**
- `TELEGRAM_NAME` = "TELEGRAM"
- `EMAIL_NAME` = "EMAIL"
- `VIBER_NAME` = "VIBER"
- `DISCORD_NAME` = "DISCORD"
- `TEAMS_NAME` = "TEAMS"

### DiscordMessageType
Discord message destination types.

| Value | Description |
|-------|-------------|
| PRIVATE | Direct message to user |
| CHANNEL | Message to guild channel |

---

## Error Responses

### Common HTTP Status Codes

| Status Code | Description | When It Occurs |
|-------------|-------------|----------------|
| 200 | OK | Request successful |
| 400 | Bad Request | Invalid input, integration error, or unknown message type |
| 401 | Unauthorized | Missing or invalid JWT token |
| 404 | Not Found | Account, chat, or resource not found in underlying services |
| 500 | Internal Server Error | Unexpected server error |

### Error Response Format

**General Error (400, 404, 500)**
```json
{
  "status": 400,
  "error": "Error message"
}
```

### Common Error Messages

| Error Message | Status | Meaning |
|--------------|--------|---------|
| `Unknown discord message type` | 400 | Invalid Discord message type specified |
| `Unauthorized` | 401 | Missing or invalid JWT token |
| Integration service errors | 400/404 | Errors propagated from Telegram, Discord, Gmail, or AI services |

---

## AI Personalization

When `personalize` is set to `true`, the service integrates with the AI Service to customize messages based on contact profiles.

### Personalization Flow

1. **Check Contact Profile**: Looks up contact profile using platform and chat identifier
2. **AI Processing**: If profile exists, calls AI Service with:
   - Original message
   - Platform type
   - Chat identifier
3. **Apply Customization**: AI Service personalizes message based on:
   - Contact name
   - Relationship type (supervisor, colleague, customer, etc.)
   - Tone style (professional, formal, casual, friendly, etc.)
   - Formality level (1-5)
   - Preferred greeting
4. **Fallback**: If no profile exists or AI fails, sends original message unchanged

### Broadcast Personalization

For broadcast messages with `personalize: true`:
- **Batch Processing**: Personalizes all messages in parallel
- **Individual Profiles**: Each recipient gets message customized to their profile
- **Efficient**: Caches personalized messages to avoid duplicate AI calls
- **Resilient**: Continues sending even if some personalizations fail

---

## Integration Architecture

The Main Service acts as an orchestration layer:

```
┌──────────────────┐
│   Main Service   │
│   (BFF Layer)    │
└────────┬─────────┘
         │
         ├─────────► Telegram Service (sends Telegram messages)
         │
         ├─────────► Discord Service (sends Discord messages)
         │
         ├─────────► Gmail Service (sends emails)
         │
         └─────────► AI Service (personalizes messages)
```

### Service Communication

- **Synchronous REST calls** to platform services
- **AI Service integration** for message personalization
- **Error propagation** from underlying services
- **No data persistence** in Main Service (stateless orchestration)

---

## Security

All endpoints require JWT Bearer token authentication. Include the token in the `Authorization` header:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

Tokens are obtained from the User Service authentication endpoints.

---

## Usage Examples

### Example 1: Simple Single-Platform Message

Send a Telegram message without personalization:

```bash
POST /messages/telegram/text?originalMessage=Hello%20team&chatId=123456789&accountId=telegram_1&personalize=false
```

### Example 2: Personalized Single Message

Send a personalized Telegram message:

```bash
POST /messages/telegram/text?originalMessage=Please%20send%20the%20report&chatId=123456789&accountId=telegram_1&personalize=true
```

If a contact profile exists for this chat, the message might be transformed to:
```
"Hello John! Could you please send me the report when you have a moment? Thank you!"
```

### Example 3: Multi-Platform Broadcast

Send the same announcement to multiple platforms:

```json
POST /messages/broadcast
{
  "receivers": [
    {
      "platform": "TELEGRAM",
      "chatIdentifier": "111111111",
      "message": "Team meeting tomorrow at 2 PM",
      "accountId": "telegram_1"
    },
    {
      "platform": "DISCORD",
      "chatIdentifier": "987654321098765432",
      "message": "Team meeting tomorrow at 2 PM",
      "accountId": 1,
      "messageType": "CHANNEL"
    },
    {
      "platform": "EMAIL",
      "chatIdentifier": "team@company.com",
      "message": "Team meeting tomorrow at 2 PM. Please confirm attendance.",
      "accountId": 1,
      "subject": "Team Meeting Tomorrow"
    }
  ],
  "personalize": false
}
```

### Example 4: Personalized Broadcast

Send personalized messages to different contacts:

```json
POST /messages/broadcast
{
  "receivers": [
    {
      "platform": "TELEGRAM",
      "chatIdentifier": "111111111",
      "message": "Please review the quarterly report",
      "accountId": "telegram_1"
    },
    {
      "platform": "TELEGRAM",
      "chatIdentifier": "222222222",
      "message": "Please review the quarterly report",
      "accountId": "telegram_1"
    }
  ],
  "personalize": true
}
```

With contact profiles configured:
- **Chat 111111111** (Supervisor, Formal): "Good morning Mr. Johnson! I hope this message finds you well. Would you be able to review the quarterly report at your earliest convenience? Thank you for your time. Best regards."
- **Chat 222222222** (Colleague, Casual): "Hey Sarah! Can you take a look at the quarterly report when you get a chance? Thanks!"

---

## Notes

- **Stateless Service**: Main Service doesn't store any data, it orchestrates calls to other services
- **Synchronous Operations**: All operations are synchronous; no background jobs
- **Error Handling**: Errors from underlying services are propagated to the client
- **AI Personalization**: Optional and configurable per message or per broadcast
- **Platform Flexibility**: Easy to add new platforms by extending MessageRequestDto
- **Account IDs**: Different platforms use different account ID formats (String for Telegram, Long for Discord/Gmail)
- **Message Types**: Discord requires messageType; other platforms don't
- **Email Subject**: Required for Gmail messages
- **Broadcast Efficiency**: Personalized broadcasts use batch processing for AI calls
- **Fallback Behavior**: If AI personalization fails, original message is sent
- **Chat Identifier**: Format varies by platform (numeric ID, email address, Discord snowflake)


