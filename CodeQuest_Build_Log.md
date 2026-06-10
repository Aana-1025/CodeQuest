# CodeQuest Build Log

## Purpose
This file solves the long-chat slowdown problem. Update it manually after every feature so a fresh ChatGPT/Codex chat can continue from the current state without needing the full conversation history.

## Current Status
Phase: MVP
Current module: Frontend / Code Submit
Current feature: Frontend Code Submit UI Foundation completed, frontend-build-verified, browser-manually-verified for safe Piston-unavailable path, committed, pushed, and awaiting Build Log docs commit
Latest commit: `52db876 feat: add frontend code submit ui`
Previous docs commit: `f1e75b1 docs: record frontend code runner ui`
Previous feature commit: `f7b4598 feat: add frontend code runner ui`
Current branch: main
Test status: Frontend `cd frontend && npm run build` PASS after Frontend Code Submit UI Foundation according to Codex output and user-side verification flow. Manual browser verification PASS for validation and safe unavailable path: the existing DashboardShell Code Runner section now includes a separate `Submit Code` button, submit uses the same problemId/language/code/stdin/expectedOutput fields, expectedOutput is required before submit, blank code disables run/submit, submit result/error handling remains separate from run behavior, a UUID-style problem id reaches the backend, and external Piston unavailable behavior after clicking Submit Code shows the safe user-facing message `Code runner is currently unavailable. Please try again later.` without raw stack traces, raw Piston internals, raw backend JSON dumps, tokens, passwords, roles, token hashes, userId, hidden tests, correctAnswer, or secrets. Scope checks PASS: only `frontend/src/pages/DashboardShell.jsx` and `frontend/src/services/courseApi.js` changed; backend, backend Docker files, backend/pom.xml, migrations, application.yml, docs, Build Log, README, .github, docker-compose, frontend package files, CI/CD, deployment, and Phase 2 files were not part of the feature implementation.
Git status: clean after `52db876 feat: add frontend code submit ui` was pushed to `main`; Build Log docs update in progress

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
- [x] Level unlock logic
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
- [x] Backend Quiz Submit/Scoring Foundation
- [x] Frontend Quiz Submit Integration
- [x] Backend Quiz Attempt Persistence Foundation
- [x] Backend Quiz Attempt History/Fetch Foundation
- [x] Frontend Quiz Attempt History Display Foundation
- [x] Backend XP Award Foundation for Quiz Submit
- [x] Frontend XP Refresh After Correct Quiz Submit
- [x] Backend Progress / Level Complete Foundation
- [x] Backend Progress Fetch Endpoint Foundation
- [x] Frontend Course Progress / Lock UI Foundation
- [x] Frontend Complete Level Button / Progress Refresh Foundation
- [x] Backend XPService + Rank Recalculation Foundation
- [x] Backend StreakService + Daily Login XP Guard
- [x] Flashcards
- [x] Notes
- [x] Backend Quiz submit/scoring endpoint
- [x] Frontend Quiz submit UI
- [x] Weak concept detection / quiz submit weakConcepts foundation
- [x] XP/rank system / rank recalculation foundation
- [x] Streak system / daily login XP guard foundation
- [x] Piston run code
- [x] Code submit
- [x] Code submissions history
- [x] AI code review
- [x] Leaderboard
- [x] Frontend Leaderboard UI
- [x] Frontend Code Runner UI
- [x] Docker
- [x] Frontend Code Submit UI
- [ ] Frontend Code Submission History UI
- [ ] Frontend AI Code Review UI
- [ ] Monaco Editor Integration
- [ ] Dashboard UI polish / section organization
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
- Backend Docker Setup Foundation is implemented as a backend-only Docker image build foundation.
- Backend Dockerfile uses a multi-stage Java 21 build with `eclipse-temurin:21-jdk` for the builder stage and `eclipse-temurin:21-jre` for the runtime stage.
- Backend Docker build uses the Maven Wrapper inside Linux with `./mvnw`; it must not use system Maven or plain `mvn`.
- Backend Dockerfile normalizes Windows CRLF line endings in `mvnw` using `sed -i 's/\r$//' mvnw` before `chmod +x mvnw` so Linux containers do not fail with `bash\r`.
- Backend Docker image build runs `./mvnw clean package -DskipTests`; automated correctness remains verified separately with Windows Maven Wrapper command `cd backend && .\mvnw.cmd test`.
- Backend Docker runtime image exposes port 8080 and starts with `java -jar /app/app.jar`.
- Backend Docker image must not bake secrets or local environment values into the image; runtime configuration must come from environment variables such as `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`, `JWT_SECRET`, optional Gemini variables, and optional `PISTON_BASE_URL`.
- Backend `.dockerignore` excludes build/dev/secret artifacts such as `target/`, `.git/`, IDE folders, logs, `.env`, `.env.*`, `node_modules/`, `build/`, and `dist/`, while keeping `pom.xml`, `mvnw`, `.mvn/`, and `src/` available to the Docker build.
- Backend Docker task intentionally did not add docker-compose, frontend Dockerfile, CI/CD, deployment config, README changes, production CORS changes, backend application logic changes, new APIs, DB migrations, or Phase 2 features.
- Leaderboard is implemented as authenticated read-only MVP REST endpoint `GET /api/leaderboard?page=0&size=50&period=ALL_TIME`.
- Leaderboard supports `ALL_TIME` only for MVP; weekly/monthly/period-specific leaderboard is deferred.
- Leaderboard response is safe and must not expose email, passwords, role, tokens, refresh-token hashes, lastLogin, secrets, raw entities, or stack traces.
- Leaderboard sorting/ranking uses deterministic ordinal global positions based on XP descending with stable tie-breaking.
- Leaderboard does not mutate XP, rank, streak, progress, quiz attempts, code submissions, or any user data.
- Frontend Leaderboard UI Foundation is implemented in the existing DashboardShell as a frontend-only MVP section.
- Frontend leaderboard uses authenticated `GET /api/leaderboard?page=0&size=50&period=ALL_TIME` through the existing Bearer token pattern.
- Frontend leaderboard loads only after explicit user action through `Load Leaderboard` / `Refresh Leaderboard`; it does not auto-fetch on initial dashboard load.
- Frontend leaderboard shows safe loading, empty, and error states.
- Frontend leaderboard displays current-user standing separately with position, XP, and rank.
- Frontend leaderboard table displays only safe user-facing fields: rank position, name, XP, rank, and streak.
- Frontend leaderboard intentionally does not display email, password fields, roles, access tokens, refresh tokens, token hashes, raw backend JSON, stack traces, or secrets.
- Frontend leaderboard intentionally does not add weekly/monthly filters, pagination controls, search, avatars, realtime updates, backend changes, DB migrations, package changes, deployment changes, or Phase 2 features.
- Frontend Code Runner UI Foundation is implemented in the existing DashboardShell as a frontend-only MVP section.
- Frontend code runner uses authenticated `POST /api/problems/{problemId}/run` through the existing Bearer token pattern.
- Frontend code runner is run-only and intentionally does not submit code, persist submissions, fetch submission history, call AI review, award XP, change progress, or mutate any backend state.
- Frontend code runner includes fields for problem id, language, code, optional stdin, and optional expected output.
- Frontend code runner currently uses a simple textarea, not Monaco editor.
- Frontend code runner supports starter code for Java, Python, JavaScript, and C++ without overwriting user-edited code unexpectedly.
- Frontend code runner validates blank problem id, blank code, and code length over 20000 characters before sending the request.
- Frontend code runner renders stdout, stderr, output, exitCode, runtimeMs, passed status, and safe message as plain text only.
- Frontend code runner shows safe error messages for 401, 400, 503, and generic failures.
- Frontend code runner intentionally does not display access tokens, refresh tokens, passwords, roles, token hashes, userId, raw backend JSON, raw Piston internals, hidden tests, correct answers, stack traces, or secrets.
- Frontend code runner intentionally does not store code, stdin, expected output, or run results in localStorage or sessionStorage.
- Frontend code runner intentionally does not add Monaco editor, auto-run, code submit, submission history UI, AI review UI, coding problem browsing, backend changes, DB migrations, package changes, deployment changes, or Phase 2 features.
- Frontend Code Submit UI Foundation is implemented in the existing DashboardShell Code Runner section as a frontend-only MVP flow.
- Frontend code submit uses authenticated `POST /api/problems/{problemId}/submit` through the existing Bearer token pattern.
- Frontend code submit reuses the current problem id, language, code, optional stdin, and expected output fields from the Code Runner section.
- Frontend code submit requires expected output before submission because the backend submit MVP uses visible expected-output comparison.
- Frontend code submit keeps `Run Code` and `Submit Code` behaviors/results separate so one action does not overwrite the other unexpectedly.
- Frontend code submit validates blank problem id, blank code, blank expected output, and code length over 20000 characters before sending the request.
- Frontend code submit renders stdout, stderr, output, exitCode, runtimeMs, passed status, `xpAwarded`, `firstAccepted`, and safe message as plain text only.
- Frontend code submit shows safe error messages for 401, 400, 503, and generic failures.
- Frontend code submit attempts to refresh the authenticated dashboard profile only when a submit response reports `xpAwarded > 0`.
- Frontend code submit keeps the submit result visible even if profile refresh fails and shows only a safe profile-refresh fallback message.
- Frontend code submit intentionally does not display access tokens, refresh tokens, passwords, roles, token hashes, userId, raw backend JSON, raw Piston internals, hidden tests, correct answers, stack traces, stdin/expectedOutput from backend internals, or secrets.
- Frontend code submit intentionally does not store code, stdin, expected output, submit results, or run results in localStorage or sessionStorage.
- Frontend code submit intentionally does not add Monaco editor, submission history UI, AI review UI, coding problem browsing, backend changes, DB migrations, package changes, deployment changes, or Phase 2 features.
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
- Flyway successfully applies/validates V1 through V8 migrations against local PostgreSQL.
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
- At that earlier stage, frontend quiz submit UI, answer persistence, XP/progress persistence, level unlock logic, Piston/code execution, leaderboard, Docker, CI/CD, deployment, and Phase 2 features were still unimplemented; later features implemented frontend quiz submit, quiz attempt persistence/history, quiz XP award/refresh, and backend level completion progress foundation.
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
- Backend Quiz Submit/Scoring Foundation is implemented.
- Backend quiz submit endpoint is POST `/api/quizzes/{quizQuestionId}/submit`.
- POST `/api/quizzes/{quizQuestionId}/submit` is authenticated and protected by the existing JWT security flow.
- POST `/api/quizzes/{quizQuestionId}/submit` accepts only `selectedAnswer` in the request body and uses `quizQuestionId` from the path.
- Quiz submit never accepts `userId` from request body, query, path, or headers.
- Quiz scoring compares the submitted answer against backend-stored `correctAnswer` only inside the service layer.
- Quiz submit response returns safe fields only: `quizQuestionId`, `selectedAnswer`, `isCorrect`, `explanation`, and `concept`.
- Quiz submit response must never expose `correctAnswer`, even for wrong answers.
- Invalid selectedAnswer values return standard 400 validation behavior.
- Missing quiz question IDs return standard 404 ErrorDTO behavior.
- No-token quiz submit requests return 401 through the existing security flow.
- Quiz submit currently does not persist attempts, score history, selected answers, XP, progress, rank, streak, weak concepts, or unlock state.
- Frontend Quiz Submit Integration is implemented in the Lesson Quiz panel.
- Frontend quiz submit uses `submitQuizAnswer(quizQuestionId, selectedAnswer)` from `frontend/src/services/courseApi.js`.
- Frontend quiz submit sends authenticated POST `/api/quizzes/{quizQuestionId}/submit` only after explicit `Submit Answer` click.
- Frontend quiz submit sends only `selectedAnswer` in the request body.
- Frontend quiz submit uses the backend quiz identity field `quizId` from fetched course data, with `quizQuestionId` fallback for compatibility.
- Frontend quiz submit tracks selection, loading, error, and result state per question.
- Frontend quiz submit keeps the Submit Answer button disabled until an option is selected and while that question is submitting.
- Frontend quiz submit displays only safe backend result fields: selected answer, Correct/Incorrect state, explanation, and concept.
- Frontend quiz submit clears the previous result when the selected option changes so stale scoring feedback is not misleading.
- Frontend quiz submit resets question selection/result state when the selected lesson changes so results do not leak between lessons.
- Frontend quiz submit never displays `correctAnswer` and does not read, store, or render it.
- Frontend quiz submit stores no quiz answers or results in localStorage/sessionStorage.
- Frontend quiz submit does not implement answer persistence, attempts, XP/progress/rank/streak, weak concept detection, or level unlock logic.
- Backend Quiz Attempt Persistence Foundation is implemented.
- V7 Flyway migration creates the `quiz_attempts` table.
- `quiz_attempts` stores one row per successful authenticated quiz submit with `id`, `user_id`, `quiz_id`, `selected_answer`, `is_correct`, and `attempted_at`.
- Quiz attempt persistence derives `user_id` only from the authenticated `CurrentUserPrincipal`; the submit endpoint still never accepts `userId` from the client.
- Each successful authenticated POST `/api/quizzes/{quizQuestionId}/submit` now creates a new attempt row.
- Repeated quiz submits are preserved as separate history rows and are not overwritten.
- Invalid selectedAnswer values return 400 and do not persist an attempt.
- Missing quiz question IDs return 404 and do not persist an attempt.
- No-token quiz submit requests return 401 through the existing security flow and do not persist an attempt.
- Quiz submit response shape remains backward-compatible and safe: `quizQuestionId`, `selectedAnswer`, `isCorrect`, `explanation`, and `concept`.
- Quiz submit response still never exposes `correctAnswer`, `userId`, `attemptId`, tokens, passwords, roles, token hashes, refresh tokens, or secrets.
- Backend Quiz Attempt History/Fetch Foundation is implemented.
- GET `/api/quizzes/attempts` is authenticated and uses `CurrentUserPrincipal` only.
- GET `/api/quizzes/attempts` never accepts `userId` from request body, query params, headers, or path.
- GET `/api/quizzes/attempts` returns only the authenticated current user's attempts.
- GET `/api/quizzes/attempts` returns attempts ordered newest-first by `attemptedAt` descending.
- GET `/api/quizzes/attempts` returns safe DTOs wrapped in `attempts`; it does not return raw entities.
- GET `/api/quizzes/attempts` response includes safe history/context fields such as `attemptId`, `quizQuestionId`, `selectedAnswer`, `isCorrect`, `attemptedAt`, `question`, `concept`, `explanation`, `levelId`, `levelTitle`, `courseId`, and `courseTitle`.
- GET `/api/quizzes/attempts` does not expose `correctAnswer`, `userId`, token, password, role, tokenHash, refreshToken, or secrets.
- GET `/api/quizzes/attempts` returns 200 with an empty `attempts` list when the authenticated user has no attempts.
- Frontend Quiz Attempt History Display Foundation is implemented in DashboardShell.
- Frontend quiz attempt history uses authenticated GET `/api/quizzes/attempts` through `getQuizAttemptHistory()` in `frontend/src/services/courseApi.js`.
- Frontend attempt history loads only after an explicit `Load Attempts` / `Refresh Attempts` button click; it does not auto-load on dashboard open.
- Frontend attempt history state is component-local only and is not persisted in localStorage or sessionStorage.
- Frontend attempt history shows safe fields only: selected answer, Correct/Incorrect status, attempted time, question, concept, explanation, course title, level title, and muted attempt/quiz ids.
- Frontend attempt history never displays `correctAnswer`, `userId`, accessToken, refreshToken, password, tokenHash, role, or secrets.
- Frontend Quiz Attempt History Display Foundation added no backend, DB migration, AI/Gemini, package, React Router, dependency, XP/progress, weak concept, or unlock changes.
- Backend XP Award Foundation for Quiz Submit is implemented.
- Correct authenticated quiz submits now award that quiz question's `xpReward` to the authenticated current user's `xp`.
- Incorrect quiz submits do not award XP.
- Invalid selectedAnswer values, missing quiz question IDs, and no-token submit requests do not award XP.
- Repeated correct submits intentionally award XP again for the MVP foundation; deduplication/anti-farming rules are deferred.
- Quiz submit response shape stayed backward-compatible and safe: `quizQuestionId`, `selectedAnswer`, `isCorrect`, `explanation`, and `concept`.
- Quiz submit response still does not expose `correctAnswer`, `userId`, full user objects, tokens, passwords, roles, token hashes, refresh tokens, or secrets.
- XP award derives the user only from the authenticated `CurrentUserPrincipal`; userId is never accepted from request body, query params, headers, or path.
- Rank, streak, progress percentage, weak concept detection, level unlock, course completion, leaderboard, achievements, and anti-duplicate XP rules remain out of scope.
- Frontend XP Refresh After Correct Quiz Submit is implemented.
- `App.jsx` remains the owner of authenticated profile state.
- `App.jsx` exposes a shared profile refresh callback to `DashboardShell` so quiz submits can update the in-memory profile state after backend XP award.
- After a correct quiz submit, DashboardShell refreshes GET `/api/user/profile` through existing safe auth/profile flow and updates the dashboard/profile XP display.
- After an incorrect quiz submit, the frontend does not claim XP increased.
- If profile refresh after a correct submit fails, the quiz result remains visible and the UI shows a safe fallback message instead of a raw backend error.
- Frontend XP refresh does not store profile/XP updates in localStorage or sessionStorage.
- Frontend XP refresh does not display `correctAnswer`, `userId` in quiz result, tokens, passwords, roles, token hashes, refresh tokens, or secrets.
- Frontend XP refresh added no backend, DB migration, AI/Gemini, package, React Router, rank, streak, progress, weak concept, unlock, leaderboard, or anti-farming changes.
- Backend Progress / Level Complete Foundation is implemented.
- V8 Flyway migration `V8__create_progress_table.sql` creates the `progress` table.
- `progress` stores one row per authenticated user and level using `user_id` and `level_id`.
- `progress` includes `completed`, `score`, nullable `quiz_answers_json`, `completed_at`, `created_at`, and `updated_at` for schema alignment.
- `progress` enforces `UNIQUE(user_id, level_id)` so the same user cannot create duplicate completion rows for the same level.
- `progress` includes indexes for user/level lookup according to the MVP database rules.
- Backend level completion endpoint is POST `/api/levels/{levelId}/complete`.
- POST `/api/levels/{levelId}/complete` is authenticated and uses `@AuthenticationPrincipal CurrentUserPrincipal`.
- POST `/api/levels/{levelId}/complete` accepts only `levelId` from the path and no request body.
- POST `/api/levels/{levelId}/complete` never accepts `userId` from request body, query params, headers, or path.
- First completion for an authenticated user and level creates one progress row, marks it completed, sets `completedAt`, and awards `level.xpReward` to the authenticated user's XP.
- Repeating completion for the same authenticated user and level is idempotent: no duplicate progress row is created, `alreadyCompleted=true` is returned, and `xpAwarded=0`.
- Different users can complete the same level independently and receive first-completion XP independently.
- Missing level IDs return standard 404 ErrorDTO behavior.
- No-token completion requests return 401 through the existing Spring Security flow.
- Level completion response is safe and includes only `levelId`, `completed`, `alreadyCompleted`, `xpAwarded`, `totalXp`, and `completedAt`.
- Level completion response must not expose `userId`, password fields, roles, tokens, refresh tokens, token hashes, secrets, or raw entities.
- `ProgressService.completeLevel(...)` is transactional so progress creation and user XP update are atomic.
- The progress feature intentionally does not implement rank recalculation, streak logic, weak concept detection, level unlock rules, boss prerequisites, progress percentage, course completion, leaderboard, achievements, anti-farming for quiz submits, frontend UI, Piston/code execution, deployment, or Phase 2 features.
- The initial manual PowerShell verification found a PostgreSQL runtime bug: `quiz_answers_json` was `jsonb` in V8 but the entity mapped it as a Java `String`, causing Hibernate to bind a varchar and PostgreSQL to return SQLState 42804.
- The JSONB runtime bug was fixed before commit by removing the unused `quizAnswersJson` field from the `Progress` entity while keeping the nullable `quiz_answers_json JSONB` column in V8 for future schema alignment.
- Backend Level Unlock Logic Foundation is implemented.
- `POST /api/levels/{levelId}/complete` now enforces unlock rules before creating new progress or awarding level XP.
- Level order number 1 is unlocked by default.
- Any level with order number greater than 1 is unlocked only when the authenticated user has completed all previous levels in the same course.
- Boss levels use the same all-previous-levels rule and cannot be completed until every earlier level in the same course is completed by the authenticated user.
- Already-completed levels remain idempotent and return `alreadyCompleted=true` with `xpAwarded=0` before rechecking unlock state.
- Locked level completion returns standard 403 ErrorDTO using existing `ErrorCode.FORBIDDEN` and safe message `Complete previous levels before unlocking this level.`
- Locked level requests do not create progress rows and do not change user XP.
- Unlock state is user-scoped through authenticated `CurrentUserPrincipal` and existing progress rows; different users have independent unlock state.
- No new Flyway migration was needed for level unlock logic because unlock state is derived from existing `levels` and `progress` data.
- Backend level unlock logic intentionally does not implement frontend lock UI, progress percentage, course completion UI, rank recalculation, streak logic, weak concept detection, leaderboard, achievements, Piston/code execution, deployment, or Phase 2 features.
- Backend Progress Fetch Endpoint Foundation is implemented.
- Backend progress fetch endpoint is GET `/api/progress/courses/{courseId}`.
- GET `/api/progress/courses/{courseId}` is authenticated and uses `@AuthenticationPrincipal CurrentUserPrincipal`.
- GET `/api/progress/courses/{courseId}` accepts only `courseId` from the path and never accepts `userId` from request body, query params, headers, or path.
- Backend progress fetch response is a safe DTO and includes `courseId`, `completedLevels`, `totalLevels`, `progressPercent`, `courseCompleted`, and ordered `levels`.
- Backend progress fetch per-level response includes `levelId`, `orderNumber`, `title`, `isBoss`, `xpReward`, `completed`, `unlocked`, and nullable `completedAt`.
- Backend progress fetch returns levels ordered by `orderNumber` ascending.
- Backend progress fetch computes `completed` and `completedAt` only from the authenticated user's completed progress rows.
- Backend progress fetch computes `unlocked` using the same MVP rule as backend level completion: level 1 unlocked by default, later levels and boss levels unlocked only when all previous levels in the same course are completed by the same authenticated user, and already-completed levels are unlocked.
- Backend progress fetch computes integer `progressPercent` using `completedLevels * 100 / totalLevels`, returning 0 for zero-level courses.
- Backend progress fetch sets `courseCompleted=true` only when `totalLevels > 0` and `completedLevels == totalLevels`.
- Backend progress fetch is user-scoped; user A progress never appears in user B progress for the same course.
- Missing courses return standard 404 ErrorDTO with safe message `Course not found.`.
- No-token progress fetch requests return 401 through the existing Spring Security flow.
- Backend progress fetch intentionally does not implement frontend lock UI, frontend complete button, frontend progress display, rank recalculation, streak logic, weak concept detection, leaderboard, achievements, Piston/code execution, deployment, or Phase 2 features.
- No new Flyway migration was needed for progress fetch because it reads existing courses, levels, and progress data.
- Frontend Course Progress / Lock UI Foundation is implemented.
- Frontend Course Map now fetches backend progress through authenticated GET `/api/progress/courses/{courseId}` using the existing token/API helper pattern.
- Frontend progress helper `getCourseProgress(courseId)` is implemented in `frontend/src/services/courseApi.js`.
- DashboardShell merges backend progress levels into existing Course Map level cards by `levelId`.
- Course Map now displays progress summary with completed level count, total level count, progress percent, progress bar, and course-completed badge/state when applicable.
- Course Map level cards now show completed, ready/unlocked, locked, boss, XP, and completedAt UI states based on backend progress data.
- Locked levels show the safe message `Complete previous levels to unlock this level.` and their `Open Lesson` action is disabled.
- Ready/unlocked and completed levels can still open the existing Lesson view.
- Progress fetch failures show a safe inline message and must not crash the Course Map or expose raw backend details.
- Frontend Course Progress / Lock UI Foundation intentionally does not call POST `/api/levels/{levelId}/complete` and does not implement a complete-level button yet.
- Frontend Course Progress / Lock UI Foundation did not change backend files, DB migrations, package files, React Router, API contracts, Gemini/AI, quiz submit, notes backend, rank, streak, weak concept detection, leaderboard, Piston/code execution, deployment, or Phase 2 features.
- Frontend Complete Level Button / Progress Refresh Foundation is implemented.
- Frontend complete-level helper `completeLevel(levelId)` is implemented in `frontend/src/services/courseApi.js`.
- Frontend complete-level flow calls authenticated POST `/api/levels/{levelId}/complete` using the existing Bearer token/API helper pattern.
- Frontend complete-level flow sends no userId and no client ownership fields; backend derives user ownership from JWT/SecurityContext.
- Course Map and Lesson view now provide a `Complete Level` action for unlocked, not-yet-completed levels.
- Locked levels do not expose an enabled normal complete action.
- Already completed levels show completed state and do not expose normal repeat-completion UI for XP re-award.
- Complete-level requests use per-level loading/success/error state so duplicate clicks are prevented and messages do not confuse multiple levels.
- After a successful level completion, the frontend refreshes course progress through GET `/api/progress/courses/{courseId}`.
- After a successful level completion, the frontend attempts to refresh the authenticated profile through the existing shared profile refresh callback so dashboard/profile XP updates.
- If profile refresh fails after completion, completion success remains visible and only a safe small follow-up message is shown.
- Frontend complete-level error handling shows safe user-facing messages for 401, 403, 404, and generic failures without exposing raw backend JSON or stack traces.
- The required locked-level message stays safe: `Complete previous levels before unlocking this level.`
- Frontend complete-level state stays in React component state only and is not stored in localStorage or sessionStorage.
- Frontend Complete Level Button / Progress Refresh Foundation did not change backend files, DB migrations, package files, React Router, API contracts, Gemini/AI, auth, quiz backend, flashcard backend, note backend, problem, leaderboard, Docker, CI/CD, deployment, rank, streak, weak concept detection, Piston/code execution, or Phase 2 features.
- Backend XPService + Rank Recalculation Foundation is implemented.
- `XPService` centralizes existing backend XP additions and rank recalculation.
- Existing XP award amounts were intentionally preserved:
  - Quiz submit continues using the existing quiz XP behavior.
  - Level completion continues using `level.xpReward`.
  - Placeholder levels continue awarding 50, 75, and 100 XP.
- XPService rank thresholds are:
  - `BEGINNER`: 0 XP
  - `CODER`: 500 XP
  - `DEVELOPER`: 2000 XP
  - `ENGINEER`: 5000 XP
  - `ARCHITECT`: 12000 XP
  - `LEGEND`: 25000 XP
