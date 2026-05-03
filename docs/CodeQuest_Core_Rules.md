**CODEQUEST CORE RULES**

*Always-Paste AI Execution Rulebook for ChatGPT/Codex*

Derived from CodeQuest AI-Control Master Blueprint v3 \| Java Developer Portfolio Project

| **Purpose: This document is the compact, strict, always-paste rulebook that prevents AI agents from hallucinating architecture, mixing layers, breaking database relationships, changing API contracts, or silently expanding scope. It must be provided to ChatGPT/Codex before every feature implementation.** |
|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|

# 0. How to Use This Core Rules Document

- Use this file as the always-paste context for every ChatGPT/Codex coding task.

- Pair it with the feature-specific context only when needed: exact endpoint, relevant DB tables, DTO fields, business rules, and expected tests.

- Keep the full master blueprint as the permanent reference, but use this compact file to avoid long-chat context loss.

- If any instruction in a chat conflicts with this document, this document wins unless the user explicitly approves a change.

## 0.1 External Tooling Note

- Codex should be guided with an AGENTS.md file at the repository root so project rules, commands, conventions, and MVP boundaries are consistently loaded.

- Spring backend changes must keep validation explicit and exception handling centralized through GlobalExceptionHandler using @ControllerAdvice or @RestControllerAdvice.

- Free-tier limits for hosting, AI, database, CI/CD, and APIs can change. Do not hard-code provider promises; verify current dashboard limits before deployment.

# 1. Non-Negotiable Hard Constraints

- Do NOT change the architecture. CodeQuest must remain a modular monolith unless the user explicitly requests an architecture migration.

- Do NOT introduce new frameworks, libraries, external APIs, paid tools, or infrastructure unless explicitly listed in the blueprint or explicitly approved by the user.

- Do NOT merge layers. Controller, Service, Repository, DTO, Entity, Mapper, Client, Config, and Exception classes must remain separate.

- Do NOT put business logic inside controllers.

- Do NOT put database queries or persistence logic inside controllers.

- Do NOT expose JPA entities directly in API responses.

- Do NOT skip validation, ownership checks, or error handling.

- Do NOT generate pseudo-code, TODO-only classes, placeholder methods, or incomplete implementations unless the user explicitly asks for a sketch.

- Do NOT rename database tables, columns, enums, DTOs, endpoints, modules, or packages unless explicitly instructed.

- Do NOT create duplicate logic across services. Reuse existing services and helpers.

- Do NOT ignore the schema, API contracts, folder structure, or business rules defined in the blueprint.

- Do NOT silently change MVP scope. Features outside MVP must be marked Phase 2 or Phase 3.

- Every feature must follow the defined folder structure exactly.

- If there is ambiguity, ask for clarification instead of guessing.

# 2. Project Identity and MVP Boundary

- Project name: CodeQuest.

- Purpose: A gamified AI learning platform for CS students and a Java/Spring Boot portfolio project.

- Tech stack: Java 21, Spring Boot, React, PostgreSQL, Gemini API, Piston API, Docker, GitHub Actions, Vercel, Render, Neon.

- Primary hiring signal: Java backend engineering depth, not only UI polish.

- MVP first. Do not implement Phase 2 or Phase 3 features unless explicitly requested.

## 2.1 MVP Features Allowed

- Auth: register, login, refresh, logout, JWT, BCrypt, protected routes.

- User profile and dashboard: XP, rank, streak, active courses, recently completed levels.

- AI course generation: Gemini API, strict JSON schema validation, persistence, cache check.

- Course map: locked/unlocked levels, boss levels, progress-based navigation.

- Lesson page: Markdown content, flashcards, notes, quiz tab, coding challenge tab.

- Quiz engine: MCQ grading, score, weak concepts, XP award.

- Progress tracking per user and level.

- Monaco code editor with language selector and starter code.

- Piston API run/submit flow with visible tests and result comparison.

