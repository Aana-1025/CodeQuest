# CodeQuest Build Log

## Purpose
This file solves the long-chat slowdown problem. Update it manually after every feature so a fresh ChatGPT/Codex chat can continue from the current state without needing the full conversation history.

## Current Status
Phase: MVP
Current module: AI / Gemini Prompt-Response Compatibility Polish
Current feature: Gemini prompt/response compatibility polish completed, tested, manual fallback verified, pending commit
Last completed feature: Gemini prompt/response compatibility polish for real Gemini AI-success path
Next feature: Investigate real Gemini failure reason using safe diagnostics only, because manual real Gemini run still persisted PLACEHOLDER; do not start until git status is clean after this feature commit
Current branch: main
Latest commit before current pending feature: 5777c2d feat: wire gemini course generation fallback
Pending commit message for current feature: fix: improve gemini response compatibility
Test status: Backend `cd backend && .\mvnw.cmd test` PASS; 74 tests, 0 failures, 0 errors; no frontend changes; no DB migration changes; CourseController unchanged; PromptBuilder stricter parser-aligned schema PASS; GeminiHttpClient fenced/prose JSON sanitization tests PASS; ResponseParser invalid XP rejection PASS; manual browser generation completed for `Dynamic Programming Memoization AI Check`; DB persisted `source_type=PLACEHOLDER`, confirming fallback safety remains but real AI-success persistence is still not manually confirmed
Git status: pending commit for Gemini prompt/response compatibility polish files plus this Build Log update

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
- [x] GeminiService + PromptBuilder
- [x] ResponseParser + AI validation
- [x] GeminiService + ResponseParser course generation wiring with safe fallback
- [x] Gemini prompt/response compatibility polish
- [ ] Real Gemini AI-success manual verification / safe diagnostics
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
- Frontend course generation UI does not implement real course map navigation, lesson page, quizzes, flashcards, notes, XP/rank/streak progress, leaderboard, code execution, Docker, CI/CD, deployment, or Phase 2 features.
- GeminiService + PromptBuilder foundation is implemented in the isolated backend `ai` module.
- GeminiService + PromptBuilder foundation originally did not call Gemini over network and did not wire into `CourseService` or `CourseController`.
- GeminiService + PromptBuilder foundation uses env-backed Gemini placeholders:
  - `GEMINI_API_KEY`
  - `GEMINI_MODEL`
  - `GEMINI_BASE_URL`
- PromptBuilder creates structured course-generation prompts with:
  - topic
  - difficulty
  - optional goal
  - JSON-only instruction
  - schema guidance
  - defensive wording against prompt-injection-style user input
- PromptBuilder must not include secrets, JWTs, passwords, refresh tokens, or private user data.
- ResponseParser + AI validation foundation is implemented in the isolated backend `ai` module.
- ResponseParser parses raw AI JSON into typed AI DTO/record models.
- ResponseParser validates malformed JSON, required fields, enum-like values, array sizes, nested objects, duplicate order numbers, quiz answers, flashcards, coding problems, and XP ranges.
- ResponseParser throws `AiResponseValidationException` for malformed JSON or invalid AI schema/data.
- ResponseParser exception messages must stay safe and must not include secrets or the full raw AI payload.
- ResponseParser does not persist anything directly.
- Current AI response model files include:
  - `AiCourseResponse`
  - `AiLevelResponse`
  - `AiFlashcardResponse`
  - `AiQuizQuestionResponse`
  - `AiCodingProblemResponse`
  - `AiResponseValidationException`
  - `ResponseParser`
- Gemini course generation wiring is implemented behind safe fallback.
- New Gemini integration support files:
  - `GeminiClient`
  - `GeminiHttpClient`
  - `GeminiException`
- `GeminiClient` is a mockable abstraction for Gemini content generation.
- `GeminiHttpClient` performs the real Gemini generate-content request using Spring `RestClient`.
- `GeminiException` is a safe runtime exception for Gemini request/response failures.
- `GeminiService` builds the prompt and delegates to `GeminiClient` only when Gemini config is present.
- `CourseService` attempts Gemini + ResponseParser only on cache miss and only when Gemini config is present.
- Cache hit behavior remains unchanged and must not call Gemini.
- If Gemini config is missing, `CourseService` uses existing placeholder generation.
- If Gemini call fails, `CourseService` uses existing placeholder generation.
- If Gemini output is malformed or rejected by ResponseParser, `CourseService` uses existing placeholder generation.
- If Gemini output parses successfully and matches requested difficulty, `CourseService` persists supported course/level fields with `sourceType=AI`.
- Current persistence boundary for AI-generated output is only:
  - course title
  - course description
  - difficulty
  - level title
  - level contentMarkdown
  - level orderNumber
  - level isBoss
  - level xpReward
- Flashcards, quizzes, and coding problems from parsed AI output are not persisted yet because their DB tables/features are not implemented.
- Placeholder fallback courses keep `sourceType=PLACEHOLDER`.
- Existing deterministic placeholder levels must remain compatible with frontend.
- `CourseController` remains unchanged.
- Frontend remains unchanged.
- DB migrations remain unchanged.
- Manual real-runtime test with Gemini env vars started backend successfully and browser course generation worked.
- Manual DB result for topic `graph traversal bfs real ai test` showed `source_type=PLACEHOLDER`, confirming safe fallback works.
- Manual real AI-success persistence was not confirmed yet; automated tests cover valid AI response path with mocked Gemini/client output.
- Gemini prompt/response compatibility polish is implemented.
- PromptBuilder now asks Gemini for parser-compatible JSON with:
  - exact difficulty matching the requested value
  - levels array size between 1 and 10
  - required `isBoss`
  - `xpReward` between 1 and 500
  - explicit `flashcards`, `quiz`, and `codingProblems` arrays
  - `correctAnswer` limited to `A`, `B`, `C`, or `D`
  - coding problem difficulty limited to `EASY`, `MEDIUM`, or `HARD`
  - JSON only, no prose, no markdown fences, and no extra keys