- XPService allows zero XP without corrupting rank/XP and rejects negative XP safely.
- Level completion now uses XPService for first-completion XP additions and rank recalculation.
- Quiz submit now uses XPService for correct-answer XP additions and rank recalculation.
- Repeated level completion remains idempotent and does not increase XP or rank again.
- Repeated correct quiz submit behavior remains the existing MVP behavior and can still award XP again; anti-farming/deduplication is still deferred.
- User profile naturally shows updated rank because profile reads persisted `user.rank`.
- Backend XPService + Rank Recalculation Foundation did not add a new endpoint and did not change public response DTO shapes.
- Backend XPService + Rank Recalculation Foundation did not add a DB migration because rank already existed on the user model/schema.
- Backend XPService + Rank Recalculation Foundation did not touch frontend, DB migrations, package files, docs/Build Log during implementation, AI/Gemini, auth, course, flashcard, note, problem, leaderboard, Docker, CI/CD, deployment, streak, weak concept detection, Piston/code execution, or Phase 2 features.
- Backend StreakService + Daily Login XP Guard is implemented.
- `StreakService` lives in the backend progress module and is independent of HTTP/client input.
- `StreakService` uses server-side time through `java.time.Clock` so streak behavior is testable and not based on frontend/client time.
- Daily login XP is `+30 XP` and is awarded only once per authenticated user per server calendar day.
- Daily login XP uses existing `XPService`, so rank recalculation happens automatically if daily login XP crosses a rank threshold.
- First successful login with `lastLogin=null` sets streak to `1`, sets `lastLogin`, and awards `+30 XP`.
- Same-day successful login does not award XP again and does not increment streak again.
- Consecutive next-day successful login increments streak and awards `+30 XP`.
- Login after a gap resets streak to `1` and awards `+30 XP`.
- Invalid/null existing streak data is normalized safely during streak processing.
- Daily login XP is not awarded on registration, profile fetch, refresh-token flow, logout, or JWT validation/filter activity.
- Registration behavior intentionally stayed unchanged. A fresh register response may still show blank/null streak before the first login; login initializes streak correctly.
- Login response naturally shows updated XP/rank/streak because streak logic runs after credential validation and before response mapping.
- Profile response naturally shows persisted XP/rank/streak, but profile fetch itself does not mutate streak or award XP.
- Refresh-token behavior, logout behavior, JWT claims, rank thresholds, quiz XP behavior, and level-completion XP behavior were not changed by the streak feature.
- Backend StreakService + Daily Login XP Guard added no new public endpoint and no DB migration.
- Backend StreakService + Daily Login XP Guard did not touch frontend, DB migrations, package files, docs/Build Log during implementation, AI/Gemini, course, flashcard, note, problem, leaderboard, Docker, CI/CD, deployment, weak concept detection, Piston/code execution, or Phase 2 features.
- Backend Weak Concept Detection Foundation is implemented as a narrow response-only MVP feature in quiz submit.
- `POST /api/quizzes/{quizQuestionId}/submit` now returns safe `weakConcepts` in addition to existing safe fields: `quizQuestionId`, `selectedAnswer`, `isCorrect`, `explanation`, and `concept`.
- For wrong quiz answers, `weakConcepts` is derived from the backend-stored quiz `conceptTag`/concept field only.
- Wrong-answer weak concept extraction trims the concept and returns a single-item list when the concept is nonblank.
- Wrong-answer weak concept extraction returns an empty list for null/blank concepts.
- Correct quiz answers always return an empty `weakConcepts` list.
- Weak concept detection is implemented in `QuizService`, not in the controller.
- Weak concept detection does not call Gemini and does not generate remedial levels.
- Weak concept detection does not persist weak concept rows and does not add a DB migration.
- Weak concept detection does not change quiz scoring, XP award rules, quiz attempt persistence/history, rank, streak, progress, unlock rules, frontend UI, Piston/code execution, leaderboard, or Phase 2 behavior.
- Quiz submit response still must never expose `correctAnswer`, `userId`, passwords, tokens, refresh tokens, token hashes, role, secrets, raw entities, raw stack traces, or raw backend JSON dumps.
- Backend Piston Run Code Foundation is implemented as a backend-only run-only MVP feature.
- `POST /api/problems/{problemId}/run` is authenticated and protected by the existing JWT security flow.
- `POST /api/problems/{problemId}/run` accepts `language`, `code`, optional `stdin`, and optional `expectedOutput`.
- Run-code language allowlist is limited to `java`, `python`, `javascript`, and `cpp`.
- Run-code requests reject blank code and reject code longer than 20000 characters.
- Run-code optional stdin and expected output use bounded MVP request validation.
- Run-code delegates execution only to Piston through a mockable `PistonClient` abstraction.
- `PistonHttpClient` uses Spring `RestClient` style and calls the configured Piston `/execute` endpoint.
- `PISTON_BASE_URL` can override the safe Piston base URL config.
- CodeQuest backend must never execute user code locally; do not use `ProcessBuilder`, `Runtime.exec`, JShell, local Docker execution, local compiler execution, or any local sandbox.
- Run-only endpoint does not award XP and does not call `XPService`.
- Run-only endpoint does not persist code, submissions, histories, attempts, or code-submission rows.
- Run-only endpoint keeps `problemId` in the path and response for API contract compatibility.
- Because coding problem persistence is not currently implemented, run-only currently does not perform a DB lookup for `problemId` and does not create fake seed problems.
- Run-code response is safe and may include `problemId`, `language`, `stdout`, `stderr`, `output`, `exitCode`, nullable `runtimeMs`, nullable `passed`, and safe `message`.
- Run-code pass/fail comparison trims the primary output and expected output only for comparison.
- If `expectedOutput` is present and trimmed output matches, `passed=true`.
- If `expectedOutput` is present and trimmed output differs, `passed=false`.
- If `expectedOutput` is missing, `passed=null`.
- Piston unavailable/request failure/malformed response maps safely to `CODE_RUNNER_UNAVAILABLE` and HTTP 503.
- Run-code errors must not expose raw Piston response bodies, raw stack traces, full user code logs, stdin, expectedOutput, passwords, tokens, token hashes, secrets, hidden tests, correctAnswer, userId, raw entities, or raw backend JSON dumps.
- Backend Piston Run Code Foundation did not touch frontend, DB migrations, backend/pom.xml, docs/Build Log during implementation, AI/Gemini, auth, course, level, progress, user, quiz, flashcard, note, leaderboard, Docker, CI/CD, deployment, code submit/history, AI code review, or Phase 2 features.
- Backend Code Submit Foundation is implemented as a backend-only submit MVP feature.
- `POST /api/problems/{problemId}/submit` is authenticated and protected by the existing JWT security flow.
- Code submit accepts `language`, `code`, optional `stdin`, and required `expectedOutput` for MVP visible-output comparison.
- Code submit language allowlist is limited to `java`, `python`, `javascript`, and `cpp`.
- Code submit rejects blank code and rejects code longer than 20000 characters.
- Code submit delegates execution only to Piston through the existing mockable `PistonClient` abstraction.
- CodeQuest backend must still never execute user code locally for submit; do not use `ProcessBuilder`, `Runtime.exec`, JShell, local Docker execution, local compiler execution, or any local sandbox.
- Code submit compares trimmed primary output with trimmed `expectedOutput`.
- Code submit persists every successful runner-backed submit attempt into `code_submissions`.
- Code submit does not persist attempts when Piston is unavailable, when validation fails, when authentication is missing, or when runner execution cannot return a safe result.
- V9 Flyway migration `V9__create_code_submissions_table.sql` creates `code_submissions`.
- `code_submissions` includes `id`, `user_id`, `problem_id`, `language`, `code`, `passed`, `passed_test_cases`, `total_test_cases`, nullable `runtime_ms`, nullable `memory_kb`, nullable `ai_review`, `submitted_at`, `created_at`, and `updated_at`.
- V9 adds indexes for `user_id`, `(user_id, problem_id)`, `problem_id`, and `submitted_at`.
- Because coding problem persistence is not currently implemented, submit stores the path `problemId` for API compatibility and does not create fake seed problems.
- Because the current Piston response abstraction does not expose runtime timing/memory, submit may return/persist nullable `runtimeMs` and nullable `memoryKb`.
- Submit uses a one visible-test MVP comparison, so `passed_test_cases` and `total_test_cases` are based on the expected-output comparison.
- Code submit awards coding XP only when `passed=true` and the authenticated user has no earlier passed submission for the same problem.
- Code submit uses the existing `XPService` for coding XP/rank recalculation.
- MVP default coding XP is 100 when no persisted coding problem XP reward is available.
- Repeated accepted submissions for the same authenticated user and problem persist another attempt but award `0` XP and return `firstAccepted=false`.
- Failed submissions persist an attempt with `passed=false` but award `0` XP.
- Run-only endpoint `POST /api/problems/{problemId}/run` remains unchanged, does not persist submissions, and does not award XP.
- Code submit response is safe and may include `problemId`, `language`, `stdout`, `stderr`, `output`, `exitCode`, nullable `runtimeMs`, `passed`, `xpAwarded`, `firstAccepted`, and safe `message`.
- Piston unavailable/request failure/malformed response maps safely to `CODE_RUNNER_UNAVAILABLE` and HTTP 503.
- Code submit response/errors must not expose raw Piston response bodies, raw stack traces, full user code logs, stdin, expectedOutput, passwords, tokens, refresh tokens, token hashes, secrets, hidden tests, correctAnswer, userId, raw entities, or raw backend JSON dumps.
- Backend Code Submit Foundation did not touch frontend, backend/pom.xml, docs/Build Log during implementation, AI/Gemini, auth, course, level, progress business rules except using existing XPService, user profile contracts, quiz, flashcard, note, leaderboard, Docker, CI/CD, deployment, code submissions history endpoint, AI code review, or Phase 2 features.
- Backend Code Submissions History / Fetch Foundation is implemented as a backend-only history MVP feature.
- `GET /api/problems/{problemId}/submissions` is authenticated and protected by the existing JWT security flow.
- Code submissions history accepts `problemId` from the path and optional query params `page` and `size`.
- Code submissions history uses default `page=0` and default `size=20`.
- Code submissions history enforces maximum `size=50`.
- Code submissions history returns safe 400 ErrorDTO for `page < 0`, `size < 1`, and `size > 50`.
- Code submissions history derives the current user only from `CurrentUserPrincipal` / JWT security context.
- Code submissions history never accepts `userId` from request body, query params, path, headers, or any client-owned field.
- Code submissions history filters by both authenticated `user_id` and path `problem_id`.
- Code submissions history returns only the authenticated user's own submissions for the requested problem.
- Other users' submissions for the same problem are hidden.
- The same user's submissions for other problems are hidden.
- Code submissions history returns rows newest-first by `submitted_at` / `submittedAt`.
- Empty code submissions history returns 200 with `totalItems=0`, `totalPages=0`, and an empty `items` list.
- Code submissions history response is a safe DTO wrapper with `problemId`, `page`, `size`, `totalItems`, `totalPages`, and `items`.
- Each history item safely exposes `submissionId`, `problemId`, `language`, `code`, `passed`, `passedTestCases`, `totalTestCases`, nullable `runtimeMs`, nullable `memoryKb`, nullable `aiReview`, and `submittedAt`.
- It is intentionally allowed for a user to see their own submitted code in their own history response.
- Code submissions history does not expose `userId`, password fields, token fields, refresh tokens, tokenHash, role, secrets, correctAnswer, hidden tests, expectedOutput, stdin, raw Piston compile/run internals, raw stack traces, raw entities, or raw backend JSON dumps.
- Code submissions history does not call Piston.
- Code submissions history does not call Gemini.
- Code submissions history does not award XP and does not call `XPService`.
- Code submissions history does not create fake seed submissions in production code.
- Code submissions history did not add or edit any Flyway migrations because V9 `code_submissions` already exists.
- Run-only endpoint `POST /api/problems/{problemId}/run` remains unchanged by history.
- Submit endpoint `POST /api/problems/{problemId}/submit` remains unchanged by history.
- Backend Code Submissions History / Fetch Foundation changed only problem module source, problem DTOs, and problem tests.
- Backend Code Submissions History / Fetch Foundation did not touch frontend, backend/pom.xml, DB migrations, docs/Build Log during implementation, AI/Gemini, auth, course, level, progress, user, quiz, flashcard, note, leaderboard, Docker, CI/CD, deployment, AI code review, or Phase 2 files.
- Backend AI Code Review Foundation is implemented as a backend-only raw-code review MVP feature.
- `POST /api/ai/review-code` is authenticated and protected by the existing JWT security flow.
- AI code review accepts raw request code only for MVP: `language`, `code`, optional `problemTitle`, and optional `problemDescription`.
- AI code review does not accept `userId`, `submissionId`, problem ownership fields, token fields, role fields, or any client-owned ownership data.
- AI code review derives authentication from `@AuthenticationPrincipal CurrentUserPrincipal`; the current raw-code MVP does not need to use or expose the user's id.
- AI code review language allowlist is limited to `java`, `python`, `javascript`, and `cpp`.
- AI code review rejects blank code and rejects code longer than 20000 characters.
- AI code review bounds optional `problemTitle` and optional `problemDescription` for safe prompt context.
- AI code review builds prompts only through `PromptBuilder.buildCodeReviewPrompt(...)`.
- AI code review calls Gemini only through the existing `GeminiService` / `GeminiClient` abstraction.
- AI code review parses/validates structured Gemini JSON only through `ResponseParser.parseCodeReviewResponse(...)`.
- AI code review response is a safe DTO with `timeComplexity`, `spaceComplexity`, `correctnessIssues`, `improvements`, `betterApproach`, and `encouragement`.
- AI code review intentionally returns only structured feedback and does not persist anything for MVP.
- AI code review does not update `code_submissions.ai_review` yet even though the V9 column exists.
- AI code review does not load or mutate `CodeSubmission` rows.
- AI code review does not call Piston and does not execute user code locally.
- AI code review does not award XP, does not call `XPService`, and does not change rank, streak, progress, unlock state, quiz attempts, notes, courses, levels, or leaderboard state.
- AI code review prompt treats user code, problem title, and problem description as untrusted text and tells Gemini to ignore instructions embedded inside them.
- AI code review prompt requires JSON only, forbids markdown fences/prose/extra keys, and tells Gemini not to echo the full submitted code back.
- AI code review must not send secrets, JWTs, refresh tokens, passwords, token hashes, API keys, DB passwords, user roles, hidden tests, correct answers, raw stack traces, or private backend data to Gemini.
- AI code review errors are safe: Gemini unavailable/missing config maps to `AI_SERVICE_UNAVAILABLE` with HTTP 503; malformed AI response maps to `AI_RESPONSE_INVALID` with HTTP 502; Gemini 429 maps through existing rate-limit handling when detected.
- AI code review responses/errors must not expose raw Gemini bodies, raw prompts, stack traces, raw backend JSON dumps, tokens, secrets, passwords, `userId`, `correctAnswer`, hidden tests, stdin, expectedOutput, or Piston internals.
- Backend AI Code Review Foundation changed only AI module source/tests plus minimal common exception files.
- Backend AI Code Review Foundation did not touch frontend, backend/pom.xml, DB migrations, problem module, docs/Build Log during implementation, auth, course, level, progress, user, quiz, flashcard, note, leaderboard, Docker, CI/CD, deployment, or Phase 2 files.
- Backend Leaderboard REST Foundation is implemented as a backend-only read-only leaderboard MVP feature.
- `GET /api/leaderboard` is authenticated and protected by the existing JWT security flow.
- Leaderboard request accepts optional query params `page`, `size`, and `period` only.
- Leaderboard defaults are `page=0`, `size=50`, and `period=ALL_TIME`.
- Leaderboard enforces maximum `size=50`.
- Leaderboard returns safe 400 ErrorDTO for `page < 0`, `size < 1`, `size > 50`, and any `period` other than `ALL_TIME`.
- Leaderboard supports `ALL_TIME` only for MVP; no weekly/monthly/seasonal leaderboard table or logic was added.
- Leaderboard derives the authenticated user only from `@AuthenticationPrincipal CurrentUserPrincipal` / JWT security context.
- Leaderboard never accepts `userId` from request body, query params, path, headers, or any client-owned field.
- Leaderboard response is a safe DTO wrapper with `page`, `size`, `period`, `totalItems`, `totalPages`, `items`, and `currentUser`.
- Leaderboard item response exposes only `rankPosition`, `userId`, `name`, `xp`, `rank`, and `streak`.
- Current user leaderboard response exposes only `rankPosition`, `userId`, `xp`, and `rank`.
- Leaderboard sorting uses XP descending with deterministic tie-breaking by name ascending and id ascending.
- Leaderboard `rankPosition` is a 1-based ordinal global position after sorting, not dense/shared rank.
- Paginated leaderboard item positions remain global, so page 1 with size 1 starts at rankPosition 2.
- `currentUser` is included even when the authenticated user is not on the requested page.
- Current user rank is computed with read-only repository/count logic using the same leaderboard ordering.
- Leaderboard reads existing `users` table data only.
- No new Flyway migration was added for leaderboard because the current MVP can use existing user data/indexing.
- Leaderboard does not expose email, password fields, role, token fields, refresh tokens, tokenHash, secrets, lastLogin, raw entities, stack traces, or backend internals.
- Leaderboard does not call Gemini, does not call Piston, does not execute code, does not award XP, and does not mutate user/progress/quiz/problem data.
- Backend Leaderboard REST Foundation changed only a new leaderboard module plus read-only query methods in `UserRepository`.
- Backend Leaderboard REST Foundation did not touch frontend, backend/pom.xml, DB migrations, AI/Gemini, problem, auth, course, level, progress, quiz, flashcard, note, docs/Build Log during implementation, Docker, CI/CD, deployment, or Phase 2 files.
- Backend tests after level unlock logic pass with 168 tests, 0 failures, 0 errors.
- Backend tests after the progress feature and JSONB mapping fix pass with 159 tests, 0 failures, 0 errors.
- Backend Quiz Attempt History/Fetch Foundation adds no migration and makes no frontend changes.
- Backend Quiz Attempt Persistence Foundation does not change frontend files.
- Backend Quiz Attempt Persistence Foundation does not change AI/Gemini, course generation/fetch, flashcards, or notes behavior.
- Backend Quiz Attempt Persistence Foundation does not implement XP/progress/rank/streak, weak concept detection, level unlock, course completion, leaderboard, Piston/code execution, deployment, or Phase 2 features.
- Quiz submit, quiz attempt persistence/history, quiz XP award/refresh, backend level completion progress foundation, backend level unlock enforcement, backend progress fetch, frontend Course Map progress/lock UI, rank, streak, weak concept detection, Piston run-code foundation, code submit, and code submissions history are implemented; AI code review, leaderboard, Docker, CI/CD, deployment, README, screenshots, demo video, and resume bullets remain unimplemented.
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
- Frontend Code Submit UI Foundation note: No blocking issue after browser verification of validation and safe unavailable path. Manual dashboard verification showed the Code Runner section still visible with problem id, language, code, stdin, expected output, Run Code, and Submit Code controls. Submit Code stayed disabled until expected output was provided, blank code disabled both Run Code and Submit Code, and clicking Submit Code after filling expected output reached the backend and showed the expected safe Piston-unavailable message `Code runner is currently unavailable. Please try again later.` This is acceptable because external Piston availability had already been known to fail intermittently during backend/frontend verification. The UI did not expose raw stack traces, raw Piston internals, raw backend JSON dumps, tokens, passwords, roles, token hashes, userId, hidden tests, correctAnswer, stdin/expectedOutput from backend internals, or secrets. Scope stayed frontend-only with expected changes limited to `frontend/src/pages/DashboardShell.jsx` and `frontend/src/services/courseApi.js`; no backend, DB migration, Docker, docs/Build Log during implementation, README, .github, docker-compose, frontend package, CI/CD, deployment, or Phase 2 files changed.
- Frontend Code Runner UI Foundation note: No blocking issue after browser verification of the safe unavailable path. Manual dashboard verification showed the Code Runner section visible with problem id, language, code, stdin, expected output, and Run Code controls. Initial use of a non-UUID-like `manual-problem-1` produced a generic safe error; retesting with UUID-style `11111111-1111-1111-1111-111111111111` reached the backend and showed the expected safe Piston-unavailable message `Code runner is currently unavailable. Please try again later.` This is acceptable because external Piston availability had already been known to fail intermittently during backend verification. The UI did not expose raw stack traces, raw Piston internals, raw backend JSON dumps, tokens, passwords, roles, token hashes, userId, hidden tests, correctAnswer, or secrets. Scope stayed frontend-only with expected changes limited to `frontend/src/pages/DashboardShell.jsx` and `frontend/src/services/courseApi.js`; no backend, DB migration, Docker, docs/Build Log during implementation, README, .github, docker-compose, frontend package, CI/CD, deployment, or Phase 2 files changed.
- Frontend Leaderboard UI Foundation note: No blocking issue after browser verification. Manual dashboard verification showed the Leaderboard section visible, current-user standing visible, `Refresh Leaderboard` button working after load, leaderboard rows displaying position/name/XP/rank/streak, and no visible token/password/role/raw JSON/stack trace data. Existing dashboard sections still rendered, including Profile Summary, Generate Course, generated course result, and Quiz Attempt History. Scope stayed frontend-only with expected changes limited to `frontend/src/pages/DashboardShell.jsx` and `frontend/src/services/courseApi.js`; no backend, DB migration, Docker, docs/Build Log during implementation, README, .github, docker-compose, frontend package, CI/CD, deployment, or Phase 2 files changed. Local observation: the pasted dashboard showed Profile Summary XP and Leaderboard current-user XP not matching at that moment; treat this as a non-blocking local state/test-data observation unless it is reproducible after a fresh profile refresh/login.
- Backend Docker Setup Foundation note: No blocking issue after manual Docker verification. Initial Docker build failed because the Linux builder stage read Windows CRLF line endings in `mvnw` and failed with `env: $'bash\r': No such file or directory`. This was fixed by normalizing `mvnw` inside `backend/Dockerfile` using `RUN sed -i 's/\r$//' mvnw && chmod +x mvnw` before running `./mvnw clean package -DskipTests`. After Docker Desktop engine was running, `docker build -t codequest-backend:local ./backend` completed successfully and `docker images codequest-backend` showed `codequest-backend:local` present. Backend tests passed with Maven Wrapper according to Codex output. Scope stayed Docker-only with expected changes limited to `backend/Dockerfile` and `backend/.dockerignore`; no frontend, frontend package files, backend/pom.xml, backend source, backend tests, migrations, application.yml, docs/Build Log during implementation, README, .github, docker-compose, CI/CD, deployment, or Phase 2 files changed.
- Backend Leaderboard REST Foundation note: No blocking issue after manual API verification. Manual verification used three fresh authenticated users and local verification-only SQL updates to set deterministic XP/rank/streak values. Authenticated `GET /api/leaderboard?page=0&size=50&period=ALL_TIME` returned 200 with users sorted by XP descending, global 1-based rank positions, and `currentUser` for Bravo at rankPosition 3. Pagination with `page=0,size=1` and `page=1,size=1` returned rank positions 1 and 2 while still including `currentUser`. Invalid page -1, size 0, size 51, and period `WEEKLY` returned safe 400 ErrorDTO responses. No-token request returned 401. Safety checks returned false for email, password fields, token fields, refresh tokens, tokenHash, role, secrets, lastLogin, `org.springframework`, and `java.lang`. Backend tests passed with 293 tests. Scope stayed backend-only with expected changes limited to `backend/src/main/java/com/codequest/leaderboard/LeaderboardController.java`, `backend/src/main/java/com/codequest/leaderboard/LeaderboardService.java`, leaderboard DTOs, leaderboard tests, and read-only query methods in `backend/src/main/java/com/codequest/user/UserRepository.java`; no frontend, frontend package files, backend/pom.xml, DB migration, docs/Build Log, AI/Gemini, problem, auth, course, level, progress, quiz, flashcard, note, Docker, CI/CD, deployment, or Phase 2 work.
- Backend AI Code Review Foundation note: No blocking code issue after manual API verification. Live manual success review could not be confirmed because the authenticated `POST /api/ai/review-code` request returned safe 503 `AI_SERVICE_UNAVAILABLE` during local runtime, which is acceptable when Gemini config/service is unavailable. The 503 body was a safe ErrorDTO with message `AI review service is currently unavailable. Please try again later.`, path `/api/ai/review-code`, and a requestId; it did not expose raw Gemini response, raw prompt, stack trace, secrets, tokens, passwords, userId, or backend internals. Invalid language `ruby` returned safe 400 `BAD_REQUEST` with message `Language must be one of: java, python, javascript, cpp.` Blank code returned safe 400 `VALIDATION_ERROR`. No-token request returned 401. Backend tests passed with 272 tests. Scope stayed backend-only with changes limited to `backend/src/main/java/com/codequest/ai/GeminiService.java`, `backend/src/main/java/com/codequest/ai/PromptBuilder.java`, `backend/src/main/java/com/codequest/ai/ResponseParser.java`, `backend/src/main/java/com/codequest/ai/AiCodeReviewService.java`, `backend/src/main/java/com/codequest/ai/AiController.java`, `backend/src/main/java/com/codequest/ai/dto/ReviewCodeRequest.java`, `backend/src/main/java/com/codequest/ai/dto/ReviewCodeResponse.java`, `backend/src/main/java/com/codequest/common/exception/ErrorCode.java`, `backend/src/main/java/com/codequest/common/exception/GlobalExceptionHandler.java`, and AI tests; no frontend, backend/pom.xml, DB migration, problem module, docs/Build Log, auth, course, level, progress, user, quiz, flashcard, note, leaderboard, Docker, CI/CD, deployment, or Phase 2 work.
- Backend Code Submissions History / Fetch Foundation note: No blocking issue after manual API verification. Manual verification used two fresh authenticated users and local manual `code_submissions` rows inserted only for verification. User 1 history for `GET /api/problems/{problemId}/submissions?page=0&size=20` returned exactly the two user 1 rows for that problem, sorted newest-first; user 2's row for the same problem was not returned; user 1's row for a different problem was not returned. User 2 history returned only user 2's own row. Empty history returned 200 with `totalItems=0`, `totalPages=0`, and empty `items`. Pagination with `page=0,size=1` and `page=1,size=1` returned the expected newest and older rows with `totalItems=2` and `totalPages=2`. Invalid pagination returned safe 400 ErrorDTO responses for negative page, size 0, and size 51. No-token request returned 401. Safety checks returned false for `userId`, password fields, token fields, refresh tokens, tokenHash, role, secrets, correctAnswer, hidden tests, expectedOutput, stdin, stackTrace, and Spring internals. Backend tests passed with 241 tests. Scope stayed backend-only with changes limited to `backend/src/main/java/com/codequest/problem/CodeSubmissionRepository.java`, `backend/src/main/java/com/codequest/problem/ProblemController.java`, `backend/src/main/java/com/codequest/problem/ProblemService.java`, `backend/src/main/java/com/codequest/problem/dto/CodeSubmissionHistoryItemResponse.java`, `backend/src/main/java/com/codequest/problem/dto/CodeSubmissionHistoryResponse.java`, `backend/src/test/java/com/codequest/problem/ProblemControllerTest.java`, and `backend/src/test/java/com/codequest/problem/ProblemServiceTest.java`; no frontend, backend/pom.xml, DB migration, docs, Build Log, AI/Gemini, auth, course, level, progress, user, quiz, flashcard, note, leaderboard, Docker, CI/CD, deployment, AI review, or Phase 2 work.
- Backend Code Submit Foundation note: No blocking issue after manual API verification. Manual runtime hit external Piston unavailability and the backend returned safe 503 ErrorDTO with `CODE_RUNNER_UNAVAILABLE`, message `Code runner is currently unavailable. Please try again later.`, path `/api/problems/{problemId}/submit`, and a requestId; no raw stack trace or raw Piston body was exposed. DB verification using `C:\Program Files\PostgreSQL\17\bin\psql.exe` confirmed `SELECT COUNT(*) FROM code_submissions WHERE problem_id = '<manual-problem-id>';` returned `0`, so Piston-unavailable submit did not persist a row. Profile after the 503 stayed at `xp=30`, so no coding XP was awarded. Invalid language `ruby` returned safe 400 with message `Language must be one of: java, python, javascript, cpp.`; no-token submit returned 401. V9 migration was manually inspected and safely creates only `code_submissions` plus indexes. Backend tests passed after implementation. Scope stayed backend-only with changes limited to `backend/src/main/java/com/codequest/problem/ProblemController.java`, `backend/src/main/java/com/codequest/problem/ProblemService.java`, `backend/src/main/java/com/codequest/problem/CodeSubmission.java`, `backend/src/main/java/com/codequest/problem/CodeSubmissionRepository.java`, `backend/src/main/java/com/codequest/problem/dto/SubmitCodeRequest.java`, `backend/src/main/java/com/codequest/problem/dto/SubmitCodeResponse.java`, `backend/src/main/resources/db/migration/V9__create_code_submissions_table.sql`, `backend/src/test/java/com/codequest/problem/ProblemControllerTest.java`, and `backend/src/test/java/com/codequest/problem/ProblemServiceTest.java`; no frontend, backend/pom.xml, AI/Gemini, auth, course, level, progress business-rule changes except using existing XPService, user, quiz, flashcard, note, leaderboard, Docker, CI/CD, deployment, history endpoint, AI review, or Phase 2 work.
- Backend Piston Run Code Foundation note: No blocking issue after manual API verification. Manual runtime hit external Piston unavailability and backend returned the expected safe 503 `CODE_RUNNER_UNAVAILABLE` style error instead of raw stack traces or raw Piston internals; this is acceptable for the feature because automated tests mock Piston and passed. Manual checks also confirmed invalid language returned 400, no-token run returned 401, run-only did not increase XP beyond existing daily login XP behavior, and response/error safety checks did not expose password fields, token fields, refresh token fields, tokenHash, secrets, `correctAnswer`, hidden tests, or `userId`. Backend tests passed with 215 tests, 0 failures, 0 errors, 0 skipped. Scope stayed backend-only with expected changes limited to `backend/src/main/java/com/codequest/problem/`, `backend/src/test/java/com/codequest/problem/`, `backend/src/main/java/com/codequest/common/exception/ErrorCode.java`, `backend/src/main/java/com/codequest/common/exception/GlobalExceptionHandler.java`, and `backend/src/main/resources/application.yml`; no frontend, DB migration, package, docs, Build Log, AI/Gemini, auth, course, level, progress, user, quiz, flashcard, note, leaderboard, Docker, CI/CD, deployment, code submit/history, AI review, or Phase 2 work.
- Backend Weak Concept Detection Foundation note: No blocking issue after manual API verification. Manual verification used existing quiz row `44821f81-730c-4b18-9b2b-fb6e70354366` with `correct_answer=B` and `concept_tag=Trie Definition`; wrong answer `A` returned `isCorrect=False`, `concept=Trie Definition`, and `weakConcepts={Trie Definition}`; correct answer `B` returned `isCorrect=True`, `concept=Trie Definition`, and empty `weakConcepts`; safety checks returned false for `correctAnswer`, `userId`, password, passwordHash, password_hash, token, refreshToken, tokenHash, and secret; no-token submit returned 401. Backend tests passed with 202 tests, 0 failures, 0 errors. Scope stayed backend-only with expected changes limited to `backend/src/main/java/com/codequest/quiz/QuizService.java`, `backend/src/main/java/com/codequest/quiz/dto/SubmitQuizAnswerResponse.java`, `backend/src/test/java/com/codequest/quiz/QuizControllerTest.java`, and `backend/src/test/java/com/codequest/quiz/QuizServiceTest.java`; no frontend, DB migration, package, docs, Build Log, AI/Gemini, auth, progress, user, problem, leaderboard, common security, Docker, CI/CD, deployment, Piston/code execution, or Phase 2 work.
- Backend StreakService + Daily Login XP Guard note: No blocking issue after manual API verification. Manual verification confirmed registration stayed unchanged with `xp=0`, `rank=BEGINNER`, and blank/null `streak`; first successful login awarded daily login XP and returned `xp=30`, `rank=BEGINNER`, `streak=1`; profile fetch after login stayed `xp=30`, `rank=BEGINNER`, `streak=1`; second same-day login did not award XP again and stayed `xp=30`, `rank=BEGINNER`, `streak=1`; repeated profile fetches and refresh-token flow did not award daily login XP; safety checks returned false for password, passwordHash, password_hash, token, refreshToken, tokenHash, secret, and correctAnswer; DB check confirmed `xp=30`, `rank=BEGINNER`, `streak=1`, and `last_login` not null. Backend tests passed with 199 tests, 0 failures, 0 errors. Scope stayed backend-only with expected changes limited to `backend/src/main/java/com/codequest/auth/AuthService.java`, `backend/src/main/java/com/codequest/progress/StreakService.java`, `backend/src/test/java/com/codequest/progress/StreakServiceTest.java`, `backend/src/test/java/com/codequest/auth/AuthServiceTest.java`, `backend/src/test/java/com/codequest/auth/AuthControllerTest.java`, `backend/src/test/java/com/codequest/user/UserControllerTest.java`, `backend/src/test/java/com/codequest/user/UserServiceTest.java`, and `backend/src/test/java/com/codequest/level/LevelControllerTest.java`; no frontend, DB migration, package, docs, Build Log, AI/Gemini, problem, leaderboard, common security, Docker, CI/CD, deployment, weak concept, Piston/code execution, or Phase 2 work.
- Backend XPService + Rank Recalculation Foundation note: No blocking issue after manual API verification. Manual verification confirmed fresh user profile started with `xp=0` and `rank=BEGINNER`; completing one placeholder course moved profile to `XP=225, Rank=BEGINNER`; completing two placeholder courses moved profile to `XP=450, Rank=BEGINNER`; completing three placeholder courses moved profile to `XP=675, Rank=CODER`; repeat level completion stayed idempotent with `alreadyCompleted=True`, `xpAwarded=0`, and unchanged XP/rank; profile safety checks returned false for password, passwordHash, password_hash, token, refreshToken, tokenHash, secret, and correctAnswer. Backend tests passed with 191 tests, 0 failures, 0 errors. Scope stayed backend-only with expected changes limited to `backend/src/main/java/com/codequest/progress/XPService.java`, `backend/src/main/java/com/codequest/progress/ProgressService.java`, `backend/src/main/java/com/codequest/quiz/QuizService.java`, `backend/src/test/java/com/codequest/progress/XPServiceTest.java`, `backend/src/test/java/com/codequest/progress/ProgressServiceTest.java`, and `backend/src/test/java/com/codequest/quiz/QuizServiceTest.java`; no frontend, DB migration, package, docs, Build Log, AI/Gemini, problem, leaderboard, Docker, CI/CD, deployment, streak, weak concept, Piston/code execution, or Phase 2 work.
- Frontend Complete Level Button / Progress Refresh Foundation note: No blocking issue after manual browser verification. Manual browser verification confirmed fresh placeholder course initially showed 0/3 completed and 0%, level 1 ready/unlocked, level 2 locked, and boss locked; `Complete Level` was visible/enabled only for unlocked incomplete levels; completing level 1 updated progress to 1/3 and 33%, marked level 1 completed, unlocked level 2, kept boss locked, and refreshed dashboard/profile XP by 50; completing level 2 updated progress to 2/3 and 66%, unlocked boss, and refreshed XP by 75 more; completing boss updated progress to 3/3 and 100%, showed course completed state, and refreshed XP by 100 more; locked levels could not be completed through normal UI; Lesson-view complete action worked; Open Lesson, Back to Course Map, Quiz panel, Flashcards panel, Notes area, and note save/preload flow remained working; no accessToken, refreshToken, password, role, tokenHash, secrets, `correctAnswer`, raw backend stack trace, or raw JSON dump was visible. Frontend build passed. Scope stayed frontend-only with changes limited to `frontend/src/pages/DashboardShell.jsx` and `frontend/src/services/courseApi.js`; no backend, DB migration, package, docs, AI/Gemini, auth, quiz backend, flashcard backend, note backend, problem, leaderboard, Docker, CI/CD, deployment, rank, streak, weak concept, Piston/code execution, or Phase 2 work.
- Frontend Course Progress / Lock UI Foundation note: No blocking issue after manual browser verification. Manual browser verification confirmed Course Map loads after login/course generation, progress summary appears, fresh placeholder course shows 0/3 completed and 0%, level 1 is ready/unlocked, level 2 and boss are locked, locked levels show a safe unlock explanation and disabled `Open Lesson`, level 1 still opens the existing Lesson view, quiz/flashcards/notes/back flow remain working, locked level 2 and boss cannot be opened, and no accessToken, refreshToken, password, role, tokenHash, secrets, `correctAnswer`, raw backend stack trace, or raw JSON dump was visible. Frontend build passed. Scope stayed frontend-only with changes limited to `frontend/src/pages/DashboardShell.jsx` and `frontend/src/services/courseApi.js`; no backend, DB migration, package, docs, AI/Gemini, auth, quiz backend, flashcard backend, note backend, problem, leaderboard, Docker, CI/CD, deployment, complete-level button, rank, streak, weak concept, Piston/code execution, or Phase 2 work.
- Backend Progress Fetch Endpoint Foundation note: No blocking issue after manual verification. Manual PowerShell verification confirmed initial progress returns `completedLevels=0`, `totalLevels=3`, `progressPercent=0`, `courseCompleted=false`, level 1 unlocked, level 2 locked, and boss locked; after level 1 completion progress returns `completedLevels=1`, `progressPercent=33`, level 1 completed with `completedAt`, level 2 unlocked, and boss locked; after level 2 completion progress returns `completedLevels=2`, `progressPercent=66`, and boss unlocked; after boss completion progress returns `completedLevels=3`, `progressPercent=100`, `courseCompleted=true`, and all levels completed/unlocked; second user isolation returns fresh progress with no completedAt leakage; missing course returns 404; no-token request returns 401; response safety check showed no `userId`, password fields, role, tokens, secrets, `correctAnswer`, or note content. Backend tests passed with 177 tests, 0 failures, 0 errors. Scope stayed backend-only with expected changes to progress controller/service/repository/DTOs and progress tests; no frontend, migration, AI, auth, quiz, flashcard, note, problem, leaderboard, course, level, common exception, package, README, Docker, or CI/CD changes.
- Backend Level Unlock Logic Foundation note: No blocking issue after manual verification. Manual PowerShell verification confirmed locked level 2 returns 403 before level 1 completion, locked boss returns 403 before all previous levels are completed, locked attempts do not change XP, completing level 1 unlocks level 2, boss stays locked until level 2 is complete, completing level 2 then unlocks boss, repeat completion remains idempotent with `alreadyCompleted=true` and `xpAwarded=0`, final XP matched expected total 225, no-token request returned 401, random valid level UUID returned 404, and response safety check showed only safe fields. Backend tests passed with 168 tests, 0 failures, 0 errors. Scope stayed backend-only with expected changes to level/progress repositories/services/controller tests and no frontend, migration, AI, auth, quiz, flashcard, note, problem, leaderboard, package, README, Docker, CI/CD, or common exception changes.
- Backend Progress / Level Complete Foundation note: No blocking issue after fix. Manual PowerShell verification confirmed first authenticated level completion created progress and awarded level XP, repeated completion returned `alreadyCompleted=true` with `xpAwarded=0`, profile XP did not increase on repeat, no-token request returned 401, random valid level UUID returned 404, and response safety check showed only safe fields. Backend tests passed with 159 tests, 0 failures, 0 errors. Scope stayed backend-only with new LevelController, progress package, V8 migration, and level/progress tests; frontend, AI, auth, course, quiz, flashcard, note, problem, leaderboard, package files, README, Docker, CI/CD, and existing migrations stayed unchanged. Rank, streak, weak concept detection, unlock logic, progress percentage, course completion, leaderboard, achievements, anti-farming, Piston/code execution, and Phase 2 remain unimplemented.
- Resolved during Backend Progress / Level Complete Foundation: Initial manual PowerShell verification of `POST /api/levels/{levelId}/complete` returned 500. Backend logs showed PostgreSQL SQLState 42804: `column "quiz_answers_json" is of type jsonb but expression is of type character varying`. Root cause was an unused Java `String quizAnswersJson` entity mapping against a PostgreSQL JSONB column. Fix: removed the unused entity field and constructor argument so Hibernate no longer binds a varchar for the JSONB column; kept nullable `quiz_answers_json JSONB` in V8 for future schema alignment. Backend tests still passed with 159 tests and manual verification passed after the fix.
- Frontend XP Refresh After Correct Quiz Submit note: No blocking issue. Manual browser verification confirmed starting XP was visible, incorrect submit did not claim XP increased, correct submit refreshed profile/dashboard XP after backend award, repeated correct submit refreshed XP again according to MVP repeated-award behavior, quiz result remained safe, `correctAnswer` and `userId` were not visible, tokens/secrets/raw backend errors were not shown, and existing dashboard, course map, lesson, quiz submit, flashcards, notes preload/save, attempt history, and back-button flows remained working. Backend, migrations, package files, AI, auth API, course API, and backend files were unchanged. Rank/progress percentage/streak/weak concept/unlock logic remains unimplemented.
- Backend XP Award Foundation note: No blocking issue. Manual API verification confirmed wrong answer left XP unchanged, correct answer awarded quiz `xpReward`, submit response still did not expose `correctAnswer` or `userId`, and the feature remained backend-only with no frontend, migration, AI, course, flashcard, note, or user entity changes. Repeated correct submits intentionally award XP again for MVP and may need deduplication/anti-farming rules later. Rank/progress/streak/weak concept/unlock logic remains unimplemented.
- Frontend Quiz Attempt History Display Foundation note: No blocking issue. Manual browser verification confirmed attempt history cards render after quiz submissions, newest-first ordering is visible, selected answer/correctness/timestamp/question/course/level/concept/explanation display safely, muted attemptId and quizQuestionId display, and `correctAnswer`, `userId`, tokens, passwords, and secrets are not visible. Backend, migrations, package files, AI, course, flashcard, note, and backend quiz files were unchanged. XP/progress/rank/streak/weak concept/unlock logic remains unimplemented.
- Backend Quiz Attempt History/Fetch Foundation note: No blocking issue. Manual API verification confirmed first user history returned only first user attempts ordered newest-first, response hid `correctAnswer` and `userId`, second user initially received empty `attempts`, second user saw only their own new attempt after submitting, first user history did not include second user attempt, no-token GET returned 401, and frontend/migration/AI/course/flashcard/note diffs were empty. XP/progress/rank/streak/weak concept/unlock logic remains unimplemented.
- Backend Quiz Attempt Persistence Foundation note: No blocking issue. Manual API/DB verification confirmed valid authenticated submit inserted one `quiz_attempts` row, repeated submit inserted a second row instead of overwriting, invalid selectedAnswer `Z` returned 400 with no new row, random valid quiz UUID returned 404, no-token request returned 401, and submit response still did not expose `correctAnswer`. Frontend, AI, course, flashcard, and note files were unchanged. DB migration change was limited to V7 quiz attempts. XP/progress/rank/streak/weak concept/unlock logic remains unimplemented.
- Frontend Quiz Submit Integration note: No blocking issue. Manual browser verification confirmed quiz questions/options render, Submit Answer is disabled until an option is selected, selected answers submit to backend scoring endpoint, safe Correct/Incorrect result plus selected answer, explanation, and concept displays, changing the selected answer clears the previous result, different lessons do not leak quiz state, and `correctAnswer`/tokens/secrets are not visible. Backend, DB migrations, AI, course, quiz backend, flashcard, and note files were unchanged. Attempts, XP/progress, weak concept detection, and unlock logic remain unimplemented.
- Backend Quiz Submit/Scoring Foundation note: No blocking issue. Manual verification confirmed authenticated POST `/api/quizzes/{quizQuestionId}/submit` returns safe scoring response with `quizQuestionId`, `selectedAnswer`, `isCorrect`, `explanation`, and `concept`; `correctAnswer` is not exposed; invalid selectedAnswer `Z` returns 400; random valid quiz UUID returns 404; no-token request returns 401. No migration, frontend, AI, course, flashcard, note, XP/progress, unlock, or attempt-persistence changes were made.
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

