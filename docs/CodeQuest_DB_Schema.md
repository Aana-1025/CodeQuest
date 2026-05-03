**CODEQUEST**

**Database Schema Resource**

*CodeQuest_DB_Schema.md — Word Document Version*

Extracted from CodeQuest_AI_Control_Master_Blueprint_v3 \| Database-only resource for ChatGPT/Codex

# 1. Purpose and Scope

- This document is the database-only reference resource for CodeQuest.

- Use this file when working on database migrations, JPA entities, repositories, service ownership checks, API changes that touch persistence, or schema-related tests.

- This file intentionally excludes frontend UI, general product strategy, resume sections, deployment strategy, and non-database feature descriptions.

- If any AI/coding-agent instruction conflicts with this database schema file, this file and the master blueprint take priority.

# 2. Database Technology Context

| **Area**          | **CodeQuest Decision**        | **Rule**                                                                   |
|-------------------|-------------------------------|----------------------------------------------------------------------------|
| Database          | PostgreSQL on Neon            | Use PostgreSQL-compatible SQL and PostgreSQL UUID/JSONB capabilities.      |
| ORM               | Hibernate / Spring Data JPA   | Entities must map directly to schema; repositories contain only DB access. |
| Migration tool    | Flyway                        | All schema changes must be versioned migration scripts.                    |
| Primary keys      | UUID                          | All tables use UUID primary keys unless explicitly approved otherwise.     |
| Caching           | PostgreSQL first, Redis later | Generated courses are persisted in DB first; Redis is Phase 2 only.        |
| Storage awareness | Neon free plan target         | Monitor DB size because free-tier storage is limited.                      |

# 3. Non-Negotiable Database Modification Rules

- Do NOT modify existing tables without explicit instruction.

- All schema changes must be done through Flyway migration scripts.

- Do NOT remove constraints, indexes, foreign keys, unique constraints, or ownership-related relationships.

- All primary keys must be UUID unless an existing table already uses a different approved key.

- Maintain all relationships exactly as defined in this schema section.

- Never store plaintext passwords, raw refresh tokens, API keys, or secrets in the database.

- If adding a new table, include id, created_at, and updated_at where appropriate.

- If adding user-owned data, include user_id and enforce ownership checks in the service layer.

- Do not store AI-generated output without validating schema, required fields, enum values, and maximum length limits.

- If changing a DTO because of a database change, update API examples and tests too.

- Do NOT rename database tables, columns, enums, DTOs, endpoints, modules, or packages unless explicitly instructed.

- Do NOT add unnecessary fields to DTOs, entities, or database tables.

- Do NOT create duplicate repositories, entities, or schema definitions.

- Do NOT generate DB tables outside MVP unless Phase 2/3 is explicitly requested.

# 4. Entity, Repository, and Service Persistence Rules

| **Layer**          | **Database Responsibility**                                                                                                       |
|--------------------|-----------------------------------------------------------------------------------------------------------------------------------|
| Entity             | Maps directly to database schema. Contains persistence fields and relationships only; avoid heavy business logic inside entities. |
| Repository         | Only database interaction using Spring Data JPA. No business logic. No HTTP logic. No DTO mapping beyond projections when needed. |
| Service            | Owns transaction boundaries, ownership checks, validation orchestration, XP/progress rules, and calls repositories/clients.       |
| DTO                | Separate request/response shape from entity shape. Never expose entities directly.                                                |
| Mapper             | Converts entity to response DTO and request DTO to entity when needed.                                                            |
| Exception Handling | Use custom domain exceptions and handle them through GlobalExceptionHandler with standard ErrorDTO.                               |

# 5. Core Tables — Source-Aligned Table List

