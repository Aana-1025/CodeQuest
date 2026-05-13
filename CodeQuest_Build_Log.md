# CodeQuest Build Log

## Purpose
This file solves the long-chat slowdown problem. Update it manually after every feature so a fresh ChatGPT/Codex chat can continue from the current state without needing the full conversation history.

## Current Status
Phase: MVP
Current module: Frontend / Note preload foundation
Current feature: Frontend Note Preload Foundation completed, tested, manually verified, committed, and pushed
Latest commit: `e7ca3b7 feat: preload lesson notes`
Previous commit: `61af8c5 docs: record get note by level completion`
Current branch: main
Test status: Frontend `cd frontend && npm run build` PASS. Manual browser verification PASS: Notes section preloads saved note content when reopening the same lesson through GET `/api/notes/levels/{levelId}`, updated note content preloads after resave, different lessons keep separate note content without leaking the previous lesson's note, no-note 404 state is handled quietly, Quiz and Flashcards still render, Back to Course Map and Back to Home still work, browser console has no red runtime errors, and no token/password/secret is visible. Backend, DB migrations, AI, backend course, backend quiz, backend flashcard, and backend note files unchanged.
Git status: clean after frontend note preload feature commit; Build Log docs update pending

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
- [x] Safe Gemini fallback diagnostics
- [x] Gemini HTTP request/status diagnostics
- [x] Gemini retry-once for transient 5xx / sourceType=AI manual verification
- [x] Frontend AI/placeholder course source badge fix
- [x] Backend course fetch endpoint
- [x] Course map
- [ ] Level unlock logic
- [x] Lesson page
- [x] Frontend Quiz Panel Foundation
- [x] Frontend Flashcards Panel Foundation
- [x] Backend Quiz Persistence/Fetch Foundation
- [x] Backend Flashcards Persistence/Fetch Foundation
- [x] Frontend Real Quiz/Flashcards Display Compatibility Check/Fix
- [x] Backend Notes Foundation
- [x] Frontend Notes Editor Foundation
- [x] Backend GET Notes Foundation
- [x] Frontend Note Preload Foundation
- [ ] Flashcards
- [x] Notes
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
- Dashboard shell course badge display now uses `sourceType` from the API response.
- Dashboard shell badge behavior after the source badge fix:
  - `cacheHit=true` shows `Cache Hit`.
  - `sourceType=AI` shows `AI Generated Course`.
  - `sourceType=PLACEHOLDER` with non-cache result shows `New Placeholder Course`.
  - unknown/missing source values show a safe neutral `New Course` fallback.
- `GenerateCourseResponse` now exposes `sourceType` to the frontend.
- `CourseService` maps persisted course source type into `GenerateCourseResponse`.
- The source badge fix required a small backend response DTO/mapping change because the frontend did not previously receive any source type field.
- `CourseController` remained unchanged for the source badge fix.
- DB migrations remained unchanged for the source badge fix.
- Backend AI retry logic remained unchanged for the source badge fix.
- Backend course fetch endpoint `GET /api/courses/{courseId}` is implemented.
- `GET /api/courses/{courseId}` is authenticated and requires Bearer JWT.
- `GET /api/courses/{courseId}` uses courseId from the path and does not accept userId from request path, params, or body.
- `CourseController` stays thin for course fetch and delegates to `CourseService`.
- `CourseService.getCourseById(UUID)` fetches a persisted course and returns safe DTOs with ordered levels.
- Course fetch response includes `courseId`, `title`, `description`, `difficulty`, `sourceType`, `totalXp`, and ordered `levels`.
- Course fetch level response includes `levelId`, `orderNumber`, `title`, `contentMarkdown`, `xpReward`, and `isBoss`.
- Missing course IDs return standard 404 ErrorDTO using existing global error handling.
- Unauthenticated course fetch returns 401.
- `GET /api/courses/{courseId}` must never call Gemini or generation flow.
- Frontend Course Map uses GET `/api/courses/{courseId}`.
- Frontend Lesson view uses existing fetched Course Map/level data and does not require a new backend endpoint.
- Course fetch endpoint does not change POST `/api/courses/generate` behavior.
- Course fetch endpoint added no DB migrations, no frontend changes, and no AI changes.
- Local PostgreSQL 17 is installed for development.
- Local database `codequest` was created with PostgreSQL user `postgres`.
- Local backend runtime works when these environment variables are set:
  - `DATABASE_URL=jdbc:postgresql://localhost:5432/codequest`
  - `DATABASE_USERNAME=postgres`
  - `DATABASE_PASSWORD=<local postgres password>`
  - `JWT_SECRET=dev-only-change-this-secret-dev-only-change-this-secret`
- Flyway successfully applies/validates V1, V2, V3, V4, V5, and V6 migrations against local PostgreSQL.
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
  - first request creates placeholder or AI course and returns `cacheHit=false`
  - later same topic/difficulty with different casing/spaces returns same course and `cacheHit=true`
- Course generation foundation creates exactly 3 placeholder levels:
  1. Introduction to `<Title>` with 50 XP
  2. Practice `<Title>` with 75 XP
  3. `<Title>` Boss Challenge with 100 XP and `isBoss=true`
- Course generation foundation persists placeholder `totalXp=225`.
- Valid AI-generated courses can have AI-defined level count and XP totals after strict parser validation.
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
  - cache hit / AI generated / placeholder / unknown new course badge
  - course id in muted text
  - ordered level cards
  - level order number, title, XP reward, and Boss/Standard badge
- Frontend Course Map foundation is implemented in DashboardShell.
- Course Map uses `getCourseById(courseId)` from `frontend/src/services/courseApi.js`.
- Course Map is opened only by explicit user click on `Open Course Map`; it does not fetch on page load.
- Course Map fetches authenticated GET `/api/courses/{courseId}` using the existing Bearer token pattern.
- Course Map shows course title, description, difficulty, sourceType, totalXp, ordered level cards, XP reward, Boss/Standard badges, and plain-text content previews.
- Course Map data is not stored in localStorage.
- Lesson Page Foundation is implemented in DashboardShell.
- Lesson view opens from Course Map level cards through an explicit `Open Lesson` action.
- Lesson view uses already-fetched Course Map data and does not refetch from the backend.
- Lesson view shows course title, level number, level title, XP reward, Boss/Standard badge, and readable plain-text lesson content from `contentMarkdown`.
- Lesson view renders safe plain text only; it does not render raw markdown as unsafe HTML.
- `Back to Course Map` returns to the already-loaded Course Map without a backend refetch requirement.
- Lesson Page Foundation is frontend-only and does not implement quiz submit, notes saving, unlock logic, XP/progress persistence, Piston/code execution, leaderboard, Docker, CI/CD, deployment, or Phase 2 features.
- Frontend Quiz Panel Foundation is implemented in DashboardShell inside the Lesson view.
- The Quiz panel is frontend-only and does not call backend quiz endpoints, submit answers, calculate score, award XP, persist answers, update progress, or unlock levels.
- The Quiz panel supports safe future-compatible quiz-like arrays if they appear on a selected level later, but current persisted level data usually has no quiz data.
- When quiz data is missing, the Lesson view shows the safe empty state: `Quiz questions are not available for this level yet.`
- Frontend Flashcards Panel Foundation is implemented in DashboardShell inside the Lesson view.
- The Flashcards panel is frontend-only and does not call backend flashcard endpoints, persist cards, calculate progress, award XP, update progress, or unlock levels.
- The Flashcards panel supports safe future-compatible flashcard-like arrays if they appear on a selected level later, but current persisted level data usually has no flashcard data.
- When flashcard data is missing, the Lesson view shows the safe empty state: `Flashcards are not available for this level yet.`
- Quiz selections and flashcard reveal state are local UI-only state and reset when the selected lesson changes.
- Frontend Real Quiz/Flashcards Display Compatibility Check/Fix is implemented.
- Lesson Quiz panel now correctly supports backend `quizQuestions[].options` object shape `{A, B, C, D}` by normalizing it into the array-style UI rendering path.
- Lesson Quiz panel still keeps local-only option selection and does not submit answers, calculate score, award XP, persist answers, update progress, or unlock levels.
- Lesson Quiz panel still does not display `correctAnswer`.
- Lesson Flashcards panel was already compatible with backend `flashcards` shape using `front` and `back`; no backend change was needed for the compatibility fix.
- Real Gemini BEGINNER AI course manual verification confirmed persisted quiz questions and flashcards render in the Lesson view.
- Manual ADVANCED `Greedy Algorithm` test fell back to placeholder with safe `PARSER_VALIDATION_FAILURE`; this is not an API key/frontend issue and can be investigated later through safe parser diagnostics if needed.
- Current real quiz submit, scoring, frontend notes UI/rendering, XP/progress persistence, level unlock logic, Piston/code execution, leaderboard, Docker, CI/CD, deployment, and Phase 2 features remain unimplemented.
- Backend Quiz Persistence/Fetch Foundation is implemented.
- V4 Flyway migration creates the `quizzes` table.
- Quiz questions are persisted only from already parsed and validated AI output on successful AI course generation.
- Placeholder courses create no quiz rows.
- Cache hits do not call Gemini and must not duplicate quiz rows.
- GET `/api/courses/{courseId}` now returns safe `quizQuestions` arrays on each level.
- GET course responses intentionally do not expose `correctAnswer`; correct answers remain backend-only for future quiz submit/scoring.
- Backend quiz persistence/fetch did not change frontend files or AI PromptBuilder/GeminiHttpClient/GeminiService/ResponseParser.
- Backend Flashcards Persistence/Fetch Foundation is implemented.
- V5 Flyway migration creates the `flashcards` table.
- Flashcards are persisted only from already parsed and validated AI output on successful AI course generation.
- Placeholder courses create no flashcard rows.
- Cache hits do not call Gemini and must not duplicate flashcard rows.
- GET `/api/courses/{courseId}` now returns safe `flashcards` arrays on each level.
- Existing `quizQuestions` behavior remains unchanged after flashcard persistence/fetch.
- Current AI flashcard DTO exposes `front` and `back`; `conceptTag` is currently stored as `null` for AI-created rows unless the AI DTO/parser is expanded later.
- Backend flashcards persistence/fetch did not change frontend files or AI PromptBuilder/GeminiHttpClient/GeminiService/ResponseParser.
- Backend Notes Foundation is implemented.
- V6 Flyway migration creates the `notes` table.
- Notes are stored per authenticated user and level using unique `(user_id, level_id)` ownership.
- `POST /api/notes` is authenticated and upserts one note for the current user per level.
- `POST /api/notes` request uses only `levelId` and `content`; it does not accept `userId`, `noteId`, role, token, or ownership fields from the request body.
- Note ownership is derived only from JWT/SecurityContext through `CurrentUserPrincipal`.
- Same authenticated user saving again for the same level updates the same note instead of creating a duplicate row.
- Different users can save separate notes for the same level.
- Note content is validated as required, non-blank, and capped at 5000 characters.
- Missing level IDs return standard 404 behavior.
- Blank note content returns 400 validation error.
- Backend Notes Foundation did not change frontend files, AI files, backend course, backend quiz, or backend flashcard files.
- Frontend Notes Editor Foundation is implemented in the existing Lesson view.
- Frontend Notes Editor Foundation uses the existing authenticated POST `/api/notes` endpoint through `saveNoteForLevel` in `frontend/src/services/courseApi.js`.
- The Notes editor uses `selectedLevel.levelId` and sends only `levelId` and `content` to the backend.
- The Notes editor saves only on explicit `Save Note` click and does not call backend on page load.
- The Notes editor keeps note content and saved metadata as local Lesson-view state only.
- The Notes editor resets local state when the selected lesson changes.
- The Notes editor now preloads existing saved notes for the selected lesson using authenticated GET `/api/notes/levels/{levelId}`.
- The Notes editor treats content as plain textarea text and does not render raw HTML.
- The Notes editor validates blank notes and 5000-character limit in the frontend before saving.
- The Notes editor does not store note content in localStorage or sessionStorage.
- Backend GET Notes Foundation is implemented.
- Backend GET Notes Foundation endpoint is GET `/api/notes/levels/{levelId}`.
- GET `/api/notes/levels/{levelId}` is authenticated and uses `@AuthenticationPrincipal CurrentUserPrincipal`.
- GET `/api/notes/levels/{levelId}` accepts only `levelId` from the path and never accepts `userId` from the request body, query, path, or headers.
- GET `/api/notes/levels/{levelId}` returns only the authenticated user's own saved note for that level.
- GET `/api/notes/levels/{levelId}` returns safe `NoteResponse` fields only: `noteId`, `levelId`, `content`, `createdAt`, and `updatedAt`.
- GET `/api/notes/levels/{levelId}` returns standard 404 behavior for missing levels.
- GET `/api/notes/levels/{levelId}` returns standard 404 behavior when the level exists but the authenticated user has no note for it.
- GET `/api/notes/levels/{levelId}` returns 401 without a valid Bearer token through the existing security flow.
- POST `/api/notes` remains the note save/update upsert endpoint and was not changed by Backend GET Notes Foundation.
- Frontend Note Preload Foundation is implemented.
- Frontend Note Preload Foundation adds `getNoteForLevel(levelId)` in `frontend/src/services/courseApi.js`.
- Lesson view calls GET `/api/notes/levels/{levelId}` only when a lesson is explicitly opened/selected and a `levelId` exists.
- Lesson note preload uses the existing Bearer access token pattern from tokenStorage and never logs tokens.
- A 200 note preload response prefills the Notes textarea and updates local note metadata such as `noteId` and `updatedAt`.
- A 404 note preload response is treated as a quiet “no saved note yet” state, not a scary error.
- A 401 note preload response shows a safe login-again style message.
- Other preload failures show a safe generic message and do not expose raw backend errors.
- Note preload uses a simple stale-response ignore flag so fast lesson switching does not leak note data into the wrong lesson.
- Save Note still uses existing POST `/api/notes` unchanged.
- Frontend note preload does not store notes in localStorage or sessionStorage and does not render raw HTML.
- Frontend notes preload/fetch using GET `/api/notes/levels/{levelId}` is implemented in the Lesson view.
- Quiz submit, scoring, answer persistence, XP/progress, weak concept detection, and level unlock logic remain unimplemented.
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
- Current persistence boundary for AI-generated output includes:
  - course title
  - course description
  - difficulty
  - level title
  - level contentMarkdown
  - level orderNumber
  - level isBoss
  - level xpReward
  - validated quiz questions from AI success responses
  - validated flashcards from AI success responses
- Coding problems from parsed AI output are still not persisted yet because their DB tables/features are not implemented.
- Placeholder fallback courses keep `sourceType=PLACEHOLDER`.
- Existing deterministic placeholder levels must remain compatible with frontend.
- `CourseController` remains unchanged.
- Frontend remains unchanged.
- DB migrations remain unchanged.
- Manual real-runtime test with Gemini env vars started backend successfully and browser course generation worked.
- Manual DB result for topic `graph traversal bfs real ai test` showed `source_type=PLACEHOLDER`, confirming safe fallback works.
- Manual real AI-success persistence was not confirmed at that time; later retry-once verification confirmed real `source_type=AI` persistence for `graph dfs gemini retry test`.
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
- CourseService logs small safe fallback reason categories only, such as Gemini request failed, AI response validation failed, or AI difficulty did not match request.
- CourseService must not log raw Gemini output, full prompts, API keys, JWTs, tokens, passwords, or secrets.
- Validation was not weakened by prompt/response compatibility polish.
- Invalid AI output is still rejected and never persisted.
- Manual browser generation for `Dynamic Programming Memoization AI Check` completed successfully with no browser error.
- Manual DB result for topic `dynamic programming memoization ai check` showed `source_type=PLACEHOLDER` and `total_xp=225`, confirming fallback safety still works after prompt/response polish.
- Manual real Gemini `source_type=AI` persistence is now confirmed for `graph dfs gemini retry test`.
- Safe Gemini fallback diagnostics are implemented.
- `GeminiException` carries safe Gemini error categories so request/config/extraction failures can be classified without exposing payloads or secrets.
- `GeminiHttpClient` classifies empty-response and response-extraction failures separately from request failures.
- `GeminiService` marks missing Gemini config with an explicit safe category.
- `CourseService` logs a single safe structured fallback diagnostic line with:
  - `reasonCategory`
  - normalized topic
  - requested difficulty
  - whether Gemini was configured
  - exception simple class name
- Safe fallback diagnostics categories currently include:
  - `MISSING_GEMINI_CONFIG`
  - `GEMINI_REQUEST_FAILURE`
  - `EMPTY_GEMINI_RESPONSE_TEXT`
  - `RESPONSE_EXTRACTION_FAILURE`
  - `PARSER_VALIDATION_FAILURE`
  - `REQUESTED_DIFFICULTY_MISMATCH`
  - `UNEXPECTED_AI_INTEGRATION_ERROR`
- Safe fallback diagnostics must never include:
  - API keys
  - JWTs or tokens
  - DB passwords
  - raw Gemini response
  - full prompt
  - secrets
- Manual browser generation for `Recursion Backtracking Safe Diagnostic Test` completed successfully with no browser error.
- Manual DB result for topic `recursion backtracking safe diagnostic test` showed `source_type=PLACEHOLDER` and `total_xp=225`.
- Manual backend log for `recursion backtracking safe diagnostic test` showed `reasonCategory=GEMINI_REQUEST_FAILURE`, `geminiConfigured=true`, and `exceptionType=GeminiException`.
- Gemini HTTP request construction/status diagnostics are implemented.
- `GeminiHttpClient` now normalizes `GEMINI_BASE_URL` so both host-only and `/v1beta` base URLs resolve to one correct `generateContent` endpoint.
- `GeminiHttpClient` keeps Gemini API key env-based and model env-based.
- `GeminiHttpClient` sends the API key as a query parameter without logging it.
- `GeminiHttpClient` keeps request body shape as `contents -> parts -> text` with safe `generationConfig.responseMimeType=application/json`.
- `GeminiException` now carries optional safe HTTP status metadata:
  - `httpStatusCode`
  - `httpStatusFamily`