| 44 | 2026-05-14 | Backend Quiz Submit/Scoring Foundation | Backend / Quiz | backend/src/main/java/com/codequest/quiz/QuizController.java; backend/src/main/java/com/codequest/quiz/QuizService.java; backend/src/main/java/com/codequest/quiz/dto/SubmitQuizAnswerRequest.java; backend/src/main/java/com/codequest/quiz/dto/SubmitQuizAnswerResponse.java; backend/src/test/java/com/codequest/quiz/QuizControllerTest.java; backend/src/test/java/com/codequest/quiz/QuizServiceTest.java | Backend `cd backend && .\mvnw.cmd test` PASS with 131 tests, 0 failures, 0 errors. Manual API verification PASS: valid quiz submit returned safe response without `correctAnswer`, invalid answer returned 400, random quiz UUID returned 404, no-token submit returned 401. Scope checks clean: frontend, DB migrations, AI, backend course, backend flashcard, and backend note diffs empty. | `a8a0f79 feat: add quiz submit scoring endpoint`. Added authenticated POST `/api/quizzes/{quizQuestionId}/submit`, request DTO, safe response DTO, service scoring logic against backend-only `correctAnswer`, and focused quiz controller/service tests. No migration, frontend, Gemini, course fetch, quiz fetch, flashcard, note, attempt persistence, XP/progress, unlock, Piston, deployment, or Phase 2 work. |
| 45 | 2026-05-14 | Frontend Quiz Submit Integration | Frontend / Quiz | frontend/src/services/courseApi.js; frontend/src/pages/DashboardShell.jsx | Frontend `cd frontend && npm run build` PASS. Manual browser verification PASS: quiz options render, Submit Answer enables only after selection, selected answer submits to backend, scoring result displays safe Correct/Incorrect state with selected answer, explanation, and concept, previous result clears on answer change, no lesson-to-lesson quiz state leakage, Flashcards and Notes still work, back navigation works, and `correctAnswer`/tokens/secrets are not visible. Scope checks clean: backend migrations, AI, backend course, backend quiz, backend flashcard, and backend note diffs empty. | `f6fa55d feat: integrate frontend quiz submit`. Added authenticated `submitQuizAnswer` helper and per-question frontend submit/loading/error/result state in Lesson Quiz panel. Uses `quizId` with `quizQuestionId` fallback, calls POST `/api/quizzes/{quizQuestionId}/submit` only on explicit button click, and keeps `correctAnswer` hidden. No backend, DB migration, Gemini, attempt persistence, XP/progress, unlock, Piston, deployment, or Phase 2 work. |
| 46 | 2026-05-15 | Backend Quiz Attempt Persistence Foundation | Backend / Quiz | backend/src/main/resources/db/migration/V7__create_quiz_attempts_table.sql; backend/src/main/java/com/codequest/quiz/QuizAttempt.java; backend/src/main/java/com/codequest/quiz/QuizAttemptRepository.java; backend/src/main/java/com/codequest/quiz/QuizService.java; backend/src/main/java/com/codequest/quiz/QuizController.java; backend/src/test/java/com/codequest/quiz/QuizServiceTest.java; backend/src/test/java/com/codequest/quiz/QuizControllerTest.java | Backend `cd backend && .\mvnw.cmd test` PASS with 136 tests, 0 failures, 0 errors. Manual API/DB verification PASS: valid submit inserted one attempt row, repeated submit inserted another row instead of overwriting, invalid answer returned 400 with no new row, random quiz UUID returned 404, no-token submit returned 401, and response still hid `correctAnswer`. Scope checks clean: frontend, AI, course, flashcard, and note diffs empty; migration diff limited to V7; quiz diff expected. | `40355ea feat: persist quiz submit attempts`. Added V7 quiz_attempts table, QuizAttempt entity/repository, and submit-flow persistence. Each successful authenticated submit creates a new attempt row; invalid/missing/unauthenticated submits do not persist. Response shape stayed unchanged and `correctAnswer` remains hidden. No frontend, AI/Gemini, course, flashcard, note, XP/progress, unlock, Piston, deployment, or Phase 2 work. |
| 47 | 2026-05-15 | Backend Quiz Attempt History/Fetch Foundation | Backend / Quiz | backend/src/main/java/com/codequest/quiz/QuizAttemptRepository.java; backend/src/main/java/com/codequest/quiz/QuizController.java; backend/src/main/java/com/codequest/quiz/QuizService.java; backend/src/main/java/com/codequest/quiz/dto/QuizAttemptHistoryItemResponse.java; backend/src/main/java/com/codequest/quiz/dto/QuizAttemptHistoryResponse.java; backend/src/test/java/com/codequest/quiz/QuizControllerTest.java; backend/src/test/java/com/codequest/quiz/QuizServiceTest.java | Backend `cd backend && .\mvnw.cmd test` PASS with 143 tests, 0 failures, 0 errors. Manual API verification PASS: first user created A then B attempts; history returned B before A; `correctAnswer` and `userId` were absent; second user initially got empty history; second user saw only their own C attempt; first user history did not include second user C; no-token request returned 401. Scope checks clean: frontend, DB migrations, AI, course, flashcard, and note diffs empty; quiz diff expected. | `a36ab6d feat: add quiz attempt history endpoint`. Added authenticated GET `/api/quizzes/attempts`, repository newest-first user-scoped query, service DTO mapping, and safe history response wrapper. No migration, frontend, AI/Gemini, course, flashcard, note, XP/progress, unlock, Piston, deployment, or Phase 2 work. |
| 48 | 2026-05-15 | Frontend Quiz Attempt History Display Foundation | Frontend / Quiz | frontend/src/services/courseApi.js; frontend/src/pages/DashboardShell.jsx | Frontend `cd frontend && npm run build` PASS. Manual browser verification PASS: DashboardShell attempt history loaded through GET `/api/quizzes/attempts`; newest-first attempts rendered with selected answer, Correct/Incorrect badge, attempted timestamp, question, concept, explanation, course title, and level title; `correctAnswer`, `userId`, tokens, passwords, and secrets were not visible. Scope checks clean: backend, DB migrations, package files, AI, course, flashcard, note, and backend quiz files unchanged. | `c227bc1 feat: display quiz attempt history`. Added authenticated `getQuizAttemptHistory()` helper and read-only Quiz Attempt History dashboard section with explicit Load/Refresh button, loading/empty/error states, safe attempt cards, and local component state only. No backend, migration, AI/Gemini, package, React Router, XP/progress, weak concept, unlock, Piston, deployment, or Phase 2 work. |
| 49 | 2026-05-15 | Backend XP Award Foundation for Quiz Submit | Backend / Quiz + User XP | backend/src/main/java/com/codequest/quiz/QuizService.java; backend/src/test/java/com/codequest/quiz/QuizServiceTest.java; backend/src/test/java/com/codequest/quiz/QuizControllerTest.java | Backend `cd backend && .\mvnw.cmd test` PASS with 147 tests, 0 failures, 0 errors. Manual API verification PASS: starting XP 0; incorrect answer A left XP unchanged; correct answer C awarded 10 XP; response hid `correctAnswer` and `userId`; repeated correct submit was verified as MVP behavior; invalid/missing/no-token paths were checked for no XP award. Scope checks clean: frontend, migrations, AI, course, flashcard, note, and user diffs empty; quiz diff expected. | `15321f1 feat: award xp for correct quiz submit`. Added backend XP award on correct quiz submit using authenticated current user only. Attempt persistence and safe submit response shape remain unchanged. Incorrect/invalid/missing/unauthenticated submits do not award XP. Repeated correct submits award XP again for MVP; no rank, streak, progress, weak concept, unlock, leaderboard, frontend, migration, AI, or Phase 2 work. |

| 50 | 2026-05-15 | Frontend XP Refresh After Correct Quiz Submit | Frontend / Profile + Quiz | frontend/src/App.jsx; frontend/src/pages/DashboardShell.jsx | Frontend `cd frontend && npm run build` PASS. Manual browser verification PASS: starting XP visible; incorrect quiz submit did not claim XP increased; correct submit refreshed profile/dashboard XP; repeated correct submit refreshed XP again; `correctAnswer`, `userId`, tokens, and secrets were not visible; existing course map, lesson, quiz submit, flashcards, notes, attempt history, and back buttons still worked. Scope checks clean: backend, migrations, package files, authApi, courseApi, and backend files unchanged. | `97e5493 feat: refresh xp after correct quiz submit`. Added shared profile refresh callback from App.jsx to DashboardShell and refreshed the authenticated profile after successful correct quiz submits. Profile/XP remain in React state only; no localStorage/sessionStorage persistence, no backend, migration, AI, package, rank, streak, progress, weak concept, unlock, anti-farming, or Phase 2 work. |

| 51 | 2026-05-16 | Backend Progress / Level Complete Foundation | Backend / Progress + Level | backend/src/main/java/com/codequest/level/LevelController.java; backend/src/main/java/com/codequest/progress/Progress.java; backend/src/main/java/com/codequest/progress/ProgressRepository.java; backend/src/main/java/com/codequest/progress/ProgressService.java; backend/src/main/java/com/codequest/progress/dto/LevelCompletionResponse.java; backend/src/main/resources/db/migration/V8__create_progress_table.sql; backend/src/test/java/com/codequest/level/LevelControllerTest.java; backend/src/test/java/com/codequest/progress/ProgressServiceTest.java | Backend `cd backend && .\mvnw.cmd test` PASS with 159 tests, 0 failures, 0 errors. Manual PowerShell verification PASS: first completion awarded level XP and created completed progress; repeat completion returned `alreadyCompleted=true`, `xpAwarded=0`, and no XP increase; no-token request returned 401; random valid level UUID returned 404; response safety check exposed only safe fields. Initial PostgreSQL JSONB/varchar mapping bug was fixed before commit and reverified. | `f86b082 feat: add level completion progress foundation`. Added V8 progress table, authenticated POST `/api/levels/{levelId}/complete`, progress entity/repository/service, safe response DTO, idempotent completion behavior, first-completion XP award, and focused level/progress tests. No frontend, AI/Gemini, auth, course generation, quiz submit/history, flashcard, note, problem, leaderboard, package, existing migration, README, Docker, CI/CD, rank, streak, weak concept, unlock, course completion, anti-farming, Piston, deployment, or Phase 2 work. |
| 52 | 2026-05-16 | Backend Level Unlock Logic Foundation | Backend / Progress + Level | backend/src/main/java/com/codequest/level/LevelController.java; backend/src/main/java/com/codequest/level/LevelRepository.java; backend/src/main/java/com/codequest/progress/ProgressRepository.java; backend/src/main/java/com/codequest/progress/ProgressService.java; backend/src/test/java/com/codequest/level/LevelControllerTest.java; backend/src/test/java/com/codequest/progress/ProgressServiceTest.java | Backend `cd backend && .\mvnw.cmd test` PASS with 168 tests, 0 failures, 0 errors. Manual PowerShell verification PASS: locked level 2 returned 403 before level 1, locked boss returned 403 before previous levels, locked attempts did not change XP, level 1/level 2/boss completed in order with XP 50/75/100, repeat level 1 returned `alreadyCompleted=true` and `xpAwarded=0`, final XP check returned true for expected 225, no-token returned 401, random valid UUID returned 404, and success responses exposed only safe fields. | `12cae38 feat: enforce level unlock rules`. Added backend unlock enforcement to the existing level completion flow. Level 1 is unlocked by default; later levels and boss levels require all earlier course levels completed by the same authenticated user. Reused existing `FORBIDDEN` ErrorDTO handling for locked levels. No frontend, migration, AI/Gemini, auth, quiz, flashcard, note, problem, leaderboard, common exception, package, README, Docker, CI/CD, rank, streak, weak concept, progress percentage, Piston, deployment, or Phase 2 work. |
| 53 | 2026-05-16 | Backend Progress Fetch Endpoint Foundation | Backend / Progress | backend/src/main/java/com/codequest/progress/ProgressController.java; backend/src/main/java/com/codequest/progress/ProgressRepository.java; backend/src/main/java/com/codequest/progress/ProgressService.java; backend/src/main/java/com/codequest/progress/dto/CourseProgressResponse.java; backend/src/main/java/com/codequest/progress/dto/LevelProgressResponse.java; backend/src/test/java/com/codequest/progress/ProgressControllerTest.java; backend/src/test/java/com/codequest/progress/ProgressServiceTest.java | Backend `cd backend && .\mvnw.cmd test` PASS with 177 tests, 0 failures, 0 errors. Manual PowerShell verification PASS: initial course progress showed 0/3, 0%, level 1 unlocked and later levels locked; after level 1 progress showed 1/3, 33%, level 2 unlocked and boss locked; after level 2 progress showed 2/3, 66%, boss unlocked; after boss progress showed 3/3, 100%, `courseCompleted=true`, and all levels completed/unlocked; second user isolation showed fresh 0/3 progress; missing course returned 404; no-token returned 401; response safety checks passed. | `f408fd6 feat: add course progress fetch endpoint`. Added authenticated GET `/api/progress/courses/{courseId}` with safe current-user course progress DTOs, ordered level progress items, completed/unlocked/completedAt calculation, integer progress percentage, course completion flag, 404/401 handling, and user-scoped isolation. No frontend, migration, AI/Gemini, auth, quiz, flashcard, note, problem, leaderboard, course, level, common exception, package, README, Docker, CI/CD, rank, streak, weak concept, Piston, deployment, or Phase 2 work. |
| 54 | 2026-05-16 | Frontend Course Progress / Lock UI Foundation | Frontend / Course Map + Progress UI | frontend/src/pages/DashboardShell.jsx; frontend/src/services/courseApi.js | Frontend `cd frontend && npm run build` PASS. Manual browser verification PASS: after backend/frontend startup, login and placeholder course generation worked; Course Map loaded with progress summary; fresh course showed 0/3 completed, 0%, level 1 ready/unlocked, level 2 locked, boss locked; locked cards showed safe unlock explanation and disabled `Open Lesson`; level 1 opened the existing Lesson view; lesson content, quiz panel, flashcards panel, notes area, and back flow still worked; locked level 2 and locked boss could not be opened; no tokens/passwords/secrets/correctAnswer/raw backend stack trace/raw JSON dump were visible. Scope checks clean: backend, migrations, package files, docs/Build Log, AI/Gemini, auth, quiz, flashcard, note, problem, leaderboard, Docker, CI/CD, deployment unchanged. | `5deeddd feat: show course progress lock states`. Added `getCourseProgress(courseId)` frontend helper, fetched progress alongside course details in DashboardShell Course Map, merged progress by `levelId`, added progress summary/progress bar/completed-ready-locked badges/completedAt display, disabled locked lesson opening, and added safe progress error handling. No complete-level button, no backend/API/DB/package change, no React Router, and no Phase 2 feature. |
| 55 | 2026-06-02 | Frontend Complete Level Button / Progress Refresh Foundation | Frontend / Level Completion + Progress Refresh | frontend/src/pages/DashboardShell.jsx; frontend/src/services/courseApi.js | Frontend `cd frontend && npm run build` PASS. Manual browser verification PASS: after backend/frontend startup with Gemini env vars removed, fresh placeholder course showed 0/3 and 0%, level 1 ready, level 2 locked, boss locked; completing level 1 updated progress to 1/3 and 33%, unlocked level 2, kept boss locked, and refreshed XP by 50; completing level 2 updated progress to 2/3 and 66%, unlocked boss, and refreshed XP by 75; completing boss updated progress to 3/3 and 100%, showed course completed state, and refreshed XP by 100; locked levels could not be completed through normal UI; Lesson complete flow, quiz, flashcards, notes, and back navigation remained working; no secrets/raw errors/correctAnswer were visible. Scope checks clean: backend, migrations, package files, docs/Build Log, AI/Gemini, auth, quiz backend, flashcard backend, note backend, problem, leaderboard, Docker, CI/CD, deployment unchanged. | `0543a9e feat: add frontend level completion flow`. Added authenticated `completeLevel(levelId)` frontend helper, per-level Complete Level UI for unlocked incomplete levels, per-level loading/success/error state, progress refresh through GET `/api/progress/courses/{courseId}`, and profile XP refresh through the existing shared profile refresh callback. No backend/API/DB/package change, no React Router, no rank/streak/weak concept/leaderboard/Piston/deployment, and no Phase 2 feature. |
| 56 | 2026-06-02 | Backend XPService + Rank Recalculation Foundation | Backend / XP + Rank | backend/src/main/java/com/codequest/progress/XPService.java; backend/src/main/java/com/codequest/progress/ProgressService.java; backend/src/main/java/com/codequest/quiz/QuizService.java; backend/src/test/java/com/codequest/progress/XPServiceTest.java; backend/src/test/java/com/codequest/progress/ProgressServiceTest.java; backend/src/test/java/com/codequest/quiz/QuizServiceTest.java | Backend `cd backend && .\mvnw.cmd test` PASS with 191 tests, 0 failures, 0 errors. Manual API verification PASS: starting profile `xp=0`, `rank=BEGINNER`; after one placeholder course `XP=225`, `Rank=BEGINNER`; after two courses `XP=450`, `Rank=BEGINNER`; after three courses `XP=675`, `Rank=CODER`; repeat completion returned `alreadyCompleted=True`, `xpAwarded=0`, and kept XP/rank unchanged; safety checks passed. Scope checks clean: frontend, DB migrations, backend/pom.xml, docs/Build Log, AI/Gemini, problem, and leaderboard diffs empty. | `6aba27a feat: add xp rank recalculation foundation`. Added XPService rank threshold foundation and wired existing level-completion and quiz-submit XP awards through it. Preserved existing XP amounts and response shapes. No new endpoint, no DB migration, no frontend change, no anti-farming change for repeated correct quiz submits, no streak/weak concept/leaderboard/Piston/deployment, and no Phase 2 feature. |

| 57 | 2026-06-02 | Backend StreakService + Daily Login XP Guard | Backend / Streak + Auth + User XP | backend/src/main/java/com/codequest/progress/StreakService.java; backend/src/main/java/com/codequest/auth/AuthService.java; backend/src/test/java/com/codequest/progress/StreakServiceTest.java; backend/src/test/java/com/codequest/auth/AuthServiceTest.java; backend/src/test/java/com/codequest/auth/AuthControllerTest.java; backend/src/test/java/com/codequest/user/UserControllerTest.java; backend/src/test/java/com/codequest/user/UserServiceTest.java; backend/src/test/java/com/codequest/level/LevelControllerTest.java | Backend `cd backend && .\mvnw.cmd test` PASS with 199 tests, 0 failures, 0 errors, 0 skipped. Manual API verification PASS: registration stayed unchanged with `xp=0`, `rank=BEGINNER`, blank/null `streak`; first login awarded daily login XP and returned `xp=30`, `rank=BEGINNER`, `streak=1`; second same-day login, repeated profile fetch, and refresh-token flow did not award XP again; DB showed `last_login` not null; safety checks passed. Scope checks clean: frontend, DB migrations, backend/pom.xml, docs/Build Log, AI/Gemini, problem, leaderboard, and common security diffs empty. | `7641b3f feat: add login streak daily xp guard`. Added backend StreakService and wired successful login to award +30 daily XP once per calendar day using XPService/rank recalculation. No new endpoint, no DB migration, no frontend change, no JWT/refresh/logout behavior change, no rank threshold change, no quiz/level XP change, no weak concept/leaderboard/Piston/deployment, and no Phase 2 feature. |

| 58 | 2026-06-03 | Backend Weak Concept Detection Foundation | Backend / Quiz | backend/src/main/java/com/codequest/quiz/QuizService.java; backend/src/main/java/com/codequest/quiz/dto/SubmitQuizAnswerResponse.java; backend/src/test/java/com/codequest/quiz/QuizControllerTest.java; backend/src/test/java/com/codequest/quiz/QuizServiceTest.java | Backend `cd backend && .\mvnw.cmd test` PASS with 202 tests, 0 failures, 0 errors, 0 skipped. Manual API verification PASS: wrong answer `A` for quiz `44821f81-730c-4b18-9b2b-fb6e70354366` returned `isCorrect=False`, `concept=Trie Definition`, and `weakConcepts={Trie Definition}`; correct answer `B` returned `isCorrect=True` and empty `weakConcepts`; safety checks confirmed no `correctAnswer`, `userId`, password/token/secret fields; no-token submit returned 401. Scope checks clean: frontend, DB migrations, backend/pom.xml, docs/Build Log, AI/Gemini, auth, progress, user, problem, leaderboard, and common security diffs empty. | `ff0a4d4 feat: add weak concepts to quiz submit`. Added response-only weak concept detection to quiz submit. Wrong answers return a trimmed backend concept tag in `weakConcepts`; correct answers return an empty list. No Gemini call, no remedial generation, no persistence/migration, no frontend change, no scoring/XP/rank/streak/progress/unlock change, no Piston/leaderboard/deployment, and no Phase 2 feature. |
| 59 | 2026-06-09 | Backend Piston Run Code Foundation | Backend / Problem + Code Runner | backend/src/main/java/com/codequest/problem/ProblemController.java; backend/src/main/java/com/codequest/problem/ProblemService.java; backend/src/main/java/com/codequest/problem/PistonClient.java; backend/src/main/java/com/codequest/problem/PistonHttpClient.java; backend/src/main/java/com/codequest/problem/PistonException.java; backend/src/main/java/com/codequest/problem/dto/RunCodeRequest.java; backend/src/main/java/com/codequest/problem/dto/RunCodeResponse.java; backend/src/main/java/com/codequest/problem/dto/PistonRequest.java; backend/src/main/java/com/codequest/problem/dto/PistonResponse.java; backend/src/main/java/com/codequest/common/exception/ErrorCode.java; backend/src/main/java/com/codequest/common/exception/GlobalExceptionHandler.java; backend/src/main/resources/application.yml; backend/src/test/java/com/codequest/problem/ProblemServiceTest.java; backend/src/test/java/com/codequest/problem/ProblemControllerTest.java | Backend `cd backend && .\mvnw.cmd test` PASS with 215 tests, 0 failures, 0 errors, 0 skipped. Manual API verification PASS for safe runtime behavior: authenticated run-code request reached backend; external Piston was unavailable and returned safe 503 `CODE_RUNNER_UNAVAILABLE` handling; invalid language returned 400; no-token returned 401; run-only did not award XP beyond daily login XP; response safety checks passed. Scope checks clean: frontend, DB migrations, backend/pom.xml, docs/Build Log, AI/Gemini, auth, course, level, progress, user, quiz, flashcard, note, and leaderboard diffs empty. | `d806c43 feat: add piston run code foundation`. Added authenticated run-only `POST /api/problems/{problemId}/run`, safe request/response DTOs, language allowlist, Piston client abstraction, RestClient-based Piston HTTP client, pass/fail expected-output comparison, safe 503 `CODE_RUNNER_UNAVAILABLE` mapping, and focused problem service/controller tests. No local code execution, no XP award, no submission persistence/history, no coding problem DB/entity work, no frontend, no migration, no AI review, no leaderboard, no deployment, and no Phase 2 feature. |

