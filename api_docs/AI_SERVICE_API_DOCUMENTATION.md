# AI Service API Documentation

## Overview
The AI Service provides endpoints for managing contact profiles and AI-powered message personalization. Contact profiles store information about recipients to enable customized message formatting based on relationship type, tone style, and formality level.

**Base URL:** `/ai-service`

**Authentication:** All endpoints require JWT Bearer token authentication.

---

## Endpoints

### 1. Create Contact Profile

**Endpoint:** `POST /ai-service/profiles/add`

**Description:** Creation of contact profile

Creates a new contact profile for the authenticated user. The profile contains information about a contact that will be used for AI message personalization.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

#### Request Body

| Field Name | Type | Required | Description | Constraints |
|------------|------|----------|-------------|-------------|
| contactName | String | Yes | Contact name | Not blank |
| relationshipType | Enum | No | Relationship type | Values: SUPERVISOR, COLLEAGUE, EMPLOYEE, CUSTOMER, LEAD, SUPPLIER, BUSINESS_PARTNER, INVESTOR |
| toneStyle | Enum | No | Tone style | Values: PROFESSIONAL, FORMAL, CASUAL, FRIENDLY, WARM, DIRECT, CONCISE, ENTHUSIASTIC |
| formalityLevel | Integer | No | Formality level | Range: 1-5 (1=informal, 5=very formal) |
| preferredGreeting | String | No | Preferred greeting | Max 10 characters |
| platform | Enum | No | Communication platform | Values: TELEGRAM, EMAIL, VIBER, DISCORD, TEAMS |
| chatIdentifier | String | Yes | Chat identifier | Not blank |

**Request Example:**
```json
{
  "contactName": "John",
  "relationshipType": "SUPERVISOR",
  "toneStyle": "FORMAL",
  "formalityLevel": 5,
  "preferredGreeting": "Hello",
  "platform": "TELEGRAM",
  "chatIdentifier": "1111111"
}
```

#### Response

**Status Code:** `200 OK`

| Field Name | Type | Description |
|------------|------|-------------|
| id | Long | Id |
| userId | Long | User id |
| contactName | String | Contact name |
| relationshipType | Enum | Relationship type |
| toneStyle | Enum | Tone style |
| formalityLevel | Integer | Formality level |
| preferredGreeting | String | Preferred greeting |
| platform | Enum | Platform |
| chatIdentifier | String | Chat identifier |
| createdDate | DateTime | Created date |
| lastModifiedDate | DateTime | Last modified date |

**Response Example:**
```json
{
  "id": 1,
  "userId": 2,
  "contactName": "John",
  "relationshipType": "SUPERVISOR",
  "toneStyle": "FORMAL",
  "formalityLevel": 5,
  "preferredGreeting": "Hello",
  "platform": "TELEGRAM",
  "chatIdentifier": "1111111",
  "createdDate": "2025-12-18T10:30:00",
  "lastModifiedDate": "2025-12-18T10:30:00"
}
```

#### Error Responses

**Validation Error (400 Bad Request)**
```json
{
  "status": 400,
  "errors": {
    "contactName": "must not be blank",
    "chatIdentifier": "must not be blank",
    "preferredGreeting": "Greeting must be shorter then 10 characters"
  }
}
```

**Unauthorized (401)**
```json
{
  "status": 401,
  "error": "Unauthorized"
}
```

**Bad Request (400)**
```json
{
  "status": 400,
  "error": "Error message"
}
```

---

### 2. Update Contact Profile

**Endpoint:** `PATCH /ai-service/profiles/{id}`

**Description:** Patching of contact profile

Updates an existing contact profile. Only provided fields will be updated, null fields are ignored.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | Long | Yes | Contact profile identifier |

#### Request Body

| Field Name | Type | Required | Description | Constraints |
|------------|------|----------|-------------|-------------|
| contactName | String | No | Contact name | - |
| relationshipType | Enum | No | Relationship type | Values: SUPERVISOR, COLLEAGUE, EMPLOYEE, CUSTOMER, LEAD, SUPPLIER, BUSINESS_PARTNER, INVESTOR |
| toneStyle | Enum | No | Tone style | Values: PROFESSIONAL, FORMAL, CASUAL, FRIENDLY, WARM, DIRECT, CONCISE, ENTHUSIASTIC |
| formalityLevel | Integer | No | Formality level | Range: 1-5 |
| preferredGreeting | String | No | Preferred greeting | Max 10 characters |

**Request Example:**
```json
{
  "toneStyle": "FRIENDLY",
  "formalityLevel": 3
}
```

#### Response

**Status Code:** `200 OK`

| Field Name | Type | Description |
|------------|------|-------------|
| id | Long | Id |
| userId | Long | User id |
| contactName | String | Contact name |
| relationshipType | Enum | Relationship type |
| toneStyle | Enum | Tone style |
| formalityLevel | Integer | Formality level |
| preferredGreeting | String | Preferred greeting |
| platform | Enum | Platform |
| chatIdentifier | String | Chat identifier |
| createdDate | DateTime | Created date |
| lastModifiedDate | DateTime | Last modified date |

**Response Example:**
```json
{
  "id": 1,
  "userId": 2,
  "contactName": "John",
  "relationshipType": "SUPERVISOR",
  "toneStyle": "FRIENDLY",
  "formalityLevel": 3,
  "preferredGreeting": "Hello",
  "platform": "TELEGRAM",
  "chatIdentifier": "1111111",
  "createdDate": "2025-12-18T10:30:00",
  "lastModifiedDate": "2025-12-18T11:45:00"
}
```

