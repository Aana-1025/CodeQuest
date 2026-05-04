# CodeQuest Build Log

## Purpose
This file solves the long-chat slowdown problem. Update it manually after every feature so a fresh ChatGPT/Codex chat can continue from the current state without needing the full conversation history.

## Current Status
Phase: MVP
Current module: Auth
Current feature: Logout / token revoke
Last completed feature: Refresh token
Next feature: Logout / token revoke
Current branch: main
Latest commit: 26fc7c5 feat: add refresh token
Test status: Backend Maven Wrapper test PASS for Refresh token (37 tests); Git status clean after commit and push; Frontend build not required for backend-only task

## Completed Features
- [x] Project setup
- [x] Backend health endpoint
- [x] Database connection
- [x] Flyway migrations
- [x] Swagger/OpenAPI setup
- [x] Global ErrorDTO + GlobalExceptionHandler
- [x] Auth register
- [x] Auth login
- [x] JWT filter
- [x] Refresh token
- [ ] Logout / token revoke
- [ ] User profile
- [ ] Frontend auth pages
- [ ] Protected routes
- [ ] Dashboard shell
- [ ] Course generation
- [ ] GeminiService + PromptBuilder
- [ ] ResponseParser + AI validation
- [ ] Course map
- [ ] Level unlock logic
- [ ] Lesson page
- [ ] Flashcards
- [ ] Notes
- [ ] Quiz submit
- [ ] Weak concept detection
- [ ] XP/rank system
- [ ] Streak system
- [ ] Piston run code
- [ ] Code submit
- [ ] Code submissions history
- [ ] AI code review
- [ ] Leaderboard
- [ ] Docker
- [ ] CI/CD
- [ ] Deployment
- [ ] README
- [ ] Screenshots
- [ ] Demo video
- [ ] Resume bullets updated

## Important Decisions
- Architecture: modular monolith.
- Backend: Java 21 + Spring Boot.
- Database: PostgreSQL + Flyway.
- Frontend: React + Vite + Tailwind.
- Security: Spring Security + JWT + BCrypt.
- Code execution: Piston API only. Never execute user code inside the backend.
- AI: Gemini API through GeminiService only.
- Deployment target: Vercel frontend, Render backend, Neon PostgreSQL, GitHub Actions CI.
- MVP first, no Phase 2 features yet.
- Source-of-truth priority: CodeQuest_AI_Control_Master_Blueprint_v3, CodeQuest_Core_Rules, CodeQuest_DB_Schema, CodeQuest_API_Contracts, CodeQuest_Feature_Prompts, CodeQuest_Build_Log, then AGENTS.md.
- For Codex specifically, use available repo files: AGENTS.md, docs/CodeQuest_Core_Rules.md, docs/CodeQuest_DB_Schema.md, docs/CodeQuest_API_Contracts.md, docs/CodeQuest_Feature_Prompts.md, and CodeQuest_Build_Log.md. The master .docx files live in ChatGPT Project resources and are not stored in the repo.
- Use Maven Wrapper only for backend tests on Windows:
  - `cd backend`
  - `.\mvnw.cmd test`
- After every Codex implementation, do not commit immediately. First verify:
  1. `git status`
  2. automated tests
  3. manual/API smoke test for the implemented feature
  4. Build Log update
  5. commit only after verification passes
- Do not start the next feature while the current feature has uncommitted changes.
- For backend-only tasks, frontend build is not required unless the backend change affects frontend integration or shared API contract behavior.

## Current Source of Truth Files
- CodeQuest_AI_Control_Master_Blueprint_v3.docx: full master blueprint in ChatGPT Project resources.
- CodeQuest_Core_Rules.docx / .md: always-paste AI-control rules.
- CodeQuest_DB_Schema.docx / .md: database rules and schema.
- CodeQuest_API_Contracts.docx / .md: endpoint contracts and examples.
- CodeQuest_Feature_Prompts.docx / .md: prompt bank for Codex tasks.
- CodeQuest_Build_Log.docx / .md: current progress and next task memory.
- AGENTS.md: repo-root AI instructions for Codex.