| **Table**        | **Purpose**                                | **Important Columns**                                                                                                                |
|------------------|--------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------|
| users            | Stores account and gamification state.     | id, name, email, password_hash, xp, rank, streak, last_login, avatar_url, goal, role, created_at, updated_at                         |
| refresh_tokens   | Stores refresh token rotation safely.      | id, user_id, token_hash, expires_at, revoked_at, created_at                                                                          |
| courses          | Stores generated or curated courses.       | id, normalized_topic, title, description, created_by, difficulty, is_public, total_xp, source_type                                   |
| levels           | Stores lessons inside a course.            | id, course_id, title, content_markdown, order_number, is_boss, xp_reward                                                             |
| quizzes          | Stores quiz questions.                     | id, level_id, question, option_a, option_b, option_c, option_d, correct_answer, explanation, concept_tag, xp_reward                  |
| flashcards       | Stores spaced-repetition style cards.      | id, level_id, front, back, order_number                                                                                              |
| coding_problems  | Stores coding challenges for levels.       | id, level_id, title, description, starter_code_json, test_cases_json, hidden_tests_json, difficulty, xp_reward                       |
| progress         | Connects user to level completion.         | id, user_id, level_id, completed, score, quiz_answers_json, completed_at                                                             |
| code_submissions | Stores every coding attempt.               | id, user_id, problem_id, language, code, passed, passed_test_cases, total_test_cases, runtime_ms, memory_kb, ai_review, submitted_at |
| notes            | Stores user notes per level.               | id, user_id, level_id, content, created_at, updated_at                                                                               |
| achievements     | Stores earned badges.                      | id, user_id, badge_name, badge_type, earned_at                                                                                       |
| daily_challenges | Stores daily practice challenge reference. | id, problem_id, challenge_date, active                                                                                               |

# 6. Relationships and Constraints

- One user can create many courses.

- One course has many levels.

- One level has many quizzes, flashcards, and coding problems.

- One progress record connects exactly one user and one level.

- Add UNIQUE(user_id, level_id) on progress to prevent duplicate completion records.

- One user can submit many code submissions for the same problem.

- XP is awarded only once for first completion/first accepted solution.

- Add UNIQUE(normalized_topic, difficulty) if generated courses are shared globally.

- Foreign keys must preserve ownership and relationship integrity.

- Do not remove or weaken relationships to make implementation easier.

# 7. Required Indexes

| **Index Target**                      | **Purpose**                                                      |
|---------------------------------------|------------------------------------------------------------------|
| users(email)                          | Fast login lookup and uniqueness enforcement.                    |
| users(xp DESC)                        | Leaderboard sorting by XP.                                       |
| courses(normalized_topic)             | Course cache lookup and topic search.                            |
| levels(course_id, order_number)       | Fetch ordered levels for a course map.                           |
| progress(user_id)                     | Fetch all progress for a user.                                   |
| progress(user_id, level_id)           | Check completion and enforce unique progress state.              |
| code_submissions(user_id, problem_id) | Fetch user attempts for a specific problem and enforce XP rules. |

# 8. Table-by-Table Implementation Notes

## users

| **Column / Constraint** | **Type / Kind**     | **Rule / Meaning**                                       |
|-------------------------|---------------------|----------------------------------------------------------|
| id                      | UUID PK             | Primary key. Use generated UUID.                         |
| name                    | VARCHAR(100)        | Display name; validate 2-100 characters.                 |
| email                   | VARCHAR(255) UNIQUE | Login email; must be unique and indexed.                 |
| password_hash           | VARCHAR(255)        | BCrypt-hashed password only; never expose in DTO.        |
| xp                      | INTEGER DEFAULT 0   | Total XP earned.                                         |
| rank                    | VARCHAR(50)         | BEGINNER, CODER, DEVELOPER, ENGINEER, ARCHITECT, LEGEND. |
| streak                  | INTEGER DEFAULT 0   | Current login streak in days.                            |
| last_login              | TIMESTAMP           | Used for streak calculation.                             |
| avatar_url              | VARCHAR(500)        | Optional profile image URL.                              |
| goal                    | VARCHAR(100)        | User goal such as Java Backend, DSA, Full Stack, AI.     |
| role                    | VARCHAR(30)         | STUDENT or ADMIN.                                        |
| created_at / updated_at | TIMESTAMP           | Audit timestamps.                                        |