- AI code review: time complexity, space complexity, improvements, better approach.

- Leaderboard via REST first.

- Swagger/OpenAPI docs, Dockerized backend, GitHub Actions CI, deployment, README screenshots.

## 2.2 Phase 2/3 Features - Block Unless Explicitly Requested

- WebSocket real-time leaderboard.

- Study rooms and chat.

- Quiz duels.

- Email streak reminders.

- Advanced AI tutor chat.

- React Flow concept graph.

- Redis caching beyond MVP necessity.

- Admin dashboard.

- Microservices, Kafka/RabbitMQ, read replicas, analytics, self-hosted code execution.

# 3. Architecture Rules

- Architecture pattern: modular monolith.

- Each backend feature module must contain its own Controller, Service, Repository, DTOs, Entity where needed, Mapper where needed, and tests.

- Frontend communicates with Spring Boot through HTTP APIs using JWT authentication.

- External API integrations must be isolated behind dedicated service/client classes.

- Gemini calls must go through GeminiService and never directly from controllers.

- Piston calls must go through Piston/Problem service classes and never directly from controllers.

- GlobalExceptionHandler must return standard error DTOs for failure cases.

- User identity for protected actions must come from JWT/SecurityContext, not from frontend-provided userId.

# 4. File Creation and Modification Rules

- Only create files inside predefined module structure: auth, user, course, level, quiz, problem, progress, ai, leaderboard, notification, and common.

- Do NOT create new top-level packages such as helper, random, manager, util2, temp, or experimental.

- Do NOT duplicate DTOs, entities, repositories, or services that already exist.

- Before creating a new class, check whether similar logic already exists and extend/reuse that class when appropriate.

- Controllers belong inside the relevant module package, not inside common.

- Shared exceptions, shared DTOs, base response objects, security config, and global utilities belong inside common.

- External API integrations go inside client/service classes, never directly inside controllers.

- Test files must mirror the production package structure under src/test/java.

- Every generated file must have a clear responsibility explainable in one sentence.

# 5. Backend Implementation Template

| **Layer**              | **Strict Responsibility**                                                                                                                                 |
|------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Controller**         | Handles HTTP request/response only. Calls the service. No business logic. No repository calls. No AI/Piston calls directly.                               |
| **Service**            | Contains business logic, validation orchestration, ownership checks, transaction boundaries, XP rules, progress rules, and calls to repositories/clients. |
| **Repository**         | Only database interaction using Spring Data JPA. No business logic. No HTTP logic. No DTO mapping beyond projections when needed.                         |
| **DTOs**               | Separate request and response DTOs. Use validation annotations on request DTOs. Never expose entities directly.                                           |
| **Entity**             | Maps directly to the database schema. Contains persistence fields and relationships only; avoid heavy business logic inside entities.                     |
| **Mapper**             | Converts Entity to Response DTO and Request DTO to Entity when needed. Manual mapper or MapStruct is allowed if already chosen.                           |
| **Exception Handling** | Use custom domain exceptions and handle them in GlobalExceptionHandler using consistent error DTOs.                                                       |
| **Tests**              | Add at least one meaningful test for backend logic; two or more tests are preferred for service behavior and failure cases.                               |

# 6. Approved Backend Module Map

