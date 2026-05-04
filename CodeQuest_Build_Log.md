# CodeQuest Build Log

## Purpose
This file solves the long-chat slowdown problem. Update it manually after every feature so a fresh ChatGPT/Codex chat can continue from the current state without needing the full conversation history.

## Current Status
Phase: MVP
Current module: User
Current feature: User profile completed, committed, and pushed
Last completed feature: User profile
Next feature: Frontend auth pages / protected routes, but do not start until git status is clean and next feature scope is confirmed
Current branch: main
Latest commit: 9ba94ad feat: add user profile endpoint
Test status: Backend Maven Wrapper test PASS for User profile (43 tests); frontend build not required for backend-only task
Git status: clean after user profile commit and push

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
- [x] Logout / token revoke
- [x] User profile
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
- For suspicious stale compiled class issues, use:
  - `cd backend`
  - `.\mvnw.cmd clean test`
- After every Codex implementation, do not commit immediately. First verify:
  1. `git status`
  2. automated tests
  3. manual/API smoke test for the implemented feature
  4. Build Log update
  5. commit only after verification passes
- Do not start the next feature while the current feature has uncommitted changes.
- For backend-only tasks, frontend build is not required unless the backend change affects frontend integration or shared API contract behavior.
- Logout/token revoke revokes refresh tokens only. Existing JWT access tokens remain stateless until expiry.
- No access-token blacklist was added for logout.
- No refresh-token rotation was added for logout.
- No new Flyway migration was added for logout because `revoked_at` already existed in the `refresh_tokens` table.
- Logout response must stay safe and must not expose `tokenHash`, raw token, user role, password, passwordHash, or password_hash.
- User profile uses the authenticated user from JWT/SecurityContext through `CurrentUserPrincipal`.
- User profile endpoint must not accept user id from request params, request body, or path.
- User profile response must stay safe and must not expose `passwordHash`, `password_hash`, `tokenHash`, `refreshToken`, `role`, or raw password.

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
- Refresh token note: JwtService includes unique `jti` claim so newly generated access tokens differ even when generated in the same second.
- Logout/token revoke note: POST `/api/auth/logout` is implemented.
- Logout/token revoke note: Logout accepts a refresh token and revokes it by setting `revokedAt`.
- Logout/token revoke note: Logout does not delete refresh token rows.
- Logout/token revoke note: Logout does not blacklist JWT access tokens.
- Logout/token revoke note: Logout does not implement refresh-token rotation.
- Logout/token revoke note: Logout does not require a new Flyway migration because `revoked_at` already exists.
- Logout/token revoke note: Logout service tests passed.
- Logout/token revoke testing note: During testing, `AuthControllerTest` initially failed with `NoClassDefFoundError: GlobalExceptionHandler$1` because stale compiled output in `target/` was missing an enum-switch helper class. This was a stale build artifact issue, not a logout business logic issue. Running `.\mvnw.cmd clean test` fixed it.
- Logout/token revoke note: Frontend auth, protected routes UI, dashboard, user profile, token rotation, and Phase 2 features were intentionally unimplemented at that stage.
- User profile note: GET `/api/users/me` is implemented as an authenticated endpoint using the existing JWT authentication flow and `CurrentUserPrincipal`.
- User profile note: User profile response exposes safe user fields only and does not expose `passwordHash`, `password_hash`, `tokenHash`, `refreshToken`, `role`, or raw password.
- User profile note: Controller test uses real register -> login -> JWT -> `/api/users/me` flow instead of mocking `CurrentUserPrincipal`.
- User profile note: No update profile endpoint was implemented in this task.
- User profile note: No frontend work was implemented in this task.

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
| 12 | 2026-05-04 | Logout / token revoke | Auth | AuthController logout endpoint, AuthService.logout(), RefreshTokenService.revokeRefreshToken(), AuthMapper.toLogoutResponse(), LogoutRequest, LogoutResponse, AuthServiceTest logout tests | Backend `cd backend && .\mvnw.cmd clean test` PASS; 39 tests total | `feat: add auth logout`. Stale `target/` build output initially caused `GlobalExceptionHandler$1` class error; fixed by clean test. Commit pushed; git status clean. |
| 13 | 2026-05-04 | User profile | User | UserProfileResponse, UserMapper, UserService, UserController, UserServiceTest, UserControllerTest | Backend `cd backend && .\mvnw.cmd test` PASS; 43 tests total | `9ba94ad feat: add user profile endpoint`. Implemented authenticated GET `/api/users/me` with safe response fields. Commit pushed; git status clean. |

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
| 2026-05-04 | `cd backend && .\mvnw.cmd test` | FAIL | Logout task initially showed 4 `AuthControllerTest` errors where `ApiException` appeared to escape instead of becoming standard ErrorDTO. Investigation found stale compiled build output, later confirmed by `NoClassDefFoundError: GlobalExceptionHandler$1`. | Yes |
| 2026-05-04 | `cd backend && .\mvnw.cmd -Dtest=AuthServiceTest test` | PASS | Logout service tests passed: 14 AuthService tests, 0 failures, 0 errors, 0 skipped. | Yes |
| 2026-05-04 | `cd backend && .\mvnw.cmd -Dtest=AuthControllerTest#shouldRegisterUserSuccessfully test` | PASS | Controller success path verified: 1 test, 0 failures, 0 errors, 0 skipped. | Yes |
| 2026-05-04 | `cd backend && .\mvnw.cmd -Dtest=AuthControllerTest#shouldReturnConflictForDuplicateEmail test` | FAIL | Exposed real cause: `NoClassDefFoundError: com/codequest/common/exception/GlobalExceptionHandler$1`, caused by stale compiled class output in `target/`. | Yes |
| 2026-05-04 | `cd backend && .\mvnw.cmd clean test` | PASS | Full backend clean test passed after removing stale target output: 39 tests, 0 failures, 0 errors, 0 skipped. | Yes |
| 2026-05-04 | `cd backend && .\mvnw.cmd -Dtest=UserServiceTest test` | PASS | User profile service tests passed: 2 tests, 0 failures, 0 errors, 0 skipped. | Yes |
| 2026-05-04 | `cd backend && .\mvnw.cmd -Dtest=UserControllerTest test` | PASS | User profile controller tests passed: 2 tests, 0 failures, 0 errors, 0 skipped. Tests use real register -> login -> JWT -> `/api/users/me` flow. | Yes |
| 2026-05-04 | `cd backend && .\mvnw.cmd test` | PASS | User profile full backend tests passed: 43 total, 0 failures, 0 errors, 0 skipped. Includes UserServiceTest and UserControllerTest for authenticated GET `/api/users/me`. | Yes |

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
| 2026-05-04 | Logout / token revoke | POST `/api/auth/logout` with valid refreshToken from login | 200 OK with safe success message; refresh token row has `revokedAt` set | Automated service tests passed; manual API smoke test recommended |
| 2026-05-04 | Refresh after logout | POST `/api/auth/refresh` using same refreshToken after logout | 401 Unauthorized with standard ErrorDTO and INVALID_REFRESH_TOKEN | Automated service tests passed; manual API smoke test recommended |
| 2026-05-04 | Logout safety | Inspect logout response | Response must not include tokenHash, raw token, passwordHash, password_hash, role, or internal user data | Automated service tests passed; manual API smoke test recommended |
| 2026-05-04 | User profile with token | GET `/api/users/me` with valid JWT access token | 200 OK with userId, name, email, rank, xp, streak, goal, avatarUrl, createdAt | Automated integration test passed; manual API smoke test recommended |
| 2026-05-04 | User profile without token | GET `/api/users/me` without Authorization header | 401 Unauthorized | Automated integration test passed; manual API smoke test recommended |
| 2026-05-04 | User profile safety | Inspect GET `/api/users/me` response | Response must not include passwordHash, password_hash, tokenHash, refreshToken, role, or raw password | Automated integration test passed |

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

