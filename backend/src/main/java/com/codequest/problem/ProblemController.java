package com.codequest.problem;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codequest.common.security.CurrentUserPrincipal;
import com.codequest.problem.dto.RunCodeRequest;
import com.codequest.problem.dto.RunCodeResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/problems")
public class ProblemController {

    private final ProblemService problemService;

    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @PostMapping("/{problemId}/run")
    @Operation(summary = "Run code", description = "Execute code through the configured code runner without awarding XP or persisting a submission")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Code executed successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
            @ApiResponse(responseCode = "503", description = "Code runner unavailable")
    })
    public ResponseEntity<RunCodeResponse> runCode(
            @AuthenticationPrincipal CurrentUserPrincipal currentUser,
            @PathVariable("problemId") UUID problemId,
            @Valid @RequestBody RunCodeRequest request
    ) {
        RunCodeResponse response = problemService.runCode(problemId, request);
        return ResponseEntity.ok(response);
    }
}
