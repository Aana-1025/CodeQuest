# CODEQUEST FEATURE PROMPTS
Reusable Copy-Paste Prompt Bank for ChatGPT + Codex

**Project:** CodeQuest - Gamified AI Learning Platform for Computer Science Students  
**Stack:** Java 21, Spring Boot, React, PostgreSQL, Gemini API, Piston API, Docker, CI/CD  
**Architecture:** Modular monolith for MVP  
**Purpose:** Strict prompt bank for building CodeQuest with ChatGPT Plus and Codex without losing architecture, API, database, security, testing, or MVP boundaries.

---

## 0. Purpose

This file is used with these source-of-truth resources:

1. `CodeQuest_AI_Control_Master_Blueprint_v3.docx` - full project blueprint and master authority.
2. `CodeQuest_Core_Rules.md` - always-paste AI execution rules.
3. `CodeQuest_DB_Schema.md` - database rules, tables, relationships, constraints, indexes, and Flyway rules.
4. `CodeQuest_API_Contracts.md` - endpoint names, HTTP methods, request DTOs, response DTOs, and examples.
5. `CodeQuest_Feature_Prompts.md` - this prompt bank.
6. `AGENTS.md` - repo-root Codex instruction file.
7. `CodeQuest_Build_Log.md` - current progress, completed features, bugs, test results, next task, and continuity memory.

This file solves repeated prompt-writing. Copy one relevant prompt, fill placeholders, and paste it into Codex or ChatGPT. Each prompt keeps tasks small, consistent, testable, and safe. The goal is to stop Codex from hallucinating architecture, mixing layers, inventing APIs, breaking DB relationships, skipping validation, ignoring tests, or accidentally implementing Phase 2 features.

## 1. How to Use This Prompt Bank

- Use one prompt for one feature.
- For complex tasks, use the Plan-First prompt before coding.
- Use the Bug Fix prompt only with exact terminal/test errors.
- After every completed feature, update `CodeQuest_Build_Log.md`.
- Keep the master blueprint and core rules uploaded in the ChatGPT Project.
- In the repo, keep `AGENTS.md` at the root and store this Markdown file under `docs/`.

## 2. Non-Negotiable Prompt Rules

- Follow the master blueprint first.
- MVP first. Do not implement Phase 2 or Phase 3 unless explicitly requested.
- CodeQuest must remain a modular monolith for MVP.
- Do not redesign the architecture.
- Do not introduce random libraries, frameworks, paid tools, external APIs, or infrastructure.
- Do not invent new features.
- Do not rename endpoints, DTOs, packages, modules, tables, fields, enums, or migrations unless instructed.
- Do not modify unrelated files.
- If unsure, ask instead of guessing.
- Backend must keep Controller -> Service -> Repository separation.
- Controllers handle HTTP only. No business logic, repository calls, Gemini calls, or Piston calls.
- Services contain business logic, validation orchestration, ownership checks, transactions, XP rules, unlock rules, progress rules, and external client calls.
- Repositories contain database access only.
- Use request DTOs and response DTOs. Never expose JPA entities directly.
- Add Jakarta Bean Validation annotations to request DTOs.
- Use custom domain exceptions and GlobalExceptionHandler with standard ErrorDTO.
- Use Flyway migrations for database schema changes.
- Use UUID primary keys.
- User-owned data must include ownership checks derived from JWT/SecurityContext.
- Protected endpoints must not accept userId in the request body for current-user actions.
- Gemini calls must go through GeminiService only.
- AI prompts must be built by PromptBuilder and parsed by ResponseParser.
- AI output must be validated before storing.
- Do not send secrets, JWTs, passwords, or private user data to Gemini.
- Code execution must use Piston API only. Never execute user code inside the backend.
- Add at least one meaningful automated test for backend business logic.
- Do not claim tests passed unless they were actually run.
- After every task, list files changed, implementation summary, manual test steps, automated test commands, assumptions, and Build Log updates.

## 3. Universal Master Templates

### 3.1 Master Codex Prompt

```text
You are helping me build CodeQuest, a Java 21 + Spring Boot + React + PostgreSQL portfolio project.

Follow the source files in this priority order:
1. CodeQuest_AI_Control_Master_Blueprint_v3.docx
2. CodeQuest_Core_Rules.md
3. CodeQuest_DB_Schema.md
4. CodeQuest_API_Contracts.md
5. CodeQuest_Feature_Prompts.md
6. CodeQuest_Build_Log.md
7. AGENTS.md

Use this prompt exactly. Follow the source files strictly. MVP first. Do not implement Phase 2 or Phase 3 unless I explicitly ask. Do not redesign the architecture. Do not invent new features. If unsure, ask instead of guessing. Keep CodeQuest as a modular monolith. For backend work, keep Controller -> Service -> Repository separation. Do not put business logic in controllers. Do not expose JPA entities directly in API responses. Use request/response DTOs. Add validation annotations. Use custom exceptions and GlobalExceptionHandler where needed. Use Flyway for schema changes and UUID primary keys. Do not modify unrelated files. Add at least one meaningful automated test for backend business logic.

Required output after every code change:
Files changed:
Implementation summary:
Manual test steps:
Automated tests:
Assumptions:
Build Log update:
```

### 3.2 Universal Feature Implementation Prompt

```text
You are implementing one feature in CodeQuest.

Feature name:
[FEATURE_NAME]

Module:
[auth/user/course/level/quiz/problem/progress/ai/leaderboard/frontend/devops/docs]

Exact endpoint if backend/API related:
[METHOD] /api/...

Relevant tables:
[table names]

Request DTO:
[fields + validation]

Response DTO:
[fields]

Business rules:
[rules from blueprint]

Security rules:
[JWT/ownership/rate-limit/role rules]

Files Codex may touch:
[list exact files or folders]

Files Codex must not touch:
[list unrelated modules and files]

Required tests:
[unit/integration/frontend tests]

Manual test steps:
[Swagger/curl/UI steps]

Automated commands:
[cd backend && mvn test]
[cd frontend && npm test -- --run]
[cd frontend && npm run build]

Build Log updates:
[what should be checked, changed, and recorded]

Implementation requirements:
1. Keep Controller, Service, Repository, DTO, Entity, Mapper separate.
2. Do not expose entities directly.
3. Add validation annotations.
4. Add custom exceptions if needed.
5. Update GlobalExceptionHandler if needed.
6. Add at least one meaningful test.
7. Do not modify unrelated files.
8. List files changed.
9. Give manual test steps.
10. Give automated test commands.
11. State assumptions.
```

### 3.3 Plan-First Prompt

```text
Before writing code, create a safe implementation plan for this CodeQuest feature.

Feature:
[FEATURE_NAME]

Use the blueprint and supporting docs strictly. Do not write code yet.

Your plan must include:
1. Files that need to be created/modified.
2. Tables and relationships involved.
3. Exact endpoints involved.
4. DTOs needed.
5. Service-layer business rules.
6. Validation rules.
7. Ownership/security checks.
8. Exceptions and ErrorDTO cases.
9. Tests to add.
10. Risks or ambiguities.

Do not invent new architecture, endpoints, libraries, or DB fields. If anything is unclear, ask before coding.
```

### 3.4 Error-Fix Prompt

```text
A CodeQuest task failed. Fix only the provided error.

Error/log/test failure:
[PASTE EXACT ERROR HERE]

Context:
- Current feature: [FEATURE_NAME]
- Module: [MODULE]
- Last changed files: [FILES]
- Command that failed: [COMMAND]

Strict bug-fix rules:
- Fix only the provided error.
- Do not redesign the architecture.
- Do not invent new features.
- Do not modify unrelated files.
- Do not hide or ignore failing tests.
- Explain the root cause in simple terms.
- Provide the smallest safe patch.
- If the error is caused by missing context, ask instead of guessing.

Required output:
1. Root cause
2. Files changed
3. Exact fix summary
4. Manual verification steps
5. Automated command to re-run
6. Any Build Log update needed
```

### 3.5 Code Review Prompt

```text
Review this CodeQuest code against the master blueprint and AI-control rules.

Code/files to review:
[PASTE CODE OR FILE LIST]

Feature/module:
[FEATURE_NAME / MODULE]

Review against architecture, API contracts, DB schema, DTO usage, validation, exception handling, ownership checks, Gemini/Piston isolation, tests, MVP scope, hidden bugs, and interview-quality backend design.

Output format:
1. Verdict: Pass / Needs fixes / Risky
2. Critical issues
3. Medium issues
4. Small improvements
5. Missing tests
6. Security or data ownership risks
7. Exact patch prompt for Codex
8. Whether Build Log should be updated
```

### 3.6 Test Generation Prompt

```text
Generate tests for this CodeQuest feature.

Feature:
[FEATURE_NAME]
Module:
[MODULE]
Files under test:
[FILES]
Business rules to test:
[RULES]
Failure cases to test:
[CASES]

Testing requirements:
- Use JUnit 5 for backend unit tests.
- Use Mockito for service dependencies.
- Use @DataJpaTest for repository behavior when needed.
- Use SpringBootTest/Testcontainers for integration tests only when useful.
- Use Vitest and React Testing Library for frontend components.
- Include negative cases, not only happy paths.
- Do not create fake test results.
- Do not mock the method under test.
- Do not make tests weak just to pass.

Required output:
1. Files created/modified
2. Test cases added
3. What each test proves
4. Commands to run
5. Assumptions
```

### 3.7 Safe Refactor Prompt

```text
Safely refactor this CodeQuest code without changing behavior unless explicitly requested.

Refactor target:
[FILES / MODULE / METHOD]

Reason for refactor:
[readability / duplication / testability / layering / naming]

Allowed behavior changes:
[None unless listed]

Strict refactor rules:
- Do not change API contracts.
- Do not change database schema unless explicitly requested.
- Do not change business behavior unless explicitly requested.
- Do not implement Phase 2/3 features.
- Do not modify unrelated files.
- Keep tests passing before and after.
- Add or update tests if the refactor touches business logic.

Required output:
1. Files changed
2. What was refactored
3. Behavior change: Yes/No
4. Why this is safe
5. Tests to run
6. Build Log update if needed
```

### 3.8 Continuation Prompt for New Chat

```text
Read the project resources and CodeQuest_Build_Log.md.
Continue from current status.
Do not redesign anything.
Do not invent new features.
MVP first.
Tell me the next safest MVP task.
Give one strict Codex prompt only.
Also tell me which Build Log fields I should update after the task.
```

## 4. Backend MVP Feature Prompts

Section 4 intentionally contains exactly 34 separate backend prompts.

### 8.1 Project setup

```text
Implement this backend MVP feature for CodeQuest.

Goal:
Create the repo skeleton for backend, frontend, docs, AGENTS.md, README skeleton, and Build Log linkage. Use Java 21, Spring Boot, React, Vite, Tailwind, Maven, and PostgreSQL-ready structure.

Module:
Foundation

Endpoint:
No endpoint

Tables involved:
None initially

Files likely to touch:
repo root, backend/, frontend/, docs/, .github/workflows/ placeholder

Files not to touch:
Do not implement business features yet. Do not add auth/course/AI logic yet.

Business rules:
- Follow the master blueprint and API/DB contracts exactly.
- MVP first only; do not implement Phase 2/3 features.
- Keep task scope narrow and avoid unrelated changes.

Validation rules:
- Use Jakarta Bean Validation annotations on request DTOs where needed.
- Reject invalid enum values, UUIDs, request fields, and unsafe user input.
- Protected endpoints derive current user from JWT/SecurityContext, not request body userId.

Error handling rules:
- Use custom exceptions where useful.
- Route failures through GlobalExceptionHandler.
- Return standard ErrorDTO and correct HTTP statuses.

Tests required:
- App starts or generated projects build where practical.
- No business feature tests required yet unless foundation code is added.

Commands to run:
- cd backend && mvn test

Build Log update:
- Mark this feature complete only after code compiles, tests pass, and manual testing works.
- Add files changed, test result, and next feature to CodeQuest_Build_Log.md.

Use this prompt exactly. Follow CodeQuest_AI_Control_Master_Blueprint_v3.docx, CodeQuest_Core_Rules.md, CodeQuest_DB_Schema.md, CodeQuest_API_Contracts.md, AGENTS.md, and CodeQuest_Build_Log.md. MVP first. Do not implement Phase 2 or Phase 3 unless I explicitly ask. Do not redesign the architecture. Do not invent new features. If unsure, ask instead of guessing. Keep CodeQuest as a modular monolith. For backend work, keep Controller -> Service -> Repository separation. Do not put business logic in controllers. Do not expose JPA entities directly in API responses. Use request/response DTOs. Add validation annotations. Use custom exceptions and GlobalExceptionHandler where needed. Use Flyway for schema changes and UUID primary keys. Derive current user identity from JWT/SecurityContext, not from request body userId. Do not modify unrelated files. Add at least one meaningful automated test for backend business logic. After implementation, list files changed, manual test steps, automated test commands, assumptions, and Build Log updates.
```

