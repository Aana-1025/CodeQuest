# CodeQuest Build Log

## Purpose
This file solves the long-chat slowdown problem. Update it manually after every feature so a fresh ChatGPT/Codex chat can continue from the current state without needing the full conversation history.

## Current Status
Phase: MVP
Current module: Course Generation / Frontend Integration
Current feature: Frontend course generation UI completed, committed, and pushed
Last completed feature: Frontend course generation UI
Next feature: GeminiService + PromptBuilder foundation only, but do not start until git status is clean after this docs update commit
Current branch: main
Latest commit: 3ba0ef8 feat: add frontend course generation UI
Test status: Frontend `cd frontend && npm run build` PASS; browser manual test PASS for Dashboard Shell course generation; browser cache-hit UI test PASS; backend `cd backend && .\mvnw.cmd test` last PASS with 53 tests during course generation foundation; local backend runtime PASS with PostgreSQL env vars; Flyway V3 applied successfully
Git status: clean after frontend course generation UI commit and push; pending docs-only Build Log update until this file is committed

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
- [x] Local backend runtime config / dev database setup
- [x] Local frontend-backend CORS
- [x] Course generation foundation
- [x] Frontend course generation UI
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
- Dashboard shell is implemented as a static MVP page.
- Dashboard shell uses React state navigation only.
- React Router was not added.
- Dashboard shell receives profile from App.jsx as props only.
- Dashboard shell does not fetch data on page load.
- Dashboard shell does not read or show accessToken or refreshToken.
- Dashboard shell shows safe profile fields only: name, email, rank, xp, streak.
- Dashboard shell now includes frontend course generation UI wired to backend POST `/api/courses/generate`.
- Dashboard shell course generation UI calls backend only when the user clicks Generate Course.
- Dashboard shell course generation UI does not store course result in localStorage.
- Dashboard shell course generation UI does not show accessToken, refreshToken, password, passwordHash, tokenHash, role, or sensitive fields.
- Local PostgreSQL 17 is installed for development.
- Local database `codequest` was created with PostgreSQL user `postgres`.
- Local backend runtime works when these environment variables are set:
  - `DATABASE_URL=jdbc:postgresql://localhost:5432/codequest`
  - `DATABASE_USERNAME=postgres`
  - `DATABASE_PASSWORD=<local postgres password>`
  - `JWT_SECRET=dev-only-change-this-secret-dev-only-change-this-secret`
- Flyway successfully applies V1, V2, and V3 migrations against local PostgreSQL.
- Running `.\mvnw.cmd spring-boot:run` without configured datasource environment variables still fails because the default profile has no datasource URL. This is expected and not a feature bug.
- Local frontend-backend CORS is configured in Spring Security.
- CORS allows only local Vite development origins:
  - `http://localhost:5173`
  - `http://localhost:5174`
  - `http://127.0.0.1:5173`
  - `http://127.0.0.1:5174`
- CORS does not use wildcard `"*"`.
- CORS does not enable credentials because the MVP frontend uses Bearer tokens/localStorage, not cookies.
- Spring Security remains enabled.
- Public auth endpoints remain public.
- Protected endpoints remain protected.
- Preflight `OPTIONS` requests are permitted.
- Browser register/login/profile manual smoke test passed after CORS fix.
- Course generation foundation is implemented as a deterministic placeholder/cache backend foundation.
- Course generation foundation endpoint is POST `/api/courses/generate`.
- POST `/api/courses/generate` is authenticated and protected.
- POST `/api/courses/generate` uses `@AuthenticationPrincipal CurrentUserPrincipal`; it does not accept user id from request path, params, or body.
- Course generation foundation creates/returns courses by `normalizedTopic + difficulty`.
- Course generation foundation normalizes topic by trim, lowercase, and whitespace collapse.
- Course generation foundation cache behavior:
  - first request creates placeholder course and returns `cacheHit=false`
  - later same topic/difficulty with different casing/spaces returns same course and `cacheHit=true`
- Course generation foundation creates exactly 3 placeholder levels:
  1. Introduction to `<Title>` with 50 XP
  2. Practice `<Title>` with 75 XP
  3. `<Title>` Boss Challenge with 100 XP and `isBoss=true`
- Course generation foundation persists `totalXp=225`.
- Course generation foundation uses `sourceType=PLACEHOLDER`.
- Course generation foundation does not call Gemini.
- Course generation foundation does not call any external API.
- Course generation foundation does not implement PromptBuilder, ResponseParser, real AI lessons, quizzes, flashcards, notes, XP/rank/streak progress, leaderboard, Piston/code execution, Docker, CI/CD, deployment, or Phase 2 features.
- V3 Flyway migration creates `courses` and `levels` tables.
- `courses(normalized_topic, difficulty)` is unique for cache behavior.
- `levels(course_id, order_number)` is unique for ordered levels.
- Frontend course generation UI is implemented in DashboardShell.
- Frontend course generation API helper is implemented in `frontend/src/services/courseApi.js`.
- Frontend course generation UI fields:
  - topic input
  - difficulty dropdown: BEGINNER, INTERMEDIATE, ADVANCED
  - optional goal input
  - Generate Course button
- Frontend course generation result UI shows:
  - title
  - description
  - cache hit / new placeholder course badge
  - course id in muted text
  - ordered level cards
  - level order number, title, XP reward, and Boss/Standard badge