| 60 | 2026-06-09 | Backend Code Submit Foundation | Backend / Problem + Code Submissions | backend/src/main/java/com/codequest/problem/ProblemController.java; backend/src/main/java/com/codequest/problem/ProblemService.java; backend/src/main/java/com/codequest/problem/CodeSubmission.java; backend/src/main/java/com/codequest/problem/CodeSubmissionRepository.java; backend/src/main/java/com/codequest/problem/dto/SubmitCodeRequest.java; backend/src/main/java/com/codequest/problem/dto/SubmitCodeResponse.java; backend/src/main/resources/db/migration/V9__create_code_submissions_table.sql; backend/src/test/java/com/codequest/problem/ProblemControllerTest.java; backend/src/test/java/com/codequest/problem/ProblemServiceTest.java | Backend `cd backend && .\mvnw.cmd test` PASS. Manual API verification PASS for safe external Piston-unavailable behavior: authenticated submit returned safe 503 `CODE_RUNNER_UNAVAILABLE`; DB count stayed 0 for the unavailable run; profile XP stayed 30; invalid language returned 400; no-token submit returned 401; V9 migration content was manually inspected. Scope checks clean: changes were limited to problem module/tests and V9 migration. | `7a24c00 feat: add code submit foundation`. Added authenticated `POST /api/problems/{problemId}/submit`, safe submit request/response DTOs, V9 `code_submissions` persistence, first-accepted coding XP award using existing XPService, repeated accepted submit no-extra-XP rule, failed submit persistence when runner result exists, and focused service/controller tests. Run-only `/run` stayed unchanged with no persistence/XP. External Piston unavailable during live manual test, so happy path persistence/XP was covered by mocked automated tests; safe unavailable behavior was verified live. No frontend, pom, AI/Gemini, auth, course, level, quiz, flashcard, note, leaderboard, Docker, CI/CD, deployment, code history endpoint, AI review, or Phase 2 work. |
| 61 | 2026-06-09 | Backend Code Submissions History / Fetch Foundation | Backend / Problem + Code Submission History | backend/src/main/java/com/codequest/problem/CodeSubmissionRepository.java; backend/src/main/java/com/codequest/problem/ProblemController.java; backend/src/main/java/com/codequest/problem/ProblemService.java; backend/src/main/java/com/codequest/problem/dto/CodeSubmissionHistoryItemResponse.java; backend/src/main/java/com/codequest/problem/dto/CodeSubmissionHistoryResponse.java; backend/src/test/java/com/codequest/problem/ProblemControllerTest.java; backend/src/test/java/com/codequest/problem/ProblemServiceTest.java | Backend `cd backend && .\mvnw.cmd test` PASS with 241 tests. Manual API verification PASS: user 1 saw only own two submissions for the requested problem newest-first; user 2 saw only own row; same-user other-problem row was hidden; empty history returned 200 with empty items; pagination worked; invalid page/size returned safe 400; no-token returned 401; response safety checks passed. Scope checks clean: only problem module source/DTO/test files changed; no frontend, pom, migration, docs, Build Log, AI/Gemini, auth, course, level, progress, user, quiz, flashcard, note, leaderboard, Docker, CI/CD, deployment, AI review, or Phase 2 work. | `e823982 feat: add code submission history endpoint`. Added authenticated `GET /api/problems/{problemId}/submissions?page=0&size=20`, safe paginated history response DTOs, user/problem-scoped repository query, newest-first ordering, pagination validation (`page >= 0`, `1 <= size <= 50`), and focused service/controller tests. Does not call Piston/Gemini, does not award XP, does not add migrations, and does not change `/run` or `/submit`. |

| 62 | 2026-06-09 | Backend AI Code Review Foundation | Backend / AI | backend/src/main/java/com/codequest/ai/GeminiService.java; backend/src/main/java/com/codequest/ai/PromptBuilder.java; backend/src/main/java/com/codequest/ai/ResponseParser.java; backend/src/main/java/com/codequest/ai/AiCodeReviewService.java; backend/src/main/java/com/codequest/ai/AiController.java; backend/src/main/java/com/codequest/ai/dto/ReviewCodeRequest.java; backend/src/main/java/com/codequest/ai/dto/ReviewCodeResponse.java; backend/src/main/java/com/codequest/common/exception/ErrorCode.java; backend/src/main/java/com/codequest/common/exception/GlobalExceptionHandler.java; backend/src/test/java/com/codequest/ai/GeminiServiceTest.java; backend/src/test/java/com/codequest/ai/PromptBuilderTest.java; backend/src/test/java/com/codequest/ai/ResponseParserTest.java; backend/src/test/java/com/codequest/ai/AiCodeReviewServiceTest.java; backend/src/test/java/com/codequest/ai/AiControllerTest.java | Backend `cd backend && .\mvnw.cmd test` PASS with 272 tests. Manual API verification PASS for safe unavailable/error behavior: authenticated review request returned safe 503 `AI_SERVICE_UNAVAILABLE` when Gemini was unavailable; invalid language returned safe 400; blank code returned safe 400; no-token returned 401. Scope checks clean: only AI module source/tests plus minimal common exception mapping changed; no frontend, pom, migration, problem module, docs/Build Log, auth, course, level, progress, user, quiz, flashcard, note, leaderboard, Docker, CI/CD, deployment, or Phase 2 work. | `b682c40 feat: add ai code review endpoint`. Added authenticated `POST /api/ai/review-code`, raw-code request DTO, safe structured review response DTO, AI controller/service, code-review PromptBuilder method, GeminiService code-review method, ResponseParser code-review validation, minimal AI-safe ErrorDTO mappings, and focused AI controller/service/prompt/parser/Gemini tests. Does not persist reviews, does not use submissionId, does not update `code_submissions.ai_review`, does not call Piston, does not execute code, does not award XP, and does not change `/run`, `/submit`, or `/submissions`. |

| 63 | 2026-06-09 | Backend Leaderboard REST Foundation | Backend / Leaderboard | backend/src/main/java/com/codequest/leaderboard/LeaderboardController.java; backend/src/main/java/com/codequest/leaderboard/LeaderboardService.java; backend/src/main/java/com/codequest/leaderboard/dto/LeaderboardResponse.java; backend/src/main/java/com/codequest/leaderboard/dto/LeaderboardItemResponse.java; backend/src/main/java/com/codequest/leaderboard/dto/CurrentUserLeaderboardResponse.java; backend/src/main/java/com/codequest/user/UserRepository.java; backend/src/test/java/com/codequest/leaderboard/LeaderboardControllerTest.java; backend/src/test/java/com/codequest/leaderboard/LeaderboardServiceTest.java | Backend `cd backend && .\mvnw.cmd test` PASS with 293 tests. Manual API verification PASS: authenticated leaderboard returned 200 sorted by XP descending with global 1-based rank positions and `currentUser`; pagination returned global rank positions 1 and 2 for page 0/1 with size 1; invalid page/size/period returned safe 400; no-token returned 401; safety checks passed. Scope checks clean: only leaderboard module files plus read-only `UserRepository` query methods changed; no frontend, pom, migration, docs, Build Log, AI/Gemini, problem, auth, course, level, progress, quiz, flashcard, note, Docker, CI/CD, deployment, or Phase 2 work. | `68144b3 feat: add leaderboard endpoint`. Added authenticated `GET /api/leaderboard?page=0&size=50&period=ALL_TIME`, safe paginated leaderboard DTOs, XP-desc deterministic sorting, global ordinal rank positions, current-user rank summary, query validation, and focused controller/service tests. Supports `ALL_TIME` only for MVP. Does not mutate XP/rank/streak/progress, does not expose sensitive user fields, and does not add migrations/frontend work. |

| 64 | 2026-06-09 | Backend Docker Setup Foundation | DevOps / Docker | backend/Dockerfile; backend/.dockerignore | Backend `cd backend && .\mvnw.cmd test` PASS according to Codex output. Docker build verification PASS: `docker build -t codequest-backend:local ./backend` completed successfully after CRLF fix; `docker images codequest-backend` showed `codequest-backend:local` present with image ID `4afd74688965`. Scope checks clean: only backend Docker files changed; no frontend, pom, source, tests, migrations, application.yml, docs/Build Log during implementation, README, .github, docker-compose, CI/CD, deployment, or Phase 2 work. | `bc321df chore: add backend dockerfile`. Added backend-only multi-stage Java 21 Dockerfile using Maven Wrapper in the builder stage and Java 21 JRE runtime image, plus backend `.dockerignore`. Fixed Linux builder CRLF issue by normalizing `mvnw` with `sed -i 's/\r$//' mvnw` before `chmod +x`. Runtime config remains env-var based; no secrets are baked into the image. |

| 65 | 2026-06-10 | Frontend Leaderboard UI Foundation | Frontend / Leaderboard | frontend/src/pages/DashboardShell.jsx; frontend/src/services/courseApi.js | Frontend `cd frontend && npm run build` PASS according to Codex output. Manual browser verification PASS from user dashboard paste: Leaderboard section rendered, current-user standing rendered, leaderboard table showed safe fields, Refresh Leaderboard was visible, and existing dashboard/course/quiz history sections still rendered. Scope checks clean: only DashboardShell and courseApi changed; no backend, docs, Build Log during implementation, Docker, package, CI/CD, deployment, or Phase 2 files changed. | `1bb5159 feat: add frontend leaderboard ui`. Added authenticated `getLeaderboard()` helper and a manual-load DashboardShell leaderboard section using GET `/api/leaderboard?page=0&size=50&period=ALL_TIME`. Includes loading/error/empty states, current-user standing, safe table fields, and no auto-fetch/pagination/weekly filters/search/realtime/backend changes. |

| 66 | 2026-06-10 | Frontend Code Runner UI Foundation | Frontend / Code Runner | frontend/src/pages/DashboardShell.jsx; frontend/src/services/courseApi.js | Frontend `cd frontend && npm run build` PASS according to Codex output. Manual browser verification PASS for safe Piston-unavailable path: Code Runner section rendered, UUID-style problem id reached backend, Java code textarea accepted code, and UI showed safe `Code runner is currently unavailable. Please try again later.` message. Scope checks clean: only DashboardShell and courseApi changed; no backend, docs, Build Log during implementation, Docker, package, CI/CD, deployment, or Phase 2 files changed. | `f7b4598 feat: add frontend code runner ui`. Added authenticated `runCode(problemId, payload)` helper and a DashboardShell Code Runner section using POST `/api/problems/{problemId}/run`. Includes language selector, starter code, code/stdin/expected output textareas, validation, loading/error/result states, safe plain-text output rendering, and no Monaco/submit/history/AI-review/backend/package changes. |
| 67 | 2026-06-10 | Frontend Code Submit UI Foundation | Frontend / Code Submit | frontend/src/pages/DashboardShell.jsx; frontend/src/services/courseApi.js | Frontend `cd frontend && npm run build` PASS according to Codex output. Manual browser verification PASS for validation and safe Piston-unavailable path: Submit Code appeared beside Run Code, expected output was required before submit, blank code disabled run/submit, UUID-style problem id reached backend, and UI showed safe `Code runner is currently unavailable. Please try again later.` after Submit Code. Scope checks clean: only DashboardShell and courseApi changed; no backend, docs, Build Log during implementation, Docker, package, CI/CD, deployment, or Phase 2 files changed. | `52db876 feat: add frontend code submit ui`. Added authenticated `submitCode(problemId, payload)` helper and extended DashboardShell Code Runner with a separate Submit Code flow using POST `/api/problems/{problemId}/submit`. Includes submit validation, loading/error/result states, XP/firstAccepted display, profile refresh attempt when `xpAwarded > 0`, safe plain-text output rendering, and no Monaco/history/AI-review/backend/package changes. |


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

| 2026-05-14 | `cd backend && .\mvnw.cmd test` after Backend Quiz Submit/Scoring Foundation | PASS | Backend tests passed with 131 tests, 0 failures, 0 errors after adding authenticated quiz submit/scoring endpoint. | Yes |
| 2026-05-14 | `cd frontend && npm run build` after Frontend Quiz Submit Integration | PASS | Frontend build passed after adding per-question quiz submit integration in the Lesson Quiz panel. | Yes |
| 2026-05-15 | `cd backend && .\mvnw.cmd test` after Backend Quiz Attempt Persistence Foundation | PASS | Backend tests passed with 136 tests, 0 failures, 0 errors after adding quiz_attempts persistence. | Yes |
| 2026-05-15 | `cd backend && .\mvnw.cmd test` after Backend Quiz Attempt History/Fetch Foundation | PASS | Backend tests passed with 143 tests, 0 failures, 0 errors after adding authenticated quiz attempt history fetch endpoint. | Yes |
| 2026-05-15 | `cd frontend && npm run build` after Frontend Quiz Attempt History Display Foundation | PASS | Frontend build passed after adding authenticated quiz attempt history helper and DashboardShell attempt history display section. | Yes |

| 2026-05-15 | `cd backend && .\mvnw.cmd test` after Backend XP Award Foundation for Quiz Submit | PASS | Backend tests passed with 147 tests, 0 failures, 0 errors after adding correct-answer XP award to quiz submit. | Yes |

| 2026-05-15 | `cd frontend && npm run build` after Frontend XP Refresh After Correct Quiz Submit | PASS | Frontend build passed after adding profile refresh callback and quiz-submit XP refresh messaging. | Yes |
| 2026-05-16 | `cd backend && .\mvnw.cmd test` after Backend Progress / Level Complete Foundation | PASS | Backend tests passed with 159 tests, 0 failures, 0 errors after adding V8 progress table, level completion endpoint, progress service, and focused level/progress tests. Initial manual PostgreSQL JSONB/varchar runtime bug was fixed before commit by removing unused `quizAnswersJson` entity mapping. | Yes |
| 2026-05-16 | `cd backend && .\mvnw.cmd test` after Backend Level Unlock Logic Foundation | PASS | Backend tests passed with 168 tests, 0 failures, 0 errors after adding level unlock enforcement, repository count queries, 403 locked-level handling, and focused level/progress tests. Initial controller fixture failures were fixed by seeding earlier levels in locked-level tests. | Yes |
| 2026-05-16 | `cd backend && .\mvnw.cmd test` after Backend Progress Fetch Endpoint Foundation | PASS | Backend tests passed with 177 tests, 0 failures, 0 errors after adding authenticated GET `/api/progress/courses/{courseId}`, safe course/level progress DTOs, user-scoped completed/unlocked calculation, progress percentage/course completion calculation, and focused progress service/controller tests. | Yes |
| 2026-05-16 | `cd frontend && npm run build` after Frontend Course Progress / Lock UI Foundation | PASS | Frontend build passed after adding `getCourseProgress(courseId)`, Course Map progress summary, completed/ready/locked badges, locked lesson disabling, and safe progress error handling. Vite build transformed 38 modules and completed successfully. | Yes |

| 2026-06-02 | `cd frontend && npm run build` after Frontend Complete Level Button / Progress Refresh Foundation | PASS | Frontend build passed after adding authenticated `completeLevel(levelId)` helper, per-level Complete Level UI, progress refresh, and profile XP refresh messaging. Vite build transformed 38 modules and completed successfully. | Yes |
| 2026-06-02 | `cd backend && .\mvnw.cmd test` after Backend XPService + Rank Recalculation Foundation | PASS | Backend tests passed with 191 tests, 0 failures, 0 errors after adding XPService, rank threshold tests, level-completion rank recalculation coverage, idempotency preservation coverage, and quiz-submit rank recalculation coverage. | Yes |
| 2026-06-02 | `cd backend && .\mvnw.cmd test` after Backend StreakService + Daily Login XP Guard | PASS | Backend tests passed with 199 tests, 0 failures, 0 errors, 0 skipped after adding StreakService, login daily XP guard, same-day/next-day/gap streak coverage, refresh/profile no-award guards, and updated login/profile/level expectations for daily login XP. | Yes |
| 2026-06-03 | `cd backend && .\mvnw.cmd test` after Backend Weak Concept Detection Foundation | PASS | Backend tests passed with 202 tests, 0 failures, 0 errors, 0 skipped after adding `weakConcepts` to safe quiz submit response, wrong/correct/blank concept service coverage, and controller JSON checks. | Yes |
| 2026-06-09 | `cd backend && .\mvnw.cmd test` after Backend Piston Run Code Foundation | PASS | Backend tests passed with 215 tests, 0 failures, 0 errors, 0 skipped after adding authenticated run-only Piston code execution endpoint, safe request/response DTOs, Piston client abstraction, safe unavailable mapping, validation coverage, and controller coverage. Tests mock Piston and do not call the real network service. | Yes |
| 2026-06-09 | `cd backend && .\mvnw.cmd test` after Backend Code Submit Foundation | PASS | Backend tests passed after adding authenticated code submit endpoint, V9 code_submissions migration/entity/repository, first-accepted XP logic, validation coverage, Piston-unavailable coverage, and controller safety coverage. Exact test count was not recorded in the terminal output shared for this Build Log update. | Yes |
| 2026-06-09 | `cd backend && .\mvnw.cmd test` after Backend Code Submissions History / Fetch Foundation | PASS | Backend tests passed with 241 tests after adding authenticated `GET /api/problems/{problemId}/submissions`, safe paginated history DTOs, user/problem-scoped repository query, newest-first ordering, pagination validation, ownership filtering, and controller safety coverage. | Yes |
| 2026-06-09 | `cd backend && .\mvnw.cmd test` after Backend Leaderboard REST Foundation | PASS | Backend tests passed with 293 tests, 0 failures, 0 errors after adding authenticated `GET /api/leaderboard`, safe leaderboard/currentUser DTOs, XP-desc deterministic sorting, global rank positions, pagination/period validation, read-only user repository queries, and leaderboard controller/service tests. | Yes |
| 2026-06-09 | `cd backend && .\mvnw.cmd test` after Backend Docker Setup Foundation | PASS | Backend tests passed according to Codex output after adding backend Dockerfile and backend `.dockerignore`; no Java source, tests, pom, migrations, or application.yml were changed. | Yes |
| 2026-06-09 | `docker build -t codequest-backend:local ./backend` after Backend Docker Setup Foundation | PASS | Initial Docker build failed with `env: $'bash\r': No such file or directory` because Windows CRLF line endings in `mvnw` broke the Linux builder shebang. Fixed by normalizing `mvnw` in Dockerfile using `sed -i 's/\r$//' mvnw && chmod +x mvnw`. Re-run Docker build completed successfully and image `codequest-backend:local` was created. | Yes |
| 2026-06-10 | `cd frontend && npm run build` after Frontend Leaderboard UI Foundation | PASS | Frontend build passed after adding authenticated leaderboard API helper and DashboardShell leaderboard UI. No package changes were made. | Yes |
| 2026-06-10 | `cd frontend && npm run build` after Frontend Code Runner UI Foundation | PASS | Frontend build passed after adding authenticated code runner API helper and DashboardShell Code Runner UI. No package changes were made. | Yes |
| 2026-06-10 | `cd frontend && npm run build` after Frontend Code Submit UI Foundation | PASS | Frontend build passed after adding authenticated code submit API helper and DashboardShell Submit Code flow inside the existing Code Runner section. No package changes were made. | Yes |


## Manual Verification Log
| Date | Feature | Manual/API check | Expected result | Status |
|---|---|---|---|---|
| 2026-06-10 | Frontend Code Runner UI Foundation | Browser dashboard Code Runner verification | Code Runner section renders, UUID-style problem id reaches backend, Java code can be entered, safe Piston-unavailable message displays, and no raw stack traces/raw Piston internals/tokens/passwords/userId/secrets are visible. | Passed |
| 2026-06-10 | Frontend Code Submit UI Foundation | Browser dashboard Submit Code verification | Submit Code appears beside Run Code, expected output is required before submit, blank code disables both run and submit, UUID-style problem id reaches backend after Submit Code, safe Piston-unavailable message displays, and no raw stack traces/raw Piston internals/tokens/passwords/userId/secrets are visible. | Passed |
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

| 2026-05-14 | Backend Quiz Submit/Scoring Foundation | Register/login -> fetch an existing AI course with quizQuestions -> pick quizId -> POST `/api/quizzes/{quizQuestionId}/submit` with valid selectedAnswer -> check response shape -> check no `correctAnswer` -> invalid selectedAnswer Z -> random valid quiz UUID -> no-token submit -> scope checks | Valid submit returned safe response with quizQuestionId, selectedAnswer, isCorrect, explanation, and concept; `correctAnswer` was not exposed; invalid answer returned 400; random quiz UUID returned 404; no-token request returned 401; frontend/migration/AI/course/flashcard/note diffs empty. | Passed |
| 2026-05-14 | Frontend Quiz Submit Integration | Start backend with DB/JWT/Gemini env vars to generate/reuse AI quiz data -> start frontend -> login -> open AI course with quizQuestions -> Open Course Map -> Open Lesson -> select option -> Submit Answer -> inspect result -> change selected answer -> resubmit -> verify navigation/notes/flashcards/security | Quiz questions and A/B/C/D options rendered; Submit Answer enabled only after selecting an option; submit showed a safe result card with Correct/Incorrect, selected answer, explanation, and concept; `correctAnswer` was not visible; changing the selected answer cleared the previous result; backend submit endpoint was called successfully; no tokens/passwords/secrets were visible. | Passed |
| 2026-05-15 | Backend Quiz Attempt Persistence Foundation | Start backend with PostgreSQL/JWT env vars -> register/login -> find existing AI course with quiz rows -> fetch quizId -> check attempt count -> submit valid answer A -> inspect safe response -> query `quiz_attempts` -> submit valid answer B again -> query attempts -> invalid answer Z -> random quiz UUID -> no-token submit -> scope checks | Valid submit inserted one row with selected_answer A and safe response without `correctAnswer`; repeated submit inserted a second row with selected_answer B without overwriting A; invalid answer returned 400 and count stayed 2; random quiz UUID returned 404; no-token request returned 401; frontend/AI/course/flashcard/note diffs were empty; DB migration change was only V7; no XP/progress/unlock behavior changed. | Passed |
| 2026-05-15 | Backend Quiz Attempt History/Fetch Foundation | Start backend with PostgreSQL/JWT env vars -> register/login user 1 -> submit A and B attempts -> GET `/api/quizzes/attempts` -> inspect newest-first ordering and safe response -> register/login user 2 -> GET empty history -> user 2 submit C -> GET user 2 history -> recheck user 1 history -> no-token GET -> scope checks | User 1 history returned B then A newest-first; response included safe attempt/context fields and did not contain `correctAnswer` or `userId`; user 2 initially received empty `attempts`; user 2 history after submit showed only C; user 1 history still showed only B and A; no-token request returned 401; frontend/migration/AI/course/flashcard/note diffs were empty; no XP/progress/unlock behavior changed. | Passed |

| 2026-05-15 | Backend XP Award Foundation for Quiz Submit | Start backend with PostgreSQL/JWT env vars -> register/login new user -> GET `/api/user/profile` starting XP 0 -> local SQL selected quiz `823a0793-a227-4474-9bde-a6c99f2ab832` with correct answer C and xpReward 10 -> submit wrong answer A -> profile XP unchanged -> submit correct answer C -> response safety check -> profile XP became 10 -> repeated correct submit -> invalid answer Z -> missing quiz UUID -> no-token submit -> scope checks | Wrong answer returned `isCorrect=false` and XP stayed 0; correct answer returned `isCorrect=true`, response did not contain `correctAnswer` or `userId`, and XP increased to 10; repeated correct submit awards XP again for MVP; invalid answer returns 400 with no XP change; missing quiz returns 404 with no XP change; no-token returns 401 with no XP change; frontend/migration/AI/course/flashcard/note/user diffs remain empty. | Passed |

| 2026-05-15 | Frontend XP Refresh After Correct Quiz Submit | Start backend with PostgreSQL/JWT env vars -> start frontend -> register/login new user -> confirm starting XP visible -> open AI course with quiz questions -> submit incorrect answer -> submit correct answer -> submit same correct answer again -> inspect quiz result/profile XP/safety/existing flows -> scope checks | Starting XP was visible; incorrect submit showed Incorrect and did not claim XP increased; correct submit showed Correct and refreshed profile/dashboard XP; repeated correct submit refreshed XP again according to backend MVP behavior; `correctAnswer`, `userId`, tokens, secrets, and raw backend errors were not visible; existing login/register, protected profile loading, dashboard, generate course, course map, lesson, quiz submit, flashcards, notes preload/save, quiz attempt history, and back buttons remained working; backend/package/authApi/courseApi diffs stayed empty. | Passed |
| 2026-05-16 | Backend Progress / Level Complete Foundation | PowerShell-only backend check: login existing progress test user -> GET `/api/courses/{courseId}` -> choose first level with `xpReward=100` -> GET `/api/user/profile` starting XP 0 -> POST `/api/levels/{levelId}/complete` -> GET profile -> repeat same POST -> GET profile -> no-token POST -> random valid UUID POST -> response safety JSON check | First completion returned `completed=true`, `alreadyCompleted=false`, `xpAwarded=100`, `totalXp=100`, and `completedAt`; profile XP became 100; repeat completion returned `completed=true`, `alreadyCompleted=true`, `xpAwarded=0`, and `totalXp=100`; profile XP stayed 100; no-token request returned 401; random valid level UUID returned 404; response JSON contained only `levelId`, `completed`, `alreadyCompleted`, `xpAwarded`, `totalXp`, and `completedAt`. Initial 500 caused by `quiz_answers_json` JSONB vs varchar mapping was fixed and the same manual flow passed after restart. | Passed |
| 2026-05-16 | Backend Level Unlock Logic Foundation | PowerShell-only backend check: start backend without Gemini env vars -> register/login fresh user -> generate placeholder course -> save level 1, level 2, and boss IDs -> check starting XP 0 -> attempt level 2 before level 1 -> attempt boss before previous levels -> complete level 1 -> attempt boss again before level 2 -> complete level 2 -> complete boss -> repeat level 1 -> final profile XP check -> no-token POST -> random valid UUID POST -> response safety JSON check | Locked level 2 returned 403 FORBIDDEN with standard ErrorDTO and safe message; locked boss returned 403 before previous levels; XP stayed 0 after locked attempts; level 1 completed with `xpAwarded=50` and `totalXp=50`; boss still returned 403 after only level 1; level 2 completed with `xpAwarded=75` and `totalXp=125`; boss completed with `xpAwarded=100` and `totalXp=225`; repeat level 1 returned `alreadyCompleted=true`, `xpAwarded=0`, and `totalXp=225`; final profile XP matched expected 225; no-token returned 401; random valid UUID returned 404; success responses contained only `levelId`, `completed`, `alreadyCompleted`, `xpAwarded`, `totalXp`, and `completedAt`. | Passed |
| 2026-05-16 | Backend Progress Fetch Endpoint Foundation | PowerShell-only backend check: start backend without Gemini env vars -> register/login fresh user -> generate placeholder course -> save level 1, level 2, and boss IDs -> GET `/api/progress/courses/{courseId}` before completion -> complete level 1 -> GET progress -> complete level 2 -> GET progress -> complete boss -> GET progress -> register/login second user -> GET same course progress -> random valid course UUID GET -> no-token GET -> response safety JSON check | Initial progress returned `completedLevels=0`, `totalLevels=3`, `progressPercent=0`, `courseCompleted=false`, level 1 unlocked, level 2 locked, and boss locked; after level 1 progress returned `completedLevels=1`, `progressPercent=33`, level 1 completed with `completedAt`, level 2 unlocked, and boss locked; after level 2 progress returned `completedLevels=2`, `progressPercent=66`, and boss unlocked; after boss progress returned `completedLevels=3`, `progressPercent=100`, `courseCompleted=true`, and all levels completed/unlocked; second user saw fresh 0/3 progress with no completedAt leakage; missing course returned 404; no-token returned 401; safety check found no `userId`, password fields, role, token fields, secrets, `correctAnswer`, or note content. | Passed |
| 2026-05-16 | Frontend Course Progress / Lock UI Foundation | Browser frontend check: start backend with PostgreSQL/JWT env vars and Gemini env vars removed -> start frontend with `npm run dev` -> open Vite URL -> register/login -> generate fresh placeholder course -> Open Course Map -> inspect progress summary and level states -> open level 1 lesson -> return to Course Map -> verify locked level 2 and boss cannot open -> inspect safety/browser console | Course Map loaded successfully; progress summary appeared for a fresh placeholder course; initial state showed 0/3 completed and 0%; level 1 displayed ready/unlocked; level 2 displayed locked; boss displayed locked; locked levels showed safe unlock explanation and disabled `Open Lesson`; level 1 opened existing Lesson view; lesson content, quiz panel, flashcards panel, notes area, and back-to-course-map flow remained working; locked level 2 and boss could not be opened; UI did not show accessToken, refreshToken, password, role, tokenHash, secrets, correctAnswer, raw backend stack trace, or raw JSON dump. | Passed |