### 8.2 Backend health endpoint

```text
Implement this backend MVP feature for CodeQuest.

Goal:
Add a health endpoint so local and deployed backend can be checked safely.

Module:
Foundation/common

Endpoint:
GET /actuator/health or simple GET /api/health if Actuator is not configured yet

Tables involved:
None

Files likely to touch:
backend pom.xml if Actuator needed, common/config or main app config, health test

Files not to touch:
Do not implement auth or business modules.

Business rules:
- Health endpoint must be public and safe.
- Prefer Actuator /actuator/health if Actuator is installed; otherwise use /api/health temporarily.
- Do not expose secrets or environment values in health response.

Validation rules:
- Use Jakarta Bean Validation annotations on request DTOs where needed.
- Reject invalid enum values, UUIDs, request fields, and unsafe user input.
- Protected endpoints derive current user from JWT/SecurityContext, not request body userId.

Error handling rules:
- Use custom exceptions where useful.
- Route failures through GlobalExceptionHandler.
- Return standard ErrorDTO and correct HTTP statuses.

Tests required:
- Add context-load or endpoint test confirming health returns 200.

Commands to run:
- cd backend && mvn test

Build Log update:
- Mark this feature complete only after code compiles, tests pass, and manual testing works.
- Add files changed, test result, and next feature to CodeQuest_Build_Log.md.

Use this prompt exactly. Follow CodeQuest_AI_Control_Master_Blueprint_v3.docx, CodeQuest_Core_Rules.md, CodeQuest_DB_Schema.md, CodeQuest_API_Contracts.md, AGENTS.md, and CodeQuest_Build_Log.md. MVP first. Do not implement Phase 2 or Phase 3 unless I explicitly ask. Do not redesign the architecture. Do not invent new features. If unsure, ask instead of guessing. Keep CodeQuest as a modular monolith. For backend work, keep Controller -> Service -> Repository separation. Do not put business logic in controllers. Do not expose JPA entities directly in API responses. Use request/response DTOs. Add validation annotations. Use custom exceptions and GlobalExceptionHandler where needed. Use Flyway for schema changes and UUID primary keys. Derive current user identity from JWT/SecurityContext, not from request body userId. Do not modify unrelated files. Add at least one meaningful automated test for backend business logic. After implementation, list files changed, manual test steps, automated test commands, assumptions, and Build Log updates.
```

### 8.3 Swagger/OpenAPI setup

```text
Implement this backend MVP feature for CodeQuest.

Goal:
Add Swagger/OpenAPI configuration for documenting MVP endpoints.

Module:
Foundation/common

Endpoint:
GET /swagger-ui/** and GET /v3/api-docs/**

Tables involved:
None

Files likely to touch:
backend pom.xml, common/config/OpenApiConfig, security allowlist if present

Files not to touch:
Do not change endpoint contracts. Do not add fake endpoints just for Swagger.

Business rules:
- Swagger must document existing MVP APIs as they are added.
- Swagger UI and /v3/api-docs should be accessible in dev.
- Keep auth-protected endpoints represented correctly.

Validation rules:
- Use Jakarta Bean Validation annotations on request DTOs where needed.
- Reject invalid enum values, UUIDs, request fields, and unsafe user input.
- Protected endpoints derive current user from JWT/SecurityContext, not request body userId.

Error handling rules:
- Use custom exceptions where useful.
- Route failures through GlobalExceptionHandler.
- Return standard ErrorDTO and correct HTTP statuses.

Tests required:
- Add context-load or smoke test if current test setup supports it.

Commands to run:
- cd backend && mvn test

Build Log update:
- Mark this feature complete only after code compiles, tests pass, and manual testing works.
- Add files changed, test result, and next feature to CodeQuest_Build_Log.md.

Use this prompt exactly. Follow CodeQuest_AI_Control_Master_Blueprint_v3.docx, CodeQuest_Core_Rules.md, CodeQuest_DB_Schema.md, CodeQuest_API_Contracts.md, AGENTS.md, and CodeQuest_Build_Log.md. MVP first. Do not implement Phase 2 or Phase 3 unless I explicitly ask. Do not redesign the architecture. Do not invent new features. If unsure, ask instead of guessing. Keep CodeQuest as a modular monolith. For backend work, keep Controller -> Service -> Repository separation. Do not put business logic in controllers. Do not expose JPA entities directly in API responses. Use request/response DTOs. Add validation annotations. Use custom exceptions and GlobalExceptionHandler where needed. Use Flyway for schema changes and UUID primary keys. Derive current user identity from JWT/SecurityContext, not from request body userId. Do not modify unrelated files. Add at least one meaningful automated test for backend business logic. After implementation, list files changed, manual test steps, automated test commands, assumptions, and Build Log updates.
```

### 8.4 PostgreSQL connection

```text
Implement this backend MVP feature for CodeQuest.

Goal:
Configure Spring Boot datasource for PostgreSQL using environment variables and dev/test/prod profiles.

Module:
Foundation/database

Endpoint:
No endpoint

Tables involved:
None yet; users later

Files likely to touch:
backend application.yml/properties, profile-specific config, test config if needed

Files not to touch:
Do not hard-code secrets. Do not create tables in this task unless explicitly combined with Flyway setup.

Business rules:
- Use environment variables for DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD.
- Support dev/test/prod profile separation.
- Keep secrets outside source control.

Validation rules:
- Use Jakarta Bean Validation annotations on request DTOs where needed.
- Reject invalid enum values, UUIDs, request fields, and unsafe user input.
- Protected endpoints derive current user from JWT/SecurityContext, not request body userId.

Error handling rules:
- Map missing/invalid DB config to startup failure, not hidden fallback.

Tests required:
- Application context test if database test profile exists.

Commands to run:
- cd backend && mvn test

Build Log update:
- Mark this feature complete only after code compiles, tests pass, and manual testing works.
- Add files changed, test result, and next feature to CodeQuest_Build_Log.md.

Use this prompt exactly. Follow CodeQuest_AI_Control_Master_Blueprint_v3.docx, CodeQuest_Core_Rules.md, CodeQuest_DB_Schema.md, CodeQuest_API_Contracts.md, AGENTS.md, and CodeQuest_Build_Log.md. MVP first. Do not implement Phase 2 or Phase 3 unless I explicitly ask. Do not redesign the architecture. Do not invent new features. If unsure, ask instead of guessing. Keep CodeQuest as a modular monolith. For backend work, keep Controller -> Service -> Repository separation. Do not put business logic in controllers. Do not expose JPA entities directly in API responses. Use request/response DTOs. Add validation annotations. Use custom exceptions and GlobalExceptionHandler where needed. Use Flyway for schema changes and UUID primary keys. Derive current user identity from JWT/SecurityContext, not from request body userId. Do not modify unrelated files. Add at least one meaningful automated test for backend business logic. After implementation, list files changed, manual test steps, automated test commands, assumptions, and Build Log updates.
```

### 8.5 Flyway setup

```text
Implement this backend MVP feature for CodeQuest.

Goal:
Enable Flyway and create the initial migration structure using PostgreSQL-compatible SQL and UUIDs.

Module:
Foundation/database

Endpoint:
No endpoint

Tables involved:
users first, then other schema migrations later

Files likely to touch:
backend pom.xml, application config, src/main/resources/db/migration

Files not to touch:
Do not create tables outside MVP schema. Do not create refresh_tokens unless the current task is refresh-token setup.

Business rules:
- All schema changes must use Flyway migrations.
- Migrations must be versioned and forward-only.
- Use UUID primary keys and created_at/updated_at where appropriate.

Validation rules:
- Use Jakarta Bean Validation annotations on request DTOs where needed.
- Reject invalid enum values, UUIDs, request fields, and unsafe user input.
- Protected endpoints derive current user from JWT/SecurityContext, not request body userId.

Error handling rules:
- Do not remove existing constraints or indexes.
- Migration failure should fail startup in non-test environments.

Tests required:
- Run mvn test; add migration validation test if project has integration test setup.

Commands to run:
- cd backend && mvn test

Build Log update:
- Mark this feature complete only after code compiles, tests pass, and manual testing works.
- Add files changed, test result, and next feature to CodeQuest_Build_Log.md.

Use this prompt exactly. Follow CodeQuest_AI_Control_Master_Blueprint_v3.docx, CodeQuest_Core_Rules.md, CodeQuest_DB_Schema.md, CodeQuest_API_Contracts.md, AGENTS.md, and CodeQuest_Build_Log.md. MVP first. Do not implement Phase 2 or Phase 3 unless I explicitly ask. Do not redesign the architecture. Do not invent new features. If unsure, ask instead of guessing. Keep CodeQuest as a modular monolith. For backend work, keep Controller -> Service -> Repository separation. Do not put business logic in controllers. Do not expose JPA entities directly in API responses. Use request/response DTOs. Add validation annotations. Use custom exceptions and GlobalExceptionHandler where needed. Use Flyway for schema changes and UUID primary keys. Derive current user identity from JWT/SecurityContext, not from request body userId. Do not modify unrelated files. Add at least one meaningful automated test for backend business logic. After implementation, list files changed, manual test steps, automated test commands, assumptions, and Build Log updates.
```

### 8.6 Standard ErrorDTO

```text
Implement this backend MVP feature for CodeQuest.

Goal:
Create the standard ErrorDTO shape used by all API error responses.

Module:
common/dto

Endpoint:
All endpoints

Tables involved:
None

Files likely to touch:
backend/src/main/java/.../common/dto/ErrorDTO.java or equivalent

Files not to touch:
Do not implement GlobalExceptionHandler in this prompt; create DTO only.

Business rules:
- ErrorDTO must include timestamp, status, code, message, path, and requestId if available.
- Do not expose stack traces or internal exception class names.
- Keep shape stable for frontend and Swagger.

Validation rules:
- Use Jakarta Bean Validation annotations on request DTOs where needed.
- Reject invalid enum values, UUIDs, request fields, and unsafe user input.
- Protected endpoints derive current user from JWT/SecurityContext, not request body userId.

Error handling rules:
- N/A for DTO except no sensitive data fields.

Tests required:
- Compile test or DTO serialization test if existing test style supports it.

Commands to run:
- cd backend && mvn test

Build Log update:
- Mark this feature complete only after code compiles, tests pass, and manual testing works.
- Add files changed, test result, and next feature to CodeQuest_Build_Log.md.

Use this prompt exactly. Follow CodeQuest_AI_Control_Master_Blueprint_v3.docx, CodeQuest_Core_Rules.md, CodeQuest_DB_Schema.md, CodeQuest_API_Contracts.md, AGENTS.md, and CodeQuest_Build_Log.md. MVP first. Do not implement Phase 2 or Phase 3 unless I explicitly ask. Do not redesign the architecture. Do not invent new features. If unsure, ask instead of guessing. Keep CodeQuest as a modular monolith. For backend work, keep Controller -> Service -> Repository separation. Do not put business logic in controllers. Do not expose JPA entities directly in API responses. Use request/response DTOs. Add validation annotations. Use custom exceptions and GlobalExceptionHandler where needed. Use Flyway for schema changes and UUID primary keys. Derive current user identity from JWT/SecurityContext, not from request body userId. Do not modify unrelated files. Add at least one meaningful automated test for backend business logic. After implementation, list files changed, manual test steps, automated test commands, assumptions, and Build Log updates.
```

