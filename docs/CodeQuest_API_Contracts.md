**CODEQUEST**

**API Contracts Resource**

*Endpoints + Request/Response Examples Only*

Derived from CodeQuest_AI_Control_Master_Blueprint_v3.docx \| MVP-first \| Java Spring Boot API

| **Purpose:** This document is a compact API-only resource for ChatGPT/Codex. It contains the exact CodeQuest endpoint catalog and request/response contract examples. It intentionally excludes product strategy, database deep-dive, frontend pages, deployment details, and interview content so it can be pasted into feature-specific coding chats without context overload. |
|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|

# 1. API Contract Rules

- API endpoints must match this document exactly unless the user explicitly changes the contract.

- Request and response structures must not change silently.

- Use GET only for read-only requests without a request body.

- Use POST for creation, actions, AI generation, submissions, code execution, or large request payloads.

- Use PATCH for partial updates and DELETE for deletion endpoints if added later.

- All protected endpoints require Authorization: Bearer \<access_token\>.

- Protected endpoints must derive the logged-in user from JWT; do not accept userId in the request body for current-user actions.

- Validate request bodies with Jakarta Bean Validation annotations in DTOs.

- All list endpoints must support pagination with a maximum page size of 50.

- Never return password_hash, refresh token hash, JWT secrets, API keys, or internal secrets.

- All error responses must use the standard ErrorDTO format.

- Swagger/OpenAPI annotations must be updated whenever an endpoint is implemented or changed.

# 2. Standard API Envelope and Error DTO

The source blueprint allows either consistent success/error envelopes or consistent DTO responses. For implementation consistency, CodeQuest should use clear DTO responses for success and the following standard ErrorDTO for failures.

{  
"timestamp": "2026-05-02T10:00:00Z",  
"status": 429,  
"code": "RATE_LIMITED",  
"message": "You have reached the course generation limit. Try again later.",  
"path": "/api/courses/generate",  
"requestId": "req_abc123"  
}

| **HTTP Status**           | **Meaning**                   | **When to use**                                     |
|---------------------------|-------------------------------|-----------------------------------------------------|
| 200 OK                    | Successful read/action        | GET success, login success, submit success          |
| 201 Created               | Resource created              | Register, create course, create note if new         |
| 400 Bad Request           | Validation failed             | Invalid DTO fields, invalid enum, invalid option    |
| 401 Unauthorized          | No/invalid auth               | Missing token, expired access token without refresh |
| 403 Forbidden             | Authenticated but not allowed | Ownership failure, role restriction                 |
| 404 Not Found             | Resource missing              | Course/level/problem not found                      |
| 409 Conflict              | Duplicate/conflict            | Email already exists, already enrolled              |
| 429 Too Many Requests     | Rate limit exceeded           | AI generation/code execution spam                   |
| 500 Internal Server Error | Unexpected backend failure    | Unhandled internal error                            |
| 502 Bad Gateway           | External service bad response | Malformed Gemini/Piston response                    |
| 503 Service Unavailable   | Dependency unavailable        | DB down, Piston unavailable, temporary outage       |

# 3. Endpoint Catalog