## Bugs / Issues
- None currently.
- Note from Global ErrorDTO task: Codex initially looped and produced a broken test. The test was manually corrected to a stable standalone MockMvcBuilders test. Final backend test passed before commit.
- Auth register note: Codex changed `backend/src/test/resources/application.yml` to use `ddl-auto: create` for test schema generation only. Production `ddl-auto` remains `none`, and production schema remains Flyway-controlled.
- Auth register note: Codex added `spring-security-crypto` only for BCrypt password hashing. Full Spring Security filter chain, JWT filter, login, refresh token, and logout were intentionally unimplemented at that stage.
- Auth register note: The register response intentionally does not return JWT/accessToken because JwtService was not implemented at that stage and was explicitly out of scope for that task.
- Auth register note: The response must not expose `passwordHash`, `password_hash`, role, JWT, refresh token, or any sensitive field.
- JWT authentication note: Login response now returns accessToken, tokenType, and expiresInSeconds.
- JWT authentication note: JWT secret and access token expiry are configuration-driven through application.yml/test application.yml with safe local/test defaults only. Real production secrets must come from environment variables.
- Refresh token note: Login response now returns refreshToken in addition to accessToken.
- Refresh token note: Refresh tokens are opaque tokens, stored only as hashes in the database, and exposed only as raw token values in the login response.
- Refresh token note: POST `/api/auth/refresh` returns a new accessToken, tokenType, and expiresInSeconds. It does not return a new refreshToken because token rotation was not implemented in this task.
- Refresh token note: V2 Flyway migration creates `refresh_tokens` table. Existing migrations were not edited.
- Refresh token note: Logout, token revoke endpoint, token rotation, frontend auth, protected routes UI, and dashboard are still intentionally unimplemented.

## Feature History
| # | Date | Feature | Module | Files changed | Tests | Commit/Notes |
|---|---|---|---|---|---|---|
| 1 | 2026-05-03 | Project setup | Foundation | Root skeleton, backend Spring Boot skeleton, frontend Vite React Tailwind skeleton, docs cleanup, Maven Wrapper | Backend `cd backend && .\mvnw.cmd test` PASS; Frontend `cd frontend && npm run build` PASS | `30c371d chore: initialize CodeQuest project skeleton` |
| 2 | 2026-05-03 | Backend health endpoint | Foundation/common | HealthController, HealthControllerTest, spring-boot-starter-web dependency | Backend `cd backend && .\mvnw.cmd test` PASS | `751dc6d feat: add backend health endpoint` |
| 3 | 2026-05-03 | Database connection | Foundation/database | backend pom/config test profile updates | Backend `cd backend && .\mvnw.cmd test` PASS | `5b5bb4a feat: configure database connection` |
| 4 | 2026-05-03 | Flyway migrations | Foundation/database | Flyway dependency/config + V1 users table migration | Backend `cd backend && .\mvnw.cmd test` PASS | `6cf8e06 feat: add Flyway users migration` |
| 5 | 2026-05-03 | Swagger/OpenAPI setup | Foundation/common | springdoc dependency + OpenApiConfig | Backend `cd backend && .\mvnw.cmd test` PASS | `54d1708 feat: add Swagger OpenAPI setup` |
| 6 | 2026-05-03 | Global ErrorDTO + GlobalExceptionHandler | Foundation/common | ErrorDTO + ErrorCode + ApiException + GlobalExceptionHandler + GlobalExceptionHandlerTest + validation dependency | Backend `cd backend && .\mvnw.cmd test` PASS | `78df72c feat: add global error handling` |
| 7 | 2026-05-03 | Build Log update after Global ErrorDTO | Docs | CodeQuest_Build_Log.md | Git status clean after docs commit | `0161bdf docs: record global error handling completion` |
| 8 | 2026-05-03 | Auth register | Auth | backend/pom.xml, AuthController, AuthService, RegisterRequest, RegisterResponse, AuthMapper, User entity, UserRepository, UserRank, UserRole, PasswordEncoderConfig, ErrorCode, GlobalExceptionHandler, AuthServiceTest, AuthControllerTest, test application.yml | Backend `cd backend && .\mvnw.cmd test` PASS according to Codex output; 15 tests total | `cb01ae3 feat: add auth register` |
| 9 | 2026-05-03 | Auth login | Auth | LoginRequest, LoginResponse, AuthService.login() method, AuthController.login() endpoint, AuthMapper.toLoginResponse(), GlobalExceptionHandler INVALID_CREDENTIALS mapping, AuthServiceTest login tests (5 methods), AuthControllerTest login tests (7 methods) | Backend `cd backend && .\mvnw.cmd test` PASS; 27 tests total (13 AuthController + 10 AuthService + 2 GlobalExceptionHandler + 1 Application + 1 Health) | `a1b500d feat: add auth login` |
| 10 | 2026-05-04 | JWT authentication | Auth/security | backend/pom.xml, application.yml, test application.yml, LoginResponse, AuthService, AuthController, AuthMapper, JwtService, CurrentUserPrincipal, JwtAuthenticationFilter, RestAuthenticationEntryPoint, SecurityConfig, AuthServiceTest, AuthControllerTest, HealthControllerTest, JwtServiceTest, SecurityConfigTest | Backend `cd backend && .\mvnw.cmd test` PASS; 33 tests total | `89564e7 feat: add jwt authentication` |
| 11 | 2026-05-04 | Refresh token | Auth | V2 refresh_tokens Flyway migration, RefreshToken entity, RefreshTokenRepository, RefreshTokenService, RefreshTokenRequest, RefreshTokenResponse, LoginResponse refreshToken field, AuthService refresh flow, AuthController refresh endpoint, AuthMapper update, ErrorCode INVALID_REFRESH_TOKEN, GlobalExceptionHandler mapping, SecurityConfig public refresh endpoint, application.yml refresh-token config, AuthServiceTest, AuthControllerTest, JwtService jti fix | Backend `cd backend && .\mvnw.cmd test` PASS; 37 tests total | `26fc7c5 feat: add refresh token` |

