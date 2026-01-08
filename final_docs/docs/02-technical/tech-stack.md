## Technology Stack

### Core Technologies

#### Backend

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17 | Primary language (LTS) |
| Spring Boot | 3.x | Application framework |
| Spring Security | 6.x | Authentication/authorization |
| Maven | 3.x | Build & dependency management |
| JUnit 5 | 5.x | Unit testing |
| Mockito | 5.x | Mocking framework |

#### Database

| Technology | Version | Purpose |
|------------|---------|---------|
| PostgreSQL | 16 | Primary RDBMS |
| Flyway | 9.x | Database migrations |
| Hibernate/JPA | 6.x | ORM framework |
| HikariCP | 5.x | Connection pooling |

#### Platform Integrations

| Technology | Version | Purpose |
|------------|---------|---------|
| TDLib | 1.8.1 | Telegram API (C++ native) |
| Discord JDA | 6.1.0 | Discord bot integration |
| Gmail API | v1-rev110-1.25.0 | Google email integration |
| OpenAI API | GPT-4o-mini, GPT-4.1-mini| AI message personalization |

#### DevOps & Infrastructure

| Technology | Version | Purpose |
|------------|---------|---------|
| Docker | 20+ | Containerization |
| Docker Compose | 2.x | Local orchestration |
| GitHub Actions | - | CI/CD automation |
| Azure Container Apps | - | Cloud deployment |
| Azure Container Registry | - | Image storage |

#### API & Documentation

| Technology | Version | Purpose |
|------------|---------|---------|
| SpringDoc OpenAPI | 2.x | API specification |
| Swagger UI | 3.x | Interactive API docs |
| OpenAPI | 3.0 | API standard |

### Architecture Decisions

#### Microservices (6 Services)
- **Pattern**: Platform-based bounded contexts
- **Communication**: Synchronous REST via BFF (Main-Service)
- **Data**: Schema-per-service (PostgreSQL)
- **Rationale**: Platform independence, independent scaling, fault isolation

#### Database Strategy
- **Approach**: Single PostgreSQL with multiple schemas
- **Migration**: Flyway per service
- **Access**: Role-based (app_admin, app_user)
- **Rationale**: Logical isolation + operational simplicity

#### Containerization
- **Base Image**: Eclipse Temurin JDK 17
- **Telegram**: Multi-stage build with TDLib base (20+ min → 2-5 min)
- **Health**: Spring Boot Actuator all services
- **Rationale**: Reproducible deployments, one-command startup

#### CI/CD Pipeline
- **Platform**: GitHub Actions (zero cost)
- **Stages**: Quality → Build/Test (matrix) → Docker → Deploy → Health
- **Security**: OWASP (CVSS ≥8 fails), GitHub Secrets
- **Rationale**: Automated quality gates, parallel builds, Azure integration

#### AI Integration
- **Provider**: OpenAI API (GPT-4o-mini)
- **Pattern**: Dedicated microservice with contact profiles
- **Storage**: PostgreSQL (relationship/tone metadata)
- **Rationale**: Quality vs cost balance, no infrastructure overhead

#### API Documentation
- **Strategy**: 3 layers (Code → Service → Integration)
- **Auto-gen**: SpringDoc annotations → Swagger UI
- **Format**: Markdown per service + OpenAPI 3.0
- **Rationale**: Auto-sync eliminates drift, interactive testing

### Security

- **Authentication**: JWT (access + refresh tokens)
- **Encryption**: AES-256 for credentials, BCrypt for passwords
- **Secrets**: GitHub Secrets, Azure Key Vault, .env files
- **HTTPS**: Enforced for all external communication
- **RBAC**: Role-based access (USER, ADMIN)

### Testing

- **Unit**: JUnit 5 + Mockito (all services)
- **Coverage**: JaCoCo (30%+ target)
- **Integration**: Docker-based tests (Telegram)
- **API**: Swagger UI manual testing
- **CI**: Automated on every push/PR

### Deployment

- **Local**: `docker-compose up` (7 containers)
- **Production**: Azure Container Apps (6 services)
- **Database**: Azure PostgreSQL Flexible Server (B1ms)
- **Registry**: Azure Container Registry (ACR)
- **Strategy**: Sequential deployment (30s intervals, DB pool limits)

### Known Technology Constraints

| Constraint | Impact | Mitigation |
|------------|--------|------------|
| TDLib not in Maven | Complex build | Pre-built base image strategy |
| PostgreSQL B1ms (50 conn) | Deployment order | Sequential with 30s waits |
| Synchronous REST only | Tight coupling | Future: message broker |
| No service mesh | Manual config | Acceptable for current scale |