| **Method** | **Endpoint**                     | **Description**                          | **Auth**                                    | **MVP?** |
|------------|----------------------------------|------------------------------------------|---------------------------------------------|----------|
| POST       | /api/auth/register               | Register new user                        | Public                                      | Yes      |
| POST       | /api/auth/login                  | Login and receive access token           | Public                                      | Yes      |
| POST       | /api/auth/refresh                | Refresh access token                     | Refresh cookie/token                        | Yes      |
| POST       | /api/auth/logout                 | Revoke refresh token                     | Authenticated/refresh                       | Yes      |
| GET        | /api/user/profile                | Get current user profile and stats       | Authenticated                               | Yes      |
| PATCH      | /api/user/profile                | Update goal/name/avatar                  | Authenticated                               | Yes      |
| POST       | /api/courses/generate            | Generate or fetch AI course              | Authenticated                               | Yes      |
| GET        | /api/courses/{courseId}          | Get course with levels                   | Authenticated or public if course is public | Yes      |
| GET        | /api/courses/public              | Discover public courses                  | Authenticated optional/public               | Yes      |
| POST       | /api/courses/{courseId}/enroll   | Enroll logged-in user in a course        | Authenticated                               | Yes      |
| GET        | /api/levels/{levelId}            | Get lesson, flashcards, quiz, problems   | Authenticated                               | Yes      |
| POST       | /api/levels/{levelId}/complete   | Complete level and award lesson XP       | Authenticated                               | Yes      |
| POST       | /api/quizzes/{levelId}/submit    | Submit quiz answers                      | Authenticated                               | Yes      |
| POST       | /api/problems/{problemId}/run    | Run code through Piston                  | Authenticated                               | Yes      |
| POST       | /api/problems/{problemId}/submit | Submit solution and award XP if accepted | Authenticated                               | Yes      |
| POST       | /api/ai/review-code              | Gemini code review                       | Authenticated                               | Yes      |
| POST       | /api/ai/explain-error            | Gemini runtime error explanation         | Authenticated                               | Yes      |
| POST       | /api/notes                       | Save note                                | Authenticated                               | Yes      |
| GET        | /api/leaderboard                 | Top users by XP                          | Authenticated or public read                | Yes      |
| GET        | /api/daily-challenge             | Today challenge                          | Public or authenticated                     | Yes      |
| WS         | /ws/leaderboard                  | Real-time leaderboard                    | Authenticated                               | Phase 2  |
| WS         | /ws/study-room/{id}              | Study room chat                          | Authenticated                               | Phase 2  |
| POST       | /api/duels/create                | Create quiz duel link                    | Authenticated                               | Phase 2  |

# 4. MVP Endpoint Request/Response Examples

### 4.1 Register User

| **Field** | **Value**                                                          |
|-----------|--------------------------------------------------------------------|
| Method    | POST                                                               |
| Endpoint  | /api/auth/register                                                 |
| Purpose   | Create a new student account and return initial auth/session data. |
| Auth      | Public                                                             |

#### Request Example

{  
"name": "Antara",  
"email": "antara@example.com",  
"password": "StrongPass123"  
}

#### Success Response Example

{  
"userId": "uuid",  
"name": "Antara",  
"email": "antara@example.com",  
"accessToken": "jwt-token",  
"rank": "BEGINNER",  
"xp": 0  
}

#### Common Error Responses

400 VALIDATION_ERROR - invalid name/email/password  
409 EMAIL_ALREADY_EXISTS - email already registered

### 4.2 Login User

| **Field** | **Value**                                                             |
|-----------|-----------------------------------------------------------------------|
| Method    | POST                                                                  |
| Endpoint  | /api/auth/login                                                       |
| Purpose   | Authenticate a user and return access token plus basic profile state. |
| Auth      | Public                                                                |

#### Request Example

{  
"email": "antara@example.com",  
"password": "StrongPass123"  
}

#### Success Response Example

{  
"userId": "uuid",  
"name": "Antara",  
"email": "antara@example.com",  
"accessToken": "jwt-token",  
"rank": "BEGINNER",  
"xp": 0,  
"streak": 1  
}

#### Common Error Responses

400 VALIDATION_ERROR - invalid email/password format  
401 INVALID_CREDENTIALS - password or email is wrong  
429 RATE_LIMITED - too many login attempts

### 4.3 Refresh Access Token

| **Field** | **Value**                                                         |
|-----------|-------------------------------------------------------------------|
| Method    | POST                                                              |
| Endpoint  | /api/auth/refresh                                                 |
| Purpose   | Issue a new short-lived access token using a valid refresh token. |
| Auth      | Refresh token cookie or refresh token mechanism                   |

#### Request Example

{}

#### Success Response Example

{  
"accessToken": "new-jwt-token",  
"expiresInMinutes": 15  
}