## Test Results Log
| Date | Command | Result | Failure summary | Fixed? |
|---|---|---|---|---|
| 2026-05-03 | `cd backend && .\mvnw.cmd test` | PASS | - | - |
| 2026-05-03 | `cd backend && .\mvnw.cmd test` | PASS | - | - |
| 2026-05-03 | `cd backend && .\mvnw.cmd test` | PASS | - | - |
| 2026-05-03 | `cd backend && .\mvnw.cmd test` | PASS | - | - |
| 2026-05-03 | `cd backend && .\mvnw.cmd test` | PASS | - | - |
| 2026-05-03 | `cd backend && .\mvnw.cmd test` | PASS | GlobalExceptionHandlerTest initially failed due missing validation provider/test context; fixed by adding validation starter and stable standalone MockMvcBuilders test | Yes |
| 2026-05-03 | `cd backend && .\mvnw.cmd test` | PASS | Auth register tests passed according to Codex output: AuthControllerTest, AuthServiceTest, HealthControllerTest, GlobalExceptionHandlerTest, CodeQuestApplicationTests; total 15 tests | Yes |
| 2026-05-03 | `cd backend && .\mvnw.cmd test` | PASS | Auth login tests: 27 total (13 AuthController: 6 register + 7 login; 10 AuthService: 5 register + 5 login; 2 GlobalExceptionHandler; 1 Application; 1 Health) | Yes |
| 2026-05-04 | `cd backend && .\mvnw.cmd test` | PASS | JWT authentication tests passed: 33 total including JwtServiceTest and SecurityConfigTest; SecurityConfigTest ran 3 tests | Yes |
| 2026-05-04 | `cd backend && .\mvnw.cmd test` | PASS | Refresh token tests passed: 37 total, 0 failures, 0 errors, 0 skipped. Initial refresh-token test failed because refreshed JWT matched original token when generated in same second; fixed by adding unique JWT `jti` claim in JwtService. | Yes |

## Manual Verification Log
| Date | Feature | Manual/API check | Expected result | Status |
|---|---|---|---|---|
| 2026-05-03 | Backend health endpoint | GET `/api/health` | 200 OK with backend health response | Passed during feature task |
| 2026-05-03 | Auth register | POST `/api/auth/register` with valid name, email, password | 201 Created with userId, name, email, rank BEGINNER, xp 0; no passwordHash | Recommended before commit |
| 2026-05-03 | Auth register duplicate email | POST `/api/auth/register` again with same email | 409 Conflict with standard ErrorDTO and EMAIL_ALREADY_EXISTS | Recommended before commit |
| 2026-05-03 | Auth register invalid password | POST `/api/auth/register` with weak password | 400 Bad Request with standard ErrorDTO and VALIDATION_ERROR | Recommended before commit |
| 2026-05-03 | Auth login | POST `/api/auth/login` with valid registered email and correct password | 200 OK with userId, name, email, rank BEGINNER, xp 0, streak 0; no passwordHash | Recommended before commit |
| 2026-05-03 | Auth login wrong password | POST `/api/auth/login` with registered email and wrong password | 401 Unauthorized with standard ErrorDTO and INVALID_CREDENTIALS | Recommended before commit |
| 2026-05-03 | Auth login unknown email | POST `/api/auth/login` with unregistered email | 401 Unauthorized with standard ErrorDTO and INVALID_CREDENTIALS | Recommended before commit |
| 2026-05-04 | JWT authentication | POST `/api/auth/login` with valid credentials | 200 OK with userId, name, email, rank, xp, streak, accessToken, tokenType Bearer, expiresInSeconds | Recommended before commit |
| 2026-05-04 | JWT protected endpoint without token | Request any protected endpoint without Authorization header | 401 Unauthorized with standard ErrorDTO | Recommended before commit |
| 2026-05-04 | JWT public health endpoint | GET `/api/health` without token | 200 OK | Covered by automated tests |
| 2026-05-04 | Refresh token login response | POST `/api/auth/login` with valid credentials | 200 OK with accessToken and refreshToken; no passwordHash, password_hash, role, or tokenHash | Automated tests passed; manual API smoke test recommended |
| 2026-05-04 | Refresh token endpoint | POST `/api/auth/refresh` with valid refreshToken | 200 OK with new accessToken, tokenType Bearer, expiresInSeconds | Automated tests passed; manual API smoke test recommended |
| 2026-05-04 | Invalid refresh token | POST `/api/auth/refresh` with invalid refreshToken | 401 Unauthorized with standard ErrorDTO and INVALID_REFRESH_TOKEN | Automated tests passed; manual API smoke test recommended |
| 2026-05-04 | Missing refresh token | POST `/api/auth/refresh` with blank/missing refreshToken | 400 Bad Request with standard ErrorDTO and VALIDATION_ERROR | Automated tests passed; manual API smoke test recommended |