### 8.7 GlobalExceptionHandler

```text
Implement this backend MVP feature for CodeQuest.

Goal:
Create centralized exception handling using GlobalExceptionHandler and standard ErrorDTO.

Module:
common/exception

Endpoint:
All endpoints

Tables involved:
None

Files likely to touch:
backend common/exception, common/dto usage, tests

Files not to touch:
Do not create feature-specific business logic here. Do not expose stack traces.

Business rules:
- Handle validation errors, auth errors, not found, conflict, rate limit, external API failures, and unexpected errors.
- Return ErrorDTO consistently.
- Map correct HTTP statuses: 400, 401, 403, 404, 409, 429, 500/502/503.

Validation rules:
- Use Jakarta Bean Validation annotations on request DTOs where needed.
- Reject invalid enum values, UUIDs, request fields, and unsafe user input.
- Protected endpoints derive current user from JWT/SecurityContext, not request body userId.

Error handling rules:
- Use safe messages. Log internal details server-side only if logging exists.

Tests required:
- Add tests for validation error and one custom exception mapping.

Commands to run:
- cd backend && mvn test

Build Log update:
- Mark this feature complete only after code compiles, tests pass, and manual testing works.
- Add files changed, test result, and next feature to CodeQuest_Build_Log.md.

Use this prompt exactly. Follow CodeQuest_AI_Control_Master_Blueprint_v3.docx, CodeQuest_Core_Rules.md, CodeQuest_DB_Schema.md, CodeQuest_API_Contracts.md, AGENTS.md, and CodeQuest_Build_Log.md. MVP first. Do not implement Phase 2 or Phase 3 unless I explicitly ask. Do not redesign the architecture. Do not invent new features. If unsure, ask instead of guessing. Keep CodeQuest as a modular monolith. For backend work, keep Controller -> Service -> Repository separation. Do not put business logic in controllers. Do not expose JPA entities directly in API responses. Use request/response DTOs. Add validation annotations. Use custom exceptions and GlobalExceptionHandler where needed. Use Flyway for schema changes and UUID primary keys. Derive current user identity from JWT/SecurityContext, not from request body userId. Do not modify unrelated files. Add at least one meaningful automated test for backend business logic. After implementation, list files changed, manual test steps, automated test commands, assumptions, and Build Log updates.
```

### 8.8 Auth register

```text
Implement this backend MVP feature for CodeQuest.

Goal:
Register a new user, validate request, hash password with BCrypt, prevent duplicate email, and return safe auth response without password_hash.

Module:
auth

Endpoint:
POST /api/auth/register

Tables involved:
users

Files likely to touch:
auth controller/service/repository/dto/entity/mapper/test and Flyway if users table missing

Files not to touch:
Do not return password_hash. Do not accept role from frontend. Do not create refresh-token flow here unless explicitly requested.

Business rules:
- Hash password with BCrypt.
- Email must be unique.
- Default rank BEGINNER, XP 0, role STUDENT.
- Never return password_hash.

Validation rules:
- Name 2-100 chars, valid email, password at least 8 chars with letters and numbers.

Error handling rules:
- Duplicate email returns 409 via GlobalExceptionHandler.
- Invalid request returns 400.

Tests required:
- Successful registration.
- Duplicate email failure.
- Password hash is not returned and stored password is hashed.

Commands to run:
- cd backend && mvn test

Build Log update:
- Mark this feature complete only after code compiles, tests pass, and manual testing works.
- Add files changed, test result, and next feature to CodeQuest_Build_Log.md.

Use this prompt exactly. Follow CodeQuest_AI_Control_Master_Blueprint_v3.docx, CodeQuest_Core_Rules.md, CodeQuest_DB_Schema.md, CodeQuest_API_Contracts.md, AGENTS.md, and CodeQuest_Build_Log.md. MVP first. Do not implement Phase 2 or Phase 3 unless I explicitly ask. Do not redesign the architecture. Do not invent new features. If unsure, ask instead of guessing. Keep CodeQuest as a modular monolith. For backend work, keep Controller -> Service -> Repository separation. Do not put business logic in controllers. Do not expose JPA entities directly in API responses. Use request/response DTOs. Add validation annotations. Use custom exceptions and GlobalExceptionHandler where needed. Use Flyway for schema changes and UUID primary keys. Derive current user identity from JWT/SecurityContext, not from request body userId. Do not modify unrelated files. Add at least one meaningful automated test for backend business logic. After implementation, list files changed, manual test steps, automated test commands, assumptions, and Build Log updates.
```

### 8.9 Auth login

```text
Implement this backend MVP feature for CodeQuest.

Goal:
Validate email/password, reject invalid credentials, and return access token plus basic profile state.

Module:
auth

Endpoint:
POST /api/auth/login

Tables involved:
users; refresh_tokens only if refresh token is already implemented

Files likely to touch:
auth controller/service/dto/security tests

Files not to touch:
Do not leak whether email exists if avoidable. Do not implement refresh token storage in this prompt unless already built.

Business rules:
- Check password with BCrypt.
- Return access token and safe user fields.
- Never expose password_hash or token_hash.

Validation rules:
- Valid email and nonblank password.

Error handling rules:
- Invalid credentials return clean 401.

Tests required:
- Valid login success.
- Invalid password returns 401.
- Response excludes sensitive fields.

Commands to run:
- cd backend && mvn test

Build Log update:
- Mark this feature complete only after code compiles, tests pass, and manual testing works.
- Add files changed, test result, and next feature to CodeQuest_Build_Log.md.

Use this prompt exactly. Follow CodeQuest_AI_Control_Master_Blueprint_v3.docx, CodeQuest_Core_Rules.md, CodeQuest_DB_Schema.md, CodeQuest_API_Contracts.md, AGENTS.md, and CodeQuest_Build_Log.md. MVP first. Do not implement Phase 2 or Phase 3 unless I explicitly ask. Do not redesign the architecture. Do not invent new features. If unsure, ask instead of guessing. Keep CodeQuest as a modular monolith. For backend work, keep Controller -> Service -> Repository separation. Do not put business logic in controllers. Do not expose JPA entities directly in API responses. Use request/response DTOs. Add validation annotations. Use custom exceptions and GlobalExceptionHandler where needed. Use Flyway for schema changes and UUID primary keys. Derive current user identity from JWT/SecurityContext, not from request body userId. Do not modify unrelated files. Add at least one meaningful automated test for backend business logic. After implementation, list files changed, manual test steps, automated test commands, assumptions, and Build Log updates.
```

### 8.10 JwtService

```text
Implement this backend MVP feature for CodeQuest.

Goal:
Create JwtService for generating and validating JWT access tokens using environment-based secret and expiry.

Module:
auth/security

Endpoint:
No direct endpoint

Tables involved:
users only for subject/user id reference

Files likely to touch:
auth/JwtService, config properties, tests

Files not to touch:
Do not create JwtFilter or SecurityConfig in this prompt unless explicitly asked. Do not hard-code JWT secret.

Business rules:
- JWT secret must come from environment/config.
- Access token expiry uses JWT_ACCESS_MINUTES.
- Token subject/claims must be sufficient to identify current user securely.

Validation rules:
- Validate secret presence/length where practical.

Error handling rules:
- Invalid/expired token should be detected and not treated as authenticated.

Tests required:
- Token generation contains expected subject/claims.
- Invalid/expired token validation fails.

Commands to run:
- cd backend && mvn test

Build Log update:
- Mark this feature complete only after code compiles, tests pass, and manual testing works.
- Add files changed, test result, and next feature to CodeQuest_Build_Log.md.

Use this prompt exactly. Follow CodeQuest_AI_Control_Master_Blueprint_v3.docx, CodeQuest_Core_Rules.md, CodeQuest_DB_Schema.md, CodeQuest_API_Contracts.md, AGENTS.md, and CodeQuest_Build_Log.md. MVP first. Do not implement Phase 2 or Phase 3 unless I explicitly ask. Do not redesign the architecture. Do not invent new features. If unsure, ask instead of guessing. Keep CodeQuest as a modular monolith. For backend work, keep Controller -> Service -> Repository separation. Do not put business logic in controllers. Do not expose JPA entities directly in API responses. Use request/response DTOs. Add validation annotations. Use custom exceptions and GlobalExceptionHandler where needed. Use Flyway for schema changes and UUID primary keys. Derive current user identity from JWT/SecurityContext, not from request body userId. Do not modify unrelated files. Add at least one meaningful automated test for backend business logic. After implementation, list files changed, manual test steps, automated test commands, assumptions, and Build Log updates.
```

### 8.11 JwtFilter + SecurityConfig

```text
Implement this backend MVP feature for CodeQuest.

Goal:
Add JWT validation filter, SecurityContext population, protected endpoint behavior, and 401 behavior without token.

Module:
auth/common/security

Endpoint:
Protect private endpoints; allow /api/auth/**, /actuator/health, /swagger-ui/**, /v3/api-docs/**

Tables involved:
users

Files likely to touch:
common/security, SecurityConfig, JwtFilter, auth/JwtService usage, tests

Files not to touch:
Do not use sessions for MVP API auth. Do not implement refresh tokens here.

Business rules:
- Use stateless Spring Security.
- JWT filter validates token and sets SecurityContext.
- Public allowlist must include auth, health, and Swagger docs.

Validation rules:
- Authorization header must be Bearer token if present.

Error handling rules:
- Unauthenticated protected request returns 401.
- Forbidden role cases return 403 if roles exist.

Tests required:
- Protected endpoint without token returns 401.
- Valid token allows protected access.

Commands to run:
- cd backend && mvn test

Build Log update:
- Mark this feature complete only after code compiles, tests pass, and manual testing works.
- Add files changed, test result, and next feature to CodeQuest_Build_Log.md.

Use this prompt exactly. Follow CodeQuest_AI_Control_Master_Blueprint_v3.docx, CodeQuest_Core_Rules.md, CodeQuest_DB_Schema.md, CodeQuest_API_Contracts.md, AGENTS.md, and CodeQuest_Build_Log.md. MVP first. Do not implement Phase 2 or Phase 3 unless I explicitly ask. Do not redesign the architecture. Do not invent new features. If unsure, ask instead of guessing. Keep CodeQuest as a modular monolith. For backend work, keep Controller -> Service -> Repository separation. Do not put business logic in controllers. Do not expose JPA entities directly in API responses. Use request/response DTOs. Add validation annotations. Use custom exceptions and GlobalExceptionHandler where needed. Use Flyway for schema changes and UUID primary keys. Derive current user identity from JWT/SecurityContext, not from request body userId. Do not modify unrelated files. Add at least one meaningful automated test for backend business logic. After implementation, list files changed, manual test steps, automated test commands, assumptions, and Build Log updates.
```

### 8.12 Refresh token

```text
Implement this backend MVP feature for CodeQuest.

Goal:
Implement refresh token creation, hashing, persistence, expiry, and access-token refresh endpoint.

Module:
auth

Endpoint:
POST /api/auth/refresh

Tables involved:
refresh_tokens, users

Files likely to touch:
auth controller/service/dto/repository/entity/Flyway/tests

Files not to touch:
Do not store raw refresh tokens. Do not implement logout in this prompt unless explicitly asked.

Business rules:
- Store refresh token as token_hash only.
- Refresh token has expires_at and optional revoked_at.
- Refresh endpoint returns a new access token after validating token.

Validation rules:
- Refresh token required and nonblank.

Error handling rules:
- Expired/revoked/unknown refresh token returns 401.

Tests required:
- Valid refresh returns access token.
- Revoked/expired refresh token rejected.
- Raw token is not stored.

Commands to run:
- cd backend && mvn test

Build Log update:
- Mark this feature complete only after code compiles, tests pass, and manual testing works.
- Add files changed, test result, and next feature to CodeQuest_Build_Log.md.

Use this prompt exactly. Follow CodeQuest_AI_Control_Master_Blueprint_v3.docx, CodeQuest_Core_Rules.md, CodeQuest_DB_Schema.md, CodeQuest_API_Contracts.md, AGENTS.md, and CodeQuest_Build_Log.md. MVP first. Do not implement Phase 2 or Phase 3 unless I explicitly ask. Do not redesign the architecture. Do not invent new features. If unsure, ask instead of guessing. Keep CodeQuest as a modular monolith. For backend work, keep Controller -> Service -> Repository separation. Do not put business logic in controllers. Do not expose JPA entities directly in API responses. Use request/response DTOs. Add validation annotations. Use custom exceptions and GlobalExceptionHandler where needed. Use Flyway for schema changes and UUID primary keys. Derive current user identity from JWT/SecurityContext, not from request body userId. Do not modify unrelated files. Add at least one meaningful automated test for backend business logic. After implementation, list files changed, manual test steps, automated test commands, assumptions, and Build Log updates.
```

