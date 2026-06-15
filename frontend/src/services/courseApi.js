import { API_BASE_URL } from "./authApi";
import { getAccessToken } from "../utils/tokenStorage";

async function parseJsonResponse(response) {
  const text = await response.text();
  if (!text) {
    return null;
  }

  try {
    return JSON.parse(text);
  } catch {
    return null;
  }
}

async function handleResponse(response) {
  const data = await parseJsonResponse(response);

  if (!response.ok) {
    const message = data?.message || "Request failed. Please try again.";
    const error = new Error(message);
    error.status = response.status;
    throw error;
  }

  return data;
}

async function generateCourse({ accessToken, topic, difficulty, goal }) {
  if (!accessToken) {
    throw new Error("Access token is missing.");
  }

  const response = await fetch(`${API_BASE_URL}/api/courses/generate`, {
    method: "POST",
    headers: {
      "Authorization": `Bearer ${accessToken}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      topic,
      difficulty,
      goal: goal || null,
    }),
  });

  return handleResponse(response);
}

async function getCourseById({ accessToken, courseId }) {
  if (!accessToken) {
    throw new Error("Access token is missing.");
  }

  if (!courseId) {
    throw new Error("Course ID is missing.");
  }

  const response = await fetch(`${API_BASE_URL}/api/courses/${courseId}`, {
    method: "GET",
    headers: {
      "Authorization": `Bearer ${accessToken}`,
    },
  });

  return handleResponse(response);
}

async function getLevelDetails(levelId) {
  const accessToken = getAccessToken();

  if (!accessToken) {
    const error = new Error("Access token is missing.");
    error.status = 401;
    throw error;
  }

  if (!levelId) {
    throw new Error("Level ID is missing.");
  }

  const response = await fetch(`${API_BASE_URL}/api/levels/${levelId}`, {
    method: "GET",
    headers: {
      "Authorization": `Bearer ${accessToken}`,
    },
  });

  return handleResponse(response);
}

async function getCourseProgress(courseId) {
  const accessToken = getAccessToken();

  if (!accessToken) {
    const error = new Error("Access token is missing.");
    error.status = 401;
    throw error;
  }

  if (!courseId) {
    throw new Error("Course ID is missing.");
  }

  const response = await fetch(`${API_BASE_URL}/api/progress/courses/${courseId}`, {
    method: "GET",
    headers: {
      "Authorization": `Bearer ${accessToken}`,
    },
  });

  return handleResponse(response);
}

async function completeLevel(levelId) {
  const accessToken = getAccessToken();

  if (!accessToken) {
    const error = new Error("Access token is missing.");
    error.status = 401;
    throw error;
  }

  if (!levelId) {
    throw new Error("Level ID is missing.");
  }

  const response = await fetch(`${API_BASE_URL}/api/levels/${levelId}/complete`, {
    method: "POST",
    headers: {
      "Authorization": `Bearer ${accessToken}`,
    },
  });

  return handleResponse(response);
}

async function saveNoteForLevel({ levelId, content }) {
  const accessToken = getAccessToken();

  if (!accessToken) {
    const error = new Error("Access token is missing.");
    error.status = 401;
    throw error;
  }

  if (!levelId) {
    throw new Error("Level ID is missing.");
  }

  const response = await fetch(`${API_BASE_URL}/api/notes`, {
    method: "POST",
    headers: {
      "Authorization": `Bearer ${accessToken}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      levelId,
      content,
    }),
  });

  return handleResponse(response);
}

async function getNoteForLevel(levelId) {
  const accessToken = getAccessToken();

  if (!accessToken) {
    const error = new Error("Access token is missing.");
    error.status = 401;
    throw error;
  }

  if (!levelId) {
    throw new Error("Level ID is missing.");
  }

  const response = await fetch(`${API_BASE_URL}/api/notes/levels/${levelId}`, {
    method: "GET",
    headers: {
      "Authorization": `Bearer ${accessToken}`,
    },
  });

  if (response.status === 404) {
    return null;
  }

  return handleResponse(response);
}