## refresh_tokens

| **Column / Constraint** | **Type / Kind**      | **Rule / Meaning**                           |
|-------------------------|----------------------|----------------------------------------------|
| id                      | UUID PK              | Primary key.                                 |
| user_id                 | UUID FK -\> users.id | Refresh token owner.                         |
| token_hash              | VARCHAR/TEXT         | Store hashed refresh token, never raw token. |
| expires_at              | TIMESTAMP            | Expiry date.                                 |
| revoked_at              | TIMESTAMP nullable   | Set when token is revoked/logged out.        |
| created_at              | TIMESTAMP            | Creation time.                               |

## courses

| **Column / Constraint** | **Type / Kind**      | **Rule / Meaning**                      |
|-------------------------|----------------------|-----------------------------------------|
| id                      | UUID PK              | Course identifier.                      |
| normalized_topic        | VARCHAR              | Normalized topic used for cache lookup. |
| title                   | VARCHAR              | Course title.                           |
| description             | TEXT                 | Course overview.                        |
| created_by              | UUID FK -\> users.id | User who generated/created course.      |
| difficulty              | ENUM/VARCHAR         | BEGINNER, INTERMEDIATE, ADVANCED.       |
| is_public               | BOOLEAN              | Whether others can discover the course. |
| total_xp                | INTEGER              | Total XP available in the course.       |
| source_type             | VARCHAR              | AI_GENERATED or CURATED if used.        |

## levels

| **Column / Constraint** | **Type / Kind**        | **Rule / Meaning**                         |
|-------------------------|------------------------|--------------------------------------------|
| id                      | UUID PK                | Level identifier.                          |
| course_id               | UUID FK -\> courses.id | Parent course.                             |
| title                   | VARCHAR                | Level title.                               |
| content_markdown        | TEXT                   | Lesson content; render safely on frontend. |
| order_number            | INTEGER                | Level order inside course.                 |
| is_boss                 | BOOLEAN                | Boss level flag.                           |
| xp_reward               | INTEGER                | XP for completing level.                   |

## quizzes

| **Column / Constraint** | **Type / Kind**       | **Rule / Meaning**          |
|-------------------------|-----------------------|-----------------------------|
| id                      | UUID PK               | Quiz question identifier.   |
| level_id                | UUID FK -\> levels.id | Parent level.               |
| question                | TEXT                  | Question text.              |
| option_a/b/c/d          | VARCHAR/TEXT          | Four answer options.        |
| correct_answer          | CHAR(1)               | A/B/C/D only.               |
| explanation             | TEXT                  | Explanation for answer.     |
| concept_tag             | VARCHAR               | Weak-concept detection tag. |
| xp_reward               | INTEGER               | XP for correct answer.      |

## flashcards

| **Column / Constraint** | **Type / Kind**       | **Rule / Meaning**    |
|-------------------------|-----------------------|-----------------------|
| id                      | UUID PK               | Flashcard identifier. |
| level_id                | UUID FK -\> levels.id | Parent level.         |
| front                   | TEXT                  | Front of flashcard.   |
| back                    | TEXT                  | Back of flashcard.    |
| order_number            | INTEGER               | Card order.           |

## coding_problems

| **Column / Constraint** | **Type / Kind**       | **Rule / Meaning**              |
|-------------------------|-----------------------|---------------------------------|
| id                      | UUID PK               | Problem identifier.             |
| level_id                | UUID FK -\> levels.id | Parent level.                   |
| title                   | VARCHAR               | Problem title.                  |
| description             | TEXT                  | Problem statement.              |
| starter_code_json       | JSONB                 | Starter code by language.       |
| test_cases_json         | JSONB                 | Visible test cases.             |
| hidden_tests_json       | JSONB                 | Hidden tests for anti-cheating. |
| difficulty              | ENUM/VARCHAR          | EASY, MEDIUM, HARD.             |
| xp_reward               | INTEGER               | XP for first accepted solution. |