### 8.13 Logout / token revoke

```text
Implement this backend MVP feature for CodeQuest.

Goal:
Implement logout by revoking the current refresh token or all active refresh tokens depending on current API contract.

Module:
auth

Endpoint:
POST /api/auth/logout

Tables involved:
refresh_tokens

Files likely to touch:
auth controller/service/dto/repository/tests

Files not to touch:
Do not delete users. Do not invalidate all users. Do not change login response shape.

Business rules:
- Logout sets revoked_at for the relevant refresh token.
- Logout is idempotent where practical.
- Never expose token_hash.

Validation rules:
- Refresh token or authenticated context required according to API contract.

Error handling rules:
- Invalid or already-revoked token returns safe response or 401 per contract.

Tests required:
- Logout revokes token.
- Revoked token cannot be refreshed.

Commands to run:
- cd backend && mvn test

Build Log update:
- Mark this feature complete only after code compiles, tests pass, and manual testing works.
- Add files changed, test result, and next feature to CodeQuest_Build_Log.md.

Use this prompt exactly. Follow CodeQuest_AI_Control_Master_Blueprint_v3.docx, CodeQuest_Core_Rules.md, CodeQuest_DB_Schema.md, CodeQuest_API_Contracts.md, AGENTS.md, and CodeQuest_Build_Log.md. MVP first. Do not implement Phase 2 or Phase 3 unless I explicitly ask. Do not redesign the architecture. Do not invent new features. If unsure, ask instead of guessing. Keep CodeQuest as a modular monolith. For backend work, keep Controller -> Service -> Repository separation. Do not put business logic in controllers. Do not expose JPA entities directly in API responses. Use request/response DTOs. Add validation annotations. Use custom exceptions and GlobalExceptionHandler where needed. Use Flyway for schema changes and UUID primary keys. Derive current user identity from JWT/SecurityContext, not from request body userId. Do not modify unrelated files. Add at least one meaningful automated test for backend business logic. After implementation, list files changed, manual test steps, automated test commands, assumptions, and Build Log updates.
```

### 8.14 User profile

```text
Implement this backend MVP feature for CodeQuest.

Goal:
Implement current user profile retrieval and update for safe editable fields.

Module:
user

Endpoint:
GET /api/user/profile and PATCH /api/user/profile

Tables involved:
users

Files likely to touch:
user controller/service/repository/dto/mapper/tests

Files not to touch:
Do not allow email/password_hash/XP/rank/role updates through profile PATCH.

Business rules:
- User identity comes from JWT/SecurityContext.
- GET returns current profile stats.
- PATCH updates only name, goal, avatarUrl if allowed.

Validation rules:
- Name 2-100 chars if present; avatarUrl max length and URL if present; goal enum if used.

Error handling rules:
- Unauthenticated request returns 401.
- Invalid profile data returns 400.

Tests required:
- GET profile returns current user.
- PATCH cannot modify protected fields.

Commands to run:
- cd backend && mvn test

Build Log update:
- Mark this feature complete only after code compiles, tests pass, and manual testing works.
- Add files changed, test result, and next feature to CodeQuest_Build_Log.md.

Use this prompt exactly. Follow CodeQuest_AI_Control_Master_Blueprint_v3.docx, CodeQuest_Core_Rules.md, CodeQuest_DB_Schema.md, CodeQuest_API_Contracts.md, AGENTS.md, and CodeQuest_Build_Log.md. MVP first. Do not implement Phase 2 or Phase 3 unless I explicitly ask. Do not redesign the architecture. Do not invent new features. If unsure, ask instead of guessing. Keep CodeQuest as a modular monolith. For backend work, keep Controller -> Service -> Repository separation. Do not put business logic in controllers. Do not expose JPA entities directly in API responses. Use request/response DTOs. Add validation annotations. Use custom exceptions and GlobalExceptionHandler where needed. Use Flyway for schema changes and UUID primary keys. Derive current user identity from JWT/SecurityContext, not from request body userId. Do not modify unrelated files. Add at least one meaningful automated test for backend business logic. After implementation, list files changed, manual test steps, automated test commands, assumptions, and Build Log updates.
```

### 8.15 AI course generation

```text
Implement this backend MVP feature for CodeQuest.

Goal:
Implement POST /api/courses/generate orchestration: cache lookup, Gemini call, response parsing, course graph persistence, and response DTO.

Module:
course/ai

Endpoint:
POST /api/courses/generate

Tables involved:
courses, levels, quizzes, flashcards, coding_problems

Files likely to touch:
course controller/service/repository/dto/entity/mapper, ai services, Flyway migrations, tests

Files not to touch:
Do not call Gemini from controller. Do not skip parser validation. Do not create Phase 2 AI tutor.

Business rules:
- Check existing course by normalized_topic + difficulty before Gemini.
- All Gemini calls go through GeminiService.
- Store course, levels, quizzes, flashcards, and coding problems in one transaction.
- Return cacheHit true when reused.

Validation rules:
- Topic 2-80 chars; difficulty enum; reject obvious prompt injection/command-like topic.

Error handling rules:
- Malformed AI JSON retries once then maps to GeminiParseException/502.
- Gemini timeout/rate limit maps cleanly.

Tests required:
- Cache hit avoids Gemini call.
- Valid Gemini JSON persists course graph.
- Malformed JSON returns clean error.

Commands to run:
- cd backend && mvn test

Build Log update:
- Mark this feature complete only after code compiles, tests pass, and manual testing works.
- Add files changed, test result, and next feature to CodeQuest_Build_Log.md.

Gemini rules:
- PromptBuilder creates prompts.
- ResponseParser validates JSON.
- Do not send secrets, JWTs, passwords, tokens, or private user data to Gemini.
- Validate schema before storing AI output.
Use this prompt exactly. Follow CodeQuest_AI_Control_Master_Blueprint_v3.docx, CodeQuest_Core_Rules.md, CodeQuest_DB_Schema.md, CodeQuest_API_Contracts.md, AGENTS.md, and CodeQuest_Build_Log.md. MVP first. Do not implement Phase 2 or Phase 3 unless I explicitly ask. Do not redesign the architecture. Do not invent new features. If unsure, ask instead of guessing. Keep CodeQuest as a modular monolith. For backend work, keep Controller -> Service -> Repository separation. Do not put business logic in controllers. Do not expose JPA entities directly in API responses. Use request/response DTOs. Add validation annotations. Use custom exceptions and GlobalExceptionHandler where needed. Use Flyway for schema changes and UUID primary keys. Derive current user identity from JWT/SecurityContext, not from request body userId. Do not modify unrelated files. Add at least one meaningful automated test for backend business logic. After implementation, list files changed, manual test steps, automated test commands, assumptions, and Build Log updates.
```

### 8.16 GeminiService + PromptBuilder

```text
Implement this backend MVP feature for CodeQuest.

Goal:
Create GeminiService and PromptBuilder as the only approved path for Gemini API calls and prompt construction.

Module:
ai

Endpoint:
No direct endpoint

Tables involved:
No direct table; used by courses and AI review

Files likely to touch:
ai/GeminiService, ai/PromptBuilder, config, tests

Files not to touch:
Do not call Gemini from controllers or unrelated services. Do not implement course persistence here.

Business rules:
- Gemini API key comes from environment.
- PromptBuilder creates course/code-review/explain-error prompts.
- GeminiService handles HTTP client, timeout, transient failure mapping.

Validation rules:
- Do not include secrets or private user data in prompts.

Error handling rules:
- Timeout, 429, 5xx map to domain exceptions handled by GlobalExceptionHandler.

Tests required:
- PromptBuilder produces expected prompt constraints.
- GeminiService handles success and external error mock.

Commands to run:
- cd backend && mvn test

Build Log update:
- Mark this feature complete only after code compiles, tests pass, and manual testing works.
- Add files changed, test result, and next feature to CodeQuest_Build_Log.md.

Gemini rules:
- All Gemini calls must go through GeminiService.
- PromptBuilder creates prompts.
- Do not send secrets, JWTs, passwords, tokens, or private user data to Gemini.
Use this prompt exactly. Follow CodeQuest_AI_Control_Master_Blueprint_v3.docx, CodeQuest_Core_Rules.md, CodeQuest_DB_Schema.md, CodeQuest_API_Contracts.md, AGENTS.md, and CodeQuest_Build_Log.md. MVP first. Do not implement Phase 2 or Phase 3 unless I explicitly ask. Do not redesign the architecture. Do not invent new features. If unsure, ask instead of guessing. Keep CodeQuest as a modular monolith. For backend work, keep Controller -> Service -> Repository separation. Do not put business logic in controllers. Do not expose JPA entities directly in API responses. Use request/response DTOs. Add validation annotations. Use custom exceptions and GlobalExceptionHandler where needed. Use Flyway for schema changes and UUID primary keys. Derive current user identity from JWT/SecurityContext, not from request body userId. Do not modify unrelated files. Add at least one meaningful automated test for backend business logic. After implementation, list files changed, manual test steps, automated test commands, assumptions, and Build Log updates.
```

### 8.17 ResponseParser + AI validation

```text
Implement this backend MVP feature for CodeQuest.

Goal:
Create ResponseParser and AI validation helpers for strict JSON parsing and schema validation before persistence.

Module:
ai

Endpoint:
No direct endpoint

Tables involved:
courses, levels, quizzes, flashcards, coding_problems when used

Files likely to touch:
ai/ResponseParser, ai/AiSafetyService or validators, tests

Files not to touch:
Do not persist AI output in this prompt unless called by existing course generation flow.

Business rules:
- Parse JSON-only AI responses.
- Validate required fields, enum values, list lengths, max lengths, and nested objects.
- Throw GeminiParseException or validation exception on malformed output.

Validation rules:
- Reject markdown fences, missing fields, invalid enum, oversized content.

Error handling rules:
- Malformed JSON maps to AI parse error, not NullPointerException.

Tests required:
- Valid course JSON parses.
- Malformed JSON rejected.
- Missing required field rejected.

Commands to run:
- cd backend && mvn test

Build Log update:
- Mark this feature complete only after code compiles, tests pass, and manual testing works.
- Add files changed, test result, and next feature to CodeQuest_Build_Log.md.

Gemini rules:
- ResponseParser validates JSON.
- Validate schema before storing AI output.
- Handle malformed JSON with clean error handling.
Use this prompt exactly. Follow CodeQuest_AI_Control_Master_Blueprint_v3.docx, CodeQuest_Core_Rules.md, CodeQuest_DB_Schema.md, CodeQuest_API_Contracts.md, AGENTS.md, and CodeQuest_Build_Log.md. MVP first. Do not implement Phase 2 or Phase 3 unless I explicitly ask. Do not redesign the architecture. Do not invent new features. If unsure, ask instead of guessing. Keep CodeQuest as a modular monolith. For backend work, keep Controller -> Service -> Repository separation. Do not put business logic in controllers. Do not expose JPA entities directly in API responses. Use request/response DTOs. Add validation annotations. Use custom exceptions and GlobalExceptionHandler where needed. Use Flyway for schema changes and UUID primary keys. Derive current user identity from JWT/SecurityContext, not from request body userId. Do not modify unrelated files. Add at least one meaningful automated test for backend business logic. After implementation, list files changed, manual test steps, automated test commands, assumptions, and Build Log updates.
```