| **Module**       | **Key Classes**                                               | **Responsibility**                                                    |
|------------------|---------------------------------------------------------------|-----------------------------------------------------------------------|
| **auth**         | AuthController, AuthService, JwtService, RefreshTokenService  | Register, login, refresh, logout, password hashing, token generation. |
| **user**         | UserController, UserService, UserRepository                   | Profile, avatar, goal, XP, rank, streak, achievements.                |
| **course**       | CourseController, CourseService, CourseRepository             | Course creation, AI generation, public discovery, enrollment.         |
| **level**        | LevelController, LevelService, LevelRepository                | Level retrieval, unlock logic, boss-level rules.                      |
| **quiz**         | QuizController, QuizService, QuizRepository                   | Question retrieval, grading, weak-concept extraction.                 |
| **problem**      | ProblemController, ProblemService, CodeSubmissionService      | Coding problems, run code, submit solution, test comparison.          |
| **ai**           | GeminiService, PromptBuilder, ResponseParser, AiSafetyService | Centralized AI calls, prompt construction, JSON parsing, retries.     |
| **progress**     | ProgressController, ProgressService, XPService, StreakService | Level completion, XP awards, rank updates, streak logic.              |
| **leaderboard**  | LeaderboardController, LeaderboardService                     | Top users by XP, weekly leaderboard, phase-2 WebSocket push.          |
| **notification** | EmailService, StreakScheduler                                 | Phase 2: reminders, welcome emails, challenge alerts.                 |

# 7. Database Rules

- All schema changes must be done through Flyway migration scripts.

- Do NOT modify existing tables without explicit instruction.

- Do NOT remove constraints, indexes, foreign keys, unique constraints, or ownership-related relationships.

- All primary keys must be UUID unless an existing table already uses a different approved key.

- Every table should include created_at and updated_at where appropriate.

- Never store plaintext passwords, raw refresh tokens, API keys, or secrets in the database.

- If adding user-owned data, include user_id and enforce ownership checks in the service layer.

- Do not store AI-generated output without validating schema and maximum length limits.

- If changing a DTO because of a DB change, update API examples and tests too.

## 7.1 Approved Core Tables

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

## 7.2 Required Constraints and Indexes

- users.email must be UNIQUE.

- progress must have UNIQUE(user_id, level_id).

- Add users(xp DESC) index for leaderboard.

- Add courses(normalized_topic) index for course lookup/cache.

- Add levels(course_id, order_number) index for ordered level map.

- Add progress(user_id) and progress(user_id, level_id) indexes.

- Add code_submissions(user_id, problem_id) index.

- XP is awarded only once for first completion/first accepted solution.

# 8. API Contract Rules

- API endpoints must match the blueprint exactly unless explicitly changed by the user.

- Request and response structures must not change silently.

- GET is for read-only requests without request bodies.

- POST is for creation, actions, generation, submission, and code execution.

- PATCH is for partial updates; DELETE is for deletion.

- Always validate request bodies using Jakarta Bean Validation annotations.

- Return proper HTTP status codes: 200/201 success, 400 validation, 401 unauthenticated, 403 forbidden, 404 missing resource, 409 conflict, 429 rate limit, 500/502/503 server or external failures.

- All error responses must use the standard ErrorDTO structure.

- Protected endpoints must derive user identity from JWT, not userId sent by frontend.

- Do NOT accept userId in request body for actions that apply to logged-in user.

- All list endpoints support pagination with max page size 50.

- Swagger/OpenAPI annotations must be updated for new endpoints.

## 8.1 MVP Endpoint Boundary

- POST /api/auth/register

- POST /api/auth/login

- POST /api/auth/refresh

- POST /api/auth/logout

- GET /api/user/profile

- PATCH /api/user/profile

- POST /api/courses/generate

- GET /api/courses/{courseId}

- GET /api/courses/public

- POST /api/courses/{courseId}/enroll

- GET /api/levels/{levelId}

- POST /api/levels/{levelId}/complete

- POST /api/quizzes/{levelId}/submit

- POST /api/problems/{problemId}/run

- POST /api/problems/{problemId}/submit

- POST /api/ai/review-code

- POST /api/ai/explain-error

- POST /api/notes

- GET /api/leaderboard

- GET /api/daily-challenge

## 8.2 Phase 2 Endpoints - Block Unless Requested

- WS /ws/leaderboard

- WS /ws/study-room/{id}

- POST /api/duels/create

# 9. Security, Validation, and Abuse Prevention Rules

- Spring Security + JWT + BCrypt is the required security model.