- GeminiHttpClient now sanitizes common Gemini output formats before parser validation:
  - trims output
  - strips fenced ```json blocks if present
  - extracts the JSON object from common prose-wrapped output such as "Here is the JSON..."
- GeminiHttpClient sanitization is only compatibility cleanup; ResponseParser still performs strict validation afterward.
- CourseService safe fallback behavior remains intact.
- CourseService now logs small safe fallback reason categories only, such as Gemini request failed, AI response validation failed, or AI difficulty did not match request.
- CourseService must not log raw Gemini output, full prompts, API keys, JWTs, tokens, passwords, or secrets.
- Validation was not weakened by prompt/response compatibility polish.
- Invalid AI output is still rejected and never persisted.
- Manual browser generation for `Dynamic Programming Memoization AI Check` completed successfully with no browser error.
- Manual DB result for topic `dynamic programming memoization ai check` showed `source_type=PLACEHOLDER` and `total_xp=225`, confirming fallback safety still works after prompt/response polish.
- Manual real Gemini `source_type=AI` persistence is still not confirmed.
- API keys and local DB passwords must never be pasted into chat, Build Log, screenshots, or committed files.
- If any Gemini API key was accidentally pasted into chat/logs/screenshots, revoke/delete it and create a new key.
- Real Gemini API key was previously accidentally pasted during manual testing and should be considered exposed. Use only a newly rotated key for future local manual tests.
- A local PostgreSQL password was also pasted during manual testing. Consider rotating the local DB password later. Do not commit or document the real password.
- Next AI-related work should investigate why real Gemini manual runs still fall back to placeholder and confirm real `source_type=AI` manually without exposing secrets.
- Do not combine next AI diagnostics work with frontend course map, quizzes UI, lessons UI, leaderboard, Docker, CI/CD, deployment, code execution, or Phase 2 features unless explicitly scoped.

## Current Source of Truth Files
- CodeQuest_AI_Control_Master_Blueprint_v3.docx: full master blueprint in ChatGPT Project resources.
- CodeQuest_Core_Rules.docx / .md: always-paste AI-control rules.
- CodeQuest_DB_Schema.docx / .md: database rules and schema.
- CodeQuest_API_Contracts.docx / .md: endpoint contracts and examples.
- CodeQuest_Feature_Prompts.docx / .md: prompt bank for Codex tasks.
- CodeQuest_Build_Log.docx / .md: current progress and next task memory.
- AGENTS.md: repo-root AI instructions for Codex.

## Bugs / Issues
- None blocking currently.
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
- Logout/token revoke note: Logout does not require a Flyway migration because `revoked_at` already exists.
- Logout/token revoke testing note: During testing, `AuthControllerTest` initially failed with `NoClassDefFoundError: GlobalExceptionHandler$1` because stale compiled output in `target/` was missing an enum-switch helper class. This was a stale build artifact issue, not a logout business logic issue. Running `.\mvnw.cmd clean test` fixed it.
- User profile note: GET `/api/user/profile` is implemented as an authenticated endpoint using the existing JWT authentication flow and `CurrentUserPrincipal`.
- User profile note: User profile response exposes safe user fields only and does not expose `passwordHash`, `password_hash`, `tokenHash`, `refreshToken`, `role`, or raw password.
- User profile note: No update profile endpoint was implemented in this task.
- User profile API alignment note: The original user profile implementation used GET `/api/users/me`; it was later aligned to the API contract endpoint GET `/api/user/profile`.
- User profile API alignment note: Commit `b9039ad fix: align user profile endpoint contract` was pushed to `main`, and git status was clean afterward.
- Frontend auth pages note: Login and Register pages are implemented using React state navigation only.
- Frontend auth pages note: No React Router was added.
- Frontend auth pages note: No new npm dependencies were added.
- Frontend auth pages note: `frontend/package.json` and `frontend/package-lock.json` were not changed.
- Frontend auth pages note: Commit `891476c feat: add frontend auth pages` was pushed to `main`, and git status was clean afterward.
- Frontend auth pages note: Manual backend-connected smoke test was initially blocked by local backend/CORS setup, then passed after local PostgreSQL runtime setup and CORS fix.
- Protected routes note: Protected Area implemented as a simple MVP protected view, not the final dashboard.
- Protected routes note: Commit `c607568 feat: add protected routes` was pushed to `main`, and git status was clean afterward.
- Dashboard shell note: DashboardShell was first implemented as a static UI shell only.
- Dashboard shell note: Commit `3abf231 feat: add dashboard shell` was pushed to `main`, and git status was clean afterward.
- Runtime database config note: A manual `.\mvnw.cmd spring-boot:run` initially failed because no active profile was set and no datasource URL was configured.
- Runtime database config note: PostgreSQL 17 was installed, `psql` was added to PATH for the active terminal, and database `codequest` was created.
- Runtime database config note: Backend runtime then started successfully with PostgreSQL env vars and Flyway applied migrations.
- Frontend manual testing note: Browser register initially showed `Failed to fetch` because CORS was not configured for Vite origin `http://localhost:5174`.
- CORS note: Commit `8da4448 fix: allow local frontend cors` was pushed to `main`.
- CORS note: CORS allows local Vite origins only and does not use wildcard `"*"`.
- CORS note: Backend tests passed after CORS fix: 44 tests, 0 failures, 0 errors, 0 skipped.
- Course generation foundation note: Commit `e4734e2 feat: add course generation foundation` was pushed to `main`, and git status was clean afterward.
- Course generation foundation note: V3 migration creates `courses` and `levels` tables. V1 and V2 were not edited.
- Course generation foundation note: Backend tests passed with 53 tests, 0 failures, 0 errors, 0 skipped.
- Course generation foundation note: Local backend runtime applied V3 successfully against PostgreSQL 17.9.
- Course generation foundation note: Manual API smoke test passed for authenticated POST `/api/courses/generate`.
- Course generation foundation note: Manual cache test passed: repeated normalized topic/difficulty returned same `courseId` with `cacheHit=true`.
- Course generation foundation note: A real local PostgreSQL password was typed in terminal during manual testing. Do not commit real passwords or include them in docs; use `<your-local-postgres-password>` placeholder only.
- Frontend course generation UI note: Commit `3ba0ef8 feat: add frontend course generation UI` was pushed to `main`, and git status was clean afterward.
- Frontend course generation UI note: Docs commit `9ee8748 docs: record frontend course generation UI completion` was pushed to `main`, and git status was clean afterward.
- Frontend course generation UI note: Frontend build passed with `cd frontend && npm run build`.
- Frontend course generation UI note: Browser manual test passed on Vite port 5174.
- Frontend course generation UI note: Dashboard Shell generated/displayed a course from browser and showed 3 levels with XP and Boss/Standard badges.
- Frontend course generation UI note: Browser cache-hit UI test passed for `Binary Search`, showing `Cache Hit` and existing course/levels.
- Frontend course generation UI note: Browser test first used a mistyped topic `Binary Searcj`; this created a correctly matching placeholder for that typo. This was input behavior, not a code bug.
- GeminiService + PromptBuilder foundation note: Commit `7162e2d feat: add gemini service and prompt builder foundation` was pushed to `main`, and git status was clean afterward.
- GeminiService + PromptBuilder foundation note: Backend `cd backend && .\mvnw.cmd test` passed with 57 tests, 0 failures, 0 errors.
- GeminiService + PromptBuilder foundation note: This task added only isolated AI foundation classes and tests.
- GeminiService + PromptBuilder foundation note: It did not call Gemini over the network.
- GeminiService + PromptBuilder foundation note: It did not wire Gemini into `CourseService`, `CourseController`, or the live `/api/courses/generate` flow.
- GeminiService + PromptBuilder foundation note: `application.yml` and test `application.yml` contain safe Gemini env placeholders/config values only. No real API key or secret was committed.
- ResponseParser + AI validation foundation note: Commit `dd3bd86 feat: add ai response parser validation foundation` was pushed to `main`, and git status was clean afterward.
- ResponseParser + AI validation foundation note: This task added only isolated AI parser/validation records and tests.
- ResponseParser + AI validation foundation note: It did not call Gemini, did not wire into CourseService/CourseController, did not persist anything, did not touch frontend, did not add migrations, and did not replace placeholder course generation.
- ResponseParser + AI validation foundation note: Backend `cd backend && .\mvnw.cmd test` passed with 65 tests, 0 failures, 0 errors.
- ResponseParser + AI validation foundation note: `ResponseParserTest` covers malformed JSON, missing title, invalid difficulty, empty levels, duplicate order numbers, invalid correctAnswer, successful parsing, and other schema/data validation paths.
- Gemini course generation wiring note: Commit `5777c2d feat: wire gemini course generation fallback` was pushed to `main`, and git status was clean afterward.
- Gemini course generation wiring note: Backend `cd backend && .\mvnw.cmd test` passed with 71 tests, 0 failures, 0 errors.
- Gemini course generation wiring note: This task added `GeminiClient`, `GeminiHttpClient`, and `GeminiException`, and updated `GeminiService`, `CourseService`, `GeminiServiceTest`, and `CourseServiceTest`.
- Gemini course generation wiring note: `CourseController` was not changed.
- Gemini course generation wiring note: Frontend files were not changed.
- Gemini course generation wiring note: DB migrations were not changed.
- Gemini course generation wiring note: Cache-hit behavior remains unchanged and should not call Gemini.
- Gemini course generation wiring note: Missing Gemini config falls back to deterministic placeholder generation.
- Gemini course generation wiring note: Gemini client failure falls back to deterministic placeholder generation.
- Gemini course generation wiring note: ResponseParser validation failure falls back to deterministic placeholder generation.
- Gemini course generation wiring note: Mocked automated tests confirm valid AI response can persist an `AI` sourceType course with parsed course/level fields.
- Gemini course generation wiring note: Manual runtime with Gemini env vars started backend successfully and browser course generation worked.
- Gemini course generation wiring note: Manual DB check for topic `graph traversal bfs real ai test` returned `source_type=PLACEHOLDER` and `total_xp=225`, confirming fallback safety but not manual AI-success persistence.
- Gemini prompt/response compatibility polish note: Backend `cd backend && .\mvnw.cmd test` passed with 74 tests, 0 failures, 0 errors.
- Gemini prompt/response compatibility polish note: This task tightened `PromptBuilder` so Gemini is asked for parser-compatible JSON with exact difficulty, bounded levels, required booleans, XP ranges, explicit arrays, valid quiz answers, and no prose/markdown fences.
- Gemini prompt/response compatibility polish note: `GeminiHttpClient` now sanitizes common Gemini output formats, including fenced ```json blocks and prose-wrapped JSON, before passing text to `ResponseParser`.
- Gemini prompt/response compatibility polish note: `CourseService` fallback behavior remains intact and now logs safe fallback reason categories only, without raw AI payloads, prompts, tokens, or secrets.
- Gemini prompt/response compatibility polish note: Validation was not weakened. Invalid AI output is still rejected and never persisted.
- Gemini prompt/response compatibility polish note: Frontend files were not changed.
- Gemini prompt/response compatibility polish note: DB migrations were not changed.
- Gemini prompt/response compatibility polish note: `CourseController` was not changed.
- Gemini prompt/response compatibility polish note: Manual browser generation for `Dynamic Programming Memoization AI Check` completed successfully with no browser error.
- Gemini prompt/response compatibility polish note: Manual DB check for `dynamic programming memoization ai check` returned `source_type=PLACEHOLDER` and `total_xp=225`; fallback safety remains confirmed, but real Gemini `source_type=AI` persistence is still not manually confirmed.
- Gemini course generation wiring note: Real Gemini API key was accidentally pasted in chat/log context during manual testing. It must be revoked/deleted and replaced with a new key. Do not commit or store the exposed key anywhere.
- Gemini course generation wiring note: Local PostgreSQL password was also pasted in chat/log context. Consider rotating the local password later. Do not commit or document the real password.
- Gemini course generation wiring note: Next AI-related work should investigate why the real Gemini manual runs still fall back to placeholder and confirm real `source_type=AI` manually without exposing secrets.

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
| 9 | 2026-05-03 | Auth login | Auth | LoginRequest, LoginResponse, AuthService.login(), AuthController.login(), AuthMapper.toLoginResponse(), GlobalExceptionHandler INVALID_CREDENTIALS mapping, AuthServiceTest, AuthControllerTest | Backend `cd backend && .\mvnw.cmd test` PASS; 27 tests total | `a1b500d feat: add auth login` |
| 10 | 2026-05-04 | JWT authentication | Auth/security | backend/pom.xml, application.yml, test application.yml, LoginResponse, AuthService, AuthController, AuthMapper, JwtService, CurrentUserPrincipal, JwtAuthenticationFilter, RestAuthenticationEntryPoint, SecurityConfig, AuthServiceTest, AuthControllerTest, HealthControllerTest, JwtServiceTest, SecurityConfigTest | Backend `cd backend && .\mvnw.cmd test` PASS; 33 tests total | `89564e7 feat: add jwt authentication` |
| 11 | 2026-05-04 | Refresh token | Auth | V2 refresh_tokens migration, RefreshToken entity/repository/service, refresh DTOs, LoginResponse refreshToken, AuthService/AuthController refresh flow, mapper/error/security config updates, tests, JwtService jti fix | Backend `cd backend && .\mvnw.cmd test` PASS; 37 tests total | `26fc7c5 feat: add refresh token` |
| 12 | 2026-05-04 | Logout / token revoke | Auth | AuthController logout endpoint, AuthService.logout(), RefreshTokenService.revokeRefreshToken(), AuthMapper.toLogoutResponse(), LogoutRequest, LogoutResponse, AuthServiceTest logout tests | Backend `cd backend && .\mvnw.cmd clean test` PASS; 39 tests total | `feat: add auth logout`. Stale `target/` build output initially caused `GlobalExceptionHandler$1` class error; fixed by clean test. Commit pushed; git status clean. |
| 13 | 2026-05-04 | User profile | User | UserProfileResponse, UserMapper, UserService, UserController, UserServiceTest, UserControllerTest | Backend `cd backend && .\mvnw.cmd test` PASS; 43 tests total | `9ba94ad feat: add user profile endpoint`. Initially implemented authenticated GET `/api/users/me` with safe response fields. Commit pushed; git status clean. |
| 14 | 2026-05-04 | Build Log update after User profile | Docs | CodeQuest_Build_Log.md | Git status clean after docs commit | `051f278 docs: record user profile completion` |
| 15 | 2026-05-04 | User profile API contract alignment | User | UserController, UserControllerTest, CodeQuest_Build_Log.md | Backend `cd backend && .\mvnw.cmd -Dtest=UserControllerTest test` PASS; Backend `cd backend && .\mvnw.cmd test` PASS; 43 tests total | `b9039ad fix: align user profile endpoint contract`. Changed GET `/api/users/me` to GET `/api/user/profile`. Commit pushed; git status clean. |
| 16 | 2026-05-05 | Frontend auth pages | Frontend Auth | App.jsx, Login.jsx, Register.jsx, authApi.js, tokenStorage.js, CodeQuest_Build_Log.md | Frontend `cd frontend && npm run build` PASS | `891476c feat: add frontend auth pages`. Commit pushed; git status clean. |
| 17 | 2026-05-05 | Protected routes | Frontend Auth | App.jsx, authApi.js, authState.js, CodeQuest_Build_Log.md | Frontend `cd frontend && npm run build` PASS | `c607568 feat: add protected routes`. Commit pushed; git status clean. |
| 18 | 2026-05-05 | Dashboard shell | Dashboard | App.jsx, DashboardShell.jsx, CodeQuest_Build_Log.md | Frontend `cd frontend && npm run build` PASS | `3abf231 feat: add dashboard shell`. Commit pushed; git status clean. |
| 19 | 2026-05-05 | Local backend runtime config + frontend CORS | Local Runtime / Security | SecurityConfig, application.yml, SecurityConfigTest, test application.yml | Backend `cd backend && .\mvnw.cmd test` PASS; 44 tests total. Local backend runtime PASS. Browser smoke test PASS. | `8da4448 fix: allow local frontend cors`. Commit pushed; git status clean. |
| 20 | 2026-05-05 | Build Log update after local runtime + CORS | Docs | CodeQuest_Build_Log.md | Git status clean after docs commit | `75fa636 docs: record local runtime and cors completion` |
| 21 | 2026-05-05 | Course generation foundation | Course Generation | V3 migration, Course entity/repository/enums, Level entity/repository, course DTOs, CourseService, CourseController, repository/service/controller tests | Backend `cd backend && .\mvnw.cmd test` PASS; 53 tests total. Local runtime PASS. Manual API/cache tests PASS. | `e4734e2 feat: add course generation foundation`. Commit pushed; git status clean. |
| 22 | 2026-05-05 | Build Log update after Course generation foundation | Docs | CodeQuest_Build_Log.md | Git status clean after docs commit | `8d66948 docs: record course generation foundation completion` |
| 23 | 2026-05-05 | Frontend course generation UI | Course Generation / Frontend | DashboardShell.jsx, courseApi.js | Frontend `cd frontend && npm run build` PASS. Browser manual generate-course test PASS. Browser cache-hit UI test PASS. | `3ba0ef8 feat: add frontend course generation UI`. Commit pushed; git status clean. |
| 24 | 2026-05-05 | Build Log update after Frontend course generation UI | Docs | CodeQuest_Build_Log.md | Git status clean after docs commit | `9ee8748 docs: record frontend course generation UI completion`. Commit pushed; git status clean. |
| 25 | 2026-05-05 | GeminiService + PromptBuilder foundation | AI / Gemini Foundation | GeminiProperties, PromptBuilder, GeminiService, main application.yml Gemini env placeholders, PromptBuilderTest, GeminiServiceTest, test application.yml Gemini test-safe config | Backend `cd backend && .\mvnw.cmd test` PASS; 57 tests total, 0 failures, 0 errors | `7162e2d feat: add gemini service and prompt builder foundation`. Commit pushed; git status clean. |
| 26 | 2026-05-05 | ResponseParser + AI validation foundation | AI / Response Parser Foundation | ResponseParser, AiCourseResponse, AiLevelResponse, AiFlashcardResponse, AiQuizQuestionResponse, AiCodingProblemResponse, AiResponseValidationException, ResponseParserTest | Backend `cd backend && .\mvnw.cmd test` PASS; 65 tests total, 0 failures, 0 errors | `dd3bd86 feat: add ai response parser validation foundation`. Commit pushed; git status clean. |
| 27 | 2026-05-05 | GeminiService + ResponseParser course generation wiring with safe fallback | AI / Course Generation Integration | GeminiClient, GeminiHttpClient, GeminiException, GeminiService, CourseService, GeminiServiceTest, CourseServiceTest | Backend `cd backend && .\mvnw.cmd test` PASS; 71 tests total, 0 failures, 0 errors. Manual backend runtime PASS with Gemini env vars. Manual browser course generation PASS. DB result for test topic persisted `source_type=PLACEHOLDER`, confirming fallback safety. | `5777c2d feat: wire gemini course generation fallback`. Added real Gemini client abstraction/HTTP client and wired AI generation on cache miss behind safe placeholder fallback. No frontend, no DB migration, no CourseController change. Mocked tests confirm AI success path; manual real AI-success persistence not confirmed yet. Commit pushed; git status clean. |
| 28 | 2026-05-05 | Gemini prompt/response compatibility polish | AI / Gemini Prompt-Response Compatibility | PromptBuilder, GeminiHttpClient, CourseService, PromptBuilderTest, GeminiServiceTest, ResponseParserTest | Backend `cd backend && .\mvnw.cmd test` PASS; 74 tests total, 0 failures, 0 errors. Manual browser generation PASS. DB result for `dynamic programming memoization ai check` persisted `source_type=PLACEHOLDER`, confirming fallback safety but not real AI-success persistence. | Pending commit: `fix: improve gemini response compatibility`. Tightened parser-aligned prompt schema, added Gemini fenced/prose JSON sanitization, and added safe fallback reason logging categories. No frontend, no DB migration, no CourseController change. |

## Test Results Log
| Date | Command | Result | Failure summary | Fixed? |
|---|---|---|---|---|
| 2026-05-03 | `cd backend && .\mvnw.cmd test` | PASS | - | - |
| 2026-05-03 | `cd backend && .\mvnw.cmd test` | PASS | - | - |
| 2026-05-03 | `cd backend && .\mvnw.cmd test` | PASS | - | - |
| 2026-05-03 | `cd backend && .\mvnw.cmd test` | PASS | - | - |
| 2026-05-03 | `cd backend && .\mvnw.cmd test` | PASS | - | - |
| 2026-05-03 | `cd backend && .\mvnw.cmd test` | PASS | GlobalExceptionHandlerTest initially failed due missing validation provider/test context; fixed by adding validation starter and stable standalone MockMvcBuilders test | Yes |
| 2026-05-03 | `cd backend && .\mvnw.cmd test` | PASS | Auth register tests passed according to Codex output: total 15 tests | Yes |
| 2026-05-03 | `cd backend && .\mvnw.cmd test` | PASS | Auth login tests: 27 total | Yes |
| 2026-05-04 | `cd backend && .\mvnw.cmd test` | PASS | JWT authentication tests passed: 33 total | Yes |
| 2026-05-04 | `cd backend && .\mvnw.cmd test` | PASS | Refresh token tests passed: 37 total. Initial refreshed-token equality issue fixed by adding JWT `jti`. | Yes |
| 2026-05-04 | `cd backend && .\mvnw.cmd test` | FAIL | Logout task initially showed controller errors caused by stale compiled build output. | Yes |
| 2026-05-04 | `cd backend && .\mvnw.cmd clean test` | PASS | Full backend clean test passed after removing stale target output: 39 tests. | Yes |
| 2026-05-04 | `cd backend && .\mvnw.cmd -Dtest=UserServiceTest test` | PASS | User profile service tests passed. | Yes |
| 2026-05-04 | `cd backend && .\mvnw.cmd -Dtest=UserControllerTest test` | PASS | User profile controller tests passed. | Yes |
| 2026-05-04 | `cd backend && .\mvnw.cmd test` | PASS | User profile full backend tests passed: 43 total. | Yes |
| 2026-05-04 | `cd backend && .\mvnw.cmd test` after profile API alignment | PASS | User profile API alignment full backend tests passed: 43 total. | Yes |
| 2026-05-04 | `cd backend && .\mvnw.cmd spring-boot:run` | FAIL | Runtime startup failed because no datasource URL was configured. | Yes; fixed by local PostgreSQL/env vars. |
| 2026-05-05 | `cd frontend && npm run build` | PASS | Frontend auth build succeeded. | Yes |
| 2026-05-05 | `cd frontend && npm run build` | PASS | Protected routes build succeeded. | Yes |
| 2026-05-05 | `cd frontend && npm run build` | PASS | Dashboard shell build succeeded. | Yes |
| 2026-05-05 | `psql -U postgres -W -c "CREATE DATABASE codequest;"` | PASS | Local PostgreSQL database `codequest` created successfully. | Yes |
| 2026-05-05 | `cd backend && .\mvnw.cmd spring-boot:run` with DB/JWT env vars | PASS | Backend started on port 8080. Flyway applied migrations. | Yes |
| 2026-05-05 | `curl http://localhost:8080/api/health` | PASS | Backend returned health response. | Yes |
| 2026-05-05 | Browser register before CORS fix | FAIL | Browser blocked request due missing CORS. | Yes; fixed by CORS config. |
| 2026-05-05 | `cd backend && .\mvnw.cmd test` after CORS fix | PASS | Backend tests passed: 44 tests. | Yes |
| 2026-05-05 | Browser register/login/profile after CORS fix | PASS | Browser register/login/profile smoke test passed. | Yes |
| 2026-05-05 | `cd backend && .\mvnw.cmd test` after Course DB/entity/repository foundation | PASS | Backend tests passed: 45 tests. | Yes |
| 2026-05-05 | `cd backend && .\mvnw.cmd test` after CourseService + DTOs | PASS | Backend tests passed: 48 tests. | Yes |
| 2026-05-05 | `cd backend && .\mvnw.cmd test` after CourseController/API endpoint | PASS | Backend tests passed: 53 tests. | Yes |
| 2026-05-05 | `cd backend && .\mvnw.cmd spring-boot:run` after Course foundation with placeholder password literal | FAIL | Runtime failed because placeholder DB password was used literally. | Yes; fixed by setting real local password in env var. |
| 2026-05-05 | `cd backend && .\mvnw.cmd spring-boot:run` after setting real local DB password | PASS | Backend started; Flyway validated 3 migrations and applied V3. | Yes |
| 2026-05-05 | Manual API: register/login then POST `/api/courses/generate` | PASS | Authenticated request returned placeholder course and 3 levels. | Yes |
| 2026-05-05 | Manual API cache test | PASS | Same normalized topic/difficulty returned same courseId with `cacheHit=true`. | Yes |
| 2026-05-05 | `cd frontend && npm run build` after frontend course generation UI | PASS | Frontend build succeeded. | Yes |
| 2026-05-05 | Browser UI: DashboardShell generate course | PASS | Browser generated/displayed course and level cards. | Yes |
| 2026-05-05 | Browser UI: DashboardShell cache-hit course generation | PASS | Browser displayed `Cache Hit` with levels visible. | Yes |
| 2026-05-05 | `cd backend && .\mvnw.cmd test` after GeminiService + PromptBuilder foundation | PASS | Backend tests passed: 57 tests. No real Gemini calls. | Yes |
| 2026-05-05 | `cd backend && .\mvnw.cmd test` after ResponseParser + AI validation foundation | PASS | Backend tests passed: 65 tests. No real Gemini calls. | Yes |
| 2026-05-05 | `cd backend && .\mvnw.cmd test` after Gemini course generation wiring | PASS | Backend tests passed: 71 tests, 0 failures, 0 errors. Tests cover cache hit no Gemini call, missing config fallback, client failure fallback, parser failure fallback, valid AI response persistence, and preserved placeholder compatibility. No real Gemini calls in tests. | Yes |
| 2026-05-05 | `git diff -- frontend`, `git diff -- backend/src/main/resources/db/migration`, `git diff -- backend/src/main/java/com/codequest/course/CourseController.java` | PASS | No frontend diff, no DB migration diff, no CourseController diff. | Yes |
| 2026-05-05 | Manual backend runtime with Gemini env vars | PASS | Backend started on port 8080 with PostgreSQL/Flyway and Gemini env vars configured. | Yes |
| 2026-05-05 | Manual browser course generation with Gemini env vars | PASS | Browser generated course for new topic without crashing. DB showed fallback `source_type=PLACEHOLDER`. | Yes |
| 2026-05-05 | `cd backend && .\mvnw.cmd test` after Gemini prompt/response compatibility polish | PASS | Backend tests passed: 74 tests, 0 failures, 0 errors. Tests cover stricter PromptBuilder schema/output rules, GeminiHttpClient fenced JSON sanitization, prose-wrapped JSON extraction, ResponseParser invalid XP rejection, and existing fallback/AI-success behavior. No real Gemini calls in tests. | Yes |
| 2026-05-05 | `git diff -- frontend`, `git diff -- backend/src/main/resources/db/migration`, `git diff -- backend/src/main/java/com/codequest/course/CourseController.java` after Gemini prompt/response compatibility polish | PASS | No frontend diff, no DB migration diff, no CourseController diff. | Yes |

