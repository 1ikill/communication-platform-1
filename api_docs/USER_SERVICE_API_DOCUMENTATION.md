# User Service API Documentation

## Overview
The User Service handles authentication, authorization, and user management for the communication platform. It provides JWT-based authentication with access and refresh tokens, role-based access control (RBAC), and complete user lifecycle management.

**Base URL:** `/users`

---

## Table of Contents
1. [Authentication Endpoints](#authentication-endpoints)
2. [User Management](#user-management)
3. [Admin Operations](#admin-operations)
4. [Data Models](#data-models)
5. [Error Responses](#error-responses)
6. [Security](#security)

---

## Authentication Endpoints

### 1. Register

**Endpoint:** `POST /users/auth/register`

**Description:** Register

Creates a new user account with the USER role. This is the public registration endpoint that does not require authentication.

#### Request Headers
```
Content-Type: application/json
```

#### Request Body

| Field Name | Type | Required | Validation | Description |
|------------|------|----------|------------|-------------|
| email | String | Yes | Valid email format | User's email address |
| username | String | Yes | 5-25 characters | Unique username for login |
| fullName | String | Yes | 5-100 characters | User's full name |
| password | String | Yes | 8-128 characters | User's password |

#### Request Example
```json
{
  "email": "john.doe@example.com",
  "username": "johndoe",
  "fullName": "John Doe",
  "password": "SecurePassword123"
}
```

#### Response

**Status Code:** `200 OK`

**Response Body:**
```json
{
  "id": 1,
  "email": "john.doe@example.com",
  "username": "johndoe",
  "fullName": "John Doe",
  "role": "USER",
  "createdDate": "2025-12-18T10:30:00",
  "lastModifiedDate": "2025-12-18T10:30:00"
}
```

#### Error Responses

**Validation Error (400)**
```json
{
  "status": 400,
  "errors": {
    "email": "Invalid email format",
    "username": "Username must be between 5 and 25 characters",
    "fullName": "Full name is required",
    "password": "Password must be 8–128 characters long"
  }
}
```

**User Already Exists (400)**
```json
{
  "status": 400,
  "error": "User already exists."
}
```

---

### 2. Login

**Endpoint:** `POST /users/auth/login`

**Description:** Login

Authenticates a user and returns JWT access and refresh tokens.

#### Request Headers
```
Content-Type: application/json
```

#### Request Body

| Field Name | Type | Required | Description |
|------------|------|----------|-------------|
| username | String | Yes | Username for authentication |
| password | String | Yes | User's password |

#### Request Example
```json
{
  "username": "johndoe",
  "password": "SecurePassword123"
}
```

#### Response

**Status Code:** `200 OK`

**Response Body:**
```json
{
  "access": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huZG9lIiwicm9sZSI6IlVTRVIiLCJpYXQiOjE3MDI5MDU2MDAsImV4cCI6MTcwMjkwOTIwMH0.signature",
  "refresh": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huZG9lIiwiaWF0IjoxNzAyOTA1NjAwLCJleHAiOjE3MDM1MTAzMDB9.signature"
}
```

#### Error Responses

**Validation Error (400)**
```json
{
  "status": 400,
  "errors": {
    "username": "User name must not be blank",
    "password": "Password must not be blank"
  }
}
```

**User Not Found (400)**
```json
{
  "status": 400,
  "error": "User not found"
}
```

**Invalid Password (400)**
```json
{
  "status": 400,
  "error": "Invalid password"
}
```

---

### 3. Refresh Token

**Endpoint:** `POST /users/auth/refresh`

**Description:** Refresh token

Generates a new access token using a valid refresh token.

#### Request Headers
```
Content-Type: application/x-www-form-urlencoded
```

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| refreshToken | String | Yes | Valid refresh token from login |

#### Request Example
```
POST /users/auth/refresh?refreshToken=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

#### Response

**Status Code:** `200 OK`

**Response Body:**
```json
{
  "access": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.newAccessToken.signature",
  "refresh": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.newRefreshToken.signature"
}
```

#### Error Responses

**Invalid Token (400)**
```json
{
  "status": 400,
  "error": "Invalid or expired refresh token"
}
```

---

## User Management

### 4. Get Current User

**Endpoint:** `GET /users/me`

**Description:** Get self iformation

Retrieves information about the currently authenticated user.

#### Request Headers
```
Authorization: Bearer <ACCESS_TOKEN>
```

#### Request Example
```
GET /users/me
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

#### Response

**Status Code:** `200 OK`

**Response Body:**
```json
{
  "id": 1,
  "email": "john.doe@example.com",
  "username": "johndoe",
  "fullName": "John Doe",
  "role": "USER",
  "createdDate": "2025-12-18T10:30:00",
  "lastModifiedDate": "2025-12-18T10:30:00"
}
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

### 5. Update User

**Endpoint:** `PATCH /users/{id}`

**Description:** Patch user

Updates user information. Users can update their own information, admins can update any user.

#### Request Headers
```
Authorization: Bearer <ACCESS_TOKEN>
Content-Type: application/json
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | Long | Yes | User ID to update |

#### Request Body

All fields are optional. Only include fields you want to update.

| Field Name | Type | Required | Validation | Description |
|------------|------|----------|------------|-------------|
| email | String | No | Valid email format | New email address |
| username | String | No | 5-25 characters | New username |
| fullName | String | No | 5-100 characters | New full name |
| password | String | No | 8-128 characters | New password |
| role | Enum | No | ADMIN or USER | New role (ADMIN only) |

#### Request Example
```json
PATCH /users/1
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

{
  "fullName": "John Michael Doe",
  "email": "john.m.doe@example.com"
}
```

#### Response

**Status Code:** `200 OK`

**Response Body:**
```json
{
  "id": 1,
  "email": "john.m.doe@example.com",
  "username": "johndoe",
  "fullName": "John Michael Doe",
  "role": "USER",
  "createdDate": "2025-12-18T10:30:00",
  "lastModifiedDate": "2025-12-18T15:45:00"
}
```

#### Error Responses

**Validation Error (400)**
```json
{
  "status": 400,
  "errors": {
    "email": "Invalid email format",
    "username": "Username must be between 5 and 25 characters"
  }
}
```

**Email Already In Use (400)**
```json
{
  "status": 400,
  "error": "Email already in use."
}
```

**Username Already In Use (400)**
```json
{
  "status": 400,
  "error": "Username already in use."
}
```

**Not Found (404)**
```json
{
  "status": 404,
  "error": "User not found"
}
```

**Forbidden (403)**
```json
{
  "status": 403,
  "error": "Access denied"
}
```

---

### 6. Get All Users

**Endpoint:** `GET /users`

**Description:** Get all users

Retrieves a list of all users in the system. Requires authentication.

#### Request Headers
```
Authorization: Bearer <ACCESS_TOKEN>
```

#### Request Example
```
GET /users
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

#### Response

**Status Code:** `200 OK`

**Response Body:**
```json
[
  {
    "id": 1,
    "email": "john.doe@example.com",
    "username": "johndoe",
    "fullName": "John Doe",
    "role": "USER",
    "createdDate": "2025-12-18T10:30:00",
    "lastModifiedDate": "2025-12-18T10:30:00"
  },
  {
    "id": 2,
    "email": "admin@example.com",
    "username": "admin",
    "fullName": "Admin User",
    "role": "ADMIN",
    "createdDate": "2025-12-01T08:00:00",
    "lastModifiedDate": "2025-12-01T08:00:00"
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

## Admin Operations

### 7. Create User (Admin)

**Endpoint:** `POST /users/admin/create-user`

**Description:** Create user

Creates a new user with a specified role. Only accessible to users with ADMIN role.

#### Request Headers
```
Authorization: Bearer <ADMIN_ACCESS_TOKEN>
Content-Type: application/json
```

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| role | Enum | Yes | Role to assign (USER or ADMIN) |

#### Request Body

| Field Name | Type | Required | Validation | Description |
|------------|------|----------|------------|-------------|
| email | String | Yes | Valid email format | User's email address |
| username | String | Yes | 5-25 characters | Unique username for login |
| fullName | String | Yes | 5-100 characters | User's full name |
| password | String | Yes | 8-128 characters | User's password |

#### Request Example
```json
POST /users/admin/create-user?role=ADMIN
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

{
  "email": "new.admin@example.com",
  "username": "newadmin",
  "fullName": "New Admin User",
  "password": "AdminPassword123"
}
```

#### Response

**Status Code:** `200 OK`

**Response Body:**
```json
{
  "id": 3,
  "email": "new.admin@example.com",
  "username": "newadmin",
  "fullName": "New Admin User",
  "role": "ADMIN",
  "createdDate": "2025-12-18T16:00:00",
  "lastModifiedDate": "2025-12-18T16:00:00"
}
```

#### Error Responses

**Validation Error (400)**
```json
{
  "status": 400,
  "errors": {
    "email": "Email is required",
    "username": "Username must be between 5 and 25 characters",
    "fullName": "Full name is required",
    "password": "Password must be 8–128 characters long"
  }
}
```

**User Already Exists (400)**
```json
{
  "status": 400,
  "error": "User already exists."
}
```

**Forbidden (403)**
```json
{
  "status": 403,
  "error": "Access denied"
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

## Data Models

### UserCreateDto
DTO for creating a new user.

| Field | Type | Validation | Description |
|-------|------|------------|-------------|
| email | String | Required, valid email | User's email address |
| username | String | Required, 5-25 characters | Unique username |
| fullName | String | Required, 5-100 characters | User's full name |
| password | String | Required, 8-128 characters | User's password (will be hashed) |

---

### UserDto
DTO representing user information.

| Field | Type | Description |
|-------|------|-------------|
| id | Long | User ID |
| email | String | Email address |
| username | String | Username |
| fullName | String | Full name |
| role | RoleType | User role (USER or ADMIN) |
| createdDate | LocalDateTime | Account creation timestamp |
| lastModifiedDate | LocalDateTime | Last modification timestamp |

---

### UserPatchDto
DTO for updating user information.

| Field | Type | Validation | Description |
|-------|------|------------|-------------|
| email | String | Optional, valid email | New email address |
| username | String | Optional, 5-25 characters | New username |
| fullName | String | Optional, 5-100 characters | New full name |
| password | String | Optional, 8-128 characters | New password |
| role | RoleType | Optional | New role (ADMIN only) |

**Note:** All fields are optional. Only non-null fields will be updated.

---

### AuthRequestDto
DTO for login authentication.

| Field | Type | Validation | Description |
|-------|------|------------|-------------|
| username | String | Required, not blank | Username for authentication |
| password | String | Required, not blank | User's password |

---

### RoleType (Enum)
User role enumeration.

| Value | Description |
|-------|-------------|
| USER | Default user role with standard permissions |
| ADMIN | Administrator role with elevated permissions |

---

## Error Responses

### Common HTTP Status Codes

| Status Code | Description | When It Occurs |
|-------------|-------------|----------------|
| 200 | OK | Request successful |
| 400 | Bad Request | Validation errors, user already exists, invalid credentials |
| 401 | Unauthorized | Missing or invalid JWT token |
| 403 | Forbidden | Insufficient permissions (not admin) |
| 404 | Not Found | User not found |
| 405 | Method Not Allowed | HTTP method not supported for endpoint |

### Error Response Formats

**Validation Error (400)**
```json
{
  "status": 400,
  "errors": {
    "fieldName1": "Error message 1",
    "fieldName2": "Error message 2"
  }
}
```

**General Error (400, 404)**
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
  "details": "Request method 'DELETE' not supported"
}
```

### Common Error Messages

| Error Message | Status | Meaning |
|--------------|--------|---------|
| `User already exists.` | 400 | Email or username already registered |
| `User not found` | 400 | Username does not exist |
| `Invalid password` | 400 | Password does not match |
| `Email already in use.` | 400 | Email is taken by another user |
| `Username already in use.` | 400 | Username is taken by another user |
| `Invalid or expired refresh token` | 400 | Refresh token is invalid or expired |
| `Unauthorized` | 401 | Missing or invalid JWT token |
| `Access denied` | 403 | User lacks required role/permissions |
| `Email is required` | 400 | Email field is missing or empty |
| `Invalid email format` | 400 | Email does not match valid format |
| `Username must be between 5 and 25 characters` | 400 | Username length validation failed |
| `Full name is required` | 400 | Full name field is missing or empty |
| `Full name must be between 5 and 100 characters` | 400 | Full name length validation failed |
| `Password is required` | 400 | Password field is missing or empty |
| `Password must be 8–128 characters long` | 400 | Password length validation failed |
| `User name must not be blank` | 400 | Username is empty in login request |
| `Password must not be blank` | 400 | Password is empty in login request |

---

## Security

### JWT Authentication

The User Service uses JWT (JSON Web Tokens) for authentication:

#### Access Token
- **Purpose**: Authenticates API requests
- **Lifetime**: 1 hour (typical)
- **Usage**: Include in `Authorization` header as `Bearer <token>`
- **Claims**: Contains user ID, username, role

#### Refresh Token
- **Purpose**: Generates new access tokens
- **Lifetime**: 7 days (typical)
- **Usage**: Used with `/users/auth/refresh` endpoint
- **Storage**: Stored securely by the client application

### Token Usage Example

```bash
# 1. Login to get tokens
POST /users/auth/login
{
  "username": "johndoe",
  "password": "SecurePassword123"
}

Response:
{
  "access": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refresh": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}

# 2. Use access token for authenticated requests
GET /users/me
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

# 3. When access token expires, refresh it
POST /users/auth/refresh?refreshToken=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

Response:
{
  "access": "newAccessToken...",
  "refresh": "newRefreshToken..."
}
```

### Role-Based Access Control (RBAC)

The system implements two roles:

#### USER Role
- Register as new user
- Login and refresh tokens
- View own profile (`GET /users/me`)
- Update own profile (`PATCH /users/{id}` where id is own user ID)
- View all users (`GET /users`)
- Access other microservices

#### ADMIN Role
- All USER permissions
- Create users with any role (`POST /users/admin/create-user`)
- Update any user's information including role changes
- Access admin-specific features in other microservices

### Password Security

- **Hashing**: Passwords are hashed using BCrypt
- **Storage**: Only password hashes are stored, never plaintext
- **Validation**: Minimum 8 characters, maximum 128 characters
- **Requirements**: No specific complexity requirements enforced by default

### Authorization Header Format

All authenticated endpoints require the following header:

```
Authorization: Bearer <ACCESS_TOKEN>
```

Example:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huZG9lIiwicm9sZSI6IlVTRVIiLCJpYXQiOjE3MDI5MDU2MDAsImV4cCI6MTcwMjkwOTIwMH0.signature
```

---

## Authentication Flow

### Registration and First Login

```
┌──────────────────────────────────────────────┐
│ 1. Register                                  │
│    POST /users/auth/register                 │
│    { email, username, fullName, password }   │
└────────────────┬─────────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────────────┐
│ 2. Login                                     │
│    POST /users/auth/login                    │
│    { username, password }                    │
└────────────────┬─────────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────────────┐
│ 3. Receive Tokens                            │
│    { access: "...", refresh: "..." }         │
└────────────────┬─────────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────────────┐
│ 4. Make Authenticated Requests               │
│    Authorization: Bearer <access_token>      │
└──────────────────────────────────────────────┘
```

### Token Refresh Flow

```
┌──────────────────────────────────────────────┐
│ 1. Access Token Expires                      │
│    API returns 401 Unauthorized              │
└────────────────┬─────────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────────────┐
│ 2. Refresh Token                             │
│    POST /users/auth/refresh                  │
│    ?refreshToken=<refresh_token>             │
└────────────────┬─────────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────────────┐
│ 3. Receive New Tokens                        │
│    { access: "...", refresh: "..." }         │
└────────────────┬─────────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────────────┐
│ 4. Continue with New Access Token            │
│    Authorization: Bearer <new_access_token>  │
└──────────────────────────────────────────────┘
```

---

## Usage Examples

### Example 1: Complete Registration and Login Flow

```bash
# Step 1: Register new user
POST /users/auth/register
Content-Type: application/json

{
  "email": "alice@example.com",
  "username": "alice123",
  "fullName": "Alice Johnson",
  "password": "SecurePass456"
}

Response:
{
  "id": 5,
  "email": "alice@example.com",
  "username": "alice123",
  "fullName": "Alice Johnson",
  "role": "USER",
  "createdDate": "2025-12-18T10:00:00",
  "lastModifiedDate": "2025-12-18T10:00:00"
}

# Step 2: Login
POST /users/auth/login
Content-Type: application/json

{
  "username": "alice123",
  "password": "SecurePass456"
}

Response:
{
  "access": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refresh": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}

# Step 3: Get current user info
GET /users/me
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

Response:
{
  "id": 5,
  "email": "alice@example.com",
  "username": "alice123",
  "fullName": "Alice Johnson",
  "role": "USER",
  "createdDate": "2025-12-18T10:00:00",
  "lastModifiedDate": "2025-12-18T10:00:00"
}
```

### Example 2: Update User Profile

```bash
PATCH /users/5
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "fullName": "Alice Marie Johnson",
  "email": "alice.johnson@example.com"
}

Response:
{
  "id": 5,
  "email": "alice.johnson@example.com",
  "username": "alice123",
  "fullName": "Alice Marie Johnson",
  "role": "USER",
  "createdDate": "2025-12-18T10:00:00",
  "lastModifiedDate": "2025-12-18T14:30:00"
}
```

### Example 3: Admin Creates New Admin User

```bash
POST /users/admin/create-user?role=ADMIN
Authorization: Bearer <ADMIN_ACCESS_TOKEN>
Content-Type: application/json

{
  "email": "bob.admin@example.com",
  "username": "bobadmin",
  "fullName": "Bob Admin",
  "password": "AdminSecure789"
}

Response:
{
  "id": 6,
  "email": "bob.admin@example.com",
  "username": "bobadmin",
  "fullName": "Bob Admin",
  "role": "ADMIN",
  "createdDate": "2025-12-18T15:00:00",
  "lastModifiedDate": "2025-12-18T15:00:00"
}
```

### Example 4: Refresh Expired Access Token

```bash
POST /users/auth/refresh?refreshToken=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhbGljZTEyMyIsImlhdCI6MTcwMjkwNTYwMCwiZXhwIjoxNzAzNTEwMzAwfQ.signature

Response:
{
  "access": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.newAccessToken...",
  "refresh": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.newRefreshToken..."
}
```

### Example 5: Get All Users

```bash
GET /users
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

Response:
[
  {
    "id": 1,
    "email": "admin@example.com",
    "username": "admin",
    "fullName": "System Admin",
    "role": "ADMIN",
    "createdDate": "2025-12-01T08:00:00",
    "lastModifiedDate": "2025-12-01T08:00:00"
  },
  {
    "id": 5,
    "email": "alice.johnson@example.com",
    "username": "alice123",
    "fullName": "Alice Marie Johnson",
    "role": "USER",
    "createdDate": "2025-12-18T10:00:00",
    "lastModifiedDate": "2025-12-18T14:30:00"
  }
]
```



---

## Integration with Other Services

The User Service acts as the authentication provider for the entire platform:

```
┌──────────────────┐
│   User Service   │
│  (Auth Provider) │
└────────┬─────────┘
         │
         │ JWT Tokens
         │
         ├─────────► Main Service (validates JWT)
         │
         ├─────────► Telegram Service (validates JWT)
         │
         ├─────────► Discord Service (validates JWT)
         │
         ├─────────► Gmail Service (validates JWT)
         │
         └─────────► AI Service (validates JWT)
```

### How Other Services Use JWT

Other microservices validate JWT tokens by:
1. Extracting the token from `Authorization` header
2. Verifying the signature using the shared secret key
3. Checking token expiration
4. Extracting user information (ID, username, role)
5. Enforcing role-based access control

---

## Troubleshooting

### Issue: "User already exists" Error

**Solution:** Username or email is already registered. Choose a different username/email or use the existing account.

### Issue: "Invalid password" Error

**Solution:** Password is incorrect. Verify the password and retry.

### Issue: 401 Unauthorized on Authenticated Endpoint

**Solution:** Access token has expired. Use the refresh token endpoint to get a new access token.

### Issue: 403 Forbidden on Admin Endpoint

**Solution:** User does not have ADMIN role. Only administrators can access admin endpoints.

### Issue: Cannot Update User Profile

**Solution:** Ensure you're either:
- Updating your own profile (user ID matches your ID)
- Have ADMIN role to update other users

### Issue: Validation Errors Not Clear

**Solution:** Check the `errors` object in the response body. Each field with a validation error will have a specific message.

### Issue: Refresh Token Not Working

**Solution:** Refresh token may have expired (typically 7 days). User must login again to get new tokens.

---

## Notes

- **Password Hashing**: Uses BCrypt with automatic salt generation
- **Token Algorithm**: JWT with HS256 (HMAC with SHA-256)
- **Database**: User data is persisted in PostgreSQL
- **Timestamps**: All dates are in ISO 8601 format (LocalDateTime)
- **Email Validation**: Uses standard email regex pattern
- **Username Constraints**: 5-25 alphanumeric characters
- **Concurrent Logins**: Multiple sessions allowed per user
- **Session Management**: Stateless (JWT-based, no server-side sessions)
- **Audit Trail**: Timestamps track creation and modification dates

---

## Security Considerations

- **JWT Secret**: Must be kept secure and never exposed
- **HTTPS**: Required in production to protect tokens in transit
- **CORS**: Configured to restrict access from authorized domains
- **SQL Injection**: Protected by JPA/Hibernate parameterized queries
- **XSS**: Frontend must sanitize user input before display
- **Token Leakage**: Tokens should not be logged in plain text
- **Password Security**: Passwords are hashed using BCrypt before storage