- Access tokens should be short-lived; refresh tokens should be rotated and stored safely.

- Password must be hashed with BCrypt; never return password_hash.

- Use service-layer ownership checks on every user-owned resource.

- Prevent SQL injection by using Spring Data JPA parameterized queries and avoiding string concatenation.

- Prevent XSS by sanitizing markdown/notes with DOMPurify on frontend rendering.

- Do not send secrets, JWTs, passwords, API keys, or private user data to Gemini.

- Rate-limit login, course generation, and code execution requests.

- Never execute user code in your backend; delegate to Piston or an isolated external runner.

## 9.1 Required Validation Rules

- Name: 2-100 characters.

- Email: valid format and unique.

- Password: at least 8 characters with letters and numbers.

- Topic: 2-80 characters; reject abusive or command-like prompt injection text.

- Difficulty: enum only.

- Code length: max 20,000 characters for MVP.

- Language: allowlist only, such as java, python, javascript, cpp.

- Quiz answers: selected option must be A/B/C/D.

- Notes: max length and sanitized output.

- Avatar: image only, max 2 MB.

# 10. AI / Gemini Rules

- All Gemini calls go through GeminiService only.

- PromptBuilder creates structured prompts; controllers never build prompts directly.

- ResponseParser validates JSON and throws GeminiParseException on malformed output.

- AI responses are cached in PostgreSQL by normalized topic and difficulty.

- Never trust AI output blindly; validate schema, required fields, enum values, and max lengths.

- Use retries only for transient errors or malformed JSON; never infinite retry.

- AI course generation must return JSON only, no markdown fences.

- Code review AI must return practical, kind, specific feedback in structured JSON.

# 11. Gamification Rules

- Complete lesson: 50 XP, awarded once per level.

- Correct quiz answer: 20 XP, awarded during first quiz completion only.

- Perfect quiz: bonus 50 XP if all answers are correct on first attempt.

- Solve coding problem: 100 XP, awarded on first accepted submission only.

- Beat boss level: 200 XP; requires all previous levels completed.

- Daily login: 30 XP once per calendar day, not on every JWT validation.

- Daily challenge: 150 XP once per day.

- Win quiz duel: 75 XP, Phase 2 only.

- Do not award XP twice for the same completed level or same accepted problem.

- Store submission history and use hidden tests for coding problems.

## 11.1 Rank Thresholds

| **Rank**      | **XP Required** | **Badge Behavior** |
|---------------|-----------------|--------------------|
| **Beginner**  | 0               | Gray badge         |
| **Coder**     | 500             | Blue badge         |
| **Developer** | 2,000           | Green badge        |
| **Engineer**  | 5,000           | Purple badge       |
| **Architect** | 12,000          | Gold badge         |
| **Legend**    | 25,000          | Animated badge     |

# 12. Error Handling and Edge-Case Rules

- Gemini timeout/5xx: retry once; return cached course if available; otherwise structured error.

- Gemini malformed JSON: retry with stricter prompt; after 2 failures throw GeminiParseException.

- Gemini rate limit: use DB cache; queue request or ask user to retry after timestamp.

- Piston down: return CODE_RUNNER_UNAVAILABLE and keep lessons/quizzes working.

- DB down: return 503 with Retry-After.

- JWT expired: frontend interceptor calls refresh endpoint; retry original request or redirect to login.

- Duplicate completion: do not award XP again.

- Refresh mid-quiz: save quiz draft to sessionStorage and resume.

- WebSocket drop: reconnect or fallback to REST polling if Phase 2 implemented.

- Free-tier cold start: frontend warm-up call and clear loading UI.

## 12.1 Standard ErrorDTO Shape

> {  
> "timestamp": "2026-05-02T10:00:00Z",  
> "status": 429,  
> "code": "RATE_LIMITED",  
> "message": "You have reached the course generation limit. Try again later.",  
> "path": "/api/courses/generate",  
> "requestId": "req_abc123"  
> }