## Manual Verification Log
| Date | Feature | Manual/API check | Expected result | Status |
|---|---|---|---|---|
| 2026-05-03 | Backend health endpoint | GET `/api/health` | 200 OK with backend health response | Passed during feature task |
| 2026-05-03 | Auth register | POST `/api/auth/register` with valid name, email, password | 201 Created with safe user response | Automated tests passed; browser smoke test passed after local runtime + CORS fix |
| 2026-05-03 | Auth register duplicate email | POST `/api/auth/register` again with same email | 409 Conflict with standard ErrorDTO and EMAIL_ALREADY_EXISTS | Recommended for future manual API pass |
| 2026-05-03 | Auth register invalid password | POST `/api/auth/register` with weak password | 400 Bad Request with standard ErrorDTO and VALIDATION_ERROR | Browser showed 400 Invalid request for weak/invalid password; strong password worked |
| 2026-05-03 | Auth login | POST `/api/auth/login` with valid credentials | 200 OK with safe user fields, accessToken, refreshToken, tokenType, expiresInSeconds | Browser smoke test passed after local runtime + CORS fix |
| 2026-05-03 | Auth login wrong password | POST `/api/auth/login` with wrong password | 401 Unauthorized with standard ErrorDTO and INVALID_CREDENTIALS | Recommended for future manual API pass |
| 2026-05-04 | JWT public health endpoint | GET `/api/health` without token | 200 OK | Passed with local backend runtime |
| 2026-05-04 | User profile API alignment with token | GET `/api/user/profile` with valid JWT | 200 OK with safe profile fields | Browser Protected Area `Load my profile` passed after local runtime + CORS fix |
| 2026-05-04 | User profile API alignment without token | GET `/api/user/profile` without token | 401 Unauthorized | Automated integration test passed |
| 2026-05-05 | Local PostgreSQL setup | Install PostgreSQL 17, add psql to current PATH, create `codequest` database | PostgreSQL accepts connection and DB exists | Passed |
| 2026-05-05 | Backend runtime startup after local DB setup | `cd backend && .\mvnw.cmd spring-boot:run` with env vars | Backend starts on port 8080 and Flyway applies migrations | Passed |
| 2026-05-05 | Backend health after runtime setup | GET `/api/health` | 200 OK | Passed |
| 2026-05-05 | Frontend auth pages after CORS fix | Register and login in browser | Register/login works | Passed |
| 2026-05-05 | Protected routes after CORS fix | Protected Area backend-connected profile load smoke test | Logged-in user loads safe profile fields | Passed |
| 2026-05-05 | Dashboard shell | Dashboard shell UI smoke check | User can open Dashboard Shell | Passed |
| 2026-05-05 | Local frontend-backend CORS | Browser requests from Vite origin to backend | No CORS block | Passed |
| 2026-05-05 | Course generation foundation runtime migration | Start backend after V3 migration added | Flyway validates/applies V3 and backend starts | Passed |
| 2026-05-05 | Course generation foundation success | Register/login user, POST `/api/courses/generate` | 200 OK placeholder course with 3 ordered levels | Passed |
| 2026-05-05 | Course generation foundation cache | Repeat normalized topic/difficulty | Same courseId with `cacheHit=true` | Passed |
| 2026-05-05 | Course generation foundation protected endpoint | POST `/api/courses/generate` without token | 401 Unauthorized | Automated controller test passed |
| 2026-05-05 | Course generation foundation validation | Invalid topic or missing difficulty | 400 Bad Request | Automated controller test passed |
| 2026-05-05 | Course generation foundation safety | Inspect response | No user password/token/internal sensitive fields | Passed |
| 2026-05-05 | Frontend course generation UI build | `cd frontend && npm run build` | Vite build succeeds | Passed |
| 2026-05-05 | Frontend course generation UI success | Browser Dashboard Shell Generate Course | Course and level cards appear | Passed |
| 2026-05-05 | Frontend course generation UI cache | Browser generate existing `Binary Search` | UI shows `Cache Hit` | Passed |
| 2026-05-05 | GeminiService + PromptBuilder foundation tests | `cd backend && .\mvnw.cmd test` | 57 tests pass | Passed |
| 2026-05-05 | GeminiService + PromptBuilder scope check | Inspect changed files and `git diff --stat` | Only AI module/config/test files changed | Passed |
| 2026-05-05 | ResponseParser + AI validation foundation tests | `cd backend && .\mvnw.cmd test` | 65 tests pass | Passed |
| 2026-05-05 | ResponseParser + AI validation scope check | `git diff -- frontend`, `git diff -- CourseService`, `git diff -- CourseController`, `git diff -- db/migration` | No unrelated changes | Passed |
| 2026-05-05 | Gemini course generation wiring tests | `cd backend && .\mvnw.cmd test` | 71 tests pass; Gemini wiring/fallback tests pass; no real Gemini call in tests | Passed |
| 2026-05-05 | Gemini course generation wiring scope check | `git diff -- frontend`, `git diff -- db/migration`, `git diff -- CourseController` | No frontend, migration, or controller changes | Passed |
| 2026-05-05 | Gemini course generation backend runtime | Start backend with DB/JWT/Gemini env vars | Backend starts successfully on port 8080; Flyway schema up to date | Passed |
| 2026-05-05 | Gemini course generation browser fallback check | Generate new uncached topic `Graph Traversal BFS Real AI Test` from browser | Course generation works without browser crash/error | Passed |
| 2026-05-05 | Gemini course generation DB fallback check | Query `courses` for normalized topic `graph traversal bfs real ai test` | Row exists with `source_type=PLACEHOLDER`, `total_xp=225` | Passed; fallback safety confirmed |
| 2026-05-05 | Gemini course generation real AI-success check | Manual real Gemini run should ideally persist `source_type=AI` for valid Gemini output | Not confirmed manually yet; automated mocked test covers AI success path | Pending future investigation |
| 2026-05-05 | Gemini prompt/response compatibility polish tests | `cd backend && .\mvnw.cmd test` | 74 tests pass; prompt/schema compatibility, response sanitization, and validation safety tests pass | Passed |
| 2026-05-05 | Gemini prompt/response compatibility scope check | `git diff -- frontend`, `git diff -- db/migration`, `git diff -- CourseController` | No frontend, DB migration, or CourseController changes | Passed |
| 2026-05-05 | Gemini prompt/response browser check | Generate new uncached topic `Dynamic Programming Memoization AI Check` from DashboardShell with Gemini env vars | Course generation completes without browser error | Passed |
| 2026-05-05 | Gemini prompt/response DB check | Query `courses` for normalized topic `dynamic programming memoization ai check` | Row exists with `source_type=PLACEHOLDER`, `total_xp=225` | Passed; fallback safety confirmed, real AI-success still not confirmed |

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
Schema "public" is up to date. No migration necessary.
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
- Frontend auth pages are implemented.
- Protected routes are implemented.
- Dashboard shell is implemented.
- Local frontend-backend CORS is implemented.
- Course generation foundation is implemented.
- Frontend course generation UI is implemented.
- GeminiService + PromptBuilder foundation is implemented.
- ResponseParser + AI validation foundation is implemented.
- Gemini course generation wiring with safe fallback is implemented.
- Gemini prompt/response compatibility polish is implemented.

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

