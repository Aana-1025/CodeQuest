# CodeQuest Build Log

## Purpose
This file solves the long-chat slowdown problem. Update it manually after every feature so a fresh ChatGPT/Codex chat can continue from the current state without needing the full conversation history.

## Current Status
Phase: MVP
Current module: Dashboard
Current feature: Dashboard shell completed, build passed, pending commit
Last completed feature: Protected routes
Next feature: Course generation only, but do not start until git status is clean after dashboard shell commit
Current branch: main
Latest commit: a2ca3d4 docs: record protected routes completion
Test status: Frontend `cd frontend && npm run build` PASS; backend tests not required for frontend-only task
Git status: modified files pending commit for dashboard shell

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
- [x] Frontend auth pages
- [x] Protected routes
- [x] Dashboard shell
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
- Do not use plain `mvn`.
- Do not tell Codex or ChatGPT to run `mvn test`; always use Maven Wrapper commands.
- For suspicious stale compiled class issues, use:
  - `cd backend`
  - `.\mvnw.cmd clean test`
- After every Codex implementation, do not commit immediately. First verify:
  1. `git status`
  2. automated tests
  3. manual/API smoke test for the implemented feature when practical
  4. Build Log update
  5. commit only after verification passes
- Do not start the next feature while the current feature has uncommitted changes.
- For backend-only tasks, frontend build is not required unless the backend change affects frontend integration or shared API contract behavior.
- For frontend-only tasks, backend tests are not required unless frontend changes also touch shared API contract behavior or backend files.
- Logout/token revoke revokes refresh tokens only. Existing JWT access tokens remain stateless until expiry.
- No access-token blacklist was added for logout.
- No refresh-token rotation was added for logout.
- No new Flyway migration was added for logout because `revoked_at` already existed in the `refresh_tokens` table.
- Logout response must stay safe and must not expose `tokenHash`, raw token, user role, password, passwordHash, or password_hash.
- User profile uses the authenticated user from JWT/SecurityContext through `CurrentUserPrincipal`.
- User profile endpoint must not accept user id from request params, request body, or path.
- User profile response must stay safe and must not expose `passwordHash`, `password_hash`, `tokenHash`, `refreshToken`, `role`, or raw password.
- User profile API contract is aligned to GET `/api/user/profile`.
- No PATCH profile endpoint is implemented yet.
- Frontend auth pages use backend auth endpoints POST `/api/auth/register` and POST `/api/auth/login`.
- Frontend auth pages store `accessToken` and `refreshToken` in localStorage for MVP only.
- Frontend auth pages do not implement dashboard, profile page, logout UI, token refresh retry logic, or refresh-token rotation.
- Protected routes are implemented using React state navigation only.
- React Router was not added for protected routes.
- Protected Area checks local accessToken presence using tokenStorage.
- Protected Area can call GET `/api/user/profile` using Bearer accessToken through getCurrentUserProfile.
- Protected Area shows safe profile fields only: name, email, rank, xp, streak.
- Protected Area does not show accessToken or refreshToken.
- Protected routes do not implement real dashboard, logout UI, refresh-token retry logic, token rotation, or profile edit.
- Frontend must use GET `/api/user/profile` for current user profile in the protected routes task.
- Running `.\mvnw.cmd spring-boot:run` without a configured local database fails because the default profile has no datasource URL. This is not caused by the user profile API alignment, frontend auth pages, or protected routes. Do not fix database runtime configuration inside unrelated feature tasks.
- Local manual API smoke testing with `spring-boot:run` requires a configured PostgreSQL datasource/profile or environment variables. Backend tests can still pass using the test configuration.
- Next feature must be Dashboard shell only, but do not start it unless git status is clean after protected routes commit.
- Do not combine dashboard with course, AI, leaderboard, Docker, CI/CD, deployment, or Phase 2 features.

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
- Logout/token revoke note: Protected routes UI, dashboard, user profile update, token rotation, and Phase 2 features were intentionally unimplemented at that stage.
- User profile note: GET `/api/user/profile` is implemented as an authenticated endpoint using the existing JWT authentication flow and `CurrentUserPrincipal`.
- User profile note: User profile response exposes safe user fields only and does not expose `passwordHash`, `password_hash`, `tokenHash`, `refreshToken`, `role`, or raw password.
- User profile note: Controller test uses real register -> login -> JWT -> `/api/user/profile` flow instead of mocking `CurrentUserPrincipal`.
- User profile note: No update profile endpoint was implemented in this task.
- User profile API alignment note: The original user profile implementation used GET `/api/users/me`; it was later aligned to the API contract endpoint GET `/api/user/profile`.
- User profile API alignment note: The alignment changed endpoint path and tests only. No business logic, DTO, security, database, Flyway, auth, refresh token, logout, or frontend changes were made.
- User profile API alignment note: Commit `b9039ad fix: align user profile endpoint contract` was pushed to `main`, and git status was clean afterward.
- Frontend auth pages note: Login and Register pages are implemented using React state navigation only.
- Frontend auth pages note: No React Router was added.
- Frontend auth pages note: No new npm dependencies were added.
- Frontend auth pages note: `frontend/package.json` and `frontend/package-lock.json` were not changed.
- Frontend auth pages note: Backend files were not changed.
- Frontend auth pages note: Frontend build passed with `cd frontend && npm run build`.
- Frontend auth pages note: Commit `891476c feat: add frontend auth pages` was pushed to `main`, and git status was clean afterward.
- Frontend auth pages note: Manual backend-connected smoke test was not completed because local backend runtime datasource is not configured.
- Protected routes note: Protected Area implemented as a simple MVP protected view, not the final dashboard.
- Protected routes note: Commit `c607568 feat: add protected routes` was pushed to `main`, and git status was clean afterward.
- Protected routes note: Manual backend-connected profile load smoke test was not completed because local backend datasource/profile is not configured.
- Dashboard shell is implemented as a static MVP page.
- Dashboard shell uses React state navigation only.
- React Router was not added.
- Dashboard shell receives profile from App.jsx as props only.
- Dashboard shell does not fetch data.
- Dashboard shell does not read accessToken or refreshToken.
- Dashboard shell shows safe profile fields only: name, email, rank, xp, streak.
- Dashboard shell does not implement course generation, AI/Gemini, XP/rank logic, streak logic, leaderboard, logout UI, code execution, or backend calls.
- Dashboard shell note: DashboardShell is a static UI shell only, not the final dashboard logic.
- Dashboard shell note: No backend-connected manual smoke test required because no new backend/API behavior was added.
- Runtime database config note: A manual `.\mvnw.cmd spring-boot:run` attempt failed because no active profile was set and no datasource URL was configured. This is a local runtime configuration issue, not a failed API alignment, frontend auth, or protected routes issue. Do not mix this with unrelated feature tasks.

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
| 13 | 2026-05-04 | User profile | User | UserProfileResponse, UserMapper, UserService, UserController, UserServiceTest, UserControllerTest | Backend `cd backend && .\mvnw.cmd test` PASS; 43 tests total | `9ba94ad feat: add user profile endpoint`. Initially implemented authenticated GET `/api/users/me` with safe response fields. Commit pushed; git status clean. |
| 14 | 2026-05-04 | Build Log update after User profile | Docs | CodeQuest_Build_Log.md | Git status clean after docs commit | `051f278 docs: record user profile completion` |
| 15 | 2026-05-04 | User profile API contract alignment | User | UserController, UserControllerTest, CodeQuest_Build_Log.md | Backend `cd backend && .\mvnw.cmd -Dtest=UserControllerTest test` PASS; Backend `cd backend && .\mvnw.cmd test` PASS; 43 tests total | `b9039ad fix: align user profile endpoint contract`. Changed authenticated profile endpoint from GET `/api/users/me` to GET `/api/user/profile`. No business logic, DTO, security, DB, Flyway, auth, refresh token, logout, or frontend changes. No PATCH profile endpoint implemented. Commit pushed; git status clean. |
| 16 | 2026-05-05 | Frontend auth pages | Frontend Auth | App.jsx, Login.jsx, Register.jsx, authApi.js, tokenStorage.js, CodeQuest_Build_Log.md | Frontend `cd frontend && npm run build` PASS | `891476c feat: add frontend auth pages`. Implemented login/register pages using React state navigation, auth API service, and localStorage token storage. No protected routes, dashboard, logout UI, profile page, package changes, or backend changes. Commit pushed; git status clean. |
| 17 | 2026-05-05 | Protected routes | Frontend Auth | App.jsx, authApi.js, authState.js, CodeQuest_Build_Log.md | Frontend `cd frontend && npm run build` PASS | `c607568 feat: add protected routes`. Implemented React state based Protected Area, local auth snapshot check, and profile loading via GET `/api/user/profile`. No React Router, dashboard, logout UI, token refresh retry, package changes, or backend changes. Commit pushed; git status clean. |
| 18 | 2026-05-05 | Dashboard shell | Dashboard | App.jsx, DashboardShell.jsx, CodeQuest_Build_Log.md | Frontend `cd frontend && npm run build` PASS | Pending commit. Implemented static dashboard shell page and wired it from Protected Area using React state navigation. No React Router, backend/API calls, course generation, AI/Gemini, XP/streak logic, leaderboard, logout UI, code execution, package changes, or backend changes. |

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
| 2026-05-04 | `cd backend && .\mvnw.cmd -Dtest=UserControllerTest test` | PASS | User profile controller tests passed: 2 tests, 0 failures, 0 errors, 0 skipped. Tests used real register -> login -> JWT -> `/api/users/me` flow before API contract alignment. | Yes |
| 2026-05-04 | `cd backend && .\mvnw.cmd test` | PASS | User profile full backend tests passed: 43 total, 0 failures, 0 errors, 0 skipped. Included UserServiceTest and UserControllerTest for authenticated GET `/api/users/me` before API contract alignment. | Yes |
| 2026-05-04 | `cd backend && .\mvnw.cmd -Dtest=UserControllerTest test` | PASS | User profile API contract alignment controller tests passed. Tests now use real register -> login -> JWT -> `/api/user/profile` flow. | Yes |
| 2026-05-04 | `cd backend && .\mvnw.cmd test` | PASS | User profile API contract alignment full backend tests passed: 43 total, 0 failures, 0 errors, 0 skipped. | Yes |
| 2026-05-04 | `cd backend && .\mvnw.cmd spring-boot:run` | FAIL | Runtime startup failed because no active profile was set and no datasource URL was configured. Error: `Failed to determine suitable jdbc url`. This is a local runtime DB configuration issue, not a user profile API alignment failure. | No action in this task; handle local runtime DB config separately later if needed. |
| 2026-05-05 | `cd frontend && npm run build` | PASS | Frontend build succeeded with authApi.js, tokenStorage.js, Register.jsx, Login.jsx, and App.jsx wiring. No errors, no warnings. | Yes |
| 2026-05-05 | `cd frontend && npm run build` | PASS | Frontend build succeeded with protected view, authState.js, and getCurrentUserProfile helper. | Yes |
| 2026-05-05 | `cd frontend && npm run build` | PASS | Frontend build succeeded with DashboardShell.jsx and App.jsx dashboard wiring. | Yes |

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
| 2026-05-04 | User profile with token before API alignment | GET `/api/users/me` with valid JWT access token | 200 OK with userId, name, email, rank, xp, streak, goal, avatarUrl, createdAt | Automated integration test passed |
| 2026-05-04 | User profile without token before API alignment | GET `/api/users/me` without Authorization header | 401 Unauthorized | Automated integration test passed |
| 2026-05-04 | User profile safety before API alignment | Inspect GET `/api/users/me` response | Response must not include passwordHash, password_hash, tokenHash, refreshToken, role, or raw password | Automated integration test passed |
| 2026-05-04 | User profile API alignment with token | GET `/api/user/profile` with valid JWT access token | 200 OK with userId, name, email, rank, xp, streak, goal, avatarUrl, createdAt | Automated integration test passed; manual API smoke test blocked until local runtime DB config is set |
| 2026-05-04 | User profile API alignment without token | GET `/api/user/profile` without Authorization header | 401 Unauthorized | Automated integration test passed; manual API smoke test blocked until local runtime DB config is set |
| 2026-05-04 | User profile API alignment safety | Inspect GET `/api/user/profile` response | Response must not include passwordHash, password_hash, tokenHash, refreshToken, role, or raw password | Automated integration test passed |
| 2026-05-04 | Backend runtime startup | `cd backend && .\mvnw.cmd spring-boot:run` | Backend starts only if datasource URL/profile is configured | Failed due missing local datasource URL; not related to API alignment |
| 2026-05-05 | Frontend auth pages | Register/login UI backend-connected smoke test | Register and login forms call backend, login saves accessToken and refreshToken | Not completed because local backend datasource is not configured |
| 2026-05-05 | Protected routes | Protected Area backend-connected profile load smoke test | Logged-in user can open Protected Area and load safe profile fields from GET `/api/user/profile` | Not completed because local backend datasource is not configured |
| 2026-05-05 | Dashboard shell | Dashboard shell UI smoke check | User can open Dashboard Shell from Protected Area and see static cards/placeholders without backend calls | Pending local browser check |

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