- Frontend course generation UI does not implement real course map navigation, lesson page, quizzes, flashcards, notes, XP/rank/streak progress, leaderboard, code execution, Gemini/AI, PromptBuilder, ResponseParser, Docker, CI/CD, deployment, or Phase 2 features.
- Next feature should be GeminiService + PromptBuilder foundation only, after this docs update is committed and git status is clean.
- Do not combine Gemini/AI with frontend course map, quizzes, lessons, leaderboard, Docker, CI/CD, deployment, code execution, or Phase 2 features unless explicitly scoped.

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
- Frontend auth pages note: Manual backend-connected smoke test was initially blocked by local backend/CORS setup, then passed after local PostgreSQL runtime setup and CORS fix.
- Protected routes note: Protected Area implemented as a simple MVP protected view, not the final dashboard.
- Protected routes note: Commit `c607568 feat: add protected routes` was pushed to `main`, and git status was clean afterward.
- Protected routes note: Manual backend-connected profile load smoke test was initially blocked by local backend/CORS setup, then passed after local PostgreSQL runtime setup and CORS fix.
- Dashboard shell note: DashboardShell was first implemented as a static UI shell only.
- Dashboard shell note: Commit `3abf231 feat: add dashboard shell` was pushed to `main`, and git status was clean afterward.
- Dashboard shell note: Static shell had no backend/API behavior at first.
- Dashboard shell note: Opening Dashboard Shell may show `Profile not loaded yet` depending on current in-memory profile state. This is acceptable for shell/profile state work and can be polished later.
- Runtime database config note: A manual `.\mvnw.cmd spring-boot:run` initially failed because no active profile was set and no datasource URL was configured.
- Runtime database config note: PostgreSQL 17 was installed, `psql` was added to PATH for the active terminal, and database `codequest` was created.
- Runtime database config note: Backend runtime then started successfully with PostgreSQL env vars and Flyway applied V1/V2 migrations.
- Frontend manual testing note: Browser register initially showed `Failed to fetch` because CORS was not configured for Vite origin `http://localhost:5174`.
- CORS note: Commit `8da4448 fix: allow local frontend cors` was pushed to `main`.
- CORS note: CORS allows local Vite origins only and does not use wildcard `"*"`.
- CORS note: Backend tests passed after CORS fix: 44 tests, 0 failures, 0 errors, 0 skipped.
- CORS note: Browser register/login/protected profile smoke test passed after CORS fix.
- Course generation foundation note: Commit `e4734e2 feat: add course generation foundation` was pushed to `main`, and git status was clean afterward.
- Course generation foundation note: V3 migration creates `courses` and `levels` tables. V1 and V2 were not edited.
- Course generation foundation note: Backend tests passed with 53 tests, 0 failures, 0 errors, 0 skipped.
- Course generation foundation note: Local backend runtime applied V3 successfully against PostgreSQL 17.9.
- Course generation foundation note: Manual API smoke test passed for authenticated POST `/api/courses/generate`.
- Course generation foundation note: Manual cache test passed: repeated normalized topic/difficulty returned same `courseId` with `cacheHit=true`.
- Course generation foundation note: No Gemini/AI, external API, PromptBuilder, ResponseParser, real lesson generation, quizzes, flashcards, notes, XP/streak/progress, leaderboard, Piston/code execution, frontend, Docker, CI/CD, or deployment was implemented.
- Course generation foundation note: A real local PostgreSQL password was typed in terminal during manual testing. Do not commit real passwords or include them in docs; use `<your-local-postgres-password>` placeholder only.
- Frontend course generation UI note: Commit `3ba0ef8 feat: add frontend course generation UI` was pushed to `main`, and git status was clean afterward.
- Frontend course generation UI note: Frontend build passed with `cd frontend && npm run build`.
- Frontend course generation UI note: Browser manual test passed on Vite port 5174.
- Frontend course generation UI note: Dashboard Shell generated/displayed a course from browser and showed 3 levels with XP and Boss/Standard badges.
- Frontend course generation UI note: Browser cache-hit UI test passed for `Binary Search`, showing `Cache Hit` and existing course/levels.
- Frontend course generation UI note: No backend files, package files, React Router, Gemini/AI, quizzes, flashcards, notes, XP/streak/progress, leaderboard, code execution, Docker, CI/CD, deployment, or Phase 2 features were implemented.
- Frontend course generation UI note: Browser test first used a mistyped topic `Binary Searcj`; this created a correctly matching placeholder for that typo. This was input behavior, not a code bug.

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
| 18 | 2026-05-05 | Dashboard shell | Dashboard | App.jsx, DashboardShell.jsx, CodeQuest_Build_Log.md | Frontend `cd frontend && npm run build` PASS | `3abf231 feat: add dashboard shell`. Implemented static dashboard shell page and wired it from Protected Area using React state navigation. No React Router, backend/API calls, course generation, AI/Gemini, XP/streak logic, leaderboard, logout UI, code execution, package changes, or backend changes. Commit pushed; git status clean. |
| 19 | 2026-05-05 | Local backend runtime config + frontend CORS | Local Runtime / Security | SecurityConfig, application.yml, SecurityConfigTest, test application.yml | Backend `cd backend && .\mvnw.cmd test` PASS; 44 tests total. Local backend runtime PASS with PostgreSQL env vars. Browser register/login/profile smoke test PASS after CORS fix. | `8da4448 fix: allow local frontend cors`. Installed local PostgreSQL 17, created `codequest` database, ran backend with env vars, Flyway applied V1/V2 migrations, fixed CORS for local Vite origins 5173/5174. No frontend, package, DB migration, course, AI, dashboard, or business logic changes. Commit pushed; git status clean. |
| 20 | 2026-05-05 | Build Log update after local runtime + CORS | Docs | CodeQuest_Build_Log.md | Git status clean after docs commit | `75fa636 docs: record local runtime and cors completion` |
| 21 | 2026-05-05 | Course generation foundation | Course Generation | V3 migration, Course entity, CourseRepository, CourseDifficulty, CourseSourceType, Level entity, LevelRepository, GenerateCourseRequest, GenerateCourseResponse, CourseLevelSummaryResponse, CourseService, CourseController, CourseRepositoryTest, CourseServiceTest, CourseControllerTest | Backend `cd backend && .\mvnw.cmd test` PASS; 53 tests total. Local runtime PASS with Flyway V3 applied. Manual API smoke test PASS. Manual cache-hit test PASS. | `e4734e2 feat: add course generation foundation`. Added authenticated POST `/api/courses/generate`, deterministic placeholder course generation, normalized topic/difficulty cache, 3 placeholder levels, V3 courses/levels schema. No frontend, Gemini/AI, external API, PromptBuilder, ResponseParser, quizzes, flashcards, notes, progress, leaderboard, code execution, Docker, CI/CD, deployment, or Phase 2 features. Commit pushed; git status clean. |
| 22 | 2026-05-05 | Build Log update after Course generation foundation | Docs | CodeQuest_Build_Log.md | Git status clean after docs commit | `8d66948 docs: record course generation foundation completion` |
| 23 | 2026-05-05 | Frontend course generation UI | Course Generation / Frontend | DashboardShell.jsx, courseApi.js | Frontend `cd frontend && npm run build` PASS. Browser manual generate-course test PASS. Browser cache-hit UI test PASS. | `3ba0ef8 feat: add frontend course generation UI`. Added DashboardShell form for topic/difficulty/goal, wired POST `/api/courses/generate` with Bearer token, displayed generated course and level cards. No backend, package files, React Router, Gemini/AI, quizzes, flashcards, notes, XP/streak/progress, leaderboard, code execution, Docker, CI/CD, deployment, or Phase 2 features. Commit pushed; git status clean. |

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
| 2026-05-04 | `cd backend && .\mvnw.cmd spring-boot:run` | FAIL | Runtime startup failed because no active profile was set and no datasource URL was configured. Error: `Failed to determine suitable jdbc url`. This was a local runtime DB configuration issue, not a user profile API alignment failure. | Yes; fixed by installing PostgreSQL, creating `codequest` DB, and running with env vars. |
| 2026-05-05 | `cd frontend && npm run build` | PASS | Frontend build succeeded with authApi.js, tokenStorage.js, Register.jsx, Login.jsx, and App.jsx wiring. No errors, no warnings. | Yes |
| 2026-05-05 | `cd frontend && npm run build` | PASS | Frontend build succeeded with protected view, authState.js, and getCurrentUserProfile helper. | Yes |
| 2026-05-05 | `cd frontend && npm run build` | PASS | Frontend build succeeded with DashboardShell.jsx and App.jsx dashboard wiring. | Yes |
| 2026-05-05 | `psql -U postgres -W -c "CREATE DATABASE codequest;"` | PASS | Local PostgreSQL database `codequest` created successfully. | Yes |
| 2026-05-05 | `cd backend && .\mvnw.cmd spring-boot:run` with DATABASE_URL/DATABASE_USERNAME/DATABASE_PASSWORD/JWT_SECRET env vars | PASS | Backend started on port 8080. Hikari connected to PostgreSQL 17.9. Flyway validated and applied 2 migrations, schema now at v2. | Yes |
| 2026-05-05 | `curl http://localhost:8080/api/health` | PASS | Backend returned 200 with `{"status":"UP","service":"CodeQuest Backend"}`. | Yes |
| 2026-05-05 | Browser register before CORS fix | FAIL | Browser blocked request from Vite origin `http://localhost:5174` to backend `http://localhost:8080` due missing `Access-Control-Allow-Origin`. | Yes; fixed by CORS config. |
| 2026-05-05 | `cd backend && .\mvnw.cmd test` after CORS fix | PASS | Backend tests passed: 44 tests, 0 failures, 0 errors, 0 skipped. Added CORS preflight test for local Vite origin. | Yes |
| 2026-05-05 | Browser register/login/profile after CORS fix | PASS | Browser register/login worked, Protected Area opened, `Load my profile` returned safe fields. | Yes |
| 2026-05-05 | `cd backend && .\mvnw.cmd test` after Course DB/entity/repository foundation | PASS | Backend tests passed: 45 tests, 0 failures, 0 errors. Added CourseRepositoryTest. | Yes |
| 2026-05-05 | `cd backend && .\mvnw.cmd test` after CourseService + DTOs | PASS | Backend tests passed: 48 tests, 0 failures, 0 errors. Added CourseServiceTest. | Yes |
| 2026-05-05 | `cd backend && .\mvnw.cmd test` after CourseController/API endpoint | PASS | Backend tests passed: 53 tests, 0 failures, 0 errors. Added CourseControllerTest with authenticated success, 401 missing token, 400 invalid topic, 400 missing difficulty, and cache-hit flow. | Yes |
| 2026-05-05 | `cd backend && .\mvnw.cmd spring-boot:run` after Course foundation with placeholder password literal | FAIL | Runtime failed with `FATAL: password authentication failed for user "postgres"` because placeholder `<your-local-postgres-password>` was used literally instead of the real local PostgreSQL password. | Yes; fixed by setting real local password in env var. Do not commit real password. |
| 2026-05-05 | `cd backend && .\mvnw.cmd spring-boot:run` after setting real local DB password | PASS | Backend started on port 8080. Flyway validated 3 migrations and applied V3 to local PostgreSQL, schema now at v3. | Yes |
| 2026-05-05 | Manual API: register/login then POST `/api/courses/generate` | PASS | Authenticated request returned 200 with courseId, title `Binary Search`, description, `cacheHit=false`, and 3 levels with XP 50/75/100. | Yes |
| 2026-05-05 | Manual API cache test: POST `/api/courses/generate` with `"  BINARY   SEARCH  "` | PASS | Returned same courseId and `cacheHit=true`. | Yes |
| 2026-05-05 | `cd frontend && npm run build` after frontend course generation UI | PASS | Frontend build succeeded with DashboardShell course generation UI and courseApi.js. Vite transformed 38 modules and built successfully. | Yes |
| 2026-05-05 | Browser UI: DashboardShell generate course | PASS | Browser generated and displayed a course from DashboardShell with title, description, course id, 3 level cards, XP rewards, and Boss/Standard badges. | Yes |
| 2026-05-05 | Browser UI: DashboardShell cache-hit course generation | PASS | Browser generated existing `Binary Search` course and displayed `Cache Hit` with levels visible. | Yes |