| 2026-06-02 | Frontend Complete Level Button / Progress Refresh Foundation | Browser frontend check: start backend with PostgreSQL/JWT env vars and Gemini env vars removed -> start frontend with `npm run dev` -> open Vite URL -> register/login fresh user -> generate fresh placeholder course -> Open Course Map -> inspect initial progress and level states -> complete level 1 -> complete level 2 -> complete boss -> verify Lesson-view complete action and existing quiz/flashcards/notes/back flow -> inspect safety/browser console | Initial Course Map showed 0/3 completed, 0%, level 1 ready/unlocked, level 2 locked, and boss locked; `Complete Level` was visible/enabled only for unlocked incomplete levels; completing level 1 updated progress to 1/3 and 33%, marked level 1 completed, unlocked level 2, kept boss locked, and refreshed dashboard/profile XP by 50; completing level 2 updated progress to 2/3 and 66%, unlocked boss, and refreshed XP by 75 more; completing boss updated progress to 3/3 and 100%, showed course completed state, and refreshed XP by 100 more; locked levels could not be completed through normal UI; Lesson-view completion worked; Open Lesson, Back to Course Map, Quiz panel, Flashcards panel, Notes area, and note save/preload flow remained working; browser console had no red runtime errors; UI did not show accessToken, refreshToken, password, role, tokenHash, secrets, correctAnswer, raw backend stack trace, or raw JSON dump. | Passed |
| 2026-06-02 | Backend XPService + Rank Recalculation Foundation | PowerShell-only backend check: start backend with PostgreSQL/JWT env vars and Gemini env vars removed -> register/login fresh user -> check starting profile -> generate and complete three fresh placeholder courses -> verify profile XP/rank after each course -> repeat already-completed level -> compare before/after profile -> run profile safety JSON checks -> scope checks | Starting profile showed `xp=0` and `rank=BEGINNER`; after course 1 profile showed `XP=225, Rank=BEGINNER`; after course 2 profile showed `XP=450, Rank=BEGINNER`; after course 3 profile showed `XP=675, Rank=CODER`; repeat completion returned `alreadyCompleted=True` and `xpAwarded=0`; XP and rank stayed unchanged after repeat; safety checks returned false for password, passwordHash, password_hash, token, refreshToken, tokenHash, secret, and correctAnswer; frontend, DB migrations, backend/pom.xml, docs/Build Log, AI/Gemini, problem, and leaderboard diffs were empty. | Passed |
| 2026-06-02 | Backend StreakService + Daily Login XP Guard | PowerShell-only backend check: start backend with PostgreSQL/JWT env vars and Gemini env vars removed -> register fresh user -> first successful login -> profile fetch -> second same-day login -> repeated profile fetch -> refresh-token flow -> profile safety JSON checks -> optional DB check for last_login -> scope checks | Register stayed existing behavior with `xp=0`, `rank=BEGINNER`, and blank/null `streak`; first successful login awarded daily login XP and returned `xp=30`, `rank=BEGINNER`, `streak=1`; profile fetch after login stayed `xp=30`, `rank=BEGINNER`, `streak=1`; second same-day login did not award again and stayed `xp=30`, `rank=BEGINNER`, `streak=1`; repeated profile fetches did not award XP; refresh-token flow did not award XP; safety checks returned false for password, passwordHash, password_hash, token, refreshToken, tokenHash, secret, and correctAnswer; DB check showed `xp=30`, `rank=BEGINNER`, `streak=1`, and `last_login` not null; scope checks showed no frontend, DB migration, package, docs/Build Log, AI/Gemini, problem, leaderboard, or common security diff. | Passed |

| 2026-06-03 | Backend Weak Concept Detection Foundation | PowerShell-only backend check: start backend with PostgreSQL/JWT env vars -> register/login fresh user -> query local quizzes with nonblank `concept_tag` -> choose quiz `44821f81-730c-4b18-9b2b-fb6e70354366` with correct answer `B` and concept `Trie Definition` -> submit wrong answer `A` -> submit correct answer `B` -> run response safety JSON checks -> no-token submit -> scope checks | Wrong answer returned `quizQuestionId=44821f81-730c-4b18-9b2b-fb6e70354366`, `selectedAnswer=A`, `isCorrect=False`, `concept=Trie Definition`, and `weakConcepts={Trie Definition}`; correct answer returned `selectedAnswer=B`, `isCorrect=True`, `concept=Trie Definition`, and empty `weakConcepts`; safety checks returned false for `correctAnswer`, `userId`, password, passwordHash, password_hash, token, refreshToken, tokenHash, and secret; no-token request returned 401; scope checks showed only quiz service/DTO/tests changed. | Passed |
| 2026-06-09 | Backend Piston Run Code Foundation | PowerShell-only backend check: start backend with PostgreSQL/JWT env vars and optional `PISTON_BASE_URL` -> register/login fresh user -> call authenticated POST `/api/problems/{problemId}/run` with Java code and expected output -> handle external Piston availability/unavailability safely -> check invalid language -> check no-token request -> check profile XP -> run response/error safety checks -> scope checks | External Piston was unavailable during manual runtime, so the run-code request returned safe 503 `CODE_RUNNER_UNAVAILABLE` style handling instead of a raw stack trace or raw Piston body; this was accepted because mocked automated tests passed. Invalid language returned 400; no-token request returned 401; run-only did not increase XP beyond existing daily login XP behavior; safety checks confirmed no password fields, token fields, refresh token fields, tokenHash, secrets, `correctAnswer`, hidden tests, or `userId`; scope checks showed only problem package, safe Piston config, and common exception enum/handler changed. | Passed |
| 2026-06-09 | Backend Code Submit Foundation | PowerShell-only backend check: start backend with PostgreSQL/JWT env vars and optional `PISTON_BASE_URL` -> register/login fresh user -> call authenticated POST `/api/problems/{problemId}/submit` with Java code and expected output -> external Piston unavailable -> capture 503 body through `Invoke-WebRequest` -> verify DB count through full `psql.exe` path -> verify profile XP -> invalid language -> no-token submit -> inspect V9 migration content | External Piston was unavailable during manual runtime, so submit returned safe 503 ErrorDTO with `CODE_RUNNER_UNAVAILABLE` and safe message; DB count for the manual problemId stayed `0`; profile XP stayed at daily-login XP `30`; invalid language returned safe 400 with allowlist message; no-token submit returned 401; V9 migration safely creates `code_submissions` and indexes only; no raw Piston body, stack trace, tokens, passwords, userId, hidden tests, or correctAnswer were exposed. Happy-path accepted/repeat/failed persistence and XP behavior were covered by mocked backend tests. | Passed |
| 2026-06-09 | Backend Code Submissions History / Fetch Foundation | PowerShell-only backend check: start backend with PostgreSQL/JWT env vars -> register/login user 1 and user 2 -> insert four local manual `code_submissions` rows only for verification: two rows for user 1/problem, one row for user 2/same problem, and one row for user 1/other problem -> call authenticated GET `/api/problems/{problemId}/submissions?page=0&size=20` as both users -> check empty history -> check pagination -> check invalid page/size -> check no-token request -> run response safety string checks | User 1 response returned `totalItems=2`, `totalPages=1`, and exactly user 1's two rows for the requested problem sorted newest-first (`println(2)` before `println(1)`). User 2 response returned `totalItems=1` and only user 2's row. User 1's other-problem row was hidden. Empty history returned 200 with `totalItems=0`, `totalPages=0`, and empty `items`. Pagination with `size=1` returned page 0 newest row and page 1 older row with `totalPages=2`. Negative page, size 0, and size 51 returned safe 400 ErrorDTO responses. No-token request returned 401. Safety checks returned false for `userId`, password fields, token fields, refresh tokens, tokenHash, role, secrets, correctAnswer, hidden tests, expectedOutput, stdin, stackTrace, and Spring internals. | Passed |

| 2026-06-09 | Backend AI Code Review Foundation | PowerShell-only backend check: start backend with PostgreSQL/JWT/Gemini env vars -> register/login fresh user -> call authenticated `POST /api/ai/review-code` with Java binary-search code -> capture safe 503 body when Gemini unavailable -> invalid language `ruby` -> blank code -> no-token request -> backend tests -> scope checks | Authenticated review request reached backend and returned safe 503 `AI_SERVICE_UNAVAILABLE` with message `AI review service is currently unavailable. Please try again later.`, path `/api/ai/review-code`, and requestId; no raw Gemini response, raw prompt, stack trace, secrets, token, password, userId, or backend internals were exposed. Invalid language returned safe 400 `BAD_REQUEST`; blank code returned safe 400 `VALIDATION_ERROR`; no-token returned 401; backend tests passed with 272 tests; scope stayed limited to AI module source/tests plus minimal common exception mapping. | Passed || 2026-06-10 | Frontend Leaderboard UI Foundation | Dashboard browser check after login: load/refresh leaderboard, inspect current-user standing, inspect table, confirm existing dashboard sections still render | Leaderboard section visible, Refresh Leaderboard works, current-user standing shows position/XP/rank, table shows position/name/XP/rank/streak only, no sensitive fields/raw JSON/stack traces visible, and existing Generate Course / generated result / Quiz Attempt History sections still render | Passed |


## Backend Progress Fetch Endpoint Foundation Manual Test Commands
Use these after the backend progress fetch endpoint task `f408fd6 feat: add course progress fetch endpoint`.

This is a backend-only feature, so manual verification should be done from PowerShell/API checks, not browser UI.

### Automated verification
```powershell
cd backend
.\mvnw.cmd test
cd ..
```

Expected after this feature:
```text
Tests run: 177
Failures: 0
Errors: 0
BUILD SUCCESS
```

### Scope checks
```powershell
git diff -- frontend
git diff -- backend/src/main/java/com/codequest/ai
git diff -- backend/src/main/java/com/codequest/auth
git diff -- backend/src/main/java/com/codequest/quiz
git diff -- backend/src/main/java/com/codequest/flashcard
git diff -- backend/src/main/java/com/codequest/note
git diff -- backend/src/main/java/com/codequest/problem
git diff -- backend/src/main/java/com/codequest/leaderboard
git diff -- backend/src/main/resources/db/migration
git diff -- backend/src/main/java/com/codequest/progress
git diff -- backend/src/main/java/com/codequest/level
git diff -- backend/src/main/java/com/codequest/course
git diff -- backend/src/main/java/com/codequest/common/exception
```

Expected:
```text
Frontend diff is empty.
AI/auth/quiz/flashcard/note/problem/leaderboard diffs are empty.
Migration diff is empty.
Level, course, and common exception diffs are empty.
Progress diffs are expected before commit.
No package/dependency diffs.
```

### Backend env/run
Start backend with local PostgreSQL/JWT env vars. Remove Gemini env vars so placeholder course generation is predictable.

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
Flyway validates V1 through V8 successfully.
Tomcat started on port 8080.
Started CodeQuestApplication.
```

### Manual PowerShell verification
From another PowerShell terminal:

```powershell
cd C:\Users\hp\Desktop\CodeQuestFinalProject
$baseUrl = "http://localhost:8080"
```

Register and login a fresh first user:
```powershell
$email1 = "progress_fetch_user1_" + (Get-Date -Format "yyyyMMddHHmmss") + "@example.com"

$registerBody1 = @{
  name = "Progress Fetch User One"
  email = $email1
  password = "StrongPass123"
} | ConvertTo-Json

$registerResponse1 = Invoke-RestMethod `
  -Uri "$baseUrl/api/auth/register" `
  -Method Post `
  -ContentType "application/json" `
  -Body $registerBody1

$registerResponse1

$loginBody1 = @{
  email = $email1
  password = "StrongPass123"
} | ConvertTo-Json

$loginResponse1 = Invoke-RestMethod `
  -Uri "$baseUrl/api/auth/login" `
  -Method Post `
  -ContentType "application/json" `
  -Body $loginBody1

$accessToken1 = $loginResponse1.accessToken
$headers1 = @{ Authorization = "Bearer $accessToken1" }

$loginResponse1 | Select-Object userId, name, email, rank, xp, streak, tokenType, expiresInSeconds
```

Generate a predictable placeholder course:
```powershell
$topic = "Progress Fetch Manual Test " + (Get-Date -Format "yyyyMMddHHmmss")

$courseBody = @{
  topic = $topic
  difficulty = "BEGINNER"
  goal = "Test progress fetch endpoint"
} | ConvertTo-Json

$courseResponse = Invoke-RestMethod `
  -Uri "$baseUrl/api/courses/generate" `
  -Method Post `
  -Headers $headers1 `
  -ContentType "application/json" `
  -Body $courseBody

$courseId = $courseResponse.courseId
$courseResponse | Select-Object courseId, title, sourceType, cacheHit
```

Fetch course details and save level IDs:
```powershell
$courseDetails = Invoke-RestMethod `
  -Uri "$baseUrl/api/courses/$courseId" `
  -Method Get `
  -Headers $headers1

$courseDetails.levels | Select-Object levelId, orderNumber, title, xpReward, isBoss

$level1 = $courseDetails.levels | Where-Object { $_.orderNumber -eq 1 }
$level2 = $courseDetails.levels | Where-Object { $_.orderNumber -eq 2 }
$boss = $courseDetails.levels | Where-Object { $_.isBoss -eq $true } | Select-Object -First 1

$level1Id = $level1.levelId
$level2Id = $level2.levelId
$bossId = $boss.levelId
```

Initial progress check:
```powershell
$progressInitial = Invoke-RestMethod `
  -Uri "$baseUrl/api/progress/courses/$courseId" `
  -Method Get `
  -Headers $headers1

$progressInitial
$progressInitial.levels | Select-Object orderNumber, title, completed, unlocked, completedAt

$progressInitial.completedLevels
$progressInitial.totalLevels
$progressInitial.progressPercent
$progressInitial.courseCompleted
$progressInitial.levels[0].unlocked
$progressInitial.levels[1].unlocked
$progressInitial.levels[2].unlocked
```

Expected:
```text
0
3
0
False
True
False
False
```

Complete level 1 and recheck progress:
```powershell
$completeLevel1 = Invoke-RestMethod `
  -Uri "$baseUrl/api/levels/$level1Id/complete" `
  -Method Post `
  -Headers $headers1

$progressAfterLevel1 = Invoke-RestMethod `
  -Uri "$baseUrl/api/progress/courses/$courseId" `
  -Method Get `
  -Headers $headers1

$progressAfterLevel1
$progressAfterLevel1.levels | Select-Object orderNumber, completed, unlocked, completedAt
```

Expected:
```text
completedLevels=1
progressPercent=33
level 1 completed=true and completedAt present
level 2 unlocked=true
boss unlocked=false
```

Complete level 2 and recheck progress:
```powershell
$completeLevel2 = Invoke-RestMethod `
  -Uri "$baseUrl/api/levels/$level2Id/complete" `
  -Method Post `
  -Headers $headers1

$progressAfterLevel2 = Invoke-RestMethod `
  -Uri "$baseUrl/api/progress/courses/$courseId" `
  -Method Get `
  -Headers $headers1

$progressAfterLevel2
$progressAfterLevel2.levels | Select-Object orderNumber, completed, unlocked, completedAt
```

Expected:
```text
completedLevels=2
progressPercent=66
boss unlocked=true
```

Complete boss and recheck progress:
```powershell
$completeBoss = Invoke-RestMethod `
  -Uri "$baseUrl/api/levels/$bossId/complete" `
  -Method Post `
  -Headers $headers1

$progressAfterBoss = Invoke-RestMethod `
  -Uri "$baseUrl/api/progress/courses/$courseId" `
  -Method Get `
  -Headers $headers1

$progressAfterBoss
$progressAfterBoss.levels | Select-Object orderNumber, completed, unlocked, completedAt
```

Expected:
```text
completedLevels=3
totalLevels=3
progressPercent=100
courseCompleted=true
all levels completed=true
all levels unlocked=true
```

Second user isolation check:
```powershell
$email2 = "progress_fetch_user2_" + (Get-Date -Format "yyyyMMddHHmmss") + "@example.com"

$registerBody2 = @{
  name = "Progress Fetch User Two"
  email = $email2
  password = "StrongPass123"
} | ConvertTo-Json

Invoke-RestMethod `
  -Uri "$baseUrl/api/auth/register" `
  -Method Post `
  -ContentType "application/json" `
  -Body $registerBody2

$loginBody2 = @{
  email = $email2
  password = "StrongPass123"
} | ConvertTo-Json

$loginResponse2 = Invoke-RestMethod `
  -Uri "$baseUrl/api/auth/login" `
  -Method Post `
  -ContentType "application/json" `
  -Body $loginBody2

$accessToken2 = $loginResponse2.accessToken
$headers2 = @{ Authorization = "Bearer $accessToken2" }

$progressUser2 = Invoke-RestMethod `
  -Uri "$baseUrl/api/progress/courses/$courseId" `
  -Method Get `
  -Headers $headers2

$progressUser2
$progressUser2.levels | Select-Object orderNumber, completed, unlocked, completedAt
```

Expected:
```text
completedLevels=0
progressPercent=0
level 1 unlocked=true
level 2 unlocked=false
boss unlocked=false
completedAt values blank/null
```

Missing course and no-token checks:
```powershell
$randomCourseId = [guid]::NewGuid().ToString()