- `CourseService` fallback log now includes safe HTTP status metadata when available:
  - `httpStatusCode`
  - `httpStatusFamily`
- HTTP status diagnostics must not include:
  - API key
  - full URL with query string
  - raw Gemini response body
  - full prompt
  - JWTs/tokens
  - DB passwords
  - secrets
- Manual browser generation for `Trees Traversal Real Gemini Fix Test` completed successfully with no browser error, but DB still showed `source_type=PLACEHOLDER`.
- Manual retry with `GEMINI_MODEL=gemini-2.0-flash` for `Queue Stack Gemini Model Retry Test` also completed with no browser error, but DB still showed `source_type=PLACEHOLDER`.
- Manual browser generation for `HashMap Gemini Status Diagnostic Test` completed successfully with no browser error.
- Manual DB result for topic `hashmap gemini status diagnostic test` showed `source_type=PLACEHOLDER` and `total_xp=225`.
- Manual backend log for `hashmap gemini status diagnostic test` showed:
  - `reasonCategory=GEMINI_REQUEST_FAILURE`
  - `geminiConfigured=true`
  - `exceptionType=GeminiException`
  - `httpStatusCode=429`
  - `httpStatusFamily=4xx`
- Current real Gemini failure reason is now known at the safe HTTP-status level: Gemini is rejecting the request with HTTP 429.
- HTTP 429 usually indicates quota/rate-limit/usage-limit/overload-style rejection, so the request path is now reaching Gemini but real AI success cannot be confirmed until quota/key/model availability is resolved.
- Current manual real Gemini `source_type=AI` persistence is now confirmed for `graph dfs gemini retry test`.
- API keys and local DB passwords must never be pasted into chat, Build Log, screenshots, or committed files.
- If any Gemini API key was accidentally pasted into chat/logs/screenshots, revoke/delete it and create a new key.
- Real Gemini API key was previously accidentally pasted during manual testing and should be considered exposed. Use only a newly rotated key for future local manual tests.
- A local PostgreSQL password was also pasted during manual testing. Consider rotating the local DB password later. Do not commit or document the real password.
- Do not combine next Gemini 429 handling/AI verification work with frontend course map, quizzes UI, lessons UI, leaderboard, Docker, CI/CD, deployment, code execution, or Phase 2 features unless explicitly scoped.

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
- Frontend Note Preload Foundation note: No blocking issue. Manual browser verification confirmed saved notes preload when reopening a lesson, edited note content preloads after resave, different lessons keep separate note state, no-note 404 is handled quietly, Quiz/Flashcards still render, back navigation works, no runtime errors were visible, and no token/password/secret was shown. Backend, DB migrations, AI, backend course, backend quiz, backend flashcard, and backend note files were unchanged.
- Backend GET Notes Foundation note: No blocking issue. Manual verification confirmed authenticated GET `/api/notes/levels/{levelId}` returns only the current user's saved note, preserves same noteId after update, prevents user 2 from fetching user 1's note before creating their own note, returns user 2's separate note after creation, returns 404 for random valid level UUID, and returns 401 without token. Frontend note preload is still not implemented yet.
- Backend Notes Foundation note: No blocking issue. Manual verification confirmed authenticated POST `/api/notes` creates and updates a note for the same user/level, preserves separate notes for different users on the same level, returns 404 for missing level, 400 for blank content, and 401 without token.
- Frontend Notes Editor Foundation note: No blocking issue. Manual browser verification confirmed notes can be saved and updated from the Lesson view using existing POST `/api/notes`; blank notes are blocked safely; saved note metadata updates locally; lesson content and quiz section still render; no backend changes were made. Existing notes are not preloaded because a GET notes endpoint is not part of the current API scope.
- Backend Quiz Persistence/Fetch Foundation note: No blocking issue. Manual verification confirmed placeholder courses return empty `quizQuestions`, `correctAnswer` is hidden from GET course responses, random valid UUID returns 404, and unauthenticated course fetch returns 401.
- Backend Flashcards Persistence/Fetch Foundation note: No blocking issue. Manual verification confirmed placeholder courses return empty `flashcards`, existing `quizQuestions` arrays remain present/stable, random valid UUID returns 404, and unauthenticated course fetch returns 401. Persisted AI flashcards currently do not carry `conceptTag` because the existing AI flashcard DTO exposes only `front` and `back`.
- Frontend Quiz/Flashcards foundation note: No blocking issue. Quiz and flashcards currently show safe empty states until persisted quiz/flashcard data is available.
- Frontend Real Quiz/Flashcards compatibility fix note: No blocking issue. Manual verification confirmed real backend `quizQuestions` and `flashcards` from an AI BEGINNER course render correctly in Lesson view. Quiz options A/B/C/D render from backend `options` object shape, flashcard Show/Hide Answer works, and `correctAnswer` is not visible.
- Gemini parser validation note: Manual ADVANCED `Greedy Algorithm` generation fell back to placeholder with safe `reasonCategory=PARSER_VALIDATION_FAILURE`. This confirms Gemini was configured and called, but the AI response failed strict parser validation; it is not a frontend bug or missing API key issue. Safe parser diagnostics/prompt compatibility can be considered later if this becomes frequent.
- Resolved: Frontend course generation badge issue. UI previously showed `New Placeholder Course` even when DB confirmed `source_type=AI`. Fixed in commit `08fe631 fix: show course source badge` by exposing `sourceType` in `GenerateCourseResponse`, mapping it from `CourseService`, and updating DashboardShell badge logic. Manual browser verification confirmed `AI Generated Course` displays for a real AI-generated course.
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
- Gemini prompt/response compatibility polish note: Commit `594636e fix: improve gemini response compatibility` was pushed to `main`, and git status was clean afterward.
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
- Safe Gemini fallback diagnostics note: Commit `4780b1e chore: add safe gemini fallback diagnostics` was pushed to `main`, and git status was clean afterward.
- Safe Gemini fallback diagnostics note: Backend `cd backend && .\mvnw.cmd test` passed with 74 tests, 0 failures, 0 errors.
- Safe Gemini fallback diagnostics note: This task added safe fallback categories for Gemini config/request/extraction/parser/difficulty/unexpected failures.
- Safe Gemini fallback diagnostics note: `GeminiException` now carries safe categories.
- Safe Gemini fallback diagnostics note: `GeminiHttpClient` classifies empty-response and response-extraction failures separately from request failures.
- Safe Gemini fallback diagnostics note: `GeminiService` marks missing Gemini config with explicit safe category `MISSING_GEMINI_CONFIG`.
- Safe Gemini fallback diagnostics note: `CourseService` logs category-only safe fallback diagnostics with normalized topic, requested difficulty, Gemini configured boolean, and exception simple class name.
- Safe Gemini fallback diagnostics note: Diagnostics do not log API keys, JWTs, tokens, DB passwords, full prompts, raw Gemini responses, or secrets.
- Safe Gemini fallback diagnostics note: Frontend files were not changed.
- Safe Gemini fallback diagnostics note: DB migrations were not changed.
- Safe Gemini fallback diagnostics note: `CourseController` was not changed.
- Safe Gemini fallback diagnostics note: Manual browser generation for `Recursion Backtracking Safe Diagnostic Test` completed successfully with no browser error.
- Safe Gemini fallback diagnostics note: Manual DB check for `recursion backtracking safe diagnostic test` returned `source_type=PLACEHOLDER` and `total_xp=225`.
- Safe Gemini fallback diagnostics note: Manual backend log showed `reasonCategory=GEMINI_REQUEST_FAILURE`, `geminiConfigured=true`, and `exceptionType=GeminiException`.
- Safe Gemini fallback diagnostics note: Current known real Gemini fallback reason is `GEMINI_REQUEST_FAILURE`.
- Gemini HTTP diagnostics note: Backend `cd backend && .\mvnw.cmd test` passed with 88 tests, 0 failures, 0 errors.
- Gemini HTTP diagnostics note: This task added focused `GeminiHttpClientTest` coverage for URI construction, request JSON shape, success extraction, empty response handling, HTTP failure mapping, and JSON sanitization.
- Gemini HTTP diagnostics note: `GeminiHttpClient` now normalizes the Gemini base URL path before appending `/models/{model}:generateContent`, preventing duplicate `/v1beta` path issues when `GEMINI_BASE_URL` already includes `/v1beta`.
- Gemini HTTP diagnostics note: The Gemini API key remains env-based and is sent as a query parameter without logging it.
- Gemini HTTP diagnostics note: The Gemini model remains env-based and is not hardcoded.
- Gemini HTTP diagnostics note: `GeminiException` now carries safe optional HTTP status metadata.
- Gemini HTTP diagnostics note: `CourseService` fallback logs now include safe `httpStatusCode` and `httpStatusFamily` fields when available.
- Gemini HTTP diagnostics note: Manual browser generation for `Trees Traversal Real Gemini Fix Test` and `Queue Stack Gemini Model Retry Test` still persisted `source_type=PLACEHOLDER`.
- Gemini HTTP diagnostics note: Manual browser generation for `HashMap Gemini Status Diagnostic Test` persisted `source_type=PLACEHOLDER`.
- Gemini HTTP diagnostics note: Manual backend log for `HashMap Gemini Status Diagnostic Test` showed `reasonCategory=GEMINI_REQUEST_FAILURE`, `httpStatusCode=429`, and `httpStatusFamily=4xx`.
- Gemini HTTP diagnostics note: Current known real Gemini root cause is now safe-status-level HTTP 429, meaning Gemini is rejecting the request due to quota/rate-limit/usage-limit/overload-style behavior rather than a frontend, DB, controller, or parser issue.
- Gemini HTTP diagnostics note: Manual real `source_type=AI` persistence was not confirmed at that time because Gemini returned 429; later retry-once verification confirmed `source_type=AI` for `graph dfs gemini retry test`.
- Gemini HTTP diagnostics note: Frontend files were not changed.
- Gemini HTTP diagnostics note: DB migrations were not changed.
- Gemini HTTP diagnostics note: `CourseController` was not changed.
- Gemini course generation wiring note: Real Gemini API key was accidentally pasted in chat/log context during manual testing. It must be revoked/deleted and replaced with a new key. Do not commit or store the exposed key anywhere.
- Gemini course generation wiring note: Local PostgreSQL password was also pasted in chat/log context. Consider rotating the local password later. Do not commit or document the real password.
- Gemini course generation wiring note: Earlier manual diagnostics showed 429 with one key/model/project, but this is no longer the current blocker after a later working key/model request and retry work.
- Gemini retry-once reliability note: Commit `4344e5b fix: retry gemini transient failures` was pushed to `main`, and git status was clean afterward.
- Gemini retry-once reliability note: Backend `cd backend && .\mvnw.cmd test` passed with 93 tests, 0 failures, 0 errors.
- Gemini retry-once reliability note: `CourseService` now performs exactly one additional retry only for transient Gemini request failures with HTTP status family `5xx`, including 503.
- Gemini retry-once reliability note: `CourseService` does not retry 400, 401, 403, 404, 429, missing config, parser validation failure, requested difficulty mismatch, or unexpected non-transient failures.
- Gemini retry-once reliability note: If the retry succeeds with valid parsed AI JSON, `CourseService` persists `sourceType=AI`.
- Gemini retry-once reliability note: If the retry fails again, `CourseService` preserves deterministic `PLACEHOLDER` fallback.
- Gemini retry-once reliability note: Cache-hit behavior remains unchanged and must not call Gemini.
- Gemini retry-once reliability note: Safe retry/fallback logs may include only topic, requested difficulty, exception type, safe HTTP status/family, and retry attempt number.
- Gemini retry-once reliability note: Safe retry/fallback logs must never include API keys, JWTs/tokens, DB passwords, raw Gemini response body, full URL with key, full prompt, or secrets.
- Gemini retry-once reliability note: Manual real Gemini success is now confirmed for normalized topic `graph dfs gemini retry test`.
- Gemini retry-once reliability note: Manual DB course check confirmed title `Mastering Graph DFS for Java Interviews (Beginner)`, `source_type=AI`, and `total_xp=375`.
- Gemini retry-once reliability note: Manual DB levels check confirmed `level_count=4` and `total_level_xp=375`.
- Gemini retry-once reliability note: Frontend, DB migrations, and `CourseController` remained unchanged.
- Gemini retry-once reliability note: Browser UI initially still showed `New Placeholder Course` even when DB confirmed `source_type=AI`; this was fixed later by commit `08fe631 fix: show course source badge`.
- Frontend source badge fix note: Commit `08fe631 fix: show course source badge` was pushed to `main`, and git status was clean afterward.
- Frontend source badge fix note: Frontend `cd frontend && npm run build` passed.
- Frontend source badge fix note: Backend `cd backend && .\mvnw.cmd test` passed with 93 tests, 0 failures, 0 errors.
- Frontend source badge fix note: Manual browser test generated an AI course for linked lists and displayed `AI Generated Course`.
- Frontend source badge fix note: API response now includes `sourceType`.
- Frontend source badge fix note: Backend response DTO/mapping was changed minimally because frontend had no source field before this fix.
- Frontend source badge fix note: `CourseController` was not changed.
- Frontend source badge fix note: DB migrations were not changed.
- Frontend source badge fix note: AI retry logic was not changed.
- Backend course fetch endpoint note: Commit `feat: add course fetch endpoint` was pushed to `main`, and git status was clean afterward.
- Backend course fetch endpoint note: Backend `cd backend && .\mvnw.cmd test` passed with 98 tests, 0 failures, 0 errors.
- Backend course fetch endpoint note: Manual API verification passed for authenticated 200 response, random valid UUID 404 response, and unauthenticated 401 response.
- Backend course fetch endpoint note: Response included safe course details, `sourceType`, `totalXp`, and ordered levels.
- Backend course fetch endpoint note: DB migrations were not changed.
- Backend course fetch endpoint note: AI files were not changed.
- Backend course fetch endpoint note: Frontend files were not changed.
- Backend course fetch endpoint note: Real Gemini env vars initially made two course controller tests non-deterministic during local testing; test isolation was fixed so backend tests pass with 98 tests and do not depend on real Gemini/network availability.
- Frontend Course Map foundation note: No blocking issue is currently known.
- Frontend Course Map foundation note: Changed files are limited to `frontend/src/services/courseApi.js` and `frontend/src/pages/DashboardShell.jsx`.
- Frontend Course Map foundation note: Backend, DB migrations, AI, and backend course files remained unchanged.
- Lesson Page Foundation note: No blocking issue is currently known.
- Lesson Page Foundation note: Changed files are limited to `frontend/src/pages/DashboardShell.jsx`.
- Lesson Page Foundation note: The Lesson view reuses already-fetched Course Map data and does not require a backend refetch.
- Lesson Page Foundation note: Backend, DB migrations, AI, and backend course files remained unchanged.

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
| 28 | 2026-05-05 | Gemini prompt/response compatibility polish | AI / Gemini Prompt-Response Compatibility | PromptBuilder, GeminiHttpClient, CourseService, PromptBuilderTest, GeminiServiceTest, ResponseParserTest | Backend `cd backend && .\mvnw.cmd test` PASS; 74 tests total, 0 failures, 0 errors. Manual browser generation PASS. DB result for `dynamic programming memoization ai check` persisted `source_type=PLACEHOLDER`, confirming fallback safety but not real AI-success persistence. | `594636e fix: improve gemini response compatibility`. Tightened parser-aligned prompt schema, added Gemini fenced/prose JSON sanitization, and added safe fallback reason logging categories. No frontend, no DB migration, no CourseController change. Commit pushed; git status clean. |
| 29 | 2026-05-06 | Safe Gemini fallback diagnostics | AI / Gemini Diagnostics | GeminiException, GeminiHttpClient, GeminiService, CourseService, GeminiServiceTest, CourseServiceTest | Backend `cd backend && .\mvnw.cmd test` PASS; 74 tests total, 0 failures, 0 errors. Manual browser generation PASS. DB result for `recursion backtracking safe diagnostic test` persisted `source_type=PLACEHOLDER`. Backend log showed `reasonCategory=GEMINI_REQUEST_FAILURE`. | `4780b1e chore: add safe gemini fallback diagnostics`. Added safe category-based fallback diagnostics for Gemini config/request/extraction/parser/difficulty/unexpected failures. No frontend, no DB migration, no CourseController change. Real Gemini request failure safely identified as `GEMINI_REQUEST_FAILURE`. Commit pushed; git status clean. |
| 30 | 2026-05-06 | Gemini HTTP request/status diagnostics | AI / Gemini HTTP Integration | GeminiException, GeminiHttpClient, CourseService, CourseServiceTest, GeminiHttpClientTest | Backend `cd backend && .\mvnw.cmd test` PASS; 88 tests total, 0 failures, 0 errors. Manual browser generation PASS. DB result for `hashmap gemini status diagnostic test` persisted `source_type=PLACEHOLDER`. Backend log showed `reasonCategory=GEMINI_REQUEST_FAILURE`, `httpStatusCode=429`, `httpStatusFamily=4xx`. | `e64c355 fix: improve gemini http diagnostics`. Normalized Gemini base URL path, added deterministic GeminiHttpClient tests, added safe HTTP status metadata in GeminiException and fallback logs. No frontend, no DB migration, no CourseController change. At that time manual real AI success was still blocked by Gemini 429 quota/rate-limit/usage-limit-style response. |
| 31 | 2026-05-13 | Gemini retry-once for transient 5xx + real AI success verification | AI / Gemini course generation reliability | backend/src/main/java/com/codequest/course/CourseService.java; backend/src/test/java/com/codequest/course/CourseServiceTest.java; CodeQuest_Build_Log.md | Backend `cd backend && .\mvnw.cmd test` PASS; 93 tests total, 0 failures, 0 errors. Manual browser generation PASS. DB confirmed `graph dfs gemini retry test` persisted `source_type=AI`, `total_xp=375`, `level_count=4`, `total_level_xp=375`. Scope checks clean: no frontend diff, no DB migration diff, CourseController unchanged. | `4344e5b fix: retry gemini transient failures`. Added exactly one retry for transient Gemini 5xx request failures before placeholder fallback. Does not retry 400/401/403/404/429, parser validation failure, difficulty mismatch, or missing config. Frontend badge display bug found after this feature and fixed in the next feature. |
| 32 | 2026-05-13 | Frontend AI/placeholder course source badge fix | Course Generation / Frontend + API Response | frontend/src/pages/DashboardShell.jsx; backend/src/main/java/com/codequest/course/dto/GenerateCourseResponse.java; backend/src/main/java/com/codequest/course/CourseService.java; backend/src/test/java/com/codequest/course/CourseServiceTest.java; CodeQuest_Build_Log.md | Frontend `cd frontend && npm run build` PASS. Backend `cd backend && .\mvnw.cmd test` PASS; 93 tests total, 0 failures, 0 errors. Manual browser verification PASS: AI course displayed `AI Generated Course`. Scope checks clean: DB migration diff empty, CourseController diff empty. | `08fe631 fix: show course source badge`. Added `sourceType` to generate-course response, mapped it from CourseService, added tests for placeholder/AI response source type, and updated DashboardShell badge logic for cache hit, AI, placeholder, and unknown source values. Commit pushed; git status clean. |
| 33 | 2026-05-13 | Backend course fetch endpoint | Course Generation / Course Map Foundation | backend/src/main/java/com/codequest/course/CourseController.java; backend/src/main/java/com/codequest/course/CourseService.java; backend/src/main/java/com/codequest/course/dto/CourseResponse.java; backend/src/main/java/com/codequest/course/dto/CourseLevelResponse.java; backend/src/test/java/com/codequest/course/CourseControllerTest.java; backend/src/test/java/com/codequest/course/CourseServiceTest.java; CodeQuest_Build_Log.md | Backend `cd backend && .\mvnw.cmd test` PASS; 98 tests total, 0 failures, 0 errors. Manual API verification PASS: authenticated GET returned 200 with safe course + ordered levels, random valid UUID returned 404, unauthenticated request returned 401. Scope checks clean: DB migration diff empty, AI diff empty, frontend diff empty. | `feat: add course fetch endpoint`. Added authenticated GET `/api/courses/{courseId}` and safe course/level response DTOs. Preserved POST `/api/courses/generate`; no DB migration, AI, or frontend changes. Commit pushed; git status clean. |
| 34 | 2026-05-13 | Frontend Course Map foundation | Course Generation / Frontend | frontend/src/services/courseApi.js; frontend/src/pages/DashboardShell.jsx; CodeQuest_Build_Log.md | Frontend `cd frontend && npm run build` PASS. Backend `cd backend && .\mvnw.cmd test` PASS; 98 tests total, 0 failures, 0 errors. Manual browser verification PASS: Login -> generate/reuse course -> Open Course Map -> Course Map loaded from GET `/api/courses/{courseId}` with ordered levels, content preview, and working Back button. | `427f9da feat: add frontend course map foundation`. Added explicit-click `Open Course Map` flow, frontend GET helper, local loading/error state, course map view, ordered level cards, and plain-text content previews. Backend, DB migrations, AI, and backend course files unchanged. |
| 35 | 2026-05-13 | Lesson Page Foundation | Frontend | frontend/src/pages/DashboardShell.jsx; CodeQuest_Build_Log.md | Frontend `cd frontend && npm run build` PASS. Manual browser verification PASS: Login -> generate/reuse course -> Open Course Map -> Open Lesson -> Back to Course Map -> Back to dashboard/generated result. | `2bc37dc feat: add lesson page foundation`. Added frontend-only Lesson view using existing fetched Course Map data, local selectedLevel state, Open Lesson action, readable plain-text lesson content, and Back to Course Map. Backend, DB migrations, AI, and backend course files unchanged. |
| 36 | 2026-05-13 | Frontend Quiz Panel Foundation and Frontend Flashcards Panel Foundation | Frontend | frontend/src/pages/DashboardShell.jsx; CodeQuest_Build_Log.md | Frontend `cd frontend && npm run build` PASS. Manual browser verification PASS: Login -> generate/reuse course -> Open Course Map -> Open Lesson -> verify Quiz empty state -> verify Flashcards empty state -> Back to Course Map -> Back to dashboard/generated result. | `5c6cb42 feat: add lesson quiz and flashcards panels`. Added frontend-only Quiz and Flashcards panels inside Lesson view with safe empty states, future-compatible quiz/flashcard data normalization, local-only quiz selection state, local-only flashcard reveal state, and reset-on-lesson-change behavior. No backend quiz/flashcard calls, no DB migration, no scoring, no XP/progress, no persistence. |
| 37 | 2026-05-13 | Backend Quiz Persistence/Fetch Foundation | Backend / Quiz | backend/src/main/resources/db/migration/V4__create_quizzes_table.sql; backend/src/main/java/com/codequest/quiz/Quiz.java; backend/src/main/java/com/codequest/quiz/QuizRepository.java; backend/src/main/java/com/codequest/quiz/dto/QuizOptionsResponse.java; backend/src/main/java/com/codequest/quiz/dto/QuizQuestionResponse.java; backend/src/main/java/com/codequest/course/CourseService.java; backend/src/main/java/com/codequest/course/dto/CourseLevelResponse.java; backend/src/test/java/com/codequest/course/CourseServiceTest.java; backend/src/test/java/com/codequest/course/CourseControllerTest.java | Backend `cd backend && .\mvnw.cmd test` PASS with 100 tests, 0 failures, 0 errors. Manual API verification PASS: register/login/generate/fetch, `quizQuestions` present as empty arrays for placeholder levels, `correctAnswer` not exposed, random valid UUID returned 404, and no-token request returned 401. | `4fff8e6 feat: persist and fetch course quiz questions`. Added V4 quizzes table, quiz entity/repository/safe DTOs, AI-success quiz persistence, and safe quizQuestions in GET `/api/courses/{courseId}`. Frontend unchanged. AI PromptBuilder/GeminiHttpClient/GeminiService/ResponseParser unchanged. No quiz submit, scoring, XP/progress, weak concept detection, or unlock logic. |
| 38 | 2026-05-13 | Backend Flashcards Persistence/Fetch Foundation | Backend / Flashcard | backend/src/main/resources/db/migration/V5__create_flashcards_table.sql; backend/src/main/java/com/codequest/flashcard/Flashcard.java; backend/src/main/java/com/codequest/flashcard/FlashcardRepository.java; backend/src/main/java/com/codequest/flashcard/dto/FlashcardResponse.java; backend/src/main/java/com/codequest/course/CourseService.java; backend/src/main/java/com/codequest/course/dto/CourseLevelResponse.java; backend/src/test/java/com/codequest/course/CourseServiceTest.java; backend/src/test/java/com/codequest/course/CourseControllerTest.java | Backend `cd backend && .\mvnw.cmd test` PASS with 101 tests, 0 failures, 0 errors. Manual API verification PASS: Flyway V5 validated, register/login/generate/fetch, `flashcards` present as empty arrays for placeholder levels, existing `quizQuestions` present/stable, random valid UUID returned 404, and no-token request returned 401. | `8c3b44e feat: persist and fetch course flashcards`. Added V5 flashcards table, flashcard entity/repository/safe DTO, AI-success flashcard persistence, and safe flashcards in GET `/api/courses/{courseId}`. Frontend unchanged. AI PromptBuilder/GeminiHttpClient/GeminiService/ResponseParser unchanged. Existing quizQuestions behavior unchanged. No notes, progress, XP/rank/streak, quiz submit, unlock logic, Piston, or Phase 2 work. |
| 39 | 2026-05-14 | Frontend Real Quiz/Flashcards Display Compatibility Check/Fix | Frontend | frontend/src/pages/DashboardShell.jsx | Frontend `cd frontend && npm run build` PASS. Manual browser verification PASS: placeholder/cached lesson showed safe empty states; real Gemini BEGINNER AI course `Trie Data Structure Quiz Flashcards AI Test` displayed AI lesson content, real `quizQuestions`, options A/B/C/D, explanation, real `flashcards`, and Show/Hide Answer behavior. `correctAnswer` was not visible. Scope checks clean: backend migrations, AI, backend course, backend quiz, and backend flashcard diffs empty. | `eb46a9e fix: support backend quiz options shape`. Added small quiz options normalizer so backend `quizQuestions[].options` object shape `{A, B, C, D}` renders in the existing Lesson Quiz panel. Flashcards were already compatible with backend `flashcards` shape. No backend, DB migration, AI, package, submit/scoring/progress, notes, unlock, Piston, or Phase 2 work. |
| 40 | 2026-05-14 | Backend Notes Foundation | Backend / Note | backend/src/main/resources/db/migration/V6__create_notes_table.sql; backend/src/main/java/com/codequest/note/Note.java; backend/src/main/java/com/codequest/note/NoteRepository.java; backend/src/main/java/com/codequest/note/NoteService.java; backend/src/main/java/com/codequest/note/NoteController.java; backend/src/main/java/com/codequest/note/NoteMapper.java; backend/src/main/java/com/codequest/note/dto/SaveNoteRequest.java; backend/src/main/java/com/codequest/note/dto/NoteResponse.java; backend/src/test/java/com/codequest/note/NoteServiceTest.java; backend/src/test/java/com/codequest/note/NoteControllerTest.java | Backend `cd backend && .\mvnw.cmd test` PASS with 113 tests, 0 failures, 0 errors. Manual API verification PASS: note create, same-user/same-level update with same noteId, second user separate note, missing level 404, blank content 400, no-token 401. Scope checks clean: frontend, AI, backend course, backend quiz, and backend flashcard diffs empty. | `58abd4d feat: add backend notes foundation`. Added V6 notes table, Note entity/repository/service/controller/mapper, validated request DTO, safe response DTO, authenticated POST `/api/notes` upsert per user/level, and focused note service/controller tests. No frontend, AI, quiz, flashcard, course, quiz submit, scoring, XP/progress, unlock, Piston, deployment, or Phase 2 work. |
| 41 | 2026-05-14 | Frontend Notes Editor Foundation | Frontend / Notes | frontend/src/services/courseApi.js; frontend/src/pages/DashboardShell.jsx | Frontend `cd frontend && npm run build` PASS. Manual browser verification PASS: Login -> generate/reuse course -> Open Course Map -> Open Lesson -> save note -> update same note -> blank note blocked safely -> lesson/quiz sections still visible -> no secrets/tokens visible. Scope checks clean: backend migrations, AI, backend course, backend quiz, backend flashcard, and backend note diffs empty. | `b1943c4 feat: add frontend notes editor foundation`. Added `saveNoteForLevel({ levelId, content })` helper and frontend-only Notes editor in Lesson view. Uses existing authenticated POST `/api/notes`, local validation, character counter, loading/success/error states, and local saved metadata. No frontend note preload, DB migration, AI, quiz submit, scoring, XP/progress, unlock logic, Piston, deployment, or Phase 2 work. |
| 42 | 2026-05-14 | Backend GET Notes Foundation | Backend / Note | backend/src/main/java/com/codequest/note/NoteController.java; backend/src/main/java/com/codequest/note/NoteService.java; backend/src/test/java/com/codequest/note/NoteControllerTest.java; backend/src/test/java/com/codequest/note/NoteServiceTest.java | Backend `cd backend && .\mvnw.cmd test` PASS with 121 tests, 0 failures, 0 errors. Manual API verification PASS: user 1 save/fetch/update/fetch, user 2 404 before own note, user 2 separate note fetch, random level UUID 404, no-token 401. Scope checks clean: frontend, DB migrations, AI, backend course, backend quiz, and backend flashcard diffs empty. | `2606bfb feat: add get note by level endpoint`. Added authenticated GET `/api/notes/levels/{levelId}` for current user's saved note only. Reused existing safe NoteResponse and note ownership logic. No migration, frontend, AI, course, quiz, flashcard, POST notes, scoring, XP/progress, unlock, Piston, deployment, or Phase 2 work. |