### 8.18 Get course by ID

```text
Implement this backend MVP feature for CodeQuest.

Goal:
Implement course detail retrieval by ID with levels summary for the course map.

Module:
course

Endpoint:
GET /api/courses/{courseId}

Tables involved:
courses, levels, progress optional

Files likely to touch:
course controller/service/repository/dto/mapper/tests

Files not to touch:
Do not include public listing or enrollment in this prompt.

Business rules:
- Return course with ordered level summaries.
- Include lock/progress information only if contract already supports it.
- Do not expose entity internals.

Validation rules:
- courseId must be valid UUID.

Error handling rules:
- Missing course returns 404.

Tests required:
- Existing course returns ordered levels.
- Missing course returns 404.

Commands to run:
- cd backend && mvn test

Build Log update:
- Mark this feature complete only after code compiles, tests pass, and manual testing works.
- Add files changed, test result, and next feature to CodeQuest_Build_Log.md.

Use this prompt exactly. Follow CodeQuest_AI_Control_Master_Blueprint_v3.docx, CodeQuest_Core_Rules.md, CodeQuest_DB_Schema.md, CodeQuest_API_Contracts.md, AGENTS.md, and CodeQuest_Build_Log.md. MVP first. Do not implement Phase 2 or Phase 3 unless I explicitly ask. Do not redesign the architecture. Do not invent new features. If unsure, ask instead of guessing. Keep CodeQuest as a modular monolith. For backend work, keep Controller -> Service -> Repository separation. Do not put business logic in controllers. Do not expose JPA entities directly in API responses. Use request/response DTOs. Add validation annotations. Use custom exceptions and GlobalExceptionHandler where needed. Use Flyway for schema changes and UUID primary keys. Derive current user identity from JWT/SecurityContext, not from request body userId. Do not modify unrelated files. Add at least one meaningful automated test for backend business logic. After implementation, list files changed, manual test steps, automated test commands, assumptions, and Build Log updates.
```

### 8.19 Public courses listing

```text
Implement this backend MVP feature for CodeQuest.

Goal:
Implement public course discovery with pagination and max page size.

Module:
course

Endpoint:
GET /api/courses/public

Tables involved:
courses

Files likely to touch:
course controller/service/repository/dto/tests

Files not to touch:
Do not implement course generation or enrollment here.

Business rules:
- Return public courses only.
- Support pagination with max page size 50.
- Sort consistently, for example newest or popular if field exists.

Validation rules:
- Validate page and size; cap size at 50.

Error handling rules:
- Invalid pagination returns 400 or normalized defaults per contract.

Tests required:
- Public-only results.
- Pagination max enforced.

Commands to run:
- cd backend && mvn test

Build Log update:
- Mark this feature complete only after code compiles, tests pass, and manual testing works.
- Add files changed, test result, and next feature to CodeQuest_Build_Log.md.

Use this prompt exactly. Follow CodeQuest_AI_Control_Master_Blueprint_v3.docx, CodeQuest_Core_Rules.md, CodeQuest_DB_Schema.md, CodeQuest_API_Contracts.md, AGENTS.md, and CodeQuest_Build_Log.md. MVP first. Do not implement Phase 2 or Phase 3 unless I explicitly ask. Do not redesign the architecture. Do not invent new features. If unsure, ask instead of guessing. Keep CodeQuest as a modular monolith. For backend work, keep Controller -> Service -> Repository separation. Do not put business logic in controllers. Do not expose JPA entities directly in API responses. Use request/response DTOs. Add validation annotations. Use custom exceptions and GlobalExceptionHandler where needed. Use Flyway for schema changes and UUID primary keys. Derive current user identity from JWT/SecurityContext, not from request body userId. Do not modify unrelated files. Add at least one meaningful automated test for backend business logic. After implementation, list files changed, manual test steps, automated test commands, assumptions, and Build Log updates.
```

### 8.20 Enroll in course

```text
Implement this backend MVP feature for CodeQuest.

Goal:
Implement enrollment in an existing course and initialize progress records if required.

Module:
course/progress

Endpoint:
POST /api/courses/{courseId}/enroll

Tables involved:
courses, levels, progress, users

Files likely to touch:
course/progress service/repository/dto/tests

Files not to touch:
Do not generate courses here. Do not duplicate progress rows.

Business rules:
- User identity comes from JWT.
- Enrollment creates initial progress records if needed.
- Duplicate enrollment is idempotent or returns 409 per API contract.

Validation rules:
- courseId must be valid UUID.

Error handling rules:
- Missing course returns 404.
- Duplicate enrollment handled cleanly.

Tests required:
- Enrollment creates progress without duplicates.
- Duplicate enrollment safe.

Commands to run:
- cd backend && mvn test

Build Log update:
- Mark this feature complete only after code compiles, tests pass, and manual testing works.
- Add files changed, test result, and next feature to CodeQuest_Build_Log.md.

Use this prompt exactly. Follow CodeQuest_AI_Control_Master_Blueprint_v3.docx, CodeQuest_Core_Rules.md, CodeQuest_DB_Schema.md, CodeQuest_API_Contracts.md, AGENTS.md, and CodeQuest_Build_Log.md. MVP first. Do not implement Phase 2 or Phase 3 unless I explicitly ask. Do not redesign the architecture. Do not invent new features. If unsure, ask instead of guessing. Keep CodeQuest as a modular monolith. For backend work, keep Controller -> Service -> Repository separation. Do not put business logic in controllers. Do not expose JPA entities directly in API responses. Use request/response DTOs. Add validation annotations. Use custom exceptions and GlobalExceptionHandler where needed. Use Flyway for schema changes and UUID primary keys. Derive current user identity from JWT/SecurityContext, not from request body userId. Do not modify unrelated files. Add at least one meaningful automated test for backend business logic. After implementation, list files changed, manual test steps, automated test commands, assumptions, and Build Log updates.
```

### 8.21 Level details

```text
Implement this backend MVP feature for CodeQuest.

Goal:
Implement level detail retrieval including lesson markdown, flashcards, quiz questions, and coding problems.

Module:
level

Endpoint:
GET /api/levels/{levelId}

Tables involved:
levels, quizzes, flashcards, coding_problems, progress

Files likely to touch:
level controller/service/repository/dto/mapper/tests

Files not to touch:
Do not implement completion in this prompt. Do not return correct quiz answers to frontend unless contract allows explanation after submit.

Business rules:
- Return level content for accessible/unlocked level.
- Enforce lock/ownership/progress rules.
- Do not expose hidden tests in coding problems.

Validation rules:
- levelId must be valid UUID.

Error handling rules:
- Missing level 404; locked level 403 or locked error.

Tests required:
- Unlocked level returns details.
- Locked level rejected.

Commands to run:
- cd backend && mvn test

Build Log update:
- Mark this feature complete only after code compiles, tests pass, and manual testing works.
- Add files changed, test result, and next feature to CodeQuest_Build_Log.md.

Use this prompt exactly. Follow CodeQuest_AI_Control_Master_Blueprint_v3.docx, CodeQuest_Core_Rules.md, CodeQuest_DB_Schema.md, CodeQuest_API_Contracts.md, AGENTS.md, and CodeQuest_Build_Log.md. MVP first. Do not implement Phase 2 or Phase 3 unless I explicitly ask. Do not redesign the architecture. Do not invent new features. If unsure, ask instead of guessing. Keep CodeQuest as a modular monolith. For backend work, keep Controller -> Service -> Repository separation. Do not put business logic in controllers. Do not expose JPA entities directly in API responses. Use request/response DTOs. Add validation annotations. Use custom exceptions and GlobalExceptionHandler where needed. Use Flyway for schema changes and UUID primary keys. Derive current user identity from JWT/SecurityContext, not from request body userId. Do not modify unrelated files. Add at least one meaningful automated test for backend business logic. After implementation, list files changed, manual test steps, automated test commands, assumptions, and Build Log updates.
```

### 8.22 Level completion

```text
Implement this backend MVP feature for CodeQuest.

Goal:
Implement level completion and lesson XP award once per level.

Module:
level/progress

Endpoint:
POST /api/levels/{levelId}/complete

Tables involved:
levels, progress, users

Files likely to touch:
level/progress service/repository/dto/tests

Files not to touch:
Do not implement quiz submit or code submit here.

Business rules:
- Completion awards lesson XP once only.
- Use @Transactional for progress + XP update.
- Do not award XP again for already completed level.

Validation rules:
- levelId must be valid UUID.

Error handling rules:
- Locked level cannot be completed.
- Duplicate completion returns zero XP or already-completed response per contract.

Tests required:
- Completion awards XP once.
- Duplicate completion awards 0.

Commands to run:
- cd backend && mvn test

Build Log update:
- Mark this feature complete only after code compiles, tests pass, and manual testing works.
- Add files changed, test result, and next feature to CodeQuest_Build_Log.md.

Use this prompt exactly. Follow CodeQuest_AI_Control_Master_Blueprint_v3.docx, CodeQuest_Core_Rules.md, CodeQuest_DB_Schema.md, CodeQuest_API_Contracts.md, AGENTS.md, and CodeQuest_Build_Log.md. MVP first. Do not implement Phase 2 or Phase 3 unless I explicitly ask. Do not redesign the architecture. Do not invent new features. If unsure, ask instead of guessing. Keep CodeQuest as a modular monolith. For backend work, keep Controller -> Service -> Repository separation. Do not put business logic in controllers. Do not expose JPA entities directly in API responses. Use request/response DTOs. Add validation annotations. Use custom exceptions and GlobalExceptionHandler where needed. Use Flyway for schema changes and UUID primary keys. Derive current user identity from JWT/SecurityContext, not from request body userId. Do not modify unrelated files. Add at least one meaningful automated test for backend business logic. After implementation, list files changed, manual test steps, automated test commands, assumptions, and Build Log updates.
```

### 8.23 Level unlock logic

```text
Implement this backend MVP feature for CodeQuest.

Goal:
Implement or extract reusable level unlock and boss-level access logic.

Module:
level/progress

Endpoint:
Used by GET /api/levels/{levelId} and POST /api/levels/{levelId}/complete

Tables involved:
levels, progress

Files likely to touch:
level service, progress repository/service, tests

Files not to touch:
Do not create new endpoints unless required. Do not bypass progress rules.

Business rules:
- Level unlock depends on previous levels completed.
- Boss level requires all previous levels completed.
- Logic must be reusable by retrieval and completion flows.

Validation rules:
- Validate course/level relationship integrity.

Error handling rules:
- LockedLevelException maps to clear 403/locked error.

Tests required:
- Previous level incomplete => locked.
- All previous complete => unlocked.
- Boss rules enforced.

Commands to run:
- cd backend && mvn test

Build Log update:
- Mark this feature complete only after code compiles, tests pass, and manual testing works.
- Add files changed, test result, and next feature to CodeQuest_Build_Log.md.

Use this prompt exactly. Follow CodeQuest_AI_Control_Master_Blueprint_v3.docx, CodeQuest_Core_Rules.md, CodeQuest_DB_Schema.md, CodeQuest_API_Contracts.md, AGENTS.md, and CodeQuest_Build_Log.md. MVP first. Do not implement Phase 2 or Phase 3 unless I explicitly ask. Do not redesign the architecture. Do not invent new features. If unsure, ask instead of guessing. Keep CodeQuest as a modular monolith. For backend work, keep Controller -> Service -> Repository separation. Do not put business logic in controllers. Do not expose JPA entities directly in API responses. Use request/response DTOs. Add validation annotations. Use custom exceptions and GlobalExceptionHandler where needed. Use Flyway for schema changes and UUID primary keys. Derive current user identity from JWT/SecurityContext, not from request body userId. Do not modify unrelated files. Add at least one meaningful automated test for backend business logic. After implementation, list files changed, manual test steps, automated test commands, assumptions, and Build Log updates.
```

### 8.24 Quiz submit

