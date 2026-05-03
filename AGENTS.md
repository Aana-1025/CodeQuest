# AGENTS.md - CodeQuest AI Coding Rules

## Project Identity
CodeQuest is a Java 21 + Spring Boot + React + PostgreSQL portfolio project.
It must remain a modular monolith for MVP.

## Source of Truth
Before making any code change, follow these project resources in this priority order:
1. CodeQuest_AI_Control_Master_Blueprint_v3.docx
2. CodeQuest_Core_Rules.md
3. CodeQuest_DB_Schema.md
4. CodeQuest_API_Contracts.md
5. CodeQuest_Feature_Prompts.md
6. CodeQuest_Build_Log.md
7. This AGENTS.md file

If any instruction conflicts with the master blueprint or core rules, the blueprint/core rules take priority. If ambiguity remains, ask for clarification instead of guessing.

## Non-Negotiable Rules
- Follow the master blueprint before making changes.
- Do not change the architecture. CodeQuest must remain a modular monolith for MVP.
- Do not introduce new frameworks, libraries, external services, paid tools, or infrastructure without explicit approval.
- Keep Controller -> Service -> Repository separation.
- Do not put business logic in controllers.
- Do not put database queries or persistence logic in controllers.
- Do not expose JPA entities directly in API responses.
- Use DTOs for all API requests and responses.
- Use validation annotations on request DTOs.
- Use custom exceptions where needed.
- Use GlobalExceptionHandler for consistent errors.
- Use Flyway migrations for schema changes.
- Use UUID primary keys.
- Never store secrets in code.
- Never store plaintext passwords, raw refresh tokens, API keys, or secrets in the database.
- Never execute user code in the backend; use Piston integration only.
- Do not implement Phase 2 or Phase 3 features unless explicitly requested.
- Do not generate pseudo-code, TODO-only classes, placeholder methods, or incomplete implementations unless explicitly requested.
- Do not silently rename database tables, columns, DTOs, endpoints, modules, packages, or enums.
- Do not create duplicate logic across services. Reuse existing services/helpers when available.

## Repository Layout
Use this layout unless the user explicitly changes it:

```text
codequest/
  AGENTS.md
  README.md
  docker-compose.yml
  docs/
    architecture/
    api/
    screenshots/
  database/
    migrations/
  backend/
    src/main/java/com/codequest/
      auth/
      user/
      course/
      level/
      quiz/
      problem/
      progress/
      ai/
      leaderboard/
      notification/
      common/
        config/
        dto/
        exception/
        security/
    src/test/java/com/codequest/
  frontend/
    src/
      pages/
      components/
      hooks/
      services/
      store/
      utils/
      constants/
  .github/workflows/
```

## File Creation Rules
- Only create files inside the predefined module structure: auth, user, course, level, quiz, problem, progress, ai, leaderboard, notification, and common.
- Do not create random top-level packages such as helper, manager, util2, temp, experimental, or random.
- Do not duplicate DTOs, entities, repositories, services, or mappers that already exist.
- Before creating a new class, check whether similar logic already exists and reuse/extend it when appropriate.
- Controllers belong inside the relevant module package, not inside common.
- Shared exceptions, shared DTOs, base response objects, security config, and global utilities belong inside common.
- External API integrations must go inside client/service classes, never directly inside controllers.
- Test files must mirror the production package structure under src/test/java.
- Every generated file must have one clear responsibility and be explainable in one sentence.

## Backend Layering Rules
Every backend feature must follow this structure:

### Controller
- Handles HTTP request/response only.
- Calls the service layer.
- Contains no business logic.
- Contains no repository calls.
- Contains no direct Gemini/Piston/external API calls.

### Service
- Contains business logic.
- Handles validation orchestration, ownership checks, transaction boundaries, XP rules, progress rules, and calls to repositories/clients.
- Uses @Transactional when multiple database updates must be atomic.

### Repository
- Handles database interaction only using Spring Data JPA.
- Contains no business logic.
- Contains no HTTP logic.
- Does not perform DTO mapping except projections when explicitly needed.