## Manual Verification Log
| Date | Feature | Manual/API check | Expected result | Status |
|---|---|---|---|---|
| 2026-05-03 | Backend health endpoint | GET `/api/health` | 200 OK with backend health response | Passed during feature task |
| 2026-05-03 | Auth register | POST `/api/auth/register` with valid name, email, password | 201 Created with userId, name, email, rank BEGINNER, xp 0; no passwordHash | Automated tests passed; browser smoke test passed after local runtime + CORS fix |
| 2026-05-03 | Auth register duplicate email | POST `/api/auth/register` again with same email | 409 Conflict with standard ErrorDTO and EMAIL_ALREADY_EXISTS | Recommended for future manual API pass |
| 2026-05-03 | Auth register invalid password | POST `/api/auth/register` with weak password | 400 Bad Request with standard ErrorDTO and VALIDATION_ERROR | Browser showed 400 Invalid request for weak/invalid password; strong password worked |
| 2026-05-03 | Auth login | POST `/api/auth/login` with valid registered email and correct password | 200 OK with userId, name, email, rank BEGINNER, xp 0, streak 0, accessToken, refreshToken, tokenType, expiresInSeconds | Browser smoke test passed after local runtime + CORS fix |
| 2026-05-03 | Auth login wrong password | POST `/api/auth/login` with registered email and wrong password | 401 Unauthorized with standard ErrorDTO and INVALID_CREDENTIALS | Recommended for future manual API pass |
| 2026-05-03 | Auth login unknown email | POST `/api/auth/login` with unregistered email | 401 Unauthorized with standard ErrorDTO and INVALID_CREDENTIALS | Recommended for future manual API pass |
| 2026-05-04 | JWT authentication | POST `/api/auth/login` with valid credentials | 200 OK with userId, name, email, rank, xp, streak, accessToken, tokenType Bearer, expiresInSeconds | Browser login smoke test passed after local runtime + CORS fix |
| 2026-05-04 | JWT protected endpoint without token | Request any protected endpoint without Authorization header | 401 Unauthorized with standard ErrorDTO | Automated tests passed; manual API check recommended later |
| 2026-05-04 | JWT public health endpoint | GET `/api/health` without token | 200 OK | Passed with local backend runtime |
| 2026-05-04 | Refresh token login response | POST `/api/auth/login` with valid credentials | 200 OK with accessToken and refreshToken; no passwordHash, password_hash, role, or tokenHash | Browser login smoke test passed; detailed token response manual API check recommended later |
| 2026-05-04 | Refresh token endpoint | POST `/api/auth/refresh` with valid refreshToken | 200 OK with new accessToken, tokenType Bearer, expiresInSeconds | Automated tests passed; manual API smoke test recommended later |
| 2026-05-04 | Invalid refresh token | POST `/api/auth/refresh` with invalid refreshToken | 401 Unauthorized with standard ErrorDTO and INVALID_REFRESH_TOKEN | Automated tests passed; manual API smoke test recommended later |
| 2026-05-04 | Missing refresh token | POST `/api/auth/refresh` with blank/missing refreshToken | 400 Bad Request with standard ErrorDTO and VALIDATION_ERROR | Automated tests passed; manual API smoke test recommended later |
| 2026-05-04 | Logout / token revoke | POST `/api/auth/logout` with valid refreshToken from login | 200 OK with safe success message; refresh token row has `revokedAt` set | Automated service tests passed; manual API smoke test recommended later |
| 2026-05-04 | Refresh after logout | POST `/api/auth/refresh` using same refreshToken after logout | 401 Unauthorized with standard ErrorDTO and INVALID_REFRESH_TOKEN | Automated service tests passed; manual API smoke test recommended later |
| 2026-05-04 | Logout safety | Inspect logout response | Response must not include tokenHash, raw token, passwordHash, password_hash, role, or internal user data | Automated service tests passed; manual API smoke test recommended later |
| 2026-05-04 | User profile with token before API alignment | GET `/api/users/me` with valid JWT access token | 200 OK with userId, name, email, rank, xp, streak, goal, avatarUrl, createdAt | Automated integration test passed before API alignment |
| 2026-05-04 | User profile without token before API alignment | GET `/api/users/me` without Authorization header | 401 Unauthorized | Automated integration test passed before API alignment |
| 2026-05-04 | User profile safety before API alignment | Inspect GET `/api/users/me` response | Response must not include passwordHash, password_hash, tokenHash, refreshToken, role, or raw password | Automated integration test passed before API alignment |
| 2026-05-04 | User profile API alignment with token | GET `/api/user/profile` with valid JWT access token | 200 OK with userId, name, email, rank, xp, streak, goal, avatarUrl, createdAt | Browser Protected Area `Load my profile` passed after local runtime + CORS fix |
| 2026-05-04 | User profile API alignment without token | GET `/api/user/profile` without Authorization header | 401 Unauthorized | Automated integration test passed; manual API check recommended later |
| 2026-05-04 | User profile API alignment safety | Inspect GET `/api/user/profile` response | Response must not include passwordHash, password_hash, tokenHash, refreshToken, role, or raw password | Browser profile showed safe fields only: name, email, rank, XP, streak |
| 2026-05-04 | Backend runtime startup before local DB setup | `cd backend && .\mvnw.cmd spring-boot:run` | Backend starts only if datasource URL/profile is configured | Initially failed due missing local datasource URL |
| 2026-05-05 | Local PostgreSQL setup | Install PostgreSQL 17, add psql to current PATH, create `codequest` database | PostgreSQL accepts connection and `CREATE DATABASE codequest` succeeds | Passed |
| 2026-05-05 | Backend runtime startup after local DB setup | `cd backend && .\mvnw.cmd spring-boot:run` with env vars | Backend starts on port 8080 and Flyway applies migrations | Passed |
| 2026-05-05 | Backend health after runtime setup | GET `/api/health` | 200 OK with `{"status":"UP","service":"CodeQuest Backend"}` | Passed |
| 2026-05-05 | Frontend auth pages before CORS fix | Register/login UI backend-connected smoke test | Register and login forms call backend, login saves tokens | Initially failed due CORS from `http://localhost:5174` |
| 2026-05-05 | Frontend auth pages after CORS fix | Register and login in browser | Register works with strong password; login works and frontend proceeds to Protected Area | Passed |
| 2026-05-05 | Protected routes after CORS fix | Protected Area backend-connected profile load smoke test | Logged-in user can open Protected Area and load safe profile fields from GET `/api/user/profile` | Passed |
| 2026-05-05 | Dashboard shell | Dashboard shell UI smoke check | User can open Dashboard Shell from Protected Area and see static cards/placeholders without backend calls | Passed; DashboardShell may show profile fallback depending on profile state |
| 2026-05-05 | Local frontend-backend CORS | Browser preflight from Vite origin `http://localhost:5174` to backend `http://localhost:8080` | No CORS block; register/login/profile browser requests reach backend | Passed after `8da4448 fix: allow local frontend cors` |
| 2026-05-05 | Course generation foundation runtime migration | Start backend with local PostgreSQL env vars after V3 migration added | Flyway validates 3 migrations and applies V3; backend starts on port 8080 | Passed |
| 2026-05-05 | Course generation foundation success | Register/login user, then POST `/api/courses/generate` with Bearer token and topic `Binary Search`, difficulty `BEGINNER` | 200 OK with courseId, title, description, `cacheHit=false`, and 3 ordered levels | Passed |
| 2026-05-05 | Course generation foundation cache | Repeat POST `/api/courses/generate` with topic `"  BINARY   SEARCH  "` and same difficulty | Same courseId as first request and `cacheHit=true` | Passed |
| 2026-05-05 | Course generation foundation protected endpoint | POST `/api/courses/generate` without token | 401 Unauthorized | Automated controller test passed |
| 2026-05-05 | Course generation foundation validation | POST `/api/courses/generate` with invalid topic or missing difficulty | 400 Bad Request | Automated controller test passed |
| 2026-05-05 | Course generation foundation safety | Inspect response | Response contains course/level fields only; no user password, token, tokenHash, role, or sensitive fields | Passed by response shape/manual inspection |
| 2026-05-05 | Frontend course generation UI build | `cd frontend && npm run build` | Vite build succeeds | Passed |
| 2026-05-05 | Frontend course generation UI success | In browser, open Dashboard Shell, enter topic/difficulty/goal, click Generate Course | Generated course appears with title, description, course id, 3 level cards, XP rewards, and Boss/Standard badges | Passed |
| 2026-05-05 | Frontend course generation UI cache | In browser, generate existing `Binary Search` course again | UI shows `Cache Hit`, course title and levels remain visible | Passed |
| 2026-05-05 | Frontend course generation UI safety | Inspect DashboardShell result UI | UI does not show accessToken, refreshToken, password, passwordHash, tokenHash, role, or sensitive backend data | Passed |

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
   - For frontend tasks, test the exact browser flow.
   - Test one success case.
   - Test one important failure/cache case where practical.
   - Confirm the response shape/UI matches API contracts.
   - Confirm standard ErrorDTO appears for backend errors where practical.
   - Confirm sensitive fields are not leaked.
   - If `spring-boot:run` fails because local datasource variables are missing, record the runtime config issue separately and do not mix it with the feature implementation unless the task is explicitly database runtime setup.