```text
Implement this backend MVP feature for CodeQuest.

Goal:
Implement quiz answer submission, score calculation, progress update, and XP award.

Module:
quiz/progress

Endpoint:
POST /api/quizzes/{levelId}/submit

Tables involved:
quizzes, progress, users

Files likely to touch:
quiz controller/service/repository/dto, progress/XP service, tests

Files not to touch:
Do not generate remedial AI level here unless explicitly requested. Do not expose answers before submit.

Business rules:
- Validate quiz belongs to level.
- Calculate score and correct count.
- Store quiz_answers_json.
- Award XP only during first completion.

Validation rules:
- selectedOption must be A/B/C/D; questionId valid UUID; answers required.

Error handling rules:
- Invalid questionId returns 400; missing level 404; locked level 403.

Tests required:
- Correct score calculation.
- Invalid questionId rejected.
- XP not awarded twice.

Commands to run:
- cd backend && mvn test

Build Log update:
- Mark this feature complete only after code compiles, tests pass, and manual testing works.
- Add files changed, test result, and next feature to CodeQuest_Build_Log.md.

Use this prompt exactly. Follow CodeQuest_AI_Control_Master_Blueprint_v3.docx, CodeQuest_Core_Rules.md, CodeQuest_DB_Schema.md, CodeQuest_API_Contracts.md, AGENTS.md, and CodeQuest_Build_Log.md. MVP first. Do not implement Phase 2 or Phase 3 unless I explicitly ask. Do not redesign the architecture. Do not invent new features. If unsure, ask instead of guessing. Keep CodeQuest as a modular monolith. For backend work, keep Controller -> Service -> Repository separation. Do not put business logic in controllers. Do not expose JPA entities directly in API responses. Use request/response DTOs. Add validation annotations. Use custom exceptions and GlobalExceptionHandler where needed. Use Flyway for schema changes and UUID primary keys. Derive current user identity from JWT/SecurityContext, not from request body userId. Do not modify unrelated files. Add at least one meaningful automated test for backend business logic. After implementation, list files changed, manual test steps, automated test commands, assumptions, and Build Log updates.
```

### 8.25 Weak concept detection

```text
Implement this backend MVP feature for CodeQuest.

Goal:
Extract weak concept tags from wrong quiz answers and return them in quiz result.

Module:
quiz/progress

Endpoint:
Used by POST /api/quizzes/{levelId}/submit

Tables involved:
quizzes, progress

Files likely to touch:
quiz service, DTOs, tests

Files not to touch:
Do not call Gemini or generate remedial levels in this MVP prompt unless explicitly requested.

Business rules:
- Use concept_tag from wrong answers.
- Return unique weak concept list.
- Store weak concepts only if schema/contract supports it.

Validation rules:
- No special request validation beyond quiz submit.

Error handling rules:
- Missing concept tags should not crash; return empty or available tags.

Tests required:
- Wrong answers produce weak concepts.
- All correct returns empty weak concept list.

Commands to run:
- cd backend && mvn test

Build Log update:
- Mark this feature complete only after code compiles, tests pass, and manual testing works.
- Add files changed, test result, and next feature to CodeQuest_Build_Log.md.

Use this prompt exactly. Follow CodeQuest_AI_Control_Master_Blueprint_v3.docx, CodeQuest_Core_Rules.md, CodeQuest_DB_Schema.md, CodeQuest_API_Contracts.md, AGENTS.md, and CodeQuest_Build_Log.md. MVP first. Do not implement Phase 2 or Phase 3 unless I explicitly ask. Do not redesign the architecture. Do not invent new features. If unsure, ask instead of guessing. Keep CodeQuest as a modular monolith. For backend work, keep Controller -> Service -> Repository separation. Do not put business logic in controllers. Do not expose JPA entities directly in API responses. Use request/response DTOs. Add validation annotations. Use custom exceptions and GlobalExceptionHandler where needed. Use Flyway for schema changes and UUID primary keys. Derive current user identity from JWT/SecurityContext, not from request body userId. Do not modify unrelated files. Add at least one meaningful automated test for backend business logic. After implementation, list files changed, manual test steps, automated test commands, assumptions, and Build Log updates.
```

### 8.26 XP/rank system

```text
Implement this backend MVP feature for CodeQuest.

Goal:
Implement XPService and rank-threshold updates used by lesson, quiz, code, daily, and boss actions.

Module:
progress/user

Endpoint:
Used by multiple endpoints; no new endpoint required

Tables involved:
users, progress, code_submissions

Files likely to touch:
progress/XPService, user entity/enum, tests

Files not to touch:
Do not add Phase 2 duel XP unless explicitly requested.

Business rules:
- Complete lesson +50 once; correct quiz answer +20 during first quiz completion; perfect quiz +50 first attempt; solve coding problem +100 first accepted; boss +200.
- Rank thresholds: BEGINNER 0, CODER 500, DEVELOPER 2000, ENGINEER 5000, ARCHITECT 12000, LEGEND 25000.
- Never award duplicate XP for same completed item.

Validation rules:
- N/A unless adding endpoint DTO.

Error handling rules:
- Invalid XP action should fail safely, not corrupt user XP.

Tests required:
- Rank upgrades at threshold.
- XP not awarded twice.
- XP addition persists correctly.

Commands to run:
- cd backend && mvn test

Build Log update:
- Mark this feature complete only after code compiles, tests pass, and manual testing works.
- Add files changed, test result, and next feature to CodeQuest_Build_Log.md.

Use this prompt exactly. Follow CodeQuest_AI_Control_Master_Blueprint_v3.docx, CodeQuest_Core_Rules.md, CodeQuest_DB_Schema.md, CodeQuest_API_Contracts.md, AGENTS.md, and CodeQuest_Build_Log.md. MVP first. Do not implement Phase 2 or Phase 3 unless I explicitly ask. Do not redesign the architecture. Do not invent new features. If unsure, ask instead of guessing. Keep CodeQuest as a modular monolith. For backend work, keep Controller -> Service -> Repository separation. Do not put business logic in controllers. Do not expose JPA entities directly in API responses. Use request/response DTOs. Add validation annotations. Use custom exceptions and GlobalExceptionHandler where needed. Use Flyway for schema changes and UUID primary keys. Derive current user identity from JWT/SecurityContext, not from request body userId. Do not modify unrelated files. Add at least one meaningful automated test for backend business logic. After implementation, list files changed, manual test steps, automated test commands, assumptions, and Build Log updates.
```

### 8.27 Streak system

```text
Implement this backend MVP feature for CodeQuest.

Goal:
Implement StreakService and daily login XP guard.

Module:
progress/user

Endpoint:
Used by login/profile; no new endpoint unless contract requires

Tables involved:
users

Files likely to touch:
StreakService, AuthService integration if requested, tests

Files not to touch:
Do not award daily login XP on every JWT validation. Do not create email reminders; those are Phase 2.

Business rules:
- Daily login XP +30 once per calendar day only.
- Streak increments on consecutive day login and resets/handles gaps per defined rules.
- Use last_login safely.

Validation rules:
- Use server-side date/time consistently.

Error handling rules:
- Missing last_login initializes streak safely.

Tests required:
- Daily XP once per day.
- Second login same day awards 0.
- Consecutive day increments streak.

Commands to run:
- cd backend && mvn test

Build Log update:
- Mark this feature complete only after code compiles, tests pass, and manual testing works.
- Add files changed, test result, and next feature to CodeQuest_Build_Log.md.

Use this prompt exactly. Follow CodeQuest_AI_Control_Master_Blueprint_v3.docx, CodeQuest_Core_Rules.md, CodeQuest_DB_Schema.md, CodeQuest_API_Contracts.md, AGENTS.md, and CodeQuest_Build_Log.md. MVP first. Do not implement Phase 2 or Phase 3 unless I explicitly ask. Do not redesign the architecture. Do not invent new features. If unsure, ask instead of guessing. Keep CodeQuest as a modular monolith. For backend work, keep Controller -> Service -> Repository separation. Do not put business logic in controllers. Do not expose JPA entities directly in API responses. Use request/response DTOs. Add validation annotations. Use custom exceptions and GlobalExceptionHandler where needed. Use Flyway for schema changes and UUID primary keys. Derive current user identity from JWT/SecurityContext, not from request body userId. Do not modify unrelated files. Add at least one meaningful automated test for backend business logic. After implementation, list files changed, manual test steps, automated test commands, assumptions, and Build Log updates.
```

### 8.28 Piston run code

```text
Implement this backend MVP feature for CodeQuest.

Goal:
Implement code execution through Piston for run-only endpoint without XP award.

Module:
problem

Endpoint:
POST /api/problems/{problemId}/run

Tables involved:
coding_problems optional read

Files likely to touch:
problem controller/service/dto, PistonClient/PistonService, tests

Files not to touch:
Do not execute user code locally. Do not award XP. Do not store submissions from run-only endpoint unless explicitly required.

Business rules:
- Backend delegates to Piston API only.
- Run visible tests or provided stdin according to contract.
- Return actual output, stderr, runtime, and pass/fail where supported.

Validation rules:
- Code max 20,000 chars; language allowlist java/python/javascript/cpp or configured MVP list.

Error handling rules:
- Piston down maps to CODE_RUNNER_UNAVAILABLE-style error.

Tests required:
- Valid run delegates to Piston client.
- Disallowed language rejected.
- Piston failure maps cleanly.

Commands to run:
- cd backend && mvn test

Build Log update:
- Mark this feature complete only after code compiles, tests pass, and manual testing works.
- Add files changed, test result, and next feature to CodeQuest_Build_Log.md.

Code execution rules:
- Backend must never execute user code locally.
- Use Piston API only.
- Enforce language allowlist and code length limit.
- Do not award XP from run-only endpoint.
Use this prompt exactly. Follow CodeQuest_AI_Control_Master_Blueprint_v3.docx, CodeQuest_Core_Rules.md, CodeQuest_DB_Schema.md, CodeQuest_API_Contracts.md, AGENTS.md, and CodeQuest_Build_Log.md. MVP first. Do not implement Phase 2 or Phase 3 unless I explicitly ask. Do not redesign the architecture. Do not invent new features. If unsure, ask instead of guessing. Keep CodeQuest as a modular monolith. For backend work, keep Controller -> Service -> Repository separation. Do not put business logic in controllers. Do not expose JPA entities directly in API responses. Use request/response DTOs. Add validation annotations. Use custom exceptions and GlobalExceptionHandler where needed. Use Flyway for schema changes and UUID primary keys. Derive current user identity from JWT/SecurityContext, not from request body userId. Do not modify unrelated files. Add at least one meaningful automated test for backend business logic. After implementation, list files changed, manual test steps, automated test commands, assumptions, and Build Log updates.
```

### 8.29 Code submit

```text
Implement this backend MVP feature for CodeQuest.

Goal:
Implement coding problem submission with hidden/visible test comparison and first-accepted XP award.

Module:
problem/progress

Endpoint:
POST /api/problems/{problemId}/submit

Tables involved:
coding_problems, code_submissions, users, progress

Files likely to touch:
problem controller/service/dto, CodeSubmissionService, XPService, repositories, tests

Files not to touch:
Do not execute locally. Do not skip hidden tests. Do not award XP more than once.

Business rules:
- Run visible and hidden tests through Piston.
- Compare output after trimming trailing whitespace.
- Store every attempt in code_submissions.
- Award XP only for first accepted solution for user/problem.

Validation rules:
- Code max 20,000 chars; language allowlist.

Error handling rules:
- Piston down maps cleanly; failed tests do not crash.

Tests required:
- All tests passed creates accepted submission.
- Failed tests create submission but no XP.
- XP awarded only once.

Commands to run:
- cd backend && mvn test

Build Log update:
- Mark this feature complete only after code compiles, tests pass, and manual testing works.
- Add files changed, test result, and next feature to CodeQuest_Build_Log.md.

Code execution rules:
- Backend must never execute user code locally.
- Use Piston API only.
- Store submissions only in submit flow.
Use this prompt exactly. Follow CodeQuest_AI_Control_Master_Blueprint_v3.docx, CodeQuest_Core_Rules.md, CodeQuest_DB_Schema.md, CodeQuest_API_Contracts.md, AGENTS.md, and CodeQuest_Build_Log.md. MVP first. Do not implement Phase 2 or Phase 3 unless I explicitly ask. Do not redesign the architecture. Do not invent new features. If unsure, ask instead of guessing. Keep CodeQuest as a modular monolith. For backend work, keep Controller -> Service -> Repository separation. Do not put business logic in controllers. Do not expose JPA entities directly in API responses. Use request/response DTOs. Add validation annotations. Use custom exceptions and GlobalExceptionHandler where needed. Use Flyway for schema changes and UUID primary keys. Derive current user identity from JWT/SecurityContext, not from request body userId. Do not modify unrelated files. Add at least one meaningful automated test for backend business logic. After implementation, list files changed, manual test steps, automated test commands, assumptions, and Build Log updates.
```

