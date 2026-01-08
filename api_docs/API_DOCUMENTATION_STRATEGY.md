# API Documentation Rules and Strategy

## Overview

This document outlines the rules, standards, and strategy for API documentation across the Communication Platform microservices. It defines the documentation approach, structure, conventions, and maintenance practices to ensure consistent, comprehensive, and maintainable API documentation.

---

## Documentation Objectives

1. **Completeness**: Document all endpoints, request/response formats, and error cases
2. **Clarity**: Provide clear, unambiguous descriptions and examples
3. **Consistency**: Maintain uniform structure and terminology across all services
4. **Accessibility**: Make documentation easy to find and navigate
5. **Accuracy**: Keep documentation synchronized with implementation
6. **Usability**: Enable developers to quickly understand and use APIs

---

## Documentation Layers

The platform implements a three-layer documentation strategy:

### 1. Code-Level Documentation (SpringDoc/OpenAPI Annotations)

**Purpose**: Embedded documentation for automated Swagger UI generation

**Implementation**:
- `@Operation` annotations on controller methods
- `@Schema` annotations on DTOs
- `@Parameter` annotations on method parameters
- OpenAPI 3.0 specification compliance

**Example**:
```java
@Operation(summary = "Register")
@PostMapping("/auth/register")
public UserDto register(
    @RequestBody
    @Valid
    final UserCreateDto createDto
) {
    return service.register(createDto);
}
```

**Benefits**:
- Auto-generated Swagger UI at runtime
- Interactive API testing interface
- Always in sync with code
- Accessible at `/swagger-ui.html` for each service

---

### 2. Service-Level Documentation (Markdown Files)

**Purpose**: Comprehensive, human-readable API documentation

**Location**: `{service-name}/API_DOCUMENTATION.md`

**Structure**:
```
# Service Name API Documentation
## Overview
## Table of Contents
## Endpoint Sections
   - Description
   - Request/Response Examples
   - Field Descriptions
   - Error Responses
## Data Models
## Error Responses
## Security
## Usage Examples
## Notes
```

**Benefits**:
- Detailed explanations and context
- Complete examples with realistic data
- Error handling guidance
- Version-controlled alongside code
- Readable without running services

---

### 3. Integration Documentation (README)

**Purpose**: High-level platform overview and getting started guide

**Location**: Root `README.md`

**Content**:
- Architecture overview
- Service descriptions
- Setup instructions
- Environment configuration
- Quick start examples
- Links to service-level documentation

---

## Documentation Standards

### Naming Conventions

**Endpoints**:
- Use consistent path naming (kebab-case)
- Document exact paths including path variables
- Example: `/users/{id}`, `/telegram/text`

**HTTP Methods**:
- Always specify method in UPPERCASE: GET, POST, PUT, PATCH, DELETE
- Use semantically correct methods

**Field Names**:
- Use camelCase for JSON fields
- Match actual DTO field names exactly
- Example: `contactName`, `relationshipType`, `formalityLevel`

**Enum Values**:
- Document in UPPERCASE as they appear in code
- Example: `TELEGRAM`, `SUPERVISOR`, `FORMAL`

---

### Endpoint Documentation Structure

Each endpoint must include:

#### 1. Header Information
```markdown
### 1. Endpoint Name

**Endpoint:** `POST /service/endpoint`

**Description:** Brief description

Detailed explanation of what the endpoint does, when to use it, and any important context.
```

#### 2. Authentication Requirements
```markdown
#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```
```

#### 3. Request Parameters

**Path Parameters** (if applicable):
- Table with: Parameter | Type | Required | Description

**Query Parameters** (if applicable):
- Table with: Parameter | Type | Required | Description

**Request Body** (if applicable):
- Table with: Field Name | Type | Required | Validation | Description

#### 4. Request Example
```markdown
#### Request Example
```json
{
  "field1": "value1",
  "field2": "value2"
}
```
```

#### 5. Response Specification
```markdown
#### Response

**Status Code:** `200 OK`

**Response Body:**
```json
{
  "id": 1,
  "field": "value"
}
```
```

#### 6. Error Documentation
```markdown
#### Error Responses

**Bad Request (400)**
```json
{
  "status": 400,
  "error": "Error message"
}
```

**Unauthorized (401)**
```json
{
  "status": 401,
  "error": "Unauthorized"
}
```
```

---

### Data Model Documentation

**Structure**:
```markdown
### ModelName
Brief description of the model.