## progress

| **Column / Constraint**   | **Type / Kind**       | **Rule / Meaning**                     |
|---------------------------|-----------------------|----------------------------------------|
| id                        | UUID PK               | Progress record identifier.            |
| user_id                   | UUID FK -\> users.id  | Learner.                               |
| level_id                  | UUID FK -\> levels.id | Tracked level.                         |
| completed                 | BOOLEAN               | Whether level is completed.            |
| score                     | INTEGER               | Quiz score percentage 0-100.           |
| quiz_answers_json         | JSONB                 | Stored answer pattern for adaptive AI. |
| completed_at              | TIMESTAMP             | Completion timestamp.                  |
| UNIQUE(user_id, level_id) | Constraint            | Prevents duplicate progress records.   |

## code_submissions

| **Column / Constraint** | **Type / Kind**                | **Rule / Meaning**                                          |
|-------------------------|--------------------------------|-------------------------------------------------------------|
| id                      | UUID PK                        | Submission identifier.                                      |
| user_id                 | UUID FK -\> users.id           | Student who submitted code.                                 |
| problem_id              | UUID FK -\> coding_problems.id | Problem attempted.                                          |
| language                | VARCHAR                        | Allowlisted language such as java, python, javascript, cpp. |
| code                    | TEXT                           | Submitted code. Never log full code in production.          |
| passed                  | BOOLEAN                        | Whether all tests passed.                                   |
| passed_test_cases       | INTEGER                        | Number of passed tests.                                     |
| total_test_cases        | INTEGER                        | Total tests run.                                            |
| runtime_ms              | INTEGER                        | Runtime from Piston if available.                           |
| memory_kb               | INTEGER                        | Memory from Piston if available.                            |
| ai_review               | TEXT/JSONB                     | AI review output after validation.                          |
| submitted_at            | TIMESTAMP                      | Submission time.                                            |

## notes

| **Column / Constraint** | **Type / Kind**       | **Rule / Meaning**      |
|-------------------------|-----------------------|-------------------------|
| id                      | UUID PK               | Note identifier.        |
| user_id                 | UUID FK -\> users.id  | Note owner.             |
| level_id                | UUID FK -\> levels.id | Level for the note.     |
| content                 | TEXT                  | Sanitized note content. |
| created_at / updated_at | TIMESTAMP             | Audit timestamps.       |

## achievements

| **Column / Constraint** | **Type / Kind**      | **Rule / Meaning**      |
|-------------------------|----------------------|-------------------------|
| id                      | UUID PK              | Achievement identifier. |
| user_id                 | UUID FK -\> users.id | Badge owner.            |
| badge_name              | VARCHAR              | Badge display name.     |
| badge_type              | VARCHAR              | Badge category/type.    |
| earned_at               | TIMESTAMP            | Earned timestamp.       |

## daily_challenges

| **Column / Constraint** | **Type / Kind**                | **Rule / Meaning**           |
|-------------------------|--------------------------------|------------------------------|
| id                      | UUID PK                        | Daily challenge identifier.  |
| problem_id              | UUID FK -\> coding_problems.id | Challenge problem.           |
| challenge_date          | DATE                           | Date for challenge.          |
| active                  | BOOLEAN                        | Whether challenge is active. |

# 9. Ownership, Transaction, and Anti-Cheating Rules

- Protected endpoints must derive user identity from JWT, not from userId sent by frontend.

- Do NOT accept userId in request body for current-user actions.

- Every user-owned table must be protected by service-layer ownership checks.

- Use @Transactional for operations that update multiple tables, such as quiz submit, level completion, XP update, rank update, and code submission.

- Do not award XP twice for the same completed level.

