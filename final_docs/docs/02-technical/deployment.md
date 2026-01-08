## Deployment & DevOps

### Infrastructure

#### Deployment Architecture

```
┌─────────────────────────────────────────────────────────┐
│              Azure Container Apps (Production)          │
├─────────────────────────────────────────────────────────┤
│                                                         │
│   ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐   │
│   │  user    │ │ telegram │ │   ai     │ │  gmail   │   │
│   │  :8082   │ │  :8084   │ │  :8085   │ │  :8086   │   │
│   └──────────┘ └──────────┘ └──────────┘ └──────────┘   │
│   ┌──────────┐ ┌──────────┐                             │
│   │ discord  │ │   main   │                             │
│   │  :8088   │ │  :8083   │   (6 Container Apps)        │
│   └──────────┘ └──────────┘                             │
│                      │                                  │
│                      ▼                                  │
│          ┌───────────────────────┐                      │
│          │ PostgreSQL Flexible   │                      │
│          │ Server (B1ms, :5432)  │                      │
│          │ 6 schemas             │                      │
│          └───────────────────────┘                      │
│                                                         │
└─────────────────────────────────────────────────────────┘
                            │
                    [HTTPS/Internet]
```

#### Environments

| Environment | Access | Deployment Branch |
|-------------|--------|-------------------|
| **Development** | `localhost:8083` (Docker Compose) | `feature/*` |
| **Production** | Azure Container Apps (HTTPS) | `main` |

### CI/CD Pipeline

#### Pipeline Overview

```
┌──────────┐   ┌─────────────┐   ┌─────────────────┐
│  Code    │─▶│  Quality    │──▶│  Build / Test   │
│  Push    │   │  Gates      │   │  (Matrix)       │
└──────────┘   └─────────────┘   └─────────────────┘
                                      │
                                      ▼
┌──────────┐   ┌─────────────┐   ┌──────────────┐
│ Health   │◀─│   Azure     │◀──│   Docker     │
│ Check    │   │  Deploy     │   │   Images     │
└──────────┘   └─────────────┘   └──────────────┘

```

#### Pipeline Steps

| Step | Tool | Actions |
|------|------|---------|
| **Code Quality** | Checkstyle, OWASP | Google Java Style, CVE scan (CVSS ≥8 fails) |
| **Build/Test** | Maven, JUnit | 5 services parallel, JaCoCo coverage |
| **Docker Build** | Docker, ACR | 6 images, multi-tag (latest/sha/timestamp) |
| **Deploy** | Azure CLI | Sequential deployment (30s intervals) |
| **Health Check** | Azure CLI | Verify all services "Running" status |

#### Pipeline Configuration

```yaml
# .github/workflows/ci-cd-pipeline.yml
name: CI/CD Pipeline - Microservices

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]
  workflow_dispatch:

jobs:
  code-quality:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Run Checkstyle
        run: mvn checkstyle:check
      - name: OWASP Dependency Check
        run: mvn org.owasp:dependency-check-maven:check

  build-and-test:
    strategy:
      matrix:
        service: [user-service, ai-service, gmail-service, discord-service, main-service]
    steps:
      - name: Build & Test
        run: mvn clean package
```

### Environment Variables

| Variable | Description | Required | Storage |
|----------|-------------|----------|---------|
| `POSTGRES_PASSWORD` | PostgreSQL admin password | Yes | GitHub Secrets |
| `APP_DB_PASSWORD` | Application database password | Yes | GitHub Secrets |
| `JWT_SECRET` | JWT token signing key | Yes | GitHub Secrets |
| `TELEGRAM_ENCRYPTION_KEY` | Telegram credentials encryption | Yes | GitHub Secrets |
| `GMAIL_ENCRYPTION_KEY` | Gmail credentials encryption | Yes | GitHub Secrets |
| `DISCORD_ENCRYPTION_KEY` | Discord credentials encryption | Yes | GitHub Secrets |
| `AI_SERVICE_API_KEY` | OpenAI API key | Yes | GitHub Secrets |
| `SPRING_DATASOURCE_URL` | Database connection string | Yes | Generated per service |
| `SPRING_FLYWAY_DEFAULT_SCHEMA` | Service database schema | Yes | Per service config |