# 13. Testing Rules

- Every backend feature with business logic must include at least one meaningful automated test.

- Preferred backend tests: JUnit 5 + Mockito for unit tests, @DataJpaTest for repository tests, SpringBootTest + Testcontainers for integration tests.

- API tests should cover 401, 400, 404, and success cases.

- Frontend tests should cover critical interactions: auth forms, quiz behavior, XP bar, flashcards.

- AI must not claim tests passed unless the commands were actually run by the user or tool.

## 13.1 Must-Have Backend Tests

- Register hashes password and never returns password_hash.

- Login rejects invalid password.

- JWT-protected endpoint returns 401 without token.

- Course generation parser rejects malformed JSON.

- XP updates rank when crossing threshold.

- XP is not awarded twice for completed level.

- Locked level cannot be completed.

- Quiz submission calculates score correctly.

- Accepted code submission awards XP once.

- Leaderboard returns users sorted by XP descending.

# 14. Repository Structure and Commands

> codequest/  
> frontend/  
> src/pages/  
> src/components/  
> src/hooks/  
> src/services/  
> src/store/  
> src/utils/  
> src/constants/  
> backend/  
> src/main/java/com/codequest/  
> auth/  
> user/  
> course/  
> level/  
> quiz/  
> problem/  
> progress/  
> ai/  
> leaderboard/  
> notification/  
> common/config/  
> common/exception/  
> common/security/  
> common/dto/  
> src/test/java/com/codequest/  
> database/migrations/  
> docs/architecture/  
> docs/api/  
> docs/screenshots/  
> .github/workflows/  
> AGENTS.md  
> README.md  
> docker-compose.yml

- Backend tests: cd backend && mvn test

- Frontend tests: cd frontend && npm test -- --run

- Frontend build: cd frontend && npm run build

- Backend must expose health endpoint: /actuator/health.

- Swagger UI must be available for API documentation.

# 15. Required AI Output Format for Code Tasks

- List all files created and modified.

- Provide complete code, not partial snippets, unless explicitly asked for a snippet.

- Ensure imports are correct and compile-ready.

- Match naming conventions from the blueprint and codebase.

- Include validation annotations where required.

- Include ownership checks when touching user-owned data.

- Include at least one automated test when backend business logic is implemented.

- Include manual test steps and exact commands after implementation.

- Explicitly state assumptions instead of inventing behavior.

- Never say a feature is complete unless the code compiles and described tests pass.

## 15.1 Required Response Template

> Files changed:  
> 1. backend/src/main/java/com/codequest/.../FileName.java - purpose  
> 2. backend/src/test/java/com/codequest/.../FileNameTest.java - purpose  
>   
> Implementation summary:  
> - What was added  
> - What business rule was implemented  
> - What validation/error handling was included  
>   
> How to test manually:  
> 1. Command or API call  
> 2. Expected response  
>   
> Automated tests:  
> - mvn test  
> - specific test class if applicable  
>   
> Assumptions:  
> - Any assumption made, or "None"

# 16. Anti-Hallucination Rules

- Do NOT invent features not present in the blueprint.

- Do NOT add unnecessary fields to DTOs, entities, or database tables.

- Do NOT change business logic without instruction.

- Do NOT introduce new external services or APIs.

- Do NOT create fake test results or claim commands were run when they were not run.

- Do NOT ignore compilation errors or failing tests.

- Do NOT generate UI routes, backend endpoints, or DB tables outside MVP unless explicitly asked for Phase 2/3.

- If unsure, ask for clarification instead of guessing.

- If a feature is large, break it into safe implementation steps rather than modifying many unrelated modules at once.

# 17. Context Splitting Protocol

- Always paste this Core Rules document.

- Also paste current folder structure if the repo has already been created.

- Also paste the relevant table schema for the feature.

- Also paste the exact endpoint and request/response DTO for the feature.

