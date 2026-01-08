## Project Scope

### In Scope

| Feature | Description | Priority |
|---------|-------------|----------|
| Java Backend | Spring Boot independent microservices | M |
| Telegram Integration | TDLib integration for messaging | M |
| Gmail Integration | OAuth2 Gmail API for messaging | M |
| Discord Integration | JDA bot management for messagins | M |
| User Authentication | JWT authentication with access/refresh tokens and RBAC | M |
| Broadcast Messaging | Multi-recipient personalized messaging | M |
| AI Message Personalization | AI-powered message adaptation via contact profiles | M |
| Contact Profile Management | Manage recipient profiles for personalization | M |
| Multi-Account Support | Multiple accounts per platform | S |
| Secure Credential Storage | AES encryption for credentials | M |
| API Documentation | Swagger/OpenAPI documentation | S |
| Database Management | PostgreSQL with Flyway migrations | M |

### Out of Scope

| Feature | Reason | When Possible |
|---------|--------|---------------|
| WhatsApp Integration | API restrictions | Future phase |
| Microsoft Teams Integration | Time constraints | Future phase |
| Data Analytics Dashboard | Beyond MVP scope | Future phase |
| Mobile Application | Backend-focused project | Future phase |
| Voice/Video Calls | Not core to messaging unification | Never |
| Social Media Integration | Outside business communication scope | Never |
| Built-in CRM Features | Beyond platform scope | Future phase |
| Advanced Reporting | Beyond basic tracking | Future phase |

### Assumptions

| # | Assumption | Impact if Wrong | Probability |
|---|------------|-----------------|-------------|
| 1 | Platform APIs and OAuth tokens remain stable | Major rework, re-authentication | Low |
| 2 | Infrastructure available for hosting | Deployment delays | Low |
| 3 | OpenAI API accessible | Need alternative provider | Low |
| 4 | Users have valid platform credentials | Requires user action | Medium |

### Constraints

Limitations that affect the project:

| Constraint Type | Description | Mitigation |
|-----------------|-------------|------------|
| **Time** | Diploma project timeframe (3-4 months) | Prioritize Must-Have using MoSCoW |
| **Budget** | No commercial licenses | Use open-source tools, free-tier APIs |
| **Technology** | Java-based stack requirement | Leverage Java ecosystem, Spring Cloud |
| **Resources** | Single-developer project | Modular architecture, comprehensive testing |
| **Security** | GDPR and ISO/IEC 27001 compliance | AES/BCrypt encryption, JWT, OAuth |
| **External APIs** | Rate limits and quotas | Retry logic, queue mgmt, respect limits |
| **Platform Policies** | Terms of service compliance | Regular policy review |

### Dependencies

| Dependency | Type | Owner | Status |
|------------|------|-------|--------|
| Telegram TDLib API | External | Telegram | + |
| Google Gmail API | External | Google Cloud | + |
| Discord JDA Library | External | Discord4J Team | + |
| OpenAI API | External | OpenAI | + |
| PostgreSQL Database | Technical | PostgreSQL Global Development Group | + |
| Spring Boot Framework | Technical | VMware/Spring Team | + |
| Docker & Docker Compose | Technical | Docker Inc. | + |
| Flyway Migrations | Technical | Redgate | + |
| Maven Build Tool | Technical | Apache Foundation | + |