Expected success:
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

Important Auth login boundaries:
- Response must not contain `passwordHash`.
- Response must not contain `password_hash`.
- Response must not contain `role`.
- Response must not contain `tokenHash`.
- Login response contains JWT accessToken, opaque refreshToken, tokenType, and expiresInSeconds.
- Token rotation is still not implemented.

## JWT Authentication Manual Test Commands
Use these after starting the backend with a configured local datasource/profile.

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
- No access-token blacklist implemented.
- No token rotation implemented yet.

## Refresh Token Manual Test Commands
Use these after starting the backend with a configured local datasource/profile.

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

Important Refresh token boundaries:
- Refresh token is opaque, not JWT.
- Only refresh token hash is stored in database.
- Raw refresh token must never be logged.
- Raw refresh token must never be stored in database.
- tokenHash must never be returned in API response.
- Refresh endpoint returns new accessToken only.
- Refresh endpoint does not return new refreshToken.
- Token rotation is not implemented yet.

## Logout / Token Revoke Manual Test Commands
Use these after starting the backend with a configured local datasource/profile.

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

Important Logout / token revoke boundaries:
- Logout revokes refresh token by setting `revokedAt`.
- Logout does not delete refresh token rows.
- Logout does not blacklist existing JWT access tokens.
- Existing access tokens remain valid until expiry.
- Logout does not rotate refresh tokens.
- Logout response must not expose sensitive fields.
- Frontend logout UI is not implemented yet.