### 8.30 Code submissions history

```text
Implement this backend MVP feature for CodeQuest.

Goal:
Implement retrieval or service support for a user's submission history for a problem or profile where the API contract allows it.

Module:
problem

Endpoint:
Endpoint only if present in API contracts; otherwise service-level support only

Tables involved:
code_submissions, coding_problems, users

Files likely to touch:
problem service/repository/dto/tests

Files not to touch:
Do not invent a new endpoint if API contracts do not include one. Ask if endpoint is missing.

Business rules:
- User can see own submissions only.
- Submission history excludes sensitive hidden test details.
- Sort newest first and paginate if endpoint exists.

Validation rules:
- problemId/user identity validation.

Error handling rules:
- Ownership violation returns 403/404 safe response.

Tests required:
- Own submissions returned.
- Other user's submissions inaccessible.

Commands to run:
- cd backend && mvn test

Build Log update:
- Mark this feature complete only after code compiles, tests pass, and manual testing works.
- Add files changed, test result, and next feature to CodeQuest_Build_Log.md.

Use this prompt exactly. Follow CodeQuest_AI_Control_Master_Blueprint_v3.docx, CodeQuest_Core_Rules.md, CodeQuest_DB_Schema.md, CodeQuest_API_Contracts.md, AGENTS.md, and CodeQuest_Build_Log.md. MVP first. Do not implement Phase 2 or Phase 3 unless I explicitly ask. Do not redesign the architecture. Do not invent new features. If unsure, ask instead of guessing. Keep CodeQuest as a modular monolith. For backend work, keep Controller -> Service -> Repository separation. Do not put business logic in controllers. Do not expose JPA entities directly in API responses. Use request/response DTOs. Add validation annotations. Use custom exceptions and GlobalExceptionHandler where needed. Use Flyway for schema changes and UUID primary keys. Derive current user identity from JWT/SecurityContext, not from request body userId. Do not modify unrelated files. Add at least one meaningful automated test for backend business logic. After implementation, list files changed, manual test steps, automated test commands, assumptions, and Build Log updates.
```

### 8.31 AI code review

```text
Implement this backend MVP feature for CodeQuest.

Goal:
Implement Gemini-powered code review endpoint returning complexity, correctness issues, improvements, better approach, and encouragement.

Module:
ai/problem

Endpoint:
POST /api/ai/review-code

Tables involved:
code_submissions optional update

Files likely to touch:
ai controller/service/dto, GeminiService, PromptBuilder, ResponseParser, tests

Files not to touch:
Do not combine with explain-error in this prompt. Do not send secrets or private data to Gemini.

Business rules:
- All Gemini calls go through GeminiService.
- PromptBuilder creates code-review prompt.
- ResponseParser validates JSON response.
- Limit code length.

Validation rules:
- Language allowlist/known language; code required; problem context required if endpoint requires it.

Error handling rules:
- Gemini malformed response maps cleanly; Gemini unavailable returns 502-style AI error.

Tests required:
- Valid review returns parsed DTO.
- Malformed Gemini response handled.
- Code length limit enforced.

Commands to run:
- cd backend && mvn test

Build Log update:
- Mark this feature complete only after code compiles, tests pass, and manual testing works.
- Add files changed, test result, and next feature to CodeQuest_Build_Log.md.

Gemini rules:
- Do not send secrets, JWTs, passwords, tokens, or private user data to Gemini.
- Validate JSON before returning/storing.
Use this prompt exactly. Follow CodeQuest_AI_Control_Master_Blueprint_v3.docx, CodeQuest_Core_Rules.md, CodeQuest_DB_Schema.md, CodeQuest_API_Contracts.md, AGENTS.md, and CodeQuest_Build_Log.md. MVP first. Do not implement Phase 2 or Phase 3 unless I explicitly ask. Do not redesign the architecture. Do not invent new features. If unsure, ask instead of guessing. Keep CodeQuest as a modular monolith. For backend work, keep Controller -> Service -> Repository separation. Do not put business logic in controllers. Do not expose JPA entities directly in API responses. Use request/response DTOs. Add validation annotations. Use custom exceptions and GlobalExceptionHandler where needed. Use Flyway for schema changes and UUID primary keys. Derive current user identity from JWT/SecurityContext, not from request body userId. Do not modify unrelated files. Add at least one meaningful automated test for backend business logic. After implementation, list files changed, manual test steps, automated test commands, assumptions, and Build Log updates.
```

### 8.32 Explain runtime error

```text
Implement this backend MVP feature for CodeQuest.

Goal:
Implement Gemini-powered runtime error explanation endpoint separate from AI code review.

Module:
ai/problem

Endpoint:
POST /api/ai/explain-error

Tables involved:
code_submissions optional update

Files likely to touch:
ai controller/service/dto, GeminiService, PromptBuilder, ResponseParser optional, tests

Files not to touch:
Do not combine with AI code review. Do not expose private user data or secrets.

Business rules:
- All Gemini calls go through GeminiService.
- PromptBuilder creates explain-error prompt from safe problem/code/error context.
- Return clear beginner-friendly explanation and suggested fix.

Validation rules:
- Code length limit; error text length limit; language allowlist if provided.

Error handling rules:
- Gemini failure returns clean AI error and does not break code execution flow.

Tests required:
- Valid error explanation returns DTO.
- Overlong code/error rejected.
- Gemini failure handled.

Commands to run:
- cd backend && mvn test

Build Log update:
- Mark this feature complete only after code compiles, tests pass, and manual testing works.
- Add files changed, test result, and next feature to CodeQuest_Build_Log.md.

Gemini rules:
- Do not send secrets, JWTs, passwords, tokens, or private user data to Gemini.
- Validate schema if JSON is expected.
Use this prompt exactly. Follow CodeQuest_AI_Control_Master_Blueprint_v3.docx, CodeQuest_Core_Rules.md, CodeQuest_DB_Schema.md, CodeQuest_API_Contracts.md, AGENTS.md, and CodeQuest_Build_Log.md. MVP first. Do not implement Phase 2 or Phase 3 unless I explicitly ask. Do not redesign the architecture. Do not invent new features. If unsure, ask instead of guessing. Keep CodeQuest as a modular monolith. For backend work, keep Controller -> Service -> Repository separation. Do not put business logic in controllers. Do not expose JPA entities directly in API responses. Use request/response DTOs. Add validation annotations. Use custom exceptions and GlobalExceptionHandler where needed. Use Flyway for schema changes and UUID primary keys. Derive current user identity from JWT/SecurityContext, not from request body userId. Do not modify unrelated files. Add at least one meaningful automated test for backend business logic. After implementation, list files changed, manual test steps, automated test commands, assumptions, and Build Log updates.
```

### 8.33 Notes API

```text
Implement this backend MVP feature for CodeQuest.

Goal:
Implement lesson notes save/update for authenticated user's own level notes.

Module:
level/user

Endpoint:
POST /api/notes

Tables involved:
notes, users, levels

Files likely to touch:
notes controller/service/repository/dto/entity/mapper/tests, Flyway if notes table missing

Files not to touch:
Do not allow modifying another user's note. Do not implement rich editor storage beyond safe text/markdown.

Business rules:
- User identity from JWT.
- User can save/update own note for a level.
- Frontend must render notes safely.

Validation rules:
- content required and max length enforced; levelId valid UUID.

Error handling rules:
- Unauthorized 401; wrong ownership 403/404; overlong note 400.

Tests required:
- User saves own note.
- Cannot modify another user's note.
- Overlong note rejected.

Commands to run:
- cd backend && mvn test

Build Log update:
- Mark this feature complete only after code compiles, tests pass, and manual testing works.
- Add files changed, test result, and next feature to CodeQuest_Build_Log.md.

Use this prompt exactly. Follow CodeQuest_AI_Control_Master_Blueprint_v3.docx, CodeQuest_Core_Rules.md, CodeQuest_DB_Schema.md, CodeQuest_API_Contracts.md, AGENTS.md, and CodeQuest_Build_Log.md. MVP first. Do not implement Phase 2 or Phase 3 unless I explicitly ask. Do not redesign the architecture. Do not invent new features. If unsure, ask instead of guessing. Keep CodeQuest as a modular monolith. For backend work, keep Controller -> Service -> Repository separation. Do not put business logic in controllers. Do not expose JPA entities directly in API responses. Use request/response DTOs. Add validation annotations. Use custom exceptions and GlobalExceptionHandler where needed. Use Flyway for schema changes and UUID primary keys. Derive current user identity from JWT/SecurityContext, not from request body userId. Do not modify unrelated files. Add at least one meaningful automated test for backend business logic. After implementation, list files changed, manual test steps, automated test commands, assumptions, and Build Log updates.
```

### 8.34 Leaderboard REST

```text
Implement this backend MVP feature for CodeQuest.

Goal:
Implement MVP leaderboard using REST sorted by XP.

Module:
leaderboard

Endpoint:
GET /api/leaderboard

Tables involved:
users

Files likely to touch:
leaderboard controller/service/repository/dto/tests

Files not to touch:
Do not implement WebSocket leaderboard; it is Phase 2. Do not expose emails or sensitive fields.

Business rules:
- Return top users sorted by XP descending.
- Support pagination with max page size 50.
- Include current user's rank if practical and contract supports it.

Validation rules:
- Validate page/size and cap size at 50.

Error handling rules:
- Invalid pagination handled safely.

Tests required:
- Users sorted by XP descending.
- Sensitive fields excluded.
- Pagination max enforced.

Commands to run:
- cd backend && mvn test

Build Log update:
- Mark this feature complete only after code compiles, tests pass, and manual testing works.
- Add files changed, test result, and next feature to CodeQuest_Build_Log.md.

Use this prompt exactly. Follow CodeQuest_AI_Control_Master_Blueprint_v3.docx, CodeQuest_Core_Rules.md, CodeQuest_DB_Schema.md, CodeQuest_API_Contracts.md, AGENTS.md, and CodeQuest_Build_Log.md. MVP first. Do not implement Phase 2 or Phase 3 unless I explicitly ask. Do not redesign the architecture. Do not invent new features. If unsure, ask instead of guessing. Keep CodeQuest as a modular monolith. For backend work, keep Controller -> Service -> Repository separation. Do not put business logic in controllers. Do not expose JPA entities directly in API responses. Use request/response DTOs. Add validation annotations. Use custom exceptions and GlobalExceptionHandler where needed. Use Flyway for schema changes and UUID primary keys. Derive current user identity from JWT/SecurityContext, not from request body userId. Do not modify unrelated files. Add at least one meaningful automated test for backend business logic. After implementation, list files changed, manual test steps, automated test commands, assumptions, and Build Log updates.
```

## 5. Frontend MVP Feature Prompts

### 5.1 Frontend foundation and API client
```text
Implement frontend foundation for CodeQuest. Use React + Vite + Tailwind, React Router, Axios API client with VITE_API_BASE_URL, token attachment interceptor, 401 refresh placeholder, and folders pages/components/hooks/services/store/utils/constants. Do not build feature pages except placeholders. Do not invent backend fields outside API contracts. Run cd frontend && npm run build and tests if configured. Update Build Log.
```

### 5.2 Login and register pages
```text
Implement /auth/login and /auth/register using POST /api/auth/login and POST /api/auth/register. Build forms with validation, show backend validation errors cleanly, store access token according to auth strategy, redirect to dashboard after success, and do not implement OAuth unless explicitly requested. Add tests if configured. Update Build Log.
```