6. Only after tests and practical manual smoke checks pass:
   - update this Build Log
   - commit changes
   - confirm clean git status

7. Do not start the next feature while current feature changes are uncommitted.

## Local Backend Runtime Setup Commands
Use these commands to run the backend locally on Windows after PostgreSQL installation.

Temporarily add PostgreSQL 17 psql to current PowerShell PATH if needed:
```powershell
$env:Path += ";C:\Program Files\PostgreSQL\17\bin"
psql --version
```

Create local database if it does not exist:
```powershell
psql -U postgres -W -c "CREATE DATABASE codequest;"
```

From repo root, start backend with local PostgreSQL env vars:
```powershell
$env:DATABASE_URL="jdbc:postgresql://localhost:5432/codequest"
$env:DATABASE_USERNAME="postgres"
$env:DATABASE_PASSWORD="<your-local-postgres-password>"
$env:JWT_SECRET="dev-only-change-this-secret-dev-only-change-this-secret"

cd backend
.\mvnw.cmd spring-boot:run
```

Expected backend runtime success:
```text
Tomcat started on port 8080
Started CodeQuestApplication
```

Expected Flyway behavior after Course generation foundation:
```text
Successfully validated 3 migrations
Successfully applied V3 if not already applied
Schema "public" now at version v3
```

