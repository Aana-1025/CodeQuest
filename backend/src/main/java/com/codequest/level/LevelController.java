package com.codequest.level;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codequest.common.security.CurrentUserPrincipal;
import com.codequest.progress.ProgressService;
import com.codequest.progress.dto.LevelCompletionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/levels")
public class LevelController {

    private final ProgressService progressService;

    public LevelController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @PostMapping("/{levelId}/complete")
    @Operation(summary = "Complete level", description = "Mark a level complete for the authenticated user and award lesson XP once")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Level completion processed successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
            @ApiResponse(responseCode = "404", description = "Level not found")
    })
    public ResponseEntity<LevelCompletionResponse> completeLevel(
            @AuthenticationPrincipal CurrentUserPrincipal currentUser,
            @PathVariable("levelId") UUID levelId
    ) {
        LevelCompletionResponse response = progressService.completeLevel(currentUser.userId(), levelId);
        return ResponseEntity.ok(response);
    }
}