#### Common Error Responses

401 REFRESH_TOKEN_INVALID - missing/expired/revoked refresh token

### 4.4 Logout

| **Field** | **Value**                                 |
|-----------|-------------------------------------------|
| Method    | POST                                      |
| Endpoint  | /api/auth/logout                          |
| Purpose   | Revoke the current refresh token/session. |
| Auth      | Authenticated or refresh token mechanism  |

#### Request Example

{}

#### Success Response Example

{  
"message": "Logged out successfully"  
}

#### Common Error Responses

401 UNAUTHORIZED - missing/invalid token

### 4.5 Get Current User Profile

| **Field** | **Value**                                                     |
|-----------|---------------------------------------------------------------|
| Method    | GET                                                           |
| Endpoint  | /api/user/profile                                             |
| Purpose   | Return current logged-in user profile and gamification stats. |
| Auth      | Authenticated                                                 |

#### Request Example

No request body.

#### Success Response Example

{  
"userId": "uuid",  
"name": "Antara",  
"email": "antara@example.com",  
"goal": "JAVA_BACKEND",  
"xp": 580,  
"rank": "CODER",  
"streak": 4,  
"avatarUrl": "https://res.cloudinary.com/.../avatar.png",  
"completedLevels": 6,  
"activeCourses": 2  
}

#### Common Error Responses

401 UNAUTHORIZED - missing/invalid token

### 4.6 Update Current User Profile

| **Field** | **Value**                                              |
|-----------|--------------------------------------------------------|
| Method    | PATCH                                                  |
| Endpoint  | /api/user/profile                                      |
| Purpose   | Update editable profile fields for the logged-in user. |
| Auth      | Authenticated                                          |

#### Request Example

{  
"name": "Antara Utane",  
"goal": "JAVA_BACKEND",  
"avatarUrl": "https://res.cloudinary.com/.../avatar.png"  
}

#### Success Response Example

{  
"userId": "uuid",  
"name": "Antara Utane",  
"email": "antara@example.com",  
"goal": "JAVA_BACKEND",  
"avatarUrl": "https://res.cloudinary.com/.../avatar.png",  
"xp": 580,  
"rank": "CODER",  
"streak": 4  
}

#### Common Error Responses

400 VALIDATION_ERROR - invalid name/goal/avatar URL  
401 UNAUTHORIZED - missing/invalid token

### 4.7 Generate or Fetch Course

| **Field** | **Value**                                                                      |
|-----------|--------------------------------------------------------------------------------|
| Method    | POST                                                                           |
| Endpoint  | /api/courses/generate                                                          |
| Purpose   | Generate a structured AI course or return an existing cached/generated course. |
| Auth      | Authenticated                                                                  |

#### Request Example

{  
"topic": "Binary Search",  
"difficulty": "BEGINNER",  
"goal": "DSA interview preparation"  
}

#### Success Response Example

{  
"courseId": "uuid",  
"title": "Binary Search",  
"description": "A beginner-friendly course on binary search.",  
"cacheHit": false,  
"levels": \[  
{  
"levelId": "uuid",  
"title": "What is Binary Search?",  
"orderNumber": 1,  
"isBoss": false,  
"xpReward": 50  
}  
\]  
}

#### Common Error Responses

400 VALIDATION_ERROR - invalid topic/difficulty  
429 RATE_LIMITED - AI generation limit reached  
502 AI_PARSE_ERROR - Gemini returned malformed JSON  
503 AI_UNAVAILABLE - Gemini unavailable and no cache exists

### 4.8 Get Course With Levels

| **Field** | **Value**                                    |
|-----------|----------------------------------------------|
| Method    | GET                                          |
| Endpoint  | /api/courses/{courseId}                      |
| Purpose   | Get a course and its ordered level map data. |
| Auth      | Authenticated or public if course is public  |

#### Request Example

No request body.

#### Success Response Example