### 5.3 Protected routes and auth state
```text
Implement protected routes for private pages. Create auth store/hook, redirect unauthenticated users to /auth/login, keep routing aligned with MVP routes, handle token state cleanly, and do not add Phase 2 routes beyond placeholders. Run frontend build/tests. Update Build Log.
```

### 5.4 Dashboard page
```text
Implement MVP DashboardPage using API contracts only. Show XP, rank, streak, active courses, recently completed/next action, and daily challenge banner if endpoint exists. Include loading skeletons, friendly errors, responsive layout, and no invented backend fields. Run frontend build/tests. Update Build Log.
```

### 5.5 Course generation UI
```text
Implement course generation UI calling POST /api/courses/generate. Topic validation 2-80 chars, difficulty BEGINNER/INTERMEDIATE/ADVANCED, goal input/select, AI loading state, cacheHit messaging only if response includes it, success navigation to /map/:courseId, and clean AI/rate-limit errors. Update Build Log.
```

### 5.6 Course map UI
```text
Implement /map/:courseId using GET /api/courses/{courseId}. Display title and levels with locked/unlocked/current/boss states. Clicking unlocked level navigates to /lesson/:levelId. Locked level explains why. Use simple cards unless React Flow is intentionally installed. Do not invent response fields. Update Build Log.
```

### 5.7 Lesson page
```text
Implement /lesson/:levelId using GET /api/levels/{levelId}. Render lesson markdown safely, show flashcards, quiz/coding sections, notes UI only if notes endpoint exists, handle locked/missing errors, and avoid dangerouslySetInnerHTML unless sanitized. Run frontend checks. Update Build Log.
```

### 5.8 Flashcards
```text
Implement FlashcardDeck for level flashcards. Use the level detail response only, support flip animation, keyboard-friendly controls, empty state, and responsive layout. Do not add backend changes. Run frontend checks. Update Build Log.
```

### 5.9 Quiz UI
```text
Implement quiz UI for POST /api/quizzes/{levelId}/submit. Render MCQs, store selected answers, save draft to sessionStorage, prevent duplicate rapid submit, show score, XP, rank, weak concepts, and errors. Do not expose correct answers before submit unless returned by API after submission. Update Build Log.
```

### 5.10 Code editor with Monaco
```text
Implement code editor page using Monaco. Use allowlisted languages only, starter code from API contract, Run and Submit buttons, loading states, and no browser-side execution of user code. Integrate with run/submit prompts separately if needed. Update Build Log.
```

### 5.11 Piston run/submit UI
```text
Wire the code editor to POST /api/problems/{problemId}/run and POST /api/problems/{problemId}/submit. Show input, expected, actual, pass/fail, runtime, memory, errors, and CODE_RUNNER_UNAVAILABLE fallback. Do not invent backend fields. Update Build Log.
```

### 5.12 AI review panel
```text
Implement AiReviewPanel using aiReview returned by code submit or POST /api/ai/review-code if endpoint is used directly. Show time complexity, space complexity, issues, improvements, better approach, and encouragement. Handle AI unavailable cleanly. Update Build Log.
```

### 5.13 Leaderboard UI
```text
Implement /leaderboard using GET /api/leaderboard. Show users sorted by XP, current-user highlight if response supports it, pagination, no email/sensitive fields, responsive layout. Do not implement WebSocket in MVP. Update Build Log.
```

### 5.14 Loading and error states
```text
Add consistent loading skeletons, empty states, toast errors, and retry actions for dashboard, course generation, course map, lesson, quiz, code editor, AI review, and leaderboard. Do not change backend contracts. Update Build Log.
```

### 5.15 Responsive polish
```text
Polish MVP pages for mobile and desktop. Use accessible buttons, labels, keyboard navigation, readable contrast, and responsive spacing. Do not add new features. Run build/tests. Update Build Log.
```

### 5.16 Frontend QA pass
```text
Audit frontend against MVP routes, API contracts, auth handling, error states, responsive behavior, and no invented backend fields. Return issues and exact fix prompts. Do not modify code unless asked. Update Build Log if fixes are applied later.
```

## 6. DevOps / Deployment Prompts

### 6.1 Docker backend setup
```text
Create backend Dockerfile for Spring Boot. Use environment variables for DB, JWT, Gemini, Piston, and FRONTEND_ORIGIN. Do not hard-code secrets. Add build/run commands. Do not change application logic. Update Build Log.
```

### 6.2 docker-compose local setup
```text
Create docker-compose.yml for local PostgreSQL and backend if useful. Use environment variables, health checks, and clear README instructions. Do not add production secrets. Update Build Log.
```

### 6.3 GitHub Actions backend tests
```text
Create backend CI job using Java 21 Temurin and cd backend && mvn test --no-transfer-progress. Keep workflow simple. Do not include secrets. Update Build Log.
```

### 6.4 GitHub Actions frontend tests/build
```text
Create frontend CI job using Node 22, npm ci, npm test -- --run if tests exist, and npm run build. Keep workflow simple and reliable. Update Build Log.
```

### 6.5 Render backend deployment
```text
Write Render deployment instructions/config for backend. Include env vars, health check, port, Docker or build command, cold start note, and Swagger verification. Do not commit secrets. Update Build Log.
```

### 6.6 Vercel frontend deployment
```text
Write Vercel deployment instructions/config for frontend. Include VITE_API_BASE_URL, build command, output directory, and production API checks. Update Build Log.
```

### 6.7 Neon PostgreSQL production config
```text
Document Neon PostgreSQL production configuration. Include DATABASE_URL/USERNAME/PASSWORD handling, SSL note if needed, migration execution, and DB size monitoring. Do not commit credentials. Update Build Log.
```

### 6.8 Environment variables audit
```text
Audit required environment variables: DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD, JWT_SECRET, JWT_ACCESS_MINUTES, GEMINI_API_KEY, PISTON_BASE_URL, FRONTEND_ORIGIN, VITE_API_BASE_URL. Ensure no secrets are committed. Update docs and Build Log.
```

### 6.9 CORS production config
```text
Configure production CORS allowlist using FRONTEND_ORIGIN. Do not use wildcard in production. Allow Swagger/dev origins only in dev profile. Add tests if practical. Update Build Log.
```

### 6.10 Final deployment smoke test
```text
Create final smoke test checklist: backend health, Swagger, auth register/login, course generation, course map, quiz, run code, submit code, AI review, leaderboard, frontend routes, CORS. Record results in Build Log.
```

## 7. README / Resume / Demo Prompts

### 7.1 README creation
```text
Create README with title, one-line value proposition, live links, screenshots, tech stack, architecture, MVP features, Phase 2 roadmap, setup, env vars, API docs, testing, limitations, resume bullets, and demo link. Do not exaggerate unbuilt features.
```

### 7.2 Architecture diagram
```text
Create a clear architecture diagram showing React frontend, Spring Boot modular monolith, PostgreSQL/Neon, Gemini API, Piston API, Vercel, Render, GitHub Actions, and optional Phase 2 services clearly marked as future.
```

### 7.3 API documentation section
```text
Create README/docs API section listing MVP endpoints, auth requirements, key request/response examples, ErrorDTO, Swagger link, and testing commands.
```

### 7.4 Screenshots checklist
```text
Create screenshot checklist: home, register/login, dashboard, course generation, course map, lesson, flashcards, quiz result, code editor, AI review, leaderboard, Swagger.
```

### 7.5 Demo video script
```text
Create 60-90 second demo script: intro, login, generate course, map, lesson, quiz, XP update, run code, submit, AI review, leaderboard, Swagger/GitHub close.
```

### 7.6 Resume bullet generation
```text
Generate honest MVP and advanced resume bullets. MVP bullet must include only built features. Advanced bullet must be marked usable only after Phase 2 features are actually implemented.
```

### 7.7 Interview explanation generation
```text
Generate interview Q&A for architecture, Spring Boot choices, JWT, database design, Gemini integration, Piston safety, XP anti-cheating, testing, deployment, and limitations.
```

### 7.8 Known limitations section
```text
Create known limitations and mitigations: Render cold starts, Gemini rate limits, Piston dependency, free-tier DB limits, AI malformed JSON, hidden test quality, no Phase 2 realtime yet.
```

## 8. Debugging, Review, and Refactor Prompts

### 8.1 Architecture compliance audit
```text
Audit the current CodeQuest codebase for architecture violations: business logic in controllers, repository calls in controllers, entity exposure, missing DTOs, validation gaps, missing GlobalExceptionHandler cases, duplicate services, random packages, endpoint mismatch, DB mismatch, and accidental Phase 2 features. Return Issue | Severity | File | Why wrong | Exact fix prompt.
```

### 8.2 Security review
```text
Review this feature for authentication, JWT-derived identity, ownership checks, sensitive fields, password/token logging, request validation, rate limiting, prompt injection if AI involved, and Piston safety if code execution involved. Return critical fixes first and a Codex patch prompt.
```

### 8.3 Test failure triage
```text
Analyze this failing test output. Return failed test, likely root cause, whether production or test code should change, minimal fix, Codex prompt, and command to rerun. Do not suggest broad rewrites.
```

## 9. Phase 2 Prompt Templates

Use these only after MVP is working, tested, deployed, and documented.

### 9.1 WebSocket leaderboard
```text
PHASE 2 ONLY. Implement real-time leaderboard with WebSocket/STOMP. Keep REST leaderboard fallback. Do not break existing endpoint. Add tests where practical and document fallback.
```

### 9.2 Study rooms
```text
PHASE 2 ONLY. Implement study rooms with REST create/list/join first, WebSocket chat only if explicitly requested, authentication and moderation rules, and no mixing with quiz/course logic.
```

### 9.3 Quiz duel
```text
PHASE 2 ONLY. Implement quiz duel invite flow with Flyway schema, create duel endpoint, scoring, duplicate XP prevention, and tests. Do not modify normal quiz submit behavior.
```

### 9.4 AI tutor
```text
PHASE 2 ONLY. Implement AI tutor chat through GeminiService only, with course/level context, rate limiting, no secrets/private data, fallback handling, and no direct XP/progress modification.
```

## 10. Feature Completion Checklist

- Endpoint matches CodeQuest_API_Contracts.
- Request DTO and response DTO are used.
- Validation annotations are added.
- Controller is thin.
- Service contains business logic and ownership checks.
- Repository is DB-only.
- No JPA entity is exposed directly.
- ErrorDTO is used for failure cases.
- GlobalExceptionHandler covers new exceptions.
- Flyway migration exists if DB changed.
- UUID primary keys are used for new tables.
- Tests are added and meaningful.
- Swagger/OpenAPI updated if endpoint changed.
- Manual test steps documented.
- Automated test commands documented.
- Build Log updated.
- No Phase 2/3 feature added accidentally.

## 11. Prompt Usage Checklist

- Did I choose exactly one feature?
- Did I include the exact endpoint?
- Did I include relevant tables?
- Did I include request/response DTOs?
- Did I include business rules?
- Did I include security and ownership rules?
- Did I tell Codex which files it may touch?
- Did I tell Codex which files it must not touch?
- Did I require validation, exceptions, and tests?
- Did I require files changed, manual tests, automated tests, assumptions, and Build Log update?
- Did I avoid Phase 2 prompts until MVP is complete?
- After Codex finishes, did I run tests and update the Build Log?

## 12. Final Audit

Before treating this prompt bank as final, confirm:

- Section 4 contains exactly 34 backend prompts.
- Standard ErrorDTO and GlobalExceptionHandler are split.
- JwtService and JwtFilter + SecurityConfig are split.
- Logout / token revoke is a separate prompt.
- Get course by ID, Public courses listing, and Enroll in course are split.
- AI code review and Explain runtime error are split.
- Frontend prompts are present.
- DevOps prompts are present.
- README/demo prompts are present.
- Debugging/review/refactor prompts are present.
- Phase 2 prompts are clearly blocked until MVP is complete.
- No generic prompt should be used without CodeQuest source docs.

**Backend prompt count confirmed:** 34 separate backend prompts.
