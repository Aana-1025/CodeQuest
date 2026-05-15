package com.codequest.quiz;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codequest.common.security.CurrentUserPrincipal;
import com.codequest.quiz.dto.SubmitQuizAnswerRequest;
import com.codequest.quiz.dto.SubmitQuizAnswerResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping("/{quizQuestionId}/submit")
    @Operation(summary = "Submit quiz answer", description = "Submit one answer for a quiz question and receive a safe correctness result")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quiz answer scored successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
            @ApiResponse(responseCode = "404", description = "Quiz question not found")
    })
    public ResponseEntity<SubmitQuizAnswerResponse> submitQuizAnswer(
            @AuthenticationPrincipal CurrentUserPrincipal currentUser,
            @PathVariable("quizQuestionId") UUID quizQuestionId,
            @Valid @RequestBody SubmitQuizAnswerRequest request
    ) {
        SubmitQuizAnswerResponse response = quizService.submitAnswer(currentUser.userId(), quizQuestionId, request.selectedAnswer());
        return ResponseEntity.ok(response);
    }
}