| 43 | 2026-05-14 | Frontend Note Preload Foundation | Frontend / Notes | frontend/src/services/courseApi.js; frontend/src/pages/DashboardShell.jsx | Frontend `cd frontend && npm run build` PASS. Manual browser verification PASS: saved notes preload on reopening the same lesson, edited note content preloads after resave, different lessons keep separate notes with no leakage, quiet no-note 404 state works, Quiz/Flashcards still render, back navigation works, no console runtime errors, and no secrets/tokens visible. Scope checks clean: backend migrations, AI, backend course, backend quiz, backend flashcard, and backend note diffs empty. | `e7ca3b7 feat: preload lesson notes`. Added authenticated `getNoteForLevel(levelId)` helper and lesson-scoped note preload flow using GET `/api/notes/levels/{levelId}`. 404 is treated as no saved note, save still uses POST `/api/notes`, stale-response protection prevents cross-lesson note leakage, and no backend/DB/AI changes were made. |

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
| 2026-05-06 | `cd backend && .\mvnw.cmd test` after safe Gemini fallback diagnostics | PASS | Backend tests passed: 74 tests, 0 failures, 0 errors. Tests verify safe fallback categorization for missing config, Gemini request failure, parser validation failure, difficulty mismatch, valid AI success, and preserved placeholder behavior. No real Gemini calls in tests. | Yes |
| 2026-05-06 | `git diff -- frontend`, `git diff -- backend/src/main/resources/db/migration`, `git diff -- backend/src/main/java/com/codequest/course/CourseController.java` after safe Gemini fallback diagnostics | PASS | No frontend diff, no DB migration diff, no CourseController diff. | Yes |
| 2026-05-06 | Manual browser course generation with safe Gemini diagnostics | PASS | Browser generated placeholder course for `Recursion Backtracking Safe Diagnostic Test` without crashing. DB showed `source_type=PLACEHOLDER`; backend log showed safe `reasonCategory=GEMINI_REQUEST_FAILURE`. | Yes |
| 2026-05-06 | `cd backend && .\mvnw.cmd test` after Gemini HTTP request/status diagnostics | PASS | Backend tests passed: 88 tests, 0 failures, 0 errors. Tests cover GeminiHttpClient URI construction, request JSON shape, success extraction, empty response handling, HTTP failure status mapping for 400/401/403/404/429/5xx, JSON sanitization, and CourseService safe fallback diagnostic message content. No real Gemini calls in tests. | Yes |
| 2026-05-06 | `git diff -- frontend`, `git diff -- backend/src/main/resources/db/migration`, `git diff -- backend/src/main/java/com/codequest/course/CourseController.java` after Gemini HTTP request/status diagnostics | PASS | No frontend diff, no DB migration diff, no CourseController diff. | Yes |
| 2026-05-06 | Manual browser course generation with Gemini HTTP status diagnostics | PASS | Browser generated placeholder course for `HashMap Gemini Status Diagnostic Test` without crashing. DB showed `source_type=PLACEHOLDER`; backend log showed safe `reasonCategory=GEMINI_REQUEST_FAILURE`, `httpStatusCode=429`, and `httpStatusFamily=4xx`. | Yes |
| 2026-05-13 | `cd backend && .\mvnw.cmd test` after Gemini retry-once 5xx reliability fix | PASS | Backend tests passed: 93 tests, 0 failures, 0 errors. Tests cover retry-once for transient 5xx/503, no retry for 403/429, parser fallback behavior, and safe retry log formatting. | Yes |
| 2026-05-13 | `git diff -- frontend`, `git diff -- backend/src/main/resources/db/migration`, `git diff -- backend/src/main/java/com/codequest/course/CourseController.java` after Gemini retry-once 5xx reliability fix | PASS | No frontend diff, no DB migration diff, no CourseController diff. | Yes |
| 2026-05-13 | Manual browser course generation + DB verification for `Graph DFS Gemini Retry Test` | PASS | Browser generated course. DB confirmed `source_type=AI`, `total_xp=375`, `level_count=4`, `total_level_xp=375`. | Yes |
| 2026-05-13 | `cd frontend && npm run build` after source badge fix | PASS | Frontend build succeeded after DashboardShell badge logic update. | Yes |
| 2026-05-13 | `cd backend && .\mvnw.cmd test` after source badge fix | PASS | Backend tests passed: 93 tests, 0 failures, 0 errors. Tests verify `sourceType` exposure for placeholder and AI generated course responses. | Yes |
| 2026-05-13 | `git diff -- backend/src/main/resources/db/migration` after source badge fix | PASS | Empty diff; no DB migration changes. | Yes |
| 2026-05-13 | `git diff -- backend/src/main/java/com/codequest/course/CourseController.java` after source badge fix | PASS | Empty diff; CourseController unchanged. | Yes |
| 2026-05-13 | Manual browser course generation after source badge fix | PASS | AI-generated Linked List course displayed `AI Generated Course` badge in DashboardShell. | Yes |
| 2026-05-13 | `cd backend && .\mvnw.cmd test` after backend course fetch endpoint | PASS | Backend tests passed: 98 tests, 0 failures, 0 errors. Tests cover authenticated fetch, 404 not found, 401 unauthorized, ordered levels, and no Gemini calls. | Yes |
| 2026-05-13 | `cd frontend && npm run build` after Frontend Course Map foundation | PASS | Frontend build passed after adding explicit-click course map fetch and Course Map view. | Yes |
| 2026-05-13 | `cd backend && .\mvnw.cmd test` after Frontend Course Map foundation verification | PASS | Backend tests passed: 98 tests, 0 failures, 0 errors. Backend remained unchanged for this feature. | Yes |
| 2026-05-13 | Manual browser verification after Frontend Course Map foundation | PASS | Login -> generate/reuse course -> Open Course Map loaded from GET `/api/courses/{courseId}`. Course Map showed title, description, difficulty, sourceType, totalXp, ordered levels, XP reward, Boss/Standard badge, content preview, working Back button, and no visible secrets/tokens. | Yes |
| 2026-05-13 | `cd frontend && npm run build` after Lesson Page Foundation | PASS | Frontend build passed after adding frontend-only Lesson view and Open Lesson flow. | Yes |
| 2026-05-13 | Manual API: authenticated GET `/api/courses/{courseId}` | PASS | Returned 200 OK with `courseId`, `title`, `description`, `difficulty`, `sourceType=AI`, `totalXp=375`, and 4 ordered levels. | Yes |
| 2026-05-13 | Manual API: random valid UUID fetch | PASS | Returned 404 for missing course. | Yes |
| 2026-05-13 | Manual API: GET `/api/courses/{courseId}` without token | PASS | Returned 401 for unauthenticated request. | Yes |
| 2026-05-13 | Scope checks after backend course fetch endpoint | PASS | `git diff -- backend/src/main/resources/db/migration`, `git diff -- backend/src/main/java/com/codequest/ai`, and `git diff -- frontend` were empty. | Yes |
| 2026-05-13 | `cd frontend && npm run build` after Frontend Quiz/Flashcards panels | PASS | Frontend build passed after adding frontend-only Quiz and Flashcards panels to Lesson view. | Yes |
| 2026-05-13 | `cd backend && .\mvnw.cmd test` after Backend Quiz Persistence/Fetch Foundation | PASS | Backend tests passed with 100 tests, 0 failures, 0 errors after adding quiz persistence/fetch foundation. | Yes |
| 2026-05-13 | `cd backend && .\mvnw.cmd test` after Backend Flashcards Persistence/Fetch Foundation | PASS | Backend tests passed with 101 tests, 0 failures, 0 errors after adding flashcards persistence/fetch foundation. | Yes |
| 2026-05-14 | `cd frontend && npm run build` after Frontend Real Quiz/Flashcards Display Compatibility Check/Fix | PASS | Frontend build passed after adding quiz option normalization for backend `quizQuestions[].options` object shape. | Yes |
| 2026-05-14 | `cd backend && .\mvnw.cmd test` after Backend Notes Foundation | PASS | Backend tests passed with 113 tests, 0 failures, 0 errors after adding notes persistence/upsert foundation. | Yes |
| 2026-05-14 | `cd frontend && npm run build` after Frontend Notes Editor Foundation | PASS | Frontend build passed after adding lesson notes editor save flow through existing POST `/api/notes`. | Yes |
| 2026-05-14 | `cd backend && .\mvnw.cmd test` after Backend GET Notes Foundation | PASS | Backend tests passed with 121 tests, 0 failures, 0 errors after adding authenticated current-user note fetch endpoint. | Yes |