Health check from another PowerShell:
```powershell
curl http://localhost:8080/api/health
```

Expected health response:
```json
{"status":"UP","service":"CodeQuest Backend"}
```

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

PowerShell register request:
```powershell
$body = @{
  name = "Antara"
  email = "antara@example.com"
  password = "StrongPass123"
} | ConvertTo-Json

Invoke-WebRequest -UseBasicParsing `
  -Uri "http://localhost:8080/api/auth/register" `
  -Method POST `
  -ContentType "application/json" `
  -Body $body
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
- Login, JwtService, JWT filter, refresh token, logout, frontend auth, dashboard, CORS, and course generation were separate tasks.
- Frontend auth pages are implemented.
- Protected routes are implemented.
- Dashboard shell is implemented.
- Local frontend-backend CORS is implemented.
- Course generation foundation is implemented.
- Frontend course generation UI is implemented.

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
- Dashboard shell is implemented.
- Local frontend-backend CORS is implemented.
- Course generation foundation is implemented.
- Frontend course generation UI is implemented.
- Token rotation is still not implemented.

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
- Course generation uses authenticated JWT principal via `CurrentUserPrincipal`.
- Course generation endpoint is POST `/api/courses/generate`.
- Frontend auth pages are implemented.
- Protected routes are implemented.
- Dashboard shell is implemented.
- Local frontend-backend CORS is implemented.
- Frontend course generation UI is implemented.
- No access-token blacklist implemented.
- No token rotation implemented yet.

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
- Course generation foundation is implemented as POST `/api/courses/generate`.
- Frontend auth pages are implemented.
- Protected routes are implemented.
- Dashboard shell is implemented.
- Local frontend-backend CORS is implemented.
- Frontend course generation UI is implemented.
- Token rotation is not implemented yet.

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
- Dashboard shell is implemented.
- Local frontend-backend CORS is implemented.
- Course generation foundation is implemented.
- Frontend course generation UI is implemented.
- Profile edit UI is not implemented yet.

## Course Generation Foundation Manual Test Commands
Use these after starting the backend with a configured local datasource/profile.

Start backend:
```powershell
$env:DATABASE_URL="jdbc:postgresql://localhost:5432/codequest"
$env:DATABASE_USERNAME="postgres"
$env:DATABASE_PASSWORD="<your-local-postgres-password>"
$env:JWT_SECRET="dev-only-change-this-secret-dev-only-change-this-secret"

cd backend
.\mvnw.cmd spring-boot:run
```

Register, login, and generate course:
```powershell
$baseUrl = "http://localhost:8080"
$email = "coursemanual$(Get-Random)@example.com"
$password = "CoursePass123"

$registerBody = @{
  name = "Course Manual"
  email = $email
  password = $password
} | ConvertTo-Json

Invoke-WebRequest -UseBasicParsing `
  -Uri "$baseUrl/api/auth/register" `
  -Method POST `
  -ContentType "application/json" `
  -Body $registerBody

$loginBody = @{
  email = $email
  password = $password
} | ConvertTo-Json

$loginResponse = Invoke-RestMethod `
  -Uri "$baseUrl/api/auth/login" `
  -Method POST `
  -ContentType "application/json" `
  -Body $loginBody

$token = $loginResponse.accessToken

$courseBody = @{
  topic = "Binary Search"
  difficulty = "BEGINNER"
  goal = "DSA interview preparation"
} | ConvertTo-Json

Invoke-RestMethod `
  -Uri "$baseUrl/api/courses/generate" `
  -Method POST `
  -ContentType "application/json" `
  -Headers @{ Authorization = "Bearer $token" } `
  -Body $courseBody
```

