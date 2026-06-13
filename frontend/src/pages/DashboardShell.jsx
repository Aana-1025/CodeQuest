import { Component, useEffect, useState } from "react";
import Editor from "@monaco-editor/react";

import {
  completeLevel,
  generateCourse,
  getCourseById,
  getCourseProgress,
  getCodeSubmissions,
  getLeaderboard,
  getNoteForLevel,
  getQuizAttemptHistory,
  reviewCodeWithAi,
  runCode,
  saveNoteForLevel,
  submitCode,
  submitQuizAnswer,
} from "../services/courseApi";
import { getAccessToken } from "../utils/tokenStorage";

const INITIAL_FORM = {
  topic: "",
  difficulty: "BEGINNER",
  goal: "",
};

const CODE_RUNNER_STARTER_CODE = {
  java: `public class Main {
    public static void main(String[] args) {
        System.out.println("Hello CodeQuest");
    }
}`,
  python: `print("Hello CodeQuest")`,
  javascript: `console.log("Hello CodeQuest");`,
  cpp: `#include <iostream>

int main() {
    std::cout << "Hello CodeQuest" << std::endl;
    return 0;
}`,
};

const MONACO_LANGUAGE_BY_CODE_RUNNER_LANGUAGE = {
  java: "java",
  python: "python",
  javascript: "javascript",
  cpp: "cpp",
};

const MONACO_EDITOR_OPTIONS = {
  minimap: { enabled: false },
  fontSize: 14,
  automaticLayout: true,
  wordWrap: "on",
  scrollBeyondLastLine: false,
};

const DEMO_PROBLEM_UUID = "11111111-1111-1111-1111-111111111111";
const UUID_PATTERN = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;

class MonacoEditorErrorBoundary extends Component {
  constructor(props) {
    super(props);

    this.state = {
      hasError: false,
    };
  }

  static getDerivedStateFromError() {
    return {
      hasError: true,
    };
  }

  render() {
    if (this.state.hasError) {
      return this.props.fallback;
    }

    return this.props.children;
  }
}

function CodeEditorField({ language, value, onChange }) {
  const monacoLanguage = MONACO_LANGUAGE_BY_CODE_RUNNER_LANGUAGE[language] ?? "plaintext";

  const fallbackEditor = (
    <div className="rounded-xl border border-slate-300 bg-slate-50 p-4">
      <p className="mb-3 text-sm text-slate-600">
        Monaco editor could not be loaded. Using the safe fallback editor instead.
      </p>
      <textarea
        id="code-runner-code"
        value={value}
        onChange={(event) => onChange(event.target.value)}
        rows={12}
        className="w-full rounded-xl border border-slate-300 px-4 py-3 text-sm text-slate-900 outline-none transition focus:border-slate-500 focus:ring-2 focus:ring-slate-200"
      />
    </div>
  );

  return (
    <MonacoEditorErrorBoundary fallback={fallbackEditor}>
      <div className="overflow-hidden rounded-xl border border-slate-300">
        <Editor
          height="360px"
          language={monacoLanguage}
          value={value}
          onChange={(nextValue) => onChange(nextValue ?? "")}
          loading={
            <div className="flex h-[360px] items-center justify-center bg-slate-50 px-4 text-sm text-slate-600">
              Loading editor...
            </div>
          }
          options={MONACO_EDITOR_OPTIONS}
        />
      </div>
    </MonacoEditorErrorBoundary>
  );
}

const INITIAL_CODE_RUNNER_FORM = {
  problemId: DEMO_PROBLEM_UUID,
  language: "java",
  code: CODE_RUNNER_STARTER_CODE.java,
  stdin: "",
  expectedOutput: "",
  problemTitle: "",
  problemDescription: "",
};

function isValidProblemUuid(problemId) {
  return UUID_PATTERN.test(problemId.trim());
}

function getMissingProblemIdMessage(action) {
  if (action === "run") {
    return "Enter a problem UUID before running code.";
  }

  if (action === "submit") {
    return "Enter a problem UUID before submitting code.";
  }

  return "Enter a problem UUID before loading submissions.";
}

function getInvalidProblemIdMessage(action) {
  if (action === "run") {
    return "Enter a valid problem UUID before running code.";
  }

  if (action === "submit") {
    return "Enter a valid problem UUID before submitting code.";
  }

  return "Enter a valid problem UUID before loading submissions.";
}

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

function normalizeQuestionOptions(question) {
  if (!question || typeof question !== "object") {
    return [];
  }

  if (Array.isArray(question.options)) {
    return question.options;
  }

  if (question.options && typeof question.options === "object") {
    return ["A", "B", "C", "D"]
      .map((label) => question.options[label])
      .filter((option) => option !== undefined && option !== null);
  }

  return [];
}