## User Profile Manual Test Commands
Use these after starting the backend with a configured local datasource/profile.

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
- Response must not expose password/token/internal fields.
- No update profile endpoint implemented yet.

## Course Generation Foundation Manual Test Commands
Use these after starting the backend with a configured local datasource/profile.

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

Expected placeholder response when fallback path is used:
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

Important Course generation boundaries:
- Endpoint is POST `/api/courses/generate`.
- Endpoint requires JWT Bearer token.
- Endpoint must not accept user id from path, query params, or request body.
- Controller must stay thin and delegate to CourseService.
- Current flow attempts Gemini only on cache miss when Gemini config is present.
- Cache hit must not call Gemini.
- Missing Gemini config falls back to placeholder.
- Gemini/client failure falls back to placeholder.
- Parser validation failure falls back to placeholder.
- Valid parsed AI output can persist supported course/level fields with `sourceType=AI`.
- Current manual real Gemini checks still persist `sourceType=PLACEHOLDER`, so real AI-success requires future safe diagnostics.
- No quiz, flashcard, note, progress, XP/rank/streak, leaderboard, Piston/code execution, Docker, CI/CD, deployment, or Phase 2 features are implemented.

## GeminiService + PromptBuilder Foundation Manual Test Commands
Automated verification:
```powershell
cd backend
.\mvnw.cmd test
cd ..
```