5. Manually test the exact implemented feature when runtime configuration allows it:
   - For backend endpoints, use Swagger, Postman, Thunder Client, or curl.
   - Test one success case.
   - Test one important failure case.
   - Confirm the response shape matches API contracts.
   - Confirm standard ErrorDTO appears for errors.
   - Confirm sensitive fields are not leaked.
   - If `spring-boot:run` fails because local datasource variables are missing, record the runtime config issue separately and do not mix it with the feature implementation unless the task is explicitly database runtime setup.

6. Only after tests and practical manual smoke checks pass:
   - update this Build Log
   - commit changes
   - confirm clean git status

7. Do not start the next feature while current feature changes are uncommitted.

## Auth Register Manual Test Commands
Use these after starting the backend with a configured local datasource/profile.

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
- Frontend auth pages are implemented.
- Protected routes are implemented.
- Dashboard is still not implemented.

## Auth Login Manual Test Commands
Use these after starting the backend with a configured local datasource/profile.

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
- User profile is implemented as authenticated GET `/api/user/profile`.
- Frontend auth pages are implemented.
- Protected routes are implemented.
- Token rotation and dashboard are still not implemented.

## JWT Authentication Manual Test Commands
Use these after starting the backend with a configured local datasource/profile.

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
- User profile endpoint is GET `/api/user/profile`.
- Frontend auth pages are implemented.
- Protected routes are implemented.
- No access-token blacklist implemented.
- No token rotation implemented yet.
- Dashboard is not implemented yet.

