## Criterion: CI/CD Automation

### Architecture Decision Record

#### Status

**Status:** Accepted

**Date:** 2025-12-19

#### Context

6-service microservices project needs automated pipeline for quality, security, and reliable deployments. Challenge: coordinate parallel builds, handle TDLib compilation (Telegram), manage Azure deployment order to avoid PostgreSQL connection pool exhaustion, enforce quality gates efficiently.

#### Decision

Implemented **GitHub Actions** 6-stage pipeline: Code Quality → Parallel Build/Test (matrix) → Telegram validation → Docker builds → Sequential Azure deployment (30s intervals) → Health checks. GitHub Secrets management, artifact retention policies, zero-downtime Azure Container Apps deployment with revision rollback.

#### Alternatives Considered

| Alternative | Pros | Cons | Why Not Chosen |
|-------------|------|------|----------------|
| Jenkins | Full control, plugins | Infrastructure cost, maintenance | Requires dedicated server |
| Azure DevOps | Azure-native features | Separate platform, learning curve | Overkill for project scale |
| GitLab CI | Powerful, integrated | Project on GitHub, migration needed | Ecosystem mismatch |

#### Consequences

**Positive:**
- Automated quality/security gates catch issues pre-merge (Checkstyle, OWASP)
- Parallel matrix builds reduce time from 30+ min to >5 min
- Zero-cost (GitHub Actions free tier), eliminates manual deployment errors

**Negative:**
- GitHub Actions minutes limit (not issue currently)
- Sequential deployment slower than parallel (DB constraints)
- Manual rollback process (no automated revert)

### Implementation Details

#### Pipeline Architecture (6 Stages)

**CI Stages:**
1. **Code Quality**: Checkstyle, OWASP (CVSS ≥8 fails), security reports (30d)
2. **Build/Test (Parallel)**: 5 services matrix, Maven package, JUnit + JaCoCo, JAR artifacts (7d)
3. **Telegram Validation**: Dockerfile check (TDLib not in Maven), tests in Docker with base image

**CD Stages:**
4. **Docker Build (Parallel)**: 6 services, multi-tag (`latest`/`git-sha`/`timestamp`), push to ACR, precompiled Telegram base image (2-3 min vs 20+)
5. **Azure Deploy (Sequential)**: 30s intervals (DB pool: 50 max, 5/service), env injection, revision suffix
6. **Health Check**: Verify all services `"Running"`, fail if unhealthy

#### Key Implementation Decisions

| Decision | Rationale |
|----------|-----------|
| GitHub Actions | Native integration, zero cost, Azure support |
| Matrix builds | 30+ min → ~8 min (parallel execution) |
| Sequential deployment | Avoid DB pool exhaustion (50 max, 5/service) |
| Telegram base image | 20+ min → 2-5 min (pre-compiled TDLib) |
| Multi-tag images | Timestamp rollback capability |
| GitHub Secrets | Encrypted, never in repo |

#### Artifact Management

```
Artifacts:
├── JAR files (7 days retention)
├── Test reports (30 days)
├── OWASP security scans (30 days)
└── Docker images (ACR, permanent)
    ├── latest
    ├── git-<7-char-sha>
    └── YYYYMMDD-HHMMSS
```

### Requirements Checklist

| # | Requirement | Status | Evidence/Notes |
|---|-------------|--------|----------------|
| 1 | CI with 3+ stages | + | Quality, build/test, Docker |
| 2 | Lint/format checks | + | Checkstyle (Google Style) |
| 3 | Automated tests | + | JUnit + JaCoCo all services |
| 4 | Artifact generation | + | JARs, images, reports |
| 5 | CD deployment | + | Azure Container Apps |
| 6 | Dependency caching | + | Maven ~/.m2/repository |
| 7 | Secrets management | + | GitHub Secrets (11 total) |
| 8 | Security scanning | + | OWASP (CVSS ≥8 fails) |
| 9 | Pipeline on push/PR | + | Push/PR/manual triggers |
| 10 | Error handling | + | Fail on errors + health checks |

### Known Limitations

| Limitation | Impact | Potential Solution |
|------------|--------|-------------------|
| Manual rollback | Slow response | Auto-rollback on health failure |
| No blue/green | Downtime risk | Azure traffic splitting |
| Sequential deploy | Slower | Upgrade PostgreSQL tier |
| Basic health checks | Limited visibility | Add distributed tracing |

### References

- CI/CD Documentation: [CI_CD_DOCUMENTATION.md](../../../CI_CD_DOCUMENTATION.md)
- Pipeline Workflow: [.github/workflows/ci-cd-pipeline.yml](../../../.github/workflows/ci-cd-pipeline.yml)
- Single Service Deploy: [.github/workflows/deploy-single-service.yml](../../../.github/workflows/deploy-single-service.yml)