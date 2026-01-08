# Discord Service API Documentation

## Overview
The Discord Service provides endpoints for managing Discord bot integration, including bot management, private and guild messaging, chat operations, and file handling. The service allows users to connect Discord bots and interact with Discord channels through the communication platform.

**Base URL:** `/api/discord`

**Authentication:** All endpoints require JWT Bearer token authentication.

---

## Table of Contents
1. [Bot Management](#bot-management)
2. [Message Operations](#message-operations)
3. [Chat Operations](#chat-operations)
4. [File Operations](#file-operations)
5. [Data Models](#data-models)
6. [Error Responses](#error-responses)

---

## Bot Management

### 1. Get Connected Bots

**Endpoint:** `GET /api/discord/me/bots`

**Description:** Get connected bots

Retrieves a list of all Discord bots connected by the authenticated user.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Array of DiscordBotInfoDto

| Field Name | Type | Description |
|------------|------|-------------|
| id | Long | Id |
| userId | Long | User id |
| botUserId | String | Bot user id |
| botUsername | String | Bot username |
| isActive | Boolean | Is active |
| createdDate | DateTime | Created date |
| lastModifiedDate | DateTime | Last modified date |

**Response Example:**
```json
[
  {
    "id": 1,
    "userId": 2,
    "botUserId": "123456789012345678",
    "botUsername": "MyBot",
    "isActive": true,
    "createdDate": "2025-12-18T10:30:00",
    "lastModifiedDate": "2025-12-18T10:30:00"
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

### 2. Add Bot

**Endpoint:** `POST /api/discord/bots/add`

**Description:** Add a new bot to the app

Adds a new Discord bot to the platform using the bot's access token.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

#### Request Body

| Field Name | Type | Required | Description | Constraints |
|------------|------|----------|-------------|-------------|
| token | String | Yes | Access token | Not blank |

**Request Example:**
```json
{
  "token": "MTIzNDU2Nzg5MDEyMzQ1Njc4.GhJkLm.nOpQrStUvWxYz1234567890AbCdEfGhIjKlMnOpQ"
}
```

#### Response

**Status Code:** `200 OK`

| Field Name | Type | Description |
|------------|------|-------------|
| id | Long | Id |
| userId | Long | User id |
| botUserId | String | Bot user id |
| botUsername | String | Bot username |
| isActive | Boolean | Is active |
| createdDate | DateTime | Created date |
| lastModifiedDate | DateTime | Last modified date |

**Response Example:**
```json
{
  "id": 1,
  "userId": 2,
  "botUserId": "123456789012345678",
  "botUsername": "MyBot",
  "isActive": true,
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
    "token": "must not be blank"
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

## Message Operations

All message endpoints are under the path: `/api/discord/messages/{botId}`

### 3. Send Direct Message

**Endpoint:** `POST /api/discord/messages/{botId}/dm/send`

**Description:** Send direct message

Sends a direct message to a Discord user.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| botId | Long | Yes | Bot identifier |

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| userId | String | Yes | Discord user ID to send message to |
| message | String | Yes | Message content |

#### Request Example
```
POST /api/discord/messages/1/dm/send?userId=987654321098765432&message=Hello!
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Empty

#### Error Responses

**Bad Request (400)**
```json
{
  "status": 400,
  "error": "No such bot for user:username"
}
```

**Not Found (404)**
```json
{
  "status": 404,
  "error": "User not found"
}
```

```json
{
  "status": 404,
  "error": "Credentials not found"
}
```

**Bad Request (400)**
```json
{
  "status": 400,
  "error": "It is not allowed to send messages to bots."
}
```

```json
{
  "status": 400,
  "error": "Bot is not initialized"
}
```

---

### 4. Send Guild Channel Message

**Endpoint:** `POST /api/discord/messages/{botId}/channel/send`

**Description:** Send guild channel message

Sends a message to a Discord guild channel.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| botId | Long | Yes | Bot identifier |

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| channelId | String | Yes | Discord channel ID |
| message | String | Yes | Message content |

#### Request Example
```
POST /api/discord/messages/1/channel/send?channelId=123456789012345678&message=Hello channel!
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Empty

#### Error Responses

**Bad Request (400)**
```json
{
  "status": 400,
  "error": "No such bot for user:username"
}
```

**Not Found (404)**
```json
{
  "status": 404,
  "error": "Channel is not found"
}
```

```json
{
  "status": 404,
  "error": "Credentials not found"
}
```

**Bad Request (400)**
```json
{
  "status": 400,
  "error": "Bot is not initialized"
}
```

---

### 5. Send Guild Channel File Message

**Endpoint:** `POST /api/discord/messages/{botId}/channel/send-file`

**Description:** Send guild channel file message

Sends a message with file attachments to a Discord guild channel.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: multipart/form-data
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| botId | Long | Yes | Bot identifier |

#### Form Data Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| channelId | String | Yes | Discord channel ID |
| files | MultipartFile[] | Yes | Files to send (one or more) |
| message | String | No | Optional message text accompanying the files |

#### Request Example
```
POST /api/discord/messages/1/channel/send-file
Content-Type: multipart/form-data

channelId=123456789012345678
files=<file1.png>
files=<file2.jpg>
message=Check these out!
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Empty

#### Error Responses

Same as "Send Guild Channel Message" endpoint.

---

### 6. Send Direct File Message

**Endpoint:** `POST /api/discord/messages/{botId}/dm/send-file`

**Description:** Send direct file message

Sends a direct message with file attachments to a Discord user.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: multipart/form-data
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| botId | Long | Yes | Bot identifier |

#### Form Data Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| userId | String | Yes | Discord user ID to send message to |
| files | MultipartFile[] | Yes | Files to send (one or more) |
| message | String | No | Optional message text accompanying the files |

#### Request Example
```
POST /api/discord/messages/1/dm/send-file
Content-Type: multipart/form-data

userId=987654321098765432
files=<document.pdf>
message=Here's the document
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Empty

#### Error Responses

Same as "Send Direct Message" endpoint.

---

### 7. Get Guild Channel Message History

**Endpoint:** `GET /api/discord/messages/{botId}/channels/{channelId}/history`

**Description:** Get guild channel message history

Retrieves message history from a Discord guild channel.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| botId | Long | Yes | Bot identifier |
| channelId | String | Yes | Discord channel ID |

#### Query Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| limit | Integer | No | 50 | Number of messages to retrieve |

#### Request Example
```
GET /api/discord/messages/1/channels/123456789012345678/history?limit=100
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Array of ChannelMessageDto

| Field Name | Type | Description |
|------------|------|-------------|
| id | String | Id |
| authorName | String | Author name |
| authorId | String | Author id |
| content | String | Content |
| timestamp | DateTime | Timestamp |
| bot | Boolean | Is bot |
| self | Boolean | Is self |
| attachments | Array | Attachments |

**Response Example:**
```json
[
  {
    "id": "123456789012345678",
    "authorName": "JohnDoe",
    "authorId": "987654321098765432",
    "content": "Hello everyone!",
    "timestamp": "2025-12-18T10:30:00",
    "bot": false,
    "self": false,
    "attachments": []
  },
  {
    "id": "123456789012345679",
    "authorName": "MyBot",
    "authorId": "111111111111111111",
    "content": "Welcome!",
    "timestamp": "2025-12-18T10:31:00",
    "bot": true,
    "self": true,
    "attachments": [
      {
        "id": 1,
        "fileName": "welcome.png",
        "fileType": "image/png",
        "discordUrl": "https://cdn.discordapp.com/attachments/..."
      }
    ]
  }
]
```

#### Error Responses

**Bad Request (400)**
```json
{
  "status": 400,
  "error": "No such bot for user:username"
}
```

**Not Found (404)**
```json
{
  "status": 404,
  "error": "Channel is not found"
}
```

---

### 8. Get Private Chat Message History

**Endpoint:** `GET /api/discord/messages/{botId}/dm/{channelId}/history`

**Description:** Get private chat message history

Retrieves message history from a private Discord chat.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| botId | Long | Yes | Bot identifier |
| channelId | String | Yes | Discord channel ID |

#### Query Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| limit | Integer | No | 50 | Number of messages to retrieve |

#### Request Example
```
GET /api/discord/messages/1/dm/123456789012345678/history?limit=50
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Array of DiscordPrivateMessageDto

| Field Name | Type | Description |
|------------|------|-------------|
| id | Long | Id |
| botId | Long | Bot id |
| discordMessageId | String | Discord message id |
| channelId | String | Channel id |
| authorId | String | Author id |
| authorName | String | Author name |
| content | String | Content |
| isFromBot | Boolean | Is from bot |
| hasAttachments | Boolean | Has attachments |
| files | Array | Files |
| timestamp | DateTime | Timestamp |
| createdDate | DateTime | Created date |
| lastModifiedDate | DateTime | Last modified date |

**Response Example:**
```json
[
  {
    "id": 1,
    "botId": 1,
    "discordMessageId": "123456789012345678",
    "channelId": "987654321098765432",
    "authorId": "555555555555555555",
    "authorName": "JohnDoe",
    "content": "Hi there!",
    "isFromBot": false,
    "hasAttachments": false,
    "files": [],
    "timestamp": "2025-12-18T10:30:00",
    "createdDate": "2025-12-18T10:30:01",
    "lastModifiedDate": "2025-12-18T10:30:01"
  }
]
```

#### Error Responses

Same as "Get Guild Channel Message History".

---

### 9. Search Messages in Private Chat

**Endpoint:** `GET /api/discord/messages/{botId}/dm/{channelId}/search`

**Description:** Search messages in private chat

Searches for messages containing the query string in a private chat.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| botId | Long | Yes | Bot identifier |
| channelId | String | Yes | Discord channel ID |

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| query | String | Yes | Search query string |

#### Request Example
```
GET /api/discord/messages/1/dm/123456789012345678/search?query=important
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Array of DiscordPrivateMessageDto (see structure in endpoint #8)

**Response Example:**
```json
[
  {
    "id": 5,
    "botId": 1,
    "discordMessageId": "123456789012345680",
    "channelId": "987654321098765432",
    "authorId": "555555555555555555",
    "authorName": "JohnDoe",
    "content": "This is important information",
    "isFromBot": false,
    "hasAttachments": false,
    "files": [],
    "timestamp": "2025-12-18T11:00:00",
    "createdDate": "2025-12-18T11:00:01",
    "lastModifiedDate": "2025-12-18T11:00:01"
  }
]
```

#### Error Responses

Same as "Get Guild Channel Message History".

---

### 10. Search Messages in Guild Channel

**Endpoint:** `GET /api/discord/messages/{botId}/channel/{channelId}/search`

**Description:** Search messages in guild channel

Searches for messages containing the query string in a guild channel.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| botId | Long | Yes | Bot identifier |
| channelId | String | Yes | Discord channel ID |

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| query | String | Yes | Search query string |

#### Request Example
```
GET /api/discord/messages/1/channel/123456789012345678/search?query=meeting
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Array of ChannelMessageDto (see structure in endpoint #7)

#### Error Responses

Same as "Get Guild Channel Message History".

---

### 11. Delete Private Message

**Endpoint:** `DELETE /api/discord/messages/{botId}/delete`

**Description:** Delete private message

Deletes one or more private messages.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| botId | Long | Yes | Bot identifier |

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| messageIds | Long[] | Yes | List of message IDs to delete |

#### Request Example
```
DELETE /api/discord/messages/1/delete?messageIds=1&messageIds=2&messageIds=3
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Empty

#### Error Responses

**Bad Request (400)**
```json
{
  "status": 400,
  "error": "No such bot for user:username"
}
```

```json
{
  "status": 400,
  "error": "Bot is not initialized"
}
```

```json
{
  "status": 400,
  "error": "Failed to delete message in Discord"
}
```

**Not Found (404)**
```json
{
  "status": 404,
  "error": "Credentials not found"
}
```

---

### 12. Update Private Message

**Endpoint:** `PUT /api/discord/messages/{botId}/update/{messageId}`

**Description:** Update private message

Updates the content of a private message.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| botId | Long | Yes | Bot identifier |
| messageId | Long | Yes | Message ID to update |

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| updatedMessage | String | Yes | New message content |

#### Request Example
```
PUT /api/discord/messages/1/update/5?updatedMessage=Updated message content
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Empty

#### Error Responses

**Bad Request (400)**
```json
{
  "status": 400,
  "error": "Message content cannot be empty"
}
```

```json
{
  "status": 400,
  "error": "No such bot for user:username"
}
```

**Not Found (404)**
```json
{
  "status": 404,
  "error": "Channel is not found"
}
```

---

### 13. Delete Guild Channel Message

**Endpoint:** `DELETE /api/discord/messages/{botId}/channel/delete`

**Description:** Delete guild channel message

Deletes one or more messages from a guild channel.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| botId | Long | Yes | Bot identifier |

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| channelId | String | Yes | Discord channel ID |
| messageIds | String[] | Yes | List of Discord message IDs to delete |

#### Request Example
```
DELETE /api/discord/messages/1/channel/delete?channelId=123456789012345678&messageIds=111111111111111111&messageIds=222222222222222222
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Empty

#### Error Responses

Same as "Delete Private Message".

---

### 14. Update Guild Channel Message

**Endpoint:** `PUT /api/discord/messages/{botId}/update/channel/{messageId}`

**Description:** Update guild channel message

Updates the content of a guild channel message.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| botId | Long | Yes | Bot identifier |
| messageId | String | Yes | Discord message ID to update |

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| channelId | String | Yes | Discord channel ID |
| updatedMessage | String | Yes | New message content |

#### Request Example
```
PUT /api/discord/messages/1/update/channel/123456789012345678?channelId=987654321098765432&updatedMessage=Updated content
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Empty

#### Error Responses

Same as "Update Private Message".

---

## Chat Operations

All chat endpoints are under the path: `/api/discord/{botId}/chats`

### 15. Get Private Chats

**Endpoint:** `GET /api/discord/{botId}/chats/private`

**Description:** Get private chats

Retrieves all private chats for the specified bot.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| botId | Long | Yes | Bot identifier |

#### Request Example
```
GET /api/discord/1/chats/private
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Array of DiscordPrivateChatDto

| Field Name | Type | Description |
|------------|------|-------------|
| id | Long | Id |
| botId | Long | Bot id |
| channelId | String | Channel id |
| userId | String | User id |
| userName | String | User name |
| userAvatarUrl | String | User avatar url |
| lastMessageTime | DateTime | Last message time |
| lastMessageId | String | Last message id |
| messageCount | Integer | Message count |
| createdDate | DateTime | Created date |
| lastModifiedDate | DateTime | Last modified date |

**Response Example:**
```json
[
  {
    "id": 1,
    "botId": 1,
    "channelId": "123456789012345678",
    "userId": "987654321098765432",
    "userName": "JohnDoe",
    "userAvatarUrl": "https://cdn.discordapp.com/avatars/...",
    "lastMessageTime": "2025-12-18T10:30:00",
    "lastMessageId": "555555555555555555",
    "messageCount": 15,
    "createdDate": "2025-12-15T09:00:00",
    "lastModifiedDate": "2025-12-18T10:30:00"
  }
]
```

#### Error Responses

**Bad Request (400)**
```json
{
  "status": 400,
  "error": "No such bot for user:username"
}
```

**Not Found (404)**
```json
{
  "status": 404,
  "error": "Credentials not found"
}
```

**Bad Request (400)**
```json
{
  "status": 400,
  "error": "Bot is not initialized"
}
```

---

### 16. Get Guilds

**Endpoint:** `GET /api/discord/{botId}/chats/guilds`

**Description:** Get guilds

Retrieves all guilds (servers) the bot is a member of.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| botId | Long | Yes | Bot identifier |

#### Request Example
```
GET /api/discord/1/chats/guilds
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Array of GuildDto

| Field Name | Type | Description |
|------------|------|-------------|
| id | String | Id |
| name | String | Name |
| iconUrl | String | Icon url |
| memberCount | Integer | Member count |

**Response Example:**
```json
[
  {
    "id": "123456789012345678",
    "name": "My Discord Server",
    "iconUrl": "https://cdn.discordapp.com/icons/...",
    "memberCount": 150
  }
]
```

#### Error Responses

Same as "Get Private Chats".

---

### 17. Get Guild Channels

**Endpoint:** `GET /api/discord/{botId}/chats/guilds/{guildId}/channels`

**Description:** Get guild channels

Retrieves all channels in a specific guild.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| botId | Long | Yes | Bot identifier |
| guildId | String | Yes | Discord guild ID |

#### Request Example
```
GET /api/discord/1/chats/guilds/123456789012345678/channels
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Array of GuildChannelDto

| Field Name | Type | Description |
|------------|------|-------------|
| id | String | Id |
| name | String | Name |
| topic | String | Topic |
| position | Integer | Position |

**Response Example:**
```json
[
  {
    "id": "111111111111111111",
    "name": "general",
    "topic": "General discussion",
    "position": 0
  },
  {
    "id": "222222222222222222",
    "name": "announcements",
    "topic": "Important announcements",
    "position": 1
  }
]
```

#### Error Responses

**Not Found (404)**
```json
{
  "status": 404,
  "error": "Server not found"
}
```

**Bad Request (400)**
```json
{
  "status": 400,
  "error": "No such bot for user:username"
}
```

---

### 18. Get Available Users

**Endpoint:** `GET /api/discord/{botId}/chats/users`

**Description:** Get available users

Retrieves all users available to the bot across all guilds.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| botId | Long | Yes | Bot identifier |

#### Request Example
```
GET /api/discord/1/chats/users
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Array of GuildUserDto

| Field Name | Type | Description |
|------------|------|-------------|
| id | String | Id |
| username | String | Username |
| globalName | String | Global name |
| avatarUrl | String | Avatar url |
| guildName | String | Guild name |
| isBot | Boolean | Is bot |

**Response Example:**
```json
[
  {
    "id": "987654321098765432",
    "username": "johndoe",
    "globalName": "John Doe",
    "avatarUrl": "https://cdn.discordapp.com/avatars/...",
    "guildName": "My Discord Server",
    "isBot": false
  },
  {
    "id": "555555555555555555",
    "username": "jane_smith",
    "globalName": "Jane Smith",
    "avatarUrl": "https://cdn.discordapp.com/avatars/...",
    "guildName": "My Discord Server",
    "isBot": false
  }
]
```

#### Error Responses

Same as "Get Private Chats".

---

### 19. Search Private Chats

**Endpoint:** `GET /api/discord/{botId}/chats/search`

**Description:** Search private chats

Searches for private chats by user name.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| botId | Long | Yes | Bot identifier |

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| query | String | Yes | Search query for user name |

#### Request Example
```
GET /api/discord/1/chats/search?query=john
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Array of DiscordPrivateChatDto (see structure in endpoint #15)

#### Error Responses

Same as "Get Private Chats".

---

### 20. Search Guild Channels

**Endpoint:** `GET /api/discord/{botId}/chats/{guildId}/channels/search`

**Description:** Search guild channels

Searches for channels within a specific guild by channel name.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| botId | Long | Yes | Bot identifier |
| guildId | String | Yes | Discord guild ID |

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| query | String | Yes | Search query for channel name |

#### Request Example
```
GET /api/discord/1/chats/123456789012345678/channels/search?query=announce
```

#### Response

**Status Code:** `200 OK`

**Response Body:** Array of GuildChannelDto (see structure in endpoint #17)

#### Error Responses

Same as "Get Guild Channels".

---

### 21. Get Private Chat

**Endpoint:** `GET /api/discord/{botId}/chats/{channelId}`

**Description:** Get private chat

Retrieves details of a specific private chat.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| botId | Long | Yes | Bot identifier |
| channelId | String | Yes | Discord channel ID |

#### Request Example
```
GET /api/discord/1/chats/123456789012345678
```

#### Response

**Status Code:** `200 OK`

**Response Body:** DiscordPrivateChatDto (see structure in endpoint #15)

#### Error Responses

Same as "Get Private Chats".

---

## File Operations

### 22. Get Image File

**Endpoint:** `GET /api/discord/files/image`

**Description:** Get image file from discord url

Retrieves an image file from a Discord CDN URL and streams it back.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| url | String | Yes | Discord CDN image URL |

#### Request Example
```
GET /api/discord/files/image?url=https://cdn.discordapp.com/attachments/.../image.jpg
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
  "error": "Error message"
}
```

---

### 23. Get Video File

**Endpoint:** `GET /api/discord/files/video`

**Description:** Get video file from discord url

Retrieves a video file from a Discord CDN URL and streams it back.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| url | String | Yes | Discord CDN video URL |

#### Request Example
```
GET /api/discord/files/video?url=https://cdn.discordapp.com/attachments/.../video.mp4
```

#### Response

**Status Code:** `200 OK`

**Content-Type:** `application/octet-stream`

**Response Body:** Binary video data (StreamingResponseBody)

#### Error Responses

Same as "Get Image File".

---

### 24. Get Document File

**Endpoint:** `GET /api/discord/files/document`

**Description:** Get document file from discord url

Retrieves a document file from a Discord CDN URL and streams it back.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| url | String | Yes | Discord CDN document URL |

#### Request Example
```
GET /api/discord/files/document?url=https://cdn.discordapp.com/attachments/.../document.pdf
```

#### Response

**Status Code:** `200 OK`

**Content-Type:** `application/octet-stream`

**Response Body:** Binary document data (StreamingResponseBody)

#### Error Responses

Same as "Get Image File".

---

## Data Models

### DiscordBotInfoDto
Bot information DTO.

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Bot record ID |
| userId | Long | Owner user ID |
| botUserId | String | Discord bot user ID |
| botUsername | String | Discord bot username |
| isActive | Boolean | Bot active status |
| createdDate | DateTime | Creation timestamp |
| lastModifiedDate | DateTime | Last modification timestamp |

### DiscordPrivateMessageDto
Private message DTO.

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Message record ID |
| botId | Long | Bot ID |
| discordMessageId | String | Discord message ID |
| channelId | String | Discord channel ID |
| authorId | String | Author user ID |
| authorName | String | Author username |
| content | String | Message content |
| isFromBot | Boolean | Message sent by bot |
| hasAttachments | Boolean | Has file attachments |
| files | Array | Attached files |
| timestamp | DateTime | Discord message timestamp |
| createdDate | DateTime | Database creation timestamp |
| lastModifiedDate | DateTime | Database modification timestamp |

### ChannelMessageDto
Guild channel message DTO.

| Field | Type | Description |
|-------|------|-------------|
| id | String | Discord message ID |
| authorName | String | Author username |
| authorId | String | Author user ID |
| content | String | Message content |
| timestamp | DateTime | Message timestamp |
| bot | Boolean | Message from bot |
| self | Boolean | Message from connected bot |
| attachments | Array | Attached files |

### DiscordPrivateChatDto
Private chat DTO.

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Chat record ID |
| botId | Long | Bot ID |
| channelId | String | Discord channel ID |
| userId | String | User ID |
| userName | String | Username |
| userAvatarUrl | String | User avatar URL |
| lastMessageTime | DateTime | Last message timestamp |
| lastMessageId | String | Last message ID |
| messageCount | Integer | Total message count |
| createdDate | DateTime | Database creation timestamp |
| lastModifiedDate | DateTime | Database modification timestamp |

### GuildDto
Guild (server) information DTO.

| Field | Type | Description |
|-------|------|-------------|
| id | String | Discord guild ID |
| name | String | Guild name |
| iconUrl | String | Guild icon URL |
| memberCount | Integer | Member count |

### GuildChannelDto
Guild channel DTO.

| Field | Type | Description |
|-------|------|-------------|
| id | String | Discord channel ID |
| name | String | Channel name |
| topic | String | Channel topic |
| position | Integer | Channel position |

### GuildUserDto
Guild user DTO.

| Field | Type | Description |
|-------|------|-------------|
| id | String | Discord user ID |
| username | String | Username |
| globalName | String | Display name |
| avatarUrl | String | Avatar URL |
| guildName | String | Guild name |
| isBot | Boolean | User is bot |

### DiscordMessageFileDto
Message file attachment DTO.

| Field | Type | Description |
|-------|------|-------------|
| id | Long | File record ID |
| fileName | String | File name |
| fileType | String | MIME type |
| discordUrl | String | Discord CDN URL |

---

## Error Responses

### Common HTTP Status Codes

| Status Code | Description | When It Occurs |
|-------------|-------------|----------------|
| 200 | OK | Request successful |
| 400 | Bad Request | Validation failed, invalid input, or business logic error |
| 401 | Unauthorized | Missing or invalid JWT token |
| 404 | Not Found | Bot, user, channel, or message not found |
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
| `No such bot for user:username` | 400 | Bot doesn't belong to authenticated user |
| `Credentials not found` | 404 | Bot credentials not found in database |
| `Bot is not initialized` | 400 | Bot is not connected/initialized |
| `User not found` | 404 | Discord user not found |
| `Channel is not found` | 404 | Discord channel not found |
| `Server not found` | 404 | Discord guild/server not found |
| `It is not allowed to send messages to bots.` | 400 | Cannot send DM to bot users |
| `Message content cannot be empty` | 400 | Update message content is empty |
| `Failed to delete message in Discord` | 400 | Discord API delete operation failed |

---

## Security

All endpoints require JWT Bearer token authentication. Include the token in the `Authorization` header:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

Tokens are obtained from the User Service authentication endpoints.

---

## Notes

- **Bot Tokens:** Bot tokens are stored encrypted in the database
- **Bot Initialization:** Bots must be initialized and connected before most operations
- **Rate Limiting:** Discord API rate limits apply to all message operations
- **File Uploads:** Support multipart/form-data for file attachments
- **File Downloads:** Files are streamed directly from Discord CDN
- **Message History:** Default limit is 50 messages, can be adjusted via query parameter
- **Search Functionality:** Search is case-insensitive and matches partial strings
- **Bot Permissions:** Bot must have appropriate Discord permissions for operations
- **User Identification:** All operations are scoped to the authenticated user's bots