| 2026-05-14 | `cd frontend && npm run build` after Frontend Note Preload Foundation | PASS | Frontend build passed after adding lesson note preload flow with GET `/api/notes/levels/{levelId}`. | Yes |

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
| 2026-05-05 | Gemini course generation real AI-success check | Manual real Gemini run should ideally persist `source_type=AI` for valid Gemini output | Not confirmed manually at that time; later confirmed with `graph dfs gemini retry test` | Completed later |
| 2026-05-05 | Gemini prompt/response compatibility polish tests | `cd backend && .\mvnw.cmd test` | 74 tests pass; prompt/schema compatibility, response sanitization, and validation safety tests pass | Passed |
| 2026-05-05 | Gemini prompt/response compatibility scope check | `git diff -- frontend`, `git diff -- db/migration`, `git diff -- CourseController` | No frontend, DB migration, or CourseController changes | Passed |
| 2026-05-05 | Gemini prompt/response browser check | Generate new uncached topic `Dynamic Programming Memoization AI Check` from DashboardShell with Gemini env vars | Course generation completes without browser error | Passed |
| 2026-05-05 | Gemini prompt/response DB check | Query `courses` for normalized topic `dynamic programming memoization ai check` | Row exists with `source_type=PLACEHOLDER`, `total_xp=225` | Passed; fallback safety confirmed at that time; later retry-once verification confirmed real AI success |
| 2026-05-06 | Safe Gemini fallback diagnostics tests | `cd backend && .\mvnw.cmd test` | 74 tests pass; safe fallback categories and existing AI/fallback behavior remain stable | Passed |
| 2026-05-06 | Safe Gemini fallback diagnostics scope check | `git diff -- frontend`, `git diff -- db/migration`, `git diff -- CourseController` | No frontend, DB migration, or CourseController changes | Passed |
| 2026-05-06 | Safe Gemini fallback diagnostics browser check | Generate new uncached topic `Recursion Backtracking Safe Diagnostic Test` from DashboardShell with Gemini env vars | Course generation completes without browser error | Passed |
| 2026-05-06 | Safe Gemini fallback diagnostics DB check | Query `courses` for normalized topic `recursion backtracking safe diagnostic test` | Row exists with `source_type=PLACEHOLDER`, `total_xp=225` | Passed; fallback safety confirmed |
| 2026-05-06 | Safe Gemini fallback diagnostics log check | Inspect backend log after browser generation | Safe log shows `reasonCategory=GEMINI_REQUEST_FAILURE`, `geminiConfigured=true`, `exceptionType=GeminiException`; no raw Gemini output or secrets included | Passed; real Gemini request failure identified safely |
| 2026-05-06 | Gemini HTTP request/status diagnostics tests | `cd backend && .\mvnw.cmd test` | 88 tests pass; Gemini HTTP client tests and status diagnostics pass | Passed |
| 2026-05-06 | Gemini HTTP request/status diagnostics scope check | `git diff -- frontend`, `git diff -- db/migration`, `git diff -- CourseController` | No frontend, DB migration, or CourseController changes | Passed |
| 2026-05-06 | Gemini HTTP URL normalization browser check | Generate new uncached topic `Trees Traversal Real Gemini Fix Test` from DashboardShell with Gemini env vars | Course generation completes without browser error | Passed; DB still showed PLACEHOLDER |
| 2026-05-06 | Gemini model retry browser check | Retry with `GEMINI_MODEL=gemini-2.0-flash`, generate `Queue Stack Gemini Model Retry Test` | Course generation completes without browser error | Passed; DB still showed PLACEHOLDER |
| 2026-05-06 | Gemini HTTP status diagnostics browser check | Generate new uncached topic `HashMap Gemini Status Diagnostic Test` from DashboardShell with Gemini env vars | Course generation completes without browser error | Passed |
| 2026-05-06 | Gemini HTTP status diagnostics DB check | Query `courses` for normalized topic `hashmap gemini status diagnostic test` | Row exists with `source_type=PLACEHOLDER`, `total_xp=225` | Passed; fallback safety confirmed |
| 2026-05-06 | Gemini HTTP status diagnostics log check | Inspect backend log after browser generation | Safe log shows `reasonCategory=GEMINI_REQUEST_FAILURE`, `httpStatusCode=429`, `httpStatusFamily=4xx`; no raw Gemini output or secrets included | Passed; real Gemini quota/rate-limit/usage-limit-style rejection identified safely |
| 2026-05-13 | Gemini retry-once transient 5xx tests | `cd backend && .\mvnw.cmd test` | 93 tests pass with retry/non-retry coverage | Passed |
| 2026-05-13 | Gemini retry-once scope check | `git diff -- frontend`, `git diff -- db/migration`, `git diff -- CourseController` | No frontend, DB migration, or CourseController changes | Passed |
| 2026-05-13 | Gemini retry-once browser check | Generate new uncached topic `Graph DFS Gemini Retry Test` from DashboardShell with working Gemini env vars | Course generation completes without browser error | Passed |
| 2026-05-13 | Gemini retry-once DB course check | Query `courses` for normalized topic `graph dfs gemini retry test` | Row exists with `source_type=AI`, title `Mastering Graph DFS for Java Interviews (Beginner)`, and `total_xp=375` | Passed; real AI persistence confirmed |
| 2026-05-13 | Gemini retry-once DB levels check | Query `levels` for the generated course | `level_count=4` and `total_level_xp=375` | Passed |
| 2026-05-13 | Gemini retry-once frontend display observation | Browser result badge after AI course generation | Backend DB said `source_type=AI`; UI still said `New Placeholder Course` | Passed; frontend display bug identified and later fixed in commit `08fe631 fix: show course source badge` |
| 2026-05-13 | Frontend source badge fix build | `cd frontend && npm run build` | Vite build succeeds | Passed |
| 2026-05-13 | Frontend source badge fix backend tests | `cd backend && .\mvnw.cmd test` | 93 tests, 0 failures, 0 errors | Passed |
| 2026-05-13 | Frontend source badge fix API response shape | Inspect course generation response behavior | `sourceType` is now exposed in `GenerateCourseResponse` | Passed |
| 2026-05-13 | Frontend source badge fix browser verification | Generate AI course for linked lists from DashboardShell | UI shows `AI Generated Course` badge | Passed |
| 2026-05-13 | Frontend source badge fix scope check | `git diff -- backend/src/main/resources/db/migration` and `git diff -- backend/src/main/java/com/codequest/course/CourseController.java` | Both diffs empty | Passed |
| 2026-05-13 | Backend course fetch endpoint authenticated success | Register/login, generate course, then GET `/api/courses/{courseId}` with Bearer token | 200 OK with safe course fields, `sourceType`, `totalXp`, and ordered levels | Passed |
| 2026-05-13 | Backend course fetch endpoint missing course | GET `/api/courses/{random-valid-uuid}` with Bearer token | 404 standard error response | Passed |
| 2026-05-13 | Backend course fetch endpoint unauthenticated request | GET `/api/courses/{courseId}` without Authorization header | 401 Unauthorized | Passed |
| 2026-05-13 | Backend course fetch endpoint backend tests | `cd backend && .\mvnw.cmd test` | 98 tests, 0 failures, 0 errors, BUILD SUCCESS | Passed |
| 2026-05-13 | Backend course fetch endpoint scope check | `git diff -- backend/src/main/resources/db/migration`, `git diff -- backend/src/main/java/com/codequest/ai`, `git diff -- frontend` | All diffs empty | Passed |
| 2026-05-13 | Frontend Course Map foundation browser verification | Login -> generate/reuse course -> Open Course Map -> Back | Course Map loads from GET `/api/courses/{courseId}` and shows title, description, difficulty, sourceType, totalXp, ordered levels, XP reward, Boss/Standard badge, content preview, working Back button, and no visible secrets/tokens | Passed |
| 2026-05-13 | Lesson Page Foundation | Login -> generate/reuse course -> Open Course Map -> Open Lesson -> Back to Course Map -> Back to dashboard/generated result | Lesson view shows course title, level number, level title, XP reward, Boss/Standard badge, and readable plain-text `contentMarkdown`; no secrets/tokens visible | Passed |
| 2026-05-13 | Frontend Quiz Panel Foundation and Frontend Flashcards Panel Foundation | Login -> generate/reuse course -> Open Course Map -> Open Lesson -> verify Quiz empty state -> verify Flashcards empty state -> Back to Course Map -> Back to dashboard/generated result | Quiz and Flashcards sections appeared with safe empty states; lesson content and back flow still worked; no secrets/tokens visible; no backend quiz/flashcard call needed; no blocking issue known | Passed |
| 2026-05-13 | Backend Quiz Persistence/Fetch Foundation | Register/login -> POST `/api/courses/generate` -> GET `/api/courses/{courseId}` -> inspect `quizQuestions` -> check hidden `correctAnswer` -> random UUID 404 -> no-token 401 | GET course response included safe `quizQuestions` arrays on levels; placeholder levels returned empty arrays; `correctAnswer` was not exposed; missing course returned 404; unauthenticated fetch returned 401; no secrets/tokens visible | Passed |
| 2026-05-13 | Backend Flashcards Persistence/Fetch Foundation | Flyway V5 validated -> register/login -> POST `/api/courses/generate` -> GET `/api/courses/{courseId}` -> inspect `flashcards` and `quizQuestions` -> random UUID 404 -> no-token 401 | GET course response included safe `flashcards` arrays on levels; placeholder levels returned empty arrays; existing `quizQuestions` arrays were still present/stable; missing course returned 404; unauthenticated fetch returned 401; no secrets/tokens visible | Passed |
| 2026-05-14 | Frontend Real Quiz/Flashcards Display Compatibility Check/Fix | Login -> generate/reuse course -> Open Course Map -> Open Lesson -> verify placeholder/cached empty states -> start backend with Gemini env vars -> generate new BEGINNER AI course `Trie Data Structure Quiz Flashcards AI Test` -> Open Course Map -> Open Lesson -> inspect Quiz and Flashcards | Placeholder/cached lesson still showed safe empty states. Real AI lesson displayed persisted quiz question, options A/B/C/D, concept/explanation, and flashcards with Show/Hide Answer. `correctAnswer` was not visible; no secrets/tokens visible; lesson/course map/back flow still worked. | Passed |
| 2026-05-14 | Backend Notes Foundation | Flyway V6 active -> register/login user 1 -> generate/reuse course -> GET course levelId -> POST `/api/notes` create -> POST `/api/notes` update same user/level -> register/login user 2 -> POST `/api/notes` same level -> missing level -> blank content -> no-token request -> diff safety checks | User 1 note create returned noteId, levelId, content, createdAt, updatedAt; same user/level update returned same noteId and updated content; user 2 got a different noteId for the same level; missing level returned 404; blank content returned 400; no-token request returned 401; frontend/AI/course/quiz/flashcard diffs empty; no secrets/tokens visible. | Passed |
| 2026-05-14 | Frontend Notes Editor Foundation | Login -> generate/reuse course -> Open Course Map -> Open Lesson -> save note -> edit and save note again -> blank note attempt -> inspect Lesson/Quiz visibility -> verify no visible secrets | Notes section appeared in Lesson view. Save returned success, `Last saved` metadata and `Note ID` appeared, repeated save updated the same noteId, blank content was blocked with `Please enter a note before saving.`, character counter worked, lesson content and Quiz section still rendered, and no token/password/secret was visible. | Passed |
| 2026-05-14 | Backend GET Notes Foundation | User 1 save note -> GET `/api/notes/levels/{levelId}` -> update with POST -> GET again -> user 2 GET before own note -> user 2 save/fetch own note -> random valid level UUID -> no-token GET -> scope checks | User 1 fetched saved note; update returned same noteId and updated content; user 2 could not fetch user 1 note and got 404 before creating own note; user 2 fetched own separate note with different noteId; random level UUID returned 404; no-token request returned 401; frontend/migration/AI/course/quiz/flashcard diffs empty; no userId/token/password/secret exposed. | Passed |

| 2026-05-14 | Frontend Note Preload Foundation | Login -> generate/reuse course -> Open Course Map -> Open Lesson -> save note -> Back to Course Map -> reopen same lesson -> edit/save -> reopen -> open a different lesson -> save separate note -> switch between lessons -> verify Quiz/Flashcards/back navigation/console/security | Saved note preloaded into textarea on reopening same lesson; updated content preloaded after resave; different lessons kept separate note content and no previous note leaked; no-note state was handled quietly; Quiz and Flashcards still rendered; Back to Course Map and Back to Home worked; browser console had no red runtime errors; no token/password/secret was visible. | Passed |

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

Expected Flyway behavior after Backend Notes Foundation:
```text
Successfully validated 6 migrations
Schema "public" is up to date. No migration necessary.
```

If V6 has not yet been applied to a local database, startup should apply `V6__create_notes_table.sql` successfully.

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
- Safe Gemini fallback diagnostics are implemented.
- Gemini HTTP request/status diagnostics are implemented.

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
sourceType: PLACEHOLDER
levels: 3 items
level 1: Introduction to Binary Search, orderNumber 1, isBoss false, xpReward 50
level 2: Practice Binary Search, orderNumber 2, isBoss false, xpReward 75
level 3: Binary Search Boss Challenge, orderNumber 3, isBoss true, xpReward 100
```

Expected AI response when Gemini succeeds:
```text
courseId: exists
title: AI-generated title
description: AI-generated description
cacheHit: False
sourceType: AI
levels: 1 to 10 validated ordered levels
total XP depends on validated AI level XP values
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
sourceType preserved from cached course
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
- `GenerateCourseResponse` now includes `sourceType`.
- Latest manual real Gemini verification confirmed sourceType=AI for graph dfs gemini retry test after retry-once reliability work.
- Latest manual browser source badge verification confirmed `AI Generated Course` appears for real AI course output.
- Earlier manual Gemini checks persisted sourceType=PLACEHOLDER during fallback diagnostics.
- Latest manual real Gemini verification confirmed sourceType=AI for graph dfs gemini retry test after retry-once reliability work.
- Earlier safe HTTP diagnostics showed 429 / 4xx for one key/model/project, but this is no longer the current blocker.
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

