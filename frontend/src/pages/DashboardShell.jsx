import { useEffect, useState } from "react";

import { generateCourse, getCourseById } from "../services/courseApi";
import { getAccessToken } from "../utils/tokenStorage";

const INITIAL_FORM = {
  topic: "",
  difficulty: "BEGINNER",
  goal: "",
};

function getCourseBadgeLabel(generatedCourse) {
  if (generatedCourse.cacheHit) {
    return "Cache Hit";
  }

  if (generatedCourse.sourceType === "AI") {
    return "AI Generated Course";
  }

  if (generatedCourse.sourceType === "PLACEHOLDER") {
    return "New Placeholder Course";
  }

  return "New Course";
}

function getLevelTypeBadgeClass(level) {
  return level.isBoss
    ? "bg-amber-100 text-amber-800"
    : "bg-slate-100 text-slate-700";
}

function getContentPreview(contentMarkdown) {
  if (!contentMarkdown) {
    return "No lesson preview available yet.";
  }

  const plainText = toPlainText(contentMarkdown);

  if (!plainText) {
    return "No lesson preview available yet.";
  }

  return plainText.length <= 140 ? plainText : `${plainText.slice(0, 140).trim()}...`;
}

function toPlainText(contentMarkdown) {
  if (!contentMarkdown) {
    return "";
  }

  return contentMarkdown
    .replace(/\[(.*?)\]\((.*?)\)/g, "$1")
    .replace(/[#*_`>-]/g, " ")
    .replace(/\s+/g, " ")
    .trim();
}

function normalizeQuizQuestions(level) {
  const quizCandidates = [level?.quiz, level?.quizzes, level?.quizQuestions];
  const quizSource = quizCandidates.find(Array.isArray);

  if (!quizSource) {
    return [];
  }

  return quizSource.filter((question) => question && typeof question === "object");
}

function normalizeFlashcards(level) {
  const flashcardCandidates = [level?.flashcards, level?.cards, level?.reviewCards];
  const flashcardSource = flashcardCandidates.find(Array.isArray);

  if (!flashcardSource) {
    return [];
  }

  return flashcardSource.filter((card) => card && typeof card === "object");
}

function getOptionLabel(index) {
  return ["A", "B", "C", "D"][index] || String(index + 1);
}

function getQuestionText(question) {
  if (!question || typeof question !== "object") {
    return "Question text is not available yet.";
  }

  const text = question.question ?? question.prompt;

  return typeof text === "string" && text.trim() ? text.trim() : "Question text is not available yet.";
}

function getOptionText(option) {
  if (typeof option === "string" || typeof option === "number") {
    return String(option);
  }

  if (!option || typeof option !== "object") {
    return "Option not available";
  }

  const text = option.text ?? option.label ?? option.value ?? option.content;

  return typeof text === "string" && text.trim() ? text.trim() : "Option not available";
}

function getFlashcardFront(card) {
  if (!card || typeof card !== "object") {
    return "Flashcard front is not available yet.";
  }

  const front = card.front ?? card.question ?? card.term;

  return typeof front === "string" && front.trim() ? front.trim() : "Flashcard front is not available yet.";
}

function getFlashcardBack(card) {
  if (!card || typeof card !== "object") {
    return "Flashcard answer is not available yet.";
  }

  const back = card.back ?? card.answer ?? card.definition;

  return typeof back === "string" && back.trim() ? back.trim() : "Flashcard answer is not available yet.";
}

export default function DashboardShell({ profile, onBackHome }) {
  const [form, setForm] = useState(INITIAL_FORM);
  const [generationLoading, setGenerationLoading] = useState(false);
  const [generationError, setGenerationError] = useState("");
  const [generatedCourse, setGeneratedCourse] = useState(null);
  const [courseMapLoading, setCourseMapLoading] = useState(false);
  const [courseMapError, setCourseMapError] = useState("");
  const [courseMap, setCourseMap] = useState(null);
  const [selectedLevel, setSelectedLevel] = useState(null);
  const [quizSelections, setQuizSelections] = useState({});
  const [revealedFlashcards, setRevealedFlashcards] = useState({});

  useEffect(() => {
    setQuizSelections({});
    setRevealedFlashcards({});
  }, [selectedLevel?.levelId, selectedLevel?.orderNumber, selectedLevel?.title]);

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
      setCourseMap(null);
      setCourseMapError("");
      setSelectedLevel(null);
    } catch (error) {
      setGenerationError(error.message || "Failed to generate course.");
    } finally {
      setGenerationLoading(false);
    }
  };

  const handleOpenCourseMap = async () => {
    if (!generatedCourse?.courseId) {
      return;
    }

    const accessToken = getAccessToken();
    if (!accessToken) {
      setCourseMapError("Access token is missing.");
      return;
    }

    setCourseMapLoading(true);
    setCourseMapError("");

    try {
      const response = await getCourseById({
        accessToken,
        courseId: generatedCourse.courseId,
      });
      setCourseMap(response);
      setSelectedLevel(null);
    } catch (error) {
      setCourseMapError(error.message || "Failed to load course map.");
    } finally {
      setCourseMapLoading(false);
    }
  };

  const handleBackToDashboard = () => {
    setCourseMapError("");
    setCourseMap(null);
    setSelectedLevel(null);
  };

  const handleOpenLesson = (level) => {
    setSelectedLevel(level);
  };

  const handleBackToCourseMap = () => {
    setSelectedLevel(null);
  };

  const handleQuizOptionSelect = (questionIndex, optionIndex) => {
    setQuizSelections((current) => ({
      ...current,
      [questionIndex]: optionIndex,
    }));
  };

  const handleFlashcardToggle = (cardIndex) => {
    setRevealedFlashcards((current) => ({
      ...current,
      [cardIndex]: !current[cardIndex],
    }));
  };

  if (courseMap && selectedLevel) {
    const quizQuestions = normalizeQuizQuestions(selectedLevel);
    const flashcards = normalizeFlashcards(selectedLevel);

    return (
      <div className="min-h-screen bg-slate-50">
        <div className="border-b border-slate-200 bg-white px-4 py-6 sm:px-8">
          <div className="flex items-center justify-between gap-4">
            <div>
              <h1 className="text-3xl font-semibold text-slate-900">Lesson</h1>
              <p className="mt-1 text-sm text-slate-600">Read through the selected level and prepare for the next step.</p>
            </div>
            <div className="flex items-center gap-3">
              <button
                onClick={handleBackToCourseMap}
                className="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-900 transition hover:bg-slate-50"
              >
                Back to Course Map
              </button>
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
        </div>

        <div className="px-4 py-8 sm:px-8">
          <div className="mx-auto max-w-4xl space-y-6">
            <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
              <p className="text-sm text-slate-500">{courseMap.title}</p>
              <div className="mt-3 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
                <div>
                  <p className="text-sm font-semibold text-slate-500">Level {selectedLevel.orderNumber}</p>
                  <h2 className="mt-1 text-2xl font-semibold text-slate-900">{selectedLevel.title}</h2>
                </div>
                <span className={`inline-flex w-fit rounded-full px-3 py-1 text-xs font-semibold ${getLevelTypeBadgeClass(selectedLevel)}`}>
                  {selectedLevel.isBoss ? "Boss" : "Standard"}
                </span>
              </div>

              <div className="mt-6 grid grid-cols-1 gap-4 sm:grid-cols-2">
                <div className="rounded-xl bg-slate-50 p-4">
                  <p className="text-sm text-slate-500">XP Reward</p>
                  <p className="mt-1 text-base font-semibold text-slate-900">{selectedLevel.xpReward}</p>
                </div>
                <div className="rounded-xl bg-slate-50 p-4">
                  <p className="text-sm text-slate-500">Level Type</p>
                  <p className="mt-1 text-base font-semibold text-slate-900">{selectedLevel.isBoss ? "Boss" : "Standard"}</p>
                </div>
              </div>
            </div>

            <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
              <h3 className="text-xl font-semibold text-slate-900">Lesson Content</h3>
              <div className="mt-4 rounded-xl bg-slate-50 p-5">
                <p className="whitespace-pre-wrap text-sm leading-7 text-slate-700">
                  {toPlainText(selectedLevel.contentMarkdown) || "Lesson content is not available yet."}
                </p>
              </div>
            </div>

            <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
              <div className="flex flex-col gap-2 sm:flex-row sm:items-start sm:justify-between">
                <div>
                  <h3 className="text-xl font-semibold text-slate-900">Quiz</h3>
                  <p className="mt-1 text-sm text-slate-600">Practice quiz foundation for this lesson. Question review will expand as quiz persistence is added later.</p>
                </div>
              </div>

              {quizQuestions.length === 0 ? (
                <div className="mt-4 rounded-xl bg-slate-50 p-5">
                  <p className="text-sm text-slate-600">Quiz questions are not available for this level yet.</p>
                </div>
              ) : (
                <div className="mt-5 space-y-4">
                  {quizQuestions.map((question, questionIndex) => {
                    const options = Array.isArray(question.options) ? question.options : [];
                    const selectedOptionIndex = quizSelections[questionIndex];

                    return (
                      <div
                        key={`quiz-question-${questionIndex}`}
                        className="rounded-xl border border-slate-200 bg-slate-50 p-5"
                      >
                        <p className="text-sm font-semibold text-slate-500">Question {questionIndex + 1}</p>
                        <h4 className="mt-2 text-base font-semibold text-slate-900">{getQuestionText(question)}</h4>
                        {question.conceptTag && (
                          <p className="mt-2 text-xs font-medium uppercase tracking-wide text-slate-500">
                            Concept: {question.conceptTag}
                          </p>
                        )}

                        {options.length > 0 ? (
                          <div className="mt-4 grid grid-cols-1 gap-3">
                            {options.map((option, optionIndex) => {
                              const isSelected = selectedOptionIndex === optionIndex;

                              return (
                                <button
                                  key={`quiz-option-${questionIndex}-${optionIndex}`}
                                  type="button"
                                  onClick={() => handleQuizOptionSelect(questionIndex, optionIndex)}
                                  className={`rounded-xl border px-4 py-3 text-left text-sm transition ${
                                    isSelected
                                      ? "border-slate-900 bg-white text-slate-900"
                                      : "border-slate-300 bg-white text-slate-700 hover:bg-slate-100"
                                  }`}
                                >
                                  <span className="font-semibold">{getOptionLabel(optionIndex)}.</span>{" "}
                                  {getOptionText(option)}
                                </button>
                              );
                            })}
                          </div>
                        ) : (
                          <p className="mt-4 text-sm text-slate-600">Options are not available for this question yet.</p>
                        )}

                        {selectedOptionIndex !== undefined && question.explanation && (
                          <div className="mt-4 rounded-xl border border-slate-200 bg-white p-4">
                            <p className="text-sm font-semibold text-slate-700">Explanation</p>
                            <p className="mt-2 text-sm leading-6 text-slate-600">{question.explanation}</p>
                          </div>
                        )}
                      </div>
                    );
                  })}
                </div>
              )}
            </div>

            <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
              <div className="flex flex-col gap-2 sm:flex-row sm:items-start sm:justify-between">
                <div>
                  <h3 className="text-xl font-semibold text-slate-900">Flashcards</h3>
                  <p className="mt-1 text-sm text-slate-600">Quick review foundation for this lesson. Flashcard study flow will expand when persistence is available.</p>
                </div>
              </div>

              {flashcards.length === 0 ? (
                <div className="mt-4 rounded-xl bg-slate-50 p-5">
                  <p className="text-sm text-slate-600">Flashcards are not available for this level yet.</p>
                </div>
              ) : (
                <div className="mt-5 grid grid-cols-1 gap-4">
                  {flashcards.map((card, cardIndex) => {
                    const isRevealed = Boolean(revealedFlashcards[cardIndex]);

                    return (
                      <div
                        key={`flashcard-${cardIndex}`}
                        className="rounded-xl border border-slate-200 bg-slate-50 p-5"
                      >
                        <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
                          <div>
                            <p className="text-sm font-semibold text-slate-500">Card {cardIndex + 1}</p>
                            {card.conceptTag && (
                              <p className="mt-1 text-xs font-medium uppercase tracking-wide text-slate-500">
                                Concept: {card.conceptTag}
                              </p>
                            )}
                          </div>
                          <button
                            type="button"
                            onClick={() => handleFlashcardToggle(cardIndex)}
                            className="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-900 transition hover:bg-slate-100"
                          >
                            {isRevealed ? "Hide Answer" : "Show Answer"}
                          </button>
                        </div>

                        <div className="mt-4 rounded-xl bg-white p-4">
                          <p className="text-sm font-semibold text-slate-500">Front</p>
                          <p className="mt-2 text-sm leading-6 text-slate-700">{getFlashcardFront(card)}</p>
                        </div>

                        {isRevealed && (
                          <div className="mt-4 rounded-xl border border-slate-200 bg-white p-4">
                            <p className="text-sm font-semibold text-slate-500">Answer</p>
                            <p className="mt-2 text-sm leading-6 text-slate-700">{getFlashcardBack(card)}</p>
                          </div>
                        )}
                      </div>
                    );
                  })}
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (courseMap) {
    return (
      <div className="min-h-screen bg-slate-50">
        <div className="border-b border-slate-200 bg-white px-4 py-6 sm:px-8">
          <div className="flex items-center justify-between gap-4">
            <div>
              <h1 className="text-3xl font-semibold text-slate-900">Course Map</h1>
              <p className="mt-1 text-sm text-slate-600">Explore your generated course structure and level flow.</p>
            </div>
            <div className="flex items-center gap-3">
              <button
                onClick={handleBackToDashboard}
                className="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-900 transition hover:bg-slate-50"
              >
                Back
              </button>
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
        </div>

        <div className="px-4 py-8 sm:px-8">
          <div className="mx-auto max-w-5xl space-y-6">
            <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
              <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
                <div>
                  <h2 className="text-2xl font-semibold text-slate-900">{courseMap.title}</h2>
                  <p className="mt-2 text-sm text-slate-600">{courseMap.description}</p>
                  <p className="mt-3 text-xs text-slate-500">Course ID: {courseMap.courseId}</p>
                </div>
                <span className="inline-flex w-fit rounded-full border border-slate-300 bg-white px-3 py-1 text-xs font-semibold text-slate-700">
                  {courseMap.sourceType || "UNKNOWN"}
                </span>
              </div>

              <div className="mt-6 grid grid-cols-1 gap-4 md:grid-cols-3">
                <div className="rounded-xl bg-slate-50 p-4">
                  <p className="text-sm text-slate-500">Difficulty</p>
                  <p className="mt-1 text-base font-semibold text-slate-900">{courseMap.difficulty}</p>
                </div>
                <div className="rounded-xl bg-slate-50 p-4">
                  <p className="text-sm text-slate-500">Source Type</p>
                  <p className="mt-1 text-base font-semibold text-slate-900">{courseMap.sourceType}</p>
                </div>
                <div className="rounded-xl bg-slate-50 p-4">
                  <p className="text-sm text-slate-500">Total XP</p>
                  <p className="mt-1 text-base font-semibold text-slate-900">{courseMap.totalXp}</p>
                </div>
              </div>
            </div>

            <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
              <h3 className="text-xl font-semibold text-slate-900">Levels</h3>
              <div className="mt-5 grid grid-cols-1 gap-4 md:grid-cols-2">
                {courseMap.levels.map((level) => (
                  <div key={level.levelId ?? `${level.orderNumber}-${level.title}`} className="rounded-xl border border-slate-200 bg-slate-50 p-5">
                    <div className="flex items-center justify-between gap-3">
                      <span className="text-sm font-semibold text-slate-500">Level {level.orderNumber}</span>
                      <span className={`rounded-full px-2.5 py-1 text-xs font-semibold ${getLevelTypeBadgeClass(level)}`}>
                        {level.isBoss ? "Boss" : "Standard"}
                      </span>
                    </div>
                    <h4 className="mt-3 text-lg font-semibold text-slate-900">{level.title}</h4>
                    <p className="mt-2 text-sm text-slate-600">XP Reward: {level.xpReward}</p>
                    <p className="mt-3 text-sm leading-6 text-slate-600">{getContentPreview(level.contentMarkdown)}</p>
                    <button
                      type="button"
                      onClick={() => handleOpenLesson(level)}
                      className="mt-4 rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-900 transition hover:bg-slate-100"
                    >
                      Open Lesson
                    </button>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }

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
                    {getCourseBadgeLabel(generatedCourse)}
                  </span>
                </div>

                <div className="mt-4 flex flex-wrap items-center gap-3">
                  <button
                    type="button"
                    onClick={handleOpenCourseMap}
                    disabled={courseMapLoading || !generatedCourse.courseId}
                    className="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-900 transition hover:bg-slate-100 disabled:cursor-not-allowed disabled:bg-slate-100 disabled:text-slate-400"
                  >
                    {courseMapLoading ? "Loading Course Map..." : "Open Course Map"}
                  </button>
                  {!generatedCourse.courseId && (
                    <p className="text-sm text-slate-500">Course map becomes available after a course ID is present.</p>
                  )}
                </div>

                {courseMapError && (
                  <div
                    className="mt-4 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800"
                    role="alert"
                  >
                    {courseMapError}
                  </div>
                )}

                <div className="mt-5 grid grid-cols-1 gap-4 md:grid-cols-3">
                  {generatedCourse.levels.map((level) => (
                    <div key={level.levelId} className="rounded-xl border border-slate-200 bg-white p-4 shadow-sm">
                      <div className="flex items-center justify-between gap-3">
                        <span className="text-sm font-semibold text-slate-500">Level {level.orderNumber}</span>
                        <span className={`rounded-full px-2.5 py-1 text-xs font-semibold ${getLevelTypeBadgeClass(level)}`}>
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