3. If tests fail in a suspicious way after multiple Java/test edits, run a clean backend test:
   ```powershell
   cd backend
   .\mvnw.cmd clean test
   cd ..
   ```

4. For frontend tasks, also run:
   ```powershell
   cd frontend
   npm run build
   cd ..
   ```

5. Manually test the exact implemented feature:
   - For backend endpoints, use Swagger, Postman, Thunder Client, or curl.
   - Test one success case.
   - Test one important failure case.
   - Confirm the response shape matches API contracts.
   - Confirm standard ErrorDTO appears for errors.
   - Confirm sensitive fields are not leaked.

6. Only after tests and manual smoke test pass:
   - update this Build Log
   - commit changes
   - confirm clean git status

7. Do not start the next feature while current feature changes are uncommitted.

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

Expected success after User profile feature:
```json
{
  "userId": "uuid",
  "name": "Antara",
  "email": "antara@example.com",
  "rank": "BEGINNER",
  "xp": 0,
  "streak": 0,
  "accessToken": "jwt-token",
  "refreshToken": "opaque-refresh-token",
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
- Response must not contain `tokenHash`.
- Login response now contains JWT accessToken, opaque refreshToken, tokenType, and expiresInSeconds.
- Logout/token revoke is implemented for refresh token revocation.
- User profile is implemented as authenticated GET `/api/users/me`.
- Token rotation, frontend auth, protected routes UI, and dashboard are still not implemented.

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
  "refreshToken": "opaque-refresh-token",
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
- JWT includes unique `jti` claim.
- JWT claims must not include passwordHash, password_hash, raw password, refresh token, secrets, or private data.
- JWT secret and expiry must come from config/environment.
- Refresh token is implemented as an opaque token stored only as a hash.
- Logout/token revoke is implemented by revoking refresh tokens only.
- User profile uses authenticated JWT principal.
- No access-token blacklist implemented.
- No token rotation implemented yet.
- No frontend auth implemented yet.

## Refresh Token Manual Test Commands
Use these after starting the backend.

Login and copy refresh token:
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "antara@example.com",
  "password": "StrongPass123"
}
```

