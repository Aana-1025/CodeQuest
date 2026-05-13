package com.codequest.quiz;

import java.util.Locale;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codequest.common.exception.ApiException;
import com.codequest.common.exception.ErrorCode;
import com.codequest.quiz.dto.SubmitQuizAnswerResponse;

@Service
public class QuizService {

    private final QuizRepository quizRepository;

    public QuizService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Transactional(readOnly = true)
    public SubmitQuizAnswerResponse submitAnswer(UUID quizQuestionId, String selectedAnswer) {
        Quiz quiz = quizRepository.findById(quizQuestionId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "Quiz question not found."));

        String normalizedSelectedAnswer = normalizeSelectedAnswer(selectedAnswer);
        boolean isCorrect = normalizedSelectedAnswer.equals(quiz.getCorrectAnswer());

        return new SubmitQuizAnswerResponse(
                quiz.getId(),
                normalizedSelectedAnswer,
                isCorrect,
                quiz.getExplanation(),
                quiz.getConceptTag()
        );
    }

    private String normalizeSelectedAnswer(String selectedAnswer) {
        return selectedAnswer.trim().toUpperCase(Locale.ROOT);
    }
}