Expected:
```text
Tests run: 57 or more depending on later features
Failures: 0
Errors: 0
BUILD SUCCESS
```

Important GeminiService + PromptBuilder boundaries:
- Implemented files:
  - `backend/src/main/java/com/codequest/ai/GeminiProperties.java`
  - `backend/src/main/java/com/codequest/ai/PromptBuilder.java`
  - `backend/src/main/java/com/codequest/ai/GeminiService.java`
  - `backend/src/test/java/com/codequest/ai/PromptBuilderTest.java`
  - `backend/src/test/java/com/codequest/ai/GeminiServiceTest.java`
- Later Gemini wiring added:
  - `GeminiClient`
  - `GeminiHttpClient`
  - `GeminiException`
- API key must come from env/config only.
- Never hardcode or commit real API keys.
- PromptBuilder must create JSON-only prompts with schema guidance.
- PromptBuilder must treat user topic/goal as untrusted input.
- PromptBuilder is now stricter after compatibility polish and asks for parser-compatible JSON with no prose/markdown fences.

## ResponseParser + AI Validation Foundation Manual Test Commands
Automated verification:
```powershell
cd backend
.\mvnw.cmd test
cd ..
```

Expected:
```text
Tests run: 65 or more depending on later features
Failures: 0
Errors: 0
BUILD SUCCESS
```

Important ResponseParser + AI validation boundaries:
- Implemented files:
  - `backend/src/main/java/com/codequest/ai/ResponseParser.java`
  - `backend/src/main/java/com/codequest/ai/AiCourseResponse.java`
  - `backend/src/main/java/com/codequest/ai/AiLevelResponse.java`
  - `backend/src/main/java/com/codequest/ai/AiFlashcardResponse.java`
  - `backend/src/main/java/com/codequest/ai/AiQuizQuestionResponse.java`
  - `backend/src/main/java/com/codequest/ai/AiCodingProblemResponse.java`
  - `backend/src/main/java/com/codequest/ai/AiResponseValidationException.java`
  - `backend/src/test/java/com/codequest/ai/ResponseParserTest.java`
- Parser validates AI JSON but does not repair bad AI data silently.
- Parser throws safe `AiResponseValidationException` for malformed/invalid AI output.
- Parser must not include raw full AI payload or secrets in exception messages.
- Prompt/response compatibility polish did not weaken parser validation.
- Invalid XP and other invalid AI fields are still rejected.

## Gemini Course Generation Wiring Manual Test Commands
Use this after the Gemini wiring task.

### Automated verification
```powershell
cd backend
.\mvnw.cmd test
cd ..
```

Expected after wiring:
```text
Tests run: 71 or more depending on later features
Failures: 0
Errors: 0
BUILD SUCCESS
```

Expected after prompt/response compatibility polish:
```text
Tests run: 74
Failures: 0
Errors: 0
BUILD SUCCESS
```

### Scope checks
```powershell
git diff -- frontend
git diff -- backend/src/main/resources/db/migration
git diff -- backend/src/main/java/com/codequest/course/CourseController.java
```

Expected:
```text
No output for all unrelated diff checks.
```

### Fallback manual check without Gemini key
Start backend without `GEMINI_API_KEY`:
```powershell
$env:DATABASE_URL="jdbc:postgresql://localhost:5432/codequest"
$env:DATABASE_USERNAME="postgres"
$env:DATABASE_PASSWORD="<your-local-postgres-password>"
$env:JWT_SECRET="dev-only-change-this-secret-dev-only-change-this-secret"
Remove-Item Env:GEMINI_API_KEY -ErrorAction SilentlyContinue

cd backend
.\mvnw.cmd spring-boot:run
```

Generate a new uncached topic from frontend/API.

Expected:
```text
Placeholder course still works.
3 deterministic levels appear.
No frontend crash.
```