#### Error Responses

**Validation Error (400 Bad Request)**
```json
{
  "status": 400,
  "errors": {
    "preferredGreeting": "Greeting must be shorter then 10 characters"
  }
}
```

**Not Found (404)**
```json
{
  "status": 404,
  "error": "Contact profile not found: 1"
}
```

**Unauthorized (401)**
```json
{
  "status": 401,
  "error": "Unauthorized"
}
```

**Bad Request (400)**
```json
{
  "status": 400,
  "error": "Error message"
}
```

---

### 3. Personalize Message

**Endpoint:** `POST /ai-service/format-message`

**Description:** Message ai-personalization

Personalizes a generic message using AI based on the contact profile for the specified platform and chat. If no profile exists, or the message is empty/too long (>500 characters), the original message is returned unchanged.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| platform | Enum | Yes | Communication platform type (TELEGRAM, EMAIL, VIBER, DISCORD, TEAMS) |
| chatIdentifier | String | Yes | Chat identifier to match with contact profile |
| message | String | Yes | Generic message text to personalize (max 500 characters) |

#### Request Example
```
POST /ai-service/format-message?platform=TELEGRAM&chatIdentifier=1111111&message=Please%20send%20me%20the%20report
```

#### Response

**Status Code:** `200 OK`

**Content-Type:** `text/plain`

**Response Body:** The personalized message as plain text

**Response Example:**
```
Hello John! Could you please send me the report when you have a moment? Thank you!
```

**Response Example (no profile or message too long):**
```
Please send me the report
```

#### Error Responses

**Unauthorized (401)**
```json
{
  "status": 401,
  "error": "Unauthorized"
}
```

**Bad Request (400)**
```json
{
  "status": 400,
  "error": "Error message"
}
```

**Method Not Allowed (405)**
```json
{
  "status": 405,
  "error": "Request method not supported",
  "details": "Request method 'GET' not supported"
}
```

---

## Enums Reference

### CommunicationPlatformType
Type of communication platform

| Value | Description |
|-------|-------------|
| TELEGRAM | Telegram messenger |
| EMAIL | Email platform |
| VIBER | Viber messenger |
| DISCORD | Discord platform |
| TEAMS | Microsoft Teams |

### RelationshipType
User relationship type with contact

| Value | Description |
|-------|-------------|
| SUPERVISOR | Supervisor |
| COLLEAGUE | Colleague |
| EMPLOYEE | Employee |
| CUSTOMER | Customer |
| LEAD | Lead |
| SUPPLIER | Supplier |
| BUSINESS_PARTNER | Business partner |
| INVESTOR | Investor |

### ToneStyleType
User tone style type with contact

| Value | Description |
|-------|-------------|
| PROFESSIONAL | Professional tone |
| FORMAL | Formal tone |
| CASUAL | Casual tone |
| FRIENDLY | Friendly tone |
| WARM | Warm tone |
| DIRECT | Direct tone |
| CONCISE | Concise tone |
| ENTHUSIASTIC | Enthusiastic tone |

---

## AI Personalization Rules

The AI message formatting service applies the following customization rules:

### Greeting Selection
- **Formality level ≥ 3** with preferred greeting → uses preferred greeting + contact name
- **Formality level ≤ 2** with preferred greeting → uses preferred greeting without contact name

### Message Body
- **Relationship type** adjusts phrasing (e.g., supervisor → respectful; colleague → casual)
- **Tone style** modifies word choice (e.g., warm → friendly language with mild emoticons; concise → brief, neutral)
- **Formality level** controls overall customization (1 = informal, 5 = very formal)

### Message Formatting
- **Messenger platforms** (TELEGRAM, VIBER, DISCORD) → single-line, chat-style messages
- **EMAIL** → traditional email format with paragraphs

### Message Endings
- **Formality level ≥ 4** → uses generic phrases (e.g., "Regards", "Respectfully") with user's full name
- **Formality level = 3** → uses signatures only for EMAIL platform
- **Formality level ≤ 2** → no signature

### Limitations
- Maximum message length: **500 characters**
- If no contact profile exists for the platform/chat combination, the original message is returned
- Empty messages are returned unchanged
- The service uses GPT-4o-mini for messages ≤200 characters, GPT-4.1-mini for longer messages
- Automatic retry and fallback mechanisms ensure availability

---

## Common Error Codes

| Status Code | Description | When It Occurs |
|-------------|-------------|----------------|
| 200 | OK | Request successful |
| 400 | Bad Request | Validation failed, invalid input data, or general errors |
| 401 | Unauthorized | Missing or invalid JWT token |
| 404 | Not Found | Contact profile with specified ID not found |
| 405 | Method Not Allowed | HTTP method not supported for the endpoint |
| 500 | Internal Server Error | Unexpected server error |

---

## Security

All endpoints require JWT Bearer token authentication. Include the token in the `Authorization` header:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

Tokens are obtained from the User Service authentication endpoints.

---

## Notes

- Contact profiles are automatically associated with the authenticated user (extracted from JWT token)
- The `userId` field in responses reflects the currently authenticated user
- Contact profiles are uniquely identified by the combination of `userId`, `platform`, and `chatIdentifier`
- The AI personalization service includes security measures to prevent prompt injection attacks
- All AI requests include automatic retries and model fallback for reliability