| Field | Type | Description |
|-------|------|-------------|
| fieldName | String | Field description |
| fieldName2 | Integer | Field description |
```

**Requirements**:
- Document all fields
- Specify exact data types
- Include validation constraints
- Document optional vs required fields
- Explain enum values

---

### Error Response Documentation

**Standard Format**:
All services use consistent error response format:
```json
{
  "status": 400,
  "error": "Error message"
}
```

Or for validation errors:
```json
{
  "status": 400,
  "errors": {
    "fieldName": "Validation error message"
  }
}
```

**Requirements**:
- Document all possible HTTP status codes
- Provide example error messages
- Create error reference table
- Explain when each error occurs

---

## OpenAPI/Swagger Configuration

### SpringDoc Configuration

Each service includes SpringDoc OpenAPI dependency:

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.x.x</version>
</dependency>
```

### Controller Annotations

**Required annotations**:
- `@RestController` - Marks REST controller
- `@RequestMapping` - Base path for controller
- `@Operation` - Endpoint description
- `@Schema` - DTO documentation

**Best practices**:
```java
@RestController
@RequestMapping("/endpoint")
public class ExampleController {
    
    @Operation(summary = "Brief description")
    @PostMapping("/action")
    public ResponseDto action(
        @RequestBody 
        @Valid 
        final RequestDto dto
    ) {
        // implementation
    }
}
```

### DTO Annotations

**Required annotations**:
- `@Schema(description = "...")` on class and fields
- `@NotBlank`, `@NotNull`, etc. for validation
- `@JsonFormat` for date/time formatting

**Example**:
```java
@Data
@Schema(description = "User creation DTO")
public class UserCreateDto {
    
    @Schema(description = "Email address")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @Schema(description = "Username for login")
    @NotBlank(message = "Username is required")
    @Size(min = 5, max = 25)
    private String username;
}
```

---

## Documentation Maintenance Strategy

### When to Update Documentation

Documentation must be updated:

1. **During Development**:
   - Add `@Operation` and `@Schema` annotations as you write code
   - Create endpoint documentation when implementing new endpoints
   - Document new DTOs when creating them

2. **Before Commit**:
   - Review that all public endpoints have documentation
   - Verify examples match current implementation
   - Check that error responses are accurate

3. **During Code Review**:
   - Reviewer checks documentation completeness
   - Verify annotations are present and accurate
   - Ensure Markdown documentation is updated

4. **After Breaking Changes**:
   - Update affected endpoint documentation
   - Update examples if request/response format changed
   - Update data model documentation if fields changed

### Version Control

- All documentation is version-controlled with code
- Markdown files live in service directories
- Changes reviewed as part of pull requests
- Documentation changes atomic with code changes

### Testing Documentation Accuracy

**Manual Verification**:
1. Start service locally
2. Open Swagger UI at `http://localhost:PORT/swagger-ui.html`
3. Test each endpoint using "Try it out"
4. Verify responses match documentation
5. Test error cases

**Automated Checks**:
- Compile-time validation of annotations
- OpenAPI spec generation during build
- Link checking in Markdown files

---

## Documentation Organization

### Service Documentation Structure

Each service follows this structure:

```
service-name/
├── src/
│   └── main/
│       └── java/
│           └── com/sdc/{service}/
│               ├── controller/     # @Operation annotations here
│               ├── domain/
│               │   └── dto/        # @Schema annotations here
│               └── config/
│                   └── OpenApiConfig.java  # Swagger configuration
├── API_DOCUMENTATION.md            # Complete service documentation
└── pom.xml
```

### Cross-Service Documentation

**README.md** (root level):
- Links to all service documentation
- Architecture diagram
- Service interaction patterns
- Authentication flow across services

**Consistency Across Services**:
- Same section ordering
- Same terminology
- Same example format
- Same error response format

---

## Documentation Review Checklist

Before marking documentation complete, verify:

### Completeness
- [ ] All public endpoints documented
- [ ] All request parameters documented
- [ ] All response fields documented
- [ ] All error cases documented
- [ ] All DTOs have field descriptions
- [ ] Usage examples provided

### Accuracy
- [ ] HTTP methods are correct
- [ ] Endpoint paths are exact
- [ ] Field types match implementation
- [ ] Required/optional fields marked correctly
- [ ] Validation constraints documented
- [ ] Examples use realistic data

### Clarity
- [ ] Descriptions are clear and concise
- [ ] Technical terms explained
- [ ] Examples are complete and runnable
- [ ] Error messages are self-explanatory
- [ ] Authentication requirements stated

### Consistency
- [ ] Same format as other services
- [ ] Same terminology used
- [ ] Same section structure
- [ ] Status codes used consistently
- [ ] Field naming conventions followed

### Usability
- [ ] Table of contents present
- [ ] Sections logically organized
- [ ] Examples include context
- [ ] Common use cases documented
- [ ] Links to related documentation

---

## Best Practices

### Writing Descriptions

**Do**:
- ✅ Use present tense: "Creates a user account"
- ✅ Be specific: "Sends a text message to a Telegram chat"
- ✅ Explain context: "Used during initial authentication flow"
- ✅ State limitations: "Maximum 10MB file size"

