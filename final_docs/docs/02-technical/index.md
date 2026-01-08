# Technical

## Technical Implementation

This section covers the technical architecture, design decisions, and implementation details.

### Contents

- [Tech Stack](tech-stack.md)
- [Criteria Documentation](criteria/) - ADR for each evaluation criterion
- [Deployment](deployment.md)

### Solution Architecture

#### High-Level Architecture

```
┌───────────────────────────────────────────────────────────┐
│              Communication Platform (Microservices)       │
├───────────────────────────────────────────────────────────┤
│                                                           │
│   ┌──────────────┐                                        │
│   │ Main-Service │  (BFF - Port 8083)                     │
│   │   (API GW)   │                                        │
│   └──────┬───────┘                                        │
│          │                                                │
│    ──────┴──────────────────────────────────              │
│    │         │         │         │         │              │
│   ┌▼───┐   ┌─▼──┐   ┌──▼─┐   ┌──▼──┐   ┌──▼──┐            │
│   │User│   │Tele│   │ AI │   │Gmail│   │Disc │            │
│   │8082│   │8084│   │8085│   │8086 │   │8088 │            │
│   └──┬─┘   └─┬──┘   └──┬─┘   └──┬──┘   └──┬──┘            │
│      │       │         │        │         │               │
│      └───────┴─────────┴────────┴─────────┘               │
│                        │                                  │
│                        ▼                                  │
│          ┌─────────────────────────┐                      │
│          │   PostgreSQL 16         │                      │
│          │   (6 schemas)           │                      │
│          └─────────────────────────┘                      │
│                                                           │
│External: Telegram API, Discord API, Gmail API, OpenAI API │
│                                                           │
└───────────────────────────────────────────────────────────┘
```

#### System Components

| Component | Description | Technology |
|-----------|-------------|------------|
| **Main-Service** | BFF/API Gateway - request routing & aggregation | Spring Boot 3.x |
| **User-Service** | Authentication, JWT management, user CRUD | Spring Boot 3.x, Spring Security |
| **Telegram-Service** | Telegram integration via TDLib | Spring Boot 3.x, TDLib 1.8.1 |
| **Discord-Service** | Discord bot integration | Spring Boot 3.x, JDA 6.1.0 |
| **Gmail-Service** | Gmail API integration with OAuth2 | Spring Boot 3.x, Gmail API v1 |
| **AI-Service** | Message personalization with OpenAI | Spring Boot 3.x, OpenAI API |
| **Database** | Multi-schema PostgreSQL (schema-per-service) | PostgreSQL 16, Flyway |

#### Data Flow

```
[User Request] → [Main-Service BFF] → [Route to Platform Service]
                                              │
                                              ▼
                                     [Service Business Logic]
                                              │
                                              ▼
                                     [PostgreSQL Schema]
                                              │
                                              ▼
                                     [Service Response]
                                              │
[Client Response] ← [Main-Service] ←──────────┘
```

### Key Technical Decisions

| Decision | Rationale | Alternatives Considered |
|----------|-----------|------------------------|
| **Microservices (6 services)** | Platform independence, fault isolation, independent scaling | Monolith, modular monolith |
| **PostgreSQL multi-schema** | Logical isolation + operational simplicity | Separate DBs per service, NoSQL |
| **Docker + Compose** | Reproducible deployments, one-command startup | Kubernetes, manual deployment |
| **GitHub Actions CI/CD** | Zero cost, native GitHub integration, Azure support | Jenkins, GitLab CI, Azure DevOps |
| **OpenAI API (GPT-4o-mini)** | Quality vs cost balance | Local LLM, Claude, rule-based |
| **SpringDoc/OpenAPI 3.0** | Auto-sync with code, interactive Swagger UI | Manual docs, Postman only |

### Security Overview

| Aspect | Implementation |
|--------|----------------|
| **Authentication** | JWT (access + refresh tokens), BCrypt password hashing |
| **Authorization** | RBAC (USER, ADMIN roles), Spring Security |
| **Data Protection** | AES-256 for credentials, TLS/HTTPS for transit |
| **Input Validation** | Bean Validation (@Valid), DTO validation |
| **Secrets Management** | GitHub Secrets, Azure Key Vault, .env files (local) |
| **Security Scanning** | OWASP Dependency Check (CVSS ≥8 fails build) |