function getQuizQuestionId(question) {
  if (!question || typeof question !== "object") {
    return null;
  }

  return question.quizId ?? question.quizQuestionId ?? null;
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

function formatSavedTimestamp(timestamp) {
  if (!timestamp) {
    return "";
  }

  const date = new Date(timestamp);

  if (Number.isNaN(date.getTime())) {
    return "";
  }

  return date.toLocaleString();
}

function formatAttemptedTimestamp(timestamp) {
  if (!timestamp) {
    return "Attempt time unavailable";
  }

  const date = new Date(timestamp);

  if (Number.isNaN(date.getTime())) {
    return "Attempt time unavailable";
  }

  return date.toLocaleString();
}

function getAttemptStatusClass(isCorrect) {
  return isCorrect
    ? "border-emerald-200 bg-emerald-50 text-emerald-800"
    : "border-amber-200 bg-amber-50 text-amber-800";
}

function getLevelProgressBadge(level) {
  if (level.completed) {
    return {
      label: "Completed",
      className: "border-emerald-200 bg-emerald-50 text-emerald-800",
    };
  }

  if (level.progressAvailable && !level.unlocked) {
    return {
      label: "Locked",
      className: "border-rose-200 bg-rose-50 text-rose-800",
    };
  }

  return {
    label: "Ready",
    className: "border-sky-200 bg-sky-50 text-sky-800",
  };
}

function getLockedExplanation(level) {
  if (!level.progressAvailable || level.unlocked) {
    return "";
  }

  return level.isBoss
    ? "Complete the previous levels to unlock this boss level."
    : "Complete previous levels to unlock this level.";
}

function getProgressErrorMessage(error) {
  if (error?.status === 401) {
    return "Please log in again to load your course progress.";
  }

  if (error?.status === 404) {
    return "Could not load progress right now.";
  }

  return "Could not load progress right now.";
}

function getCompleteLevelErrorMessage(error) {
  if (error?.status === 401) {
    return "Please log in again to complete this level.";
  }

  if (error?.status === 403) {
    return "Complete previous levels before unlocking this level.";
  }

  if (error?.status === 404) {
    return "This level is no longer available.";
  }

  if (error?.status === 409) {
    return "This level was already completed.";
  }

  return "Could not complete this level right now.";
}

function getLeaderboardErrorMessage(error) {
  if (error?.status === 401) {
    return "Your session may have expired. Please log in again.";
  }

  return "Could not load leaderboard right now. Please try again.";
}

function getLeaderboardBadgeClass(rankPosition) {
  if (rankPosition === 1) {
    return "bg-amber-100 text-amber-800";
  }

  if (rankPosition === 2) {
    return "bg-slate-200 text-slate-700";
  }

  if (rankPosition === 3) {
    return "bg-orange-100 text-orange-800";
  }

  return "bg-sky-50 text-sky-700";
}

function getCodeRunnerErrorMessage(error) {
  if (error?.status === 401) {
    return "Your session may have expired. Please log in again.";
  }

  if (error?.status === 400) {
    return "Please check your code runner input and try again.";
  }

  if (error?.status === 503) {
    return "Code runner is currently unavailable. Please try again later.";
  }

  if (error?.status === 500) {
    return "Code runner failed safely. Please check the backend logs and try again.";
  }

  return "Could not run code right now. Please try again.";
}

function getCodeSubmitErrorMessage(error) {
  if (error?.status === 401) {
    return "Your session may have expired. Please log in again.";
  }

  if (error?.status === 400) {
    return "Please check your code submit input and try again.";
  }

  if (error?.status === 503) {
    return "Code runner is currently unavailable. Please try again later.";
  }

  return "Could not submit code right now. Please try again.";
}

function getCodeRunnerComparisonMessage(result) {
  if (result?.passed === true) {
    return "Passed expected output";
  }

  if (result?.passed === false) {
    return "Output did not match expected output";
  }

  return "No expected output comparison";
}

function getCodeRunnerStatusClass(result) {
  if (result?.passed === true) {
    return "border-emerald-200 bg-emerald-50 text-emerald-800";
  }

  if (result?.passed === false) {
    return "border-amber-200 bg-amber-50 text-amber-800";
  }

  return "border-slate-200 bg-slate-50 text-slate-700";
}

function getCodeSubmitSummary(result) {
  if (result?.passed === true && result?.firstAccepted === true) {
    return `Accepted - first solve! XP awarded: ${result?.xpAwarded ?? 0}`;
  }

  if (result?.passed === true && result?.firstAccepted === false) {
    return "Accepted again - no extra XP for repeated accepted submission.";
  }

  if (result?.passed === false) {
    return "Not accepted yet. Output did not match expected output.";
  }

  return "Submission completed without comparison status.";
}

function getCodeSubmitStatusClass(result) {
  if (result?.passed === true) {
    return "border-emerald-200 bg-emerald-50 text-emerald-800";
  }

  if (result?.passed === false) {
    return "border-amber-200 bg-amber-50 text-amber-800";
  }

  return "border-slate-200 bg-slate-50 text-slate-700";
}

function getCodeSubmissionHistoryErrorMessage(error) {
  if (error?.status === 401) {
    return "Your session may have expired. Please log in again.";
  }

  if (error?.status === 400) {
    return "Please check the submissions history request and try again.";
  }

  return "Could not load code submissions right now. Please try again.";
}

function getAiCodeReviewErrorMessage(error) {
  if (error?.status === 401) {
    return "Your session may have expired. Please log in again.";
  }

  if (error?.status === 400) {
    return "Please check your AI review input and try again.";
  }

  if (error?.status === 502) {
    return "AI review response was invalid. Please try again.";
  }

  if (error?.status === 503) {
    return "AI review service is currently unavailable. Please try again later.";
  }

  return "Could not review code right now. Please try again.";
}

function getAiReviewText(value) {
  if (typeof value !== "string") {
    return "Not provided.";
  }

  const trimmedValue = value.trim();
  return trimmedValue ? trimmedValue : "Not provided.";
}

function getAiReviewItems(value, emptyMessage) {
  if (!Array.isArray(value)) {
    return [emptyMessage];
  }

  const items = value
    .filter((item) => typeof item === "string")
    .map((item) => item.trim())
    .filter(Boolean);

  return items.length > 0 ? items : [emptyMessage];
}

function getSubmissionStatusLabel(submission) {
  if (submission?.passed === true) {
    return "Accepted";
  }

  if (submission?.passed === false) {
    return "Not Accepted";
  }

  return "Unknown status";
}

function getSubmissionStatusClass(submission) {
  if (submission?.passed === true) {
    return "border-emerald-200 bg-emerald-50 text-emerald-800";
  }

  if (submission?.passed === false) {
    return "border-amber-200 bg-amber-50 text-amber-800";
  }

  return "border-slate-200 bg-slate-50 text-slate-700";
}

function mergeCourseMapLevels(courseMap, courseProgress) {
  const progressLevels = Array.isArray(courseProgress?.levels) ? courseProgress.levels : [];
  const progressByLevelId = new Map(
    progressLevels
      .filter((level) => level?.levelId)
      .map((level) => [level.levelId, level]),
  );

  return (courseMap?.levels ?? []).map((level) => {
    const progressLevel = progressByLevelId.get(level.levelId);

    if (!progressLevel) {
      return {
        ...level,
        progressAvailable: false,
        completed: false,
        unlocked: true,
        completedAt: null,
      };
    }

    return {
      ...level,
      completed: Boolean(progressLevel.completed),
      unlocked: Boolean(progressLevel.unlocked),
      completedAt: progressLevel.completedAt ?? null,
      progressAvailable: true,
    };
  });
}

export default function DashboardShell({ profile, onRefreshProfile, onBackHome }) {
  const [form, setForm] = useState(INITIAL_FORM);
  const [generationLoading, setGenerationLoading] = useState(false);
  const [generationError, setGenerationError] = useState("");
  const [generatedCourse, setGeneratedCourse] = useState(null);
  const [courseMapLoading, setCourseMapLoading] = useState(false);
  const [courseMapError, setCourseMapError] = useState("");
  const [courseMap, setCourseMap] = useState(null);
  const [courseProgress, setCourseProgress] = useState(null);
  const [courseProgressError, setCourseProgressError] = useState("");
  const [levelCompletionLoading, setLevelCompletionLoading] = useState({});
  const [levelCompletionErrors, setLevelCompletionErrors] = useState({});
  const [levelCompletionSuccess, setLevelCompletionSuccess] = useState({});
  const [levelCompletionMessages, setLevelCompletionMessages] = useState({});
  const [selectedLevel, setSelectedLevel] = useState(null);
  const [quizSelections, setQuizSelections] = useState({});
  const [quizSubmitLoading, setQuizSubmitLoading] = useState({});
  const [quizSubmitErrors, setQuizSubmitErrors] = useState({});
  const [quizSubmitResults, setQuizSubmitResults] = useState({});
  const [quizSubmitProfileMessages, setQuizSubmitProfileMessages] = useState({});
  const [revealedFlashcards, setRevealedFlashcards] = useState({});
  const [noteContent, setNoteContent] = useState("");
  const [noteSaving, setNoteSaving] = useState(false);
  const [noteError, setNoteError] = useState("");
  const [noteSuccess, setNoteSuccess] = useState("");
  const [savedNoteMeta, setSavedNoteMeta] = useState(null);
  const [noteLoading, setNoteLoading] = useState(false);
  const [noteInfo, setNoteInfo] = useState("");
  const [quizAttemptHistory, setQuizAttemptHistory] = useState([]);
  const [quizAttemptHistoryLoading, setQuizAttemptHistoryLoading] = useState(false);
  const [quizAttemptHistoryError, setQuizAttemptHistoryError] = useState("");
  const [quizAttemptHistoryLoaded, setQuizAttemptHistoryLoaded] = useState(false);
  const [leaderboard, setLeaderboard] = useState(null);
  const [leaderboardLoading, setLeaderboardLoading] = useState(false);
  const [leaderboardError, setLeaderboardError] = useState("");
  const [leaderboardLoaded, setLeaderboardLoaded] = useState(false);
  const [codeRunnerForm, setCodeRunnerForm] = useState(INITIAL_CODE_RUNNER_FORM);
  const [codeRunnerLoading, setCodeRunnerLoading] = useState(false);
  const [codeRunnerError, setCodeRunnerError] = useState("");
  const [codeRunnerResult, setCodeRunnerResult] = useState(null);
  const [codeRunnerCodeTouched, setCodeRunnerCodeTouched] = useState(false);
  const [codeSubmitLoading, setCodeSubmitLoading] = useState(false);
  const [codeSubmitError, setCodeSubmitError] = useState("");
  const [codeSubmitResult, setCodeSubmitResult] = useState(null);
  const [codeSubmitProfileMessage, setCodeSubmitProfileMessage] = useState("");
  const [aiCodeReviewLoading, setAiCodeReviewLoading] = useState(false);
  const [aiCodeReviewError, setAiCodeReviewError] = useState("");
  const [aiCodeReviewResult, setAiCodeReviewResult] = useState(null);
  const [codeSubmissionsHistoryLoading, setCodeSubmissionsHistoryLoading] = useState(false);
  const [codeSubmissionsHistoryError, setCodeSubmissionsHistoryError] = useState("");
  const [codeSubmissionsHistory, setCodeSubmissionsHistory] = useState(null);
  const [codeSubmissionsHistoryLoaded, setCodeSubmissionsHistoryLoaded] = useState(false);
  const [codeSubmissionsHistoryPage, setCodeSubmissionsHistoryPage] = useState(0);

  const clearLevelCompletionFeedback = (levelId) => {
    if (!levelId) {
      return;
    }

    setLevelCompletionErrors((current) => {
      if (current[levelId] === undefined) {
        return current;
      }

      const nextErrors = { ...current };
      delete nextErrors[levelId];
      return nextErrors;
    });
    setLevelCompletionSuccess((current) => {
      if (current[levelId] === undefined) {
        return current;
      }

      const nextSuccess = { ...current };
      delete nextSuccess[levelId];
      return nextSuccess;
    });
    setLevelCompletionMessages((current) => {
      if (current[levelId] === undefined) {
        return current;
      }

      const nextMessages = { ...current };
      delete nextMessages[levelId];
      return nextMessages;
    });
  };

  const resetLevelCompletionState = () => {
    setLevelCompletionLoading({});
    setLevelCompletionErrors({});
    setLevelCompletionSuccess({});
    setLevelCompletionMessages({});
  };

  useEffect(() => {
    setQuizSelections({});
    setQuizSubmitLoading({});
    setQuizSubmitErrors({});
    setQuizSubmitResults({});
    setQuizSubmitProfileMessages({});
    setRevealedFlashcards({});
    setNoteContent("");
    setNoteSaving(false);
    setNoteLoading(false);
    setNoteError("");
    setNoteSuccess("");
    setNoteInfo("");
    setSavedNoteMeta(null);
  }, [selectedLevel?.levelId, selectedLevel?.orderNumber, selectedLevel?.title]);

  useEffect(() => {
    const levelId = selectedLevel?.levelId;

    if (!levelId) {
      return undefined;
    }

    let ignore = false;

    setNoteLoading(true);
    setNoteError("");
    setNoteSuccess("");
    setNoteInfo("");
    setNoteContent("");
    setSavedNoteMeta(null);

    getNoteForLevel(levelId)
      .then((note) => {
        if (ignore) {
          return;
        }

        if (!note) {
          setNoteInfo("No saved note yet.");
          return;
        }

        setNoteContent(note.content ?? "");
        setSavedNoteMeta({
          noteId: note.noteId ?? null,
          updatedAt: note.updatedAt ?? null,
        });
      })
      .catch((error) => {
        if (ignore) {
          return;
        }

        if (error?.status === 401) {
          setNoteError("Please log in again to load saved notes.");
        } else {
          setNoteError("Could not load saved note right now.");
        }
      })
      .finally(() => {
        if (!ignore) {
          setNoteLoading(false);
        }
      });

    return () => {
      ignore = true;
    };
  }, [selectedLevel?.levelId]);

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
      setCourseProgress(null);
      setCourseMapError("");
      setCourseProgressError("");
      resetLevelCompletionState();
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
    setCourseProgressError("");
    resetLevelCompletionState();

    try {
      const [courseResult, progressResult] = await Promise.allSettled([
        getCourseById({
          accessToken,
          courseId: generatedCourse.courseId,
        }),
        getCourseProgress(generatedCourse.courseId),
      ]);

      if (courseResult.status === "rejected") {
        throw courseResult.reason;
      }

      setCourseMap(courseResult.value);
      setSelectedLevel(null);

      if (progressResult.status === "fulfilled") {
        setCourseProgress(progressResult.value);
        setCourseProgressError("");
      } else {
        setCourseProgress(null);
        setCourseProgressError(getProgressErrorMessage(progressResult.reason));
      }
    } catch (error) {
      setCourseProgress(null);
      setCourseMapError(error.message || "Failed to load course map.");
    } finally {
      setCourseMapLoading(false);
    }
  };

  const refreshCurrentCourseProgress = async (courseId, { clearOnError = false } = {}) => {
    try {
      const progress = await getCourseProgress(courseId);
      setCourseProgress(progress);
      setCourseProgressError("");
      setSelectedLevel((currentLevel) => {
        if (!currentLevel?.levelId || !courseMap) {
          return currentLevel;
        }

        const mergedLevels = mergeCourseMapLevels(courseMap, progress);
        return mergedLevels.find((level) => level.levelId === currentLevel.levelId) ?? currentLevel;
      });
      return progress;
    } catch (error) {
      if (clearOnError) {
        setCourseProgress(null);
      }

      setCourseProgressError(getProgressErrorMessage(error));
      throw error;
    }
  };

  const handleBackToDashboard = () => {
    setCourseMapError("");
    setCourseMap(null);
    setCourseProgress(null);
    setCourseProgressError("");
    resetLevelCompletionState();
    setSelectedLevel(null);
  };

  const handleOpenLesson = (level) => {
    if (level.progressAvailable && !level.unlocked) {
      return;
    }

    clearLevelCompletionFeedback(level.levelId);
    setSelectedLevel(level);
  };

  const handleBackToCourseMap = () => {
    setSelectedLevel(null);
  };

  const handleCompleteLevel = async (level) => {
    const levelId = level?.levelId;
    const courseId = courseMap?.courseId;

    if (!levelId || !courseId) {
      return;
    }

    clearLevelCompletionFeedback(levelId);
    setLevelCompletionLoading((current) => ({
      ...current,
      [levelId]: true,
    }));

    try {
      const response = await completeLevel(levelId);
      const successMessage = response?.alreadyCompleted
        ? "This level is already completed."
        : response?.xpAwarded > 0
          ? `Level completed. You earned ${response.xpAwarded} XP.`
          : "Level completed.";

      setLevelCompletionSuccess((current) => ({
        ...current,
        [levelId]: successMessage,
      }));

      const completionMessages = [];

      try {
        await refreshCurrentCourseProgress(courseId);
      } catch {
        completionMessages.push("Level completion was saved, but course progress could not be refreshed right now.");
      }

      if (!response?.alreadyCompleted && typeof onRefreshProfile === "function") {
        try {
          const refreshedProfile = await onRefreshProfile();
          if (typeof refreshedProfile?.xp === "number") {
            completionMessages.push(`Current XP: ${refreshedProfile.xp}`);
          }
        } catch {
          completionMessages.push("Level completion is visible, but profile XP could not be refreshed right now.");
        }
      }

      setLevelCompletionMessages((current) => ({
        ...current,
        [levelId]: completionMessages.join(" "),
      }));
    } catch (error) {
      setLevelCompletionErrors((current) => ({
        ...current,
        [levelId]: getCompleteLevelErrorMessage(error),
      }));
    } finally {
      setLevelCompletionLoading((current) => ({
        ...current,
        [levelId]: false,
      }));
    }
  };

  const handleQuizOptionSelect = (questionIndex, optionIndex) => {
    setQuizSelections((current) => ({
      ...current,
      [questionIndex]: optionIndex,
    }));
    setQuizSubmitErrors((current) => ({
      ...current,
      [questionIndex]: "",
    }));
    setQuizSubmitProfileMessages((current) => {
      if (current[questionIndex] === undefined) {
        return current;
      }

      const nextMessages = { ...current };
      delete nextMessages[questionIndex];
      return nextMessages;
    });
    setQuizSubmitResults((current) => {
      if (current[questionIndex] === undefined) {
        return current;
      }

      const nextResults = { ...current };
      delete nextResults[questionIndex];
      return nextResults;
    });
  };

  const handleSubmitQuizAnswer = async (question, questionIndex) => {
    const selectedOptionIndex = quizSelections[questionIndex];
    const quizQuestionId = getQuizQuestionId(question);

    if (selectedOptionIndex === undefined) {
      return;
    }

    if (!quizQuestionId) {
      setQuizSubmitErrors((current) => ({
        ...current,
        [questionIndex]: "This quiz question is not available for scoring yet.",
      }));
      return;
    }

    const selectedAnswer = getOptionLabel(selectedOptionIndex);

    setQuizSubmitLoading((current) => ({
      ...current,
      [questionIndex]: true,
    }));
    setQuizSubmitErrors((current) => ({
      ...current,
      [questionIndex]: "",
    }));
    setQuizSubmitProfileMessages((current) => ({
      ...current,
      [questionIndex]: "",
    }));

    try {
      const response = await submitQuizAnswer(quizQuestionId, selectedAnswer);

      let profileMessage = "";
      if (response?.isCorrect && typeof onRefreshProfile === "function") {
        try {
          const refreshedProfile = await onRefreshProfile();
          if (typeof refreshedProfile?.xp === "number") {
            profileMessage = `XP updated. Current XP: ${refreshedProfile.xp}`;
          }
        } catch {
          profileMessage = "Answer submitted, but profile XP could not be refreshed. Reload profile to see latest XP.";
        }
      }

      setQuizSubmitResults((current) => ({
        ...current,
        [questionIndex]: response,
      }));
      setQuizSubmitProfileMessages((current) => ({
        ...current,
        [questionIndex]: profileMessage,
      }));
    } catch (error) {
      let message = "Could not submit answer right now.";

      if (error?.status === 400) {
        message = "Please choose a valid option.";
      } else if (error?.status === 401) {
        message = "Please log in again to submit quiz answers.";
      } else if (error?.status === 404) {
        message = "This quiz question is no longer available.";
      }

      setQuizSubmitErrors((current) => ({
        ...current,
        [questionIndex]: message,
      }));
      setQuizSubmitProfileMessages((current) => {
        if (current[questionIndex] === undefined) {
          return current;
        }

        const nextMessages = { ...current };
        delete nextMessages[questionIndex];
        return nextMessages;
      });
      setQuizSubmitResults((current) => {
        if (current[questionIndex] === undefined) {
          return current;
        }

        const nextResults = { ...current };
        delete nextResults[questionIndex];
        return nextResults;
      });
    } finally {
      setQuizSubmitLoading((current) => ({
        ...current,
        [questionIndex]: false,
      }));
    }
  };

  const handleFlashcardToggle = (cardIndex) => {
    setRevealedFlashcards((current) => ({
      ...current,
      [cardIndex]: !current[cardIndex],
    }));
  };

  const handleNoteContentChange = (event) => {
    setNoteContent(event.target.value);
    setNoteError("");
    setNoteSuccess("");
    setNoteInfo("");
  };

  const handleSaveNote = async () => {
    const levelId = selectedLevel?.levelId;
    const trimmedNote = noteContent.trim();

    if (!levelId) {
      setNoteError("This lesson cannot be saved yet because the level ID is missing.");
      setNoteSuccess("");
      return;
    }

    if (!trimmedNote) {
      setNoteError("Please enter a note before saving.");
      setNoteSuccess("");
      return;
    }

    if (noteContent.length > 5000) {
      setNoteError("Notes must be 5000 characters or fewer.");
      setNoteSuccess("");
      return;
    }

    setNoteSaving(true);
    setNoteError("");
    setNoteSuccess("");

    try {
      const response = await saveNoteForLevel({
        levelId,
        content: noteContent,
      });

      setNoteContent(response?.content ?? noteContent);
      setSavedNoteMeta({
        noteId: response?.noteId ?? null,
        updatedAt: response?.updatedAt ?? null,
      });
      setNoteInfo("");
      setNoteSuccess("Note saved.");
    } catch (error) {
      if (error?.status === 400) {
        setNoteError("The note could not be saved. Please check the content and try again.");
      } else if (error?.status === 401) {
        setNoteError("Your session has expired. Please log in again.");
      } else if (error?.status === 404) {
        setNoteError("The selected level could not be found.");
      } else {
        setNoteError("We could not save your note right now. Please try again.");
      }
    } finally {
      setNoteSaving(false);
    }
  };

  const handleLoadQuizAttemptHistory = async () => {
    setQuizAttemptHistoryLoading(true);
    setQuizAttemptHistoryError("");

    try {
      const response = await getQuizAttemptHistory();
      setQuizAttemptHistory(Array.isArray(response?.attempts) ? response.attempts : []);
      setQuizAttemptHistoryLoaded(true);
    } catch (error) {
      if (error?.status === 401) {
        setQuizAttemptHistoryError("Please log in again to load quiz attempt history.");
      } else {
        setQuizAttemptHistoryError("Could not load quiz attempt history right now.");
      }
    } finally {
      setQuizAttemptHistoryLoading(false);
    }
  };

  const handleLoadLeaderboard = async () => {
    setLeaderboardLoading(true);
    setLeaderboardError("");

    try {
      const response = await getLeaderboard();
      setLeaderboard(response);
      setLeaderboardLoaded(true);
    } catch (error) {
      setLeaderboardError(getLeaderboardErrorMessage(error));
      setLeaderboardLoaded(true);
    } finally {
      setLeaderboardLoading(false);
    }
  };

  const handleCodeRunnerFieldChange = (field, value) => {
    setCodeRunnerForm((current) => ({
      ...current,
      [field]: value,
    }));
  };

  const handleCodeLanguageChange = (language) => {
    setCodeRunnerForm((current) => ({
      ...current,
      language,
      code: codeRunnerCodeTouched ? current.code : CODE_RUNNER_STARTER_CODE[language],
    }));
  };

  const handleCodeRunnerSubmit = async (event) => {
    event.preventDefault();

    const trimmedProblemId = codeRunnerForm.problemId.trim();
    const trimmedCode = codeRunnerForm.code.trim();

    setCodeRunnerResult(null);

    if (!trimmedProblemId) {
      setCodeRunnerError(getMissingProblemIdMessage("run"));
      return;
    }

    if (!isValidProblemUuid(trimmedProblemId)) {
      setCodeRunnerError(getInvalidProblemIdMessage("run"));
      return;
    }

    if (!trimmedCode) {
      setCodeRunnerError("Please check your code runner input and try again.");
      return;
    }

    if (trimmedCode.length > 20000) {
      setCodeRunnerError("Please check your code runner input and try again.");
      return;
    }

    setCodeRunnerLoading(true);
    setCodeRunnerError("");
    setCodeRunnerResult(null);

    try {
      const response = await runCode(trimmedProblemId, {
        language: codeRunnerForm.language,
        code: codeRunnerForm.code,
        stdin: codeRunnerForm.stdin,
        expectedOutput: codeRunnerForm.expectedOutput,
      });
      setCodeRunnerResult(response);
    } catch (error) {
      setCodeRunnerError(getCodeRunnerErrorMessage(error));
    } finally {
      setCodeRunnerLoading(false);
    }
  };

  const handleAiCodeReview = async () => {
    const trimmedCode = codeRunnerForm.code.trim();

    if (!trimmedCode) {
      setAiCodeReviewError("Please check your AI review input and try again.");
      return;
    }

    if (trimmedCode.length > 20000) {
      setAiCodeReviewError("Please check your AI review input and try again.");
      return;
    }

    setAiCodeReviewLoading(true);
    setAiCodeReviewError("");
    setAiCodeReviewResult(null);

    try {
      const response = await reviewCodeWithAi({
        language: codeRunnerForm.language,
        code: codeRunnerForm.code,
        problemTitle: codeRunnerForm.problemTitle.trim() || null,
        problemDescription: codeRunnerForm.problemDescription.trim() || null,
      });

      setAiCodeReviewResult(response);
    } catch (error) {
      setAiCodeReviewError(getAiCodeReviewErrorMessage(error));
    } finally {
      setAiCodeReviewLoading(false);
    }
  };

  const handleCodeSubmit = async () => {
    const trimmedProblemId = codeRunnerForm.problemId.trim();
    const trimmedCode = codeRunnerForm.code.trim();
    const trimmedExpectedOutput = codeRunnerForm.expectedOutput.trim();

    setCodeSubmitResult(null);
    setCodeSubmitProfileMessage("");

    if (!trimmedProblemId) {
      setCodeSubmitError(getMissingProblemIdMessage("submit"));
      return;
    }

    if (!isValidProblemUuid(trimmedProblemId)) {
      setCodeSubmitError(getInvalidProblemIdMessage("submit"));
      return;
    }

    if (!trimmedCode) {
      setCodeSubmitError("Please check your code submit input and try again.");
      return;
    }

    if (!trimmedExpectedOutput) {
      setCodeSubmitError("Expected output is required before submitting.");
      return;
    }

    if (trimmedCode.length > 20000) {
      setCodeSubmitError("Please check your code submit input and try again.");
      return;
    }

    setCodeSubmitLoading(true);
    setCodeSubmitError("");
    setCodeSubmitResult(null);
    setCodeSubmitProfileMessage("");

    try {
      const response = await submitCode(trimmedProblemId, {
        language: codeRunnerForm.language,
        code: codeRunnerForm.code,
        stdin: codeRunnerForm.stdin,
        expectedOutput: codeRunnerForm.expectedOutput,
      });

      setCodeSubmitResult(response);

      if ((response?.xpAwarded ?? 0) > 0 && typeof onRefreshProfile === "function") {
        try {
          await onRefreshProfile();
        } catch {
          setCodeSubmitProfileMessage(
            "Submission saved, but profile refresh failed. Refresh the page to see updated XP.",
          );
        }
      }
    } catch (error) {
      setCodeSubmitError(getCodeSubmitErrorMessage(error));
    } finally {
      setCodeSubmitLoading(false);
    }
  };

  const loadCodeSubmissionsHistory = async (page = 0) => {
    const trimmedProblemId = codeRunnerForm.problemId.trim();

    if (!trimmedProblemId) {
      setCodeSubmissionsHistory(null);
      setCodeSubmissionsHistoryLoaded(false);
      setCodeSubmissionsHistoryError(getMissingProblemIdMessage("submissions"));
      return;
    }

    if (!isValidProblemUuid(trimmedProblemId)) {
      setCodeSubmissionsHistory(null);
      setCodeSubmissionsHistoryLoaded(false);
      setCodeSubmissionsHistoryError(getInvalidProblemIdMessage("submissions"));
      return;
    }

    setCodeSubmissionsHistoryLoading(true);
    setCodeSubmissionsHistoryError("");

    try {
      const response = await getCodeSubmissions(trimmedProblemId, page, 20);
      setCodeSubmissionsHistory(response);
      setCodeSubmissionsHistoryLoaded(true);
      setCodeSubmissionsHistoryPage(page);
    } catch (error) {
      setCodeSubmissionsHistoryError(getCodeSubmissionHistoryErrorMessage(error));
      setCodeSubmissionsHistoryLoaded(true);
    } finally {
      setCodeSubmissionsHistoryLoading(false);
    }
  };

  const handleLoadCodeSubmissionsHistory = async () => {
    await loadCodeSubmissionsHistory(0);
  };

  const handlePreviousCodeSubmissionsPage = async () => {
    if (codeSubmissionsHistoryPage <= 0 || codeSubmissionsHistoryLoading) {
      return;
    }

    await loadCodeSubmissionsHistory(codeSubmissionsHistoryPage - 1);
  };

  const handleNextCodeSubmissionsPage = async () => {
    const totalPages = codeSubmissionsHistory?.totalPages ?? 0;

    if (
      codeSubmissionsHistoryLoading ||
      totalPages <= 0 ||
      codeSubmissionsHistoryPage >= totalPages - 1
    ) {
      return;
    }

    await loadCodeSubmissionsHistory(codeSubmissionsHistoryPage + 1);
  };

  if (courseMap && selectedLevel) {
    const quizQuestions = normalizeQuizQuestions(selectedLevel);
    const flashcards = normalizeFlashcards(selectedLevel);
    const selectedLevelId = selectedLevel.levelId;
    const selectedLevelCompleteLoading = Boolean(levelCompletionLoading[selectedLevelId]);
    const selectedLevelCompleteError = levelCompletionErrors[selectedLevelId];
    const selectedLevelCompleteSuccess = levelCompletionSuccess[selectedLevelId];
    const selectedLevelCompleteMessage = levelCompletionMessages[selectedLevelId];
    const canCompleteSelectedLevel = Boolean(selectedLevelId) && selectedLevel.unlocked && !selectedLevel.completed;

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

              <div className="mt-6 rounded-xl border border-slate-200 bg-slate-50 p-4">
                <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
                  <div>
                    <h3 className="text-base font-semibold text-slate-900">Level Completion</h3>
                    <p className="mt-1 text-sm text-slate-600">
                      Mark this level complete after you finish the lesson to refresh progress and XP.
                    </p>
                  </div>
                  {canCompleteSelectedLevel ? (
                    <button
                      type="button"
                      onClick={() => handleCompleteLevel(selectedLevel)}
                      disabled={selectedLevelCompleteLoading}
                      className="rounded-xl bg-slate-900 px-4 py-2 text-sm font-semibold text-white transition hover:bg-slate-700 disabled:cursor-not-allowed disabled:bg-slate-400"
                    >
                      {selectedLevelCompleteLoading ? "Completing..." : "Complete Level"}
                    </button>
                  ) : (
                    <span className="text-sm font-semibold text-emerald-700">
                      {selectedLevel.completed ? "This level is already completed." : "Complete previous levels first."}
                    </span>
                  )}
                </div>

                {selectedLevelCompleteError && (
                  <div className="mt-4 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800" role="alert">
                    {selectedLevelCompleteError}
                  </div>
                )}

                {selectedLevelCompleteSuccess && (
                  <div className="mt-4 rounded-lg border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-800" role="status">
                    {selectedLevelCompleteSuccess}
                  </div>
                )}

                {selectedLevelCompleteMessage && (
                  <p className="mt-3 text-sm text-slate-600">{selectedLevelCompleteMessage}</p>
                )}
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
                  <h3 className="text-xl font-semibold text-slate-900">Notes</h3>
                  <p className="mt-1 text-sm text-slate-600">Write down the key idea you want to remember from this lesson and save it to your account.</p>
                </div>
                <p className="text-sm text-slate-500">{noteContent.length}/5000</p>
              </div>

              <div className="mt-4">
                <label htmlFor="lesson-note" className="sr-only">
                  Lesson note
                </label>
                <textarea
                  id="lesson-note"
                  value={noteContent}
                  onChange={handleNoteContentChange}
                  rows={6}
                  placeholder="Write your note for this lesson here."
                  disabled={noteLoading}
                  className="w-full rounded-xl border border-slate-300 px-4 py-3 text-sm text-slate-900 outline-none transition focus:border-slate-500 focus:ring-2 focus:ring-slate-200"
                />
              </div>

              {noteLoading && (
                <p className="mt-3 text-sm text-slate-500">Loading saved note...</p>
              )}

              {noteInfo && !noteLoading && (
                <p className="mt-3 text-sm text-slate-500">{noteInfo}</p>
              )}

              {savedNoteMeta?.updatedAt && (
                <p className="mt-3 text-xs text-slate-500">
                  Last saved: {formatSavedTimestamp(savedNoteMeta.updatedAt)}
                </p>
              )}

              {savedNoteMeta?.noteId && (
                <p className="mt-1 text-xs text-slate-500">Note ID: {savedNoteMeta.noteId}</p>
              )}

              {noteError && (
                <div className="mt-4 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800" role="alert">
                  {noteError}
                </div>
              )}

              {noteSuccess && (
                <div className="mt-4 rounded-lg border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-800" role="status">
                  {noteSuccess}
                </div>
              )}

              <div className="mt-4 flex flex-wrap items-center gap-3">
                <button
                  type="button"
                  onClick={handleSaveNote}
                  disabled={noteSaving || noteLoading || !selectedLevel?.levelId}
                  className="rounded-xl bg-slate-900 px-5 py-3 text-sm font-semibold text-white transition hover:bg-slate-700 disabled:cursor-not-allowed disabled:bg-slate-400"
                >
                  {noteSaving ? "Saving..." : "Save Note"}
                </button>
                {!selectedLevel?.levelId && (
                  <p className="text-sm text-slate-500">Notes can be saved after a valid level ID is available.</p>
                )}
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
                      const options = normalizeQuestionOptions(question);
                      const selectedOptionIndex = quizSelections[questionIndex];
                      const quizResult = quizSubmitResults[questionIndex];
                      const quizProfileMessage = quizSubmitProfileMessages[questionIndex];
                      const quizError = quizSubmitErrors[questionIndex];
                      const isQuizSubmitting = Boolean(quizSubmitLoading[questionIndex]);
                      const quizQuestionId = getQuizQuestionId(question);

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
                          <>
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

                            <div className="mt-4 flex flex-wrap items-center gap-3">
                              <button
                                type="button"
                                onClick={() => handleSubmitQuizAnswer(question, questionIndex)}
                                disabled={selectedOptionIndex === undefined || isQuizSubmitting || !quizQuestionId}
                                className="rounded-xl bg-slate-900 px-4 py-2 text-sm font-semibold text-white transition hover:bg-slate-700 disabled:cursor-not-allowed disabled:bg-slate-400"
                              >
                                {isQuizSubmitting ? "Submitting..." : "Submit Answer"}
                              </button>
                              {!quizQuestionId && (
                                <p className="text-sm text-slate-500">This quiz question cannot be submitted yet.</p>
                              )}
                            </div>
                          </>
                        ) : (
                          <p className="mt-4 text-sm text-slate-600">Options are not available for this question yet.</p>
                        )}

                        {quizError && (
                          <div className="mt-4 rounded-xl border border-red-200 bg-red-50 p-4">
                            <p className="text-sm text-red-800">{quizError}</p>
                          </div>
                        )}

                          {quizResult && (
                            <div className={`mt-4 rounded-xl border p-4 ${
                              quizResult.isCorrect
                                ? "border-emerald-200 bg-emerald-50"
                                : "border-amber-200 bg-amber-50"
                          }`}>
                            <p className={`text-sm font-semibold ${
                              quizResult.isCorrect ? "text-emerald-800" : "text-amber-800"
                            }`}>
                              {quizResult.isCorrect ? "Correct" : "Incorrect"}
                            </p>
                            <p className="mt-2 text-sm text-slate-700">
                              Selected answer: {quizResult.selectedAnswer}
                            </p>
                            {quizResult.concept && (
                              <p className="mt-2 text-sm text-slate-700">
                                Concept: {quizResult.concept}
                              </p>
                            )}
                              {quizResult.explanation && (
                                <div className="mt-3 rounded-xl border border-white/70 bg-white p-4">
                                  <p className="text-sm font-semibold text-slate-700">Explanation</p>
                                  <p className="mt-2 text-sm leading-6 text-slate-600">{quizResult.explanation}</p>
                                </div>
                              )}
                              {quizProfileMessage && (
                                <p className="mt-3 text-sm text-slate-700">{quizProfileMessage}</p>
                              )}
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
    const mergedLevels = mergeCourseMapLevels(courseMap, courseProgress);
    const totalLevels = courseProgress?.totalLevels ?? mergedLevels.length;
    const completedLevels = courseProgress?.completedLevels ?? mergedLevels.filter((level) => level.completed).length;
    const progressPercent = typeof courseProgress?.progressPercent === "number" ? courseProgress.progressPercent : 0;
    const courseCompleted = Boolean(courseProgress?.courseCompleted);

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
                <div className="flex flex-wrap items-center gap-2">
                  {courseCompleted && (
                    <span className="inline-flex w-fit rounded-full border border-emerald-200 bg-emerald-50 px-3 py-1 text-xs font-semibold text-emerald-800">
                      Course Completed
                    </span>
                  )}
                  <span className="inline-flex w-fit rounded-full border border-slate-300 bg-white px-3 py-1 text-xs font-semibold text-slate-700">
                    {courseMap.sourceType || "UNKNOWN"}
                  </span>
                </div>
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

              <div className="mt-6 rounded-2xl border border-slate-200 bg-slate-50 p-5">
                <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
                  <div>
                    <h3 className="text-lg font-semibold text-slate-900">Progress Summary</h3>
                    <p className="mt-1 text-sm text-slate-600">
                      {completedLevels} / {totalLevels} levels completed
                    </p>
                  </div>
                  <p className="text-2xl font-semibold text-slate-900">{progressPercent}%</p>
                </div>

                <div className="mt-4 h-3 overflow-hidden rounded-full bg-slate-200">
                  <div
                    className="h-full rounded-full bg-slate-900 transition-all"
                    style={{ width: `${Math.max(0, Math.min(progressPercent, 100))}%` }}
                  />
                </div>

                {courseProgressError && (
                  <div
                    className="mt-4 rounded-lg border border-amber-200 bg-amber-50 px-4 py-3 text-sm text-amber-800"
                    role="alert"
                  >
                    {courseProgressError}
                  </div>
                )}
              </div>
            </div>

            <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
              <h3 className="text-xl font-semibold text-slate-900">Levels</h3>
              <div className="mt-5 grid grid-cols-1 gap-4 md:grid-cols-2">
                {mergedLevels.map((level) => {
                  const progressBadge = getLevelProgressBadge(level);
                  const lockedExplanation = getLockedExplanation(level);
                  const isCompleteButtonVisible = level.unlocked && !level.completed;
                  const isLevelCompleting = Boolean(levelCompletionLoading[level.levelId]);
                  const levelCompleteError = levelCompletionErrors[level.levelId];
                  const levelCompleteSuccess = levelCompletionSuccess[level.levelId];
                  const levelCompleteMessage = levelCompletionMessages[level.levelId];

                  return (
                    <div key={level.levelId ?? `${level.orderNumber}-${level.title}`} className="rounded-xl border border-slate-200 bg-slate-50 p-5">
                      <div className="flex flex-wrap items-center justify-between gap-3">
                        <span className="text-sm font-semibold text-slate-500">Level {level.orderNumber}</span>
                        <div className="flex flex-wrap items-center gap-2">
                          <span className={`rounded-full px-2.5 py-1 text-xs font-semibold ${getLevelTypeBadgeClass(level)}`}>
                            {level.isBoss ? "Boss" : "Standard"}
                          </span>
                          <span className={`rounded-full border px-2.5 py-1 text-xs font-semibold ${progressBadge.className}`}>
                            {progressBadge.label}
                          </span>
                        </div>
                      </div>
                      <h4 className="mt-3 text-lg font-semibold text-slate-900">{level.title}</h4>
                      <p className="mt-2 text-sm text-slate-600">XP Reward: {level.xpReward}</p>
                      <p className="mt-3 text-sm leading-6 text-slate-600">{getContentPreview(level.contentMarkdown)}</p>

                      {level.completedAt && (
                        <p className="mt-3 text-xs text-emerald-700">
                          Completed on {formatSavedTimestamp(level.completedAt)}
                        </p>
                      )}

                      {lockedExplanation && (
                        <p className="mt-3 text-sm text-rose-700">{lockedExplanation}</p>
                      )}

                      <div className="mt-4 flex flex-wrap items-center gap-3">
                        <button
                          type="button"
                          onClick={() => handleOpenLesson(level)}
                          disabled={level.progressAvailable && !level.unlocked}
                          className="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-900 transition hover:bg-slate-100 disabled:cursor-not-allowed disabled:border-slate-200 disabled:bg-slate-100 disabled:text-slate-400"
                        >
                          Open Lesson
                        </button>

                        {isCompleteButtonVisible && (
                          <button
                            type="button"
                            onClick={() => handleCompleteLevel(level)}
                            disabled={isLevelCompleting}
                            className="rounded-xl bg-slate-900 px-4 py-2 text-sm font-semibold text-white transition hover:bg-slate-700 disabled:cursor-not-allowed disabled:bg-slate-400"
                          >
                            {isLevelCompleting ? "Completing..." : "Complete Level"}
                          </button>
                        )}
                      </div>

                      {level.completed && (
                        <p className="mt-3 text-sm text-emerald-700">This level is already completed.</p>
                      )}

                      {levelCompleteError && (
                        <div className="mt-4 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800" role="alert">
                          {levelCompleteError}
                        </div>
                      )}

                      {levelCompleteSuccess && (
                        <div className="mt-4 rounded-lg border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-800" role="status">
                          {levelCompleteSuccess}
                        </div>
                      )}

                      {levelCompleteMessage && (
                        <p className="mt-3 text-sm text-slate-600">{levelCompleteMessage}</p>
                      )}
                    </div>
                  );
                })}
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }

  const profileName = typeof profile?.name === "string" && profile.name.trim()
    ? profile.name.trim()
    : "CodeQuest Learner";
  const profileEmail = typeof profile?.email === "string" && profile.email.trim()
    ? profile.email.trim()
    : "Profile not loaded yet";
  const profileRank = profile?.rank ?? "Unknown";
  const profileXp = profile?.xp ?? 0;
  const profileStreak = profile?.streak ?? 0;
  const leaderboardPosition = leaderboard?.currentUser?.rankPosition ?? null;
  const generatedLevels = Array.isArray(generatedCourse?.levels) ? generatedCourse.levels : [];
  const generatedLevelCount = generatedLevels.length;
  const currentCourseLabel = generatedCourse?.title ?? "No active course yet";
  const learningStatus = generatedCourse
    ? courseMapLoading
      ? "Loading map"
      : "Course ready"
    : generationLoading
      ? "Generating"
      : "Ready to begin";
  const nextActionItems = [
    generatedCourse
      ? "Open the course map to move from course creation into lesson-by-lesson practice."
      : "Generate a personalized Java learning path to populate your learning workspace.",
    codeRunnerForm.problemId.trim()
      ? "Run or submit code for the current problem ID from the practice workspace."
      : "Add a problem ID in the code practice area before running or submitting code.",
    leaderboardLoaded
      ? "Refresh the leaderboard whenever you want an updated snapshot of XP standings."
      : "Load the leaderboard to compare your XP progress with other learners.",
    quizAttemptHistoryLoaded
      ? "Review quiz history to spot concepts that still need reinforcement."
      : "Load quiz attempt history after lessons to review mistakes and explanations.",
  ];
  const navItems = [
    { id: "overview-section", label: "Overview" },
    { id: "learn-section", label: "Learn" },
    { id: "practice-section", label: "Practice" },
    { id: "history-section", label: "History" },
    { id: "progress-section", label: "Progress" },
  ];
  const statCards = [
    {
      label: "XP",
      value: profileXp,
      detail: "Total points earned",
      className: "border-blue-100 bg-blue-50",
    },
    {
      label: "Rank",
      value: profileRank,
      detail: "Current learning tier",
      className: "border-violet-100 bg-violet-50",
    },
    {
      label: "Streak",
      value: profileStreak,
      detail: "Daily learning streak",
      className: "border-emerald-100 bg-emerald-50",
    },
    {
      label: "Leaderboard",
      value: leaderboardPosition ? `#${leaderboardPosition}` : "Load rank",
      detail: leaderboardLoaded ? "Latest standing snapshot" : "Available after loading leaderboard",
      className: "border-orange-100 bg-orange-50",
    },
  ];
  const scrollToSection = (sectionId) => {
    if (typeof document === "undefined") {
      return;
    }

    document.getElementById(sectionId)?.scrollIntoView({
      behavior: "smooth",
      block: "start",
    });
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

          <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
            <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
              <div>
                <h2 className="text-xl font-semibold text-slate-900 mb-1">Leaderboard</h2>
                <p className="text-sm text-slate-600">See how learners rank by XP.</p>
              </div>
              <button
                type="button"
                onClick={handleLoadLeaderboard}
                disabled={leaderboardLoading}
                className="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-900 transition hover:bg-slate-100 disabled:cursor-not-allowed disabled:bg-slate-100 disabled:text-slate-400"
              >
                {leaderboardLoading
                  ? "Loading Leaderboard..."
                  : leaderboardLoaded
                    ? "Refresh Leaderboard"
                    : "Load Leaderboard"}
              </button>
            </div>

            {leaderboardError && (
              <div
                className="mt-4 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800"
                role="alert"
              >
                {leaderboardError}
              </div>
            )}

            {leaderboard?.currentUser && (
              <div className="mt-5 rounded-2xl border border-sky-200 bg-sky-50 p-5">
                <p className="text-sm font-semibold text-sky-800">Your Standing</p>
                <div className="mt-3 grid grid-cols-1 gap-4 sm:grid-cols-3">
                  <div>
                    <p className="text-sm text-sky-700">Your position</p>
                    <p className="text-lg font-semibold text-slate-900">
                      {leaderboard.currentUser.rankPosition ? `#${leaderboard.currentUser.rankPosition}` : "Unranked"}
                    </p>
                  </div>
                  <div>
                    <p className="text-sm text-sky-700">XP</p>
                    <p className="text-lg font-semibold text-slate-900">{leaderboard.currentUser.xp ?? 0}</p>
                  </div>
                  <div>
                    <p className="text-sm text-sky-700">Rank</p>
                    <p className="text-lg font-semibold text-slate-900">{leaderboard.currentUser.rank ?? "Unknown"}</p>
                  </div>
                </div>
              </div>
            )}

            {leaderboardLoading && !leaderboard && (
              <div className="mt-4 rounded-xl bg-slate-50 p-5">
                <p className="text-sm text-slate-600">Loading leaderboard...</p>
              </div>
            )}

            {leaderboardLoaded && !leaderboardError && (!Array.isArray(leaderboard?.items) || leaderboard.items.length === 0) && (
              <div className="mt-4 rounded-xl bg-slate-50 p-5">
                <p className="text-sm text-slate-600">No leaderboard entries yet. Earn XP to appear here.</p>
              </div>
            )}

            {Array.isArray(leaderboard?.items) && leaderboard.items.length > 0 && (
              <div className="mt-5 overflow-hidden rounded-2xl border border-slate-200">
                <div className="overflow-x-auto">
                  <table className="min-w-full divide-y divide-slate-200">
                    <thead className="bg-slate-50">
                      <tr>
                        <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">Position</th>
                        <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">Name</th>
                        <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">XP</th>
                        <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">Rank</th>
                        <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">Streak</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-200 bg-white">
                      {leaderboard.items.map((entry, index) => (
                        <tr key={`${entry.userId ?? entry.name ?? "leader"}-${entry.rankPosition ?? index}`} className="align-top">
                          <td className="px-4 py-4 text-sm text-slate-700">
                            <span className={`inline-flex rounded-full px-2.5 py-1 text-xs font-semibold ${getLeaderboardBadgeClass(entry.rankPosition)}`}>
                              #{entry.rankPosition ?? "-"}
                            </span>
                          </td>
                          <td className="px-4 py-4 text-sm font-semibold text-slate-900">{entry.name ?? "Unknown learner"}</td>
                          <td className="px-4 py-4 text-sm text-slate-700">{entry.xp ?? 0}</td>
                          <td className="px-4 py-4 text-sm text-slate-700">{entry.rank ?? "Unknown"}</td>
                          <td className="px-4 py-4 text-sm text-slate-700">{entry.streak ?? 0}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
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

          <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
            <div className="mb-4">
              <h2 className="text-xl font-semibold text-slate-900">Code Runner</h2>
              <p className="mt-1 text-sm text-slate-600">
                Run small code snippets using the backend Piston runner.
              </p>
            </div>

            <form className="space-y-4" onSubmit={handleCodeRunnerSubmit}>
              <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
                <div>
                  <label htmlFor="code-runner-problem-id" className="mb-2 block text-sm font-medium text-slate-700">
                    Problem ID
                  </label>
                  <input
                    id="code-runner-problem-id"
                    type="text"
                    value={codeRunnerForm.problemId}
                    onChange={(event) => handleCodeRunnerFieldChange("problemId", event.target.value)}
                    placeholder={DEMO_PROBLEM_UUID}
                    className="w-full rounded-xl border border-slate-300 px-4 py-3 text-sm text-slate-900 outline-none transition focus:border-slate-500 focus:ring-2 focus:ring-slate-200"
                  />
                </div>

                <div>
                  <label htmlFor="code-runner-language" className="mb-2 block text-sm font-medium text-slate-700">
                    Language
                  </label>
                  <select
                    id="code-runner-language"
                    value={codeRunnerForm.language}
                    onChange={(event) => handleCodeLanguageChange(event.target.value)}
                    className="w-full rounded-xl border border-slate-300 px-4 py-3 text-sm text-slate-900 outline-none transition focus:border-slate-500 focus:ring-2 focus:ring-slate-200"
                  >
                    <option value="java">Java</option>
                    <option value="python">Python</option>
                    <option value="javascript">JavaScript</option>
                    <option value="cpp">C++</option>
                  </select>
                </div>
              </div>

              <div>
                <label htmlFor="code-runner-code" className="mb-2 block text-sm font-medium text-slate-700">
                  Code
                </label>
                <CodeEditorField
                  language={codeRunnerForm.language}
                  value={codeRunnerForm.code}
                  onChange={(nextValue) => {
                    setCodeRunnerCodeTouched(true);
                    handleCodeRunnerFieldChange("code", nextValue);
                  }}
                />
                <p className="mt-2 text-xs text-slate-500">
                  {codeRunnerForm.code.length}/20000 characters
                </p>
              </div>

              <div className="grid grid-cols-1 gap-4 lg:grid-cols-2">
                <div>
                  <label htmlFor="code-runner-stdin" className="mb-2 block text-sm font-medium text-slate-700">
                    Standard Input (Optional)
                  </label>
                  <textarea
                    id="code-runner-stdin"
                    value={codeRunnerForm.stdin}
                    onChange={(event) => handleCodeRunnerFieldChange("stdin", event.target.value)}
                    rows={5}
                    className="w-full rounded-xl border border-slate-300 px-4 py-3 text-sm text-slate-900 outline-none transition focus:border-slate-500 focus:ring-2 focus:ring-slate-200"
                  />
                </div>

                <div>
                  <label htmlFor="code-runner-expected-output" className="mb-2 block text-sm font-medium text-slate-700">
                    Expected Output (Optional)
                  </label>
                  <textarea
                    id="code-runner-expected-output"
                    value={codeRunnerForm.expectedOutput}
                    onChange={(event) => handleCodeRunnerFieldChange("expectedOutput", event.target.value)}
                    rows={5}
                    className="w-full rounded-xl border border-slate-300 px-4 py-3 text-sm text-slate-900 outline-none transition focus:border-slate-500 focus:ring-2 focus:ring-slate-200"
                  />
                </div>
              </div>

              <div className="rounded-2xl border border-slate-200 bg-slate-50 p-5">
                <div className="mb-4">
                  <h3 className="text-lg font-semibold text-slate-900">AI Code Review</h3>
                  <p className="mt-1 text-sm text-slate-600">
                    Send the current language and code to the backend AI review endpoint for structured feedback.
                  </p>
                </div>

                <div className="grid grid-cols-1 gap-4 lg:grid-cols-2">
                  <div>
                    <label htmlFor="ai-review-problem-title" className="mb-2 block text-sm font-medium text-slate-700">
                      Problem Title (Optional)
                    </label>
                    <input
                      id="ai-review-problem-title"
                      type="text"
                      value={codeRunnerForm.problemTitle}
                      onChange={(event) => handleCodeRunnerFieldChange("problemTitle", event.target.value)}
                      placeholder="Hello World"
                      className="w-full rounded-xl border border-slate-300 px-4 py-3 text-sm text-slate-900 outline-none transition focus:border-slate-500 focus:ring-2 focus:ring-slate-200"
                    />
                  </div>

                  <div>
                    <label htmlFor="ai-review-problem-description" className="mb-2 block text-sm font-medium text-slate-700">
                      Problem Description (Optional)
                    </label>
                    <textarea
                      id="ai-review-problem-description"
                      value={codeRunnerForm.problemDescription}
                      onChange={(event) => handleCodeRunnerFieldChange("problemDescription", event.target.value)}
                      rows={4}
                      placeholder="Print Hello CodeQuest."
                      className="w-full rounded-xl border border-slate-300 px-4 py-3 text-sm text-slate-900 outline-none transition focus:border-slate-500 focus:ring-2 focus:ring-slate-200"
                    />
                  </div>
                </div>

                {codeRunnerForm.code.length > 20000 && (
                  <div
                    className="mt-4 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800"
                    role="alert"
                  >
                    Code must be 20000 characters or fewer for AI review.
                  </div>
                )}

                {aiCodeReviewError && (
                  <div
                    className="mt-4 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800"
                    role="alert"
                  >
                    {aiCodeReviewError}
                  </div>
                )}

                <button
                  type="button"
                  onClick={handleAiCodeReview}
                  disabled={
                    aiCodeReviewLoading ||
                    !codeRunnerForm.code.trim() ||
                    codeRunnerForm.code.length > 20000
                  }
                  className="mt-4 rounded-xl border border-slate-300 bg-white px-5 py-3 text-sm font-semibold text-slate-900 transition hover:bg-slate-100 disabled:cursor-not-allowed disabled:bg-slate-100 disabled:text-slate-400"
                >
                  {aiCodeReviewLoading ? "Reviewing Code..." : "Review Code with AI"}
                </button>

                {aiCodeReviewLoading && !aiCodeReviewResult && (
                  <div className="mt-4 rounded-xl border border-slate-200 bg-white p-5">
                    <p className="text-sm text-slate-600">Reviewing your code with AI...</p>
                  </div>
                )}

                {aiCodeReviewResult && (
                  <div className="mt-5 rounded-2xl border border-slate-200 bg-white p-5">
                    <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
                      <div>
                        <h4 className="text-lg font-semibold text-slate-900">AI Review Result</h4>
                        <p className="mt-1 text-sm text-slate-600">
                          Structured feedback only. No raw backend or model internals are shown.
                        </p>
                      </div>
                      <span className="inline-flex w-fit rounded-full border border-sky-200 bg-sky-50 px-3 py-1 text-xs font-semibold text-sky-800">
                        {codeRunnerForm.language}
                      </span>
                    </div>

                    <div className="mt-4 grid grid-cols-1 gap-4 md:grid-cols-2">
                      <div className="rounded-xl bg-slate-50 p-4">
                        <p className="text-sm text-slate-500">Time Complexity</p>
                        <p className="mt-1 text-base font-semibold text-slate-900">
                          {getAiReviewText(aiCodeReviewResult.timeComplexity)}
                        </p>
                      </div>
                      <div className="rounded-xl bg-slate-50 p-4">
                        <p className="text-sm text-slate-500">Space Complexity</p>
                        <p className="mt-1 text-base font-semibold text-slate-900">
                          {getAiReviewText(aiCodeReviewResult.spaceComplexity)}
                        </p>
                      </div>
                    </div>

                    <div className="mt-4 grid grid-cols-1 gap-4 lg:grid-cols-2">
                      <div className="rounded-xl border border-slate-200 bg-slate-50 p-4">
                        <p className="text-sm font-semibold text-slate-700">Correctness Issues</p>
                        <ul className="mt-2 space-y-2 text-sm text-slate-700">
                          {getAiReviewItems(
                            aiCodeReviewResult.correctnessIssues,
                            "No correctness issues returned.",
                          ).map((item, index) => (
                            <li key={`correctness-issue-${index}`}>{item}</li>
                          ))}
                        </ul>
                      </div>

                      <div className="rounded-xl border border-slate-200 bg-slate-50 p-4">
                        <p className="text-sm font-semibold text-slate-700">Improvements</p>
                        <ul className="mt-2 space-y-2 text-sm text-slate-700">
                          {getAiReviewItems(
                            aiCodeReviewResult.improvements,
                            "No improvements returned.",
                          ).map((item, index) => (
                            <li key={`improvement-${index}`}>{item}</li>
                          ))}
                        </ul>
                      </div>
                    </div>

                    <div className="mt-4 rounded-xl border border-slate-200 bg-slate-50 p-4">
                      <p className="text-sm font-semibold text-slate-700">Better Approach</p>
                      <p className="mt-2 text-sm text-slate-700">
                        {getAiReviewText(aiCodeReviewResult.betterApproach)}
                      </p>
                    </div>

                    <div className="mt-4 rounded-xl border border-slate-200 bg-slate-50 p-4">
                      <p className="text-sm font-semibold text-slate-700">Encouragement</p>
                      <p className="mt-2 text-sm text-slate-700">
                        {getAiReviewText(aiCodeReviewResult.encouragement)}
                      </p>
                    </div>
                  </div>
                )}
              </div>

              {codeRunnerError && (
                <div
                  className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800"
                  role="alert"
                >
                  {codeRunnerError}
                </div>
              )}

              <button
                type="submit"
                disabled={
                  codeRunnerLoading ||
                  !codeRunnerForm.problemId.trim() ||
                  !codeRunnerForm.code.trim()
                }
                className="rounded-xl bg-slate-900 px-5 py-3 text-sm font-semibold text-white transition hover:bg-slate-700 disabled:cursor-not-allowed disabled:bg-slate-400"
              >
                {codeRunnerLoading ? "Running Code..." : "Run Code"}
              </button>

              <button
                type="button"
                onClick={handleCodeSubmit}
                disabled={
                  codeSubmitLoading ||
                  !codeRunnerForm.problemId.trim() ||
                  !codeRunnerForm.code.trim() ||
                  !codeRunnerForm.expectedOutput.trim()
                }
                className="ml-3 rounded-xl border border-slate-300 bg-white px-5 py-3 text-sm font-semibold text-slate-900 transition hover:bg-slate-100 disabled:cursor-not-allowed disabled:bg-slate-100 disabled:text-slate-400"
              >
                {codeSubmitLoading ? "Submitting Code..." : "Submit Code"}
              </button>
            </form>

            {codeRunnerResult && (
              <div className="mt-6 rounded-2xl border border-slate-200 bg-slate-50 p-5">
                <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
                  <div>
                    <h3 className="text-lg font-semibold text-slate-900">Run Result</h3>
                    {codeRunnerResult.message && (
                      <p className="mt-1 text-sm text-slate-600">{codeRunnerResult.message}</p>
                    )}
                  </div>
                  <span className={`inline-flex w-fit rounded-full border px-3 py-1 text-xs font-semibold ${getCodeRunnerStatusClass(codeRunnerResult)}`}>
                    {getCodeRunnerComparisonMessage(codeRunnerResult)}
                  </span>
                </div>

                <div className="mt-4 grid grid-cols-1 gap-4 md:grid-cols-3">
                  <div className="rounded-xl bg-white p-4">
                    <p className="text-sm text-slate-500">Language</p>
                    <p className="mt-1 text-base font-semibold text-slate-900">
                      {codeRunnerResult.language ?? codeRunnerForm.language}
                    </p>
                  </div>
                  <div className="rounded-xl bg-white p-4">
                    <p className="text-sm text-slate-500">Exit Code</p>
                    <p className="mt-1 text-base font-semibold text-slate-900">
                      {codeRunnerResult.exitCode ?? "Unavailable"}
                    </p>
                  </div>
                  <div className="rounded-xl bg-white p-4">
                    <p className="text-sm text-slate-500">Runtime</p>
                    <p className="mt-1 text-base font-semibold text-slate-900">
                      {codeRunnerResult.runtimeMs ?? codeRunnerResult.runtimeMs === 0
                        ? `${codeRunnerResult.runtimeMs} ms`
                        : "Unavailable"}
                    </p>
                  </div>
                </div>

                <div className="mt-4 rounded-xl bg-white p-4">
                  <p className="text-sm text-slate-500">Passed Status</p>
                  <p className="mt-1 text-base font-semibold text-slate-900">
                    {codeRunnerResult.passed === true
                      ? "Passed expected output"
                      : codeRunnerResult.passed === false
                        ? "Output did not match expected output"
                        : "No expected output comparison"}
                  </p>
                </div>

                <div className="mt-4 space-y-4">
                  <div className="rounded-xl border border-slate-200 bg-white p-4">
                    <p className="text-sm font-semibold text-slate-700">Stdout</p>
                    <pre className="mt-2 whitespace-pre-wrap break-words text-sm text-slate-700">
                      {codeRunnerResult.stdout ? codeRunnerResult.stdout : "No stdout."}
                    </pre>
                  </div>

                  <div className="rounded-xl border border-slate-200 bg-white p-4">
                    <p className="text-sm font-semibold text-slate-700">Stderr</p>
                    <pre className="mt-2 whitespace-pre-wrap break-words text-sm text-slate-700">
                      {codeRunnerResult.stderr ? codeRunnerResult.stderr : "No stderr."}
                    </pre>
                  </div>

                  <div className="rounded-xl border border-slate-200 bg-white p-4">
                    <p className="text-sm font-semibold text-slate-700">Output</p>
                    <pre className="mt-2 whitespace-pre-wrap break-words text-sm text-slate-700">
                      {codeRunnerResult.output ? codeRunnerResult.output : "No output."}
                    </pre>
                  </div>
                </div>
              </div>
            )}

            {codeSubmitError && (
              <div
                className="mt-4 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800"
                role="alert"
              >
                {codeSubmitError}
              </div>
            )}

            {codeSubmitResult && (
              <div className="mt-6 rounded-2xl border border-slate-200 bg-slate-50 p-5">
                <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
                  <div>
                    <h3 className="text-lg font-semibold text-slate-900">Submit Result</h3>
                    <p className="mt-1 text-sm text-slate-600">{getCodeSubmitSummary(codeSubmitResult)}</p>
                    {codeSubmitResult.message && (
                      <p className="mt-1 text-sm text-slate-600">{codeSubmitResult.message}</p>
                    )}
                  </div>
                  <span className={`inline-flex w-fit rounded-full border px-3 py-1 text-xs font-semibold ${getCodeSubmitStatusClass(codeSubmitResult)}`}>
                    {codeSubmitResult.passed === true
                      ? "Accepted"
                      : codeSubmitResult.passed === false
                        ? "Not accepted"
                        : "Submitted"}
                  </span>
                </div>

                <div className="mt-4 grid grid-cols-1 gap-4 md:grid-cols-3">
                  <div className="rounded-xl bg-white p-4">
                    <p className="text-sm text-slate-500">Language</p>
                    <p className="mt-1 text-base font-semibold text-slate-900">
                      {codeSubmitResult.language ?? codeRunnerForm.language}
                    </p>
                  </div>
                  <div className="rounded-xl bg-white p-4">
                    <p className="text-sm text-slate-500">Exit Code</p>
                    <p className="mt-1 text-base font-semibold text-slate-900">
                      {codeSubmitResult.exitCode ?? "Unavailable"}
                    </p>
                  </div>
                  <div className="rounded-xl bg-white p-4">
                    <p className="text-sm text-slate-500">Runtime</p>
                    <p className="mt-1 text-base font-semibold text-slate-900">
                      {codeSubmitResult.runtimeMs ?? codeSubmitResult.runtimeMs === 0
                        ? `${codeSubmitResult.runtimeMs} ms`
                        : "Unavailable"}
                    </p>
                  </div>
                </div>

                <div className="mt-4 grid grid-cols-1 gap-4 md:grid-cols-2">
                  <div className="rounded-xl bg-white p-4">
                    <p className="text-sm text-slate-500">XP Awarded</p>
                    <p className="mt-1 text-base font-semibold text-slate-900">
                      {codeSubmitResult.xpAwarded ?? codeSubmitResult.xpAwarded === 0
                        ? codeSubmitResult.xpAwarded
                        : "Unavailable"}
                    </p>
                  </div>
                  <div className="rounded-xl bg-white p-4">
                    <p className="text-sm text-slate-500">First Accepted</p>
                    <p className="mt-1 text-base font-semibold text-slate-900">
                      {codeSubmitResult.firstAccepted === true
                        ? "Yes"
                        : codeSubmitResult.firstAccepted === false
                          ? "No"
                          : "Unavailable"}
                    </p>
                  </div>
                </div>

                {codeSubmitProfileMessage && (
                  <p className="mt-4 text-sm text-slate-600">{codeSubmitProfileMessage}</p>
                )}

                <div className="mt-4 space-y-4">
                  <div className="rounded-xl border border-slate-200 bg-white p-4">
                    <p className="text-sm font-semibold text-slate-700">Stdout</p>
                    <pre className="mt-2 whitespace-pre-wrap break-words text-sm text-slate-700">
                      {codeSubmitResult.stdout ? codeSubmitResult.stdout : "No stdout."}
                    </pre>
                  </div>

                  <div className="rounded-xl border border-slate-200 bg-white p-4">
                    <p className="text-sm font-semibold text-slate-700">Stderr</p>
                    <pre className="mt-2 whitespace-pre-wrap break-words text-sm text-slate-700">
                      {codeSubmitResult.stderr ? codeSubmitResult.stderr : "No stderr."}
                    </pre>
                  </div>

                  <div className="rounded-xl border border-slate-200 bg-white p-4">
                    <p className="text-sm font-semibold text-slate-700">Output</p>
                    <pre className="mt-2 whitespace-pre-wrap break-words text-sm text-slate-700">
                      {codeSubmitResult.output ? codeSubmitResult.output : "No output."}
                    </pre>
                  </div>
                </div>
              </div>
            )}

            <div className="mt-6 rounded-2xl border border-slate-200 bg-slate-50 p-5">
              <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
                <div>
                  <h3 className="text-lg font-semibold text-slate-900">Code Submission History</h3>
                  <p className="mt-1 text-sm text-slate-600">
                    Load your own recent submissions for the current problem ID.
                  </p>
                </div>
                <button
                  type="button"
                  onClick={handleLoadCodeSubmissionsHistory}
                  disabled={codeSubmissionsHistoryLoading || !codeRunnerForm.problemId.trim()}
                  className="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-900 transition hover:bg-slate-100 disabled:cursor-not-allowed disabled:bg-slate-100 disabled:text-slate-400"
                >
                  {codeSubmissionsHistoryLoading
                    ? "Loading Submissions..."
                    : codeSubmissionsHistoryLoaded
                      ? "Refresh Submissions"
                      : "Load Submissions"}
                </button>
              </div>

              {codeSubmissionsHistoryError && (
                <div
                  className="mt-4 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800"
                  role="alert"
                >
                  {codeSubmissionsHistoryError}
                </div>
              )}

              {codeSubmissionsHistoryLoading && !codeSubmissionsHistory && (
                <div className="mt-4 rounded-xl bg-white p-5">
                  <p className="text-sm text-slate-600">Loading code submissions...</p>
                </div>
              )}

              {codeSubmissionsHistory && (
                <div className="mt-4 flex flex-wrap items-center gap-3 text-sm text-slate-600">
                  <span>Page: {codeSubmissionsHistory.page ?? codeSubmissionsHistoryPage}</span>
                  <span>Size: {codeSubmissionsHistory.size ?? 20}</span>
                  <span>Total Items: {codeSubmissionsHistory.totalItems ?? 0}</span>
                  <span>Total Pages: {codeSubmissionsHistory.totalPages ?? 0}</span>
                </div>
              )}

              {codeSubmissionsHistoryLoaded &&
                !codeSubmissionsHistoryError &&
                (!Array.isArray(codeSubmissionsHistory?.items) || codeSubmissionsHistory.items.length === 0) && (
                  <div className="mt-4 rounded-xl bg-white p-5">
                    <p className="text-sm text-slate-600">No submissions found for this problem yet.</p>
                  </div>
                )}

              {Array.isArray(codeSubmissionsHistory?.items) && codeSubmissionsHistory.items.length > 0 && (
                <div className="mt-5 space-y-4">
                  {codeSubmissionsHistory.items.map((submission, index) => (
                    <div
                      key={submission.submissionId ?? `${submission.submittedAt ?? "submission"}-${index}`}
                      className="rounded-xl border border-slate-200 bg-white p-5"
                    >
                      <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
                        <div>
                          <h4 className="text-base font-semibold text-slate-900">
                            {submission.language ?? "Unknown language"}
                          </h4>
                          <p className="mt-1 text-sm text-slate-600">
                            {submission.submittedAt ? formatSavedTimestamp(submission.submittedAt) : "Submission time unavailable"}
                          </p>
                        </div>
                        <span className={`inline-flex w-fit rounded-full border px-3 py-1 text-xs font-semibold ${getSubmissionStatusClass(submission)}`}>
                          {getSubmissionStatusLabel(submission)}
                        </span>
                      </div>

                      <div className="mt-4 grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-4">
                        <div className="rounded-xl bg-slate-50 p-4">
                          <p className="text-sm text-slate-500">Visible Tests</p>
                          <p className="mt-1 text-base font-semibold text-slate-900">
                            {submission.passedTestCases ?? 0}/{submission.totalTestCases ?? 0} visible tests
                          </p>
                        </div>
                        <div className="rounded-xl bg-slate-50 p-4">
                          <p className="text-sm text-slate-500">Runtime</p>
                          <p className="mt-1 text-base font-semibold text-slate-900">
                            {submission.runtimeMs ?? submission.runtimeMs === 0
                              ? `${submission.runtimeMs} ms`
                              : "Runtime not available"}
                          </p>
                        </div>
                        <div className="rounded-xl bg-slate-50 p-4">
                          <p className="text-sm text-slate-500">Memory</p>
                          <p className="mt-1 text-base font-semibold text-slate-900">
                            {submission.memoryKb ?? submission.memoryKb === 0
                              ? `${submission.memoryKb} KB`
                              : "Memory not available"}
                          </p>
                        </div>
                        <div className="rounded-xl bg-slate-50 p-4">
                          <p className="text-sm text-slate-500">Problem ID</p>
                          <p className="mt-1 break-all text-base font-semibold text-slate-900">
                            {submission.problemId ?? codeRunnerForm.problemId.trim()}
                          </p>
                        </div>
                      </div>

                      <div className="mt-4 rounded-xl border border-slate-200 bg-slate-50 p-4">
                        <p className="text-sm font-semibold text-slate-700">Submitted Code</p>
                        <pre className="mt-2 max-h-64 overflow-auto whitespace-pre-wrap break-words text-sm text-slate-700">
                          {submission.code ? submission.code : "No submitted code."}
                        </pre>
                      </div>

                      <div className="mt-4 rounded-xl border border-slate-200 bg-slate-50 p-4">
                        <p className="text-sm font-semibold text-slate-700">AI Review</p>
                        <pre className="mt-2 max-h-48 overflow-auto whitespace-pre-wrap break-words text-sm text-slate-700">
                          {submission.aiReview ? submission.aiReview : "No AI review saved yet"}
                        </pre>
                      </div>
                    </div>
                  ))}
                </div>
              )}

              {codeSubmissionsHistory && (
                <div className="mt-5 flex flex-wrap items-center gap-3">
                  <button
                    type="button"
                    onClick={handlePreviousCodeSubmissionsPage}
                    disabled={codeSubmissionsHistoryLoading || codeSubmissionsHistoryPage <= 0}
                    className="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-900 transition hover:bg-slate-100 disabled:cursor-not-allowed disabled:bg-slate-100 disabled:text-slate-400"
                  >
                    Previous
                  </button>
                  <button
                    type="button"
                    onClick={handleNextCodeSubmissionsPage}
                    disabled={
                      codeSubmissionsHistoryLoading ||
                      (codeSubmissionsHistory?.totalPages ?? 0) <= 0 ||
                      codeSubmissionsHistoryPage >= (codeSubmissionsHistory?.totalPages ?? 0) - 1
                    }
                    className="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-900 transition hover:bg-slate-100 disabled:cursor-not-allowed disabled:bg-slate-100 disabled:text-slate-400"
                  >
                    Next
                  </button>
                </div>
              )}
            </div>
          </div>

          {/* Quiz attempt history */}
          <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
            <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
              <div>
                <h2 className="text-xl font-semibold text-slate-900 mb-1">Quiz Attempt History</h2>
                <p className="text-sm text-slate-600">
                  Review your recent quiz answers and the learning context returned by the backend.
                </p>
              </div>
              <button
                type="button"
                onClick={handleLoadQuizAttemptHistory}
                disabled={quizAttemptHistoryLoading}
                className="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-900 transition hover:bg-slate-100 disabled:cursor-not-allowed disabled:bg-slate-100 disabled:text-slate-400"
              >
                {quizAttemptHistoryLoading
                  ? "Loading Attempts..."
                  : quizAttemptHistoryLoaded
                    ? "Refresh Attempts"
                    : "Load Attempts"}
              </button>
            </div>

            {quizAttemptHistoryError && (
              <div
                className="mt-4 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800"
                role="alert"
              >
                {quizAttemptHistoryError}
              </div>
            )}

            {quizAttemptHistoryLoading && !quizAttemptHistoryLoaded && (
              <div className="mt-4 rounded-xl bg-slate-50 p-5">
                <p className="text-sm text-slate-600">Loading quiz attempt history...</p>
              </div>
            )}

            {quizAttemptHistoryLoaded && quizAttemptHistory.length === 0 && !quizAttemptHistoryError && (
              <div className="mt-4 rounded-xl bg-slate-50 p-5">
                <p className="text-sm text-slate-600">
                  No quiz attempts yet. Submit a quiz answer from a lesson to see history here.
                </p>
              </div>
            )}

            {quizAttemptHistory.length > 0 && (
              <div className="mt-5 space-y-4">
                {quizAttemptHistory.map((attempt, index) => (
                  <div
                    key={attempt.attemptId ?? `${attempt.quizQuestionId ?? "quiz"}-${attempt.attemptedAt ?? index}`}
                    className="rounded-xl border border-slate-200 bg-slate-50 p-5"
                  >
                    <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
                      <div>
                        <h3 className="text-base font-semibold text-slate-900">
                          {attempt.question || "Quiz question unavailable"}
                        </h3>
                        <p className="mt-2 text-sm text-slate-600">
                          {attempt.courseTitle || "Unknown course"} · {attempt.levelTitle || "Unknown level"}
                        </p>
                      </div>
                      <span className={`inline-flex w-fit rounded-full border px-3 py-1 text-xs font-semibold ${getAttemptStatusClass(Boolean(attempt.isCorrect))}`}>
                        {attempt.isCorrect ? "Correct" : "Incorrect"}
                      </span>
                    </div>

                    <div className="mt-4 grid grid-cols-1 gap-4 md:grid-cols-2">
                      <div className="rounded-xl bg-white p-4">
                        <p className="text-sm text-slate-500">Selected Answer</p>
                        <p className="mt-1 text-base font-semibold text-slate-900">
                          {attempt.selectedAnswer || "Unavailable"}
                        </p>
                      </div>
                      <div className="rounded-xl bg-white p-4">
                        <p className="text-sm text-slate-500">Attempted At</p>
                        <p className="mt-1 text-base font-semibold text-slate-900">
                          {formatAttemptedTimestamp(attempt.attemptedAt)}
                        </p>
                      </div>
                    </div>

                    {attempt.concept && (
                      <p className="mt-4 text-sm text-slate-700">
                        <span className="font-semibold text-slate-900">Concept:</span> {attempt.concept}
                      </p>
                    )}

                    {attempt.explanation && (
                      <div className="mt-4 rounded-xl border border-slate-200 bg-white p-4">
                        <p className="text-sm font-semibold text-slate-700">Explanation</p>
                        <p className="mt-2 text-sm leading-6 text-slate-600">{attempt.explanation}</p>
                      </div>
                    )}

                    {(attempt.attemptId || attempt.quizQuestionId) && (
                      <div className="mt-4 flex flex-col gap-1 text-xs text-slate-500">
                        {attempt.attemptId && <p>Attempt ID: {attempt.attemptId}</p>}
                        {attempt.quizQuestionId && <p>Quiz Question ID: {attempt.quizQuestionId}</p>}
                      </div>
                    )}
                  </div>
                ))}
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
              <strong>Dashboard shell:</strong> Course generation, lessons, quiz history, leaderboard loading, code runner, code submit, submission history, and AI review are wired to the current MVP foundation. Broader dashboard polish is still pending later tasks.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
