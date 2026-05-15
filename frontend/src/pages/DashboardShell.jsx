import { useEffect, useState } from "react";

import { generateCourse, getCourseById, getNoteForLevel, getQuizAttemptHistory, saveNoteForLevel, submitQuizAnswer } from "../services/courseApi";
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

export default function DashboardShell({ profile, onRefreshProfile, onBackHome }) {
  const [form, setForm] = useState(INITIAL_FORM);
  const [generationLoading, setGenerationLoading] = useState(false);
  const [generationError, setGenerationError] = useState("");
  const [generatedCourse, setGeneratedCourse] = useState(null);
  const [courseMapLoading, setCourseMapLoading] = useState(false);
  const [courseMapError, setCourseMapError] = useState("");
  const [courseMap, setCourseMap] = useState(null);
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
              <strong>Dashboard shell:</strong> Placeholder course generation is now wired to the backend foundation. AI, lessons, quizzes, XP, streak, leaderboard, and code execution are still not implemented yet.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