{  
"courseId": "uuid",  
"title": "Binary Search",  
"description": "A beginner-friendly course on binary search.",  
"difficulty": "BEGINNER",  
"totalXp": 500,  
"isPublic": true,  
"levels": \[  
{  
"levelId": "uuid",  
"title": "What is Binary Search?",  
"orderNumber": 1,  
"isBoss": false,  
"xpReward": 50,  
"status": "UNLOCKED"  
}  
\]  
}

#### Common Error Responses

404 COURSE_NOT_FOUND - courseId does not exist  
403 FORBIDDEN - private course not owned/enrolled

### 4.9 Discover Public Courses

| **Field** | **Value**                                                         |
|-----------|-------------------------------------------------------------------|
| Method    | GET                                                               |
| Endpoint  | /api/courses/public?page=0&size=20&difficulty=BEGINNER&topic=java |
| Purpose   | Browse public courses with pagination and optional filters.       |
| Auth      | Public or authenticated                                           |

#### Request Example

No request body.

#### Success Response Example

{  
"page": 0,  
"size": 20,  
"totalElements": 42,  
"totalPages": 3,  
"items": \[  
{  
"courseId": "uuid",  
"title": "Java OOP",  
"description": "Learn classes, objects, inheritance, and polymorphism.",  
"difficulty": "BEGINNER",  
"totalXp": 650,  
"levelCount": 8  
}  
\]  
}

#### Common Error Responses

400 VALIDATION_ERROR - invalid page/size/difficulty  
Maximum page size must be 50.

### 4.10 Enroll in Course

| **Field** | **Value**                                                             |
|-----------|-----------------------------------------------------------------------|
| Method    | POST                                                                  |
| Endpoint  | /api/courses/{courseId}/enroll                                        |
| Purpose   | Enroll the logged-in user in an existing public or accessible course. |
| Auth      | Authenticated                                                         |

#### Request Example

{}

#### Success Response Example

{  
"courseId": "uuid",  
"enrolled": true,  
"startedAt": "2026-05-02T10:00:00Z",  
"nextLevelId": "uuid"  
}

#### Common Error Responses

404 COURSE_NOT_FOUND - courseId does not exist  
409 ALREADY_ENROLLED - user is already enrolled  
403 FORBIDDEN - course is private/inaccessible

### 4.11 Get Level Content

| **Field** | **Value**                                                                        |
|-----------|----------------------------------------------------------------------------------|
| Method    | GET                                                                              |
| Endpoint  | /api/levels/{levelId}                                                            |
| Purpose   | Get lesson content, flashcards, quiz questions, and coding problems for a level. |
| Auth      | Authenticated                                                                    |

#### Request Example

No request body.

#### Success Response Example

{  
"levelId": "uuid",  
"courseId": "uuid",  
"title": "What is Binary Search?",  
"contentMarkdown": "# Binary Search\nBinary search works on sorted arrays...",  
"orderNumber": 1,  
"isBoss": false,  
"xpReward": 50,  
"flashcards": \[  
{"flashcardId": "uuid", "front": "What is the condition for binary search?", "back": "The search space must be sorted or monotonic."}  
\],  
"quiz": \[  
{"questionId": "uuid", "question": "Binary search needs...", "options": {"A":"Sorted data","B":"Random data","C":"Only strings","D":"No condition"}}  
\],  
"problems": \[  
{"problemId": "uuid", "title": "Find target index", "difficulty": "EASY"}  
\]  
}

#### Common Error Responses

404 LEVEL_NOT_FOUND - levelId does not exist  
403 LEVEL_LOCKED - previous level not completed

### 4.12 Complete Level

| **Field** | **Value**                                       |
|-----------|-------------------------------------------------|
| Method    | POST                                            |
| Endpoint  | /api/levels/{levelId}/complete                  |
| Purpose   | Mark a level complete and award lesson XP once. |
| Auth      | Authenticated                                   |

#### Request Example

{}

#### Success Response Example

