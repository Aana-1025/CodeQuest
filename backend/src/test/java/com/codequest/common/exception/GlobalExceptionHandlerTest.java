package com.codequest.common.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void shouldReturnValidationErrorForInvalidRequest() throws Exception {
        mockMvc.perform(post("/api/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("$.message").value("Invalid request."))
                .andExpect(jsonPath("$.path").value("/api/test"))
                .andExpect(jsonPath("$.requestId").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldReturnInternalErrorForGenericException() throws Exception {
        mockMvc.perform(post("/api/test/generic-error")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.code").value(ErrorCode.INTERNAL_ERROR.name()))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred."))
                .andExpect(jsonPath("$.path").value("/api/test/generic-error"))
                .andExpect(jsonPath("$.requestId").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @RestController
    static class TestController {

        @PostMapping("/api/test")
        void triggerValidationError(@Valid @RequestBody TestRequest request) {
            // Test-only endpoint for validation failure.
        }

        @PostMapping("/api/test/generic-error")
        void triggerGenericError() {
            throw new RuntimeException("Test exception");
        }
    }

    record TestRequest(@NotBlank String requiredField) {
    }
}