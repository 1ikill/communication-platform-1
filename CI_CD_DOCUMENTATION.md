# CI/CD Implementation Documentation

**Project:** Communication Platform  
**Date:** December 2025

---

## Table of Contents

1. [Executive Summary](#executive-summary)
2. [Tool Selection & Justification](#tool-selection--justification)
3. [Pipeline Architecture](#pipeline-architecture)
4. [Implementation Details](#implementation-details)
5. [Quality Gates & Security](#quality-gates--security)
6. [Deployment Strategy](#deployment-strategy)
7. [Artifacts & Environments](#artifacts--environments)
8. [Pipeline Diagrams](#pipeline-diagrams)
9. [Grade Requirements Mapping](#grade-requirements-mapping)

---

## Executive Summary

This document describes the complete CI/CD automation for a microservices-based communication platform consisting of 6 services (user-service, telegram-service, ai-service, gmail-service, discord-service, main-service) deployed on Azure Container Apps.

**Key Achievements:**
- Fully automated CI/CD pipeline with 6 stages
- Zero-downtime deployments with health checks
- Security scanning (OWASP Dependency Check)
- Parallel execution (6 services build simultaneously)
- Optimized build times (telegram-service: 5-8 min using base image strategy)
- Quality gates (code style, tests, coverage, security)
- Comprehensive documentation with diagrams

---

## Tool Selection & Justification

### Selected Platform: GitHub Actions

**Decision Rationale:**

1. **Native Integration**
   - Built into GitHub (no external service setup)
   - Direct repository access without authentication complexity
   - Automatic webhook triggers on push/PR

2. **Cost Effectiveness**
   - Free for public repositories (unlimited minutes)
   - 2000 free minutes/month for private repositories
   - Student account benefits (GitHub Education Pack)
   - No infrastructure costs

3. **Azure Ecosystem**
   - First-class Azure support via official actions (`azure/login`, `azure/docker-login`, `azure/CLI`)
   - Azure Container Registry integration
   - Azure Container Apps deployment support

4. **Feature Completeness**
   - Matrix builds (parallel execution)
   - Artifact management (upload/download between jobs)
   - Secrets management (encrypted GitHub Secrets)
   - Environment support (production/development)
   - Manual workflow dispatch

5. **Developer Experience**
   - YAML-based configuration (infrastructure as code)
   - Excellent documentation
   - Large community and marketplace
   - Visual workflow editor and logs

**Alternatives Considered:**

| Platform | Pros | Cons | Decision |
|----------|------|------|----------|
| **Jenkins** | Self-hosted, full control, plugins | Infrastructure cost, maintenance overhead, complex setup |  Rejected |
| **GitLab CI** | Integrated, powerful features | Requires GitLab (project on GitHub), additional service |  Rejected |
| **Azure DevOps** | Azure-native, advanced features | Learning curve, separate platform, overkill for project |  Rejected |
| **GitHub Actions** | Native, free, easy setup | Minutes limit (not issue for this project) |  **Selected** |

---

## Pipeline Architecture

### Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                        CI/CD PIPELINE                           │
│                                                                 │
│  Trigger: Push to main OR Manual dispatch                       │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ STAGE 1: Code Quality (Sequential)                              │
│  • Checkstyle (Google Java Style)                               │
│  • OWASP Dependency Check (CVSS ≥8 fails)                       │
│  • Security report artifact (30 days retention)                 │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ STAGE 2: Build & Test (Parallel Matrix - 5 services)            │
│  ┌──────────┬──────────┬──────────┬──────────┬──────────┐       │
│  │  user    │    ai    │  gmail   │ discord  │   main   │       │
│  │ service  │ service  │ service  │ service  │ service  │       │
│  ├──────────┼──────────┼──────────┼──────────┼──────────┤       │
│  │ • Maven  │ • Maven  │ • Maven  │ • Maven  │ • Maven  │       │
│  │ • JUnit  │ • JUnit  │ • JUnit  │ • JUnit  │ • JUnit  │       │
│  │ • JaCoCo │ • JaCoCo │ • JaCoCo │ • JaCoCo │ • JaCoCo │       │
│  │ • Upload │ • Upload │ • Upload │ • Upload │ • Upload │       │
│  │   JAR    │   JAR    │   JAR    │   JAR    │   JAR    │       │
│  └──────────┴──────────┴──────────┴──────────┴──────────┘       │
│                                                                 │
│  Artifacts: JAR files (7 days), Test reports (30 days)          │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ STAGE 3: Verify telegram-service (Sequential)                   │
│  • Dockerfile validation (no Maven build - TDLib not in central)│
│  • Tests run inside Docker during image build                   │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ STAGE 4: Build & Push Docker Images (Parallel Matrix - 6)       │
│  ┌──────────┬──────────┬──────────┬──────────┬──────────┬─────┐ │
│  │  user    │ telegram │    ai    │  gmail   │ discord  │main │ │
│  ├──────────┼──────────┼──────────┼──────────┼──────────┼─────┤ │
│  │ Download │ Use base │ Download │ Download │ Download │Down │ │
│  │ JAR      │ image*   │ JAR      │ JAR      │ JAR      │ JAR │ │
│  │ Build    │ Build    │ Build    │ Build    │ Build    │Bld  │ │
│  │ Push ACR │ Push ACR │ Push ACR │ Push ACR │ Push ACR │Push │ │
│  └──────────┴──────────┴──────────┴──────────┴──────────┴─────┘ │
│                                                                 │
│  *telegram uses telegram-tdlib-base:latest (pre-built TDLib)    │
│  Tags: latest, git-sha (7 chars), timestamp                     │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ STAGE 5: Deploy to Azure (Sequential with waits)                │
│  1. user-service      → Wait 30s                                │
│  2. telegram-service  → Wait 30s                                │
│  3. ai-service        → Wait 30s                                │
│  4. gmail-service     → Wait 30s                                │
│  5. discord-service   → Wait 30s                                │
│  6. main-service      (final)                                   │
│                                                                 │
│  • Azure Container Apps update with revision suffix             │
│  • Environment variables injection                              │
│  • Database connection strings                                  │
│  • Secrets (JWT, encryption keys, API keys)                     │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ STAGE 6: Health Check & Verification                            │
│  • Verify all services status = "Running"                       │
│  • Check service URLs accessible                                │
│  • Report deployment success/failure                            │
└─────────────────────────────────────────────────────────────────┘
```

---

## Implementation Details

### Continuous Integration (CI)

#### 1. Code Quality Job

**Purpose:** Enforce code standards and identify security vulnerabilities early.

**Tools:**
- **Maven Checkstyle Plugin** - Google Java Style validation
- **OWASP Dependency Check** - CVE scanning (CVSS threshold ≥8)

**Configuration:**
```yaml
- name: Run Checkstyle
  run: mvn checkstyle:check

- name: Run OWASP Dependency Check
  run: mvn org.owasp:dependency-check-maven:check
```

**Failure Conditions:**
- Checkstyle violations found → Fail
- High/Critical CVEs detected (CVSS ≥8) → Fail

#### 2. Build & Test Jobs (Matrix Strategy)

**Purpose:** Build and test 5 services in parallel (telegram-service excluded due to TDLib Maven dependency unavailability).

**Matrix Configuration:**
```yaml
strategy:
  matrix:
    service:
      - user-service
      - ai-service
      - gmail-service
      - discord-service
      - main-service
  fail-fast: false
```

**Steps per Service:**
1. Checkout code
2. Setup JDK 17 (Temurin distribution)
3. Maven dependency caching
4. Maven build: `mvn clean package`
5. Run tests: JUnit automatically executed
6. Code coverage: JaCoCo plugin
7. Upload artifacts:
   - JAR file (7 days retention)
   - Test results (30 days retention)

**Test Coverage:** 30% line coverage minimum (JaCoCo configured but not enforcing in current implementation).

#### 3. telegram-service Validation

**Special Handling:** TDLib (Telegram Database Library) is not available in Maven Central. It must be compiled from source.

**Solution Implemented:**
- **Pre-built base image strategy**: `telegram-tdlib-base:latest` stored in ACR
- telegram-service Dockerfile uses base image via `FROM ${ACR_SERVER}/telegram-tdlib-base:latest`
- Tests run inside Docker build (Maven has access to TDLib from base image)
- Build time reduced from 20+ min to 5-8 min

**CI Job:** Only validates Dockerfile existence (actual build happens in Stage 4).

### Continuous Deployment (CD)

#### 4. Docker Image Build Job

**Purpose:** Build production Docker images for all 6 services.

**Process:**
1. **Artifact Download** (non-telegram services):
   - Download JAR files from build-and-test job
   - Place in `{service}/target/` directory for Dockerfile COPY

2. **Docker Build**:
   ```bash
   docker build \
     --build-arg BUILD_DATE=<timestamp> \
     --build-arg VCS_REF=<git-sha> \
     --build-arg ACR_SERVER=<acr-server> \
     -t <acr>/service:latest \
     -t <acr>/service:<short-sha> \
     -t <acr>/service:<timestamp> \
     ./service
   ```

3. **Multi-tag Strategy**:
   - `latest` - Always points to most recent build
   - `<git-sha>` - 7-char commit hash for traceability
   - `<timestamp>` - YYYYMMDD-HHMMSS for rollback capability

4. **Push to ACR**: All three tags pushed to Azure Container Registry

**telegram-service Special Build:**
- Uses `ARG ACR_SERVER` to reference base image
- Dockerfile: `FROM ${ACR_SERVER}/telegram-tdlib-base:latest AS tdlib-builder`
- Maven build happens inside Docker with TDLib available
- Tests execute: `RUN mvn test`
- Final image: JDK 17 + TDLib native libraries + application JAR

#### 5. Azure Deployment Job

**Purpose:** Deploy all services to Azure Container Apps with zero-downtime.

**Deployment Order (Sequential with 30s waits):**
```
user-service → 30s → telegram-service → 30s → ai-service → 30s 
→ gmail-service → 30s → discord-service → 30s → main-service
```

**Reason for Sequential:** 
- Database connection pool limits (PostgreSQL B1ms: max 50 connections)
- Each service configured for max 5 connections, min 2 idle
- Sequential deployment prevents connection pool exhaustion
- 30-second waits allow service stabilization

**Deployment Command:**
```bash
az containerapp update \
  --name <service-name> \
  --resource-group rg-communication-platform \
  --image "<ACR>/service:latest" \
  --revision-suffix <timestamp> \
  --set-env-vars \
    "SPRING_DATASOURCE_URL=jdbc:postgresql://..." \
    "APP_DB_PASSWORD=***" \
    "JWT_SECRET=***" \
    "SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=5" \
    "SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE=2"
```

**Revision Suffix:** Timestamp-based suffix forces Azure to create new revision even if image digest is identical (ensures deployments are visible in revision history).

**Environment Variables Injected:**
- Database connection strings (PostgreSQL FQDN, schema)
- Secrets (JWT, encryption keys, API keys)
- Flyway migration settings
- Connection pool limits

**Zero-Downtime:** Azure Container Apps creates new revision, routes traffic gradually, keeps old revision running until new one is healthy.

#### 6. Health Check Job

**Purpose:** Verify deployment success.

**Checks:**
- Service status: `az containerapp show --query properties.runningStatus`
- Expected status: `"Running"`
- All 6 services must be running
- Job fails if any service is not running

---

## Quality Gates & Security

### Code Quality

| Gate | Tool | Threshold | Action on Failure |
|------|------|-----------|-------------------|
| Code Style | Maven Checkstyle | Google Java Style | Fail build |
| Test Execution | JUnit | All tests pass | Fail build |
| Code Coverage | JaCoCo | 30% line coverage | Report only (not enforcing) |

### Security Scanning

**OWASP Dependency Check:**
- Scans all Maven dependencies for known CVEs
- Fails build if CVSS score ≥8 (High/Critical)
- Generates HTML report uploaded as artifact (30 days retention)
- Database updated automatically during each run

**Secrets Management:**
- All secrets stored in GitHub Secrets (encrypted at rest)
- Never committed to repository
- Injected at deployment time
- Azure Service Principal with least-privilege (contributor role scoped to resource group)

**Required Secrets (11 total):**
```
AZURE_CREDENTIALS         - Service Principal JSON
ACR_NAME                  - Container registry name
ACR_USERNAME              - Registry username
ACR_PASSWORD              - Registry password
ACR_LOGIN_SERVER          - Registry FQDN
APP_DB_PASSWORD           - PostgreSQL password
JWT_SECRET                - JWT signing key
TELEGRAM_ENCRYPTION_KEY   - Telegram data encryption
GMAIL_ENCRYPTION_KEY      - Gmail data encryption
DISCORD_ENCRYPTION_KEY    - Discord data encryption
AI_SERVICE_API_KEY        - AI service API key
```

### Testing Strategy

| Service | Test Location | Coverage |
|---------|---------------|----------|
| user-service | CI (Maven) | JUnit + JaCoCo |
| ai-service | CI (Maven) | JUnit + JaCoCo |
| gmail-service | CI (Maven) | JUnit + JaCoCo |
| discord-service | CI (Maven) | JUnit + JaCoCo |
| main-service | CI (Maven) | JUnit + JaCoCo |
| telegram-service | Docker build (Maven) | JUnit (TDLib available from base) |

---

## Deployment Strategy

### Environment Configuration

**Production Environment:**
- **Platform:** Azure Container Apps
- **Region:** Determined by resource group
- **Database:** PostgreSQL Flexible Server (B1ms, 1 vCore, 2GB RAM)
- **Registry:** Azure Container Registry (Basic SKU)
- **Networking:** Public ingress, HTTPS enabled
- **Scaling:** Manual (min 1, max 1 replica per service)

### Database Connection Management

**Challenge:** PostgreSQL B1ms limits: ~50 max connections total

**Solution:** Connection pool configuration per service
```properties
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.minimum-idle=2
```

**Calculation:**
- 6 services × 5 max connections = 30 connections
- Leaves ~20 for admin, migrations, monitoring
- Sequential deployment prevents spikes

### Rollback Strategy

**Manual Rollback:**
1. Identify previous working revision:
   ```bash
   az containerapp revision list --name <service> \
     --resource-group rg-communication-platform
   ```

2. Activate previous revision:
   ```bash
   az containerapp revision activate \
     --name <service> \
     --resource-group rg-communication-platform \
     --revision <revision-name>
   ```

**Tag-based Rollback:**
1. Find previous commit SHA from Git history
2. Deploy previous image tag:
   ```bash
   az containerapp update --name <service> \
     --image "<ACR>/service:<git-sha>"
   ```

**Automated Rollback:** Not implemented (future enhancement).

---

## Artifacts & Environments

### Build Artifacts

| Artifact Type | Contents | Retention | Purpose |
|---------------|----------|-----------|---------|
| JAR Files | `{service}-1.0-SNAPSHOT.jar` | 7 days | Application binaries for Docker build |
| Test Reports | JUnit XML, Surefire reports | 30 days | Test result analysis, debugging failures |
| Security Reports | OWASP HTML report | 30 days | Vulnerability tracking, compliance |
| Docker Images | Multi-stage images | Permanent (ACR) | Deployment artifacts |

### Docker Images

**Storage:** Azure Container Registry (Basic tier, $0.167/day)

**Tags per Image:**
- `latest` - Continuous deployment target
- `<git-sha>` - Version tracking (e.g., `a3f2b1c`)
- `<timestamp>` - Rollback identifier (e.g., `20251218-142530`)

**Special Image:**
- `telegram-tdlib-base:latest` - Pre-built TDLib (580MB, built once)
- `telegram-tdlib-base:1.8.42` - Version-tagged base

**Size Estimates:**
- user-service: ~280MB
- telegram-service: ~320MB (includes TDLib native libs)
- ai-service: ~290MB
- gmail-service: ~285MB
- discord-service: ~285MB
- main-service: ~295MB

### Environments

**Production:**
- Trigger: Push to `main` branch
- Auto-deployment: Yes
- URL: `https://<service>.{region}.azurecontainerapps.io`
- Database: Shared PostgreSQL (separate schemas per service)

**Manual Deployment:**
- Trigger: Workflow dispatch (GitHub Actions UI)
- Service selection: Dropdown menu
- Environment selection: production/development (development not configured)

---

## Pipeline Diagrams

### CI/CD Flow Diagram

```
┌────────────────────────────────────────────────────────────────────┐
│                                                                    │
│                    Developer Push to main                          │
│                                                                    │
└────────────────┬───────────────────────────────────────────────────┘
                 │
                 ▼
        ┌────────────────┐
        │  GitHub Webhook │
        │   Trigger       │
        └────────┬────────┘
                 │
                 ▼
┌────────────────────────────────────────────────────────────────────┐
│ Job 1: code-quality (ubuntu-latest)                    ~2-3 min    │
│ ┌────────────────────────────────────────────────────────────────┐ │
│ │ Checkout → JDK 17 → Maven Cache → Checkstyle → OWASP Check     │ │
│ └────────────────────────────────────────────────────────────────┘ │
│                                                                    │
│ Outputs: security-reports artifact                                 │
└────────────────┬───────────────────────────────────────────────────┘
                 │
                 ▼
┌────────────────────────────────────────────────────────────────────┐
│ Job 2: build-and-test (Matrix, ubuntu-latest)         ~3-4 min     │
│                                                                    │
│     ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌──────┐   │
│     │  user   │  │   ai    │  │  gmail  │  │ discord │  │ main │   │
│     └────┬────┘  └────┬────┘  └────┬────┘  └────┬────┘  └───┬──┘   │
│          │            │            │            │           │      │
│          └────────────┴────────────┴────────────┴───────────┘      │
│                              │                                     │
│               Build → Test → Package → Upload JAR                  │
│                                                                    │
│ Outputs: {service}-jar artifacts (5), test reports                 │
└────────────────┬───────────────────────────────────────────────────┘
                 │
                 ▼
┌────────────────────────────────────────────────────────────────────┐
│ Job 3: build-telegram-service (ubuntu-latest)          ~1 min      │
│ ┌────────────────────────────────────────────────────────────────┐ │
│ │ Verify Dockerfile exists (no Maven build)                      │ │
│ └────────────────────────────────────────────────────────────────┘ │
└────────────────┬───────────────────────────────────────────────────┘
                 │
                 ▼
┌────────────────────────────────────────────────────────────────────┐
│ Job 4: build-docker-images (Matrix, ubuntu-latest)   ~5-8 min      │
│                                                                    │
│ ┌────────┐  ┌────────┐  ┌────────┐  ┌────────┐  ┌────────┐  ┌──┐   │
│ │ user   │  │telegram│  │  ai    │  │ gmail  │  │discord │  │..│   │
│ └───┬────┘  └───┬────┘  └───┬────┘  └───┬────┘  └───┬────┘  └──┘   │
│     │           │           │           │           │              │
│     │           │           │           │           │              │
│  Download    Use base   Download   Download   Download             │
│    JAR       image      JAR        JAR        JAR                  │
│     │           │           │           │           │              │
│  Docker      Docker     Docker     Docker     Docker               │
│  Build       Build      Build      Build      Build                │
│     │           │           │           │           │              │
│  Push        Push       Push       Push       Push                 │
│  3 tags      3 tags     3 tags     3 tags     3 tags               │
│                                                                    │
│ Images: {service}:latest, {service}:<sha>, {service}:<timestamp>   │
└────────────────┬───────────────────────────────────────────────────┘
                 │
                 ▼
┌────────────────────────────────────────────────────────────────────┐
│ Job 5: deploy-to-azure (ubuntu-latest)               ~3-4 min      │
│                                                                    │
│  Azure Login → Get ACR Creds → Get DB Connection                   │
│                                                                    │
│  Sequential Deployment:                                            │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │ 1. user-service       → Update → Wait 30s                    │  │
│  │ 2. telegram-service   → Update → Wait 30s                    │  │
│  │ 3. ai-service         → Update → Wait 30s                    │  │
│  │ 4. gmail-service      → Update → Wait 30s                    │  │
│  │ 5. discord-service    → Update → Wait 30s                    │  │
│  │ 6. main-service       → Update                               │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                    │
│  Each update:                                                      │
│   - New revision with timestamp suffix                             │
│   - Environment variables injection                                │
│   - Image: {service}:latest                                        │
└────────────────┬───────────────────────────────────────────────────┘
                 │
                 ▼
┌────────────────────────────────────────────────────────────────────┐
│ Job 6: health-check (ubuntu-latest)                   ~30 sec      │
│ ┌────────────────────────────────────────────────────────────────┐ │
│ │ Check each service status = "Running"                          │ │
│ │ Fail if any service not running                                │ │
│ │ Report success                                                 │ │
│ └────────────────────────────────────────────────────────────────┘ │
└────────────────┬───────────────────────────────────────────────────┘
                 │
                 ▼
        ┌────────────────┐
        │    SUCCESS     │
        │  All services  │
        │    deployed    │
        └────────────────┘

Total Pipeline Time: ~12-15 minutes
```

### Deployment Flow with Zero-Downtime

```
┌─────────────────────────────────────────────────────────────┐
│  Azure Container Apps - Zero-Downtime Deployment            │
└─────────────────────────────────────────────────────────────┘

Before Deployment:
┌──────────────────────────────────────────┐
│ Revision: user-service--20251217-110000  │
│ Traffic: 100%                            │
│ Status: Active                           │
│ Replicas: 1                              │
└──────────────────────────────────────────┘
           │
           │  az containerapp update --revision-suffix 20251218-140000
           ▼
┌──────────────────────────────────────────┐
│ Azure Creates New Revision               │
│ Revision: user-service--20251218-140000  │
│ Status: Provisioning                     │
└──────────────────────────────────────────┘
           │
           │  Health probe checks
           ▼
┌──────────────────────────────────────────┐
│ New Revision: Ready                      │
│ Traffic: 0% → 25% → 50% → 100%           │
│ (Gradual traffic shift)                  │
└──────────────────────────────────────────┘
           │
           │  Old revision traffic: 100% → 0%
           ▼
┌──────────────────────────────────────────┐  ┌────────────────────────┐
│ New Revision: user-service--20251218     │  │ Old Revision: Inactive │
│ Traffic: 100%                            │  │ (Kept for rollback)    │
│ Status: Active                           │  └────────────────────────┘
│ Replicas: 1                              │
└──────────────────────────────────────────┘

Result: No downtime, instant rollback capability
```

### Secrets Management Flow

```
┌────────────────────────────────────────────────────────────────┐
│                  Secrets Management Flow                       │
└────────────────────────────────────────────────────────────────┘

Local Development:
┌──────────────────────┐
│   .env file          │  ← Never committed (in .gitignore)
│  (Local secrets)     │
└──────────────────────┘

Azure Resources:
┌────────────────────────────────────┐
│ Azure Portal / Azure CLI           │
│  • PostgreSQL password             │
│  • ACR credentials                 │
│  • Service Principal JSON          │
└──────────────┬─────────────────────┘
               │
               │  Manual
               ▼
┌────────────────────────────────────┐
│ GitHub Repository Settings         │
│  Settings → Secrets and variables  │
│  → Actions → Repository secrets    │
│                                    │
│  [New repository secret]           │
│   Name: AZURE_CREDENTIALS          │
│   Value: {encrypted JSON}          │
└──────────────┬─────────────────────┘
               │
               │  Reference in workflow: ${{ secrets.AZURE_CREDENTIALS }}
               ▼
┌────────────────────────────────────┐
│ GitHub Actions Runner              │
│  • Secrets decrypted at runtime    │
│  • Never logged or visible         │
│  • Masked in output (*** ****)     │
└──────────────┬─────────────────────┘
               │
               │  Passed as environment variables
               ▼
┌────────────────────────────────────┐
│ Azure Container Apps               │
│  Environment Variables:            │
│   APP_DB_PASSWORD=***              │
│   JWT_SECRET=***                   │
│   (Encrypted at rest)              │
└────────────────────────────────────┘

Security: End-to-end encryption, no secrets in code/logs
```

---

## Grade Requirements Mapping

### Minimum Requirements - ALL IMPLEMENTED 

| # | Requirement | Implementation | Status |
|---|-------------|----------------|--------|
| 1 | Documentation with diagrams | This document with ASCII diagrams | done   |
| 2 | Tool description | GitHub Actions (see Tool Selection section) | done   |
| 3 | Automation justification | Cost, integration, features (see Justification) | done   |
| 4 | Artifact list | JARs, test reports, security reports, Docker images | done   |
| 5 | Environment description | Azure Container Apps (production) | done   |
| 6 | 3+ CI stages | Code quality, build, test, verify telegram | done   |
| 7 | CD deployment | Azure Container Apps via Azure CLI | done   |
| 8 | Secrets management | GitHub Secrets (11 secrets) | done   |
| 9 | Security scanning | OWASP Dependency Check (CVSS ≥8) | done   |

### Quality Enhancement 

| # | Enhancement | Implementation | Status |
|---|-------------|----------------|--------|
| 10 | Parallel execution | Matrix builds (5 services CI, 6 services Docker) | done   |
| 11 | Dependency caching | Maven cache, base image strategy | done   |
| 12 | Multi-stage builds | All Dockerfiles use multi-stage | done   |
| 13 | Multi-tag strategy | latest, git-sha, timestamp | done   |
| 14 | Health checks | Post-deployment verification job | done   |
| 15 | Zero-downtime | Azure Container Apps revision system | done   |
| 16 | Comprehensive docs | This document (implementation + diagrams) | done   |
| 17 | Quality gates | Checkstyle, tests, coverage, security | done   |

### Advanced Features - DOCUMENTED FOR FUTURE 

| # | Feature | Current Status | Future Implementation |
|---|---------|----------------|----------------------|
| 18 | Blue/Green deployment | Manual rollback via revisions | Automate with traffic splitting |
| 19 | Canary releases | Not implemented | Traffic-based gradual rollout |
| 20 | Infrastructure as Code | Manual Azure setup | Terraform/Bicep automation |
| 21 | Observability | Azure Portal only | Prometheus + Grafana stack |
| 22 | SonarCloud integration | Not implemented | Code quality dashboard |
| 23 | Automated rollback | Manual process | Automatic on health check failure |

---

## Setup Instructions

### Prerequisites

- Azure subscription (student account recommended)
- GitHub repository
- Azure CLI installed locally
- Docker installed locally (for building TDLib base image)

### First-Time Setup

**1. Create Azure Resources** (if not done):
```bash
# Resource group
az group create --name rg-communication-platform --location eastus

# PostgreSQL
az postgres flexible-server create --name <unique-name> --resource-group rg-communication-platform

# Container Registry
az acr create --name <unique-name> --resource-group rg-communication-platform --sku Basic

# Container Apps (6 services)
az containerapp create --name user-service --resource-group rg-communication-platform ...
```

**2. Build TDLib Base Image** (one-time, ~20-30 min):
```powershell
cd telegram-service
docker build -f Dockerfile.tdlib-base -t <acr>.azurecr.io/telegram-tdlib-base:latest .
az acr login --name <acr>
docker push <acr>.azurecr.io/telegram-tdlib-base:latest
```

**3. Configure GitHub Secrets**:

Go to GitHub → Settings → Secrets and variables → Actions → New repository secret

Add 11 secrets (get values from Azure):
- `AZURE_CREDENTIALS` - Service Principal JSON (create with: `az ad sp create-for-rbac --role contributor`)
- `ACR_NAME`, `ACR_USERNAME`, `ACR_PASSWORD`, `ACR_LOGIN_SERVER`
- `APP_DB_PASSWORD`, `JWT_SECRET`
- `TELEGRAM_ENCRYPTION_KEY`, `GMAIL_ENCRYPTION_KEY`, `DISCORD_ENCRYPTION_KEY`
- `AI_SERVICE_API_KEY`

**4. Push Workflow Files**:
```bash
git add .github/
git commit -m "Add CI/CD pipeline"
git push origin main
```

**5. Watch First Run**: Go to Actions tab, monitor pipeline execution (~12-15 min)

### Daily Usage

**Automatic Deployment:**
- Push to `main` → Full CI/CD pipeline runs → Deployment to production

**Manual Deployment:**
- Actions → "CI/CD Pipeline" → Run workflow → Select environment → Run

**Single Service Deployment:**
- Actions → "Deploy Individual Service" → Select service → Run workflow

### Monitoring

**GitHub Actions Dashboard:**
- Repository → Actions tab → View all workflow runs
- Click run → See logs for each job
- Download artifacts (JARs, reports)

**Azure Portal:**
- Container Apps → Select service → View logs, metrics, revisions
- Log Analytics: Real-time log streaming

**Azure CLI:**
```bash
# Service status
az containerapp list --resource-group rg-communication-platform --query "[].{Name:name, Status:properties.runningStatus}"

# Service logs
az containerapp logs show --name telegram-service --resource-group rg-communication-platform --follow

# Revisions
az containerapp revision list --name telegram-service --resource-group rg-communication-platform
```

---

## Troubleshooting

### Pipeline Failures

**Issue:** "AZURE_CREDENTIALS secret incorrect"
- **Solution:** Recreate Service Principal and update GitHub Secret

**Issue:** "ACR authentication failed"
- **Solution:** Verify ACR credentials in GitHub Secrets match `az acr credential show`

**Issue:** "Service deployment succeeded but not running"
- **Solution:** Check service logs for startup errors (database connection, missing env vars)

**Issue:** "telegram-service: base image not found"
- **Solution:** Build and push telegram-tdlib-base:latest to ACR first

### Build Performance

**Issue:** "telegram-service builds taking 20+ min"
- **Solution:** Verify telegram-tdlib-base:latest exists in ACR (should be ~5-8 min)

**Issue:** "Matrix jobs failing randomly"
- **Solution:** Check Maven Central availability, retry workflow

### Deployment Issues

**Issue:** "Database connection refused"
- **Solution:** Verify PostgreSQL firewall allows Azure services, check FQDN in env vars

**Issue:** "Service starts then crashes"
- **Solution:** Check environment variables, verify all secrets are set correctly

---

## Conclusion
**Key Achievements:**
-  Full automation from code push to production deployment
-  Security-first approach (OWASP scanning, secrets management, least-privilege)
-  Optimized build times (telegram-service: 5-8 min via base image strategy)
-  Zero-downtime deployments with instant rollback capability
-  Comprehensive quality gates (code style, tests, security)
-  Parallel execution for fast feedback (matrix builds)
-  Production-ready monitoring and health checks

**Future Enhancements:**
- Automated rollback on health check failure
- Infrastructure as Code (Terraform)
- Observability stack (Prometheus + Grafana)
- SonarCloud integration for code quality metrics

---