{  
"levelId": "uuid",  
"completed": true,  
"xpAwarded": 50,  
"newTotalXp": 630,  
"newRank": "CODER",  
"nextLevelUnlocked": true,  
"nextLevelId": "uuid"  
}

#### Common Error Responses

403 LEVEL_LOCKED - level is not available yet  
409 ALREADY_COMPLETED - no duplicate XP awarded  
404 LEVEL_NOT_FOUND - levelId does not exist

### 4.13 Submit Quiz

| **Field** | **Value**                                                                           |
|-----------|-------------------------------------------------------------------------------------|
| Method    | POST                                                                                |
| Endpoint  | /api/quizzes/{levelId}/submit                                                       |
| Purpose   | Submit quiz answers, calculate score, detect weak concepts, and award quiz XP once. |
| Auth      | Authenticated                                                                       |

#### Request Example

{  
"answers": \[  
{"questionId": "uuid", "selectedOption": "B"}  
\]  
}

#### Success Response Example

{  
"score": 80,  
"correctAnswers": 4,  
"totalQuestions": 5,  
"xpAwarded": 80,  
"newTotalXp": 580,  
"newRank": "CODER",  
"weakConcepts": \["loop invariants", "mid calculation"\]  
}

#### Common Error Responses

400 VALIDATION_ERROR - selectedOption must be A/B/C/D  
404 LEVEL_NOT_FOUND - levelId does not exist  
409 QUIZ_ALREADY_SUBMITTED - no duplicate XP awarded

### 4.14 Run Code

| **Field** | **Value**                                                                                  |
|-----------|--------------------------------------------------------------------------------------------|
| Method    | POST                                                                                       |
| Endpoint  | /api/problems/{problemId}/run                                                              |
| Purpose   | Run user code through Piston against provided or visible sample input without awarding XP. |
| Auth      | Authenticated                                                                              |

#### Request Example

{  
"language": "java",  
"code": "public class Main { public static void main(String\[\] args) { System.out.println(42); } }",  
"stdin": ""  
}

#### Success Response Example

{  
"executed": true,  
"stdout": "42\n",  
"stderr": "",  
"exitCode": 0,  
"runtimeMs": 120,  
"memoryKb": 22000  
}

#### Common Error Responses

400 VALIDATION_ERROR - invalid language or code too long  
503 CODE_RUNNER_UNAVAILABLE - Piston API unavailable

### 4.15 Submit Code Solution

| **Field** | **Value**                                                                                 |
|-----------|-------------------------------------------------------------------------------------------|
| Method    | POST                                                                                      |
| Endpoint  | /api/problems/{problemId}/submit                                                          |
| Purpose   | Run all visible/hidden tests, store submission, and award XP for first accepted solution. |
| Auth      | Authenticated                                                                             |

#### Request Example

{  
"language": "java",  
"code": "public class Main { public static void main(String\[\] args) { ... } }"  
}

#### Success Response Example

{  
"passed": true,  
"passedTestCases": 5,  
"totalTestCases": 5,  
"runtimeMs": 130,  
"memoryKb": 24000,  
"xpAwarded": 100,  
"aiReview": {  
"timeComplexity": "O(log n)",  
"spaceComplexity": "O(1)",  
"improvements": \["Handle empty input", "Avoid overflow in mid calculation", "Use clearer variable names"\]  
}  
}

#### Common Error Responses

400 VALIDATION_ERROR - invalid language/code length  
404 PROBLEM_NOT_FOUND - problemId does not exist  
503 CODE_RUNNER_UNAVAILABLE - Piston unavailable  
409 XP_ALREADY_AWARDED - accepted earlier, submission stored but no duplicate XP

### 4.16 AI Review Code

| **Field** | **Value**                                         |
|-----------|---------------------------------------------------|
| Method    | POST                                              |
| Endpoint  | /api/ai/review-code                               |
| Purpose   | Ask Gemini for educational feedback on user code. |
| Auth      | Authenticated                                     |

#### Request Example