**Secrets Management:** GitHub Secrets (11 total), Azure Key Vault (production), `.env` file (local development)

### How to Run Locally

#### Prerequisites

- [Java 17](https://adoptium.net/) (Eclipse Temurin)
- [Maven 3.8+](https://maven.apache.org/)
- [Docker 20+](https://docker.com/) & Docker Compose
- [PostgreSQL 16](https://postgresql.org/) (via Docker)

#### Setup Steps

```bash
# 1. Clone repository
git clone https://github.com/[your-repo]/communication-platform.git
cd communication-platform

# 2. Set up environment variables
cp .env.example .env
# Edit .env with your credentials:
# - POSTGRES_PASSWORD
# - APP_DB_PASSWORD
# - JWT_SECRET
# - Encryption keys (TELEGRAM/GMAIL/DISCORD)
# - AI_SERVICE_API_KEY (OpenAI)

# 3. Build services (optional - Docker Compose will build)
mvn clean package -DskipTests

# 4. Start all services with Docker Compose
docker-compose up --build

# Services will start in order:
# postgres -> user-service -> telegram-service -> ai-service
# -> gmail-service -> discord-service -> main-service
```

#### Docker Setup (Alternative)

```bash
# Build and run with Docker Compose (recommended)
docker-compose up -d --build

# Or run individual service
cd user-service
mvn clean package
docker build -t user-service .
docker run -p 8082:8082 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/communication_platform \
  user-service
```

#### Verify Installation

After starting services (wait ~2 minutes for initialization):

1. **Main Service (BFF)**: [http://localhost:8083/actuator/health](http://localhost:8083/actuator/health)
2. **User Service**: [http://localhost:8082/swagger-ui.html](http://localhost:8082/actuator/health)
3. **Telegram Service**: [http://localhost:8084/actuator/health](http://localhost:8084/actuator/health)
4. **AI Service**: [http://localhost:8085/actuator/health](http://localhost:8085/actuator/health)
5. **Gmail Service**: [http://localhost:8086/actuator/health](http://localhost:8086/actuator/health)
6. **Discord Service**: [http://localhost:8088/actuator/health](http://localhost:8088/actuator/health)

**Expected Response:** `{"status":"UP"}` from all health endpoints

**Database Access:**
```bash
# Connect to PostgreSQL
psql -h localhost -U postgres -d communication_platform
# Password from .env POSTGRES_PASSWORD

# Verify schemas
\dn
# Should show: user_service, telegram_service, ai_service, 
#              gmail_service, discord_service
```

### Production Deployment

#### Manual Deployment

```bash
# Login to Azure
az login

# Deploy single service (example: user-service)
az containerapp update \
  --name user-service \
  --resource-group rg-communication-platform \
  --image [ACR_SERVER]/user-service:latest

# Verify deployment
az containerapp show \
  --name user-service \
  --resource-group rg-communication-platform \
  --query properties.runningStatus
```

#### Rollback Procedure

```bash
# List revisions
az containerapp revision list \
  --name user-service \
  --resource-group rg-communication-platform \
  --query "[].{Name:name, Active:properties.active, Created:properties.createdTime}"

# Activate previous revision
az containerapp revision activate \
  --revision [previous-revision-name] \
  --resource-group rg-communication-platform
```

### Monitoring & Logging

| Aspect | Tool | Access |
|--------|------|--------|
| **Application Logs** | Azure Container Apps Logs | Azure Portal → Container Apps → Log Stream |
| **Health Checks** | Spring Boot Actuator | `/actuator/health` endpoints |
| **CI/CD Pipeline** | GitHub Actions | Repository → Actions tab |
| **Database Metrics** | Azure PostgreSQL Insights | Azure Portal → Database → Monitoring |
| **Error Tracking** | Application logs (JSON format) | Centralized via Azure Log Analytics |