## Refresh Token Manual Test Commands
Use these after starting the backend with a configured local datasource/profile.

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
- User profile is implemented as GET `/api/user/profile`.
- Frontend auth pages are implemented.
- Protected routes are implemented.
- Token rotation is not implemented yet.
- Dashboard is not implemented yet.

## Logout / Token Revoke Manual Test Commands
Use these after starting the backend with a configured local datasource/profile.

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
- Frontend logout UI is not implemented yet.

## User Profile Manual Test Commands
Use these after starting the backend with a configured local datasource/profile.

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
GET http://localhost:8080/api/user/profile
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
GET http://localhost:8080/api/user/profile
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
- Current implemented endpoint is GET `/api/user/profile`.
- Old GET `/api/users/me` path should no longer be used after API contract alignment.
- No update profile endpoint implemented yet.
- Frontend auth pages are implemented.
- Protected routes are implemented.
- Profile edit UI is not implemented yet.

## Frontend Auth Pages Manual Test Commands
Use these after starting the backend with a configured local datasource/profile.

Start frontend:
```powershell
cd frontend
npm run dev
```

Open:
```text
http://localhost:5173
```

Register UI check:
```text
Open Register page.
Enter name, email, and password.
Submit the form.
Expected: frontend calls POST /api/auth/register and shows success or backend validation error.
```