### Real Gemini runtime check
Use only with a valid local Gemini key. Do not paste key in chat, screenshots, logs, or Build Log.
```powershell
$env:DATABASE_URL="jdbc:postgresql://localhost:5432/codequest"
$env:DATABASE_USERNAME="postgres"
$env:DATABASE_PASSWORD="<your-local-postgres-password>"
$env:JWT_SECRET="dev-only-change-this-secret-dev-only-change-this-secret"

$env:GEMINI_API_KEY="<your-real-gemini-key>"
$env:GEMINI_MODEL="gemini-1.5-flash"
$env:GEMINI_BASE_URL="https://generativelanguage.googleapis.com"

cd backend
.\mvnw.cmd spring-boot:run
```

Generate a new uncached topic from frontend/API:
```text
Topic: Dynamic Programming Memoization AI Check
Difficulty: BEGINNER
Goal: Learn DP memoization for Java DSA interviews
```

Check DB source type:
```powershell
$env:Path += ";C:\Program Files\PostgreSQL\17\bin"
psql -U postgres -W -d codequest -c "select title, difficulty, source_type, total_xp, created_at from courses where normalized_topic='dynamic programming memoization ai check' order by created_at desc limit 1;"
```

Observed latest manual result:
```text
title: Dynamic Programming Memoization Ai Check
difficulty: BEGINNER
source_type: PLACEHOLDER
total_xp: 225
```

Meaning:
```text
Manual fallback safety confirmed.
Manual real AI-success persistence not confirmed yet.
Mocked automated tests confirm valid AI-success path.
Next AI task should diagnose real Gemini failure reason safely.
```

Important Gemini wiring boundaries:
- No frontend changes.
- No DB migration changes.
- CourseController unchanged.
- Tests must not call real Gemini.
- API key must be env/config only.
- Do not log or expose API key.
- Do not log full prompts or raw Gemini output.
- Safe fallback must remain.

## Gemini Prompt/Response Compatibility Polish Manual Test Commands
Use this after the prompt/response compatibility polish task.

### Automated verification
```powershell
cd backend
.\mvnw.cmd test
cd ..
```

Expected:
```text
Tests run: 74
Failures: 0
Errors: 0
BUILD SUCCESS
```

### Scope checks
```powershell
git diff -- frontend
git diff -- backend/src/main/resources/db/migration
git diff -- backend/src/main/java/com/codequest/course/CourseController.java
```

Expected:
```text
No output for all unrelated diff checks.
```

### Real Gemini manual check
Start backend with DB/JWT/Gemini env vars, using only a newly rotated Gemini key in local PowerShell:
```powershell
$env:DATABASE_URL="jdbc:postgresql://localhost:5432/codequest"
$env:DATABASE_USERNAME="postgres"
$env:DATABASE_PASSWORD="<your-local-postgres-password>"
$env:JWT_SECRET="dev-only-change-this-secret-dev-only-change-this-secret"

$env:GEMINI_API_KEY="<your-new-rotated-gemini-key>"
$env:GEMINI_MODEL="gemini-1.5-flash"
$env:GEMINI_BASE_URL="https://generativelanguage.googleapis.com"

cd backend
.\mvnw.cmd spring-boot:run
```

Generate a new uncached topic from DashboardShell:
```text
Topic: Dynamic Programming Memoization AI Check
Difficulty: BEGINNER
Goal: Learn DP memoization for Java DSA interviews
```

Check DB:
```powershell
$env:Path += ";C:\Program Files\PostgreSQL\17\bin"
psql -U postgres -W -d codequest -c "select title, difficulty, source_type, total_xp, created_at from courses where normalized_topic='dynamic programming memoization ai check' order by created_at desc limit 1;"
```

Observed result:
```text
source_type=PLACEHOLDER
total_xp=225
```

Meaning:
```text
Prompt/response compatibility polish did not break the app.
Fallback safety is still confirmed.
Manual real source_type=AI persistence is still not confirmed.
Next task should use safe diagnostics to determine why real Gemini run falls back.
```

Important boundaries:
- No raw Gemini response should be logged.
- No API key should be pasted, logged, committed, or stored in Build Log.
- No DB migration should be added for this diagnostic step unless explicitly scoped.
- No frontend changes should be made unless explicitly scoped.

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
4. In Generate Course form, enter a topic/difficulty/goal.
5. Click Generate Course.
```

Expected UI:
```text
Generated course appears.
Title appears.
Description appears.
Course ID appears in muted text.
Cache badge appears.
Level cards appear.
No accessToken or refreshToken is visible.
No CORS error appears.
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
- Does not implement real course map, lessons UI, quizzes UI, flashcards UI, notes UI, XP/streak/progress, leaderboard, code execution, Docker, CI/CD, deployment, or Phase 2 features.

## Frontend Auth Pages Manual Test Commands
Use these after starting backend and frontend.

Start frontend:
```powershell
cd frontend
npm run dev
```

Open:
```text
http://localhost:5173
```

or Vite’s shown port.

Expected localStorage keys:
```text
codequest_access_token
codequest_refresh_token
```

Important Frontend auth boundaries:
- Frontend auth pages use React state navigation only.
- React Router was not added.
- No package.json or package-lock.json change was made.
- No logout UI was implemented.
- No profile page was implemented.

## Protected Routes Manual Test Commands
Use these after starting backend and frontend.

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
Expected: frontend calls GET /api/user/profile with Authorization Bearer accessToken and shows safe fields only.
```

Important Protected routes boundaries:
- React Router was not added.
- Protected Area must not show accessToken or refreshToken.
- Protected Area is not the final dashboard.
- Logout UI is not implemented.
- Refresh-token retry and token rotation are not implemented.

## Dashboard Shell Manual Test Commands
Use these after starting frontend.

Dashboard shell route through UI:
```text
Open Protected Area.
Click Open Dashboard Shell.
Expected: Dashboard shell opens with dashboard content and Generate Course form.
```

Generate Course from DashboardShell:
```text
Enter topic/difficulty/goal.
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
- Dashboard shell does not read or show accessToken or refreshToken.
- Dashboard shell calls POST `/api/courses/generate` only when user clicks Generate Course.
- Dashboard shell does not implement real course map routing, XP/streak logic, leaderboard, logout UI, code execution, Docker, CI/CD, deployment, or Phase 2 features.

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

Current module: AI / Gemini Prompt-Response Compatibility Polish.
Last completed feature: Gemini prompt/response compatibility polish for real Gemini AI-success path.
Current feature status: Gemini prompt/response compatibility polish completed, backend tests passed, manual fallback verified; commit may still be pending if this Build Log update has not been committed.
Latest completed commit before current pending feature: 5777c2d feat: wire gemini course generation fallback.
Pending commit for current feature: fix: improve gemini response compatibility.
Git status: should be clean only after committing PromptBuilder, GeminiHttpClient, CourseService, GeminiServiceTest, PromptBuilderTest, ResponseParserTest, and this Build Log update.

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

Important completed Course generation details:
- V3 migration creates courses and levels tables.
- CourseService implements normalized topic/difficulty cache behavior.
- CourseController exposes authenticated POST /api/courses/generate.
- Endpoint uses JWT principal and does not accept userId from path, params, or body.
- Cache key is normalizedTopic + difficulty.
- Cache hit returns same courseId with cacheHit=true.
- Cache hit must not call Gemini.
- Placeholder fallback creates exactly 3 levels and totalXp 225.
- Frontend DashboardShell calls POST /api/courses/generate and displays returned course/levels.

