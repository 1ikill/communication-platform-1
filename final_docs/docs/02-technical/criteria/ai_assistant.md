## Criterion: AI Assistant & Message Personalization

### Architecture Decision Record

#### Status

**Status:** Accepted

**Date:** 2025-12-04

#### Context

Communication platform requires AI-driven message personalization to adapt content based on recipient relationships and communication styles. Challenge: integrate LLM capabilities while maintaining performance (<3s response), managing API costs, and ensuring security without exposing keys or allowing prompt injection attacks.

#### Decision

Implemented dedicated AI microservice using OpenAI API (GPT-4o-mini, GPT-4.1-mini) with contact profile database. Service provides prompt engineering for message adaptation based on relationship type (Supervisor, Customer, Colleague), tone style (Professional, Casual, Formal), and formality level (1-5 scale). API keys stored in environment variables, input validation prevents injection.

#### Alternatives Considered

| Alternative | Pros | Cons | Why Not Chosen |
|-------------|------|------|----------------|
| Local open-source LLM (LLaMA, Mistral) | No API costs, data privacy | Requires GPU infrastructure, slower, lower quality | Infrastructure complexity, cost trade-off |
| Anthropic Claude API | Better safety, longer context | Higher cost, less familiar | OpenAI sufficient for use case |
| Rule-based templates | Fast, predictable, cheap | Inflexible, poor quality, not truly personalized | Doesn't meet AI requirement |

#### Consequences

**Positive:**
- High-quality personalization with minimal latency
- Scalable through API without infrastructure investment
- Flexible prompt engineering for different scenarios
- Easy model upgrades without redeployment

**Negative:**
- Ongoing API costs per message
- Dependency on external service availability
- Limited control over model behavior

### Implementation Details

#### Key Implementation Decisions

| Decision | Rationale |
|----------|-----------|
| OpenAI GPT-4o-mini as primary model | Balance of quality, speed, and cost-effectiveness |
| Contact profile database schema | Persistent storage of recipient preferences and relationship context |
| Prompt template system | Structured prompts with relationship/tone/formality parameters |
| Environment-based API key management | Security best practice, prevents credential exposure |
| Input validation & sanitization | Prevents prompt injection and malicious content |

#### Project Structure

```
ai-service/
├── controller/
│   └── AiServiceController.java       # REST endpoints
├── service/
│   ├── AIMessageFormattingService.java # OpenAI integration
│   └── ContactProfileService.java      # Profile CRUD
├── domain/
│   ├── model/ContactProfile.java       # JPA entity
│   └── dto/                            # Request/response DTOs
├── repository/
│   └── ContactProfileRepository.java   # Data access
└── config/
    └── OpenAIConfig.java               # API configuration
```

### Requirements Checklist

| # | Requirement | Status | Evidence/Notes |
|---|-------------|--------|----------------|
| 1 | Integration with modern LLM | + | OpenAI GPT-4o-mini/GPT-4.1-mini |
| 2 | Prompt engineering with templates | + | Relationship/tone/formality parameters |
| 3 | API key security (env variables) | + | Spring Boot externalized configuration |
| 4 | Input validation & sanitization | + | DTO validation, length checks |
| 5 | Error handling for API failures | + | Try-catch with fallback mechanisms |
| 6 | Response time <10s | + | Avg 2-3s, configurable timeout |
| 7 | Contact profile management | + | CRUD operations with persistence |
| 8 | Logging of requests/responses | + | SLF4J logging throughout service |
| 9 | Multi-layer architecture | + | Controller/Service/Repository pattern |
| 10 | API documentation | + | Swagger/OpenAPI specification |

### Known Limitations

| Limitation | Impact | Potential Solution |
|------------|--------|-------------------|
| No response caching | Repeated personalization costs | Implement Redis cache for similar requests |
| Single model dependency | Vendor lock-in, single point of failure | Add fallback to alternative LLM provider |
| No context between personalizations | Each message personalized independently | Implement conversation history tracking |

### References

- [OpenAI API Documentation](https://platform.openai.com/docs)
- [Prompt Engineering Guide](https://www.promptingguide.ai/)
- AI Service API: `/api_docs/AI_SERVICE_API_DOCUMENTATION.md`
- Contact Profile Schema: `ai-service/src/main/resources/db/migration/V1__create_contact_profiles.sql`