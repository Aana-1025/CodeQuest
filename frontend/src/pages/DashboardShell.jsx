import { useState } from "react";

import { generateCourse } from "../services/courseApi";
import { getAccessToken } from "../utils/tokenStorage";

const INITIAL_FORM = {
  topic: "",
  difficulty: "BEGINNER",
  goal: "",
};

export default function DashboardShell({ profile, onBackHome }) {
  const [form, setForm] = useState(INITIAL_FORM);
  const [generationLoading, setGenerationLoading] = useState(false);
  const [generationError, setGenerationError] = useState("");
  const [generatedCourse, setGeneratedCourse] = useState(null);

  const handleGenerateCourse = async (event) => {
    event.preventDefault();

    const trimmedTopic = form.topic.trim();
    if (!trimmedTopic) {
      setGenerationError("Topic is required.");
      return;
    }

    const accessToken = getAccessToken();
    if (!accessToken) {
      setGenerationError("Access token is missing.");
      return;
    }

    setGenerationLoading(true);
    setGenerationError("");

    try {
      const response = await generateCourse({
        accessToken,
        topic: trimmedTopic,
        difficulty: form.difficulty,
        goal: form.goal.trim(),
      });
      setGeneratedCourse(response);
    } catch (error) {
      setGenerationError(error.message || "Failed to generate course.");
    } finally {
      setGenerationLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-50">
      {/* Header */}
      <div className="border-b border-slate-200 bg-white px-4 py-6 sm:px-8">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-semibold text-slate-900">Dashboard</h1>
            <p className="mt-1 text-sm text-slate-600">Your Java learning command center.</p>
          </div>
          {typeof onBackHome === "function" && (
            <button
              onClick={onBackHome}
              className="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-900 transition hover:bg-slate-50"
            >
              Back to Home
            </button>
          )}
        </div>
      </div>

      {/* Main content */}
      <div className="px-4 py-8 sm:px-8">
        <div className="max-w-4xl mx-auto space-y-6">
          {/* Profile summary card */}
          <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
            <h2 className="text-xl font-semibold text-slate-900 mb-4">Profile Summary</h2>
            {profile ? (
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <p className="text-sm text-slate-600">Name</p>
                  <p className="text-lg font-semibold text-slate-900">{profile.name}</p>
                </div>
                <div>
                  <p className="text-sm text-slate-600">Email</p>
                  <p className="text-lg font-semibold text-slate-900">{profile.email}</p>
                </div>
                <div>
                  <p className="text-sm text-slate-600">Rank</p>
                  <p className="text-lg font-semibold text-slate-900">{profile.rank}</p>
                </div>
                <div>
                  <p className="text-sm text-slate-600">XP</p>
                  <p className="text-lg font-semibold text-slate-900">{profile.xp}</p>
                </div>
                <div>
                  <p className="text-sm text-slate-600">Streak</p>
                  <p className="text-lg font-semibold text-slate-900">{profile.streak}</p>
                </div>
              </div>
            ) : (
              <p className="text-slate-600">Profile not loaded yet.</p>
            )}
          </div>

          {/* Course generation */}
          <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
            <h2 className="mb-4 text-xl font-semibold text-slate-900">Generate Course</h2>

            <form className="space-y-4" onSubmit={handleGenerateCourse}>
              <div>
                <label htmlFor="topic" className="mb-2 block text-sm font-medium text-slate-700">
                  Topic
                </label>
                <input
                  id="topic"
                  type="text"
                  value={form.topic}
                  onChange={(event) => setForm((current) => ({ ...current, topic: event.target.value }))}
                  placeholder="Binary Search"
                  className="w-full rounded-xl border border-slate-300 px-4 py-3 text-sm text-slate-900 outline-none transition focus:border-slate-500 focus:ring-2 focus:ring-slate-200"
                />
              </div>

              <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
                <div>
                  <label htmlFor="difficulty" className="mb-2 block text-sm font-medium text-slate-700">
                    Difficulty
                  </label>
                  <select
                    id="difficulty"
                    value={form.difficulty}
                    onChange={(event) => setForm((current) => ({ ...current, difficulty: event.target.value }))}
                    className="w-full rounded-xl border border-slate-300 px-4 py-3 text-sm text-slate-900 outline-none transition focus:border-slate-500 focus:ring-2 focus:ring-slate-200"
                  >
                    <option value="BEGINNER">BEGINNER</option>
                    <option value="INTERMEDIATE">INTERMEDIATE</option>
                    <option value="ADVANCED">ADVANCED</option>
                  </select>
                </div>

                <div>
                  <label htmlFor="goal" className="mb-2 block text-sm font-medium text-slate-700">
                    Goal (Optional)
                  </label>
                  <input
                    id="goal"
                    type="text"
                    value={form.goal}
                    onChange={(event) => setForm((current) => ({ ...current, goal: event.target.value }))}
                    placeholder="DSA interview preparation"
                    className="w-full rounded-xl border border-slate-300 px-4 py-3 text-sm text-slate-900 outline-none transition focus:border-slate-500 focus:ring-2 focus:ring-slate-200"
                  />
                </div>
              </div>

              {generationError && (
                <div
                  className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800"
                  role="alert"
                >
                  {generationError}
                </div>
              )}

              <button
                type="submit"
                disabled={generationLoading}
                className="rounded-xl bg-slate-900 px-5 py-3 text-sm font-semibold text-white transition hover:bg-slate-700 disabled:cursor-not-allowed disabled:bg-slate-400"
              >
                {generationLoading ? "Generating..." : "Generate Course"}
              </button>
            </form>

            {generatedCourse && (
              <div className="mt-6 rounded-2xl border border-slate-200 bg-slate-50 p-5">
                <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
                  <div>
                    <h3 className="text-lg font-semibold text-slate-900">{generatedCourse.title}</h3>
                    <p className="mt-1 text-sm text-slate-600">{generatedCourse.description}</p>
                    <p className="mt-2 text-xs text-slate-500">Course ID: {generatedCourse.courseId}</p>
                  </div>
                  <span className="inline-flex w-fit rounded-full border border-slate-300 bg-white px-3 py-1 text-xs font-semibold text-slate-700">
                    {generatedCourse.cacheHit ? "Cache Hit" : "New Placeholder Course"}
                  </span>
                </div>

                <div className="mt-5 grid grid-cols-1 gap-4 md:grid-cols-3">
                  {generatedCourse.levels.map((level) => (
                    <div key={level.levelId} className="rounded-xl border border-slate-200 bg-white p-4 shadow-sm">
                      <div className="flex items-center justify-between gap-3">
                        <span className="text-sm font-semibold text-slate-500">Level {level.orderNumber}</span>
                        <span
                          className={`rounded-full px-2.5 py-1 text-xs font-semibold ${
                            level.isBoss
                              ? "bg-amber-100 text-amber-800"
                              : "bg-slate-100 text-slate-700"
                          }`}
                        >
                          {level.isBoss ? "Boss" : "Standard"}
                        </span>
                      </div>
                      <h4 className="mt-3 text-base font-semibold text-slate-900">{level.title}</h4>
                      <p className="mt-2 text-sm text-slate-600">XP Reward: {level.xpReward}</p>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>

          {/* Course progress placeholder */}
          <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
            <h2 className="text-xl font-semibold text-slate-900 mb-4">Course Progress</h2>
            <div className="bg-slate-50 rounded-lg px-4 py-6 text-center">
              <p className="text-slate-600">Generated course progress details will appear here later.</p>
            </div>
          </div>

          {/* Next actions placeholder */}
          <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
            <h2 className="text-xl font-semibold text-slate-900 mb-4">Next Actions</h2>
            <div className="bg-slate-50 rounded-lg px-4 py-6 text-center">
              <p className="text-slate-600">Lessons, quizzes, flashcards, and code practice will be added in later tasks.</p>
            </div>
          </div>

          {/* Status note */}
          <div className="rounded-2xl border border-amber-200 bg-amber-50 p-6 shadow-sm">
            <p className="text-sm text-amber-800">
              <strong>Dashboard shell:</strong> Placeholder course generation is now wired to the backend foundation. AI, lessons, quizzes, XP, streak, leaderboard, and code execution are still not implemented yet.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
