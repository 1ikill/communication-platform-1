# Gmail Service API Documentation

## Overview
The Gmail Service provides endpoints for Gmail integration, including OAuth authentication, sending emails with attachments, reading messages, searching, marking as read/unread, and downloading attachments. The service enables users to connect their Gmail accounts and interact with emails through the communication platform.

**Base URLs:** 
- `/gmail` - Authentication and sending operations
- `/api/gmail` - Reading and attachment operations

**Authentication:** All endpoints require JWT Bearer token authentication.

---

## Table of Contents
1. [Authentication & Account Management](#authentication--account-management)
2. [Email Sending Operations](#email-sending-operations)
3. [Email Reading Operations](#email-reading-operations)
4. [Attachment Operations](#attachment-operations)
5. [Data Models](#data-models)
6. [Error Responses](#error-responses)

---

## Authentication & Account Management

### 1. Authenticate Gmail Account

**Endpoint:** `POST /gmail/auth`

**Description:** Authenticate gmail account

Initiates Gmail OAuth2 authentication flow. Upload Gmail API credentials file (downloaded from Google Cloud Console) to receive an authorization URL.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: multipart/form-data
```

#### Form Data Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| file | MultipartFile | Yes | Gmail API credentials JSON file from Google Cloud Console |

#### Request Example
```
POST /gmail/auth
Content-Type: multipart/form-data

file=<credentials.json>
```

#### Response

**Status Code:** `200 OK`

**Content-Type:** `text/plain`

**Response Body:** OAuth authorization URL string

**Response Example:**
```
https://accounts.google.com/o/oauth2/auth?client_id=...&redirect_uri=...&scope=...&response_type=code&state=user_123
```

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

#### Usage Flow
1. Upload credentials JSON file from Google Cloud Console
2. Receive authorization URL
3. Navigate user to the authorization URL
4. User grants permissions in Google OAuth consent screen
5. Google redirects back to callback endpoint with authorization code

---

### 2. OAuth Callback

**Endpoint:** `GET /gmail/oauth/google/callback`

**Description:** Callback for google cloud console

Handles the OAuth2 callback from Google after user authorization. This endpoint is called by Google's OAuth service.

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| code | String | Yes | Authorization code from Google OAuth |
| state | String | No | State parameter for security verification |

#### Request Example
```
GET /gmail/oauth/google/callback?code=4/0AY0e-g7X...&state=user_123
```

#### Response

**Status Code:** `200 OK`

**Content-Type:** `text/plain`

**Response Body:** Authenticated user's email address

**Response Example:**
```
user@gmail.com
```

#### Error Responses

**Bad Request (400)**
```json
{
  "status": 400,
  "error": "No refresh token returned. Re-authorize with prompt=consent."
}
```

```json
{
  "status": 400,
  "error": "Failed to refresh access token. Reauthorize Gmail account."
}
```

---

### 3. Get Connected Accounts

**Endpoint:** `GET /gmail/me`

**Description:** Get connected gmail accounts

Retrieves all Gmail accounts connected by the authenticated user.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Array of GmailAccountInfoDto

| Field Name | Type | Description |
|------------|------|-------------|
| accountId | Long | Account id |
| gmail | String | Gmail email address |

**Response Example:**
```json
[
  {
    "accountId": 1,
    "gmail": "user@gmail.com"
  },
  {
    "accountId": 2,
    "gmail": "work@gmail.com"
  }
]
```

#### Error Responses

**Unauthorized (401)**
```json
{
  "status": 401,
  "error": "Unauthorized"
}
```

---

## Email Sending Operations

### 4. Send Email

**Endpoint:** `POST /gmail/send`

**Description:** Send message

Sends an email message from the authenticated Gmail account.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| accountId | Long | Yes | Gmail account ID to send from |
| to | String | Yes | Recipient email address |
| subject | String | Yes | Email subject |
| body | String | Yes | Email body content |

#### Request Example
```
POST /gmail/send?accountId=1&to=recipient@example.com&subject=Hello&body=This is the email body
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

**Not Found (404)**
```json
{
  "status": 404,
  "error": "Account not found"
}
```

**Bad Request (400) - Token Expired**
```json
{
  "status": 400,
  "error": "Failed to refresh access token. Reauthorize Gmail account."
}
```

---

### 5. Send Email with File Attachment

**Endpoint:** `POST /gmail/send-file`

**Description:** Send message with file attachment

Sends an email message with a file attachment (image, video, or document).

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: multipart/form-data
```

#### Form Data Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| accountId | Long | Yes | Gmail account ID to send from |
| to | String | Yes | Recipient email address |
| subject | String | Yes | Email subject |
| body | String | Yes | Email body content |
| file | MultipartFile | Yes | File attachment (image, video, document) |

#### Request Example
```
POST /gmail/send-file
Content-Type: multipart/form-data

accountId=1
to=recipient@example.com
subject=Document attached
body=Please find the document attached
file=<document.pdf>
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Empty

#### Error Responses

Same as "Send Email" endpoint.

---

## Email Reading Operations

### 6. Get Messages

**Endpoint:** `GET /api/gmail/{accountId}/messages`

**Description:** Get list of emails

Retrieves a paginated list of email messages from the specified Gmail account.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| accountId | Long | Yes | Gmail account ID |

#### Query Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| maxResults | Integer | No | 20 | Maximum number of results per page |
| pageToken | String | No | - | Page token for pagination (from previous response) |

#### Request Example
```
GET /api/gmail/1/messages?maxResults=50&pageToken=token123
```

#### Response

**Status Code:** `200 OK`

**Response Body:** GmailMessagesResponseDto

| Field Name | Type | Description |
|------------|------|-------------|
| messages | Array | List of email messages |
| nextPageToken | String | Token for next page of results |
| resultSizeEstimate | Long | Estimated total number of results |

**Response Example:**
```json
{
  "messages": [
    {
      "id": "18c5e3f4a2b9d1e7",
      "threadId": "18c5e3f4a2b9d1e7",
      "labelIds": ["INBOX", "UNREAD"],
      "snippet": "This is a preview of the email content...",
      "internalDate": "2025-12-18T10:30:00.000Z",
      "from": "sender@example.com",
      "to": "user@gmail.com",
      "subject": "Important Meeting",
      "body": "Full email body content here...",
      "unread": true,
      "attachments": [
        {
          "attachmentId": "ANGjdJ8w...",
          "filename": "report.pdf",
          "mimeType": "application/pdf",
          "size": 245678
        }
      ]
    }
  ],
  "nextPageToken": "15429384756382947",
  "resultSizeEstimate": 150
}
```

#### Error Responses

**Bad Request (400)**
```json
{
  "status": 400,
  "error": "Failed to refresh access token. Reauthorization required."
}
```

**Not Found (404)**
```json
{
  "status": 404,
  "error": "Account not found"
}
```

---

### 7. Get Unread Messages

**Endpoint:** `GET /api/gmail/{accountId}/unread`

**Description:** Get unread emails

Retrieves a paginated list of unread email messages.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| accountId | Long | Yes | Gmail account ID |

#### Query Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| maxResults | Integer | No | 20 | Maximum number of results per page |
| pageToken | String | No | - | Page token for pagination |

#### Request Example
```
GET /api/gmail/1/unread?maxResults=30
```

#### Response

**Status Code:** `200 OK`

**Response Body:** GmailMessagesResponseDto (see structure in endpoint #6)

**Response Example:**
```json
{
  "messages": [
    {
      "id": "18c5e3f4a2b9d1e7",
      "threadId": "18c5e3f4a2b9d1e7",
      "labelIds": ["INBOX", "UNREAD"],
      "snippet": "Unread email preview...",
      "internalDate": "2025-12-18T10:30:00.000Z",
      "from": "sender@example.com",
      "to": "user@gmail.com",
      "subject": "Action Required",
      "body": "Email body...",
      "unread": true,
      "attachments": []
    }
  ],
  "nextPageToken": "15429384756382948",
  "resultSizeEstimate": 45
}
```

#### Error Responses

Same as "Get Messages" endpoint.

---

### 8. Search Messages

**Endpoint:** `GET /api/gmail/{accountId}/search`

**Description:** Search emails by query

Searches for emails matching the specified query using Gmail search syntax.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| accountId | Long | Yes | Gmail account ID |

#### Query Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| query | String | Yes | - | Gmail search query |
| maxResults | Integer | No | 20 | Maximum number of results per page |
| pageToken | String | No | - | Page token for pagination |

#### Gmail Search Query Examples

| Query | Description |
|-------|-------------|
| `from:example@gmail.com` | Emails from specific sender |
| `to:recipient@gmail.com` | Emails to specific recipient |
| `subject:important` | Emails with "important" in subject |
| `has:attachment` | Emails with attachments |
| `is:unread` | Unread emails |
| `after:2025/12/01` | Emails after specific date |
| `filename:pdf` | Emails with PDF attachments |

#### Request Example
```
GET /api/gmail/1/search?query=from:boss@company.com subject:urgent&maxResults=10
```

#### Response

**Status Code:** `200 OK`

**Response Body:** GmailMessagesResponseDto (see structure in endpoint #6)

#### Error Responses

Same as "Get Messages" endpoint.

---

### 9. Mark Email as Read

**Endpoint:** `POST /api/gmail/{accountId}/read/{messageId}`

**Description:** Mark email as read

Marks a specific email message as read.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| accountId | Long | Yes | Gmail account ID |
| messageId | String | Yes | Gmail message ID |

#### Request Example
```
POST /api/gmail/1/read/18c5e3f4a2b9d1e7
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Empty

#### Error Responses

**Bad Request (400)**
```json
{
  "status": 400,
  "error": "Failed to refresh access token. Reauthorization required."
}
```

**Not Found (404)**
```json
{
  "status": 404,
  "error": "Message not found"
}
```

---

### 10. Mark Email as Unread

**Endpoint:** `POST /api/gmail/{accountId}/unread/{messageId}`

**Description:** Mark email as unread

Marks a specific email message as unread.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| accountId | Long | Yes | Gmail account ID |
| messageId | String | Yes | Gmail message ID |

#### Request Example
```
POST /api/gmail/1/unread/18c5e3f4a2b9d1e7
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Empty

#### Error Responses

Same as "Mark Email as Read" endpoint.

---

## Attachment Operations

### 11. Download Image Attachment

**Endpoint:** `GET /api/gmail/{accountId}/attachment/{attachmentId}/image`

**Description:** Download image attachment

Downloads an image attachment from an email message.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| accountId | Long | Yes | Gmail account ID |
| attachmentId | String | Yes | Attachment ID from message |

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| messageId | String | Yes | Gmail message ID containing the attachment |

#### Request Example
```
GET /api/gmail/1/attachment/ANGjdJ8w.../image?messageId=18c5e3f4a2b9d1e7
```

#### Response

**Status Code:** `200 OK`

**Content-Type:** `image/jpeg`

**Response Body:** Binary image data (StreamingResponseBody)

#### Error Responses

**Bad Request (400)**
```json
{
  "status": 400,
  "error": "Failed to download image attachment"
}
```

**Bad Request (400) - Token Expired**
```json
{
  "status": 400,
  "error": "Failed to refresh access token. Reauthorization required."
}
```

---

### 12. Download Video Attachment

**Endpoint:** `GET /api/gmail/{accountId}/attachment/{attachmentId}/video`

**Description:** Download video attachment

Downloads a video attachment from an email message.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| accountId | Long | Yes | Gmail account ID |
| attachmentId | String | Yes | Attachment ID from message |

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| messageId | String | Yes | Gmail message ID containing the attachment |

#### Request Example
```
GET /api/gmail/1/attachment/ANGjdJ8w.../video?messageId=18c5e3f4a2b9d1e7
```

#### Response

**Status Code:** `200 OK`

**Content-Type:** `application/octet-stream`

**Response Body:** Binary video data (StreamingResponseBody)

#### Error Responses

**Bad Request (400)**
```json
{
  "status": 400,
  "error": "Failed to download video attachment"
}
```

**Bad Request (400) - Token Expired**
```json
{
  "status": 400,
  "error": "Failed to refresh access token. Reauthorization required."
}
```

---

### 13. Download Document Attachment

**Endpoint:** `GET /api/gmail/{accountId}/attachment/{attachmentId}/document`

**Description:** Download document attachment

Downloads a document attachment from an email message.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| accountId | Long | Yes | Gmail account ID |
| attachmentId | String | Yes | Attachment ID from message |

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| messageId | String | Yes | Gmail message ID containing the attachment |

#### Request Example
```
GET /api/gmail/1/attachment/ANGjdJ8w.../document?messageId=18c5e3f4a2b9d1e7
```

#### Response

**Status Code:** `200 OK`

**Content-Type:** `application/octet-stream`

**Response Body:** Binary document data (StreamingResponseBody)

#### Error Responses

**Bad Request (400)**
```json
{
  "status": 400,
  "error": "Failed to download document attachment"
}
```

**Bad Request (400) - Token Expired**
```json
{
  "status": 400,
  "error": "Failed to refresh access token. Reauthorization required."
}
```

---

## Data Models

### GmailAccountInfoDto
Gmail account information DTO.

| Field | Type | Description |
|-------|------|-------------|
| accountId | Long | Account ID in the platform |
| gmail | String | Gmail email address |

### GmailMessagesResponseDto
Response containing paginated email messages.

| Field | Type | Description |
|-------|------|-------------|
| messages | Array | List of GmailMessageDto |
| nextPageToken | String | Token for retrieving next page |
| resultSizeEstimate | Long | Estimated total number of matching emails |

### GmailMessageDto
Individual email message DTO.

| Field | Type | Description |
|-------|------|-------------|
| id | String | Gmail message ID |
| threadId | String | Gmail thread ID |
| labelIds | Array | List of label IDs (e.g., "INBOX", "UNREAD") |
| snippet | String | Short preview of email content |
| internalDate | Date | Internal Gmail timestamp |
| from | String | Sender email address |
| to | String | Recipient email address |
| subject | String | Email subject |
| body | String | Full email body content |
| unread | Boolean | Whether email is unread |
| attachments | Array | List of AttachmentDto |

### AttachmentDto
Email attachment information DTO.

| Field | Type | Description |
|-------|------|-------------|
| attachmentId | String | Gmail attachment ID |
| filename | String | Original filename |
| mimeType | String | MIME type (e.g., "application/pdf", "image/jpeg") |
| size | Long | File size in bytes |

---

## Error Responses

### Common HTTP Status Codes

| Status Code | Description | When It Occurs |
|-------------|-------------|----------------|
| 200 | OK | Request successful |
| 400 | Bad Request | Invalid input, OAuth error, or token expired |
| 401 | Unauthorized | Missing or invalid JWT token |
| 404 | Not Found | Account or message not found |
| 405 | Method Not Allowed | HTTP method not supported for the endpoint |
| 500 | Internal Server Error | Unexpected server error |

### Error Response Format

**Validation Error (400)**
```json
{
  "status": 400,
  "errors": {
    "fieldName": "error message"
  }
}
```

**General Error (400, 404, 500)**
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

### Common Error Messages

| Error Message | Status | Meaning |
|--------------|--------|---------|
| `No refresh token returned. Re-authorize with prompt=consent.` | 400 | OAuth refresh token not provided by Google |
| `Failed to refresh access token. Reauthorize Gmail account.` | 400 | OAuth access token expired and refresh failed |
| `Failed to refresh access token. Reauthorization required.` | 400 | OAuth token invalid, need to re-authenticate |
| `Failed to download image attachment` | 400 | Error downloading image from Gmail |
| `Failed to download video attachment` | 400 | Error downloading video from Gmail |
| `Failed to download document attachment` | 400 | Error downloading document from Gmail |
| `Account not found` | 404 | Gmail account ID doesn't exist |
| `Message not found` | 404 | Gmail message ID doesn't exist |

---

## OAuth 2.0 Authentication Flow

### Setup Requirements

1. **Create Google Cloud Project**
   - Go to [Google Cloud Console](https://console.cloud.google.com/)
   - Create a new project or select existing

2. **Enable Gmail API**
   - Navigate to "APIs & Services" → "Library"
   - Search for "Gmail API"
   - Click "Enable"

3. **Configure OAuth Consent Screen**
   - Go to "APIs & Services" → "OAuth consent screen"
   - Configure application name, support email, authorized domains
   - Add scopes: `https://www.googleapis.com/auth/gmail.readonly`, `https://www.googleapis.com/auth/gmail.send`

4. **Create OAuth 2.0 Credentials**
   - Go to "APIs & Services" → "Credentials"
   - Click "Create Credentials" → "OAuth client ID"
   - Application type: Web application
   - Add authorized redirect URI: `{YOUR_APP_URL}/gmail/oauth/google/callback`
   - Download credentials JSON file

### Authentication Workflow

```
1. User initiates Gmail connection
   ↓
2. Upload credentials.json to POST /gmail/auth
   ↓
3. Receive authorization URL
   ↓
4. Redirect user to Google OAuth consent screen
   ↓
5. User grants permissions
   ↓
6. Google redirects to GET /gmail/oauth/google/callback?code=...
   ↓
7. Service exchanges code for access & refresh tokens
   ↓
8. Tokens stored encrypted in database
   ↓
9. User's Gmail account is connected (returns email address)
   ↓
10. Use accountId for all subsequent API calls
```

### Token Management

- **Access Token:** Short-lived (typically 1 hour), used for API requests
- **Refresh Token:** Long-lived, used to obtain new access tokens
- **Automatic Refresh:** Service automatically refreshes expired access tokens
- **Re-authorization:** If refresh fails, user must re-authenticate using OAuth flow

---

## Gmail Search Syntax

The search endpoint supports Gmail's powerful search operators:

### Basic Search Operators

| Operator | Example | Description |
|----------|---------|-------------|
| `from:` | `from:user@example.com` | Emails from specific sender |
| `to:` | `to:recipient@example.com` | Emails to specific recipient |
| `subject:` | `subject:meeting` | Emails with word in subject |
| `has:attachment` | `has:attachment` | Emails with any attachment |
| `filename:` | `filename:pdf` | Emails with specific file type |
| `is:unread` | `is:unread` | Unread emails only |
| `is:read` | `is:read` | Read emails only |
| `is:starred` | `is:starred` | Starred emails |
| `is:important` | `is:important` | Important emails |

### Date Operators

| Operator | Example | Description |
|----------|---------|-------------|
| `after:` | `after:2025/12/01` | After specific date |
| `before:` | `before:2025/12/31` | Before specific date |
| `newer_than:` | `newer_than:7d` | Newer than X days/months |
| `older_than:` | `older_than:1m` | Older than X days/months |

### Combining Operators

Use spaces to combine multiple search criteria:
```
from:boss@company.com subject:urgent after:2025/12/01
```

Use OR for alternatives:
```
from:user1@example.com OR from:user2@example.com
```

Use quotes for exact phrases:
```
subject:"quarterly report"
```

---

## Security

All endpoints require JWT Bearer token authentication. Include the token in the `Authorization` header:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

Tokens are obtained from the User Service authentication endpoints.

---

## Notes

- **OAuth Credentials:** Stored encrypted in the database
- **Token Refresh:** Automatic refresh of expired access tokens
- **Rate Limiting:** Gmail API quotas apply (10,000 requests per project per day by default)
- **Pagination:** Use `nextPageToken` for retrieving subsequent pages
- **Label IDs:** Standard Gmail labels include "INBOX", "SENT", "DRAFT", "SPAM", "TRASH", "UNREAD", "STARRED"
- **Attachment Size:** Large attachments are streamed to prevent memory issues
- **Search Performance:** Complex searches may take longer to execute
- **Thread ID:** Multiple related emails share the same thread ID
- **Internal Date:** Gmail's internal timestamp, may differ from email headers
- **Snippet:** Auto-generated preview, typically first 200 characters
- **Re-authorization:** Required when refresh tokens expire or are revoked
- **Scopes Required:** 
  - `https://www.googleapis.com/auth/gmail.readonly` - Read emails
  - `https://www.googleapis.com/auth/gmail.send` - Send emails