## Verification Protocol After Every Codex Task
Before committing any Codex-generated change, always do this:

1. Check changed files:
   ```powershell
   git status
   ```

2. Run the correct automated backend test command:
   ```powershell
   cd backend
   .\mvnw.cmd test
   cd ..
   ```

3. For frontend tasks, also run:
   ```powershell
   cd frontend
   npm run build
   cd ..
   ```

4. Manually test the exact implemented feature:
   - For backend endpoints, use Swagger, Postman, Thunder Client, or curl.
   - Test one success case.
   - Test one important failure case.
   - Confirm the response shape matches API contracts.
   - Confirm standard ErrorDTO appears for errors.
   - Confirm sensitive fields are not leaked.

5. Only after tests and manual smoke test pass:
   - update this Build Log
   - commit changes
   - confirm clean git status

6. Do not start the next feature while current feature changes are uncommitted.

## Auth Register Manual Test Commands
Use these after starting the backend.

Start backend:
```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Valid register request:
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "name": "Antara",
  "email": "antara@example.com",
  "password": "StrongPass123"
}
```

Expected success:
```json
{
  "userId": "uuid",
  "name": "Antara",
  "email": "antara@example.com",
  "rank": "BEGINNER",
  "xp": 0
}
```

Duplicate email expected:
```text
HTTP 409 Conflict
code: EMAIL_ALREADY_EXISTS
```

Invalid password expected:
```text
HTTP 400 Bad Request
code: VALIDATION_ERROR
```

Important Auth register boundaries:
- Response must not contain `passwordHash`.
- Response must not contain `password_hash`.
- Response must not contain `role`.
- Register response still does not return JWT/access token.
- Login, JwtService, JWT filter, refresh token, logout, frontend auth, and dashboard were separate tasks.

## Auth Login Manual Test Commands
Use these after starting the backend.

Valid login request:
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "antara@example.com",
  "password": "StrongPass123"
}
```

Expected success after JWT authentication feature:
```json
{
  "userId": "uuid",
  "name": "Antara",
  "email": "antara@example.com",
  "rank": "BEGINNER",
  "xp": 0,
  "streak": 0,
  "accessToken": "jwt-token",
  "tokenType": "Bearer",
  "expiresInSeconds": 900
}
```

Wrong password expected:
```text
HTTP 401 Unauthorized
code: INVALID_CREDENTIALS
message: "Invalid email or password."
```

Unknown email expected:
```text
HTTP 401 Unauthorized
code: INVALID_CREDENTIALS
message: "Invalid email or password."
```

Important Auth login boundaries:
- Response must not contain `passwordHash`.
- Response must not contain `password_hash`.
- Response must not contain `role`.
- Login response now contains JWT accessToken, tokenType, and expiresInSeconds.
- Refresh token, logout, frontend auth, protected routes UI, and dashboard are still not implemented.

## JWT Authentication Manual Test Commands
Use these after starting the backend.

Login and copy token:
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "antara@example.com",
  "password": "StrongPass123"
}
```

Expected token fields:
```json
{
  "accessToken": "jwt-token",
  "tokenType": "Bearer",
  "expiresInSeconds": 900
}
```

Public endpoint without token:
```http
GET http://localhost:8080/api/health
```

Expected:
```text
HTTP 200 OK
```

