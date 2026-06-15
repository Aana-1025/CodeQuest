package com.codequest.level;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codequest.common.exception.ApiException;
import com.codequest.common.exception.ErrorCode;
import com.codequest.flashcard.Flashcard;
import com.codequest.flashcard.FlashcardRepository;
import com.codequest.flashcard.dto.FlashcardResponse;
import com.codequest.level.dto.LevelDetailsResponse;
import com.codequest.problem.CodingProblem;
import com.codequest.problem.CodingProblemRepository;
import com.codequest.problem.dto.CodingProblemResponse;
import com.codequest.progress.ProgressService;
import com.codequest.quiz.Quiz;
import com.codequest.quiz.QuizRepository;
import com.codequest.quiz.dto.QuizOptionsResponse;
import com.codequest.quiz.dto.QuizQuestionResponse;

@Service
public class LevelService {

    private final LevelRepository levelRepository;
    private final QuizRepository quizRepository;
    private final FlashcardRepository flashcardRepository;
    private final CodingProblemRepository codingProblemRepository;
    private final ProgressService progressService;

    public LevelService(
            LevelRepository levelRepository,
            QuizRepository quizRepository,
            FlashcardRepository flashcardRepository,
            CodingProblemRepository codingProblemRepository,
            ProgressService progressService
    ) {
        this.levelRepository = levelRepository;
        this.quizRepository = quizRepository;
        this.flashcardRepository = flashcardRepository;
        this.codingProblemRepository = codingProblemRepository;
        this.progressService = progressService;
    }

    @Transactional(readOnly = true)
    public LevelDetailsResponse getLevelDetails(UUID userId, UUID levelId) {
        Level level = levelRepository.findById(levelId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "Level not found."));

        ProgressService.LevelReadState levelReadState = progressService.getLevelReadState(userId, level);
        if (!levelReadState.unlocked()) {
            throw new ApiException(ErrorCode.FORBIDDEN, "Complete previous levels before opening this lesson.");
        }

        List<UUID> levelIds = List.of(level.getId());
        List<QuizQuestionResponse> quizQuestions = mapQuizQuestions(levelIds).getOrDefault(level.getId(), List.of());
        List<FlashcardResponse> flashcards = mapFlashcards(levelIds).getOrDefault(level.getId(), List.of());
        List<CodingProblemResponse> codingProblems = mapCodingProblems(levelIds).getOrDefault(level.getId(), List.of());

        return new LevelDetailsResponse(
                level.getId(),
                level.getCourse().getId(),
                level.getCourse().getTitle(),
                level.getOrderNumber() == null ? 0 : level.getOrderNumber(),
                level.getTitle(),
                level.getContentMarkdown(),
                level.getXpReward() == null ? 0 : level.getXpReward(),
                level.isBoss(),
                levelReadState.completed(),
                levelReadState.unlocked(),
                levelReadState.completedAt(),
                quizQuestions,
                flashcards,
                codingProblems
        );
    }

    private Map<UUID, List<QuizQuestionResponse>> mapQuizQuestions(List<UUID> levelIds) {
        List<Quiz> quizzes = quizRepository.findByLevelIdInOrderByLevelIdAscOrderNumberAsc(levelIds);
        if (quizzes.isEmpty()) {
            return Map.of();
        }

        return quizzes.stream()
                .collect(Collectors.groupingBy(
                        quiz -> quiz.getLevel().getId(),
                        LinkedHashMap::new,
                        Collectors.mapping(this::toQuizQuestionResponse, Collectors.toList())
                ));
    }

    private Map<UUID, List<FlashcardResponse>> mapFlashcards(List<UUID> levelIds) {
        List<Flashcard> flashcards = flashcardRepository.findByLevelIdInOrderByLevelIdAscOrderNumberAsc(levelIds);
        if (flashcards.isEmpty()) {
            return Map.of();
        }

        return flashcards.stream()
                .collect(Collectors.groupingBy(
                        flashcard -> flashcard.getLevel().getId(),
                        LinkedHashMap::new,
                        Collectors.mapping(this::toFlashcardResponse, Collectors.toList())
                ));
    }

    private Map<UUID, List<CodingProblemResponse>> mapCodingProblems(List<UUID> levelIds) {
        List<CodingProblem> codingProblems = codingProblemRepository.findByLevelIdInOrderByLevelIdAscCreatedAtAsc(levelIds);
        if (codingProblems.isEmpty()) {
            return Map.of();
        }

        return codingProblems.stream()
                .collect(Collectors.groupingBy(
                        codingProblem -> codingProblem.getLevel().getId(),
                        LinkedHashMap::new,
                        Collectors.mapping(this::toCodingProblemResponse, Collectors.toList())
                ));
    }

    private QuizQuestionResponse toQuizQuestionResponse(Quiz quiz) {
        return new QuizQuestionResponse(
                quiz.getId(),
                quiz.getOrderNumber(),
                quiz.getQuestion(),
                new QuizOptionsResponse(
                        quiz.getOptionA(),
                        quiz.getOptionB(),
                        quiz.getOptionC(),
                        quiz.getOptionD()
                ),
                quiz.getExplanation(),
                quiz.getConceptTag(),
                quiz.getXpReward()
        );
    }

    private FlashcardResponse toFlashcardResponse(Flashcard flashcard) {
        return new FlashcardResponse(
                flashcard.getId(),
                flashcard.getOrderNumber(),
                flashcard.getFront(),
                flashcard.getBack(),
                flashcard.getConceptTag()
        );
    }

    private CodingProblemResponse toCodingProblemResponse(CodingProblem codingProblem) {
        return new CodingProblemResponse(
                codingProblem.getId(),
                codingProblem.getTitle(),
                codingProblem.getDescription(),
                codingProblem.getDifficulty(),
                codingProblem.getXpReward() == null ? 0 : codingProblem.getXpReward(),
                copyStarterCode(codingProblem.getStarterCodeJson()),
                copyTestCases(codingProblem.getTestCasesJson())
        );
    }

    private Map<String, String> copyStarterCode(Map<String, String> starterCode) {
        if (starterCode == null || starterCode.isEmpty()) {
            return Map.of();
        }

        return Map.copyOf(starterCode);
    }

    private List<Map<String, String>> copyTestCases(List<Map<String, String>> testCases) {
        if (testCases == null || testCases.isEmpty()) {
            return List.of();
        }

        return testCases.stream()
                .map(Map::copyOf)
                .toList();
    }
}
