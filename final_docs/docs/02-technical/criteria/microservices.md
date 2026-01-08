## Criterion: Microservices Architecture

### Architecture Decision Record

#### Status

**Status:** Accepted

**Date:** 2025-12-12

#### Context

Multi-platform messaging aggregation requires independent platform evolution with different APIs, rate limits, and auth mechanisms. Challenge: balance service autonomy with operational simplicity, enable per-platform scaling and fault isolation while avoiding distributed system complexity.

#### Decision

Implemented **6 microservices** with platform-based bounded contexts: User-Service, Main-Service (BFF), Telegram-Service, Discord-Service, Gmail-Service, AI-Service. Each owns complete data lifecycle with isolated PostgreSQL schema, communicates via synchronous REST through BFF, deploys independently via Docker with Spring Boot Actuator health endpoints.

#### Alternatives Considered

| Alternative | Pros | Cons | Why Not Chosen |
|-------------|------|------|----------------|
| Monolithic | Simple deployment, easy debugging | Coupled deployments, no isolation | Can't scale platforms independently |
| API Gateway pattern | Centralized routing | Extra infrastructure, complexity | BFF simpler for single frontend |
| Event-driven async | Loose coupling, high throughput | Complex errors, eventual consistency | Need immediate feedback |

#### Consequences

**Positive:**
- Fault isolation: Platform failures contained, no cascade (Telegram down ≠ Discord down)
- Independent scaling and deployment per platform usage patterns
- Technology flexibility: Different libraries per platform (TDLib, JDA, Gmail API)
- Parallel development across service teams

**Negative:**
- Distributed debugging complexity
- Network latency for inter-service calls
- Data consistency challenges across services

### Implementation Details

#### Service Decomposition Strategy

| Service | Bounded Context | Port | Key Responsibility |
|---------|----------------|------|-------------------|
| User-Service | Authentication | 8082 | JWT management, user CRUD |
| Main-Service | BFF aggregation | 8083 | Request routing, data composition |
| Telegram-Service | Telegram | 8084 | TDLib wrapper, messages/chats |
| Discord-Service | Discord | 8088 | JDA bot, commands, channels |
| Gmail-Service | Gmail | 8086 | OAuth2, SMTP/IMAP, threads |
| AI-Service | Personalization | 8085 | OpenAI, contact profiles |

#### Architecture Highlights

**BFF Pattern**: Main-Service aggregates platform data, max 2 hops (Frontend → Main → Platform).

**Data Ownership**: Telegram (`telegram_*` tables), Discord (`discord_*`), Gmail (`gmail_*`), AI (`contact_profiles`).

**Deployment**: Docker Compose with PostgreSQL 16, 6 Spring Boot containers, Actuator health checks, env-based config.

### Requirements Checklist

| # | Requirement | Status | Evidence/Notes |
|---|-------------|--------|----------------|
| 1 | Minimum 3 business services | + | 4 platform services (Telegram, Discord, Gmail, AI) |
| 2 | Separate bounded contexts | + | Platform-based decomposition |
| 3 | Independent databases/schemas | + | Schema-per-service (PostgreSQL) |
| 4 | Synchronous API contracts | + | REST OpenAPI 3.0, /api/v1/ versioning |
| 5 | Health/readiness endpoints | + | Actuator /actuator/health |
| 6 | Independent Docker images | + | Dockerfile per service, docker-compose |
| 7 | Correlation ID logging | + | MDC-based tracking |
| 8 | Graceful degradation | + | Isolated failures, partial data |
| 9 | API Gateway/BFF | + | Main-Service BFF |
| 10 | No shared business logic | + | Utilities only |

### Known Limitations

| Limitation | Impact | Potential Solution |
|------------|--------|-------------------|
| Synchronous only | Tight coupling | Add message broker (RabbitMQ/Kafka) |
| No circuit breakers | Cascade failures | Implement Resilience4j |
| Basic monitoring | Limited observability | Add distributed tracing (Jaeger) |
| Single DB instance | Scaling bottleneck | Separate DB instances per service |

### References

- Microservices Documentation: [microservices_doc.md](microservices_doc.md)
- API Specifications: `/api_docs/*_API_DOCUMENTATION.md`
- Deployment Config: [docker-compose.yml](../../../docker-compose.yml)
- Architecture Diagram: [microservices_doc.md#3-architectural-diagram](microservices_doc.md#3-architectural-diagram)