### DTOs
- Use separate request and response DTOs.
- Add Jakarta Bean Validation annotations on request DTOs.
- Never expose JPA entities directly.

### Entity
- Maps directly to the database schema.
- Contains persistence fields and relationships.
- Avoid heavy business logic inside entities.

### Mapper
- Converts Entity to Response DTO and Request DTO to Entity when needed.
- Manual mapper is allowed. MapStruct is allowed only if already chosen in the project.

### Exception Handling
- Use custom domain exceptions for domain failures.
- Handle exceptions in GlobalExceptionHandler.
- Return the standard ErrorDTO structure.

## Database Rules
- All schema changes must be done through Flyway migration scripts.
- Do not modify existing tables without explicit instruction.
- Do not remove constraints, indexes, foreign keys, unique constraints, or ownership-related relationships.
- All primary keys must be UUID unless an existing table already uses a different approved key.
- Every table should include created_at and updated_at where appropriate.
- If adding user-owned data, include user_id and enforce ownership checks in the service layer.
- Do not store AI-generated output without validating schema and maximum length limits.
- If changing a DTO because of a DB change, update API examples and tests too.

Core tables:
- users
- refresh_tokens
- courses
- levels
- quizzes
- flashcards
- coding_problems
- progress
- code_submissions
- notes
- achievements
- daily_challenges

Important constraints and indexes:
- users.email must be UNIQUE.
- progress must have UNIQUE(user_id, level_id).
- users.xp should be indexed for leaderboard sorting.
- courses.normalized_topic should be indexed.
- levels(course_id, order_number) should be indexed.
- progress(user_id) and progress(user_id, level_id) should be indexed.
- code_submissions(user_id, problem_id) should be indexed.

## API Contract Rules
- API endpoints must match CodeQuest_API_Contracts.md exactly unless explicitly changed by the user.
- Request and response structures must not change silently.
- Use GET only for read-only requests with no request body.
- Use POST for creation, action, generation, submission, and code execution.
- Use PATCH for partial updates.
- Use DELETE for deletion.
- Always validate request body using Jakarta Bean Validation annotations.
- Protected endpoints must derive user identity from JWT, not from userId sent by the frontend.
- Do not accept userId in request body for actions that apply to the logged-in user.
- All list endpoints must support pagination with maximum page size 50.
- Swagger/OpenAPI annotations must be updated for new endpoints.

HTTP status rules:
- 200/201 for success.
- 400 for validation errors.
- 401 for unauthenticated requests.
- 403 for forbidden access.
- 404 for missing resources.
- 409 for conflicts.
- 429 for rate limits.
- 500/502/503 for server or external-service failures.

Standard ErrorDTO shape:

```json
{
  "timestamp": "2026-05-02T10:00:00Z",
  "status": 429,
  "code": "RATE_LIMITED",
  "message": "You have reached the course generation limit. Try again later.",
  "path": "/api/courses/generate",
  "requestId": "req_abc123"
}
```

## Security and Validation Rules
- Passwords must be BCrypt hashed and never returned in API responses.
- JWT access tokens must be short-lived.
- Refresh tokens must be rotated/revoked safely.
- Ownership checks are required for all user-owned data.
- Do not log passwords, JWT tokens, refresh tokens, API keys, or private user data.
- Sanitize markdown/notes before rendering to prevent XSS.
- Rate-limit login, AI generation, and code execution endpoints.
- Reject abusive prompt-injection-like topic inputs.
- Code length must be capped for MVP.
- Language values for code execution must come from an allowlist.

Validation rules:
- Name: 2-100 characters.
- Email: valid format and unique.
- Password: at least 8 characters with letters and numbers.
- Topic: 2-80 characters.
- Difficulty: enum only.
- Code length: maximum 20,000 characters for MVP.
- Quiz answer: A/B/C/D only.
- Avatar: image only, maximum 2 MB.

