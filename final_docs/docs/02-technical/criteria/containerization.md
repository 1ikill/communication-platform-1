## Criterion: Containerization & Deployment

### Architecture Decision Record

#### Status

**Status:** Accepted

**Date:** 2025-12-12

#### Context

6-service microservices needs reproducible deployments, service isolation, and simplified local development. Challenge: TDLib native library compilation (Telegram), inter-service dependencies, image size optimization, environment-based config without hardcoded credentials.

#### Decision

**Docker + Docker Compose** orchestration. Dedicated Dockerfile per service with Eclipse Temurin JDK 17 base. Telegram uses multi-stage build with pre-compiled TDLib base image. PostgreSQL 16 official image, `.env` file for config, Actuator health checks, volumes for persistence, restart policies.

#### Alternatives Considered

| Alternative | Pros | Cons | Why Not Chosen |
|-------------|------|------|----------------|
| Kubernetes | Production-grade, auto-scaling | Complex setup, overkill for dev | Too heavy for local development |
| Manual deployment | Simple, no abstraction | Not reproducible, manual config | Violates DevOps principles |
| VM-based (Vagrant) | Full OS isolation | Resource-heavy, slow startup | Containers more efficient |

#### Consequences

**Positive:**
- One-command deployment with consistent environments (`docker-compose up`)
- Service isolation + automated health monitoring
- TDLib base image: 20+ min → 5-8 min build time

**Negative:**
- Requires Docker daemon, multi-stage complexity (Telegram), volume management

### Implementation Details

#### Container Architecture

**Services**: user:8082, telegram:8084, ai:8085, gmail:8086, discord:8088, main:8083
**Database**: PostgreSQL 16:5432, schema-per-service
**Orchestration**: Docker Compose with dependency management

#### Key Implementation Decisions

| Decision | Rationale |
|----------|-----------|
| Eclipse Temurin JDK 17 | Official OpenJDK, LTS, ~250MB |
| Multi-stage Telegram | TDLib libs + app separation |
| TDLib base image | Pre-compile once (20+ → 5-8 min) |
| Health checks | Auto-restart on failure |
| Volumes | PostgreSQL/Telegram persistence |
| `.env` secrets | No hardcoded credentials |

#### Dockerfile Patterns

**Standard Services** (user, ai, gmail, discord, main):
```dockerfile
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE <port>
ENTRYPOINT ["java","-jar","app.jar"]
```

**Telegram Multi-stage**:
1. Base: Pre-built TDLib (C++ libs compiled once)
2. Builder: Maven build + tests with TDLib access
3. Runtime: JDK 17 + native libs + app JAR

#### Docker Compose Features

`depends_on` for startup order, Actuator `/health` checks (30s/3 retries), `unless-stopped` restart, isolated network, schema-specific Flyway migrations.

### Requirements Checklist

| # | Requirement | Status | Evidence/Notes |
|---|-------------|--------|----------------|
| 1 | Dockerfile per service | + | 6 + Telegram multi-stage |
| 2 | Layer optimization | + | .dockerignore, deps first |
| 3 | ENV variables | + | .env file externalization |
| 4 | Volumes for persistence | + | PostgreSQL, Telegram sessions |
| 5 | Port exposure | + | EXPOSE + compose mapping |
| 6 | Image size optimization | + | ~250MB, no dev tools |
| 7 | Non-root user | - | Default JDK user (not explicit) |
| 8 | Health checks | + | Actuator all services |
| 9 | Docker Compose | + | Single-command deploy |
| 10 | Documentation | + | containerization_doc.md |

### Known Limitations

| Limitation | Impact | Potential Solution |
|------------|--------|-------------------|
| No explicit USER | Security risk | Add USER in Dockerfiles |
| Single network | No segmentation | Separate networks per group |
| No resource limits | Resource exhaustion | Add memory/CPU limits |
| Manual TDLib rebuild | Maintenance overhead | Automate base image CI/CD |

### References

- Containerization Documentation: [containerization_doc.md](containerization_doc.md)
- Docker Compose: [docker-compose.yml](../../../docker-compose.yml)
- Telegram Dockerfile: [telegram-service/Dockerfile](../../../telegram-service/Dockerfile)
- Database Init Scripts: [database-init/01-init.sql](../../../database-init/01-init.sql)