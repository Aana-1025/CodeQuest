package com.codequest.leaderboard;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codequest.common.security.CurrentUserPrincipal;
import com.codequest.leaderboard.dto.LeaderboardResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping
    @Operation(summary = "Get leaderboard", description = "Return leaderboard users sorted by XP with global rank positions and current user summary")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Leaderboard retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid page, size, or period"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token")
    })
    public ResponseEntity<LeaderboardResponse> getLeaderboard(
            @AuthenticationPrincipal CurrentUserPrincipal currentUser,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "50") int size,
            @RequestParam(name = "period", defaultValue = "ALL_TIME") String period
    ) {
        LeaderboardResponse response = leaderboardService.getLeaderboard(currentUser.userId(), page, size, period);
        return ResponseEntity.ok(response);
    }
}