async function submitQuizAnswer(quizQuestionId, selectedAnswer) {
  const accessToken = getAccessToken();

  if (!accessToken) {
    const error = new Error("Access token is missing.");
    error.status = 401;
    throw error;
  }

  if (!quizQuestionId) {
    throw new Error("Quiz question ID is missing.");
  }

  const response = await fetch(`${API_BASE_URL}/api/quizzes/${quizQuestionId}/submit`, {
    method: "POST",
    headers: {
      "Authorization": `Bearer ${accessToken}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      selectedAnswer,
    }),
  });

  return handleResponse(response);
}

async function getQuizAttemptHistory() {
  const accessToken = getAccessToken();

  if (!accessToken) {
    const error = new Error("Access token is missing.");
    error.status = 401;
    throw error;
  }

  const response = await fetch(`${API_BASE_URL}/api/quizzes/attempts`, {
    method: "GET",
    headers: {
      "Authorization": `Bearer ${accessToken}`,
    },
  });

  return handleResponse(response);
}

async function getLeaderboard() {
  const accessToken = getAccessToken();

  if (!accessToken) {
    const error = new Error("Your session may have expired. Please log in again.");
    error.status = 401;
    throw error;
  }

  const response = await fetch(`${API_BASE_URL}/api/leaderboard?page=0&size=50&period=ALL_TIME`, {
    method: "GET",
    headers: {
      "Authorization": `Bearer ${accessToken}`,
    },
  });

  const data = await parseJsonResponse(response);

  if (response.status === 401) {
    const error = new Error("Your session may have expired. Please log in again.");
    error.status = 401;
    throw error;
  }

  if (!response.ok) {
    const error = new Error(data?.message ? "Could not load leaderboard right now. Please try again." : "Could not load leaderboard right now. Please try again.");
    error.status = response.status;
    throw error;
  }

  return data;
}

async function runCode(problemId, payload) {
  const accessToken = getAccessToken();

  if (!accessToken) {
    const error = new Error("Your session may have expired. Please log in again.");
    error.status = 401;
    throw error;
  }

  const trimmedProblemId = typeof problemId === "string" ? problemId.trim() : "";

  if (!trimmedProblemId) {
    const error = new Error("Please check your code runner input and try again.");
    error.status = 400;
    throw error;
  }

  const response = await fetch(`${API_BASE_URL}/api/problems/${encodeURIComponent(trimmedProblemId)}/run`, {
    method: "POST",
    headers: {
      "Authorization": `Bearer ${accessToken}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      language: payload.language,
      code: payload.code,
      stdin: payload.stdin,
      expectedOutput: payload.expectedOutput,
    }),
  });

  const data = await parseJsonResponse(response);

  if (response.status === 401) {
    const error = new Error("Your session may have expired. Please log in again.");
    error.status = 401;
    throw error;
  }

  if (response.status === 400) {
    const error = new Error("Please check your code runner input and try again.");
    error.status = 400;
    error.code = data?.code ?? null;
    throw error;
  }

  if (response.status === 503) {
    const error = new Error("Code runner is currently unavailable. Please try again later.");
    error.status = 503;
    error.code = data?.code ?? null;
    throw error;
  }

  if (response.status === 500) {
    const error = new Error("Code runner failed safely. Please check the backend logs and try again.");
    error.status = 500;
    error.code = data?.code ?? null;
    throw error;
  }

  if (!response.ok) {
    const error = new Error("Could not run code right now. Please try again.");
    error.status = response.status;
    error.code = data?.code ?? null;
    error.data = data ?? null;
    throw error;
  }

  return data;
}