Login UI check:
```text
Open Login page.
Enter email and password.
Submit the form.
Expected: frontend calls POST /api/auth/login, saves accessToken and refreshToken to localStorage, and returns to home with a safe logged-in message.
```

Expected localStorage keys:
```text
codequest_access_token
codequest_refresh_token
```

Important Frontend auth boundaries:
- Frontend auth pages use React state navigation only.
- React Router was not added.
- No package.json or package-lock.json change was made.
- No protected routes were implemented during the frontend auth pages task.
- Protected routes are implemented later in row 17.
- No dashboard was implemented.
- No logout UI was implemented.
- No profile page was implemented.
- No backend files were touched.
- Manual backend-connected smoke test is blocked until backend local datasource/profile is configured.

## Protected Routes Manual Test Commands
Use these after starting the backend with a configured local datasource/profile.

Start frontend:
```powershell
cd frontend
npm run dev
```

Open:
```text
http://localhost:5173
```

Protected Area without login:
```text
Open home page.
Click Protected Area without logging in.
Expected: inline message says "Please log in to view the protected area."
```

Login then Protected Area:
```text
Open Login page.
Enter valid email and password.
Submit the form.
Expected: login saves accessToken and refreshToken to localStorage and navigates to Protected Area.
```

Load profile:
```text
In Protected Area, click "Load my profile".
Expected: frontend calls GET /api/user/profile with Authorization Bearer accessToken and shows safe fields only: name, email, rank, xp, streak.
```