- Do not award code XP twice for the same problem after first accepted submission.

- Store submission history to detect repeated copy-paste attempts.

- Use hidden tests for coding problems.

- For MVP, hidden tests can be generated manually by the developer, not by AI.

# 10. JSONB and AI-Generated Data Rules

- Use JSONB for quiz_answers_json, starter_code_json, test_cases_json, hidden_tests_json, and possibly ai_review when structured querying is useful.

- Validate AI-generated course JSON before inserting courses, levels, quizzes, flashcards, or problems into the database.

- Validate required fields, enum values, string lengths, arrays, and nested test cases before persistence.

- Do not send secrets, JWTs, passwords, or private user data to Gemini.

- Never trust AI output blindly; ResponseParser must validate schema before DB persistence.

- If AI output is malformed, retry with a stricter prompt and fail cleanly after the defined retry limit.

# 11. Flyway Migration Strategy

- All database schema changes must be represented as versioned Flyway migration scripts.

- Migration files belong in the database/migrations folder or backend resources folder depending on final project setup.

- Never edit a migration after it has been applied to a shared/permanent environment. Create a new migration instead.

- Each migration should be small, reviewable, and named by purpose.

- Migration scripts must be committed to Git along with the entity/repository/service changes that depend on them.

Recommended naming pattern: **V1\_\_create_users_table.sql, V2\_\_create_courses_and_levels.sql, V3\_\_create_quiz_problem_progress_tables.sql**

# 12. Example Source-Aligned Users Migration

CREATE TABLE users (

id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

name VARCHAR(100) NOT NULL,

email VARCHAR(255) NOT NULL UNIQUE,

password_hash VARCHAR(255) NOT NULL,

xp INTEGER NOT NULL DEFAULT 0,

rank VARCHAR(50) NOT NULL DEFAULT 'BEGINNER',

streak INTEGER NOT NULL DEFAULT 0,

last_login TIMESTAMP,

avatar_url VARCHAR(500),

goal VARCHAR(100),

role VARCHAR(30) NOT NULL DEFAULT 'STUDENT',

created_at TIMESTAMP NOT NULL DEFAULT now(),

updated_at TIMESTAMP NOT NULL DEFAULT now()

);

CREATE INDEX idx_users_xp ON users (xp DESC);

# 13. Database Context to Paste into ChatGPT/Codex

Use CodeQuest_DB_Schema as the database source of truth.

Do not modify existing tables without explicit instruction.

Use Flyway migrations for every schema change.

Use UUID primary keys.

Do not remove constraints, indexes, foreign keys, unique constraints, or ownership relationships.

Do not expose JPA entities directly in API responses.

All user-owned data must include user_id and service-layer ownership checks.

If changing a DB field affects a DTO or API response, update API examples and tests too.

Do not invent new tables or columns outside the MVP scope unless explicitly requested.

# 14. Database Feature Completion Checklist

- \[ \] Migration file created if schema changed.

- \[ \] Migration file name is versioned and purpose-based.

- \[ \] Entity maps to the table accurately.

- \[ \] Repository contains only database access.

- \[ \] Service owns ownership checks and transaction boundaries.

- \[ \] DTO/API examples updated if schema affects API.

- \[ \] Indexes and constraints preserved.

- \[ \] Sensitive fields are not exposed in responses.

- \[ \] At least one repository/service test added where logic is affected.

- \[ \] No Phase 2/3 tables added accidentally.

# 15. Source Integrity Note

This database-only resource was extracted from CodeQuest_AI_Control_Master_Blueprint_v3. It preserves the database modification rules, core tables, relationships, constraints, indexes, Flyway migration approach, PostgreSQL/Neon context, ownership rules, anti-cheating data rules, JSONB/AI persistence rules, and database-related AI-control rules from the source blueprint. Non-database sections were intentionally excluded to keep this file short enough for repeated ChatGPT/Codex use.