Important completed AI details:
- GeminiProperties, PromptBuilder, GeminiService foundation implemented.
- ResponseParser + AI response DTO records implemented.
- ResponseParser validates Gemini JSON and throws safe AiResponseValidationException.
- GeminiClient abstraction implemented.
- GeminiHttpClient implemented using Spring RestClient.
- GeminiException implemented for safe Gemini failures.
- GeminiService delegates to GeminiClient only when Gemini config is present.
- CourseService attempts Gemini + ResponseParser only on cache miss when config is present.
- Missing config/client failure/parser failure falls back to placeholder.
- Valid AI response can persist supported course/level fields with sourceType=AI.
- Flashcards, quizzes, and codingProblems are parsed/validated but not persisted.
- PromptBuilder has been tightened to request exact parser-compatible JSON.
- GeminiHttpClient now sanitizes fenced ```json blocks and common prose-wrapped JSON before parser validation.
- CourseService logs safe fallback reason categories only.
- No raw Gemini output, prompt, API key, JWT, token, password, or secret should be logged.
- No frontend changes.
- No DB migration changes.
- CourseController unchanged.

Testing completed:
- Backend test command:
  cd backend
  .\mvnw.cmd test
- Result after latest polish: 74 tests, 0 failures, 0 errors.
- Tests cover stricter PromptBuilder schema/output rules, GeminiHttpClient fenced JSON sanitization, prose-wrapped JSON extraction, ResponseParser invalid XP rejection, cache hit no Gemini call, missing config fallback, Gemini/client failure fallback, parser failure fallback, valid mocked AI response persistence, and preserved placeholder compatibility.
- Scope checks showed no frontend diff, no DB migration diff, and no CourseController diff.
- Manual browser course generation for `Dynamic Programming Memoization AI Check` worked.
- Manual DB check for normalized topic `dynamic programming memoization ai check` returned source_type=PLACEHOLDER and total_xp=225.
- This confirms fallback safety after prompt/response polish.
- Manual real AI-success persistence with source_type=AI is still not confirmed.
- Mocked automated tests confirm valid AI success path.

Security notes:
- Do not paste API keys, DB passwords, or JWT secrets in chat, screenshots, Build Log, or commits.
- A Gemini API key was accidentally pasted during manual testing. Revoke/delete that exposed key and create a new key.
- A local DB password was also pasted during manual testing. Consider rotating local DB password later.
- Never hardcode API keys.
- Never commit real secrets.

Not implemented yet:
- Confirmed real Gemini AI-success manual persistence
- Safe diagnostics for real Gemini fallback reason
- Real Course map navigation
- Lesson page
- Flashcards UI
- Notes
- Quizzes UI
- XP/rank/streak/progress
- Leaderboard
- Piston/code execution
- Logout UI
- Docker
- CI/CD
- Deployment
- Phase 2 features

Next safest step:
First confirm git status is clean after committing Gemini prompt/response compatibility polish.
Then investigate why real Gemini manual run still falls back to PLACEHOLDER using safe diagnostics only.
Goal: identify whether fallback is caused by Gemini request failure, response extraction, parser validation, difficulty mismatch, quota/model/API version, or other safe reason.
Do not remove placeholder fallback.
Do not touch frontend unless explicitly scoped.
Do not add DB migrations unless explicitly scoped.
Do not implement course map, lesson UI, quiz UI, flashcard UI, code execution, leaderboard, deployment, or Phase 2 features.
Do not call Gemini in automated tests.
Keep tests deterministic.
Do not persist unsafe/unvalidated AI output.
Do not log raw Gemini response, full prompt, API key, JWTs, tokens, passwords, or secrets.
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

Current module: AI / Gemini Prompt-Response Compatibility Polish
Last completed feature: Gemini prompt/response compatibility polish for real Gemini AI-success path
Current feature status: Gemini prompt/response compatibility polish completed, backend tests passed, manual fallback verified; commit may still be pending if Build Log update has not been committed yet
Next task: Investigate real Gemini fallback reason using safe diagnostics only
Latest completed commit before current pending feature: 5777c2d feat: wire gemini course generation fallback
Pending commit: fix: improve gemini response compatibility
Git status: should be clean only after committing PromptBuilder, GeminiHttpClient, CourseService, GeminiServiceTest, PromptBuilderTest, ResponseParserTest, and this Build Log update
Tests passed: Backend .\mvnw.cmd test PASS with 74 tests, 0 failures, 0 errors
Known bugs/blockers: Manual real Gemini run still fell back to PLACEHOLDER; source_type=AI not manually confirmed yet. Mocked automated tests cover AI success path.

Important completed Auth details:
- Register implemented.
- Login implemented.
- JWT authentication implemented.
- Login response returns accessToken and refreshToken.
- Refresh token is opaque, not JWT.
- Refresh token is stored only as a hash in the refresh_tokens table.
- POST /api/auth/refresh implemented.
- POST /api/auth/logout implemented.
- Existing access tokens remain valid until expiry.
- No refresh-token rotation implemented.

Important completed User profile details:
- GET /api/user/profile implemented.
- Endpoint requires JWT authentication.
- Endpoint uses CurrentUserPrincipal from SecurityContext.
- Endpoint does not accept userId from params, body, or path.
- UserProfileResponse returns safe fields.
- No update profile endpoint implemented.

Important completed Frontend details:
- Login page implemented.
- Register page implemented.
- Protected Area implemented.
- DashboardShell implemented.
- DashboardShell includes Generate Course form.
- DashboardShell calls POST /api/courses/generate only on button click.
- DashboardShell displays generated course and levels.
- React Router not added.
- Logout UI not implemented.

Important completed Local runtime / CORS details:
- PostgreSQL 17 installed locally.
- Local database codequest created.
- Backend starts with DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD, JWT_SECRET.
- Flyway applied V1, V2, V3.
- CORS allows localhost/127.0.0.1 ports 5173 and 5174.
- Browser register/login/profile/course-generation smoke tests pass.

Important completed Course generation details:
- V3 migration creates courses and levels tables.
- CourseService implements normalized topic/difficulty cache behavior.
- CourseController exposes authenticated POST /api/courses/generate.
- Cache hit returns same courseId with cacheHit=true.
- Cache hit does not call Gemini.
- Placeholder fallback creates exactly 3 levels and totalXp 225.
- Frontend DashboardShell displays returned course and levels.

Important completed AI details:
- GeminiProperties implemented.
- PromptBuilder implemented and later tightened for parser-compatible JSON.
- GeminiService implemented.
- ResponseParser implemented.
- AI response DTO records implemented.
- GeminiClient abstraction implemented.
- GeminiHttpClient implemented using Spring RestClient.
- GeminiHttpClient now sanitizes fenced/prose-wrapped JSON.
- GeminiException implemented.
- GeminiService delegates to GeminiClient only when config is present.
- CourseService attempts Gemini + ResponseParser only on cache miss when config is present.
- Missing config/client failure/parser failure falls back to placeholder.
- Valid mocked AI response can persist supported course/level fields with sourceType=AI.
- Flashcards, quizzes, and codingProblems are parsed/validated but not persisted.
- No frontend changes.
- No DB migration changes.
- CourseController unchanged.

Testing notes:
- Always use Maven Wrapper only for backend:
  cd backend
  .\mvnw.cmd test
- If stale compiled class issues appear:
  cd backend
  .\mvnw.cmd clean test
- For frontend tasks:
  cd frontend
  npm run build
- For browser integration:
  start backend on 8080 and frontend on Vite 5173 or 5174.
- For real Gemini runtime:
  set GEMINI_API_KEY, GEMINI_MODEL, GEMINI_BASE_URL in local PowerShell only.
  Never paste or commit real keys.

Security notes:
- A Gemini API key was accidentally pasted in chat/log context during manual testing. Revoke/delete that key and create a new one.
- A local DB password was also pasted in chat/log context. Consider rotating it later.
- Do not commit or document real secrets.
- Do not paste secrets in future chats/screenshots.
- Do not log raw Gemini output, full prompts, API keys, JWTs, tokens, passwords, or secrets.

Runtime note:
- `cd backend && .\mvnw.cmd spring-boot:run` requires datasource env vars.
- Without datasource URL/profile it fails with `Failed to determine suitable jdbc url`.
- Local PostgreSQL + env var path is confirmed working.

Rules:
Follow master blueprint, Core Rules, DB Schema, API Contracts, Feature Prompts, Build Log, and AGENTS.md.
Do not redesign anything.
Do not add Phase 2 features.
Do not start the next feature while current feature changes are uncommitted.
Use Maven Wrapper only for backend.
```