**Don't**:
- ❌ Use vague terms: "Does stuff with messages"
- ❌ Assume knowledge: "Standard OAuth flow" (explain it)
- ❌ Leave out constraints: Document all limits
- ❌ Use implementation details: Focus on what, not how

### Writing Examples

**Do**:
- ✅ Use realistic data
- ✅ Include all required fields
- ✅ Show complete requests
- ✅ Demonstrate common use cases
- ✅ Include authentication headers

**Don't**:
- ❌ Use placeholder values: "string", "number"
- ❌ Omit required fields
- ❌ Show only partial requests
- ❌ Include only happy path

### Error Documentation

**Do**:
- ✅ Document all HTTP status codes used
- ✅ Provide actual error messages
- ✅ Explain when each error occurs
- ✅ Show error response format
- ✅ Group related errors

**Don't**:
- ❌ Say "error occurs on failure"
- ❌ Use generic "400 Bad Request" without specifics
- ❌ Forget validation errors
- ❌ Omit authentication errors

---

## Tools and Resources

### Documentation Tools

**Swagger UI**:
- URL: `http://localhost:PORT/swagger-ui.html`
- Interactive API testing
- Auto-generated from annotations

**OpenAPI Specification**:
- URL: `http://localhost:PORT/v3/api-docs`
- Machine-readable API definition
- Can be imported to Postman, Insomnia

**Markdown Editors**:
- VS Code with Markdown Preview
- Table formatting extensions
- Link checking extensions

### Validation Tools

**API Testing**:
- Swagger UI "Try it out"
- Postman collections
- cURL commands

**Markdown Validation**:
- Markdown linters
- Link checkers
- Table formatters

---

## Documentation Templates

### Endpoint Template

```markdown
### X. Endpoint Name

**Endpoint:** `METHOD /path`

**Description:** Brief description

Detailed explanation.

#### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

#### Request Body

| Field Name | Type | Required | Description |
|------------|------|----------|-------------|
| field1 | String | Yes | Description |

#### Request Example
```json
{
  "field1": "value"
}
```

#### Response

**Status Code:** `200 OK`

**Response Body:**
```json
{
  "id": 1
}
```

#### Error Responses

**Error Type (4XX)**
```json
{
  "status": 400,
  "error": "Error message"
}
```
```

### Data Model Template

```markdown
### ModelName
Brief description.

| Field | Type | Description |
|-------|------|-------------|
| field1 | String | Description |
| field2 | Integer | Description |
```

---

## Quality Metrics

### Documentation Coverage

Target: 100% of public endpoints documented

**Measure**:
- Count of public endpoints
- Count of documented endpoints
- Percentage coverage

### Documentation Accuracy

Target: 0 discrepancies between docs and implementation

**Measure**:
- Manual testing results
- User-reported issues
- Code review findings

### Documentation Completeness

Target: All sections present for each endpoint

**Measure**:
- Checklist completion rate
- Missing examples count
- Missing error documentation count

---

## Roles and Responsibilities

### Developers
- Add annotations during development
- Write endpoint documentation
- Update docs when changing APIs
- Test documentation accuracy

### Code Reviewers
- Verify documentation completeness
- Check example accuracy
- Ensure consistency
- Validate annotations

### Technical Writers (if applicable)
- Review terminology consistency
- Improve clarity and readability
- Standardize formatting
- Create usage guides

---

## Documentation Evolution

### Current State (v1.0)

- ✅ All 6 services fully documented
- ✅ Swagger UI available for all services
- ✅ Comprehensive Markdown documentation
- ✅ Usage examples for common scenarios
- ✅ Complete data model documentation
- ✅ Error response documentation

### Future Enhancements

**Potential Improvements**:
- API versioning strategy
- Changelog documentation
- Migration guides for breaking changes
- Interactive tutorials
- SDK documentation (if SDKs created)
- Postman collection exports

---

## Conclusion

This documentation strategy ensures:

1. **Comprehensive Coverage**: Every endpoint is documented at multiple levels
2. **Consistency**: All services follow the same structure and conventions
3. **Maintainability**: Documentation evolves with code
4. **Usability**: Developers can quickly understand and use APIs
5. **Quality**: Documentation is accurate and up-to-date

By following these rules and standards, the Communication Platform maintains high-quality API documentation that serves both current development and future maintenance needs.

---

## References

- [OpenAPI Specification 3.0](https://spec.openapis.org/oas/v3.0.0)
- [SpringDoc OpenAPI Documentation](https://springdoc.org/)
- [Spring Boot REST API Documentation](https://spring.io/guides/tutorials/rest/)
- Service-specific API documentation files in each service directory