Expected first response:
```text
courseId: exists
title: Binary Search
description: A CodeQuest course foundation for Binary Search.
cacheHit: False
levels: 3 items
level 1: Introduction to Binary Search, orderNumber 1, isBoss false, xpReward 50
level 2: Practice Binary Search, orderNumber 2, isBoss false, xpReward 75
level 3: Binary Search Boss Challenge, orderNumber 3, isBoss true, xpReward 100
```

Cache check:
```powershell
$courseBody2 = @{
  topic = "  BINARY   SEARCH  "
  difficulty = "BEGINNER"
  goal = "same"
} | ConvertTo-Json

Invoke-RestMethod `
  -Uri "$baseUrl/api/courses/generate" `
  -Method POST `
  -ContentType "application/json" `
  -Headers @{ Authorization = "Bearer $token" } `
  -Body $courseBody2
```

Expected second response:
```text
same courseId as first response
cacheHit: True
levels remain ordered by orderNumber
```

Missing token check:
```http
POST http://localhost:8080/api/courses/generate
Content-Type: application/json

{
  "topic": "Binary Search",
  "difficulty": "BEGINNER",
  "goal": "DSA"
}
```

Expected:
```text
HTTP 401 Unauthorized
```

Invalid request checks:
```text
blank topic -> HTTP 400 Bad Request
missing difficulty -> HTTP 400 Bad Request
```

Important Course generation foundation boundaries:
- Endpoint is POST `/api/courses/generate`.
- Endpoint requires JWT Bearer token.
- Endpoint must not accept user id from path, query params, or request body.
- Controller must stay thin and delegate to CourseService.
- Service handles normalization, cache lookup, deterministic placeholder creation, and response mapping.
- Current source type is PLACEHOLDER.
- No Gemini/AI is implemented.
- No external API call is implemented.
- No PromptBuilder is implemented.
- No ResponseParser is implemented.
- No real AI-generated lessons are implemented.
- No quiz, flashcard, note, progress, XP/rank/streak, leaderboard, Piston/code execution, Docker, CI/CD, deployment, or Phase 2 features are implemented.

## Frontend Course Generation UI Manual Test Commands
Use these after starting backend and frontend.

Start backend:
```powershell
$env:DATABASE_URL="jdbc:postgresql://localhost:5432/codequest"
$env:DATABASE_USERNAME="postgres"
$env:DATABASE_PASSWORD="<your-local-postgres-password>"
$env:JWT_SECRET="dev-only-change-this-secret-dev-only-change-this-secret"

cd backend
.\mvnw.cmd spring-boot:run
```

Start frontend:
```powershell
cd frontend
npm run dev
```

Open Vite URL:
```text
http://localhost:5173
```

or:
```text
http://localhost:5174
```

Browser flow:
```text
1. Register/login if needed.
2. Open Protected Area.
3. Open Dashboard Shell.
4. In Generate Course form, enter:
   Topic: Binary Search
   Difficulty: BEGINNER
   Goal: DSA interview preparation
5. Click Generate Course.
```

Expected UI:
```text
Generated course appears.
Title appears.
Description appears.
Course ID appears in muted text.
Cache badge appears.
3 level cards appear.
Level 1: Introduction to Binary Search, XP Reward 50, Standard
Level 2: Practice Binary Search, XP Reward 75, Standard
Level 3: Binary Search Boss Challenge, XP Reward 100, Boss
No accessToken or refreshToken is visible.
No CORS error appears.
```

Cache UI check:
```text
Generate the same topic/difficulty again, or use topic with different casing/spaces:
  BINARY   SEARCH
```

Expected:
```text
Cache Hit badge appears.
Course title and levels remain visible.
```

Important Frontend course generation UI boundaries:
- Implemented in DashboardShell only.
- API helper is `frontend/src/services/courseApi.js`.
- Uses existing `API_BASE_URL` from authApi.js.
- Uses access token from existing tokenStorage.
- Sends Authorization Bearer token to POST `/api/courses/generate`.
- Calls backend only when user clicks Generate Course.
- Does not call backend on page load.
- Does not store generated course in localStorage.
- Does not show accessToken or refreshToken.
- Does not add React Router.
- Does not add dependencies.
- Does not touch package files.
- Does not touch backend.
- Does not implement Gemini/AI, PromptBuilder, ResponseParser, real lessons, quizzes, flashcards, notes, XP/streak/progress, leaderboard, code execution, Docker, CI/CD, deployment, or Phase 2 features.

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

If Vite starts on a different port such as 5174, use that URL. CORS currently allows local Vite ports 5173 and 5174.

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
Expected: frontend calls POST /api/auth/login, saves accessToken and refreshToken to localStorage, and returns to Protected Area or home according to current App.jsx flow.
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
- Protected routes are implemented later in row 17.
- Dashboard shell is implemented later in row 18.
- Local frontend-backend CORS is implemented later in row 19.
- Course generation foundation is implemented later in row 21.
- Frontend course generation UI is implemented later in row 23.
- No logout UI was implemented.
- No profile page was implemented.
- Manual browser register/login smoke test now passes after local runtime setup and CORS fix.

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

If Vite starts on `http://localhost:5174`, use that URL.

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
- Dashboard shell is implemented.
- Local frontend-backend CORS is implemented.
- Course generation foundation is implemented.
- Frontend course generation UI is implemented in DashboardShell.
- Logout UI is not implemented.
- Refresh-token retry and token rotation are not implemented.
- Browser profile smoke test now passes after local runtime setup and CORS fix.

## Dashboard Shell Manual Test Commands
Use these after starting the frontend. Opening DashboardShell through the normal login flow requires backend auth to work.

Start frontend:
```powershell
cd frontend
npm run dev
```

Open:
```text
http://localhost:5173
```

If Vite starts on `http://localhost:5174`, use that URL.

Dashboard shell route through UI:
```text
Open Protected Area.
Click Open Dashboard Shell.
Expected: Dashboard shell opens with title "Dashboard", subtitle "Your Java learning command center.", profile summary/fallback, Generate Course form, course progress placeholder, next actions placeholder, and safety/status note.
```

If profile is not loaded or profile state is not passed:
```text
Expected: Dashboard shell shows "Profile not loaded yet."
```

Generate Course from DashboardShell:
```text
Topic: Binary Search
Difficulty: BEGINNER
Goal: DSA interview preparation
Click Generate Course.
```

Expected:
```text
Generated course result appears with title, description, cache badge, course id, and level cards.
```