{  
"language": "java",  
"problemTitle": "Find target index",  
"problemDescription": "Given a sorted array and target, return the target index or -1.",  
"code": "public class Main { ... }"  
}

#### Success Response Example

{  
"timeComplexity": "O(log n)",  
"spaceComplexity": "O(1)",  
"correctnessIssues": \[\],  
"improvements": \[  
"Use left + (right - left) / 2 to avoid overflow.",  
"Handle empty arrays explicitly."  
\],  
"betterApproach": "The binary search approach is already optimal for sorted input.",  
"encouragement": "Good job using a logarithmic search strategy."  
}

#### Common Error Responses

400 VALIDATION_ERROR - invalid/missing code fields  
429 RATE_LIMITED - AI review limit reached  
502 AI_PARSE_ERROR - Gemini returned invalid structure

### 4.17 Explain Runtime Error

| **Field** | **Value**                                                                    |
|-----------|------------------------------------------------------------------------------|
| Method    | POST                                                                         |
| Endpoint  | /api/ai/explain-error                                                        |
| Purpose   | Ask Gemini to explain a runtime/compile error in beginner-friendly language. |
| Auth      | Authenticated                                                                |

#### Request Example

{  
"language": "java",  
"code": "public class Main { public static void main(String\[\] args) { int x = 1 / 0; } }",  
"stderr": "Exception in thread "main" java.lang.ArithmeticException: / by zero",  
"problemTitle": "Simple Java Program"  
}

#### Success Response Example

{  
"errorType": "ArithmeticException",  
"simpleExplanation": "Your code tried to divide a number by zero, which Java does not allow.",  
"likelyCause": "The divisor became 0 at runtime.",  
"fixSuggestions": \[  
"Check the value before division.",  
"Add an if condition to avoid dividing by zero."  
\]  
}

#### Common Error Responses

400 VALIDATION_ERROR - missing stderr/code  
429 RATE_LIMITED - AI explanation limit reached  
502 AI_PARSE_ERROR - Gemini returned invalid structure

### 4.18 Save Note

| **Field** | **Value**                                 |
|-----------|-------------------------------------------|
| Method    | POST                                      |
| Endpoint  | /api/notes                                |
| Purpose   | Create or update a user note for a level. |
| Auth      | Authenticated                             |

#### Request Example

{  
"levelId": "uuid",  
"content": "Remember: binary search only works on sorted or monotonic search spaces."  
}

#### Success Response Example

{  
"noteId": "uuid",  
"levelId": "uuid",  
"content": "Remember: binary search only works on sorted or monotonic search spaces.",  
"updatedAt": "2026-05-02T10:00:00Z"  
}

#### Common Error Responses

400 VALIDATION_ERROR - content too long/empty  
403 FORBIDDEN - level not accessible to user  
404 LEVEL_NOT_FOUND - levelId does not exist

### 4.19 Get Leaderboard

| **Field** | **Value**                                       |
|-----------|-------------------------------------------------|
| Method    | GET                                             |
| Endpoint  | /api/leaderboard?page=0&size=50&period=ALL_TIME |
| Purpose   | Return top users sorted by XP.                  |
| Auth      | Authenticated or public read                    |

#### Request Example

No request body.

#### Success Response Example

{  
"page": 0,  
"size": 50,  
"period": "ALL_TIME",  
"items": \[  
{"rankPosition": 1, "userId": "uuid", "name": "Antara", "xp": 25000, "rank": "LEGEND", "streak": 20}  
\],  
"currentUser": {"rankPosition": 12, "userId": "uuid", "xp": 580, "rank": "CODER"}  
}

#### Common Error Responses

400 VALIDATION_ERROR - invalid page/size/period  
Maximum page size must be 50.

### 4.20 Get Daily Challenge

| **Field** | **Value**                         |
|-----------|-----------------------------------|
| Method    | GET                               |
| Endpoint  | /api/daily-challenge              |
| Purpose   | Return today's challenge problem. |
| Auth      | Public or authenticated           |

#### Request Example

No request body.