Expected after prompt/response compatibility polish and safe diagnostics:
```text
Tests run: 74
Failures: 0
Errors: 0
BUILD SUCCESS
```

Expected after HTTP request/status diagnostics:
```text
Tests run: 88
Failures: 0
Errors: 0
BUILD SUCCESS
```

Expected after retry-once and source badge response DTO update:
```text
Tests run: 93
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
sourceType=PLACEHOLDER.
No frontend crash.
Safe fallback category should be MISSING_GEMINI_CONFIG if diagnostics are active.
```

### Real Gemini runtime check
Use only with a valid local Gemini key. Do not paste key in chat, screenshots, logs, or Build Log.
```powershell
$env:DATABASE_URL="jdbc:postgresql://localhost:5432/codequest"
$env:DATABASE_USERNAME="postgres"
$env:DATABASE_PASSWORD="<your-local-postgres-password>"
$env:JWT_SECRET="dev-only-change-this-secret-dev-only-change-this-secret"

$env:GEMINI_API_KEY="<your-real-gemini-key>"
$env:GEMINI_MODEL="gemini-2.0-flash"
$env:GEMINI_BASE_URL="https://generativelanguage.googleapis.com"

cd backend
.\mvnw.cmd spring-boot:run
```

Generate a new uncached topic from frontend/API:
```text
Topic: HashMap Gemini Status Diagnostic Test
Difficulty: BEGINNER
Goal: Learn HashMap for Java DSA interviews
```

Check DB source type:
```powershell
$env:Path += ";C:\Program Files\PostgreSQL\17\bin"
psql -U postgres -W -d codequest -c "select title, difficulty, source_type, total_xp, created_at from courses where normalized_topic='hashmap gemini status diagnostic test' order by created_at desc limit 1;"
```

Observed latest manual result:
```text
title: Hashmap Gemini Status Diagnostic Test
difficulty: BEGINNER
source_type: PLACEHOLDER
total_xp: 225
reasonCategory: GEMINI_REQUEST_FAILURE
geminiConfigured: true
exceptionType: GeminiException
httpStatusCode: 429
httpStatusFamily: 4xx
```

Meaning:
```text
Manual fallback safety confirmed.
Manual real AI-success persistence not confirmed yet.
Gemini configuration is present.
CourseService attempted real Gemini.
Gemini HTTP request reached the Gemini integration path.
Gemini returned/reported HTTP 429, which usually indicates quota/rate-limit/usage-limit/overload-style rejection.
Next task should handle 429 gracefully and/or retry manual sourceType=AI only after quota/key/model availability is resolved.
```

Important Gemini wiring boundaries:
- No frontend changes.
- No DB migration changes.
- CourseController unchanged.
- Tests must not call real Gemini.
- API key must be env/config only.
- Do not log or expose API key.
- Do not log full prompts or raw Gemini output.
- Do not log full URL containing query key.
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
Manual real source_type=AI persistence was not confirmed during this earlier polish check.
Safe diagnostics later identified real Gemini fallback reason as GEMINI_REQUEST_FAILURE.
HTTP status diagnostics later identified the specific safe status as 429 / 4xx.
Final retry-once verification later confirmed real source_type=AI persistence for graph dfs gemini retry test.
```

Important boundaries:
- No raw Gemini response should be logged.
- No API key should be pasted, logged, committed, or stored in Build Log.
- No DB migration should be added for this diagnostic step unless explicitly scoped.
- No frontend changes should be made unless explicitly scoped.

## Safe Gemini Fallback Diagnostics Manual Test Commands
Use this after the safe diagnostics task.

### Automated verification
```powershell
cd backend
.\mvnw.cmd test
cd ..
```

Expected:
```text
Tests run: 74 or more depending on later features
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

### Real Gemini diagnostic check
Start backend with DB/JWT/Gemini env vars using a rotated key only:
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
Topic: Recursion Backtracking Safe Diagnostic Test
Difficulty: BEGINNER
Goal: Learn recursion and backtracking for Java interviews
```

Check backend log for safe diagnostic line:
```text
Falling back to placeholder course. reasonCategory=GEMINI_REQUEST_FAILURE, topic='recursion backtracking safe diagnostic test', requestedDifficulty=BEGINNER, geminiConfigured=true, exceptionType=GeminiException
```

Check DB:
```powershell
$env:Path += ";C:\Program Files\PostgreSQL\17\bin"
psql -U postgres -W -d codequest -c "select title, difficulty, source_type, total_xp, created_at from courses where normalized_topic='recursion backtracking safe diagnostic test' order by created_at desc limit 1;"
```

Observed result:
```text
source_type=PLACEHOLDER
total_xp=225
reasonCategory=GEMINI_REQUEST_FAILURE
```

Meaning:
```text
Diagnostics are working.
Gemini config is present.
Request reaches Gemini integration path.
Fallback occurs because the Gemini request itself fails.
No raw Gemini output or secrets were logged.
HTTP status diagnostics later identified the specific safe status as 429 / 4xx.
```

Important boundaries:
- No raw Gemini response should be logged.
- No API key should be pasted, logged, committed, or stored in Build Log.
- No full prompt should be logged.
- No DB migration should be added for this diagnostic step unless explicitly scoped.
- No frontend changes should be made unless explicitly scoped.
- Placeholder fallback must remain.

## Gemini HTTP Request/Status Diagnostics Manual Test Commands
Use this after the Gemini HTTP status diagnostics task.

### Automated verification
```powershell
cd backend
.\mvnw.cmd test
cd ..
```

Expected:
```text
Tests run: 88
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

### Real Gemini HTTP status diagnostic check
Start backend with DB/JWT/Gemini env vars using a rotated key only:
```powershell
$env:DATABASE_URL="jdbc:postgresql://localhost:5432/codequest"
$env:DATABASE_USERNAME="postgres"
$env:DATABASE_PASSWORD="<your-local-postgres-password>"
$env:JWT_SECRET="dev-only-change-this-secret-dev-only-change-this-secret"

$env:GEMINI_API_KEY="<your-new-rotated-gemini-key>"
$env:GEMINI_MODEL="gemini-2.0-flash"
$env:GEMINI_BASE_URL="https://generativelanguage.googleapis.com"

cd backend
.\mvnw.cmd spring-boot:run
```

Generate a new uncached topic from DashboardShell:
```text
Topic: HashMap Gemini Status Diagnostic Test
Difficulty: BEGINNER
Goal: Learn HashMap for Java DSA interviews
```

Check backend log for safe diagnostic line:
```text
Falling back to placeholder course. reasonCategory=GEMINI_REQUEST_FAILURE, topic='hashmap gemini status diagnostic test', requestedDifficulty=BEGINNER, geminiConfigured=true, exceptionType=GeminiException, httpStatusCode=429, httpStatusFamily=4xx
```

Check DB:
```powershell
$env:Path += ";C:\Program Files\PostgreSQL\17\bin"
psql -U postgres -W -d codequest -c "select title, difficulty, source_type, total_xp, created_at from courses where normalized_topic='hashmap gemini status diagnostic test' order by created_at desc limit 1;"
```

Observed result:
```text
source_type=PLACEHOLDER
total_xp=225
reasonCategory=GEMINI_REQUEST_FAILURE
httpStatusCode=429
httpStatusFamily=4xx
```

Meaning:
```text
Diagnostics are working.
Gemini config is present.
Request reaches Gemini integration path.
Fallback occurs because Gemini returns/reports HTTP 429.
No raw Gemini output, full prompt, full URL with key, or secrets were logged.
Next task should not keep changing parser/frontend/DB/controller.
Next task should handle 429 gracefully and/or retry real AI verification only after quota/key/model availability is resolved.
```

Important boundaries:
- No raw Gemini response should be logged.
- No API key should be pasted, logged, committed, or stored in Build Log.
- No full prompt should be logged.
- No full URL containing query key should be logged.
- No DB migration should be added for this diagnostic step unless explicitly scoped.
- No frontend changes should be made unless explicitly scoped.
- Placeholder fallback must remain.

## Gemini Retry-once Manual Verification Commands
Use this after the Gemini retry-once reliability task.

### Automated verification
```powershell
cd backend
.\mvnw.cmd test
cd ..
```

Expected:
```text
Tests run: 93
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

### Backend env/run
Use local DB/JWT/Gemini env vars with a rotated working Gemini key only. Do not paste real secrets.

```powershell
cd C:\Users\hp\Desktop\CodeQuestFinalProject

$env:DATABASE_URL="jdbc:postgresql://localhost:5432/codequest"
$env:DATABASE_USERNAME="postgres"
$env:DATABASE_PASSWORD="<your-local-postgres-password>"
$env:JWT_SECRET="dev-only-change-this-secret-dev-only-change-this-secret"

$env:GEMINI_API_KEY="<working-rotated-gemini-key>"
$env:GEMINI_MODEL="gemini-2.5-flash"
$env:GEMINI_BASE_URL="https://generativelanguage.googleapis.com"

cd backend
.\mvnw.cmd spring-boot:run
```

### Browser test
```text
Topic: Graph DFS Gemini Retry Test
Difficulty: BEGINNER
Goal: Learn DFS for Java DSA interviews
```

### DB course check
```powershell
$env:Path += ";C:\Program Files\PostgreSQL\17\bin"

psql -U postgres -W -d codequest -c "select title, difficulty, source_type, total_xp, created_at from courses where normalized_topic='graph dfs gemini retry test' order by created_at desc limit 1;"
```

Observed:
```text
title=Mastering Graph DFS for Java Interviews (Beginner)
difficulty=BEGINNER
source_type=AI
total_xp=375
```

### DB levels check
```powershell
psql -U postgres -W -d codequest -c "select count(*) as level_count, sum(xp_reward) as total_level_xp from levels where course_id = (select id from courses where normalized_topic='graph dfs gemini retry test' order by created_at desc limit 1);"
```

Observed:
```text
level_count=4
total_level_xp=375
```

Meaning:
```text
Real Gemini AI course generation is now confirmed end-to-end.
Direct Gemini access works.
Backend Gemini call works.
ResponseParser validation passed.
CourseService persisted source_type=AI.
Retry-once reliability behavior is implemented and tested.
Placeholder fallback remains available for failures.
```

Important boundaries:
- No raw Gemini response should be logged.
- No API key should be pasted, logged, committed, or stored in Build Log.
- No full prompt should be logged.
- No full URL containing query key should be logged.
- No DB migration was added for this retry task.
- No frontend changes were made for this retry task.
- `CourseController` was not changed for this retry task.
- Placeholder fallback must remain.
- Frontend source badge display was fixed later in commit `08fe631 fix: show course source badge`.

## Backend Course Fetch Endpoint Manual Test Commands
Use these after the backend course fetch endpoint task.

### Automated verification
```powershell
cd backend
.\mvnw.cmd test
cd ..
```

Expected:
```text
Tests run: 98
Failures: 0
Errors: 0
BUILD SUCCESS
```

### Scope checks
```powershell
git diff -- backend/src/main/resources/db/migration
git diff -- backend/src/main/java/com/codequest/ai
git diff -- frontend
```

Expected:
```text
No output for all unrelated diff checks.
```

### Backend env/run
Start backend with local PostgreSQL/JWT env vars:
```powershell
cd C:\Users\hp\Desktop\CodeQuestFinalProject

$env:DATABASE_URL="jdbc:postgresql://localhost:5432/codequest"
$env:DATABASE_USERNAME="postgres"
$env:DATABASE_PASSWORD="<your-local-postgres-password>"
$env:JWT_SECRET="dev-only-change-this-secret-dev-only-change-this-secret"

cd backend
.\mvnw.cmd spring-boot:run
```

### Manual 200 success check
From another PowerShell:
```powershell
$baseUrl = "http://localhost:8080"
$email = "coursefetch$(Get-Random)@example.com"
$password = "CourseFetch123"

$registerBody = @{
  name = "Course Fetch Manual"
  email = $email
  password = $password
} | ConvertTo-Json

Invoke-RestMethod `
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
  topic = "Course Fetch Manual Test"
  difficulty = "BEGINNER"
  goal = "Verify course fetch endpoint"
} | ConvertTo-Json

$generatedCourse = Invoke-RestMethod `
  -Uri "$baseUrl/api/courses/generate" `
  -Method POST `
  -ContentType "application/json" `
  -Headers @{ Authorization = "Bearer $token" } `
  -Body $courseBody

$courseId = $generatedCourse.courseId

$fetchedCourse = Invoke-RestMethod `
  -Uri "$baseUrl/api/courses/$courseId" `
  -Method GET `
  -Headers @{ Authorization = "Bearer $token" }

$fetchedCourse
$fetchedCourse.levels
```

Expected 200 response fields:
```text
courseId
title
description
difficulty
sourceType
totalXp
levels
```

Expected level fields:
```text
levelId
orderNumber
title
contentMarkdown
xpReward
isBoss
```

Observed latest manual result:
```text
GET /api/courses/{courseId} returned 200 OK.
sourceType=AI
totalXp=375
levels=4
orderNumber values were 1, 2, 3, 4.
```

### Manual 404 check
```powershell
$missingCourseId = [guid]::NewGuid().ToString()

try {
  Invoke-RestMethod `
    -Uri "$baseUrl/api/courses/$missingCourseId" `
    -Method GET `
    -Headers @{ Authorization = "Bearer $token" }
} catch {
  $_.Exception.Response.StatusCode.value__
}
```

Expected:
```text
404
```

### Manual 401 check
```powershell
try {
  Invoke-RestMethod `
    -Uri "$baseUrl/api/courses/$courseId" `
    -Method GET
} catch {
  $_.Exception.Response.StatusCode.value__
}
```

Expected:
```text
401
```

Important boundaries:
- Endpoint is GET `/api/courses/{courseId}`.
- Endpoint requires JWT Bearer token.
- Endpoint returns safe course details plus ordered levels.
- Endpoint does not accept userId from request path, params, or body.
- Endpoint does not call Gemini.
- Endpoint does not generate courses.
- Endpoint does not change POST `/api/courses/generate` behavior.
- No DB migration changes were made.
- No AI files were changed.
- No frontend files were changed.
- Course map UI is not implemented yet.

## Frontend Course Source Badge Fix Manual Test Commands
Use these after the source badge fix task.

### Automated verification
Frontend build:
```powershell
cd frontend
npm run build
cd ..
```

Expected:
```text
Build succeeds.
```

Backend tests because the response DTO/mapping was changed:
```powershell
cd backend
.\mvnw.cmd test
cd ..
```

Expected:
```text
Tests run: 93
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
Frontend diff contains DashboardShell badge fix only.
DB migration diff is empty.
CourseController diff is empty.
```

### Manual browser verification
Start backend with DB/JWT/Gemini env vars:
```powershell
$env:DATABASE_URL="jdbc:postgresql://localhost:5432/codequest"
$env:DATABASE_USERNAME="postgres"
$env:DATABASE_PASSWORD="<your-local-postgres-password>"
$env:JWT_SECRET="dev-only-change-this-secret-dev-only-change-this-secret"

$env:GEMINI_API_KEY="<working-rotated-gemini-key>"
$env:GEMINI_MODEL="gemini-2.5-flash"
$env:GEMINI_BASE_URL="https://generativelanguage.googleapis.com"

cd backend
.\mvnw.cmd spring-boot:run
```

Start frontend:
```powershell
cd frontend
npm run dev
```

Browser flow:
```text
1. Open Vite URL such as http://localhost:5173.
2. Log in.
3. Open Dashboard Shell.
4. Generate a new course topic likely to use AI.
5. Confirm API response includes sourceType.
6. Confirm UI badge behavior.
```

Observed manual result:
```text
Topic generated: Linked List / Linked Lists for Java Interviews
Difficulty: INTERMEDIATE
UI displayed: AI Generated Course
Levels displayed: 4
Badge bug fixed.
```

Expected badge behavior:
```text
cacheHit=true -> Cache Hit
sourceType=AI -> AI Generated Course
sourceType=PLACEHOLDER and cacheHit=false -> New Placeholder Course
unknown or missing sourceType -> New Course
```

Important boundaries:
- No DB migration changes.
- CourseController unchanged.
- AI retry logic unchanged.
- Auth/user logic unchanged.
- No course map UI implemented.
- No lesson/quiz/flashcard/progress/Piston/leaderboard/deployment work implemented.

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
Correct badge appears: Cache Hit, AI Generated Course, New Placeholder Course, or New Course.
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
Generated course result appears with title, description, correct source/cache badge, course id, and level cards.
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


## Backend Quiz Persistence/Fetch Foundation Manual Test Commands
Use these after the backend quiz persistence/fetch foundation task.

### Automated verification
```powershell
cd backend
.\mvnw.cmd test
cd ..
```

Expected after this feature:
```text
Tests run: 100
Failures: 0
Errors: 0
BUILD SUCCESS
```

### Scope checks
```powershell
git diff -- frontend
git diff -- backend/src/main/java/com/codequest/ai/PromptBuilder.java
git diff -- backend/src/main/java/com/codequest/ai/GeminiHttpClient.java
git diff -- backend/src/main/java/com/codequest/ai/GeminiService.java
git diff -- backend/src/main/java/com/codequest/ai/ResponseParser.java
git diff -- backend/src/main/resources/db/migration
```

Expected:
```text
Frontend diff is empty.
AI PromptBuilder/GeminiHttpClient/GeminiService/ResponseParser diffs are empty.
DB migration diff contains only the new V4 quizzes migration before commit.
Backend course/quiz/test changes are expected.
```

### Backend env/run
Start backend with local PostgreSQL/JWT env vars:
```powershell
cd C:\Users\hp\Desktop\CodeQuestFinalProject

