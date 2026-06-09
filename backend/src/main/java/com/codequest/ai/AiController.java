package com.codequest.ai;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codequest.ai.dto.ReviewCodeRequest;
import com.codequest.ai.dto.ReviewCodeResponse;
import com.codequest.common.security.CurrentUserPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiCodeReviewService aiCodeReviewService;

    public AiController(AiCodeReviewService aiCodeReviewService) {
        this.aiCodeReviewService = aiCodeReviewService;
    }

    @PostMapping("/review-code")
    @Operation(summary = "Review code", description = "Return structured Gemini-powered educational feedback for authenticated user code input")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Code review generated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
            @ApiResponse(responseCode = "429", description = "AI review rate limited"),
            @ApiResponse(responseCode = "502", description = "AI review response invalid"),
            @ApiResponse(responseCode = "503", description = "AI review unavailable")
    })
    public ResponseEntity<ReviewCodeResponse> reviewCode(
            @AuthenticationPrincipal CurrentUserPrincipal currentUser,
            @Valid @RequestBody ReviewCodeRequest request
    ) {
        ReviewCodeResponse response = aiCodeReviewService.reviewCode(request);
        return ResponseEntity.ok(response);
    }
}