#### Success Response Example

{  
"challengeDate": "2026-05-02",  
"problemId": "uuid",  
"title": "Binary Search Warmup",  
"description": "Find the target index in a sorted array.",  
"difficulty": "EASY",  
"xpReward": 150,  
"alreadyCompleted": false  
}

#### Common Error Responses

404 DAILY_CHALLENGE_NOT_FOUND - no active challenge configured

# 5. Phase 2 Endpoint Contracts

### 5.1 Real-Time Leaderboard WebSocket

| **Field** | **Value**                                          |
|-----------|----------------------------------------------------|
| Method    | WS                                                 |
| Endpoint  | /ws/leaderboard                                    |
| Purpose   | Subscribe to leaderboard updates after XP changes. |
| Auth      | Authenticated WebSocket session                    |

#### Request Example

Client subscribes to: /topic/leaderboard  
Client may send no request body for read-only updates.

#### Success Response Example

{  
"eventType": "LEADERBOARD_UPDATED",  
"updatedAt": "2026-05-02T10:00:00Z",  
"topUsers": \[  
{"rankPosition": 1, "userId": "uuid", "name": "Antara", "xp": 25000, "rank": "LEGEND"}  
\]  
}

#### Common Error Responses

401 UNAUTHORIZED - invalid WebSocket auth  
Fallback: REST polling through GET /api/leaderboard.

### 5.2 Study Room WebSocket

| **Field** | **Value**                                        |
|-----------|--------------------------------------------------|
| Method    | WS                                               |
| Endpoint  | /ws/study-room/{id}                              |
| Purpose   | Phase 2 study room chat and participant updates. |
| Auth      | Authenticated WebSocket session                  |

#### Request Example

Client sends message example:  
{  
"type": "CHAT_MESSAGE",  
"content": "Can someone explain binary search mid calculation?"  
}

#### Success Response Example

{  
"type": "CHAT_MESSAGE",  
"roomId": "uuid",  
"senderName": "Antara",  
"content": "Can someone explain binary search mid calculation?",  
"sentAt": "2026-05-02T10:00:00Z"  
}

#### Common Error Responses

401 UNAUTHORIZED - invalid WebSocket auth  
404 ROOM_NOT_FOUND - room does not exist

### 5.3 Create Quiz Duel

| **Field** | **Value**                               |
|-----------|-----------------------------------------|
| Method    | POST                                    |
| Endpoint  | /api/duels/create                       |
| Purpose   | Create a Phase 2 quiz duel invite link. |
| Auth      | Authenticated                           |

#### Request Example

{  
"levelId": "uuid",  
"opponentEmail": "friend@example.com"  
}

#### Success Response Example

{  
"duelId": "uuid",  
"inviteUrl": "https://codequest.app/duel/uuid",  
"status": "PENDING",  
"createdAt": "2026-05-02T10:00:00Z"  
}

#### Common Error Responses

404 LEVEL_NOT_FOUND - levelId does not exist  
403 FORBIDDEN - level not accessible to current user  
400 VALIDATION_ERROR - invalid opponent email

# 6. Feature-Specific API Context Template

Paste this template into ChatGPT/Codex when implementing an endpoint. Replace bracketed fields with the endpoint details from this document.

Feature name:  
\[FEATURE_NAME\]  
  
Exact endpoint:  
\[METHOD\] /api/...  
  
Auth:  
\[Public / Authenticated / Phase 2\]  
  
Request DTO:  
\[fields + validation\]  
  
Response DTO:  
\[fields\]  
  
Business rules:  
\[rules from source blueprint\]  
  
Error cases:  
\[400/401/403/404/409/429/502/503 as applicable\]  
  
Implementation constraints:  
- Controller must only handle request/response.  
- Service must contain business logic and ownership checks.  
- Repository must only handle database access.  
- Do not expose JPA entities.  
- Update Swagger/OpenAPI annotations.  
- Add at least one meaningful backend test.  
- Do not modify unrelated endpoints.
