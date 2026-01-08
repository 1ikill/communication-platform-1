## Criterion: API Documentation

### Architecture Decision Record

#### Status

**Status:** Accepted

**Date:** 2025-12-19

#### Context

6 microservices with RESTful APIs need comprehensive documentation for developers to integrate, test, and maintain services. Challenge: maintain sync between code and docs, provide interactive testing interface, ensure consistency across services, support multiple documentation layers for different audiences (API consumers vs maintainers).

#### Decision

**Three-layer documentation strategy**: (1) Code-level SpringDoc/OpenAPI annotations for auto-generated Swagger UI, (2) Markdown files per service with detailed examples and context, (3) High-level integration README. OpenAPI 3.0 standard with `@Operation`, `@Schema`, `@Parameter` annotations. All services expose Swagger UI at `/swagger-ui.html`. Versioned docs in Git alongside code.

#### Alternatives Considered

| Alternative | Pros | Cons | Why Not Chosen |
|-------------|------|------|----------------|
| Postman collections only | Interactive testing | No auto-sync, manual updates | Maintenance burden, drift risk |
| README-only | Simple, single source | No interactive UI, hard to navigate | Poor DX for complex APIs |
| External docs site (GitBook) | Professional look | Separate from code, sync issues | Overhead for small team |

#### Consequences

**Positive:**
- Auto-sync via annotations eliminates doc drift
- Interactive Swagger UI enables instant testing
- Three layers serve different user needs (quick ref vs deep dive)

**Negative:**
- Annotation verbosity in controllers, requires discipline to maintain Markdown docs

### Implementation Details

#### Documentation Architecture

**Layer 1 - Code Annotations**: SpringDoc generates OpenAPI 3.0 spec from `@Operation`, `@Schema`, `@Parameter`
**Layer 2 - Service Docs**: 7 Markdown files (6 services + strategy) in `/api_docs/` with examples, errors, models
**Layer 3 - Integration**: Root README with architecture, setup, quick start

#### Key Implementation Decisions

| Decision | Rationale |
|----------|-----------|
| SpringDoc over Springfox | Active development, Spring Boot 3 support |
| OpenAPI 3.0 standard | Industry standard, tooling ecosystem |
| Markdown per service | Version control, searchability, offline access |
| Swagger UI embedded | Zero setup for developers, auto-updated |
| Consistent structure | TOC, Request/Response, Errors, Models sections |

#### Documentation Structure

**Per-Service Markdown**:
- Overview & base URL
- Endpoint sections: Description, Request/Response examples, Field tables, Error codes
- Data models with validation rules
- Security details (JWT, RBAC)
- Usage examples

### Requirements Checklist

| # | Requirement | Status | Evidence/Notes |
|---|-------------|--------|----------------|
| 1 | Complete API specification | + | OpenAPI 3.0 via SpringDoc all services |
| 2 | Endpoints with examples | + | Request/response samples, field tables |
| 3 | Getting started guide | + | README with setup + quick start |
| 4 | Architecture overview | + | Microservices diagram, service descriptions |
| 5 | Developer-accessible format | + | Swagger UI + Markdown in Git |
| 6 | Documentation strategy | + | API_DOCUMENTATION_STRATEGY.md (3 layers) |
| 7 | Consistent formatting | + | Template structure all service docs |
| 8 | HTTP status codes | + | Success/error codes documented per endpoint |
| 9 | Authentication guide | + | JWT flow, token refresh, RBAC explained |
| 10 | Error handling | + | Error structures, recovery steps, codes |

### Known Limitations

| Limitation | Impact | Potential Solution |
|------------|--------|-------------------|
| No versioning strategy | API changes break clients | Implement URL versioning (/v1/, /v2/) |
| Manual Markdown sync | Risk of outdated examples | Add doc tests validating examples |
| No interactive sandbox | Can't test without deployment | Add mock server or Docker sandbox |
| Missing changelog | Hard to track API changes | Maintain CHANGELOG.md per service |

### References

- Documentation Strategy: [api_docs/API_DOCUMENTATION_STRATEGY.md](../../../api_docs/API_DOCUMENTATION_STRATEGY.md)
- User Service API: [api_docs/USER_SERVICE_API_DOCUMENTATION.md](../../../api_docs/USER_SERVICE_API_DOCUMENTATION.md)
- OpenAPI Specs: `/swagger-ui.html` per service (runtime)
- SpringDoc Documentation: https://springdoc.org/