Important Protected routes boundaries:
- Protected routes use React state navigation only.
- React Router was not added.
- Protected Area checks local accessToken presence.
- Protected Area can load profile via GET `/api/user/profile`.
- Protected Area must not show accessToken or refreshToken.
- Protected Area must not show passwordHash, password_hash, tokenHash, role, or raw password.
- Protected Area is not the final dashboard.
- Dashboard shell is still not implemented.
- Logout UI is not implemented.
- Refresh-token retry and token rotation are not implemented.
- Manual backend-connected smoke test is blocked until backend local datasource/profile is configured.

## Next Chat Prompt
Paste this into a fresh ChatGPT Project chat whenever the current chat becomes slow or confusing:

```text
Read the project resources and this Build Log.
Continue CodeQuest from the current status.
Do not redesign anything.
Do not implement Phase 2 features.

Current module: Dashboard.
Last completed feature: Dashboard shell.
Current feature status: Dashboard shell implemented, frontend build passed, pending commit.
Latest completed commit: a2ca3d4 docs: record protected routes completion.
Pending commit: Dashboard shell.
Git status: modified files pending commit for dashboard shell.
Next feature must be Course generation only after dashboard shell commit and clean git status.
Do not implement AI/Gemini, lessons, quizzes, code execution, leaderboard, Docker, CI/CD, deployment, or Phase 2 features unless the next scoped task explicitly says so.
```

## Update Protocol After Every Feature
1. Update Current Status: phase, current module, last completed feature, next feature, latest commit, and test status.
2. Tick the completed feature only after code compiles and manual testing is done.
3. Add a Feature History row with files changed, tests, and commit message.
4. Add bugs to Bugs / Issues immediately. Do not hide failing tests.
5. Add manual verification steps and result in Manual Verification Log.
6. Paste the next exact task into Next Chat Prompt before starting a new chat.
7. If Codex made assumptions, record them in Feature History or Bugs / Issues.
8. Commit only after automated tests and manual smoke test pass, or after blocker is clearly documented.
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
- [ ] Automated tests pass using Maven Wrapper for backend tasks.
- [ ] Frontend build passes for frontend tasks.
- [ ] Manual/API smoke test passes for the exact feature, or blocker is documented clearly.
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

