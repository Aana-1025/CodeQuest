# CodeQuest Build Log

## Purpose
This file solves the long-chat slowdown problem. Update it manually after every feature so a fresh ChatGPT/Codex chat can continue from the current state without needing the full conversation history.

## Current Status
Phase: MVP
Current module: Foundation
Current feature: Project setup
Last completed feature: Project setup
Next feature: Backend health endpoint
Test status: Backend Maven Wrapper test PASS, Frontend build PASS

## Completed Features
- [x] Project setup

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
- AGENTS.md and the CodeQuest resource docs are the source of truth for Codex.

## Current Source of Truth Files
- CodeQuest_AI_Control_Master_Blueprint_v3.docx: full master blueprint.
- CodeQuest_Core_Rules.docx / .md: always-paste AI-control rules.
- CodeQuest_DB_Schema.docx / .md: database rules and schema.
- CodeQuest_API_Contracts.docx / .md: endpoint contracts and examples.
- CodeQuest_Feature_Prompts.docx / .md: prompt bank for Codex tasks.
- AGENTS.md: repo-root AI instructions for Codex.
- CodeQuest_Build_Log.docx / .md: current progress and next task memory.

## Bugs / Issues
- None yet.

## Feature History
1 | 2026-05-03 | Project setup | Foundation | backend/frontend skeleton, Maven Wrapper, docs cleanup | Backend PASS, Frontend PASS | pending commit

## Test Results Log
2026-05-03 | cd backend && .\mvnw.cmd test | PASS | - | -
2026-05-03 | cd frontend && npm run build | PASS | - | -

## Next Chat Prompt
Paste this into a fresh ChatGPT Project chat whenever the current chat becomes slow or confusing:

```text
Read the project resources and this Build Log.
Continue CodeQuest from the current status.
Do not redesign anything.
Do not implement Phase 2 features.
Tell me the next safest MVP task.
Give me one strict Codex prompt for that task only.
Include exact files to touch, files not to touch, commands to run, and what to update in this Build Log after completion.
```

## Update Protocol After Every Feature
1. Update Current Status: phase, current module, last completed feature, next feature, latest commit, and test status.
2. Tick the completed feature only after code compiles and manual testing is done.
3. Add a Feature History row with files changed, tests, and commit message.
4. Add bugs to Bugs / Issues immediately. Do not hide failing tests.
5. Paste the next exact task into Next Chat Prompt before starting a new chat.
6. If Codex made assumptions, record them in Feature History or Bugs / Issues.

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
- [ ] Manual test steps are documented.
- [ ] Build Log is updated.
- [ ] Commit is created with a clear message.

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
Current module: [UPDATE]
Last completed feature: [UPDATE]
Tests passed: [UPDATE]
Known bugs: [UPDATE]
Next task: [UPDATE]
Rules: Follow master blueprint, Core Rules, DB Schema, API Contracts, Feature Prompts, and AGENTS.md. Do not redesign anything. Do not add Phase 2 features.
```
