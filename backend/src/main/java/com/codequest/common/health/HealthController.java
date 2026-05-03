package com.codequest.common.health;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public ResponseEntity<HealthStatusResponse> getHealth() {
        return ResponseEntity.ok(new HealthStatusResponse("UP", "CodeQuest Backend"));
    }

    public record HealthStatusResponse(String status, String service) {
    }
}