## AI, Gemini, and Piston Rules
- All Gemini calls must go through GeminiService only.
- Controllers must never build prompts directly.
- PromptBuilder creates structured prompts.
- ResponseParser validates JSON and throws GeminiParseException for malformed output.
- Never trust AI output blindly; validate schema, required fields, enum values, and max lengths.
- Do not send secrets, JWTs, passwords, or private user data to Gemini.
- Use retries only for transient errors or malformed JSON; never infinite retry.
- Cache AI-generated course data in PostgreSQL by normalized topic and difficulty.
- Backend must never execute user code locally.
- Code execution must use Piston integration or an approved isolated external runner.
- If Piston is unavailable, return a graceful CODE_RUNNER_UNAVAILABLE response and keep lessons/quizzes usable.

## Gamification and Anti-Cheating Rules
- Award lesson XP once per level.
- Award quiz XP only during first quiz completion.
- Award coding XP only on first accepted submission for a problem.
- Do not award XP twice for duplicate completion.
- Store submission history in code_submissions.
- Use hidden tests for coding problems.
- Boss levels require previous levels to be completed.
- Daily login XP is awarded once per calendar day, not on every JWT validation.

## MVP Boundary
Build MVP first:
- auth
- profile
- AI course generation
- course map
- lesson
- quiz
- progress
- XP/rank
- Monaco editor
- Piston execution
- AI code review
- leaderboard
- Swagger
- Docker
- CI
- deployment
- README/demo polish

Phase 2 features must not be implemented unless explicitly requested:
- WebSocket leaderboard
- study rooms
- quiz duels
- email reminders
- advanced AI tutor
- concept graph
- Redis cache
- admin dashboard

Phase 3 features must not be implemented unless explicitly requested:
- microservices
- Kafka/RabbitMQ
- read replicas
- advanced analytics
- self-hosted code runner

## Required Output After Any Code Change
After any code change, respond in this exact format:

```text
Files changed:
1. path/to/FileName.java - purpose
2. path/to/FileNameTest.java - purpose

Implementation summary:
- What was added
- What business rule was implemented
- What validation/error handling was included

How to test manually:
1. Command/API call
2. Expected response/result

Automated tests:
- Exact command(s) to run
- Specific test class if applicable

Assumptions:
- Any assumption made, or "None"
```

## Commands
Backend tests:

Mac/Linux:
```bash
cd backend && ./mvnw test
```

Windows:
```powershell
cd backend && .\mvnw.cmd test
```

Frontend tests:

```bash
cd frontend && npm test -- --run
```

Frontend build:

```bash
cd frontend && npm run build
```

Backend run command, if needed:

Mac/Linux:
```bash
cd backend && ./mvnw spring-boot:run
```

Windows:
```powershell
cd backend && .\mvnw.cmd spring-boot:run
```

Frontend dev command, if needed:

```bash
cd frontend && npm run dev
```

## Definition of Done
A feature is not done until:
- Endpoint matches the blueprint/API contract.
- Request DTO validates all required fields.
- Controller has no business logic.
- Service contains business logic and ownership checks.
- Repository contains only database access.
- Response DTO does not expose entity internals.
- GlobalExceptionHandler returns standard ErrorDTO for failure cases.
- Database changes, if any, are in Flyway migration files.
- At least one meaningful automated test exists for backend logic.
- Swagger/OpenAPI docs are updated for new endpoints.
- Manual test steps are documented.
- No unrelated files were modified.
- No Phase 2/3 feature was accidentally added.
- Tests/build commands are provided and, if run, results are reported honestly.

## Anti-Hallucination Rules
- Do not invent features not present in the blueprint.
- Do not add unnecessary fields to DTOs, entities, or database tables.
- Do not change business logic without instruction.
- Do not introduce new external services or APIs.
- Do not create fake test results.
- Do not claim commands were run if they were not run.
- Do not ignore compilation errors or failing tests.
- If unsure, ask for clarification instead of guessing.
- If the user asks for a large feature, break it into safe implementation steps rather than modifying many unrelated modules at once.