$env:DATABASE_URL="jdbc:postgresql://localhost:5432/codequest"
$env:DATABASE_USERNAME="postgres"
$env:DATABASE_PASSWORD="<your-local-postgres-password>"
$env:JWT_SECRET="dev-only-change-this-secret-dev-only-change-this-secret"

cd backend
.\mvnw.cmd spring-boot:run
```

Expected:
```text
Flyway validates/applies V4__create_quizzes_table.sql successfully.
Tomcat started on port 8080.
Started CodeQuestApplication.
```

### Manual placeholder/safe fetch check
From another PowerShell:
```powershell
$baseUrl = "http://localhost:8080"
$email = "quizmanual$(Get-Random)@example.com"
$password = "QuizManual123"

$registerBody = @{
  name = "Quiz Manual"
  email = $email
  password = $password
} | ConvertTo-Json

Invoke-RestMethod `
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
  topic = "Quiz Persistence Manual Test"
  difficulty = "BEGINNER"
  goal = "Verify safe quiz fetch"
} | ConvertTo-Json

$generatedCourse = Invoke-RestMethod `
  -Uri "$baseUrl/api/courses/generate" `
  -Method POST `
  -ContentType "application/json" `
  -Headers @{ Authorization = "Bearer $token" } `
  -Body $courseBody

$courseId = $generatedCourse.courseId

$fetchedCourse = Invoke-RestMethod `
  -Uri "$baseUrl/api/courses/$courseId" `
  -Method GET `
  -Headers @{ Authorization = "Bearer $token" }

$fetchedCourse
$fetchedCourse.levels | Format-List
$fetchedCourse.levels | Select-Object orderNumber,title,quizQuestions
```

Expected placeholder response:
```text
sourceType=PLACEHOLDER
totalXp=225
3 ordered levels
each level includes quizQuestions
placeholder levels have quizQuestions: []
```

### Correct answer exposure check
```powershell
$json = $fetchedCourse | ConvertTo-Json -Depth 20
$json
$json.Contains("correctAnswer")
```

Expected:
```text
False
```

### Manual 404 check
```powershell
$missingCourseId = [guid]::NewGuid().ToString()

try {
  Invoke-RestMethod `
    -Uri "$baseUrl/api/courses/$missingCourseId" `
    -Method GET `
    -Headers @{ Authorization = "Bearer $token" }
} catch {
  $_.Exception.Response.StatusCode.value__
}
```

Expected:
```text
404
```

### Manual 401 check
```powershell
try {
  Invoke-RestMethod `
    -Uri "$baseUrl/api/courses/$courseId" `
    -Method GET
} catch {
  $_.Exception.Response.StatusCode.value__
}
```

Expected:
```text
401
```

Observed latest manual result:
```text
Register/login passed.
POST /api/courses/generate returned a PLACEHOLDER course for Quiz Persistence Manual Test.
GET /api/courses/{courseId} returned course fields and ordered levels.
Each level included quizQuestions: [].
$json.Contains("correctAnswer") returned False.
Random valid UUID returned 404.
No-token request returned 401.
```

Important boundaries:
- V4 adds `quizzes` table only.
- Existing migrations V1/V2/V3 must not be edited.
- GET `/api/courses/{courseId}` returns safe `quizQuestions` but not `correctAnswer`.
- Placeholder courses create no quiz rows.
- Placeholder courses create no flashcard rows.
- AI-success course generation can persist validated quiz rows linked to levels.
- AI-success course generation can persist validated flashcards linked to levels.
- Cache hits must not call Gemini and must not duplicate quiz rows.
- Frontend was not changed in this feature.
- AI PromptBuilder, GeminiHttpClient, GeminiService, and ResponseParser were not changed in this feature.
- Quiz submit, scoring, answer persistence, XP/progress, weak concept detection, and level unlock logic are still not implemented.

## Backend Flashcards Persistence/Fetch Foundation Manual Test Commands
Use these after the backend flashcards persistence/fetch foundation task.

### Automated verification
```powershell
cd backend
.\mvnw.cmd test
cd ..
```

Expected after this feature:
```text
Tests run: 101
Failures: 0
Errors: 0
BUILD SUCCESS
```

### Scope checks
```powershell
git diff -- frontend
git diff -- backend/src/main/java/com/codequest/ai/PromptBuilder.java
git diff -- backend/src/main/java/com/codequest/ai/GeminiHttpClient.java
git diff -- backend/src/main/java/com/codequest/ai/GeminiService.java
git diff -- backend/src/main/java/com/codequest/ai/ResponseParser.java
git diff -- backend/src/main/resources/db/migration
```

Expected:
```text
Frontend diff is empty.
AI PromptBuilder/GeminiHttpClient/GeminiService/ResponseParser diffs are empty.
DB migration diff contains only the new V5 flashcards migration before commit.
Backend course/flashcard/test changes are expected.
Existing quizQuestions behavior remains stable.
```

### Backend env/run
Start backend with local PostgreSQL/JWT env vars:
```powershell
cd C:\Users\hp\Desktop\CodeQuestFinalProject

$env:DATABASE_URL="jdbc:postgresql://localhost:5432/codequest"
$env:DATABASE_USERNAME="postgres"
$env:DATABASE_PASSWORD="<your-local-postgres-password>"
$env:JWT_SECRET="dev-only-change-this-secret-dev-only-change-this-secret"

cd backend
.\mvnw.cmd spring-boot:run
```

Expected:
```text
Flyway validates/applies V5__create_flashcards_table.sql successfully.
Tomcat started on port 8080.
Started CodeQuestApplication.
```

Observed latest runtime result:
```text
Successfully validated 5 migrations.
Current version of schema "public": 5.
Schema "public" is up to date. No migration necessary.
Tomcat started on port 8080.
Started CodeQuestApplication.
```

### Manual placeholder/safe fetch check
From another PowerShell:
```powershell
$baseUrl = "http://localhost:8080"
$email = "flashcardmanual$(Get-Random)@example.com"
$password = "FlashcardManual123"

$registerBody = @{
  name = "Flashcard Manual"
  email = $email
  password = $password
} | ConvertTo-Json

Invoke-RestMethod `
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
  topic = "Flashcards Persistence Manual Test"
  difficulty = "BEGINNER"
  goal = "Verify safe flashcards fetch"
} | ConvertTo-Json

$generatedCourse = Invoke-RestMethod `
  -Uri "$baseUrl/api/courses/generate" `
  -Method POST `
  -ContentType "application/json" `
  -Headers @{ Authorization = "Bearer $token" } `
  -Body $courseBody

$courseId = $generatedCourse.courseId

$fetchedCourse = Invoke-RestMethod `
  -Uri "$baseUrl/api/courses/$courseId" `
  -Method GET `
  -Headers @{ Authorization = "Bearer $token" }

$fetchedCourse
$fetchedCourse.levels | Format-List
$fetchedCourse.levels | Select-Object orderNumber,title,quizQuestions,flashcards
```

Expected placeholder response:
```text
sourceType=PLACEHOLDER
totalXp=225
3 ordered levels
each level includes quizQuestions
each level includes flashcards
placeholder levels have quizQuestions: []
placeholder levels have flashcards: []
```

### Flashcards/quizQuestions presence check
```powershell
$json = $fetchedCourse | ConvertTo-Json -Depth 20
$json
$json.Contains("flashcards")
$json.Contains("quizQuestions")
```

Expected:
```text
True
True
```

### Manual 404 check
```powershell
$missingCourseId = [guid]::NewGuid().ToString()

try {
  Invoke-RestMethod `
    -Uri "$baseUrl/api/courses/$missingCourseId" `
    -Method GET `
    -Headers @{ Authorization = "Bearer $token" }
} catch {
  $_.Exception.Response.StatusCode.value__
}
```

Expected:
```text
404
```

### Manual 401 check
```powershell
try {
  Invoke-RestMethod `
    -Uri "$baseUrl/api/courses/$courseId" `
    -Method GET
} catch {
  $_.Exception.Response.StatusCode.value__
}
```

Expected:
```text
401
```

Observed latest manual result:
```text
Backend started successfully.
Flyway validated 5 migrations and schema version 5.
Register/login passed.
POST /api/courses/generate returned a PLACEHOLDER course for Flashcards Persistence Manual Test.
GET /api/courses/{courseId} returned course fields and ordered levels.
Each level included quizQuestions: [].
Each level included flashcards: [].
$json.Contains("flashcards") returned True.
$json.Contains("quizQuestions") returned True.
Random valid UUID returned 404.
No-token request returned 401.
```

Important boundaries:
- V5 adds `flashcards` table only.
- Existing migrations V1/V2/V3/V4 must not be edited.
- GET `/api/courses/{courseId}` returns safe `flashcards`.
- Placeholder courses create no flashcard rows.
- AI-success course generation can persist validated flashcards linked to levels.
- Current AI flashcard DTO exposes `front` and `back`; `conceptTag` is stored as `null` for AI-created rows unless the DTO/parser is expanded later.
- Cache hits must not call Gemini and must not duplicate flashcard rows.
- Existing quizQuestions behavior remains stable.
- Frontend was not changed in this feature.
- AI PromptBuilder, GeminiHttpClient, GeminiService, and ResponseParser were not changed in this feature.
- Notes saving, quiz submit, scoring, answer persistence, XP/progress, weak concept detection, and level unlock logic are still not implemented.

## Frontend Real Quiz/Flashcards Display Compatibility Manual Test Commands
Use these after the frontend compatibility fix `eb46a9e fix: support backend quiz options shape`.

### Automated verification
```powershell
cd frontend
npm run build
cd ..
```

Expected after this feature:
```text
Vite build succeeds.
```

### Scope checks
```powershell
git diff -- backend/src/main/resources/db/migration
git diff -- backend/src/main/java/com/codequest/ai
git diff -- backend/src/main/java/com/codequest/course
git diff -- backend/src/main/java/com/codequest/quiz
git diff -- backend/src/main/java/com/codequest/flashcard
git diff -- frontend
```

Expected:
```text
Backend migration diff is empty.
AI diff is empty.
Backend course diff is empty.
Backend quiz diff is empty.
Backend flashcard diff is empty.
Frontend diff contains only the DashboardShell quiz option normalization fix before commit.
```

### Placeholder/empty-state browser check
Start backend with local PostgreSQL/JWT env vars:
```powershell
cd C:\Users\hp\Desktop\CodeQuestFinalProject

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

Browser flow:
```text
1. Login.
2. Generate or reuse a course.
3. Click Open Course Map.
4. Click Open Lesson.
5. Confirm Lesson content appears.
6. Confirm Quiz section appears.
7. For placeholder/cached courses with no quiz rows, confirm empty state: Quiz questions are not available for this level yet.
8. Confirm Flashcards section appears.
9. For placeholder/cached courses with no flashcard rows, confirm empty state: Flashcards are not available for this level yet.
10. Confirm Back to Course Map and Back to Home still work.
11. Confirm no browser console runtime error and no visible secrets/tokens.
```

### Real AI quiz/flashcards browser check
Use only with a valid local Gemini key. Do not paste real secrets into chat, screenshots, logs, or Build Log.

Start backend with DB/JWT/Gemini env vars:
```powershell
cd C:\Users\hp\Desktop\CodeQuestFinalProject

$env:DATABASE_URL="jdbc:postgresql://localhost:5432/codequest"
$env:DATABASE_USERNAME="postgres"
$env:DATABASE_PASSWORD="<your-local-postgres-password>"
$env:JWT_SECRET="dev-only-change-this-secret-dev-only-change-this-secret"

$env:GEMINI_API_KEY="<working-rotated-gemini-key>"
$env:GEMINI_MODEL="gemini-2.5-flash"
$env:GEMINI_BASE_URL="https://generativelanguage.googleapis.com"

cd backend
.\mvnw.cmd spring-boot:run
```

Generate a new uncached BEGINNER topic from DashboardShell:
```text
Topic: Trie Data Structure Quiz Flashcards AI Test
Difficulty: BEGINNER
Goal: Learn trie basics for Java DSA interviews with quiz and flashcards
```

Observed latest manual result:
```text
AI course generated successfully.
Lesson page displayed AI lesson content.
Quiz section displayed real backend quizQuestions.
Options A/B/C/D rendered correctly from backend quizQuestions[].options object shape.
Concept/explanation rendered.
Flashcards section displayed real backend flashcards.
Show Answer / Hide Answer worked.
correctAnswer was not visible.
No secrets/tokens visible.
```

Advanced/fallback observation:
```text
Manual ADVANCED Greedy Algorithm generation fell back to PLACEHOLDER with reasonCategory=PARSER_VALIDATION_FAILURE.
This is not an API key or frontend bug. Gemini was configured and called, but the AI output failed strict parser validation.
Use safe parser diagnostics/prompt compatibility later only if this becomes a frequent blocker.
```

Important boundaries:
- No backend change was made for this compatibility fix.
- No DB migration was added.
- AI PromptBuilder, GeminiHttpClient, GeminiService, and ResponseParser were not changed.
- Quiz submit, scoring, answer persistence, XP/progress, weak concept detection, notes saving, level unlock logic, Piston/code execution, Docker, CI/CD, deployment, and Phase 2 features are still not implemented.

## Backend Notes Foundation Manual Test Commands
Use these after the backend notes foundation task.

### Automated verification
```powershell
cd backend
.\mvnw.cmd test
cd ..
```

Expected after this feature:
```text
Tests run: 113
Failures: 0
Errors: 0
BUILD SUCCESS
```

### Scope checks
```powershell
git diff -- frontend
git diff -- backend/src/main/java/com/codequest/ai
git diff -- backend/src/main/java/com/codequest/quiz
git diff -- backend/src/main/java/com/codequest/flashcard
git diff -- backend/src/main/java/com/codequest/course
git diff -- backend/src/main/resources/db/migration
```

Expected:
```text
Frontend diff is empty.
AI diff is empty.
Backend quiz diff is empty.
Backend flashcard diff is empty.
Backend course diff is empty.
DB migration diff contains only the new V6 notes migration before commit.
Backend note package and note tests are expected.
```

### Backend env/run
Start backend with local PostgreSQL/JWT env vars. Gemini key is not required for notes testing.
```powershell
cd C:\Users\hp\Desktop\CodeQuestFinalProject

$env:DATABASE_URL="jdbc:postgresql://localhost:5432/codequest"
$env:DATABASE_USERNAME="postgres"
$env:DATABASE_PASSWORD="<your-local-postgres-password>"
$env:JWT_SECRET="dev-only-change-this-secret-dev-only-change-this-secret"

Remove-Item Env:GEMINI_API_KEY -ErrorAction SilentlyContinue
Remove-Item Env:GEMINI_MODEL -ErrorAction SilentlyContinue
Remove-Item Env:GEMINI_BASE_URL -ErrorAction SilentlyContinue

cd backend
.\mvnw.cmd spring-boot:run
```

Expected:
```text
Flyway validates/applies V6__create_notes_table.sql successfully.
Tomcat started on port 8080.
Started CodeQuestApplication.
```

### Manual notes create/update check
From another PowerShell:
```powershell
$baseUrl = "http://localhost:8080"

$email1 = "notesmanual1$(Get-Random)@example.com"
$password = "NotesManual123"

$registerBody1 = @{
  name = "Notes Manual One"
  email = $email1
  password = $password
} | ConvertTo-Json

Invoke-RestMethod `
  -Uri "$baseUrl/api/auth/register" `
  -Method POST `
  -ContentType "application/json" `
  -Body $registerBody1

$loginBody1 = @{
  email = $email1
  password = $password
} | ConvertTo-Json

$loginResponse1 = Invoke-RestMethod `
  -Uri "$baseUrl/api/auth/login" `
  -Method POST `
  -ContentType "application/json" `
  -Body $loginBody1

$token1 = $loginResponse1.accessToken

$courseBody = @{
  topic = "Notes Foundation Manual Test"
  difficulty = "BEGINNER"
  goal = "Verify notes save update"
} | ConvertTo-Json

$generatedCourse = Invoke-RestMethod `
  -Uri "$baseUrl/api/courses/generate" `
  -Method POST `
  -ContentType "application/json" `
  -Headers @{ Authorization = "Bearer $token1" } `
  -Body $courseBody

$courseId = $generatedCourse.courseId

$fetchedCourse = Invoke-RestMethod `
  -Uri "$baseUrl/api/courses/$courseId" `
  -Method GET `
  -Headers @{ Authorization = "Bearer $token1" }

$levelId = $fetchedCourse.levels[0].levelId

$noteBody1 = @{
  levelId = $levelId
  content = "My first note for this level"
} | ConvertTo-Json

$noteCreate = Invoke-RestMethod `
  -Uri "$baseUrl/api/notes" `
  -Method POST `
  -ContentType "application/json" `
  -Headers @{ Authorization = "Bearer $token1" } `
  -Body $noteBody1

$noteCreate

$noteBodyUpdate = @{
  levelId = $levelId
  content = "My updated note for this level"
} | ConvertTo-Json

$noteUpdate = Invoke-RestMethod `
  -Uri "$baseUrl/api/notes" `
  -Method POST `
  -ContentType "application/json" `
  -Headers @{ Authorization = "Bearer $token1" } `
  -Body $noteBodyUpdate

$noteUpdate
$noteCreate.noteId -eq $noteUpdate.noteId
```

Expected:
```text
First request creates a note and returns noteId, levelId, content, createdAt, updatedAt.
Second request updates the same note for the same user/level.
Last equality check returns True.
```

