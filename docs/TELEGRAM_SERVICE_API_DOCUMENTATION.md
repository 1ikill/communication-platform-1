# Telegram Service API Documentation

## Overview
The Telegram Service provides comprehensive integration with Telegram using TDLib (Telegram Database Library). It manages multiple Telegram accounts, handles authentication, messaging, file transfers, and chat management.

**Authentication:** All endpoints require JWT Bearer token authentication.

---

## Table of Contents
1. [Authentication Endpoints](#authentication-endpoints)
2. [Credentials Management](#credentials-management)
3. [Account Management](#account-management)
4. [Chat Management](#chat-management)
5. [Message Operations](#message-operations)
6. [File Operations](#file-operations)
7. [Notification Management](#notification-management)
8. [Data Models](#data-models)
9. [Error Responses](#error-responses)

---

## Authentication Endpoints

### 1. Submit Phone Number

**Endpoint:** `POST /auth/login/phone`

**Description:** Phone number submission for account auth

Submits a phone number to initiate the Telegram authentication process. This is the first step in authenticating a Telegram account.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| phone | String | Yes | Phone number in international format (e.g., +1234567890) |
| accountId | String | Yes | Unique account identifier |

#### Request Example
```
POST /auth/login/phone?phone=%2B1234567890&accountId=telegram_account_1
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Empty

#### Error Responses

**Bad Request (400)**
```json
{
  "status": 400,
  "error": "Invalid accountId: telegram_account_1"
}
```

**Bad Request (400)**
```json
{
  "status": 400,
  "error": "Error sending phone: [error details]"
}
```

---

### 2. Submit Authentication Code

**Endpoint:** `POST /auth/login/code`

**Description:** Telegram code submission for account auth

Submits the authentication code received via SMS or Telegram app. This is the second step in the authentication process.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| code | String | Yes | Authentication code received via SMS/app |
| accountId | String | Yes | Unique account identifier |

#### Request Example
```
POST /auth/login/code?code=12345&accountId=telegram_account_1
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Empty

#### Error Responses

**Bad Request (400)**
```json
{
  "status": 400,
  "error": "Error sending code: [error details]"
}
```

---

### 3. Submit 2FA Password

**Endpoint:** `POST /auth/login/password`

**Description:** Telegram 2Auth password submission for account auth

Submits the two-factor authentication password if the account has 2FA enabled.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| password | String | Yes | Two-factor authentication password |
| accountId | String | Yes | Unique account identifier |

#### Request Example
```
POST /auth/login/password?password=mySecurePassword&accountId=telegram_account_1
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Empty

#### Error Responses

**Bad Request (400)**
```json
{
  "status": 400,
  "error": "Error sending password: [error details]"
}
```

---

### 4. Get Authorization State

**Endpoint:** `GET /auth/state`

**Description:** Get account authorization state

Retrieves the current authentication state of a Telegram account.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| accountId | String | Yes | Unique account identifier |

#### Request Example
```
GET /auth/state?accountId=telegram_account_1
```

#### Response

**Status Code:** `200 OK`

**Response Body:**

**AuthorizationStateReadyDto:**
```json
{
  "type": "READY"
}
```

**AuthorizationStateWaitPhoneNumberDto:**
```json
{
  "type": "WAIT_PHONE_NUMBER"
}
```

**AuthorizationStateWaitCodeDto:**
```json
{
  "type": "WAIT_CODE"
}
```

**AuthorizationStateWaitPasswordDto:**
```json
{
  "type": "WAIT_PASSWORD"
}
```

#### Error Responses

**Bad Request (400)**
```json
{
  "status": 400,
  "error": "Invalid accountId: telegram_account_1"
}
```

---

### 5. Logout

**Endpoint:** `POST /auth/logout`

**Description:** Logout

Logs out from a Telegram account and clears the session.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| accountId | String | Yes | Unique account identifier |

#### Request Example
```
POST /auth/logout?accountId=telegram_account_1
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Empty

---

## Credentials Management

### 6. Add Telegram Credentials

**Endpoint:** `POST /telegram-credentials/add`

**Description:** Add telegram credentials

Registers new Telegram API credentials for an account. You need to obtain API credentials from https://my.telegram.org/apps.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

#### Request Body

| Field Name | Type | Required | Description |
|------------|------|----------|-------------|
| apiId | String | Yes | Telegram API ID from my.telegram.org |
| apiHash | String | Yes | Telegram API Hash from my.telegram.org |
| accountId | String | Yes | Unique account identifier |
| accountName | String | Yes | Human-readable account name |
| phoneNumber | String | Yes | Phone number in international format |

#### Request Example
```json
{
  "apiId": "12345678",
  "apiHash": "abcdef1234567890abcdef1234567890",
  "accountId": "telegram_account_1",
  "accountName": "My Work Account",
  "phoneNumber": "+1234567890"
}
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Empty

#### Error Responses

**Internal Server Error (500)**
```json
{
  "status": 500,
  "error": "Exception details"
}
```

---

## Account Management

### 7. Get Account Info

**Endpoint:** `GET /telegram/account`

**Description:** Get connected account info

Retrieves detailed information about a connected Telegram account.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| accountId | String | Yes | Unique account identifier |

#### Request Example
```
GET /telegram/account?accountId=telegram_account_1
```

#### Response

**Status Code:** `200 OK`

**Response Body:**
```json
{
  "id": 123456789,
  "accountId": "telegram_account_1",
  "firstName": "John",
  "lastName": "Doe",
  "usernames": {
    "activeUsernames": ["johndoe"],
    "disabledUsernames": [],
    "editableUsername": "johndoe"
  },
  "phoneNumber": "+1234567890",
  "status": {
    "type": "ONLINE"
  },
  "profilePhoto": {
    "id": "987654321",
    "small": {
      "remoteId": "AQADAgATsomeRemoteId"
    },
    "big": {
      "remoteId": "AQADAgATsomeRemoteId"
    }
  },
  "isContact": false,
  "isMutualContact": false,
  "isCloseFriend": false,
  "isPremium": true,
  "isSupport": false,
  "haveAccess": true,
  "type": {
    "type": "REGULAR"
  },
  "languageCode": "en"
}
```

---

### 8. Get All Connected Accounts

**Endpoint:** `GET /telegram/accounts`

**Description:** Get all connected accounts list

Retrieves a list of all connected Telegram accounts.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Request Example
```
GET /telegram/accounts
```

#### Response

**Status Code:** `200 OK`

**Response Body:**
```json
[
  {
    "accountId": "telegram_account_1",
    "accountName": "My Work Account",
    "photoRemoteId": "AQADAgATsomeRemoteId"
  },
  {
    "accountId": "telegram_account_2",
    "accountName": "Personal Account",
    "photoRemoteId": null
  }
]
```

---

## Chat Management

### 9. Get Main Chat List

**Endpoint:** `GET /telegram/main`

**Description:** Get Main chat list chats

Retrieves chats from the main chat list.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| limit | Integer | Yes | Maximum number of chats to retrieve |
| accountId | String | Yes | Unique account identifier |

#### Request Example
```
GET /telegram/main?limit=20&accountId=telegram_account_1
```

#### Response

**Status Code:** `200 OK`

**Response Body:**
```json
[
  {
    "id": 123456789,
    "type": {
      "type": "PRIVATE",
      "userId": 987654321
    },
    "title": "John Doe",
    "permissions": {
      "canSendBasicMessages": true,
      "canSendAudios": true,
      "canSendDocuments": true,
      "canSendPhotos": true,
      "canSendVideos": true,
      "canSendVideoNotes": true,
      "canSendVoiceNotes": true,
      "canSendPolls": true,
      "canSendOtherMessages": true,
      "canAddWebPagePreviews": true,
      "canChangeInfo": false,
      "canInviteUsers": false,
      "canPinMessages": false,
      "canManageTopics": false
    },
    "lastMessage": {
      "id": 1234567890,
      "senderId": {
        "type": "USER",
        "userId": 987654321
      },
      "chatId": 123456789,
      "isOutgoing": false,
      "date": 1702905600,
      "content": {
        "type": "TEXT",
        "text": "Hello! How are you?"
      }
    },
    "unreadCount": 2,
    "lastReadInboxMessageId": 1234567888,
    "lastReadOutboxMessageId": 1234567887,
    "isMarkedAsUnread": false,
    "hasScheduledMessages": false,
    "profilePhotoUrl": "AQADAgATsomeRemoteId"
  }
]
```

---

### 10. Get Chat Folders

**Endpoint:** `GET /telegram/chats/folders`

**Description:** Get all chat folders

Retrieves all custom chat folders configured in Telegram.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| accountId | String | Yes | Unique account identifier |

#### Request Example
```
GET /telegram/chats/folders?accountId=telegram_account_1
```

#### Response

**Status Code:** `200 OK`

**Response Body:**
```json
[
  {
    "folderName": "Work",
    "folderId": 1
  },
  {
    "folderName": "Friends",
    "folderId": 2
  }
]
```

---

### 11. Get Chats in Folder

**Endpoint:** `GET /telegram/folder`

**Description:** Find all chats in chat folder

Retrieves all chats from a specific custom folder.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| limit | Integer | Yes | Maximum number of chats to retrieve |
| folderId | Integer | Yes | Folder ID from folders endpoint |
| accountId | String | Yes | Unique account identifier |

#### Request Example
```
GET /telegram/folder?limit=20&folderId=1&accountId=telegram_account_1
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Same structure as "Get Main Chat List" endpoint.

---

### 12. Get Archive Chats

**Endpoint:** `GET /telegram/archive`

**Description:** Find all chats in archive

Retrieves all archived chats.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| limit | Integer | Yes | Maximum number of chats to retrieve |
| accountId | String | Yes | Unique account identifier |

#### Request Example
```
GET /telegram/archive?limit=20&accountId=telegram_account_1
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Same structure as "Get Main Chat List" endpoint.

---

### 13. Get User Chat ID

**Endpoint:** `GET /telegram/user-chat`

**Description:** Get user chat-id

Finds a user's chat ID by their username.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| username | String | Yes | Telegram username (without @) |
| accountId | String | Yes | Unique account identifier |

#### Request Example
```
GET /telegram/user-chat?username=johndoe&accountId=telegram_account_1
```

#### Response

**Status Code:** `200 OK`

**Response Body:**
```json
123456789
```

---

### 14. Create Chat with User

**Endpoint:** `POST /telegram/create-chat/{userId}`

**Description:** Create chat with user

Creates or retrieves a private chat with a specific user.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| userId | Long | Yes | Telegram user ID |

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| accountId | String | Yes | Unique account identifier |

#### Request Example
```
POST /telegram/create-chat/987654321?accountId=telegram_account_1
```

#### Response

**Status Code:** `200 OK`

**Response Body:**
```json
123456789
```

---

### 15. Create Empty Chat

**Endpoint:** `POST /telegram/empty-chats`

**Description:** Create empty chat

Creates an empty chat entry.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| chatId | Long | Yes | Chat ID |
| accountId | String | Yes | Unique account identifier |

#### Request Example
```
POST /telegram/empty-chats?chatId=123456789&accountId=telegram_account_1
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Empty

---

### 16. Delete Empty Chat

**Endpoint:** `DELETE /telegram/empty-chats`

**Description:** Delete empty chat

Deletes an empty chat entry.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| chatId | Long | Yes | Chat ID |
| accountId | String | Yes | Unique account identifier |

#### Request Example
```
DELETE /telegram/empty-chats?chatId=123456789&accountId=telegram_account_1
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Empty

---

## Message Operations

### 17. Get All Messages in Chat

**Endpoint:** `GET /telegram/{chatId}/messages`

**Description:** Find all messages in chat

Retrieves messages from a specific chat.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| chatId | Long | Yes | Chat ID |

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| limit | Integer | Yes | Maximum number of messages to retrieve |
| accountId | String | Yes | Unique account identifier |

#### Request Example
```
GET /telegram/123456789/messages?limit=50&accountId=telegram_account_1
```

#### Response

**Status Code:** `200 OK`

**Response Body:**
```json
[
  {
    "id": 1234567890,
    "senderId": {
      "type": "USER",
      "userId": 987654321
    },
    "chatId": 123456789,
    "isOutgoing": false,
    "isRead": true,
    "isPinned": false,
    "date": 1702905600,
    "content": {
      "type": "TEXT",
      "text": "Hello! How are you?"
    }
  },
  {
    "id": 1234567891,
    "senderId": {
      "type": "USER",
      "userId": 111111111
    },
    "chatId": 123456789,
    "isOutgoing": true,
    "isRead": true,
    "isPinned": false,
    "date": 1702905650,
    "content": {
      "type": "TEXT",
      "text": "I'm doing great, thanks!"
    }
  }
]
```

---

### 18. Get Single Message

**Endpoint:** `GET /telegram/{chatId}/message`

**Description:** Find message in chat

Retrieves a specific message by its ID.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| chatId | Long | Yes | Chat ID |

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| messageId | Long | Yes | Message ID |
| accountId | String | Yes | Unique account identifier |

#### Request Example
```
GET /telegram/123456789/message?messageId=1234567890&accountId=telegram_account_1
```

#### Response

**Status Code:** `200 OK`

**Response Body:**
```json
{
  "id": 1234567890,
  "senderId": {
    "type": "USER",
    "userId": 987654321
  },
  "chatId": 123456789,
  "isOutgoing": false,
  "isRead": true,
  "isPinned": false,
  "date": 1702905600,
  "editDate": 0,
  "content": {
    "type": "TEXT",
    "text": "Hello! How are you?"
  }
}
```

---

### 19. Send Text Message

**Endpoint:** `POST /telegram/text`

**Description:** Send text message

Sends a text message to a Telegram chat.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| chatId | Long | Yes | Chat ID |
| messageText | String | Yes | Text content of the message |
| accountId | String | Yes | Unique account identifier |

#### Request Example
```
POST /telegram/text?chatId=123456789&messageText=Hello%20World&accountId=telegram_account_1
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Empty

#### Error Responses

**Internal Server Error (500)**
```json
{
  "status": 500,
  "error": "RuntimeException details"
}
```

---

### 20. Send Image Message

**Endpoint:** `POST /telegram/image`

**Description:** Send image message

Sends an image with optional caption to a Telegram chat.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: multipart/form-data
```

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| chatId | Long | Yes | Chat ID |
| accountId | String | Yes | Unique account identifier |
| message | String | No | Caption for the image |

#### Form Data

| Field Name | Type | Required | Description |
|------------|------|----------|-------------|
| image | File | Yes | Image file (JPEG, PNG, etc.) |

#### Request Example
```
POST /telegram/image?chatId=123456789&accountId=telegram_account_1&message=Check%20this%20out
Content-Type: multipart/form-data

--boundary
Content-Disposition: form-data; name="image"; filename="photo.jpg"
Content-Type: image/jpeg

[binary image data]
--boundary--
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Empty

#### Error Responses

**Internal Server Error (500)**
```json
{
  "status": 500,
  "error": "Failed to process image"
}
```

---

### 21. Send Video Message

**Endpoint:** `POST /telegram/videos`

**Description:** Send video file message

Sends a video file with optional caption to a Telegram chat.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: multipart/form-data
```

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| chatId | Long | Yes | Chat ID |
| accountId | String | Yes | Unique account identifier |
| message | String | No | Caption for the video |

#### Form Data

| Field Name | Type | Required | Description |
|------------|------|----------|-------------|
| video | File | Yes | Video file (MP4, MOV, etc.) |

#### Request Example
```
POST /telegram/videos?chatId=123456789&accountId=telegram_account_1&message=My%20video
Content-Type: multipart/form-data

--boundary
Content-Disposition: form-data; name="video"; filename="video.mp4"
Content-Type: video/mp4

[binary video data]
--boundary--
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Empty

#### Error Responses

**Internal Server Error (500)**
```json
{
  "status": 500,
  "error": "Failed to process video"
}
```

---

### 22. Send Document Message

**Endpoint:** `POST /telegram/documents`

**Description:** Send document message

Sends a document file with optional caption to a Telegram chat.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: multipart/form-data
```

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| chatId | Long | Yes | Chat ID |
| accountId | String | Yes | Unique account identifier |
| message | String | No | Caption for the document |

#### Form Data

| Field Name | Type | Required | Description |
|------------|------|----------|-------------|
| document | File | Yes | Document file (PDF, DOC, etc.) |

#### Request Example
```
POST /telegram/documents?chatId=123456789&accountId=telegram_account_1&message=Important%20document
Content-Type: multipart/form-data

--boundary
Content-Disposition: form-data; name="document"; filename="report.pdf"
Content-Type: application/pdf

[binary document data]
--boundary--
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Empty

#### Error Responses

**Internal Server Error (500)**
```json
{
  "status": 500,
  "error": "Failed to process document"
}
```

---

## File Operations

### 23. Get Image

**Endpoint:** `GET /telegram/file/{remoteId}`

**Description:** Get image

Downloads an image file from Telegram by its remote ID.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| remoteId | String | Yes | Remote file ID from Telegram |

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| accountId | String | Yes | Unique account identifier |

#### Request Example
```
GET /telegram/file/AQADAgATsomeRemoteId?accountId=telegram_account_1
```

#### Response

**Status Code:** `200 OK`

**Content-Type:** `image/jpeg`

**Response Body:** Binary image data

#### Error Responses

**Not Found (404)**
```json
{
  "status": 404,
  "error": "File still not downloaded after waiting"
}
```

---

### 24. Get Video

**Endpoint:** `GET /telegram/video/{remoteId}`

**Description:** Get video

Downloads a video file from Telegram by its remote ID.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| remoteId | String | Yes | Remote file ID from Telegram |

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| accountId | String | Yes | Unique account identifier |

#### Request Example
```
GET /telegram/video/AQADAgATsomeRemoteId?accountId=telegram_account_1
```

#### Response

**Status Code:** `200 OK`

**Content-Type:** `application/octet-stream`

**Response Body:** Streaming video data

#### Error Responses

**Not Found (404)**
```json
{
  "status": 404,
  "error": "File still not downloaded after waiting"
}
```

---

### 25. Get Document

**Endpoint:** `GET /telegram/document/{remoteId}`

**Description:** Get document

Downloads a document file from Telegram by its remote ID.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| remoteId | String | Yes | Remote file ID from Telegram |

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| accountId | String | Yes | Unique account identifier |

#### Request Example
```
GET /telegram/document/AQADAgATsomeRemoteId?accountId=telegram_account_1
```

#### Response

**Status Code:** `200 OK`

**Content-Type:** `application/octet-stream`

**Response Body:** Streaming document data

#### Error Responses

**Not Found (404)**
```json
{
  "status": 404,
  "error": "File still not downloaded after waiting"
}
```

---

### 26. Set Profile Image

**Endpoint:** `POST /telegram/profile/images`

**Description:** Change profile photo

Updates the profile photo for a Telegram account.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: multipart/form-data
```

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| accountId | String | Yes | Unique account identifier |

#### Form Data

| Field Name | Type | Required | Description |
|------------|------|----------|-------------|
| image | File | Yes | Profile image file (JPEG, PNG) |

#### Request Example
```
POST /telegram/profile/images?accountId=telegram_account_1
Content-Type: multipart/form-data

--boundary
Content-Disposition: form-data; name="image"; filename="profile.jpg"
Content-Type: image/jpeg

[binary image data]
--boundary--
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Empty

#### Error Responses

**Internal Server Error (500)**
```json
{
  "status": 500,
  "error": "Failed to process image"
}
```

---

## Notification Management

### 27. Get Chat Notifications

**Endpoint:** `GET /telegram/notifications`

**Description:** Get chats notifications

Retrieves notification counts for all chats with unread messages.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| accountId | String | Yes | Unique account identifier |

#### Request Example
```
GET /telegram/notifications?accountId=telegram_account_1
```

#### Response

**Status Code:** `200 OK`

**Response Body:**
```json
[
  {
    "accountId": "telegram_account_1",
    "chatId": 123456789,
    "notificationsCount": 5
  },
  {
    "accountId": "telegram_account_1",
    "chatId": 987654321,
    "notificationsCount": 2
  }
]
```

---

## Data Models

### TelegramCredentialsCreateDto
DTO for creating new Telegram credentials.

| Field | Type | Description |
|-------|------|-------------|
| apiId | String | Telegram API ID from my.telegram.org |
| apiHash | String | Telegram API Hash from my.telegram.org |
| accountId | String | Unique account identifier |
| accountName | String | Human-readable account name |
| phoneNumber | String | Phone number in international format |

---

### TelegramAccountDto
DTO representing a connected Telegram account.

| Field | Type | Description |
|-------|------|-------------|
| accountId | String | Account identifier |
| accountName | String | Account name |
| photoRemoteId | String | Profile photo remote ID |

---

### TelegramNotificationDto
DTO representing chat notification with unread count.

| Field | Type | Description |
|-------|------|-------------|
| accountId | String | Account identifier |
| chatId | Long | Chat ID |
| notificationsCount | Integer | Number of unread messages |

---

### UserTdlibDto
DTO representing a Telegram user.

| Field | Type | Description |
|-------|------|-------------|
| id | Long | User ID |
| accountId | String | Account identifier |
| firstName | String | First name |
| lastName | String | Last name |
| usernames | UsernamesDto | Active and disabled usernames |
| phoneNumber | String | Phone number |
| status | UserStatusTdlib | Online/offline status |
| profilePhoto | ProfilePhotoDto | Profile photo information |
| isContact | Boolean | Is in contacts |
| isMutualContact | Boolean | Is mutual contact |
| isCloseFriend | Boolean | Is close friend |
| isPremium | Boolean | Has Telegram Premium |
| isSupport | Boolean | Is support account |
| haveAccess | Boolean | Have access to this user |
| type | UserTypeTdlib | User type (regular, bot, etc.) |
| languageCode | String | Language code |

---

### ChatTdlibDto
DTO representing a Telegram chat.

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Chat ID |
| type | ChatTypeTdlib | Chat type (private, group, supergroup, channel) |
| title | String | Chat title |
| permissions | ChatPermissionsDto | Chat permissions |
| lastMessage | MessageTdlibDto | Last message in chat |
| positions | List | Chat positions in various lists |
| chatLists | List | Lists containing this chat |
| messageSenderId | MessageSenderTdlib | Default message sender |
| isMarkedAsUnread | Boolean | Marked as unread |
| hasScheduledMessages | Boolean | Has scheduled messages |
| canBeDeletedForAllUsers | Boolean | Can be deleted for all |
| defaultDisableNotification | Boolean | Disable notifications by default |
| unreadCount | Integer | Number of unread messages |
| lastReadInboxMessageId | Long | Last read incoming message ID |
| lastReadOutboxMessageId | Long | Last read outgoing message ID |
| unreadMentionCount | Integer | Unread mentions count |
| unreadReactionCount | Integer | Unread reactions count |
| notificationSettings | ChatNotificationSettingsDto | Notification settings |
| messageAutoDeleteTime | Integer | Auto-delete timer (seconds) |
| pendingJoinRequests | ChatJoinRequestsInfoDto | Pending join requests |
| replyMarkupMessageId | Long | Reply markup message ID |
| clientData | String | Client-specific data |
| profilePhotoUrl | String | Profile photo remote ID |

---

### MessageTdlibDto
DTO representing a Telegram message.

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Message ID |
| senderId | MessageSenderTdlib | Message sender |
| chatId | Long | Chat ID |
| sendingState | MessageSendingStateTdlib | Sending state |
| isOutgoing | Boolean | Is outgoing message |
| isRead | Boolean | Is read |
| isPinned | Boolean | Is pinned |
| isFromOffline | Boolean | Sent from offline |
| canBeSaved | Boolean | Can be saved |
| hasTimestampedMedia | Boolean | Has timestamped media |
| isChannelPost | Boolean | Is channel post |
| isTopicMessage | Boolean | Is topic message |
| containsUnreadMention | Boolean | Contains unread mention |
| date | Integer | Send date (Unix timestamp) |
| editDate | Integer | Edit date (Unix timestamp) |
| forwardInfo | MessageForwardInfoDto | Forward information |
| importInfo | MessageImportInfoDto | Import information |
| interactionInfo | MessageInteractionInfoDto | Views, forwards, replies |
| replyTo | MessageReplyToTdlib | Reply information |
| messageThreadId | Long | Message thread ID |
| savedMessagesTopicId | Long | Saved messages topic ID |
| selfDestructIn | Double | Self-destruct timer |
| autoDeleteIn | Double | Auto-delete timer |
| viaBotUserId | Long | Via bot user ID |
| senderBusinessBotUserId | Long | Business bot sender ID |
| senderBoostCount | Integer | Sender boost count |
| authorSignature | String | Author signature |
| mediaAlbumId | Long | Media album ID |
| effectId | Long | Effect ID |
| content | MessageContentTdlib | Message content |

---

### TelegramChatFolderDto
DTO representing a chat folder.

| Field | Type | Description |
|-------|------|-------------|
| folderName | String | Folder name |
| folderId | Integer | Folder ID |

---

### AuthorizationStateTdlib (Abstract)
Base class for authorization states. Has the following subtypes:

#### AuthorizationStateReadyDto
Account is ready to use.

| Field | Type | Description |
|-------|------|-------------|
| type | String | "READY" |

#### AuthorizationStateWaitPhoneNumberDto
Waiting for phone number.

| Field | Type | Description |
|-------|------|-------------|
| type | String | "WAIT_PHONE_NUMBER" |

#### AuthorizationStateWaitCodeDto
Waiting for authentication code.

| Field | Type | Description |
|-------|------|-------------|
| type | String | "WAIT_CODE" |

#### AuthorizationStateWaitPasswordDto
Waiting for 2FA password.

| Field | Type | Description |
|-------|------|-------------|
| type | String | "WAIT_PASSWORD" |

---

## Error Responses

### Common HTTP Status Codes

| Status Code | Description | When It Occurs |
|-------------|-------------|----------------|
| 200 | OK | Request successful |
| 400 | Bad Request | Invalid input, authentication errors, or processing errors |
| 401 | Unauthorized | Missing or invalid JWT token |
| 404 | Not Found | File not found, chat not found, or user not found |
| 500 | Internal Server Error | Unexpected server error, file processing errors, or TDLib errors |

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
| `Invalid accountId: {accountId}` | 400 | Account ID is invalid or not found |
| `Error sending phone: [details]` | 400 | Failed to submit phone number |
| `Error sending code: [details]` | 400 | Failed to submit authentication code |
| `Error sending password: [details]` | 400 | Failed to submit 2FA password |
| `File still not downloaded after waiting` | 404 | File download timeout |
| `Failed to process image` | 500 | Image processing error |
| `Failed to process video` | 500 | Video processing error |
| `Failed to process document` | 500 | Document processing error |
| `Error fetching chat details` | 500 | TDLib error fetching chat |
| `Can process photo sizes, no suitable size type found.` | 500 | Invalid photo format |
| `Failed to reload clients cache for accountId: {accountId}` | 500 | Client reloading error |

---

## Authentication Flow

The Telegram authentication process involves multiple steps:

### Step 1: Add Credentials
```
POST /telegram-credentials/add
{
  "apiId": "12345678",
  "apiHash": "abcdef...",
  "accountId": "telegram_account_1",
  "accountName": "My Account",
  "phoneNumber": "+1234567890"
}
```

### Step 2: Check Authorization State
```
GET /auth/state?accountId=telegram_account_1
Response: { "type": "WAIT_PHONE_NUMBER" }
```

### Step 3: Submit Phone Number
```
POST /auth/login/phone?phone=%2B1234567890&accountId=telegram_account_1
```

### Step 4: Check State Again
```
GET /auth/state?accountId=telegram_account_1
Response: { "type": "WAIT_CODE" }
```

### Step 5: Submit Authentication Code
```
POST /auth/login/code?code=12345&accountId=telegram_account_1
```

### Step 6 (if 2FA enabled): Submit Password
```
GET /auth/state?accountId=telegram_account_1
Response: { "type": "WAIT_PASSWORD" }

POST /auth/login/password?password=myPassword&accountId=telegram_account_1
```

### Step 7: Verify Ready State
```
GET /auth/state?accountId=telegram_account_1
Response: { "type": "READY" }
```

### Obtaining Telegram API Credentials

1. Visit https://my.telegram.org/apps
2. Log in with your phone number
3. Click "API development tools"
4. Fill in application details
5. Obtain `api_id` and `api_hash`

---

## TDLib Integration

This service uses **TDLib (Telegram Database Library)**, which provides a complete Telegram client implementation. Key features:

- **Persistent Sessions**: Authentication sessions are stored and reused
- **Automatic Updates**: Real-time updates for messages, chats, and user status
- **File Management**: Automatic file downloading with caching
- **Multiple Accounts**: Support for multiple authenticated accounts simultaneously
- **Encryption**: All credentials are encrypted before storage

### Account Identifier Format

The `accountId` parameter is a string that uniquely identifies each Telegram account:
- Format: Any unique string (e.g., "telegram_account_1", "work_account")
- Used across all endpoints to specify which account to use
- Must match the `accountId` provided during credential creation

### Chat ID vs User ID

- **Chat ID**: Identifies conversations (private chats, groups, channels)
  - Private chat with user: Usually matches user ID
  - Groups: Negative IDs (e.g., -1001234567890)
  - Channels: Negative IDs with special format
  
- **User ID**: Identifies individual Telegram users
  - Positive integers (e.g., 123456789)
  - Used when creating chats or looking up users

### Remote File IDs

Telegram uses remote file IDs to reference media:
- Format: Base64-encoded strings (e.g., "AQADAgATsomeRemoteId")
- Obtained from message content or profile photos
- Used in file download endpoints
- Valid across sessions for the same account

---

## Message Content Types

Telegram supports various message content types (represented in `MessageContentTdlib`):

| Type | Description |
|------|-------------|
| TEXT | Plain text message |
| PHOTO | Image message |
| VIDEO | Video message |
| DOCUMENT | Document file |
| AUDIO | Audio file |
| VOICE | Voice message |
| VIDEO_NOTE | Video note (round video) |
| STICKER | Sticker |
| ANIMATION | GIF animation |
| LOCATION | Location share |
| CONTACT | Contact share |
| POLL | Poll |
| VENUE | Venue location |
| GAME | Game |

---

## Chat Types

Chats are categorized by type (represented in `ChatTypeTdlib`):

| Type | Description |
|------|-------------|
| PRIVATE | One-on-one private chat |
| BASIC_GROUP | Basic group (legacy, up to 200 members) |
| SUPERGROUP | Supergroup (up to 200,000 members) |
| CHANNEL | Broadcast channel |
| SECRET | Secret chat with end-to-end encryption |



---

## Usage Examples

### Example 1: Complete Authentication

```bash
# 1. Add credentials
POST /telegram-credentials/add
{
  "apiId": "12345678",
  "apiHash": "abcdef1234567890",
  "accountId": "work_telegram",
  "accountName": "Work Account",
  "phoneNumber": "+1234567890"
}

# 2. Submit phone
POST /auth/login/phone?phone=%2B1234567890&accountId=work_telegram

# 3. Submit code (received via SMS)
POST /auth/login/code?code=12345&accountId=work_telegram

# 4. Submit password (if 2FA enabled)
POST /auth/login/password?password=myPassword&accountId=work_telegram

# 5. Verify authentication
GET /auth/state?accountId=work_telegram
Response: { "type": "READY" }
```

### Example 2: Send Message to Username

```bash
# 1. Find user by username
GET /telegram/user-chat?username=johndoe&accountId=work_telegram
Response: 123456789

# 2. Create or get chat
POST /telegram/create-chat/123456789?accountId=work_telegram
Response: 987654321

# 3. Send message
POST /telegram/text?chatId=987654321&messageText=Hello%20John&accountId=work_telegram
```

### Example 3: Send Image with Caption

```bash
POST /telegram/image?chatId=123456789&accountId=work_telegram&message=Check%20this%20out
Content-Type: multipart/form-data

[image file data]
```

### Example 4: Retrieve Recent Messages

```bash
GET /telegram/123456789/messages?limit=10&accountId=work_telegram
```

### Example 5: Check Unread Notifications

```bash
GET /telegram/notifications?accountId=work_telegram
Response: [
  {
    "accountId": "work_telegram",
    "chatId": 123456789,
    "notificationsCount": 5
  }
]
```

---

## Troubleshooting

### Issue: "Invalid accountId" Error

**Solution:** Ensure you've added credentials with this `accountId` first using the `/telegram-credentials/add` endpoint.

### Issue: File Download Returns 404

**Solution:** Files are downloaded asynchronously. Wait a few seconds and retry. The service automatically requests file downloads from Telegram servers.

### Issue: Authentication Fails After Code Submission

**Solution:** Check if 2FA is enabled. Call `/auth/state` to see if it's waiting for a password.

### Issue: "Failed to process image" Error

**Solution:** Ensure the image file is valid and not corrupted. Supported formats: JPEG, PNG, GIF (under 10MB).

### Issue: Cannot Send Messages to User

**Solution:** Create a chat first using `/telegram/create-chat/{userId}`. You need an active chat before sending messages.

### Issue: Messages Not Appearing in Chat List

**Solution:** Use the `/telegram/main` endpoint with a higher limit, or check if the chat is in a folder or archive.

---

## Security Considerations

- **JWT Authentication**: All endpoints require valid JWT Bearer tokens
- **Credential Encryption**: API credentials are encrypted using AES-256 before storage
- **Session Security**: TDLib sessions are stored securely with encryption
- **File Access**: File downloads require proper authentication and account ownership
- **2FA Support**: Two-factor authentication is fully supported
- **Account Isolation**: Accounts are isolated; users can only access their own data

---

## Notes

- **TDLib Version**: Uses latest stable TDLib version
- **Multiple Accounts**: Supports unlimited connected accounts per user
- **Real-time Updates**: Background service processes Telegram updates
- **File Caching**: Downloaded files are cached temporarily
- **Session Persistence**: Sessions survive service restarts
- **Unicode Support**: Full Unicode support for messages and usernames
- **Media Albums**: Media albums are supported (grouped photos/videos)
- **Emoji and Stickers**: Full support for emoji, stickers, and custom emoji
- **Reactions**: Message reactions are tracked and returned
- **Threads**: Support for message threads in groups
- **Topics**: Support for forum topics in supergroups
- **Bot API Compatibility**: Compatible with Telegram Bot API standards