Current module: Dashboard
Last completed feature: Dashboard shell
Current feature status: Dashboard shell implemented, frontend build passed, pending commit
Next task: Commit dashboard shell if pending, confirm git status clean, then select Course generation only
Latest completed commit: a2ca3d4 docs: record protected routes completion
Pending commit: Dashboard shell
Git status: modified files pending commit for dashboard shell
Tests passed: Frontend npm run build PASS; backend tests not required for frontend-only task
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
- GET /api/user/profile implemented.
- Endpoint requires JWT authentication.
- Endpoint uses CurrentUserPrincipal from SecurityContext.
- Endpoint does not accept userId from params, body, or path.
- UserProfileResponse returns safe fields: userId, name, email, rank, xp, streak, goal, avatarUrl, createdAt.
- UserProfileResponse does not expose passwordHash, password_hash, tokenHash, refreshToken, role, or raw password.
- UserControllerTest uses real register -> login -> JWT -> /api/user/profile flow.
- UserServiceTest and UserControllerTest pass.
- No update profile endpoint implemented.

Important completed Frontend auth details:
- Login page implemented.
- Register page implemented.
- App.jsx uses React state navigation only.
- No React Router added.
- No package.json or package-lock.json changes.
- No backend files touched.
- authApi.js uses VITE_API_BASE_URL or fallback http://localhost:8080.
- tokenStorage.js stores accessToken and refreshToken in localStorage for MVP.
- Login saves accessToken and refreshToken.
- Register does not save tokens.
- Frontend build passed with npm run build.
- Commit `891476c feat: add frontend auth pages` was pushed to main.
- Manual backend-connected smoke test not completed because local backend datasource is not configured.
- Dashboard, profile UI, logout UI, token rotation, and Phase 2 features are not implemented yet.

Important completed Protected routes details:
- App.jsx wired with React state navigation only.
- authApi.js includes getCurrentUserProfile(accessToken) function.
- authState.js includes isAuthenticated() and getStoredAuthSnapshot() functions.
- Protected Area implemented as MVP protected view, not final dashboard.
- Protected Area checks local accessToken presence.
- Protected Area loads profile via GET /api/user/profile with Bearer token.
- Protected Area shows safe fields: name, email, rank, xp, streak.
- Protected Area does not show accessToken, refreshToken, or sensitive fields.
- No React Router added.
- No package.json or package-lock.json changes.
- No backend files touched.
- No real dashboard implemented.
- No logout UI implemented.
- No token refresh retry or rotation implemented.
- Frontend build passed with npm run build.
- Commit `c607568 feat: add protected routes` was pushed to main.
- Manual backend-connected profile load smoke test not completed because local backend datasource is not configured.
- Dashboard implementation, course, AI, leaderboard, Docker, CI/CD, deployment, and Phase 2 features are not implemented yet.

Important completed Dashboard shell details:
- DashboardShell.jsx is implemented as a static MVP dashboard shell only.
- App.jsx wires DashboardShell using React state navigation only.
- Dashboard shell can be opened from Protected Area even if profile is not loaded.
- Dashboard shell receives profile from App.jsx props only.
- Dashboard shell does not fetch data.
- Dashboard shell does not read accessToken or refreshToken.
- Dashboard shell shows safe profile fields only: name, email, rank, xp, streak.
- No React Router added.
- No backend files touched.
- No package.json or package-lock.json changes.
- No logout UI implemented.
- No course generation, AI/Gemini, XP/streak logic, leaderboard, or code execution implemented.
- Frontend build passed with npm run build.
- Dashboard shell changes are pending commit.
- Local browser-only manual dashboard shell smoke check is still pending.

Testing notes:
- Always use Maven Wrapper only for backend:
  cd backend
  .\mvnw.cmd test
- If stale compiled class issues appear, run:
  cd backend
  .\mvnw.cmd clean test
- For frontend tasks:
  cd frontend
  npm run build
- Frontend protected routes build result:
  npm run build PASS

Runtime note:
- `cd backend && .\mvnw.cmd spring-boot:run` currently requires local datasource configuration.
- Without datasource URL/profile it fails with `Failed to determine suitable jdbc url`.
- This is not an API alignment, frontend auth, protected routes, or dashboard shell bug.

Rules:
Follow master blueprint, Core Rules, DB Schema, API Contracts, Feature Prompts, Build Log, and AGENTS.md.
Do not redesign anything.
Do not add Phase 2 features.
Do not start the next feature while current feature changes are uncommitted.
Use Maven Wrapper only for backend.
```