### Manual second user separate note check
```powershell
$email2 = "notesmanual2$(Get-Random)@example.com"

$registerBody2 = @{
  name = "Notes Manual Two"
  email = $email2
  password = $password
} | ConvertTo-Json

Invoke-RestMethod `
  -Uri "$baseUrl/api/auth/register" `
  -Method POST `
  -ContentType "application/json" `
  -Body $registerBody2

$loginBody2 = @{
  email = $email2
  password = $password
} | ConvertTo-Json

$loginResponse2 = Invoke-RestMethod `
  -Uri "$baseUrl/api/auth/login" `
  -Method POST `
  -ContentType "application/json" `
  -Body $loginBody2

$token2 = $loginResponse2.accessToken

$noteBodySecondUser = @{
  levelId = $levelId
  content = "Second user's separate note"
} | ConvertTo-Json

$noteSecondUser = Invoke-RestMethod `
  -Uri "$baseUrl/api/notes" `
  -Method POST `
  -ContentType "application/json" `
  -Headers @{ Authorization = "Bearer $token2" } `
  -Body $noteBodySecondUser

$noteSecondUser
$noteUpdate.noteId -ne $noteSecondUser.noteId
```

Expected:
```text
Second user gets a different noteId for the same level.
Last inequality check returns True.
```

### Manual error checks
Missing level:
```powershell
$missingLevelId = [guid]::NewGuid().ToString()

$missingLevelBody = @{
  levelId = $missingLevelId
  content = "Note for missing level"
} | ConvertTo-Json

try {
  Invoke-RestMethod `
    -Uri "$baseUrl/api/notes" `
    -Method POST `
    -ContentType "application/json" `
    -Headers @{ Authorization = "Bearer $token1" } `
    -Body $missingLevelBody
} catch {
  $_.Exception.Response.StatusCode.value__
}
```

Expected:
```text
404
```

Blank content:
```powershell
$blankContentBody = @{
  levelId = $levelId
  content = "   "
} | ConvertTo-Json

try {
  Invoke-RestMethod `
    -Uri "$baseUrl/api/notes" `
    -Method POST `
    -ContentType "application/json" `
    -Headers @{ Authorization = "Bearer $token1" } `
    -Body $blankContentBody
} catch {
  $_.Exception.Response.StatusCode.value__
}
```

Expected:
```text
400
```

No token:
```powershell
$noteNoTokenBody = @{
  levelId = $levelId
  content = "No token note"
} | ConvertTo-Json

try {
  Invoke-RestMethod `
    -Uri "$baseUrl/api/notes" `
    -Method POST `
    -ContentType "application/json" `
    -Body $noteNoTokenBody
} catch {
  $_.Exception.Response.StatusCode.value__
}
```

Expected:
```text
401
```

Observed latest manual result:
```text
User 1 created a note successfully.
User 1 updated the same note successfully.
Same user + same level returned the same noteId.
User 2 saved a separate note for the same level.
User 2 got a different noteId.
Missing level returned 404.
Blank content returned 400.
No-token request returned 401.
Scope checks showed no frontend, AI, course, quiz, or flashcard diffs.
```

Important boundaries:
- V6 adds `notes` table only.
- Existing migrations V1/V2/V3/V4/V5 must not be edited.
- POST `/api/notes` is authenticated.
- POST `/api/notes` accepts only `levelId` and `content`.
- User identity comes from JWT / `CurrentUserPrincipal`, not the request body.
- One note is stored per `(user_id, level_id)`.
- Same user saving the same level updates the same note.
- Different users can have separate notes for the same level.
- Authenticated GET `/api/notes/levels/{levelId}` is implemented for fetching the current user's saved note for a level.
- GET `/api/notes/levels/{levelId}` returns only the authenticated user's note and returns 404 for missing level or missing current-user note.
- Frontend Notes editor now preloads saved notes using GET `/api/notes/levels/{levelId}` when a lesson is opened.
- Quiz submit, scoring, XP/progress, unlock logic, Piston/code execution, deployment, and Phase 2 remain unimplemented.

## Frontend Notes Editor Foundation Manual Test Commands
Use these after the frontend notes editor foundation task `b1943c4 feat: add frontend notes editor foundation`.

### Automated verification
```powershell
cd frontend
npm run build
cd ..
```

Expected:
```text
Vite build succeeds.
```

### Scope checks
```powershell
git diff -- backend/src/main/resources/db/migration
git diff -- backend/src/main/java/com/codequest/ai
git diff -- backend/src/main/java/com/codequest/course
git diff -- backend/src/main/java/com/codequest/quiz
git diff -- backend/src/main/java/com/codequest/flashcard
git diff -- backend/src/main/java/com/codequest/note
git diff -- frontend
```

Expected:
```text
Backend migration diff is empty.
AI diff is empty.
Backend course diff is empty.
Backend quiz diff is empty.
Backend flashcard diff is empty.
Backend note diff is empty.
Frontend diff contains only `frontend/src/pages/DashboardShell.jsx` and `frontend/src/services/courseApi.js` before commit.
```

### Backend env/run
Start backend with local PostgreSQL/JWT env vars. Gemini key is not required for notes testing.
```powershell
cd C:\Users\hp\Desktop\CodeQuestFinalProject

$env:DATABASE_URL="jdbc:postgresql://localhost:5432/codequest"
$env:DATABASE_USERNAME="postgres"
$env:DATABASE_PASSWORD="<your-local-postgres-password>"
$env:JWT_SECRET="dev-only-change-this-secret-dev-only-change-this-secret"

Remove-Item Env:GEMINI_API_KEY -ErrorAction SilentlyContinue
Remove-Item Env:GEMINI_MODEL -ErrorAction SilentlyContinue
Remove-Item Env:GEMINI_BASE_URL -ErrorAction SilentlyContinue

cd backend
.\mvnw.cmd spring-boot:run
```

### Frontend env/run
```powershell
cd C:\Users\hp\Desktop\CodeQuestFinalProject\frontend
npm run dev
```

### Browser verification
```text
1. Login.
2. Generate or reuse a course.
3. Click Open Course Map.
4. Click Open Lesson on one level.
5. Confirm Lesson content still appears.
6. Confirm Quiz and Flashcards sections still work as before.
7. Confirm Notes section appears.
8. Type a note such as: Remember this level’s key idea.
9. Click Save Note.
10. Confirm button loading/disabled behavior and success message.
11. Confirm Last saved and Note ID metadata appear if shown.
12. Edit the note and click Save Note again.
13. Confirm update succeeds and the same Note ID remains.
14. Clear note content or enter spaces only.
15. Click Save Note and confirm safe message: Please enter a note before saving.
16. Confirm Back to Course Map and Back to Home still work.
17. Confirm browser console has no red runtime errors.
18. Confirm no token/password/secret is visible.
```

Observed latest manual result:
```text
Notes section appeared in Lesson view.
First save returned success and displayed Note saved.
Last saved metadata and Note ID appeared.
Second save updated the same Note ID.
Blank content was blocked safely with Please enter a note before saving.
Character counter worked.
Lesson content and Quiz section remained visible.
No token/password/secret was visible.
```

Important boundaries:
- Frontend-only task.
- Uses existing POST `/api/notes` only.
- Existing saved notes now preload through GET `/api/notes/levels/{levelId}` after Frontend Note Preload Foundation.
- No backend changes.
- No DB migration.
- No AI changes.
- Notes are local editor state only and reset/preload when the selected lesson changes.
- Notes are not stored in localStorage/sessionStorage.
- Notes are plain textarea text and are not rendered as raw HTML.
- Quiz submit, scoring, XP/progress, unlock logic, Piston/code execution, deployment, and Phase 2 remain unimplemented.


## Backend GET Notes Foundation Manual Test Commands
Use these after the backend GET notes foundation task `2606bfb feat: add get note by level endpoint`.

### Automated verification
```powershell
cd backend
.\mvnw.cmd test
cd ..
```

Expected after this feature:
```text
Tests run: 121
Failures: 0
Errors: 0
BUILD SUCCESS
```

### Scope checks
```powershell
git diff -- frontend
git diff -- backend/src/main/resources/db/migration
git diff -- backend/src/main/java/com/codequest/ai
git diff -- backend/src/main/java/com/codequest/course
git diff -- backend/src/main/java/com/codequest/quiz
git diff -- backend/src/main/java/com/codequest/flashcard
git diff -- backend/src/main/java/com/codequest/note
```

Expected:
```text
Frontend diff is empty.
DB migration diff is empty.
AI diff is empty.
Backend course diff is empty.
Backend quiz diff is empty.
Backend flashcard diff is empty.
Backend note diff contains only NoteController, NoteService, NoteControllerTest, and NoteServiceTest before commit.
```

### Backend env/run
Start backend with local PostgreSQL/JWT env vars. Gemini key is not required for notes fetch testing.
```powershell
cd C:\Users\hp\Desktop\CodeQuestFinalProject

$env:DATABASE_URL="jdbc:postgresql://localhost:5432/codequest"
$env:DATABASE_USERNAME="postgres"
$env:DATABASE_PASSWORD="<your-local-postgres-password>"
$env:JWT_SECRET="dev-only-change-this-secret-dev-only-change-this-secret"

Remove-Item Env:GEMINI_API_KEY -ErrorAction SilentlyContinue
Remove-Item Env:GEMINI_MODEL -ErrorAction SilentlyContinue
Remove-Item Env:GEMINI_BASE_URL -ErrorAction SilentlyContinue

cd backend
.\mvnw.cmd spring-boot:run
```

Expected:
```text
Flyway validates 6 migrations successfully.
Tomcat started on port 8080.
Started CodeQuestApplication.
```

### Manual get-note verification
From another PowerShell:
```powershell
$baseUrl = "http://localhost:8080"

$email1 = "getnotesmanual1$(Get-Random)@example.com"
$email2 = "getnotesmanual2$(Get-Random)@example.com"
$password = "GetNotesManual123"

$registerBody1 = @{
  name = "Get Notes Manual One"
  email = $email1
  password = $password
} | ConvertTo-Json

Invoke-RestMethod `
  -Uri "$baseUrl/api/auth/register" `
  -Method POST `
  -ContentType "application/json" `
  -Body $registerBody1

$loginBody1 = @{
  email = $email1
  password = $password
} | ConvertTo-Json

$loginResponse1 = Invoke-RestMethod `
  -Uri "$baseUrl/api/auth/login" `
  -Method POST `
  -ContentType "application/json" `
  -Body $loginBody1

$token1 = $loginResponse1.accessToken

$courseBody = @{
  topic = "Get Notes Foundation Manual Test"
  difficulty = "BEGINNER"
  goal = "Verify get saved notes"
} | ConvertTo-Json

$generatedCourse = Invoke-RestMethod `
  -Uri "$baseUrl/api/courses/generate" `
  -Method POST `
  -ContentType "application/json" `
  -Headers @{ Authorization = "Bearer $token1" } `
  -Body $courseBody

$courseId = $generatedCourse.courseId

$fetchedCourse = Invoke-RestMethod `
  -Uri "$baseUrl/api/courses/$courseId" `
  -Method GET `
  -Headers @{ Authorization = "Bearer $token1" }

$levelId = $fetchedCourse.levels[0].levelId

$noteBody1 = @{
  levelId = $levelId
  content = "User 1 first saved note"
} | ConvertTo-Json

$noteCreate = Invoke-RestMethod `
  -Uri "$baseUrl/api/notes" `
  -Method POST `
  -ContentType "application/json" `
  -Headers @{ Authorization = "Bearer $token1" } `
  -Body $noteBody1

$noteFetch1 = Invoke-RestMethod `
  -Uri "$baseUrl/api/notes/levels/$levelId" `
  -Method GET `
  -Headers @{ Authorization = "Bearer $token1" }

$noteCreate
$noteFetch1
$noteCreate.noteId -eq $noteFetch1.noteId
$noteFetch1.content
```

Expected:
```text
GET /api/notes/levels/{levelId} returns the saved note for user 1.
The noteId equality check returns True.
The content is User 1 first saved note.
```

### Manual update and fetch check
```powershell
$noteBodyUpdate = @{
  levelId = $levelId
  content = "User 1 updated saved note"
} | ConvertTo-Json

$noteUpdate = Invoke-RestMethod `
  -Uri "$baseUrl/api/notes" `
  -Method POST `
  -ContentType "application/json" `
  -Headers @{ Authorization = "Bearer $token1" } `
  -Body $noteBodyUpdate

$noteFetchUpdated = Invoke-RestMethod `
  -Uri "$baseUrl/api/notes/levels/$levelId" `
  -Method GET `
  -Headers @{ Authorization = "Bearer $token1" }

$noteUpdate
$noteFetchUpdated
$noteCreate.noteId -eq $noteFetchUpdated.noteId
$noteFetchUpdated.content
```

Expected:
```text
Same noteId remains True.
GET returns User 1 updated saved note.
```

### Manual user isolation check
```powershell
$registerBody2 = @{
  name = "Get Notes Manual Two"
  email = $email2
  password = $password
} | ConvertTo-Json

Invoke-RestMethod `
  -Uri "$baseUrl/api/auth/register" `
  -Method POST `
  -ContentType "application/json" `
  -Body $registerBody2

$loginBody2 = @{
  email = $email2
  password = $password
} | ConvertTo-Json

$loginResponse2 = Invoke-RestMethod `
  -Uri "$baseUrl/api/auth/login" `
  -Method POST `
  -ContentType "application/json" `
  -Body $loginBody2

$token2 = $loginResponse2.accessToken

try {
  Invoke-RestMethod `
    -Uri "$baseUrl/api/notes/levels/$levelId" `
    -Method GET `
    -Headers @{ Authorization = "Bearer $token2" }
} catch {
  $_.Exception.Response.StatusCode.value__
}
```

Expected before user 2 creates a note:
```text
404
```

Then create and fetch user 2's separate note:
```powershell
$noteBodyUser2 = @{
  levelId = $levelId
  content = "User 2 separate saved note"
} | ConvertTo-Json

$noteUser2 = Invoke-RestMethod `
  -Uri "$baseUrl/api/notes" `
  -Method POST `
  -ContentType "application/json" `
  -Headers @{ Authorization = "Bearer $token2" } `
  -Body $noteBodyUser2

$noteFetchUser2 = Invoke-RestMethod `
  -Uri "$baseUrl/api/notes/levels/$levelId" `
  -Method GET `
  -Headers @{ Authorization = "Bearer $token2" }

$noteUser2
$noteFetchUser2
$noteFetchUpdated.noteId -ne $noteFetchUser2.noteId
$noteFetchUser2.content
```

Expected:
```text
User 2 gets a different noteId from user 1.
The inequality check returns True.
GET returns User 2 separate saved note.
```

### Manual error checks
Random valid level UUID:
```powershell
$missingLevelId = [guid]::NewGuid().ToString()

try {
  Invoke-RestMethod `
    -Uri "$baseUrl/api/notes/levels/$missingLevelId" `
    -Method GET `
    -Headers @{ Authorization = "Bearer $token1" }
} catch {
  $_.Exception.Response.StatusCode.value__
}
```

Expected:
```text
404
```

No token:
```powershell
try {
  Invoke-RestMethod `
    -Uri "$baseUrl/api/notes/levels/$levelId" `
    -Method GET
} catch {
  $_.Exception.Response.StatusCode.value__
}
```

Expected:
```text
401
```

Observed latest manual result:
```text
User 1 saved and fetched a note successfully.
User 1 updated the note and fetched the same noteId with updated content.
User 2 received 404 when fetching user 1's level note before creating their own note.
User 2 saved and fetched a separate note for the same level with a different noteId.
Random valid level UUID returned 404.
No-token request returned 401.
Scope checks showed no frontend, migration, AI, course, quiz, or flashcard diffs.
```

Important boundaries:
- Backend-only task.
- No migration was added or changed.
- Existing V6 notes table is reused.
- GET `/api/notes/levels/{levelId}` is authenticated.
- GET `/api/notes/levels/{levelId}` accepts only levelId from path.
- Current user identity comes from JWT / CurrentUserPrincipal.
- Another user's note is never returned.
- Missing level and missing current-user note both return safe 404 behavior.
- POST `/api/notes` remains the upsert/save path and was not changed.
- Frontend note preload is still not implemented yet.
- Quiz submit, scoring, XP/progress, unlock logic, Piston/code execution, deployment, and Phase 2 remain unimplemented.



## Frontend Note Preload Foundation Manual Test Commands
Use these after the frontend note preload foundation task `e7ca3b7 feat: preload lesson notes`.

### Automated verification
```powershell
cd frontend
npm run build
cd ..
```

Expected:
```text
Vite build succeeds.
```

### Scope checks
```powershell
git diff -- backend/src/main/resources/db/migration
git diff -- backend/src/main/java/com/codequest/ai
git diff -- backend/src/main/java/com/codequest/course
git diff -- backend/src/main/java/com/codequest/quiz
git diff -- backend/src/main/java/com/codequest/flashcard
git diff -- backend/src/main/java/com/codequest/note
git diff -- frontend
```

Expected:
```text
Backend migration diff is empty.
AI diff is empty.
Backend course diff is empty.
Backend quiz diff is empty.
Backend flashcard diff is empty.
Backend note diff is empty.
Frontend diff contains only `frontend/src/pages/DashboardShell.jsx` and `frontend/src/services/courseApi.js` before commit.
```

### Backend env/run
Start backend with local PostgreSQL/JWT env vars. Gemini key is not required for notes preload testing.
```powershell
cd C:\Users\hp\Desktop\CodeQuestFinalProject

$env:DATABASE_URL="jdbc:postgresql://localhost:5432/codequest"
$env:DATABASE_USERNAME="postgres"
$env:DATABASE_PASSWORD="<your-local-postgres-password>"
$env:JWT_SECRET="dev-only-change-this-secret-dev-only-change-this-secret"

