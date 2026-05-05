import { API_BASE_URL } from "./authApi";

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
    throw new Error(message);
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

export { generateCourse };