try {
  Invoke-RestMethod `
    -Uri "$baseUrl/api/progress/courses/$randomCourseId" `
    -Method Get `
    -Headers $headers1
} catch {
  "STATUS: " + $_.Exception.Response.StatusCode.value__
  $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
  "BODY:"
  $reader.ReadToEnd()
}

try {
  Invoke-RestMethod `
    -Uri "$baseUrl/api/progress/courses/$courseId" `
    -Method Get
} catch {
  $_.Exception.Response.StatusCode.value__
}
```

Expected:
```text
Missing course: 404 standard ErrorDTO
No-token request: 401
```

Safety check:
```powershell
$progressAfterBossJson = $progressAfterBoss | ConvertTo-Json -Depth 10
$progressAfterBossJson.Contains("userId")
$progressAfterBossJson.Contains("password")
$progressAfterBossJson.Contains("passwordHash")
$progressAfterBossJson.Contains("password_hash")
$progressAfterBossJson.Contains("role")
$progressAfterBossJson.Contains("token")
$progressAfterBossJson.Contains("accessToken")
$progressAfterBossJson.Contains("refreshToken")
$progressAfterBossJson.Contains("tokenHash")
$progressAfterBossJson.Contains("secret")
$progressAfterBossJson.Contains("correctAnswer")
$progressAfterBossJson.Contains("content")
```

Expected:
```text
All checks return False.
```

Important boundaries:
- Endpoint is GET `/api/progress/courses/{courseId}`.
- Endpoint requires JWT Bearer token.
- Endpoint accepts no request body.
- Current user comes only from `CurrentUserPrincipal`.
- Request must not accept userId from body, query params, headers, or path.
- Response is safe and does not expose userId, passwords, roles, tokens, refresh tokens, token hashes, secrets, correct answers, note content, or raw entities.
- Frontend UI integration is not implemented in this backend foundation.
- No DB migration was added.
- At the backend progress fetch stage, rank, streak, weak concept detection, frontend progress display, frontend lock UI, frontend complete button, leaderboard, achievements, Piston/code execution, deployment, and Phase 2 were still unimplemented. Frontend progress display and lock UI were later implemented in `5deeddd feat: show course progress lock states`.


## Frontend Course Progress / Lock UI Foundation Manual Test Commands
Use these after the frontend course progress lock UI task `5deeddd feat: show course progress lock states`.

This feature is frontend-only, so browser verification is required before commit. Backend tests are not required if backend files stayed unchanged, but the backend must be running for the browser smoke test.

### Terminal 1 - Start backend

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

Expected backend startup:

```text
Tomcat started on port 8080
Started CodeQuestApplication
```

### Terminal 2 - Start frontend

```powershell
cd C:\Users\hp\Desktop\CodeQuestFinalProject\frontend
npm run dev
```

Expected Vite startup:

```text
Local: http://localhost:5173/
```

### Browser checks

1. Open the Vite URL shown by `npm run dev`.
2. Register or login.
3. Generate a fresh placeholder course with a unique topic, for example:

```text
Topic: Frontend Progress Lock UI Manual Test
Difficulty: BEGINNER
Goal: Test course progress UI
```

4. Click `Open Course Map`.
5. Confirm Course Map loads course details and progress summary.
6. Expected fresh placeholder course progress:

```text
0 / 3 completed
0%
Level 1: Ready / Unlocked
Level 2: Locked
Boss: Locked
```

7. Confirm locked levels show this safe explanation:

```text
Complete previous levels to unlock this level.
```

8. Confirm locked level 2 and locked boss have disabled `Open Lesson` actions.
9. Click `Open Lesson` on level 1.
10. Confirm the existing Lesson view still works:

```text
Lesson content visible
Quiz panel visible
Flashcards panel visible
Notes area visible
Back to Course Map works
```

11. Return to Course Map.
12. Confirm locked level 2 cannot open Lesson view.
13. Confirm locked boss cannot open Lesson view.
14. Confirm browser console has no red runtime errors.
15. Confirm UI does not display:

```text
accessToken
refreshToken
password
role
tokenHash
secret
correctAnswer
raw backend stack trace
raw JSON dump
```

Important boundaries:
- Frontend only.
- Changed files should be limited to `frontend/src/pages/DashboardShell.jsx` and `frontend/src/services/courseApi.js`.
- No backend files should change.
- No DB migration should be added.
- No package/dependency files should change.
- No complete-level button is implemented in this feature.
- No POST `/api/levels/{levelId}/complete` call is made by the frontend in this feature.
- Rank, streak, weak concept detection, leaderboard, Piston/code execution, deployment, and Phase 2 remain unimplemented.

## Frontend Complete Level Button / Progress Refresh Foundation Manual Test Commands
Use these after the frontend level completion task `0543a9e feat: add frontend level completion flow`.

This feature is frontend-only. Browser verification is required before commit. Backend tests are not required if backend files stayed unchanged, but the backend must be running for the browser smoke test.

### Automated verification

```powershell
cd frontend
npm run build
cd ..
```

Expected:
```text
Vite production build succeeds.
38 modules transformed.
BUILD/output generated in frontend/dist.
```

### Scope checks

```powershell
git status --short
git diff -- backend
git diff -- backend/src/main/resources/db/migration
git diff -- frontend/package.json
git diff -- frontend/package-lock.json
git diff -- docs
git diff -- CodeQuest_Build_Log.md
```

Expected before commit:
```text
Only these files are modified:
 M frontend/src/pages/DashboardShell.jsx
 M frontend/src/services/courseApi.js

All diff checks for backend, migrations, package files, docs, and Build Log are empty.
```

### Terminal 1 - Start backend

Start backend with local PostgreSQL/JWT env vars. Remove Gemini env vars so placeholder course generation is predictable.

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

Expected backend startup:
```text
Tomcat started on port 8080
Started CodeQuestApplication
```

### Terminal 2 - Start frontend

```powershell
cd C:\Users\hp\Desktop\CodeQuestFinalProject\frontend
npm run dev
```

Expected Vite startup:
```text
Local: http://localhost:5173/
```

### Browser checks

1. Open the Vite URL shown by `npm run dev`.
2. Register or login with a fresh user.
3. Generate a fresh placeholder course with a unique topic, for example:

```text
Topic: Frontend Complete Level Manual Test <current timestamp>
Difficulty: BEGINNER
Goal: Test complete level UI
```

4. Click `Open Course Map`.
5. Confirm initial Course Map state:

```text
0 / 3 completed
0%
Level 1: Ready / Unlocked
Level 2: Locked
Boss: Locked
Complete Level visible/enabled only for Level 1
Complete Level not enabled for Level 2 or Boss
```

6. Click `Complete Level` for level 1.
7. Expected after level 1 completion:

```text
Level 1 becomes Completed.
Level 1 normal Complete Level action disappears or is disabled as completed.
Progress becomes 1 / 3.
Progress percent becomes 33%.
Level 2 becomes Ready / Unlocked.
Boss remains Locked.
Dashboard/profile XP increases by 50 for placeholder level 1.
A safe success message appears.
```

8. Complete level 2.
9. Expected after level 2 completion:

```text
Level 2 becomes Completed.
Progress becomes 2 / 3.
Progress percent becomes 66%.
Boss becomes Ready / Unlocked.
Dashboard/profile XP increases by 75 more.
A safe success message appears.
```

10. Complete boss.
11. Expected after boss completion:

```text
Boss becomes Completed.
Progress becomes 3 / 3.
Progress percent becomes 100%.
Course completed state/badge appears.
Dashboard/profile XP increases by 100 more.
A safe success message appears.
```

12. Confirm locked levels cannot be completed before they unlock.
13. Open a lesson and verify the Lesson-view `Complete Level` action works for an unlocked incomplete lesson.
14. Confirm existing flows still work:

```text
Open Lesson works.
Back to Course Map works.
Quiz panel is visible.
Flashcards panel is visible.
Notes area is visible.
Note save/preload still works if tested.
```

15. Confirm browser console has no red runtime errors.
16. Confirm UI does not display:

```text
accessToken
refreshToken
password
role
tokenHash
secret
correctAnswer
raw backend stack trace
raw JSON dump
```

Important boundaries:
- Frontend only.
- Changed files should be limited to `frontend/src/pages/DashboardShell.jsx` and `frontend/src/services/courseApi.js`.
- No backend files should change.
- No DB migration should be added.
- No package/dependency files should change.
- No React Router should be added.
- No localStorage/sessionStorage progress persistence should be added.
- Complete Level calls existing backend endpoint POST `/api/levels/{levelId}/complete`.
- Complete Level should not send userId or ownership fields.
- Progress refresh should use GET `/api/progress/courses/{courseId}` after completion.
- Profile XP refresh should use the existing shared profile refresh callback.
- Rank, streak, weak concept detection, leaderboard, Piston/code execution, deployment, and Phase 2 remain unimplemented.


## Backend XPService + Rank Recalculation Foundation Manual Test Commands
Use these after the backend XP/rank task `6aba27a feat: add xp rank recalculation foundation`.

This is a backend-only feature, so manual verification should be done from PowerShell/API checks, not browser UI.

### Automated verification

```powershell
cd backend
.\mvnw.cmd test
cd ..
```

Expected after this feature:
```text
Tests run: 191
Failures: 0
Errors: 0
BUILD SUCCESS
```

### Scope checks

```powershell
git status --short
git diff -- frontend
git diff -- backend/src/main/resources/db/migration
git diff -- backend/pom.xml
git diff -- docs
git diff -- CodeQuest_Build_Log.md
git diff -- backend/src/main/java/com/codequest/ai
git diff -- backend/src/main/java/com/codequest/problem
git diff -- backend/src/main/java/com/codequest/leaderboard
```

Expected before commit:
```text
Only these files are modified:
 M backend/src/main/java/com/codequest/progress/ProgressService.java
 M backend/src/main/java/com/codequest/quiz/QuizService.java
 M backend/src/test/java/com/codequest/progress/ProgressServiceTest.java
 M backend/src/test/java/com/codequest/quiz/QuizServiceTest.java
?? backend/src/main/java/com/codequest/progress/XPService.java
?? backend/src/test/java/com/codequest/progress/XPServiceTest.java

Frontend diff empty.
DB migration diff empty.
backend/pom.xml diff empty.
docs diff empty.
Build Log diff empty.
AI/problem/leaderboard diffs empty.
```

### Terminal 1 - Start backend

Start backend with local PostgreSQL/JWT env vars. Remove Gemini env vars so placeholder course generation is predictable.

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

Expected backend startup:
```text
Tomcat started on port 8080
Started CodeQuestApplication
```

### Terminal 2 - Manual PowerShell API verification

```powershell
cd C:\Users\hp\Desktop\CodeQuestFinalProject
$baseUrl = "http://localhost:8080"
```

Register and login a fresh user:
```powershell
$email = "rank_manual_" + (Get-Date -Format "yyyyMMddHHmmss") + "@example.com"

$registerBody = @{
  name = "Rank Manual User"
  email = $email
  password = "StrongPass123"
} | ConvertTo-Json

Invoke-RestMethod `
  -Uri "$baseUrl/api/auth/register" `
  -Method Post `
  -ContentType "application/json" `
  -Body $registerBody

$loginBody = @{
  email = $email
  password = "StrongPass123"
} | ConvertTo-Json

$loginResponse = Invoke-RestMethod `
  -Uri "$baseUrl/api/auth/login" `
  -Method Post `
  -ContentType "application/json" `
  -Body $loginBody

$token = $loginResponse.accessToken
$headers = @{ Authorization = "Bearer $token" }
```

Check starting profile:
```powershell
$profileStart = Invoke-RestMethod `
  -Uri "$baseUrl/api/user/profile" `
  -Method Get `
  -Headers $headers

$profileStart | Select-Object xp, rank
```

Expected:
```text
xp = 0
rank = BEGINNER
```

Generate and complete three fresh placeholder courses to cross 500 XP:
```powershell
for ($i = 1; $i -le 3; $i++) {
  $topic = "Rank Manual Course " + (Get-Date -Format "yyyyMMddHHmmss") + " " + $i

  $courseBody = @{
    topic = $topic
    difficulty = "BEGINNER"
    goal = "Rank recalculation manual test"
  } | ConvertTo-Json

  $courseResponse = Invoke-RestMethod `
    -Uri "$baseUrl/api/courses/generate" `
    -Method Post `
    -Headers $headers `
    -ContentType "application/json" `
    -Body $courseBody

  $courseId = $courseResponse.courseId

  $courseDetails = Invoke-RestMethod `
    -Uri "$baseUrl/api/courses/$courseId" `
    -Method Get `
    -Headers $headers

  $orderedLevels = $courseDetails.levels | Sort-Object orderNumber

  foreach ($level in $orderedLevels) {
    Invoke-RestMethod `
      -Uri "$baseUrl/api/levels/$($level.levelId)/complete" `
      -Method Post `
      -Headers $headers
  }

  $profileNow = Invoke-RestMethod `
    -Uri "$baseUrl/api/user/profile" `
    -Method Get `
    -Headers $headers

  "After course $i => XP=$($profileNow.xp), Rank=$($profileNow.rank)"
}
```

Expected:
```text
After course 1 => XP=225, Rank=BEGINNER
After course 2 => XP=450, Rank=BEGINNER
After course 3 => XP=675, Rank=CODER
```

Repeat-completion idempotency check:
```powershell
$repeatLevel = $orderedLevels[0]

$beforeRepeatProfile = Invoke-RestMethod `
  -Uri "$baseUrl/api/user/profile" `
  -Method Get `
  -Headers $headers

$repeatResponse = Invoke-RestMethod `
  -Uri "$baseUrl/api/levels/$($repeatLevel.levelId)/complete" `
  -Method Post `
  -Headers $headers

$afterRepeatProfile = Invoke-RestMethod `
  -Uri "$baseUrl/api/user/profile" `
  -Method Get `
  -Headers $headers

$repeatResponse
$beforeRepeatProfile | Select-Object xp, rank
$afterRepeatProfile | Select-Object xp, rank
```

Expected:
```text
repeatResponse.alreadyCompleted = True
repeatResponse.xpAwarded = 0
XP unchanged
Rank unchanged
```

Safety check:
```powershell
$profileJson = $afterRepeatProfile | ConvertTo-Json -Depth 10
$profileJson.Contains("password")
$profileJson.Contains("passwordHash")
$profileJson.Contains("password_hash")
$profileJson.Contains("token")
$profileJson.Contains("refreshToken")
$profileJson.Contains("tokenHash")
$profileJson.Contains("secret")
$profileJson.Contains("correctAnswer")
```

Expected:
```text
False
False
False
False
False
False
False
False
```

Important boundaries:
- XPService is backend-only.
- Existing XP amounts are preserved.
- Level completion uses `level.xpReward`.
- Quiz submit uses existing quiz XP behavior.
- Rank thresholds are BEGINNER 0, CODER 500, DEVELOPER 2000, ENGINEER 5000, ARCHITECT 12000, LEGEND 25000.
- Repeated level completion remains idempotent and awards `xpAwarded=0`.
- Repeated correct quiz submit still follows existing MVP behavior; anti-farming/deduplication is not implemented.
- No new endpoint was added.
- No DB migration was added.
- No frontend changes were made.
- Streak, weak concept detection, leaderboard, Piston/code execution, deployment, and Phase 2 remain unimplemented.


## Backend StreakService + Daily Login XP Guard Manual Test Commands
Use these after the backend streak task `7641b3f feat: add login streak daily xp guard`.

This is a backend-only feature, so manual verification should be done from PowerShell/API checks, not browser UI.

### Automated verification

```powershell
cd backend
.\mvnw.cmd test
cd ..
```

Expected after this feature:
```text
Tests run: 199
Failures: 0
Errors: 0
Skipped: 0
BUILD SUCCESS
```

### Scope checks

```powershell
git status --short
git diff -- frontend
git diff -- backend/src/main/resources/db/migration
git diff -- backend/pom.xml
git diff -- docs
git diff -- CodeQuest_Build_Log.md
git diff -- backend/src/main/java/com/codequest/ai
git diff -- backend/src/main/java/com/codequest/problem
git diff -- backend/src/main/java/com/codequest/leaderboard
git diff -- backend/src/main/java/com/codequest/common/security
```

Expected before commit:
```text
Only these files are modified:
 M backend/src/main/java/com/codequest/auth/AuthService.java
 M backend/src/test/java/com/codequest/auth/AuthControllerTest.java
 M backend/src/test/java/com/codequest/auth/AuthServiceTest.java
 M backend/src/test/java/com/codequest/level/LevelControllerTest.java
 M backend/src/test/java/com/codequest/user/UserControllerTest.java
 M backend/src/test/java/com/codequest/user/UserServiceTest.java
?? backend/src/main/java/com/codequest/progress/StreakService.java
?? backend/src/test/java/com/codequest/progress/StreakServiceTest.java

Frontend diff empty.
DB migration diff empty.
backend/pom.xml diff empty.
docs diff empty.
Build Log diff empty.
AI/problem/leaderboard/common security diffs empty.
```

### Terminal 1 - Start backend

Start backend with local PostgreSQL/JWT env vars. Remove Gemini env vars so placeholder course generation is predictable.

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

Expected backend startup:
```text
Tomcat started on port 8080
Started CodeQuestApplication
```

### Terminal 2 - Manual PowerShell API verification

```powershell
cd C:\Users\hp\Desktop\CodeQuestFinalProject
$baseUrl = "http://localhost:8080"
```

Register a fresh user:
```powershell
$email = "streak_manual_" + (Get-Date -Format "yyyyMMddHHmmss") + "@example.com"

$registerBody = @{
  name = "Streak Manual User"
  email = $email
  password = "StrongPass123"
} | ConvertTo-Json

$registerResponse = Invoke-RestMethod `
  -Uri "$baseUrl/api/auth/register" `
  -Method Post `
  -ContentType "application/json" `
  -Body $registerBody

$registerResponse | Select-Object xp, rank, streak
```

Expected:
```text
xp = 0
rank = BEGINNER
streak may be 0 or blank/null depending on existing register response mapping
```

First login should award daily login XP:
```powershell
$loginBody = @{
  email = $email
  password = "StrongPass123"
} | ConvertTo-Json

$loginResponse1 = Invoke-RestMethod `
  -Uri "$baseUrl/api/auth/login" `
  -Method Post `
  -ContentType "application/json" `
  -Body $loginBody

$token1 = $loginResponse1.accessToken
$headers1 = @{ Authorization = "Bearer $token1" }

$loginResponse1 | Select-Object xp, rank, streak
```

Expected:
```text
xp = 30
rank = BEGINNER
streak = 1
```

Profile after first login:
```powershell
$profileAfterLogin1 = Invoke-RestMethod `
  -Uri "$baseUrl/api/user/profile" `
  -Method Get `
  -Headers $headers1

$profileAfterLogin1 | Select-Object xp, rank, streak
```

Expected:
```text
xp = 30
rank = BEGINNER
streak = 1
```

Second same-day login should not award daily XP again:
```powershell
$loginResponse2 = Invoke-RestMethod `
  -Uri "$baseUrl/api/auth/login" `
  -Method Post `
  -ContentType "application/json" `
  -Body $loginBody

$token2 = $loginResponse2.accessToken
$headers2 = @{ Authorization = "Bearer $token2" }

$loginResponse2 | Select-Object xp, rank, streak
```

Expected:
```text
xp = 30
rank = BEGINNER
streak = 1
```

Profile fetch should not award XP:
```powershell
$profileBeforeRepeatedFetch = Invoke-RestMethod `
  -Uri "$baseUrl/api/user/profile" `
  -Method Get `
  -Headers $headers2

$profileAfterRepeatedFetch = Invoke-RestMethod `
  -Uri "$baseUrl/api/user/profile" `
  -Method Get `
  -Headers $headers2

$profileBeforeRepeatedFetch | Select-Object xp, rank, streak
$profileAfterRepeatedFetch | Select-Object xp, rank, streak
```

Expected both times:
```text
xp = 30
rank = BEGINNER
streak = 1
```

Refresh token flow should not award XP:
```powershell
$refreshBody = @{
  refreshToken = $loginResponse2.refreshToken
} | ConvertTo-Json

$refreshResponse = Invoke-RestMethod `
  -Uri "$baseUrl/api/auth/refresh" `
  -Method Post `
  -ContentType "application/json" `
  -Body $refreshBody

$profileAfterRefresh = Invoke-RestMethod `
  -Uri "$baseUrl/api/user/profile" `
  -Method Get `
  -Headers $headers2

$profileAfterRefresh | Select-Object xp, rank, streak
```

Expected:
```text
xp = 30
rank = BEGINNER
streak = 1
```

Safety check:
```powershell
$profileJson = $profileAfterRefresh | ConvertTo-Json -Depth 10
$profileJson.Contains("password")
$profileJson.Contains("passwordHash")
$profileJson.Contains("password_hash")
$profileJson.Contains("token")
$profileJson.Contains("refreshToken")
$profileJson.Contains("tokenHash")
$profileJson.Contains("secret")
$profileJson.Contains("correctAnswer")
```

Expected:
```text
False
False
False
False
False
False
False
False
```

Optional DB check for last_login:
```powershell
$env:Path += ";C:\Program Files\PostgreSQL\17\bin"
psql -U postgres -W -d codequest -c "select email, xp, rank, streak, last_login from users where email='$email';"
```

Expected:
```text
email matches fresh user
xp = 30
rank = BEGINNER
streak = 1
last_login is not null
```

Important boundaries:
- Daily login XP is +30.
- Daily login XP is awarded only once per user per calendar day.
- Daily login XP is awarded only by successful explicit login.
- Daily login XP is not awarded by registration, profile fetch, refresh-token flow, logout, or JWT validation.
- StreakService uses server-side time through Clock, not frontend/client time.
- Daily login XP uses XPService, so rank recalculation remains centralized.
- Registration behavior remains unchanged; first login initializes streak.
- No new endpoint was added.
- No DB migration was added.
- No frontend changes were made.
- Refresh token, logout, JWT claims, rank thresholds, quiz XP, and level-completion XP were not changed.
- Weak concept detection, leaderboard, Piston/code execution, deployment, and Phase 2 remain unimplemented.

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
   - For backend-only endpoints, prefer PowerShell API checks with exact commands and expected outputs.
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

Expected Flyway behavior after Backend Progress / Level Complete Foundation:
```text
Successfully validated 8 migrations
Schema "public" is up to date. No migration necessary.
```

If V8 has not yet been applied to a local database, startup should apply `V8__create_progress_table.sql` successfully.

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
- At that feature stage, notes saving, quiz submit, scoring, answer persistence, XP/progress, weak concept detection, and level unlock logic were still not implemented; later features implemented notes, quiz submit/scoring, attempt persistence/history, quiz XP award/refresh, backend progress foundation, and backend level unlock enforcement.

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


## Backend Quiz Submit/Scoring Foundation Manual Test Commands
Use these after the backend quiz submit/scoring foundation task `a8a0f79 feat: add quiz submit scoring endpoint`.

### Automated verification
```powershell
cd backend
.\mvnw.cmd test
cd ..
```

Expected after this feature:
```text
Tests run: 131
Failures: 0
Errors: 0
BUILD SUCCESS
```

### Scope checks
```powershell
git status --short
git diff -- frontend
git diff -- backend/src/main/resources/db/migration
git diff -- backend/src/main/java/com/codequest/ai
git diff -- backend/src/main/java/com/codequest/course
git diff -- backend/src/main/java/com/codequest/flashcard
git diff -- backend/src/main/java/com/codequest/note
git diff -- backend/src/main/java/com/codequest/quiz
```

Expected before commit:
```text
Frontend diff is empty.
DB migration diff is empty.
AI diff is empty.
Backend course diff is empty.
Backend flashcard diff is empty.
Backend note diff is empty.
Backend quiz diff contains only the new quiz submit/scoring files and tests.
```

### Backend env/run
Start backend with local PostgreSQL/JWT env vars. Gemini key is not required if an existing course with persisted quiz questions is already available locally.
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

### Manual quiz submit verification
From another PowerShell, register/login and fetch a course that already has quiz rows:
```powershell
$baseUrl = "http://localhost:8080"

$email = "quizsubmitmanual$(Get-Random)@example.com"
$password = "QuizSubmit123"

$registerBody = @{
  name = "Quiz Submit Manual"
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
```

Find a local course with quiz rows:
```powershell
$env:Path += ";C:\Program Files\PostgreSQL\17\bin"

psql -U postgres -W -d codequest -c "select c.id, c.title, c.source_type, count(q.id) as quiz_count from courses c join levels l on l.course_id = c.id left join quizzes q on q.level_id = l.id group by c.id, c.title, c.source_type having count(q.id) > 0 order by c.created_at desc limit 5;"
```

Set one course id from that output:
```powershell
$courseId = "<course-id-with-quiz-count>"
```

Fetch the course and pick a quiz id. Current GET course response uses `quizId` for quiz question identity:
```powershell
$fetchedCourse = Invoke-RestMethod `
  -Uri "$baseUrl/api/courses/$courseId" `
  -Method GET `
  -Headers @{ Authorization = "Bearer $token" }

$quizQuestion = $fetchedCourse.levels |
  ForEach-Object { $_.quizQuestions } |
  Where-Object { $_ } |
  Select-Object -First 1

$quizQuestion | ConvertTo-Json -Depth 10

$quizQuestionId = $quizQuestion.quizId
$quizQuestionId
```

Expected:
```text
A valid UUID is printed for $quizQuestionId.
```

Submit a valid answer:
```powershell
$submitBody = @{
  selectedAnswer = "A"
} | ConvertTo-Json

$submitResponse = Invoke-RestMethod `
  -Uri "$baseUrl/api/quizzes/$quizQuestionId/submit" `
  -Method POST `
  -ContentType "application/json" `
  -Headers @{ Authorization = "Bearer $token" } `
  -Body $submitBody

$submitResponse
$json = $submitResponse | ConvertTo-Json -Depth 10
$json
$json.Contains("correctAnswer")
```

Expected:
```text
Response includes quizQuestionId, selectedAnswer, isCorrect, explanation, and concept.
$json.Contains("correctAnswer") returns False.
```

Invalid answer check:
```powershell
$invalidBody = @{
  selectedAnswer = "Z"
} | ConvertTo-Json

try {
  Invoke-RestMethod `
    -Uri "$baseUrl/api/quizzes/$quizQuestionId/submit" `
    -Method POST `
    -ContentType "application/json" `
    -Headers @{ Authorization = "Bearer $token" } `
    -Body $invalidBody
} catch {
  $_.Exception.Response.StatusCode.value__
}
```

Expected:
```text
400
```

Missing quiz question check:
```powershell
$missingQuizQuestionId = [guid]::NewGuid().ToString()

$validBody = @{
  selectedAnswer = "A"
} | ConvertTo-Json

try {
  Invoke-RestMethod `
    -Uri "$baseUrl/api/quizzes/$missingQuizQuestionId/submit" `
    -Method POST `
    -ContentType "application/json" `
    -Headers @{ Authorization = "Bearer $token" } `
    -Body $validBody
} catch {
  $_.Exception.Response.StatusCode.value__
}
```

Expected:
```text
404
```

No-token check:
```powershell
try {
  Invoke-RestMethod `
    -Uri "$baseUrl/api/quizzes/$quizQuestionId/submit" `
    -Method POST `
    -ContentType "application/json" `
    -Body $validBody
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
Valid submit returned safe response with quizQuestionId, selectedAnswer, isCorrect=false, explanation, and concept.
$json.Contains("correctAnswer") returned False.
Invalid selectedAnswer Z returned 400.
Random valid quiz UUID returned 404.
No-token request returned 401.
Scope checks showed only backend quiz submit/scoring files changed.
```

Important boundaries:
- Backend-only task.
- No migration was added or changed.
- Existing V4 quizzes table is reused.
- POST `/api/quizzes/{quizQuestionId}/submit` is authenticated.
- Request accepts only selectedAnswer.
- selectedAnswer is validated as A/B/C/D with trim/uppercase normalization.
- Scoring uses backend-stored correctAnswer only.
- correctAnswer is never returned in the response.
- No quiz attempts are persisted yet.
- No XP, progress, rank, streak, weak concept, or unlock logic was added.
- Frontend quiz submit UI is implemented; attempt persistence is still not implemented.
- Piston/code execution, deployment, and Phase 2 remain unimplemented.


## Frontend Quiz Submit Integration Manual Test Commands
Use these after the frontend quiz submit integration task `f6fa55d feat: integrate frontend quiz submit`.

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
git status --short
git diff -- backend/src/main/resources/db/migration
git diff -- backend/src/main/java/com/codequest/ai
git diff -- backend/src/main/java/com/codequest/course
git diff -- backend/src/main/java/com/codequest/quiz
git diff -- backend/src/main/java/com/codequest/flashcard
git diff -- backend/src/main/java/com/codequest/note
git diff -- frontend
```

Expected before commit:
```text
Backend migration diff is empty.
AI diff is empty.
Backend course diff is empty.
Backend quiz diff is empty.
Backend flashcard diff is empty.
Backend note diff is empty.
Frontend diff contains only `frontend/src/pages/DashboardShell.jsx` and `frontend/src/services/courseApi.js` before commit.
```

### Backend env/run for browser verification
Gemini key is required only if local DB has no existing AI course with quizQuestions. Do not paste real keys in chat, logs, screenshots, docs, or commits.

Start backend with local PostgreSQL/JWT env vars and, if needed, a rotated Gemini key:
```powershell
cd C:\Users\hp\Desktop\CodeQuestFinalProject

$env:DATABASE_URL="jdbc:postgresql://localhost:5432/codequest"
$env:DATABASE_USERNAME="postgres"
$env:DATABASE_PASSWORD="<your-local-postgres-password>"
$env:JWT_SECRET="dev-only-change-this-secret-dev-only-change-this-secret"

$env:GEMINI_API_KEY="<your-working-rotated-gemini-key>"
$env:GEMINI_MODEL="gemini-2.5-flash"
$env:GEMINI_BASE_URL="https://generativelanguage.googleapis.com"

cd backend
.\mvnw.cmd spring-boot:run
```

If using an existing local AI course with quizQuestions, Gemini env vars can be removed before running backend:
```powershell
Remove-Item Env:GEMINI_API_KEY -ErrorAction SilentlyContinue
Remove-Item Env:GEMINI_MODEL -ErrorAction SilentlyContinue
Remove-Item Env:GEMINI_BASE_URL -ErrorAction SilentlyContinue
```

### Frontend env/run
```powershell
cd C:\Users\hp\Desktop\CodeQuestFinalProject\frontend
npm run dev
```

### Browser verification
```text
1. Login.
2. Generate or reuse an AI course with quizQuestions.
3. Click Open Course Map.
4. Click Open Lesson on a level that has quiz questions.
5. Confirm quiz questions and options A/B/C/D render.
6. Confirm Submit Answer is disabled until an option is selected.
7. Select an option.
8. Confirm Submit Answer becomes enabled.
9. Click Submit Answer.
10. Confirm scoring result appears with Correct/Incorrect, selected answer, explanation, and concept.
11. Confirm correctAnswer is not visible anywhere.
12. Change the selected answer after submit.
13. Confirm the previous result clears.
14. Submit again and confirm the result updates safely.
15. Open a different lesson and confirm previous quiz selection/result does not leak.
16. Confirm Flashcards still work.
17. Confirm Notes preload/save still works.
18. Confirm Back to Course Map and Back to Home still work.
19. Confirm browser console has no red runtime errors.
20. Confirm no token/password/secret/correctAnswer is visible.
```

Observed latest manual result:
```text
AI-generated Trie lesson with quizQuestions rendered in the Lesson Quiz panel.
Option selection worked.
Submit Answer called backend scoring endpoint.
Result card showed Incorrect, selected answer C, concept Trie Search, and safe explanation.
correctAnswer was not visible.
No token/password/secret was visible.
```

Important boundaries:
- Frontend-only task.
- Uses existing backend POST `/api/quizzes/{quizQuestionId}/submit`.
- Uses `quizId` from GET course quiz data with `quizQuestionId` fallback.
- Sends only `selectedAnswer` in request body.
- Calls backend only on explicit Submit Answer click.
- Tracks selection/loading/error/result per question.
- Clears previous result when selected option changes.
- Does not store quiz selections/results in localStorage/sessionStorage.
- Does not display correctAnswer.
- Does not persist attempts.
- Does not implement XP/progress/rank/streak/weak concept/level unlock logic.
- No backend, DB migration, AI, course, quiz backend, flashcard, or note changes.
- Piston/code execution, deployment, and Phase 2 remain unimplemented.


## Backend Quiz Attempt Persistence Foundation Manual Test Commands
Use these after the backend quiz attempt persistence foundation task `40355ea feat: persist quiz submit attempts`.

### Automated verification
```powershell
cd backend
.\mvnw.cmd test
cd ..
```

Expected after this feature:
```text
Tests run: 136
Failures: 0
Errors: 0
BUILD SUCCESS
```

### Scope checks
```powershell
git status --short
git diff -- frontend
git diff -- backend/src/main/java/com/codequest/ai
git diff -- backend/src/main/java/com/codequest/course
git diff -- backend/src/main/java/com/codequest/flashcard
git diff -- backend/src/main/java/com/codequest/note
git diff -- backend/src/main/resources/db/migration
git diff -- backend/src/main/java/com/codequest/quiz
```

Expected before commit:
```text
Frontend diff is empty.
AI diff is empty.
Backend course diff is empty.
Backend flashcard diff is empty.
Backend note diff is empty.
DB migration diff contains only the new V7 quiz_attempts migration before commit.
Backend quiz diff contains QuizAttempt persistence work and tests.
```

### Backend env/run
Start backend with local PostgreSQL/JWT env vars. Gemini key is not required if an existing course with persisted quiz questions is already available locally.
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
Flyway validates/applies V7__create_quiz_attempts_table.sql successfully.
Tomcat started on port 8080.
Started CodeQuestApplication.
```

### Manual quiz attempt persistence verification
From another PowerShell, register/login and fetch a course that already has quiz rows:
```powershell
$baseUrl = "http://localhost:8080"

$email = "attemptmanual$(Get-Random)@example.com"
$password = "AttemptManual123"

$registerBody = @{
  name = "Attempt Manual"
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
$token.Length
```

Find a local course with quiz rows:
```powershell
$env:Path += ";C:\Program Files\PostgreSQL\17\bin"

psql -U postgres -W -d codequest -P pager=off -c "select c.id, c.title, c.source_type, count(q.id) as quiz_count from courses c join levels l on l.course_id = c.id left join quizzes q on q.level_id = l.id group by c.id, c.title, c.source_type having count(q.id) > 0 order by c.created_at desc limit 5;"
```

Set one course id from that output:
```powershell
$courseId = "<course-id-with-quiz-count>"
```

Fetch the course and pick a quiz id. Current GET course response uses `quizId` for quiz question identity:
```powershell
$fetchedCourse = Invoke-RestMethod `
  -Uri "$baseUrl/api/courses/$courseId" `
  -Method GET `
  -Headers @{ Authorization = "Bearer $token" }

$quizQuestion = $fetchedCourse.levels |
  ForEach-Object { $_.quizQuestions } |
  Where-Object { $_ } |
  Select-Object -First 1

$quizQuestion | ConvertTo-Json -Depth 10

$quizQuestionId = $quizQuestion.quizId
$quizQuestionId
```

Count attempts before submit:
```powershell
psql -U postgres -W -d codequest -P pager=off -c "select count(*) as attempt_count_before from quiz_attempts where quiz_id = '$quizQuestionId';"
```

Submit a valid answer and verify safe response:
```powershell
$submitBody = @{
  selectedAnswer = "A"
} | ConvertTo-Json

$submitResponse = Invoke-RestMethod `
  -Uri "$baseUrl/api/quizzes/$quizQuestionId/submit" `
  -Method POST `
  -ContentType "application/json" `
  -Headers @{ Authorization = "Bearer $token" } `
  -Body $submitBody

$submitResponse
$json = $submitResponse | ConvertTo-Json -Depth 10
$json
$json.Contains("correctAnswer")
```

Expected:
```text
Response includes quizQuestionId, selectedAnswer, isCorrect, explanation, and concept.
$json.Contains("correctAnswer") returns False.
```

Verify DB row inserted:
```powershell
psql -U postgres -W -d codequest -P pager=off -c "select selected_answer, is_correct, attempted_at from quiz_attempts where quiz_id = '$quizQuestionId' order by attempted_at desc limit 5;"
```

Repeat submit to confirm history rows are not overwritten:
```powershell
$submitBody2 = @{
  selectedAnswer = "B"
} | ConvertTo-Json

Invoke-RestMethod `
  -Uri "$baseUrl/api/quizzes/$quizQuestionId/submit" `
  -Method POST `
  -ContentType "application/json" `
  -Headers @{ Authorization = "Bearer $token" } `
  -Body $submitBody2

psql -U postgres -W -d codequest -P pager=off -c "select selected_answer, is_correct, attempted_at from quiz_attempts where quiz_id = '$quizQuestionId' order by attempted_at desc limit 5;"
```

Expected:
```text
Both latest B row and earlier A row are present.
```

Invalid answer check:
```powershell
psql -U postgres -W -d codequest -P pager=off -c "select count(*) as before_invalid from quiz_attempts where quiz_id = '$quizQuestionId';"

$invalidBody = @{
  selectedAnswer = "Z"
} | ConvertTo-Json

try {
  Invoke-RestMethod `
    -Uri "$baseUrl/api/quizzes/$quizQuestionId/submit" `
    -Method POST `
    -ContentType "application/json" `
    -Headers @{ Authorization = "Bearer $token" } `
    -Body $invalidBody
} catch {
  $_.Exception.Response.StatusCode.value__
}

psql -U postgres -W -d codequest -P pager=off -c "select count(*) as after_invalid from quiz_attempts where quiz_id = '$quizQuestionId';"
```

Expected:
```text
400
before_invalid and after_invalid are the same count.
```

Missing quiz question check:
```powershell
$missingQuizQuestionId = [guid]::NewGuid().ToString()

$validBody = @{
  selectedAnswer = "A"
} | ConvertTo-Json

try {
  Invoke-RestMethod `
    -Uri "$baseUrl/api/quizzes/$missingQuizQuestionId/submit" `
    -Method POST `
    -ContentType "application/json" `
    -Headers @{ Authorization = "Bearer $token" } `
    -Body $validBody
} catch {
  $_.Exception.Response.StatusCode.value__
}
```

Expected:
```text
404
```

No-token check:
```powershell
try {
  Invoke-RestMethod `
    -Uri "$baseUrl/api/quizzes/$quizQuestionId/submit" `
    -Method POST `
    -ContentType "application/json" `
    -Body $validBody
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
Existing AI course with quiz rows was found.
A valid quizId was selected.
Attempt count before submit was 0.
Valid submit with A returned safe response and inserted one row in quiz_attempts.
$json.Contains("correctAnswer") returned False.
Repeated submit with B returned safe response and inserted another row while preserving the A row.
Invalid selectedAnswer Z returned 400 and did not insert a new row.
Random valid quiz UUID returned 404.
No-token submit returned 401.
Frontend, AI, course, flashcard, and note diffs were empty.
DB migration change was limited to V7 quiz_attempts.
```

Important boundaries:
- Backend-only task.
- V7 adds `quiz_attempts` table only.
- Existing migrations V1 to V6 must not be edited.
- POST `/api/quizzes/{quizQuestionId}/submit` remains authenticated.
- Request still accepts only `selectedAnswer`.
- User identity comes only from JWT / `CurrentUserPrincipal`, never from client `userId`.
- Each successful authenticated submit creates one new attempt row.
- Repeated submits are preserved as separate rows and are not overwritten.
- Invalid answer, missing quiz, and no-token requests do not persist attempts.
- Submit response shape remains unchanged and still hides `correctAnswer`.
- No frontend changes were made.
- No AI/Gemini, course, flashcard, or note changes were made.
- No XP/progress/rank/streak, weak concept, unlock, attempt history fetch endpoint, leaderboard, Piston/code execution, deployment, or Phase 2 work was added.

## Backend Quiz Attempt History/Fetch Foundation Manual Test Commands
Use these after the backend quiz attempt history/fetch task `a36ab6d feat: add quiz attempt history endpoint`.

### Automated verification
```powershell
cd backend
.\mvnw.cmd test
cd ..
```

Expected after this feature:
```text
Tests run: 143
Failures: 0
Errors: 0
BUILD SUCCESS
```

### Scope checks
```powershell
git status --short
git diff -- frontend
git diff -- backend/src/main/resources/db/migration
git diff -- backend/src/main/java/com/codequest/ai
git diff -- backend/src/main/java/com/codequest/course
git diff -- backend/src/main/java/com/codequest/flashcard
git diff -- backend/src/main/java/com/codequest/note
git diff -- backend/src/main/java/com/codequest/quiz
```

Expected before commit:
```text
Frontend diff is empty.
DB migration diff is empty.
AI diff is empty.
Backend course diff is empty.
Backend flashcard diff is empty.
Backend note diff is empty.
Backend quiz diff contains only attempt history endpoint/service/repository/DTO/tests.
```

### Backend env/run
Start backend with local PostgreSQL/JWT env vars. Gemini key is not required if existing quiz rows exist locally.
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
Flyway validates 7 migrations successfully.
Tomcat started on port 8080.
Started CodeQuestApplication.
```

### Manual history verification summary
Observed latest manual result:
```text
User 1 registered/logged in and selected quizId 43aa6c46-a242-4e42-8e3c-db43dfba1c56 from an existing AI course.
User 1 submitted selectedAnswer A and then B.
GET /api/quizzes/attempts returned B first, then A, confirming newest-first ordering.
History response included safe fields: attemptId, quizQuestionId, selectedAnswer, isCorrect, attemptedAt, question, concept, explanation, levelId, levelTitle, courseId, and courseTitle.
$json.Contains("correctAnswer") returned False.
$json.Contains("userId") returned False.
User 2 registered/logged in and initially received attempts count 0.
User 2 submitted selectedAnswer C and then saw only C in their history.
User 1 history still showed only B and A, not user 2's C.
GET /api/quizzes/attempts without token returned 401.
Scope checks showed empty frontend, migration, AI, course, flashcard, and note diffs.
No XP/progress/rank/streak/weak concept/unlock behavior changed.
```

Important boundaries:
- Backend-only task.
- No migration was added or changed.
- Existing V7 `quiz_attempts` table is reused.
- GET `/api/quizzes/attempts` is authenticated.
- Current user identity comes from JWT / `CurrentUserPrincipal`.
- Endpoint never accepts `userId` from client request body, query, path, or headers.
- Endpoint returns only the authenticated user's attempts.
- Attempts are ordered newest-first by `attemptedAt` descending.
- Empty history returns 200 with `attempts: []`.
- Response DTOs are safe and do not expose `correctAnswer`, `userId`, raw entities, tokens, passwords, roles, refresh tokens, token hashes, or secrets.
- POST `/api/quizzes/{quizQuestionId}/submit` behavior and response shape remain unchanged.
- Frontend attempt history UI is implemented and displays current-user attempt history from GET `/api/quizzes/attempts`.
- At that feature stage, XP/progress/rank/streak, weak concept detection, level unlock logic, Piston/code execution, deployment, and Phase 2 were still unimplemented; later features implemented quiz XP award/refresh, backend progress foundation, and backend level unlock enforcement. Rank, streak, weak concept detection, Piston/code execution, deployment, and Phase 2 remain unimplemented.


## Backend Progress / Level Complete Foundation Manual Test Commands
Use these after the backend progress foundation task `f86b082 feat: add level completion progress foundation`.

This is a backend-only feature, so manual verification should be done from PowerShell/API checks, not browser UI.

### Automated verification
```powershell
cd backend
.\mvnw.cmd test
cd ..
```

Expected after this feature:
```text
Tests run: 159
Failures: 0
Errors: 0
BUILD SUCCESS
```

### Scope checks
```powershell
git diff -- frontend
git diff -- backend/src/main/java/com/codequest/ai
git diff -- backend/src/main/java/com/codequest/auth
git diff -- backend/src/main/java/com/codequest/course
git diff -- backend/src/main/java/com/codequest/quiz
git diff -- backend/src/main/java/com/codequest/flashcard
git diff -- backend/src/main/java/com/codequest/note
git diff -- backend/src/main/java/com/codequest/problem
git diff -- backend/src/main/java/com/codequest/leaderboard
git diff -- backend/src/main/resources/db/migration
git diff -- backend/src/main/java/com/codequest/level
git diff -- backend/src/main/java/com/codequest/progress
```

Expected:
```text
Frontend diff is empty.
AI/auth/course/quiz/flashcard/note/problem/leaderboard diffs are empty.
DB migration diff contains only V8 before commit.
Level/progress diffs are expected before commit.
```

### Backend env/run
Start backend with local PostgreSQL/JWT env vars. Gemini key is not required for the level completion endpoint, but a course/level must already exist or be generated/fetched.

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
Flyway validates/applies V8__create_progress_table.sql successfully.
Tomcat started on port 8080.
Started CodeQuestApplication.
```

### Manual PowerShell verification
From another PowerShell terminal:

```powershell
cd C:\Users\hp\Desktop\CodeQuestFinalProject
$baseUrl = "http://localhost:8080"
```

Register a fresh user:
```powershell
$email = "progress_test_" + (Get-Date -Format "yyyyMMddHHmmss") + "@example.com"

$registerBody = @{
  name = "Progress Tester"
  email = $email
  password = "StrongPass123"
} | ConvertTo-Json

$registerResponse = Invoke-RestMethod `
  -Uri "$baseUrl/api/auth/register" `
  -Method Post `
  -ContentType "application/json" `
  -Body $registerBody

$registerResponse
```

Login without printing tokens:
```powershell
$loginBody = @{
  email = $email
  password = "StrongPass123"
} | ConvertTo-Json

$loginResponse = Invoke-RestMethod `
  -Uri "$baseUrl/api/auth/login" `
  -Method Post `
  -ContentType "application/json" `
  -Body $loginBody

$accessToken = $loginResponse.accessToken
$headers = @{
  Authorization = "Bearer $accessToken"
}

$loginResponse | Select-Object userId, name, email, rank, xp, streak, tokenType, expiresInSeconds
```

Generate or fetch a course:
```powershell
$courseBody = @{
  topic = "Progress Manual Test"
  difficulty = "BEGINNER"
  goal = "Test level completion"
} | ConvertTo-Json

$courseResponse = Invoke-RestMethod `
  -Uri "$baseUrl/api/courses/generate" `
  -Method Post `
  -Headers $headers `
  -ContentType "application/json" `
  -Body $courseBody

$courseId = $courseResponse.courseId
$courseResponse
```

Fetch course details and choose first level:
```powershell
$courseDetails = Invoke-RestMethod `
  -Uri "$baseUrl/api/courses/$courseId" `
  -Method Get `
  -Headers $headers

$courseDetails.levels | Select-Object levelId, title, xpReward, isBoss

$levelId = $courseDetails.levels[0].levelId
$levelXp = $courseDetails.levels[0].xpReward

$levelId
$levelXp
```

Check starting profile XP:
```powershell
$profileBefore = Invoke-RestMethod `
  -Uri "$baseUrl/api/user/profile" `
  -Method Get `
  -Headers $headers

$profileBefore | Select-Object name, email, xp, rank, streak

$startingXp = $profileBefore.xp
$startingXp
```

Complete the level first time:
```powershell
$completeResponse1 = Invoke-RestMethod `
  -Uri "$baseUrl/api/levels/$levelId/complete" `
  -Method Post `
  -Headers $headers

$completeResponse1
```

Expected first completion:
```text
completed=true
alreadyCompleted=false
xpAwarded=<level.xpReward>
totalXp=<startingXp + level.xpReward>
completedAt present
```

Confirm profile XP increased:
```powershell
$profileAfterFirst = Invoke-RestMethod `
  -Uri "$baseUrl/api/user/profile" `
  -Method Get `
  -Headers $headers

$profileAfterFirst | Select-Object xp, rank, streak
$profileAfterFirst.xp -eq ($startingXp + $levelXp)
```

Expected:
```text
True
```

Complete the same level again:
```powershell
$completeResponse2 = Invoke-RestMethod `
  -Uri "$baseUrl/api/levels/$levelId/complete" `
  -Method Post `
  -Headers $headers

$completeResponse2
```

Expected repeat completion:
```text
completed=true
alreadyCompleted=true
xpAwarded=0
totalXp unchanged
```

Confirm repeat did not increase XP:
```powershell
$profileAfterRepeat = Invoke-RestMethod `
  -Uri "$baseUrl/api/user/profile" `
  -Method Get `
  -Headers $headers

$profileAfterRepeat | Select-Object xp, rank, streak
$profileAfterRepeat.xp -eq $profileAfterFirst.xp
```

Expected:
```text
True
```

No-token check:
```powershell
try {
  Invoke-RestMethod `
    -Uri "$baseUrl/api/levels/$levelId/complete" `
    -Method Post
} catch {
  $_.Exception.Response.StatusCode.value__
}
```

Expected:
```text
401
```

Missing-level check:
```powershell
$randomLevelId = [guid]::NewGuid().ToString()

try {
  Invoke-RestMethod `
    -Uri "$baseUrl/api/levels/$randomLevelId/complete" `
    -Method Post `
    -Headers $headers
} catch {
  $_.Exception.Response.StatusCode.value__
}
```

Expected:
```text
404
```

Safety response check:
```powershell
$completeResponse1 | ConvertTo-Json -Depth 5
```

Expected safe fields only:
```text
levelId
completed
alreadyCompleted
xpAwarded
totalXp
completedAt
```

Must not contain:
```text
userId
password
passwordHash
password_hash
role
token
accessToken
refreshToken
tokenHash
secret
```

Observed latest manual result:
```text
Login worked with xp=0 and rank=BEGINNER.
Course fetch worked and first level had xpReward=100.
First completion returned completed=True, alreadyCompleted=False, xpAwarded=100, totalXp=100.
Profile XP became 100 after first completion.
Repeat completion returned completed=True, alreadyCompleted=True, xpAwarded=0, totalXp=100.
Profile XP stayed 100 after repeat completion.
No-token request returned 401.
Random valid level UUID returned 404.
Response JSON contained only levelId, completed, alreadyCompleted, xpAwarded, totalXp, and completedAt.
```

### Resolved runtime issue during this feature
Initial manual verification returned HTTP 500 for `POST /api/levels/{levelId}/complete`.

Backend log:
```text
ERROR: column "quiz_answers_json" is of type jsonb but expression is of type character varying
SQLState: 42804
```

Root cause:
```text
The V8 migration created quiz_answers_json as PostgreSQL JSONB, but the Progress entity mapped it as a Java String, causing Hibernate to bind it as VARCHAR during insert.
```

Fix:
```text
Removed the unused quizAnswersJson field from Progress entity and constructor so Hibernate no longer writes a varchar value into the JSONB column.
Kept nullable quiz_answers_json JSONB in V8 for future schema alignment.
```

Important boundaries:
- Endpoint is POST `/api/levels/{levelId}/complete`.
- Endpoint requires JWT Bearer token.
- Endpoint accepts no request body.
- Current user comes only from `CurrentUserPrincipal`.
- Request must not accept userId from body, query params, headers, or path.
- First completion creates one progress row and awards `level.xpReward`.
- Repeat completion is idempotent and does not award XP again.
- Different users can complete the same level independently.
- Response is safe and does not expose userId, passwords, roles, tokens, refresh tokens, token hashes, or secrets.
- Frontend UI integration is not implemented in this backend foundation.
- Rank, streak, progress percentage, weak concept detection, unlock logic, boss prerequisites, course completion, leaderboard, achievements, anti-farming, Piston/code execution, deployment, and Phase 2 remain unimplemented.




## Backend Level Unlock Logic Foundation Manual Test Commands
Use these after the backend level unlock logic task `12cae38 feat: enforce level unlock rules`.

This is a backend-only feature, so manual verification should be done from PowerShell/API checks, not browser UI.

### Automated verification
```powershell
cd backend
.\mvnw.cmd test
cd ..
```

Expected after this feature:
```text
Tests run: 168
Failures: 0
Errors: 0
BUILD SUCCESS
```

### Scope checks
```powershell
git diff -- frontend
git diff -- backend/src/main/java/com/codequest/ai
git diff -- backend/src/main/java/com/codequest/auth
git diff -- backend/src/main/java/com/codequest/quiz
git diff -- backend/src/main/java/com/codequest/flashcard
git diff -- backend/src/main/java/com/codequest/note
git diff -- backend/src/main/java/com/codequest/problem
git diff -- backend/src/main/java/com/codequest/leaderboard
git diff -- backend/src/main/resources/db/migration
git diff -- backend/src/main/java/com/codequest/progress
git diff -- backend/src/main/java/com/codequest/level
git diff -- backend/src/main/java/com/codequest/course
git diff -- backend/src/main/java/com/codequest/common/exception
```

Expected:
```text
Frontend diff is empty.
AI/auth/quiz/flashcard/note/problem/leaderboard diffs are empty.
Migration diff is empty.
Course and common exception diffs are empty.
Progress and level diffs are expected before commit.
No package/dependency diffs.
```

### Backend env/run
Start backend with local PostgreSQL/JWT env vars. Remove Gemini env vars so placeholder course generation is predictable.

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
Flyway validates V1 through V8 successfully.
Tomcat started on port 8080.
Started CodeQuestApplication.
```

### Manual PowerShell verification
From another PowerShell terminal:

```powershell
cd C:\Users\hp\Desktop\CodeQuestFinalProject
$baseUrl = "http://localhost:8080"
```

Register and login a fresh user:
```powershell
$email = "unlock_test_" + (Get-Date -Format "yyyyMMddHHmmss") + "@example.com"

$registerBody = @{
  name = "Unlock Tester"
  email = $email
  password = "StrongPass123"
} | ConvertTo-Json

$registerResponse = Invoke-RestMethod `
  -Uri "$baseUrl/api/auth/register" `
  -Method Post `
  -ContentType "application/json" `
  -Body $registerBody

$registerResponse

$loginBody = @{
  email = $email
  password = "StrongPass123"
} | ConvertTo-Json

$loginResponse = Invoke-RestMethod `
  -Uri "$baseUrl/api/auth/login" `
  -Method Post `
  -ContentType "application/json" `
  -Body $loginBody

$accessToken = $loginResponse.accessToken
$headers = @{ Authorization = "Bearer $accessToken" }

$loginResponse | Select-Object userId, name, email, rank, xp, streak, tokenType, expiresInSeconds
```

Generate a predictable placeholder course:
```powershell
$topic = "Unlock Manual Test " + (Get-Date -Format "yyyyMMddHHmmss")

$courseBody = @{
  topic = $topic
  difficulty = "BEGINNER"
  goal = "Test unlock logic"
} | ConvertTo-Json

$courseResponse = Invoke-RestMethod `
  -Uri "$baseUrl/api/courses/generate" `
  -Method Post `
  -Headers $headers `
  -ContentType "application/json" `
  -Body $courseBody

$courseId = $courseResponse.courseId
$courseResponse | Select-Object courseId, title, sourceType, cacheHit
```

Fetch course details and save level IDs:
```powershell
$courseDetails = Invoke-RestMethod `
  -Uri "$baseUrl/api/courses/$courseId" `
  -Method Get `
  -Headers $headers

$courseDetails.levels | Select-Object levelId, orderNumber, title, xpReward, isBoss

$level1 = $courseDetails.levels | Where-Object { $_.orderNumber -eq 1 }
$level2 = $courseDetails.levels | Where-Object { $_.orderNumber -eq 2 }
$boss = $courseDetails.levels | Where-Object { $_.isBoss -eq $true } | Select-Object -First 1

$level1Id = $level1.levelId
$level2Id = $level2.levelId
$bossId = $boss.levelId

$level1Xp = $level1.xpReward
$level2Xp = $level2.xpReward
$bossXp = $boss.xpReward

"Level1: $level1Id XP=$level1Xp"
"Level2: $level2Id XP=$level2Xp"
"Boss: $bossId XP=$bossXp"
```

Check starting XP:
```powershell
$profileBefore = Invoke-RestMethod `
  -Uri "$baseUrl/api/user/profile" `
  -Method Get `
  -Headers $headers

$startingXp = $profileBefore.xp
$profileBefore | Select-Object xp, rank, streak
```

Try locked level 2 before level 1:
```powershell
try {
  Invoke-RestMethod `
    -Uri "$baseUrl/api/levels/$level2Id/complete" `
    -Method Post `
    -Headers $headers
} catch {
  "STATUS: " + $_.Exception.Response.StatusCode.value__
  $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
  "BODY:"
  $reader.ReadToEnd()
}
```

Expected:
```text
STATUS: 403
code: FORBIDDEN
message: Complete previous levels before unlocking this level.
```

Confirm XP unchanged:
```powershell
$profileAfterLockedLevel2 = Invoke-RestMethod `
  -Uri "$baseUrl/api/user/profile" `
  -Method Get `
  -Headers $headers

$profileAfterLockedLevel2.xp -eq $startingXp
```

Expected:
```text
True
```

Try locked boss before previous levels:
```powershell
try {
  Invoke-RestMethod `
    -Uri "$baseUrl/api/levels/$bossId/complete" `
    -Method Post `
    -Headers $headers
} catch {
  "STATUS: " + $_.Exception.Response.StatusCode.value__
  $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
  "BODY:"
  $reader.ReadToEnd()
}
```

Expected:
```text
STATUS: 403
```

Confirm XP unchanged:
```powershell
$profileAfterLockedBoss = Invoke-RestMethod `
  -Uri "$baseUrl/api/user/profile" `
  -Method Get `
  -Headers $headers

$profileAfterLockedBoss.xp -eq $startingXp
```

Expected:
```text
True
```

Complete level 1:
```powershell
$completeLevel1 = Invoke-RestMethod `
  -Uri "$baseUrl/api/levels/$level1Id/complete" `
  -Method Post `
  -Headers $headers

$completeLevel1
```

Expected:
```text
completed=true
alreadyCompleted=false
xpAwarded=$level1Xp
totalXp=$startingXp + $level1Xp
completedAt present
```

Try boss again before level 2:
```powershell
try {
  Invoke-RestMethod `
    -Uri "$baseUrl/api/levels/$bossId/complete" `
    -Method Post `
    -Headers $headers
} catch {
  "STATUS: " + $_.Exception.Response.StatusCode.value__
  $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
  "BODY:"
  $reader.ReadToEnd()
}
```

Expected:
```text
STATUS: 403
```

Complete level 2:
```powershell
$completeLevel2 = Invoke-RestMethod `
  -Uri "$baseUrl/api/levels/$level2Id/complete" `
  -Method Post `
  -Headers $headers

$completeLevel2
```

Expected:
```text
completed=true
alreadyCompleted=false
xpAwarded=$level2Xp
```

Complete boss:
```powershell
$completeBoss = Invoke-RestMethod `
  -Uri "$baseUrl/api/levels/$bossId/complete" `
  -Method Post `
  -Headers $headers

$completeBoss
```

Expected:
```text
completed=true
alreadyCompleted=false
xpAwarded=$bossXp
```

Repeat level 1:
```powershell
$repeatLevel1 = Invoke-RestMethod `
  -Uri "$baseUrl/api/levels/$level1Id/complete" `
  -Method Post `
  -Headers $headers

$repeatLevel1
```

Expected:
```text
completed=true
alreadyCompleted=true
xpAwarded=0
totalXp unchanged
```

Final XP check:
```powershell
$profileAfter = Invoke-RestMethod `
  -Uri "$baseUrl/api/user/profile" `
  -Method Get `
  -Headers $headers

$profileAfter | Select-Object xp, rank, streak

$expectedXp = $startingXp + $level1Xp + $level2Xp + $bossXp
"Expected XP: $expectedXp"
"Actual XP: $($profileAfter.xp)"
$profileAfter.xp -eq $expectedXp
```

Expected:
```text
True
```

No-token check:
```powershell
try {
  Invoke-RestMethod `
    -Uri "$baseUrl/api/levels/$level1Id/complete" `
    -Method Post
} catch {
  $_.Exception.Response.StatusCode.value__
}
```

Expected:
```text
401
```

Missing level check:
```powershell
$randomLevelId = [guid]::NewGuid().ToString()

try {
  Invoke-RestMethod `
    -Uri "$baseUrl/api/levels/$randomLevelId/complete" `
    -Method Post `
    -Headers $headers
} catch {
  $_.Exception.Response.StatusCode.value__
}
```

Expected:
```text
404
```

Safety check:
```powershell
$completeLevel1 | ConvertTo-Json -Depth 5
$completeLevel2 | ConvertTo-Json -Depth 5
$completeBoss | ConvertTo-Json -Depth 5
$repeatLevel1 | ConvertTo-Json -Depth 5
```

Expected success response fields only:
```text
levelId
completed
alreadyCompleted
xpAwarded
totalXp
completedAt
```

Must not show:
```text
userId
password
passwordHash
password_hash
role
token
accessToken
refreshToken
tokenHash
secret
```

Observed latest manual result:
```text
Locked level 2 before level 1 returned 403 FORBIDDEN.
Locked boss before previous levels returned 403 FORBIDDEN.
XP stayed 0 after locked attempts.
Level 1 completed with xpAwarded=50 and totalXp=50.
Boss still returned 403 after only level 1.
Level 2 completed with xpAwarded=75 and totalXp=125.
Boss completed with xpAwarded=100 and totalXp=225.
Repeat level 1 returned alreadyCompleted=true, xpAwarded=0, totalXp=225.
Final expected XP 225 matched actual XP 225.
No-token request returned 401.
Random valid level UUID returned 404.
Success responses exposed only levelId, completed, alreadyCompleted, xpAwarded, totalXp, and completedAt.
```

Important boundaries:
- No frontend lock UI was implemented.
- No DB migration was added.
- Existing V8 progress migration was not changed.
- AI/Gemini, auth, quiz, flashcard, note, problem, leaderboard, common exception, package files, README, Docker, CI/CD, and deployment were not changed.
- Existing first-completion XP award and repeat idempotency behavior were preserved.
- Locked level attempts do not create progress and do not award XP.
- Unlock state is scoped to the authenticated user only.
- Existing `FORBIDDEN` ErrorDTO handling is reused.
- Rank, streak, weak concept detection, progress percentage, course completion UI, leaderboard, achievements, Piston/code execution, and Phase 2 features remain unimplemented.

## Backend Weak Concept Detection Foundation Manual Test Commands
Use these after the backend weak concept task `ff0a4d4 feat: add weak concepts to quiz submit`.

This is a backend-only feature, so manual verification should be done from PowerShell/API checks, not browser UI.

### Automated verification

```powershell
cd backend
.\mvnw.cmd test
cd ..
```

Expected after this feature:
```text
Tests run: 202
Failures: 0
Errors: 0
Skipped: 0
BUILD SUCCESS
```

### Scope checks

```powershell
git status --short
git diff -- frontend
git diff -- backend/src/main/resources/db/migration
git diff -- backend/pom.xml
git diff -- docs
git diff -- CodeQuest_Build_Log.md
git diff -- backend/src/main/java/com/codequest/ai
git diff -- backend/src/main/java/com/codequest/auth
git diff -- backend/src/main/java/com/codequest/progress
git diff -- backend/src/main/java/com/codequest/user
git diff -- backend/src/main/java/com/codequest/problem
git diff -- backend/src/main/java/com/codequest/leaderboard
git diff -- backend/src/main/java/com/codequest/common/security
```

Expected before commit:
```text
Only these files are modified:
 M backend/src/main/java/com/codequest/quiz/QuizService.java
 M backend/src/main/java/com/codequest/quiz/dto/SubmitQuizAnswerResponse.java
 M backend/src/test/java/com/codequest/quiz/QuizControllerTest.java
 M backend/src/test/java/com/codequest/quiz/QuizServiceTest.java

Frontend diff empty.
DB migration diff empty.
backend/pom.xml diff empty.
docs diff empty.
Build Log diff empty.
AI/auth/progress/user/problem/leaderboard/common security diffs empty.
```

### Terminal 1 - Start backend

Start backend with local PostgreSQL/JWT env vars.

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

Expected backend startup:
```text
Tomcat started on port 8080
Started CodeQuestApplication
```

### Terminal 2 - Manual PowerShell API verification

```powershell
cd C:\Users\hp\Desktop\CodeQuestFinalProject
$baseUrl = "http://localhost:8080"
```

Register and login a fresh user:
```powershell
$email = "weak_manual_" + (Get-Date -Format "yyyyMMddHHmmss") + "@example.com"

$registerBody = @{
  name = "Weak Manual User"
  email = $email
  password = "StrongPass123"
} | ConvertTo-Json

Invoke-RestMethod `
  -Uri "$baseUrl/api/auth/register" `
  -Method Post `
  -ContentType "application/json" `
  -Body $registerBody

$loginBody = @{
  email = $email
  password = "StrongPass123"
} | ConvertTo-Json

$loginResponse = Invoke-RestMethod `
  -Uri "$baseUrl/api/auth/login" `
  -Method Post `
  -ContentType "application/json" `
  -Body $loginBody

$token = $loginResponse.accessToken
$headers = @{ Authorization = "Bearer $token" }
```

Find an existing quiz row with a nonblank concept tag:
```powershell
$env:Path += ";C:\Program Files\PostgreSQL\17\bin"
psql -U postgres -W -d codequest -c "select id, correct_answer, concept_tag from quizzes where concept_tag is not null and trim(concept_tag) <> '' limit 5;"
```

Verified manual row used during this feature:
```text
id = 44821f81-730c-4b18-9b2b-fb6e70354366
correct_answer = B
concept_tag = Trie Definition
```

Submit a wrong answer:
```powershell
$quizId = "44821f81-730c-4b18-9b2b-fb6e70354366"
$correctAnswer = "B"
$wrongAnswer = "A"

$wrongBody = @{
  selectedAnswer = $wrongAnswer
} | ConvertTo-Json

$wrongResponse = Invoke-RestMethod `
  -Uri "$baseUrl/api/quizzes/$quizId/submit" `
  -Method Post `
  -Headers $headers `
  -ContentType "application/json" `
  -Body $wrongBody

$wrongResponse | Select-Object quizQuestionId, selectedAnswer, isCorrect, concept, weakConcepts
```

Expected:
```text
quizQuestionId = 44821f81-730c-4b18-9b2b-fb6e70354366
selectedAnswer = A
isCorrect = False
concept = Trie Definition
weakConcepts = {Trie Definition}
correctAnswer is not present
```

Submit the correct answer:
```powershell
$correctBody = @{
  selectedAnswer = $correctAnswer
} | ConvertTo-Json

$correctResponse = Invoke-RestMethod `
  -Uri "$baseUrl/api/quizzes/$quizId/submit" `
  -Method Post `
  -Headers $headers `
  -ContentType "application/json" `
  -Body $correctBody

$correctResponse | Select-Object quizQuestionId, selectedAnswer, isCorrect, concept, weakConcepts
```

Expected:
```text
quizQuestionId = 44821f81-730c-4b18-9b2b-fb6e70354366
selectedAnswer = B
isCorrect = True
concept = Trie Definition
weakConcepts = empty list
correctAnswer is not present
```

Safety check:
```powershell
$wrongJson = $wrongResponse | ConvertTo-Json -Depth 10
$correctJson = $correctResponse | ConvertTo-Json -Depth 10

$wrongJson.Contains("correctAnswer")
$wrongJson.Contains("userId")
$wrongJson.Contains("password")
$wrongJson.Contains("passwordHash")
$wrongJson.Contains("password_hash")
$wrongJson.Contains("token")
$wrongJson.Contains("refreshToken")
$wrongJson.Contains("tokenHash")
$wrongJson.Contains("secret")
$correctJson.Contains("correctAnswer")
$correctJson.Contains("userId")
$correctJson.Contains("password")
$correctJson.Contains("token")
```

Expected:
```text
All checks return False.
```

No-token check:
```powershell
try {
  Invoke-RestMethod `
    -Uri "$baseUrl/api/quizzes/$quizId/submit" `
    -Method Post `
    -ContentType "application/json" `
    -Body $wrongBody
} catch {
  $_.Exception.Response.StatusCode.value__
}
```

Expected:
```text
401
```

Important boundaries:
- Feature is backend-only.
- Endpoint remains POST `/api/quizzes/{quizQuestionId}/submit`.
- Response includes `weakConcepts` but still keeps existing safe response fields backward-compatible.
- Wrong answers with nonblank concept return a one-item `weakConcepts` list.
- Correct answers return an empty `weakConcepts` list.
- No Gemini call is made.
- No remedial level generation is implemented.
- No weak concept persistence table or DB migration is added.
- No frontend UI is added.
- Scoring, XP awards, quiz attempt persistence/history, rank, streak, progress, unlock rules, leaderboard, Piston/code execution, deployment, and Phase 2 behavior are unchanged.


## Backend Piston Run Code Foundation Manual Test Commands
Use these after the backend Piston run-code task `d806c43 feat: add piston run code foundation`.

This is a backend-only feature. Manual verification should be done from PowerShell/API checks. External Piston availability is not guaranteed; a safe 503 `CODE_RUNNER_UNAVAILABLE` response is acceptable if mocked automated tests pass and the error response is safe.

### Automated verification

```powershell
cd backend
.\mvnw.cmd test
cd ..
```

Expected after this feature:
```text
Tests run: 215
Failures: 0
Errors: 0
Skipped: 0
BUILD SUCCESS
```

### Scope checks

```powershell
git status --short
git diff -- frontend
git diff -- backend/src/main/resources/db/migration
git diff -- backend/pom.xml
git diff -- docs
git diff -- CodeQuest_Build_Log.md
git diff -- backend/src/main/java/com/codequest/ai
git diff -- backend/src/main/java/com/codequest/auth
git diff -- backend/src/main/java/com/codequest/course
git diff -- backend/src/main/java/com/codequest/level
git diff -- backend/src/main/java/com/codequest/progress
git diff -- backend/src/main/java/com/codequest/user
git diff -- backend/src/main/java/com/codequest/quiz
git diff -- backend/src/main/java/com/codequest/flashcard
git diff -- backend/src/main/java/com/codequest/note
git diff -- backend/src/main/java/com/codequest/leaderboard
```

Expected before commit:
```text
Only expected backend files are modified:
 M backend/src/main/java/com/codequest/common/exception/ErrorCode.java
 M backend/src/main/java/com/codequest/common/exception/GlobalExceptionHandler.java
 M backend/src/main/resources/application.yml
 ?? backend/src/main/java/com/codequest/problem/
 ?? backend/src/test/java/com/codequest/problem/

All listed diff checks for frontend, migrations, pom, docs, Build Log, AI/Gemini, auth, course, level, progress, user, quiz, flashcard, note, and leaderboard are empty.
```

### Terminal 1 - Start backend

Start backend with local PostgreSQL/JWT env vars. Remove Gemini env vars because this feature does not need Gemini. Set Piston base URL only if the implementation reads it.

```powershell
cd C:\Users\hp\Desktop\CodeQuestFinalProject

$env:DATABASE_URL="jdbc:postgresql://localhost:5432/codequest"
$env:DATABASE_USERNAME="postgres"
$env:DATABASE_PASSWORD="<your-local-postgres-password>"
$env:JWT_SECRET="dev-only-change-this-secret-dev-only-change-this-secret"

Remove-Item Env:GEMINI_API_KEY -ErrorAction SilentlyContinue
Remove-Item Env:GEMINI_MODEL -ErrorAction SilentlyContinue
Remove-Item Env:GEMINI_BASE_URL -ErrorAction SilentlyContinue

$env:PISTON_BASE_URL="https://emkc.org/api/v2/piston"

cd backend
.\mvnw.cmd spring-boot:run
```

Expected backend startup:
```text
Tomcat started on port 8080
Started CodeQuestApplication
```

### Terminal 2 - Manual PowerShell API verification

```powershell
cd C:\Users\hp\Desktop\CodeQuestFinalProject
$baseUrl = "http://localhost:8080"
```

Register and login a fresh user:
```powershell
$email = "piston_run_manual_" + (Get-Date -Format "yyyyMMddHHmmss") + "@example.com"

$registerBody = @{
  name = "Piston Run Manual User"
  email = $email
  password = "StrongPass123"
} | ConvertTo-Json

Invoke-RestMethod `
  -Uri "$baseUrl/api/auth/register" `
  -Method Post `
  -ContentType "application/json" `
  -Body $registerBody

$loginBody = @{
  email = $email
  password = "StrongPass123"
} | ConvertTo-Json

$loginResponse = Invoke-RestMethod `
  -Uri "$baseUrl/api/auth/login" `
  -Method Post `
  -ContentType "application/json" `
  -Body $loginBody

$token = $loginResponse.accessToken
$headers = @{ Authorization = "Bearer $token" }

$problemId = [guid]::NewGuid().ToString()
```

Run simple Java code:
```powershell
$runBody = @{
  language = "java"
  code = @"
public class Main {
  public static void main(String[] args) {
    System.out.println("Hello CodeQuest");
  }
}
"@
  stdin = ""
  expectedOutput = "Hello CodeQuest"
} | ConvertTo-Json

try {
  $runResponse = Invoke-RestMethod `
    -Uri "$baseUrl/api/problems/$problemId/run" `
    -Method Post `
    -Headers $headers `
    -ContentType "application/json" `
    -Body $runBody

  "STATUS: 200"
  $runResponse
} catch {
  "STATUS: " + $_.Exception.Response.StatusCode.value__
  $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
  "BODY:"
  $reader.ReadToEnd()
}
```

Expected if external Piston is reachable:
```text
STATUS: 200
language = java
output/stdout contains Hello CodeQuest
passed = True
```

Acceptable if external Piston is unavailable:
```text
STATUS: 503
code = CODE_RUNNER_UNAVAILABLE
safe message only
no raw Piston body
no raw stack trace
```

Check profile XP did not change except existing login daily XP:
```powershell
$profileAfterRun = Invoke-RestMethod `
  -Uri "$baseUrl/api/user/profile" `
  -Method Get `
  -Headers $headers

$profileAfterRun | Select-Object xp, rank, streak
```

Expected:
```text
XP should remain the same as after login.
Because StreakService exists, fresh first login usually gives xp=30, rank=BEGINNER, streak=1.
Run-only endpoint must not increase XP beyond that.
```

Expected-output mismatch check if Piston is reachable:
```powershell
$mismatchBody = @{
  language = "java"
  code = @"
public class Main {
  public static void main(String[] args) {
    System.out.println("Hello CodeQuest");
  }
}
"@
  stdin = ""
  expectedOutput = "Different Output"
} | ConvertTo-Json

$mismatchResponse = Invoke-RestMethod `
  -Uri "$baseUrl/api/problems/$problemId/run" `
  -Method Post `
  -Headers $headers `
  -ContentType "application/json" `
  -Body $mismatchBody

$mismatchResponse | Select-Object passed, output, stdout, stderr
```

Expected if Piston is reachable:
```text
passed = False
output/stdout contains Hello CodeQuest
```

Invalid language check:
```powershell
$badLanguageBody = @{
  language = "ruby"
  code = "puts 'hi'"
  stdin = ""
  expectedOutput = "hi"
} | ConvertTo-Json

try {
  Invoke-RestMethod `
    -Uri "$baseUrl/api/problems/$problemId/run" `
    -Method Post `
    -Headers $headers `
    -ContentType "application/json" `
    -Body $badLanguageBody
} catch {
  $_.Exception.Response.StatusCode.value__
}
```

Expected:
```text
400
```

No-token check:
```powershell
try {
  Invoke-RestMethod `
    -Uri "$baseUrl/api/problems/$problemId/run" `
    -Method Post `
    -ContentType "application/json" `
    -Body $runBody
} catch {
  $_.Exception.Response.StatusCode.value__
}
```

Expected:
```text
401
```

Safety check if `$runResponse` exists:
```powershell
$runJson = $runResponse | ConvertTo-Json -Depth 10
$runJson.Contains("password")
$runJson.Contains("passwordHash")
$runJson.Contains("password_hash")
$runJson.Contains("token")
$runJson.Contains("refreshToken")
$runJson.Contains("tokenHash")
$runJson.Contains("secret")
$runJson.Contains("correctAnswer")
$runJson.Contains("hidden")
$runJson.Contains("userId")
```

Expected:
```text
All checks return False.
```

Safety check if the run returned a 503:
```powershell
try {
  Invoke-RestMethod `
    -Uri "$baseUrl/api/problems/$problemId/run" `
    -Method Post `
    -Headers $headers `
    -ContentType "application/json" `
    -Body $runBody
} catch {
  $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
  $errorBody = $reader.ReadToEnd()
  $errorBody.Contains("password")
  $errorBody.Contains("passwordHash")
  $errorBody.Contains("password_hash")
  $errorBody.Contains("token")
  $errorBody.Contains("refreshToken")
  $errorBody.Contains("tokenHash")
  $errorBody.Contains("secret")
  $errorBody.Contains("correctAnswer")
  $errorBody.Contains("hidden")
  $errorBody.Contains("userId")
  $errorBody.Contains("java.lang")
  $errorBody.Contains("org.springframework")
  $errorBody.Contains("stackTrace")
}
```

Expected:
```text
All checks return False.
```

Important boundaries:
- Endpoint is POST `/api/problems/{problemId}/run`.
- Endpoint requires JWT Bearer token.
- Endpoint is run-only.
- Current implementation does not persist submissions.
- Current implementation does not award XP.
- Current implementation does not call `XPService`.
- Current implementation does not implement code submit/history.
- Current implementation does not implement frontend code editor or Monaco.
- Current implementation does not implement AI code review.
- Current implementation does not add coding problem persistence/fetch.
- Current implementation keeps problemId for API contract compatibility and echoes it in the safe response.
- Backend must never execute user code locally.
- Piston unavailable should return safe 503 `CODE_RUNNER_UNAVAILABLE` style response.

## Backend Code Submit Foundation Manual Test Commands
Use these after the backend code submit foundation task `7a24c00 feat: add code submit foundation`.

This is a backend-only feature. Frontend build is not required unless frontend files changed. Because external Piston can be unavailable, manual verification must at least confirm safe unavailable handling, validation, auth, no persistence on unavailable runner, and no XP award on unavailable runner. Mocked automated tests cover accepted/repeat/failed submit behavior.

### Automated verification

```powershell
cd C:\Users\hp\Desktop\CodeQuestFinalProject\backend
.\mvnw.cmd test
cd ..
```

Expected:
```text
BUILD SUCCESS
```

If stale compiled output appears:
```powershell
cd C:\Users\hp\Desktop\CodeQuestFinalProject\backend
.\mvnw.cmd clean test
cd ..
```

### Backend env/run

```powershell
cd C:\Users\hp\Desktop\CodeQuestFinalProject

$env:DATABASE_URL="jdbc:postgresql://localhost:5432/codequest"
$env:DATABASE_USERNAME="postgres"
$env:DATABASE_PASSWORD="<your-local-postgres-password>"
$env:JWT_SECRET="dev-only-change-this-secret-dev-only-change-this-secret"
$env:PISTON_BASE_URL="https://emkc.org/api/v2/piston"

cd backend
.\mvnw.cmd spring-boot:run
```

Expected:
```text
Flyway validates/applies V1 through V9 successfully.
Tomcat started on port 8080.
Started CodeQuestApplication.
```

### Manual PowerShell verification

From another PowerShell terminal:

```powershell
cd C:\Users\hp\Desktop\CodeQuestFinalProject

$baseUrl = "http://localhost:8080"
$email = "codesubmit_test_$([guid]::NewGuid().ToString('N').Substring(0,8))@example.com"
$password = "StrongPass123"

$registerBody = @{
  name = "Code Submit Tester"
  email = $email
  password = $password
} | ConvertTo-Json

Invoke-RestMethod `
  -Uri "$baseUrl/api/auth/register" `
  -Method Post `
  -ContentType "application/json" `
  -Body $registerBody

$loginBody = @{
  email = $email
  password = $password
} | ConvertTo-Json

$login = Invoke-RestMethod `
  -Uri "$baseUrl/api/auth/login" `
  -Method Post `
  -ContentType "application/json" `
  -Body $loginBody

$token = $login.accessToken
$headers = @{ Authorization = "Bearer $token" }

$profileBefore = Invoke-RestMethod `
  -Uri "$baseUrl/api/user/profile" `
  -Method Get `
  -Headers $headers

$profileBefore
```

Expected:
```text
Registration response is safe.
Login response is safe.
Profile after first login may show xp=30 and streak=1 because daily login XP is implemented.
```

Prepare submit payload:
```powershell
$problemId = [guid]::NewGuid()

$submitBody = @{
  language = "java"
  code = 'public class Main { public static void main(String[] args) { System.out.println("42"); } }'
  stdin = ""
  expectedOutput = "42"
} | ConvertTo-Json
```

Try authenticated submit and capture safe error body if Piston is unavailable:
```powershell
try {
  $firstSubmit = Invoke-RestMethod `
    -Uri "$baseUrl/api/problems/$problemId/submit" `
    -Method Post `
    -Headers $headers `
    -ContentType "application/json" `
    -Body $submitBody

  $firstSubmit
} catch {
  $_.Exception.Response.StatusCode.value__
  $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
  $reader.ReadToEnd()
}
```

Expected if Piston is available:
```text
passed=true
xpAwarded=100
firstAccepted=true
message indicates accepted/XP awarded
```

Expected if Piston is unavailable:
```text
503
ErrorDTO code CODE_RUNNER_UNAVAILABLE
No raw Piston response body
No stack trace
No user code
No token/password/secret
```

If Piston is unavailable, verify no row was persisted for the failed runner call:
```powershell
& "C:\Program Files\PostgreSQL\17\bin\psql.exe" -U postgres -d codequest -c "SELECT COUNT(*) FROM code_submissions WHERE problem_id = '$problemId';"
```

Expected:
```text
0
```

If your PostgreSQL version folder is not `17`, replace `17` with the folder shown by:
```powershell
Get-ChildItem "C:\Program Files\PostgreSQL" -Directory
```

Verify profile XP did not increase on unavailable runner:
```powershell
$profileAfter503 = Invoke-RestMethod `
  -Uri "$baseUrl/api/user/profile" `
  -Method Get `
  -Headers $headers

$profileAfter503
```

Expected:
```text
XP stays at the previous profile value, usually 30 for a fresh user after daily login XP.
No coding XP is awarded.
```

Invalid language check:
```powershell
$invalidBody = @{
  language = "ruby"
  code = 'puts "42"'
  stdin = ""
  expectedOutput = "42"
} | ConvertTo-Json

try {
  Invoke-WebRequest `
    -Uri "$baseUrl/api/problems/$problemId/submit" `
    -Method Post `
    -Headers $headers `
    -ContentType "application/json" `
    -Body $invalidBody `
    -UseBasicParsing
} catch {
  $_.Exception.Response.StatusCode.value__
  $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
  $reader.ReadToEnd()
}
```

Expected:
```text
400
Safe validation/ErrorDTO message such as:
Language must be one of: java, python, javascript, cpp.
```

No-token check:
```powershell
try {
  Invoke-WebRequest `
    -Uri "$baseUrl/api/problems/$problemId/submit" `
    -Method Post `
    -ContentType "application/json" `
    -Body $submitBody `
    -UseBasicParsing
} catch {
  $_.Exception.Response.StatusCode.value__
}
```

Expected:
```text
401
```

Migration inspection:
```powershell
Get-Content backend/src/main/resources/db/migration/V9__create_code_submissions_table.sql
```

Expected:
```text
Only creates code_submissions.
Includes user_id, problem_id, language, code, passed, passed_test_cases, total_test_cases, runtime_ms, memory_kb, ai_review, submitted_at, created_at, updated_at.
Includes indexes for user_id and user_id + problem_id.
Does not edit old migrations or old tables.
```

Safety expectations:
```text
Submit responses/errors must not expose:
userId
password/passwordHash/password_hash
tokens/accessToken/refreshToken/tokenHash
secrets
correctAnswer
hidden tests
raw Piston compile/run internals
raw stack traces
raw entities
```

Important boundaries:
- Endpoint is POST `/api/problems/{problemId}/submit`.
- Endpoint requires JWT Bearer token.
- Endpoint accepts no userId.
- Current user comes only from `CurrentUserPrincipal` / SecurityContext.
- Code execution goes through Piston only.
- Backend must never execute user code locally.
- Run-only endpoint `/api/problems/{problemId}/run` remains unchanged, does not persist, and does not award XP.
- Code submissions history endpoint is now implemented separately as authenticated `GET /api/problems/{problemId}/submissions`.
- AI code review is now implemented separately as authenticated raw-code `POST /api/ai/review-code`.
- Frontend code editor/submit UI is not implemented yet.


## Backend Code Submissions History / Fetch Foundation Manual Test Commands
Use these after the backend code submissions history task `e823982 feat: add code submission history endpoint`.

This is a backend-only feature, so manual verification should be done from PowerShell/API checks, not browser UI.

### Automated verification
```powershell
cd C:\Users\hp\Desktop\CodeQuestFinalProject\backend
.\mvnw.cmd test
cd ..
```

Expected after this feature:
```text
Tests run: 241
Failures: 0
Errors: 0
BUILD SUCCESS
```

### Scope checks
```powershell
cd C:\Users\hp\Desktop\CodeQuestFinalProject

git status --short
git diff --stat
git diff -- frontend
git diff -- backend/pom.xml
git diff -- backend/src/main/resources/db/migration
git diff -- docs
git diff -- CodeQuest_Build_Log.md
git diff -- backend/src/main/java/com/codequest/ai
git diff -- backend/src/main/java/com/codequest/auth
git diff -- backend/src/main/java/com/codequest/course
git diff -- backend/src/main/java/com/codequest/level
git diff -- backend/src/main/java/com/codequest/progress
git diff -- backend/src/main/java/com/codequest/user
git diff -- backend/src/main/java/com/codequest/quiz
git diff -- backend/src/main/java/com/codequest/flashcard
git diff -- backend/src/main/java/com/codequest/note
git diff -- backend/src/main/java/com/codequest/leaderboard
```

Expected:
```text
Only backend problem module source/DTO/test files changed before commit.
Frontend diff is empty.
backend/pom.xml diff is empty.
DB migration diff is empty.
Docs and Build Log diffs are empty during implementation.
AI/auth/course/level/progress/user/quiz/flashcard/note/leaderboard diffs are empty.
```

### Backend env/run
Start backend with local PostgreSQL/JWT env vars.

```powershell
cd C:\Users\hp\Desktop\CodeQuestFinalProject

$env:DATABASE_URL="jdbc:postgresql://localhost:5432/codequest"
$env:DATABASE_USERNAME="postgres"
$env:DATABASE_PASSWORD="<your-local-postgres-password>"
$env:JWT_SECRET="dev-only-change-this-secret-dev-only-change-this-secret"
$env:PISTON_BASE_URL="https://emkc.org/api/v2/piston"

cd backend
.\mvnw.cmd spring-boot:run
```

Expected:
```text
Tomcat started on port 8080.
Started CodeQuestApplication.
```

### Manual verification summary
Manual verification for this feature should confirm:
- user 1 sees only user 1 submissions for the requested problem
- user 2 sees only user 2 submissions for the same problem
- user 1's other-problem submissions are hidden
- results are newest-first
- empty history returns 200 with empty `items`
- pagination works for `page` and `size`
- invalid pagination returns safe 400 ErrorDTO
- no-token request returns 401
- response safety checks do not expose sensitive/internal fields


## Backend AI Code Review Foundation Manual Test Commands
Use these after the backend AI code review task `b682c40 feat: add ai code review endpoint`.

This is a backend-only feature. Use PowerShell. Use Maven Wrapper only. Do not use plain `mvn`.

### Automated verification
```powershell
cd C:\Users\hp\Desktop\CodeQuestFinalProject\backend
.\mvnw.cmd test
cd ..
```

Expected:
- `BUILD SUCCESS`
- Backend tests pass with 272 tests.

### Scope checks
```powershell
cd C:\Users\hp\Desktop\CodeQuestFinalProject

git status --short
git diff --stat
git diff -- frontend
git diff -- frontend/package.json
git diff -- frontend/package-lock.json
git diff -- backend/pom.xml
git diff -- backend/src/main/resources/db/migration
git diff -- backend/src/main/java/com/codequest/problem
git diff -- backend/src/main/java/com/codequest/auth
git diff -- backend/src/main/java/com/codequest/course
git diff -- backend/src/main/java/com/codequest/level
git diff -- backend/src/main/java/com/codequest/progress
git diff -- backend/src/main/java/com/codequest/user
git diff -- backend/src/main/java/com/codequest/quiz
git diff -- backend/src/main/java/com/codequest/flashcard
git diff -- backend/src/main/java/com/codequest/note
git diff -- backend/src/main/java/com/codequest/leaderboard
git diff -- docs
git diff -- CodeQuest_Build_Log.md
```

Expected before commit:
- `git diff --stat` shows only AI module files/tests plus minimal common exception files.
- All forbidden-area diff commands return empty output.
- In particular, `backend/src/main/java/com/codequest/problem` must be empty because AI review MVP is raw-code return-only and does not touch submissions.

Expected after commit:
- `git status --short` is clean.

### Backend env/run
Terminal 1:
```powershell
cd C:\Users\hp\Desktop\CodeQuestFinalProject

$env:DATABASE_URL="jdbc:postgresql://localhost:5432/codequest"
$env:DATABASE_USERNAME="postgres"
$env:DATABASE_PASSWORD="<your-local-postgres-password>"
$env:JWT_SECRET="dev-only-change-this-secret-dev-only-change-this-secret"
$env:GEMINI_API_KEY="<your-local-gemini-key>"
$env:GEMINI_MODEL="gemini-1.5-flash"
$env:GEMINI_BASE_URL="https://generativelanguage.googleapis.com"

cd backend
.\mvnw.cmd spring-boot:run
```

Expected:
- Backend starts on port 8080.
- If Gemini config/service/quota is unavailable, the review endpoint should return safe 503 instead of crashing.

### Manual PowerShell API verification
Terminal 2:
```powershell
cd C:\Users\hp\Desktop\CodeQuestFinalProject
$baseUrl = "http://localhost:8080"

$email = "ai_review_$([guid]::NewGuid().ToString('N').Substring(0,8))@example.com"
$password = "StrongPass123"

$registerBody = @{
  name = "AI Review Tester"
  email = $email
  password = $password
} | ConvertTo-Json

Invoke-RestMethod `
  -Uri "$baseUrl/api/auth/register" `
  -Method Post `
  -ContentType "application/json" `
  -Body $registerBody

$loginBody = @{
  email = $email
  password = $password
} | ConvertTo-Json

$login = Invoke-RestMethod `
  -Uri "$baseUrl/api/auth/login" `
  -Method Post `
  -ContentType "application/json" `
  -Body $loginBody

$headers = @{ Authorization = "Bearer $($login.accessToken)" }

$reviewBody = @{
  language = "java"
  problemTitle = "Binary Search"
  problemDescription = "Given a sorted array and target, return the index of target or -1."
  code = "public class Main { public static int search(int[] nums, int target) { int left = 0, right = nums.length - 1; while (left <= right) { int mid = (left + right) / 2; if (nums[mid] == target) return mid; if (nums[mid] < target) left = mid + 1; else right = mid - 1; } return -1; } }"
} | ConvertTo-Json

try {
  $review = Invoke-RestMethod `
    -Uri "$baseUrl/api/ai/review-code" `
    -Method Post `
    -Headers $headers `
    -ContentType "application/json" `
    -Body $reviewBody

  $review
} catch {
  $_.Exception.Response.StatusCode.value__
  $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
  $reader.ReadToEnd()
}
```

Expected when Gemini is available:
- 200 response.
- Response contains only safe structured fields:
  - `timeComplexity`
  - `spaceComplexity`
  - `correctnessIssues`
  - `improvements`
  - `betterApproach`
  - `encouragement`

Expected when Gemini is unavailable/missing/quota-limited:
- Safe ErrorDTO instead of crash.
- Verified during this feature: 503 `AI_SERVICE_UNAVAILABLE` with message `AI review service is currently unavailable. Please try again later.`
- No raw Gemini body.
- No raw prompt.
- No stack trace.

Invalid language:
```powershell
$badLanguageBody = @{
  language = "ruby"
  code = "puts 'hello'"
} | ConvertTo-Json

try {
  Invoke-WebRequest `
    -Uri "$baseUrl/api/ai/review-code" `
    -Method Post `
    -Headers $headers `
    -ContentType "application/json" `
    -Body $badLanguageBody `
    -UseBasicParsing
} catch {
  $_.Exception.Response.StatusCode.value__
  $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
  $reader.ReadToEnd()
}
```

Expected:
- 400 safe ErrorDTO.
- Message: `Language must be one of: java, python, javascript, cpp.`

Blank code:
```powershell
$blankCodeBody = @{
  language = "java"
  code = " "
} | ConvertTo-Json

try {
  Invoke-WebRequest `
    -Uri "$baseUrl/api/ai/review-code" `
    -Method Post `
    -Headers $headers `
    -ContentType "application/json" `
    -Body $blankCodeBody `
    -UseBasicParsing
} catch {
  $_.Exception.Response.StatusCode.value__
  $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
  $reader.ReadToEnd()
}
```

Expected:
- 400 safe ErrorDTO.

No-token:
```powershell
try {
  Invoke-WebRequest `
    -Uri "$baseUrl/api/ai/review-code" `
    -Method Post `
    -ContentType "application/json" `
    -Body $reviewBody `
    -UseBasicParsing
} catch {
  $_.Exception.Response.StatusCode.value__
}
```

Expected:
- 401.

Safety check after a successful `$review` response:
```powershell
$reviewJson = $review | ConvertTo-Json -Depth 10

$reviewJson.Contains("userId")
$reviewJson.Contains("password")
$reviewJson.Contains("passwordHash")
$reviewJson.Contains("password_hash")
$reviewJson.Contains("token")
$reviewJson.Contains("accessToken")
$reviewJson.Contains("refreshToken")
$reviewJson.Contains("tokenHash")
$reviewJson.Contains("role")
$reviewJson.Contains("secret")
$reviewJson.Contains("correctAnswer")
$reviewJson.Contains("hidden")
$reviewJson.Contains("stdin")
$reviewJson.Contains("expectedOutput")
$reviewJson.Contains("rawPrompt")
$reviewJson.Contains("rawGemini")
$reviewJson.Contains("stackTrace")
$reviewJson.Contains("java.lang")
$reviewJson.Contains("org.springframework")
```

Expected:
- all `False`.

Manual verification result recorded for this feature:
- Live Gemini review success was not confirmed because Gemini returned safe unavailable behavior.
- Safe 503 unavailable behavior was confirmed.
- Invalid language 400, blank code 400, no-token 401, and backend tests passed.
| 2026-06-09 | Backend Leaderboard REST Foundation | Start backend with PostgreSQL/JWT env vars -> register/login Alpha, Bravo, Charlie -> local verification-only SQL updates Alpha XP 500/CODER/streak 5, Bravo XP 300/BEGINNER/streak 2, Charlie XP 100/BEGINNER/streak 1 -> authenticated GET `/api/leaderboard?page=0&size=50&period=ALL_TIME` -> pagination `page=0&size=1` and `page=1&size=1` -> invalid page -1 -> invalid size 0 -> invalid size 51 -> invalid period WEEKLY -> no-token request -> response safety check | Main leaderboard returned 200 with XP-desc order, global 1-based rank positions, and Bravo `currentUser` at rankPosition 3; pagination returned rank positions 1 and 2 while preserving currentUser; invalid page/size/period returned safe 400 ErrorDTO messages; no-token returned 401; safety check returned false for email, password fields, token fields, refresh tokens, tokenHash, role, secret, lastLogin, `org.springframework`, and `java.lang`. | Passed |
| 2026-06-09 | Backend Docker Setup Foundation | Start Docker Desktop -> confirm `docker info` server output -> run `docker build -t codequest-backend:local ./backend` -> handle initial CRLF `mvnw` failure -> fix Dockerfile with line-ending normalization -> rerun Docker build -> run `docker images codequest-backend` -> run `git status --short` and diff safety checks | Docker Desktop engine was running; initial build failed with `env: $'bash\r': No such file or directory`; after Dockerfile fix, Docker build completed successfully, image was named `docker.io/library/codequest-backend:local`, `docker images codequest-backend` showed `codequest-backend:local` present with image ID `4afd74688965`; scope stayed limited to `backend/Dockerfile` and `backend/.dockerignore`; no secrets were committed and no application logic changed. | Passed |


## Next Chat Prompt
Use this prompt in the next chat if this one becomes slow:

```text
You are continuing the CodeQuest project. This is a Java 21 + Spring Boot + React + PostgreSQL AI-assisted Java learning MVP. Do not restart from scratch. Read the latest CodeQuest_Build_Log.md first, then inspect AGENTS.md and the docs files.

Current repo state:
- Branch: main
- Latest feature commit: 52db876 feat: add frontend code submit ui
- Previous docs commit: f1e75b1 docs: record frontend code runner ui
- Previous feature commit: f7b4598 feat: add frontend code runner ui
- Latest completed feature: Frontend Code Submit UI Foundation
- Build Log docs update after Frontend Code Submit UI may still need a docs commit if CodeQuest_Build_Log.md is modified.

Latest completed feature:
Frontend Code Submit UI Foundation:
- Added authenticated `submitCode(problemId, payload)` frontend helper in `frontend/src/services/courseApi.js`.
- Extended the existing DashboardShell Code Runner section in `frontend/src/pages/DashboardShell.jsx`.
- Uses POST `/api/problems/{problemId}/submit`.
- Uses existing Bearer token pattern.
- Reuses the same problem id, language, code, stdin, and expected output fields as Code Runner.
- Keeps Run Code behavior unchanged.
- Keeps run result/error state and submit result/error state separate.
- Submit requires expected output before calling backend.
- Validates blank problem id, blank code, blank expected output, and code length over 20000 characters.
- Shows submit loading, safe error, and safe result states.
- Renders stdout/stderr/output as plain text inside safe blocks.
- Displays safe submit fields only: language, passed status, stdout, stderr, output, exitCode, runtimeMs, xpAwarded, firstAccepted, and safe message.
- Attempts profile XP refresh through the existing shared profile refresh callback only when `xpAwarded > 0`.
- If profile refresh fails, the submit result remains visible and only a safe refresh-failed message is shown.
- Does not display accessToken, refreshToken, password, role, tokenHash, userId, raw backend JSON, raw Piston internals, hidden tests, correctAnswer, stack traces, stdin/expectedOutput from backend internals, or secrets.
- Does not store code/stdin/expected output/results in localStorage or sessionStorage.
- Frontend build passed.
- Browser verification passed for validation and safe Piston-unavailable path using UUID-style problem id `11111111-1111-1111-1111-111111111111`; UI showed `Code runner is currently unavailable. Please try again later.` safely after Submit Code.
- Commit pushed: `52db876 feat: add frontend code submit ui`.

Previous latest frontend feature:
Frontend Code Runner UI Foundation:
- Commit `f7b4598 feat: add frontend code runner ui`.
- Added `runCode(problemId, payload)` helper and DashboardShell Code Runner section using POST `/api/problems/{problemId}/run`.

Important completed backend features already available:
- Backend Piston run-code endpoint exists: POST `/api/problems/{problemId}/run`.
- Backend code submit endpoint exists: POST `/api/problems/{problemId}/submit`.
- Backend code submissions history endpoint exists: GET `/api/problems/{problemId}/submissions?page=0&size=20`.
- Backend AI code review endpoint exists: POST `/api/ai/review-code`.
- Backend leaderboard endpoint exists and frontend leaderboard UI exists.
- Backend Docker image foundation exists.

Recommended next feature:
Frontend Code Submission History UI Foundation.

Suggested next scope:
- Frontend-only.
- Use existing backend GET `/api/problems/{problemId}/submissions?page=0&size=20`.
- Add a small submission history area near Code Runner/Submit Code.
- Manual load only, no auto-fetch.
- Show only authenticated user's own safe submission history fields returned by backend.
- No backend changes.
- No migrations.
- No package changes.
- No Monaco.
- No AI review UI in the same task.
- No Build Log/docs update during Codex implementation.

Rules:
- Use Maven Wrapper only for backend commands; never plain `mvn`.
- For frontend-only tasks, run `cd frontend && npm run build`.
- Do not commit until build, manual browser verification, and diff checks pass.
- Update CodeQuest_Build_Log.md after the feature commit, as a separate docs commit.
```

## New Chat Continuation Summary Template
Copy this into a new chat if this chat becomes slow:

```text
CodeQuest continuation summary:

We are building CodeQuest, a Java 21 + Spring Boot + React + PostgreSQL AI-assisted Java learning MVP. Continue from the latest CodeQuest_Build_Log.md and project resources. Do not restart from scratch. MVP first only. One feature per task. Use Maven Wrapper only; never use plain mvn.

Latest repo state:
- Branch: main
- Latest feature commit: 52db876 feat: add frontend code submit ui
- Previous docs commit: f1e75b1 docs: record frontend code runner ui
- Previous feature commit: f7b4598 feat: add frontend code runner ui
- Latest completed feature: Frontend Code Submit UI Foundation
- Build Log docs update after Frontend Code Submit UI may still need a docs commit if CodeQuest_Build_Log.md is modified.

Latest completed feature:
Frontend Code Submit UI Foundation:
- Added authenticated `submitCode(problemId, payload)` helper.
- Extended DashboardShell Code Runner UI with a separate Submit Code flow.
- Uses POST `/api/problems/{problemId}/submit`.
- Uses existing Bearer token pattern.
- Reuses problem id, language, code, stdin, and expected output fields.
- Keeps Run Code behavior unchanged and keeps run/submit results separate.
- Requires expected output before submit.
- Validates blank problem id, blank code, blank expected output, and code length over 20000 characters.
- Shows submit loading/error/result states.
- Shows safe submit fields: language, passed status, stdout, stderr, output, exitCode, runtimeMs, xpAwarded, firstAccepted, and safe message.
- Attempts profile refresh only when `xpAwarded > 0`.
- Safe Piston-unavailable path was browser verified and showed `Code runner is currently unavailable. Please try again later.` after Submit Code.
- Does not show tokens/passwords/roles/token hashes/userId/raw backend JSON/raw Piston internals/hidden tests/correctAnswer/stack traces/secrets.
- Does not store code/stdin/expected output/results in localStorage/sessionStorage.
- Does not implement submissions history UI, AI review UI, Monaco editor, coding problem browsing, backend changes, DB migrations, package changes, CI/CD, deployment, or Phase 2.
- Frontend build passed.
- Commit pushed: `52db876 feat: add frontend code submit ui`.

Previous latest feature:
Frontend Code Runner UI Foundation:
- Commit `f7b4598 feat: add frontend code runner ui`.
- Dashboard Code Runner uses authenticated POST `/api/problems/{problemId}/run`.
- Manual run only; no persistence/XP.

Important completed backend features already available:
- Backend Piston run-code endpoint exists: POST `/api/problems/{problemId}/run`.
- Backend code submit endpoint exists: POST `/api/problems/{problemId}/submit`.
- Backend code submissions history endpoint exists: GET `/api/problems/{problemId}/submissions?page=0&size=20`.
- Backend AI code review endpoint exists: POST `/api/ai/review-code`.
- Backend leaderboard endpoint exists: GET `/api/leaderboard?page=0&size=50&period=ALL_TIME`.
- Backend Docker image foundation exists.

Current remaining planned items:
- [ ] Frontend Code Submission History UI
- [ ] Frontend AI Code Review UI
- [ ] Monaco Editor Integration
- [ ] Dashboard UI polish / section organization
- [ ] CI/CD
- [ ] Deployment
- [ ] README
- [ ] Screenshots
- [ ] Demo video
- [ ] Resume bullets updated

Recommended next feature:
Frontend Code Submission History UI Foundation.

Expected next feature scope:
- Frontend-only.
- Add authenticated helper for GET `/api/problems/{problemId}/submissions?page=0&size=20`.
- Add manual-load submission history UI near Code Runner/Submit Code.
- Show safe fields only.
- Do not add backend changes, migrations, package changes, Monaco, AI review UI, CI/CD, deployment, or docs during implementation.
- Run `cd frontend && npm run build`.
- Manually verify in browser.
- Commit feature first, then update Build Log in a separate docs commit.
```

## Latest Safe Continuation Notes
- Latest feature commit pushed to main: `52db876 feat: add frontend code submit ui`.
- Previous docs commit on main: `f1e75b1 docs: record frontend code runner ui`.
- Previous feature commit on main: `f7b4598 feat: add frontend code runner ui`.
- Frontend Code Submit UI Foundation is the latest completed feature.
- Frontend Code Submit UI Foundation changed only `frontend/src/pages/DashboardShell.jsx` and `frontend/src/services/courseApi.js`.
- `submitCode(problemId, payload)` calls authenticated POST `/api/problems/{problemId}/submit`.
- DashboardShell Code Runner section now has both Run Code and Submit Code controls.
- Submit Code reuses the current problem id, language, code, stdin, and expected output fields.
- Submit Code requires expected output before calling backend.
- Submit Code does not auto-submit.
- Submit Code does not fetch submission history.
- Submit Code does not call AI review.
- Submit Code does not add Monaco editor.
- Submit Code validates blank problem id, blank code, blank expected output, and code over 20000 characters.
- Submit Code shows loading, safe error, and safe result states.
- Submit Code outputs render as plain text only.
- Submit Code displays safe fields only: language, passed status, stdout, stderr, output, exitCode, runtimeMs, xpAwarded, firstAccepted, and safe message.
- Submit Code attempts to refresh the profile only if `xpAwarded > 0`.
- Browser verification passed for validation and safe Piston-unavailable path using `11111111-1111-1111-1111-111111111111` as problem id.
- Safe user-facing unavailable message observed after Submit Code: `Code runner is currently unavailable. Please try again later.`
- UI does not show access tokens, refresh tokens, passwords, roles, token hashes, userId, raw backend JSON, raw Piston internals, hidden tests, correctAnswer, stdin/expectedOutput from backend internals, stack traces, or secrets.
- Frontend build passed after the feature.
- Backend Piston availability is external and may return safe 503; this is acceptable when shown safely.
- Frontend Code Runner UI Foundation remains implemented and committed as `f7b4598 feat: add frontend code runner ui`.
- Frontend Leaderboard UI Foundation remains implemented and committed as `1bb5159 feat: add frontend leaderboard ui`.
- Backend Docker Setup Foundation remains implemented and committed as `bc321df chore: add backend dockerfile`.
- Build Log docs update after Frontend Code Submit UI should be committed separately before starting the next feature.
- Recommended next implementation feature after docs commit: Frontend Code Submission History UI Foundation.
- Use Maven Wrapper only for backend tests; never use plain `mvn`.
- For frontend-only work, run `cd frontend && npm run build`.
- Do not start CI/CD/deployment until the visible frontend integrations for code submission history and AI review are considered, unless the user explicitly chooses DevOps next.