- Also paste validation, security, and error response rules for the feature.

- Only paste feature-specific module details when needed to avoid long-context loss.

# 18. Feature Prompt Template

> You are implementing one feature in CodeQuest.  
> Follow the master blueprint, CodeQuest_Core_Rules, and AGENTS.md strictly.  
>   
> Feature name:  
> \[FEATURE_NAME\]  
>   
> Module:  
> \[auth/course/level/quiz/problem/progress/ai/leaderboard/user\]  
>   
> Exact endpoint:  
> \[METHOD\] /api/...  
>   
> Relevant database tables:  
> \[table names\]  
>   
> Request DTO:  
> \[fields + validation\]  
>   
> Response DTO:  
> \[fields\]  
>   
> Business rules:  
> \[rules from blueprint\]  
>   
> Security rules:  
> \[JWT/ownership/role/rate-limit rules\]  
>   
> Implementation requirements:  
> 1. Keep Controller, Service, Repository, DTO, Entity, Mapper separate.  
> 2. Do not expose entities directly.  
> 3. Add validation and custom exceptions.  
> 4. Update GlobalExceptionHandler if needed.  
> 5. Add at least one test.  
> 6. Do not modify unrelated files.  
> 7. After coding, list files changed, test commands, and assumptions.

# 19. Root AGENTS.md Content

> \# AGENTS.md - CodeQuest AI Coding Rules  
>   
> \## Project Identity  
> CodeQuest is a Java 21 + Spring Boot + React + PostgreSQL portfolio project.  
> It must remain a modular monolith for MVP.  
>   
> \## Non-Negotiable Rules  
> - Follow the master blueprint before making changes.  
> - Do not introduce new frameworks or external services without approval.  
> - Keep Controller -\> Service -\> Repository separation.  
> - Do not put business logic in controllers.  
> - Do not expose JPA entities directly in API responses.  
> - Use DTOs for all API requests and responses.  
> - Use validation annotations on request DTOs.  
> - Use GlobalExceptionHandler for errors.  
> - Use Flyway migrations for schema changes.  
> - Use UUID primary keys.  
> - Never store secrets in code.  
> - Never execute user code in the backend; use Piston integration only.  
> - Do not implement Phase 2 features unless explicitly requested.  
>   
> \## Required Output After Any Code Change  
> 1. Files changed  
> 2. Implementation summary  
> 3. Manual test steps  
> 4. Automated tests to run  
> 5. Assumptions  
>   
> \## Commands  
> Backend tests: cd backend && mvn test  
> Frontend tests: cd frontend && npm test -- --run  
> Frontend build: cd frontend && npm run build  
>   
> \## MVP Boundary  
> Build MVP first: auth, profile, AI course generation, course map, lesson, quiz, progress, XP/rank, Monaco editor, Piston execution, AI code review, leaderboard, Swagger, Docker, CI, deployment.

# 20. Feature Completion Checklist

- Endpoint matches the blueprint exactly.

- Request DTO validates all required fields.

- Controller has no business logic.

- Service contains business logic and ownership checks.

- Repository has only database logic.

- Response DTO does not expose entity internals.

- GlobalExceptionHandler returns standard ErrorDTO for failure cases.

- Database changes, if any, are in Flyway migration files.

- At least one meaningful test exists.

- Swagger/OpenAPI docs updated.

- Manual test steps are documented.

- No Phase 2/3 feature was accidentally added.

- Build log and README are updated when relevant.

# 21. Source Notes

- This document was derived from CodeQuest_AI_Control_Master_Blueprint_v3 and preserves the core AI execution, architecture, database, API, security, testing, and Codex rules from that source.

- The full master blueprint remains the complete reference for product scope, detailed implementation roadmap, resume/interview positioning, deployment notes, and long-form explanations.

- Use this Core Rules document as the compact, always-paste guardrail. Use the full master blueprint when details are needed.