Remove-Item Env:GEMINI_API_KEY -ErrorAction SilentlyContinue
Remove-Item Env:GEMINI_MODEL -ErrorAction SilentlyContinue
Remove-Item Env:GEMINI_BASE_URL -ErrorAction SilentlyContinue

cd backend
.\mvnw.cmd spring-boot:run
```

### Frontend env/run
```powershell
cd C:\Users\hp\Desktop\CodeQuestFinalProject\frontend
npm run dev
```

### Browser verification
```text
1. Login.
2. Generate or reuse a course.
3. Click Open Course Map.
4. Click Open Lesson on one level.
5. Confirm Notes section appears.
6. If no saved note exists yet, confirm Loading saved note finishes, textarea stays empty, and no scary error appears.
7. Type a note such as: Preload test note for this level.
8. Click Save Note.
9. Confirm Note saved, Last saved, and Note ID metadata appear.
10. Click Back to Course Map.
11. Open the same lesson again.
12. Confirm the saved note is automatically preloaded into the textarea.
13. Edit the note and click Save Note again.
14. Go Back to Course Map and reopen the same lesson.
15. Confirm the updated note content is preloaded.
16. Open a different lesson with no note.
17. Confirm the previous lesson's note does not leak into this different lesson.
18. Save a separate note for the second lesson.
19. Switch between both lessons and confirm each lesson preloads its own saved note.
20. Confirm Quiz and Flashcards still render correctly.
21. Confirm Back to Course Map and Back to Home still work.
22. Confirm browser console has no red runtime errors.
23. Confirm no token/password/secret is visible.
```

Observed latest manual result:
```text
Notes section preloaded saved note content when reopening the same lesson.
Edited note content preloaded after resave.
Different lessons kept separate note content and the first lesson's note did not leak into another lesson.
No-note state was handled quietly.
Quiz and Flashcards still rendered correctly.
Back to Course Map and Back to Home worked.
Browser console had no red runtime errors.
No token/password/secret was visible.
```

Important boundaries:
- Frontend-only task.
- Uses existing GET `/api/notes/levels/{levelId}` only for explicit lesson-open preload.
- Uses existing POST `/api/notes` unchanged for save/update.
- 404 from GET note is treated as no saved note yet.
- Notes are not loaded on app page load.
- Notes are not stored in localStorage/sessionStorage.
- Notes are plain textarea text and are not rendered as raw HTML.
- Stale-response protection prevents quick lesson switches from leaking one lesson's note into another lesson.
- No backend changes.
- No DB migration.
- No AI changes.
- Quiz submit, scoring, XP/progress, unlock logic, Piston/code execution, deployment, and Phase 2 remain unimplemented.

## Next Chat Prompt
Paste this into a fresh ChatGPT Project chat whenever the current chat becomes slow or confusing:

```text
Read all CodeQuest project resources and the current CodeQuest_Build_Log.md before replying.

Project: CodeQuest — AI-assisted Java learning platform MVP
Repo: Aana-1025/CodeQuest
Branch: main
Latest pushed feature commit: e7ca3b7 feat: preload lesson notes
Previous pushed commit: 61af8c5 docs: record get note by level completion

Very important workflow rule:
We use Maven Wrapper, not plain Maven.
For backend tests always use:
cd backend
.\mvnw.cmd test

If stale compiled class issues happen:
cd backend
.\mvnw.cmd clean test

Never tell me to run plain mvn test.

Current completed state:
1. Project skeleton is complete.
2. Backend is Java 21 + Spring Boot.
3. Frontend is React + Vite + Tailwind.
4. Database is PostgreSQL + Flyway.
5. Security is Spring Security + JWT + BCrypt.
6. AI is Gemini API through GeminiService only.
7. Code execution must eventually use Piston API only; never run user code inside backend.
8. Deployment target later: Vercel frontend, Render backend, Neon PostgreSQL, GitHub Actions CI.

Completed backend features:
- Health endpoint /api/health
- PostgreSQL/Flyway setup
- Swagger/OpenAPI setup
- Global ErrorDTO + GlobalExceptionHandler
- Auth register
- Auth login
- JWT authentication
- Refresh token
- Logout / refresh token revoke
- User profile endpoint GET /api/user/profile
- Local backend runtime with PostgreSQL
- Local frontend-backend CORS
- Course generation foundation
- Gemini AI foundation
- ResponseParser + AI validation foundation
- Gemini course generation wiring with safe placeholder fallback
- Gemini prompt/response compatibility polish
- Safe Gemini fallback diagnostics
- Gemini HTTP request/status diagnostics
- Gemini retry-once for transient 5xx failures
- GenerateCourseResponse now exposes sourceType
- CourseService maps sourceType in course generation response
- Backend course fetch endpoint GET /api/courses/{courseId}
- Backend Quiz Persistence/Fetch Foundation
- Backend Flashcards Persistence/Fetch Foundation
- Backend Notes Foundation
- Backend GET Notes Foundation
- Frontend Note Preload Foundation

Completed frontend features:
- Login page
- Register page
- Protected Area
- DashboardShell
- DashboardShell course generation UI
- Course generation API helper
- Browser auth/profile/course-generation flow works
- AI/placeholder course source badge fix
- Frontend Course Map foundation
- Lesson Page Foundation
- Frontend Quiz Panel Foundation
- Frontend Flashcards Panel Foundation
- Frontend Real Quiz/Flashcards Display Compatibility Check/Fix
- Frontend Notes Editor Foundation
- Frontend Note Preload Foundation

Latest completed feature:
Frontend Note Preload Foundation.

What was done:
- Added `getNoteForLevel(levelId)` in `frontend/src/services/courseApi.js`.
- `getNoteForLevel` sends authenticated GET `/api/notes/levels/{levelId}` using the existing Bearer token pattern.
- `getNoteForLevel` returns parsed note data for 200.
- `getNoteForLevel` returns `null` for 404 so the UI treats it as “no saved note yet”.
- Lesson view now resets note state when the selected lesson changes.
- Lesson view fetches the saved note only when a lesson is explicitly opened/selected and `selectedLevel.levelId` exists.
- On 200, the Notes textarea is prefilled and local note metadata is updated.
- On 404, the Notes editor stays empty with a safe no-note state.
- On 401 or other errors, safe messages are shown without raw backend details.
- Save Note behavior remains unchanged and still uses POST `/api/notes`.
- Note metadata updates after save and preload.
- A simple stale-response ignore flag prevents quick lesson switching from leaking one lesson's note into another.
- No notes are stored in localStorage/sessionStorage.
- No raw HTML rendering was added.
- Backend, migrations, AI, course, quiz, flashcard, and backend note files were unchanged.
- Quiz submit, scoring, answer persistence, XP/progress, weak concept detection, and level unlock logic were not implemented.

Latest test results:
cd frontend && npm run build
PASS

Latest manual verification:
- Backend was started with PostgreSQL/JWT env vars and no Gemini key required.
- Frontend was started with Vite.
- User logged in, generated/reused a course, opened Course Map, and opened a Lesson.
- Notes section appeared.
- No-note state was handled quietly when no saved note existed.
- User saved a note for one lesson.
- Reopening the same lesson preloaded the saved note into the textarea.
- Editing and saving again updated the note.
- Reopening the same lesson preloaded the updated note content.
- Opening a different lesson did not leak the first lesson's note.
- A separate note could be saved for the second lesson.
- Switching between lessons preloaded each lesson's own note.
- Quiz and Flashcards still rendered correctly.
- Back to Course Map and Back to Home worked.
- Browser console had no red runtime errors.
- No token/password/secret was visible.
- Scope checks confirmed no backend, migration, AI, course, quiz, flashcard, or backend note diffs.

Latest git log should include:
e7ca3b7 feat: preload lesson notes
61af8c5 docs: record get note by level completion
2606bfb feat: add get note by level endpoint

Current known blockers:
None blocking.

Current important known notes:
- A Gemini API key was accidentally pasted in chat/log context earlier. Treat it as compromised and use only a rotated/new key.
- A local PostgreSQL password was also pasted earlier. Consider rotating local password later.
- Never paste keys/passwords/secrets again.
- Never commit or document real secrets.
- Tests must stay deterministic and must not call real Gemini even when Gemini env vars are present locally.

Next safest MVP task:
Backend Quiz Submit/Scoring Foundation or next safest MVP task.

Recommended next task:
Backend Quiz Submit/Scoring Foundation, because quiz questions are now persisted/fetched and displayed in the Lesson view, but quiz submit, scoring, and answer-check flow are still not implemented. Keep this backend-only at first and do not add XP/progress/unlock logic unless explicitly scoped.

Alternative next task:
Frontend quiz submit UI can come after a backend submit/scoring endpoint exists. Safe parser diagnostics/prompt compatibility for occasional `PARSER_VALIDATION_FAILURE` on harder/advanced Gemini outputs can come later if this becomes a recurring blocker. Safe parser diagnostics/prompt compatibility for occasional `PARSER_VALIDATION_FAILURE` on harder/advanced Gemini outputs can come later if it becomes a recurring blocker. Do not combine that with notes preload, quiz submit, XP/progress, unlock logic, Piston, deployment, or Phase 2.

Important next-task boundaries:
- Do not implement quiz submit, scoring, answer persistence, XP/progress, unlock logic, Piston/code execution, leaderboard, Docker, CI/CD, deployment, or Phase 2.
- Do not implement weak concept detection, XP/rank/streak progress, leaderboard, Piston, Docker, CI/CD, deployment, or Phase 2 features.
- Do not touch Gemini/AI retry logic unless explicitly scoped.
- Do not touch auth/user unless strictly necessary.
- Do not change existing Flyway migrations; add a new migration only if the selected backend persistence task requires it.
- Keep tests deterministic and do not call real Gemini.
- For frontend task run:
  cd frontend
  npm run build
- For backend task run:
  cd backend
  .\mvnw.cmd test

Give me one strict Codex prompt for only the next safest MVP task.
Do not implement XP/progress, unlock logic, Piston, leaderboard, deployment, or Phase 2. If quiz submit/scoring is selected, keep it small, backend-only, and do not add XP/progress/unlock logic.
Include exact files to inspect/touch, files not to touch, commands to run, manual/API/browser verification steps, diff checks, and Build Log update instructions.
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

Current module: Frontend / Note preload foundation
Last completed feature: Frontend Note Preload Foundation
Latest feature commit: e7ca3b7 feat: preload lesson notes
Previous commit: 61af8c5 docs: record get note by level completion
Git status: clean after frontend note preload feature commit; Build Log docs update pending until this file is committed
Tests passed:
- Frontend cd frontend && npm run build PASS

Manual verification passed:
- Backend was started with PostgreSQL/JWT env vars.
- Frontend was started with Vite.
- User logged in, generated/reused a course, opened Course Map, and opened a Lesson.
- Notes section appeared.
- No-note state was handled quietly.
- User saved a note for one lesson.
- Reopening the same lesson preloaded the saved note into the textarea.
- Editing and saving again updated the note.
- Reopening the same lesson preloaded the updated note content.
- Opening a different lesson did not leak the first lesson's note.
- A separate note could be saved for the second lesson.
- Switching between lessons preloaded each lesson's own note.
- Quiz and Flashcards still rendered correctly.
- Back to Course Map and Back to Home worked.
- Browser console had no red runtime errors.
- No token/password/secret was visible.
- Backend, DB migrations, AI, backend course, backend quiz, backend flashcard, and backend note files were unchanged.

Known bugs/blockers:
- None blocking currently.
- No blocking issue is currently known.

Next task:
Backend Quiz Submit/Scoring Foundation or next safest MVP task.

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
- DashboardShell displays correct source badge: Cache Hit, AI Generated Course, New Placeholder Course, or New Course.
- DashboardShell supports explicit-click Course Map loading from GET /api/courses/{courseId}.
- DashboardShell supports frontend-only Lesson view opened from Course Map level cards.
- Lesson view reuses already-fetched Course Map data and supports Back to Course Map without refetching.
- Lesson view includes frontend-only Quiz panel with safe empty state when quiz data is missing.
- Lesson view includes frontend-only Flashcards panel with safe empty state when flashcard data is missing.
- Lesson Quiz panel supports backend quizQuestions[].options object shape `{A, B, C, D}`.
- Lesson Flashcards panel supports backend flashcards front/back shape.
- Real AI quizQuestions and flashcards display has been manually verified in Lesson view.
- Quiz and Flashcards panel state is local UI-only and resets when selected lesson changes.
- React Router not added.
- Lesson view includes a frontend-only Notes editor that saves notes using authenticated POST `/api/notes`.
- Notes editor preloads saved notes using authenticated GET `/api/notes/levels/{levelId}` when a lesson is opened.
- Notes editor uses local state only, resets/preloads on lesson change, and does not store notes in browser storage.
- Notes editor does not store notes in localStorage/sessionStorage and does not render raw HTML.
- Logout UI not implemented.

Important completed Local runtime / CORS details:
- PostgreSQL 17 installed locally.
- Local database codequest created.
- Backend starts with DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD, JWT_SECRET.
- Flyway applied V1, V2, V3, V4, V5, and V6.
- CORS allows localhost/127.0.0.1 ports 5173 and 5174.
- Browser register/login/profile/course-generation smoke tests pass.

Important completed Course generation/details:
- V3 migration creates courses and levels tables.
- V4 migration creates quizzes table.
- V5 migration creates flashcards table.
- V6 migration creates notes table.
- CourseService implements normalized topic/difficulty cache behavior.
- CourseController exposes authenticated POST /api/courses/generate.
- Cache hit returns same courseId with cacheHit=true.
- Cache hit does not call Gemini.
- Placeholder fallback creates exactly 3 levels and totalXp 225.
- Placeholder courses create no quiz rows.
- Placeholder courses create no flashcard rows.
- AI-success course generation can persist validated quiz rows linked to levels.
- AI-success course generation can persist validated flashcards linked to levels.
- GenerateCourseResponse includes sourceType.
- Frontend DashboardShell displays returned course and levels.
- Authenticated GET /api/courses/{courseId} is implemented.
- GET /api/courses/{courseId} returns courseId, title, description, difficulty, sourceType, totalXp, and ordered levels.
- Level response returns levelId, orderNumber, title, contentMarkdown, xpReward, isBoss, quizQuestions, and flashcards.
- quizQuestions is empty when no quiz rows exist.
- flashcards is empty when no flashcard rows exist.
- correctAnswer is intentionally hidden from GET /api/courses/{courseId} responses.
- GET /api/courses/{courseId} returns 404 for missing course and 401 without token.
- GET /api/courses/{courseId} does not call Gemini or generation flow.
- Frontend Course Map uses GET /api/courses/{courseId}.
- Frontend Lesson view uses existing fetched Course Map/level data and does not require a new backend endpoint.
- Authenticated POST /api/notes is implemented.
- POST /api/notes accepts levelId and content only.
- User identity comes from JWT / CurrentUserPrincipal.
- Same user/level note saves update the existing note.
- Different users can save separate notes for the same level.
- Frontend Notes editor is implemented for save/update and saved-note preload/fetch.

Important completed AI details:
- GeminiProperties implemented.
- PromptBuilder implemented and later tightened for parser-compatible JSON.
- GeminiService implemented.
- ResponseParser implemented.
- AI response DTO records implemented.
- GeminiClient abstraction implemented.
- GeminiHttpClient implemented using Spring RestClient.
- GeminiHttpClient sanitizes fenced/prose-wrapped JSON.
- GeminiHttpClient normalizes GEMINI_BASE_URL to avoid duplicate /v1beta.
- GeminiException implemented and carries safe categories plus optional HTTP status metadata.
- GeminiService delegates to GeminiClient only when config is present.
- CourseService attempts Gemini + ResponseParser only on cache miss when config is present.
- Missing config/client failure/parser failure falls back to placeholder.
- Valid mocked AI response can persist supported course/level fields with sourceType=AI.
- Valid AI quiz questions are now persisted on AI success.
- CourseService retries exactly once for transient Gemini request failures with HTTP status family 5xx.
- CourseService does not retry 400, 401, 403, 404, 429, missing config, parser validation failure, requested difficulty mismatch, or unexpected non-transient failures.
- Safe diagnostics categories implemented: MISSING_GEMINI_CONFIG, GEMINI_REQUEST_FAILURE, EMPTY_GEMINI_RESPONSE_TEXT, RESPONSE_EXTRACTION_FAILURE, PARSER_VALIDATION_FAILURE, REQUESTED_DIFFICULTY_MISMATCH, UNEXPECTED_AI_INTEGRATION_ERROR.
- Earlier manual Gemini diagnostics showed GEMINI_REQUEST_FAILURE with HTTP 429 / 4xx for one key/model/project.
- Latest manual verification confirmed real Gemini success for graph dfs gemini retry test with source_type=AI, total_xp=375, level_count=4, and total_level_xp=375.
- Latest browser verification confirmed source badge displays AI Generated Course.
- Flashcards are now persisted on AI success.
- Frontend Lesson view now renders real persisted quizQuestions and flashcards from GET /api/courses/{courseId}.
- CodingProblems are parsed/validated but not persisted yet.

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
- Tests must stay deterministic and must not call real Gemini even when Gemini env vars are set locally.

Security notes:
- A Gemini API key was accidentally pasted in chat/log context during manual testing. Revoke/delete that key and create a new one.
- A local DB password was also pasted in chat/log context. Consider rotating it later.
- Do not commit or document real secrets.
- Do not paste secrets in future chats/screenshots.
- Do not log raw Gemini output, full prompts, full URLs with query keys, API keys, JWTs, tokens, passwords, or secrets.

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