async function submitCode(problemId, payload) {
  const accessToken = getAccessToken();

  if (!accessToken) {
    const error = new Error("Your session may have expired. Please log in again.");
    error.status = 401;
    throw error;
  }

  const trimmedProblemId = typeof problemId === "string" ? problemId.trim() : "";

  if (!trimmedProblemId) {
    const error = new Error("Please check your code submit input and try again.");
    error.status = 400;
    throw error;
  }

  const response = await fetch(`${API_BASE_URL}/api/problems/${encodeURIComponent(trimmedProblemId)}/submit`, {
    method: "POST",
    headers: {
      "Authorization": `Bearer ${accessToken}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      language: payload.language,
      code: payload.code,
      stdin: payload.stdin,
      expectedOutput: payload.expectedOutput,
    }),
  });

  const data = await parseJsonResponse(response);

  if (response.status === 401) {
    const error = new Error("Your session may have expired. Please log in again.");
    error.status = 401;
    throw error;
  }

  if (response.status === 400) {
    const error = new Error("Please check your code submit input and try again.");
    error.status = 400;
    error.code = data?.code ?? null;
    throw error;
  }

  if (response.status === 503) {
    const error = new Error("Code runner is currently unavailable. Please try again later.");
    error.status = 503;
    error.code = data?.code ?? null;
    throw error;
  }

  if (!response.ok) {
    const error = new Error("Could not submit code right now. Please try again.");
    error.status = response.status;
    error.code = data?.code ?? null;
    error.data = data ?? null;
    throw error;
  }

  return data;
}

async function reviewCodeWithAi(payload) {
  const accessToken = getAccessToken();

  if (!accessToken) {
    const error = new Error("Your session may have expired. Please log in again.");
    error.status = 401;
    throw error;
  }

  const response = await fetch(`${API_BASE_URL}/api/ai/review-code`, {
    method: "POST",
    headers: {
      "Authorization": `Bearer ${accessToken}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      language: payload.language,
      code: payload.code,
      problemTitle: payload.problemTitle,
      problemDescription: payload.problemDescription,
    }),
  });

  const data = await parseJsonResponse(response);

  if (response.status === 401) {
    const error = new Error("Your session may have expired. Please log in again.");
    error.status = 401;
    throw error;
  }

  if (response.status === 400) {
    const error = new Error("Please check your AI review input and try again.");
    error.status = 400;
    throw error;
  }

  if (response.status === 502) {
    const error = new Error("AI review response was invalid. Please try again.");
    error.status = 502;
    throw error;
  }

  if (response.status === 503) {
    const error = new Error("AI review service is currently unavailable. Please try again later.");
    error.status = 503;
    throw error;
  }

  if (!response.ok) {
    const error = new Error("Could not review code right now. Please try again.");
    error.status = response.status;
    throw error;
  }

  return data;
}

async function getCodeSubmissions(problemId, page = 0, size = 20) {
  const accessToken = getAccessToken();

  if (!accessToken) {
    const error = new Error("Your session may have expired. Please log in again.");
    error.status = 401;
    throw error;
  }

  const trimmedProblemId = typeof problemId === "string" ? problemId.trim() : "";

  if (!trimmedProblemId) {
    const error = new Error("Please check the submissions history request and try again.");
    error.status = 400;
    throw error;
  }

  const safePage = Number.isInteger(page) && page >= 0 ? page : 0;
  const safeSize = size === 20 ? 20 : 20;

  const response = await fetch(
    `${API_BASE_URL}/api/problems/${encodeURIComponent(trimmedProblemId)}/submissions?page=${safePage}&size=${safeSize}`,
    {
      method: "GET",
      headers: {
        "Authorization": `Bearer ${accessToken}`,
      },
    },
  );

  const data = await parseJsonResponse(response);

  if (response.status === 401) {
    const error = new Error("Your session may have expired. Please log in again.");
    error.status = 401;
    throw error;
  }

  if (response.status === 400) {
    const error = new Error("Please check the submissions history request and try again.");
    error.status = 400;
    error.code = data?.code ?? null;
    throw error;
  }

  if (!response.ok) {
    const error = new Error("Could not load code submissions right now. Please try again.");
    error.status = response.status;
    error.code = data?.code ?? null;
    error.data = data ?? null;
    throw error;
  }

  return data;
}

export {
  generateCourse,
  getCourseById,
  getLevelDetails,
  getCourseProgress,
  completeLevel,
  saveNoteForLevel,
  getNoteForLevel,
  submitQuizAnswer,
  getQuizAttemptHistory,
  getLeaderboard,
  runCode,
  submitCode,
  reviewCodeWithAi,
  getCodeSubmissions,
};
