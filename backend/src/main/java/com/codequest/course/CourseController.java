package com.codequest.course;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codequest.common.security.CurrentUserPrincipal;
import com.codequest.course.dto.GenerateCourseRequest;
import com.codequest.course.dto.GenerateCourseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping("/generate")
    @Operation(summary = "Generate or fetch course", description = "Generate or return a cached course for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course generated or fetched successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token")
    })
    public ResponseEntity<GenerateCourseResponse> generateCourse(
            @AuthenticationPrincipal CurrentUserPrincipal currentUser,
            @Valid @RequestBody GenerateCourseRequest request
    ) {
        GenerateCourseResponse response = courseService.generateCourse(currentUser.userId(), request);
        return ResponseEntity.ok(response);
    }
}