Important Dashboard shell boundaries:
- Dashboard shell is still MVP UI, not final product dashboard.
- Dashboard shell uses React state navigation only.
- React Router was not added.
- Dashboard shell receives profile from App.jsx props only.
- Dashboard shell does not read or show accessToken or refreshToken.
- Dashboard shell now calls POST `/api/courses/generate` only when user clicks Generate Course.
- Dashboard shell does not implement real course map routing, AI/Gemini, XP/streak logic, leaderboard, logout UI, code execution, Docker, CI/CD, deployment, or Phase 2 features.

## Local Frontend-Backend CORS Manual Test Commands
Use these after starting backend and frontend.

Start backend with env vars:
```powershell
$env:DATABASE_URL="jdbc:postgresql://localhost:5432/codequest"
$env:DATABASE_USERNAME="postgres"
$env:DATABASE_PASSWORD="<your-local-postgres-password>"
$env:JWT_SECRET="dev-only-change-this-secret-dev-only-change-this-secret"

cd backend
.\mvnw.cmd spring-boot:run
```

Start frontend separately:
```powershell
cd frontend
npm run dev
```

Open the Vite URL:
```text
http://localhost:5173
```

or:
```text
http://localhost:5174
```

Browser checks:
```text
1. Register a user with a strong password such as StrongPass123.
2. Login with the same user.
3. Open Protected Area.
4. Click Load my profile.
5. Confirm safe fields appear: name, email, rank, XP, streak.
6. Click Open Dashboard Shell.
7. Generate a course from Dashboard Shell.
```

Expected:
```text
No CORS error in browser console.
No Failed to fetch for register/login/profile/course generation.
```

Important CORS boundaries:
- Allowed origins are limited to local Vite origins 5173 and 5174 on localhost/127.0.0.1.
- Wildcard `"*"` is not used.
- Credentials are not enabled.
- Spring Security remains enabled.
- JWT filter remains active.
- Auth endpoints remain public.
- Protected endpoints remain protected.
- POST `/api/courses/generate` is protected and works from frontend with Bearer token.

## Next Chat Prompt
Paste this into a fresh ChatGPT Project chat whenever the current chat becomes slow or confusing:

```text
Read the project resources and this Build Log.
Continue CodeQuest from the current status.
Do not redesign anything.
Do not implement Phase 2 features.

Current module: Course Generation / Frontend Integration.
Last completed feature: Frontend course generation UI.
Current feature status: Frontend course generation UI completed, committed, and pushed.
Latest completed commit: 3ba0ef8 feat: add frontend course generation UI.
Git status: clean after frontend course generation UI commit and push, except this Build Log may need a docs-only commit if not already committed.

Important completed local runtime details:
- PostgreSQL 17 installed locally.
- Local database `codequest` created.
- Backend starts with:
  DATABASE_URL=jdbc:postgresql://localhost:5432/codequest
  DATABASE_USERNAME=postgres
  DATABASE_PASSWORD=<local postgres password>
  JWT_SECRET=dev-only-change-this-secret-dev-only-change-this-secret
- Flyway applied V1, V2, and V3 migrations to local PostgreSQL.
- Backend health endpoint works on http://localhost:8080/api/health.

Important completed CORS details:
- CORS enabled in Spring Security.
- Allowed origins:
  http://localhost:5173
  http://localhost:5174
  http://127.0.0.1:5173
  http://127.0.0.1:5174
- No wildcard CORS.
- No credentials enabled.
- Preflight OPTIONS permitted.
- Browser register/login/protected profile smoke test passed after CORS fix.
- Browser course generation from DashboardShell works with Bearer token.

Important completed Course generation foundation details:
- V3 migration creates courses and levels tables.
- Course entity, CourseRepository, CourseDifficulty, CourseSourceType are implemented.
- Level entity and LevelRepository are implemented.
- GenerateCourseRequest, GenerateCourseResponse, and CourseLevelSummaryResponse are implemented.
- CourseService implements deterministic placeholder generation and cache behavior.
- CourseController exposes authenticated POST /api/courses/generate.
- Endpoint uses JWT principal and does not accept userId from path, params, or body.
- Cache key is normalizedTopic + difficulty.
- Topic normalization trims, lowercases, and collapses whitespace.
- Cache miss creates PLACEHOLDER course with exactly 3 levels and totalXp 225.
- Cache hit returns same courseId with cacheHit=true.
- Backend tests pass: 53 tests, 0 failures, 0 errors, 0 skipped.
- Manual API smoke test passed.
- Manual cache-hit test passed.
- Commit e4734e2 was pushed to main.

Important completed Frontend course generation UI details:
- `frontend/src/services/courseApi.js` created.
- `generateCourse({ accessToken, topic, difficulty, goal })` calls POST `/api/courses/generate`.
- DashboardShell has Generate Course form with topic, difficulty, optional goal.
- DashboardShell uses existing tokenStorage access token.
- DashboardShell calls backend only when user clicks Generate Course.
- DashboardShell shows loading and error states.
- DashboardShell displays generated course title, description, cache badge, course id, and ordered level cards.
- Level cards show order number, title, XP reward, and Boss/Standard badge.
- No token is shown or stored in UI.
- Frontend build passed.
- Browser manual test passed.
- Browser cache-hit UI test passed.
- Commit `3ba0ef8 feat: add frontend course generation UI` was pushed to main.

Not implemented yet:
- GeminiService
- PromptBuilder
- ResponseParser
- AI validation
- Real AI-generated course content
- Real Course map navigation
- Lesson page
- Flashcards
- Notes
- Quizzes
- XP/rank/streak/progress
- Leaderboard
- Piston/code execution
- Logout UI
- Docker
- CI/CD
- Deployment
- Phase 2 features

Next safest step:
First confirm git status is clean.
Then implement GeminiService + PromptBuilder foundation only.
Do not replace the stable placeholder course generation flow yet.
Do not call real Gemini from tests.
Do not touch frontend.
Do not implement ResponseParser yet unless explicitly scoped separately.
Do not implement real AI course persistence yet.
Do not combine with lessons/quizzes/code execution/leaderboard/deployment.

Give me the next strict Codex prompt with:
- exact scope
- files to touch
- files not to touch
- commands to run
- manual checks
- expected output
- Build Log update plan
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
- [ ] Manual/API/browser smoke test passes for the exact feature, or blocker is documented clearly.
- [ ] Error/cache case is manually checked where practical.
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

Current module: Course Generation / Frontend Integration
Last completed feature: Frontend course generation UI
Current feature status: Frontend course generation UI completed, committed, and pushed
Next task: GeminiService + PromptBuilder foundation only
Latest completed commit: 3ba0ef8 feat: add frontend course generation UI
Git status: clean after frontend course generation UI commit and push, except docs-only Build Log update may be pending if not committed yet
Tests passed: Frontend npm run build PASS; browser course generation UI PASS; browser cache-hit UI PASS; backend .\mvnw.cmd test last PASS with 53 tests during course generation foundation; local runtime PASS; Flyway V3 applied
Known bugs/blockers: None currently.

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
- authApi.js uses VITE_API_BASE_URL or fallback http://localhost:8080.
- tokenStorage.js stores accessToken and refreshToken in localStorage for MVP.
- Login saves accessToken and refreshToken.
- Register does not save tokens.
- Frontend build passed with npm run build.
- Browser register/login smoke test passes after local runtime setup and CORS fix.

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
- No logout UI implemented.
- No token refresh retry or rotation implemented.
- Frontend build passed with npm run build.
- Browser protected profile smoke test passes after local runtime setup and CORS fix.

Important completed Dashboard shell details:
- DashboardShell.jsx is implemented as MVP dashboard shell.
- App.jsx wires DashboardShell using React state navigation only.
- Dashboard shell can be opened from Protected Area.
- Dashboard shell receives profile from App.jsx props only.
- Dashboard shell does not show accessToken or refreshToken.
- Dashboard shell shows safe profile fields only when profile prop is available.
- Dashboard shell now includes Generate Course form.
- Dashboard shell calls POST /api/courses/generate only on button click.
- Dashboard shell displays generated course and levels.
- No React Router added.
- No logout UI implemented.
- No AI/Gemini, XP/streak logic, leaderboard, or code execution implemented.
- Browser DashboardShell course generation smoke test passed.
- Browser cache-hit UI test passed.

Important completed Local runtime / CORS details:
- PostgreSQL 17 installed locally.
- `psql` works after adding `C:\Program Files\PostgreSQL\17\bin` to current PowerShell PATH.
- Local database `codequest` created.
- Backend starts with PostgreSQL env vars:
  DATABASE_URL=jdbc:postgresql://localhost:5432/codequest
  DATABASE_USERNAME=postgres
  DATABASE_PASSWORD=<local postgres password>
  JWT_SECRET=dev-only-change-this-secret-dev-only-change-this-secret
- Flyway applied V1, V2, and V3 migrations to local PostgreSQL.
- Backend health endpoint returns 200.
- Browser initially failed with CORS from Vite origin 5174.
- CORS fixed in SecurityConfig and application.yml.
- Allowed origins: localhost/127.0.0.1 on ports 5173 and 5174.
- No wildcard CORS.
- No credentials enabled.
- Spring Security remains enabled.
- Preflight OPTIONS permitted.
- Browser register/login/profile/course-generation smoke tests pass.

Important completed Course generation foundation details:
- V3 migration creates courses and levels tables.
- Course table includes normalized_topic, title, description, created_by, difficulty, is_public, total_xp, source_type, created_at, updated_at.
- Levels table includes course_id, title, content_markdown, order_number, is_boss, xp_reward, created_at, updated_at.
- courses(normalized_topic, difficulty) is unique.
- levels(course_id, order_number) is unique.
- Course entity, CourseRepository, CourseDifficulty, CourseSourceType implemented.
- Level entity and LevelRepository implemented.
- GenerateCourseRequest, GenerateCourseResponse, CourseLevelSummaryResponse implemented.
- CourseService implements deterministic placeholder generation and cache behavior.
- CourseController exposes authenticated POST /api/courses/generate.
- Endpoint uses JWT principal and does not accept userId from path, params, or body.
- Request fields: topic, difficulty, goal.
- topic validation: not blank, size 2 to 80.
- difficulty validation: not null.
- goal validation: optional max 200.
- Topic normalization trims, lowercases, and collapses whitespace.
- Cache key is normalizedTopic + difficulty.
- Cache miss creates PLACEHOLDER course with exactly 3 levels and totalXp 225.
- Cache hit returns same courseId with cacheHit=true.
- Backend tests passed: 53 tests, 0 failures, 0 errors, 0 skipped.
- Local runtime applied V3 successfully.
- Manual API smoke test passed.
- Manual cache-hit test passed.
- Commit `e4734e2 feat: add course generation foundation` was pushed to main.
- No Gemini/AI/external API implemented.
- No PromptBuilder/ResponseParser implemented.
- No real AI-generated lesson content implemented.

Important completed Frontend course generation UI details:
- `frontend/src/services/courseApi.js` implemented.
- `generateCourse({ accessToken, topic, difficulty, goal })` calls POST /api/courses/generate.
- DashboardShell has form fields for topic, difficulty, and optional goal.
- DashboardShell gets accessToken from existing tokenStorage.
- DashboardShell calls backend only when user clicks Generate Course.
- DashboardShell shows loading and error states.
- DashboardShell displays generated course title, description, cache badge, course id, and level cards.
- Level cards show order number, title, XP reward, and Boss/Standard badge.
- No token is shown or stored in UI.
- No localStorage persistence for generated course.
- Frontend build passed.
- Browser manual test passed.
- Browser cache-hit UI test passed.
- Commit `3ba0ef8 feat: add frontend course generation UI` was pushed to main.
- No backend files touched.
- No package files touched.
- No React Router added.
- No Gemini/AI implemented.

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
- For local backend runtime:
  set DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD, JWT_SECRET, then run:
  cd backend
  .\mvnw.cmd spring-boot:run
- For browser integration:
  start backend on 8080 and frontend on Vite 5173 or 5174.

Runtime note:
- `cd backend && .\mvnw.cmd spring-boot:run` requires datasource env vars.
- Without datasource URL/profile it fails with `Failed to determine suitable jdbc url`.
- This is expected unless env vars or a profile are configured.
- Local PostgreSQL + env var path is confirmed working.
- Do not commit or document real local DB passwords.

Rules:
Follow master blueprint, Core Rules, DB Schema, API Contracts, Feature Prompts, Build Log, and AGENTS.md.
Do not redesign anything.
Do not add Phase 2 features.
Do not start the next feature while current feature changes are uncommitted.
Use Maven Wrapper only for backend.
```