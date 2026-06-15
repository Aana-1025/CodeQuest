package com.codequest.level;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codequest.common.security.CurrentUserPrincipal;
import com.codequest.level.dto.LevelDetailsResponse;
import com.codequest.progress.ProgressService;
import com.codequest.progress.dto.LevelCompletionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/levels")
public class LevelController {

    private final ProgressService progressService;
    private final LevelService levelService;

    public LevelController(ProgressService progressService, LevelService levelService) {
        this.progressService = progressService;
        this.levelService = levelService;
    }

    @GetMapping("/{levelId}")
    @Operation(summary = "Get level details", description = "Fetch lesson content and safe study data for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Level details returned successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - level is locked"),
            @ApiResponse(responseCode = "404", description = "Level not found")
    })
    public ResponseEntity<LevelDetailsResponse> getLevelDetails(
            @AuthenticationPrincipal CurrentUserPrincipal currentUser,
            @PathVariable("levelId") UUID levelId
    ) {
        LevelDetailsResponse response = levelService.getLevelDetails(currentUser.userId(), levelId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{levelId}/complete")
    @Operation(summary = "Complete level", description = "Mark a level complete for the authenticated user and award lesson XP once")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Level completion processed successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - level is locked"),
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