Expected login token fields:
```json
{
  "accessToken": "jwt-token",
  "refreshToken": "opaque-refresh-token",
  "tokenType": "Bearer",
  "expiresInSeconds": 900
}
```

Refresh access token:
```http
POST http://localhost:8080/api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "<refreshToken from login>"
}
```

Expected refresh success:
```json
{
  "accessToken": "new-jwt-token",
  "tokenType": "Bearer",
  "expiresInSeconds": 900
}
```

Invalid refresh token expected:
```text
HTTP 401 Unauthorized
code: INVALID_REFRESH_TOKEN
message: "Invalid refresh token."
```

Missing or blank refresh token expected:
```text
HTTP 400 Bad Request
code: VALIDATION_ERROR
```

Important Refresh token boundaries:
- Refresh token is opaque, not JWT.
- Only refresh token hash is stored in database.
- Raw refresh token must never be logged.
- Raw refresh token must never be stored in database.
- tokenHash must never be returned in API response.
- Refresh endpoint returns new accessToken only.
- Refresh endpoint does not return new refreshToken.
- Logout/token revoke is implemented.
- User profile is implemented.
- Token rotation is not implemented yet.
- Frontend auth is not implemented yet.

## Logout / Token Revoke Manual Test Commands
Use these after starting the backend.

Start backend:
```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Login and copy refresh token:
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "antara@example.com",
  "password": "StrongPass123"
}
```

Expected login token fields:
```json
{
  "accessToken": "jwt-token",
  "refreshToken": "opaque-refresh-token",
  "tokenType": "Bearer",
  "expiresInSeconds": 900
}
```

Logout using refresh token:
```http
POST http://localhost:8080/api/auth/logout
Content-Type: application/json

{
  "refreshToken": "<refreshToken from login>"
}
```

Expected logout success:
```json
{
  "message": "Logged out successfully."
}
```

Try refreshing with same refresh token after logout:
```http
POST http://localhost:8080/api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "<same refreshToken used in logout>"
}
```

Expected after logout:
```text
HTTP 401 Unauthorized
code: INVALID_REFRESH_TOKEN
message: "Invalid refresh token."
```

Blank logout refresh token expected:
```text
HTTP 400 Bad Request
code: VALIDATION_ERROR
```

Important Logout / token revoke boundaries:
- Logout revokes refresh token by setting `revokedAt`.
- Logout does not delete refresh token rows.
- Logout does not blacklist existing JWT access tokens.
- Existing access tokens remain valid until expiry.
- Logout does not rotate refresh tokens.
- Logout does not require a Flyway migration.
- Logout response must not contain `refreshToken`.
- Logout response must not contain `tokenHash`.
- Logout response must not contain `passwordHash`.
- Logout response must not contain `password_hash`.
- Logout response must not contain `role`.
- Logout response must not expose internal user data.

## User Profile Manual Test Commands
Use these after starting the backend.

Start backend:
```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Register a user:
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "name": "Antara",
  "email": "antara@example.com",
  "password": "StrongPass123"
}
```

Login and copy access token:
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "antara@example.com",
  "password": "StrongPass123"
}
```

Expected login token fields:
```json
{
  "accessToken": "jwt-token",
  "refreshToken": "opaque-refresh-token",
  "tokenType": "Bearer",
  "expiresInSeconds": 900
}
```

Get current user profile:
```http
GET http://localhost:8080/api/users/me
Authorization: Bearer <accessToken>
```

Expected profile success:
```json
{
  "userId": "uuid",
  "name": "Antara",
  "email": "antara@example.com",
  "rank": "BEGINNER",
  "xp": 0,
  "streak": 0,
  "goal": null,
  "avatarUrl": null,
  "createdAt": "timestamp"
}
```

Without token:
```http
GET http://localhost:8080/api/users/me
```

Expected:
```text
HTTP 401 Unauthorized
```

Important User profile boundaries:
- Endpoint must use authenticated user from JWT/SecurityContext.
- Endpoint must not accept userId from request params, request body, or path.
- Response must not expose `passwordHash`.
- Response must not expose `password_hash`.
- Response must not expose `tokenHash`.
- Response must not expose `refreshToken`.
- Response must not expose `role`.
- Response must not expose raw password.
- No update profile endpoint implemented yet.
- Frontend auth is not implemented yet.

## Next Chat Prompt
Paste this into a fresh ChatGPT Project chat whenever the current chat becomes slow or confusing:

```text
Read the project resources and this Build Log.
Continue CodeQuest from the current status.
Do not redesign anything.
Do not implement Phase 2 features.

