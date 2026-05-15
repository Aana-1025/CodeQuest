package com.codequest.progress;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codequest.common.security.CurrentUserPrincipal;
import com.codequest.progress.dto.CourseProgressResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @GetMapping("/courses/{courseId}")
    @Operation(summary = "Get course progress", description = "Return progress state for the authenticated user and a specific course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course progress returned successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    public ResponseEntity<CourseProgressResponse> getCourseProgress(
            @AuthenticationPrincipal CurrentUserPrincipal currentUser,
            @PathVariable("courseId") UUID courseId
    ) {
        CourseProgressResponse response = progressService.getCourseProgress(currentUser.userId(), courseId);
        return ResponseEntity.ok(response);
    }
}