Protected endpoint without token:
```text
Any non-public endpoint should return HTTP 401 Unauthorized without Authorization header.
```

Protected endpoint with token:
```http
Authorization: Bearer <accessToken>
```

Expected:
```text
Valid JWT should populate Spring SecurityContext and allow authenticated protected access.
```

Important JWT authentication boundaries:
- JWT subject is user id.
- JWT claims must not include passwordHash, password_hash, raw password, refresh token, secrets, or private data.
- JWT secret and expiry must come from config/environment.
- No refresh token implemented yet.
- No logout/token revoke implemented yet.
- No token rotation implemented yet.
- No frontend auth implemented yet.

## Next Chat Prompt
Paste this into a fresh ChatGPT Project chat whenever the current chat becomes slow or confusing:

```text
Read the project resources and this Build Log.
Continue CodeQuest from the current status.
Do not redesign anything.
Do not implement Phase 2 features.
Current module: Auth.
Last completed feature: JWT filter.
Current feature / next feature: Refresh token.
Latest JWT authentication commit: 89564e7 feat: add jwt authentication.
Give me one strict Codex prompt for Refresh token only.
Do not implement logout, token rotation beyond what refresh-token safety requires, frontend auth, dashboard, or Phase 2 features.
Include exact files to touch, files not to touch, commands to run, manual API checks, expected output, and Build Log update after completion.
```

## Update Protocol After Every Feature
1. Update Current Status: phase, current module, last completed feature, next feature, latest commit, and test status.
2. Tick the completed feature only after code compiles and manual testing is done.
3. Add a Feature History row with files changed, tests, and commit message.
4. Add bugs to Bugs / Issues immediately. Do not hide failing tests.
5. Add manual verification steps and result in Manual Verification Log.
6. Paste the next exact task into Next Chat Prompt before starting a new chat.
7. If Codex made assumptions, record them in Feature History or Bugs / Issues.
8. Commit only after automated tests and manual smoke test pass.
9. Confirm `git status` is clean after every feature commit.

## Definition of Done for Each Feature
- [ ] Feature follows the master blueprint and Core Rules.
- [ ] No unrelated files were modified.
- [ ] Controller has no business logic.
- [ ] Service contains business rules and ownership checks.
- [ ] DTOs are used for request/response.
- [ ] Validation annotations are added where needed.
- [ ] GlobalExceptionHandler handles new failure cases if required.
- [ ] Database changes use Flyway migration files.
- [ ] At least one meaningful automated test exists for backend logic.
- [ ] Automated tests pass using Maven Wrapper.
- [ ] Manual/API smoke test passes for the exact feature.
- [ ] Error case is manually checked where practical.
- [ ] Manual test steps are documented.
- [ ] Build Log is updated.
- [ ] Commit is created with a clear message.
- [ ] Git status is clean after commit.

## New Chat Continuation Summary Template
```text
Project: CodeQuest
Phase: MVP
Architecture: Modular monolith
Backend: Java 21 + Spring Boot
Frontend: React + Vite + Tailwind
Database: PostgreSQL + Flyway
AI: Gemini API
Code execution: Piston API

Current module: Auth
Last completed feature: JWT filter
Current feature / next task: Refresh token
Latest commit: 89564e7 feat: add jwt authentication
Git status expected after commit: clean
Tests passed: Backend Maven Wrapper test PASS for JWT authentication (33 tests); frontend build not required for backend-only task
Known bugs: None currently

Important completed JWT authentication details:
- JWT access token support implemented.
- Login response now returns accessToken, tokenType Bearer, and expiresInSeconds.
- JwtService generates and validates access tokens.
- JwtAuthenticationFilter parses Bearer tokens and populates Spring SecurityContext.
- SecurityConfig uses stateless API security.
- Public endpoints remain accessible without token: POST /api/auth/register, POST /api/auth/login, GET /api/health, Swagger/OpenAPI docs, actuator health if present.
- Non-public endpoints require authentication and return 401 without valid token.
- RestAuthenticationEntryPoint returns standard 401 ErrorDTO for unauthorized access.
- JWT claims are safe and must not include passwordHash, password_hash, raw password, refresh token, secrets, or private data.
- JWT secret and access-token expiry come from config/environment.
- Refresh token, logout, token revoke, token rotation, frontend auth, protected routes UI, and dashboard are not implemented yet.

Rules:
Follow master blueprint, Core Rules, DB Schema, API Contracts, Feature Prompts, Build Log, and AGENTS.md.
Do not redesign anything.
Do not add Phase 2 features.
Use Maven Wrapper only:
cd backend
.\mvnw.cmd test
```