Current module: User.
Last completed feature: User profile.
Current feature status: User profile implemented, backend tests passed, committed and pushed.
Latest completed commit: 9ba94ad feat: add user profile endpoint.
Git status: clean after user profile commit and push.

Current user profile implementation details:
- GET /api/users/me implemented.
- Endpoint requires JWT authentication.
- Endpoint uses CurrentUserPrincipal from SecurityContext.
- Endpoint does not accept userId from params, body, or path.
- UserProfileResponse returns safe fields only: userId, name, email, rank, xp, streak, goal, avatarUrl, createdAt.
- UserProfileResponse does not expose passwordHash, password_hash, tokenHash, refreshToken, role, or raw password.
- UserControllerTest uses real register -> login -> JWT -> /api/users/me flow.
- UserServiceTest and UserControllerTest pass.
- No update profile endpoint implemented.
- No frontend work added.

Testing:
- Use Maven Wrapper only.
- Backend test passed:
  cd backend
  .\mvnw.cmd test
- Result: 43 tests, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS.

Do not implement frontend auth, dashboard, protected routes UI, course, AI, leaderboard, Docker, CI/CD, deployment, or Phase 2 features until git status is clean and the next feature scope is confirmed.

Give me the next safest step only:
1. First confirm git status is clean.
2. If git status is clean, propose one strict Codex prompt for the next MVP feature only.
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

Current module: User
Last completed feature: User profile
Current feature status: User profile implemented, backend tests passed, committed and pushed
Next task: Select next MVP feature only after confirming git status is clean
Latest completed commit: 9ba94ad feat: add user profile endpoint
Git status: clean after user profile commit and push
Tests passed: Backend Maven Wrapper test PASS for User profile (43 tests); frontend build not required for backend-only task
Known bugs: None currently

Important completed Auth details:
- Register implemented.
- Login implemented.
- JWT authentication implemented.
- Login response returns accessToken and refreshToken.
- Refresh token is opaque, not JWT.
- Refresh token is stored only as a hash in the refresh_tokens table.
- V2 Flyway migration creates refresh_tokens table.
- POST /api/auth/refresh implemented.
- Refresh endpoint accepts refreshToken and returns a new accessToken, tokenType Bearer, and expiresInSeconds.
- Refresh endpoint does not return a new refreshToken because token rotation is not implemented yet.
- INVALID_REFRESH_TOKEN maps to HTTP 401 with a safe generic message.
- JwtService includes unique jti claim so newly generated access tokens differ even when generated in the same second.
- POST /api/auth/logout implemented.
- Logout accepts refreshToken through LogoutRequest.
- Logout returns safe LogoutResponse message.
- Logout revokes refresh token by setting revokedAt.
- Logout does not delete refresh token rows.
- Logout does not blacklist JWT access tokens.
- Existing access tokens remain valid until expiry.
- No refresh-token rotation implemented.

Important completed User profile details:
- GET /api/users/me implemented.
- Endpoint requires JWT authentication.
- Endpoint uses CurrentUserPrincipal from SecurityContext.
- Endpoint does not accept userId from params, body, or path.
- UserProfileResponse returns safe fields: userId, name, email, rank, xp, streak, goal, avatarUrl, createdAt.
- UserProfileResponse does not expose passwordHash, password_hash, tokenHash, refreshToken, role, or raw password.
- UserControllerTest uses real register -> login -> JWT -> /api/users/me flow.
- UserServiceTest and UserControllerTest pass.
- No update profile endpoint implemented.
- Frontend auth, protected routes UI, dashboard, token rotation, and Phase 2 features are not implemented yet.

Testing notes:
- Always use Maven Wrapper only:
  cd backend
  .\mvnw.cmd test
- If stale compiled class issues appear, run:
  cd backend
  .\mvnw.cmd clean test
- User profile test result:
  Tests run: 43, Failures: 0, Errors: 0, Skipped: 0
  BUILD SUCCESS

Rules:
Follow master blueprint, Core Rules, DB Schema, API Contracts, Feature Prompts, Build Log, and AGENTS.md.
Do not redesign anything.
Do not add Phase 2 features.
Do not start the next feature while current feature changes are uncommitted.
Use Maven Wrapper only.
```