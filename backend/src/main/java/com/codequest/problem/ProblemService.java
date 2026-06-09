package com.codequest.problem;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.codequest.common.exception.ApiException;
import com.codequest.common.exception.ErrorCode;
import com.codequest.problem.dto.PistonRequest;
import com.codequest.problem.dto.PistonResponse;
import com.codequest.problem.dto.RunCodeRequest;
import com.codequest.problem.dto.RunCodeResponse;

@Service
public class ProblemService {

    private static final Map<String, String> LANGUAGE_VERSION_MAP = Map.of(
            "java", "15.0.2",
            "python", "3.10.0",
            "javascript", "18.15.0",
            "cpp", "10.2.0"
    );

    private final PistonClient pistonClient;

    public ProblemService(PistonClient pistonClient) {
        this.pistonClient = pistonClient;
    }

    public RunCodeResponse runCode(UUID problemId, RunCodeRequest request) {
        String normalizedLanguage = normalizeLanguage(request.language());
        validateLanguage(normalizedLanguage);
        validateCode(request.code());

        PistonRequest pistonRequest = new PistonRequest(
                normalizedLanguage,
                LANGUAGE_VERSION_MAP.get(normalizedLanguage),
                List.of(new PistonRequest.PistonFile(request.code())),
                request.stdin() == null ? "" : request.stdin()
        );

        PistonResponse pistonResponse;
        try {
            pistonResponse = pistonClient.execute(pistonRequest);
        } catch (PistonException ex) {
            throw new ApiException(ErrorCode.CODE_RUNNER_UNAVAILABLE, "Code runner is currently unavailable. Please try again later.");
        }

        String stdout = defaultString(pistonResponse.run().stdout());
        String stderr = chooseStderr(pistonResponse);
        String output = chooseOutput(pistonResponse, stdout);
        Integer exitCode = pistonResponse.run().code();
        Boolean passed = compareExpectedOutput(request.expectedOutput(), output);
        String message = exitCode != null && exitCode == 0
                ? "Code executed successfully."
                : "Code execution finished with errors.";

        return new RunCodeResponse(
                problemId,
                normalizedLanguage,
                stdout,
                stderr,
                output,
                exitCode,
                null,
                passed,
                message
        );
    }

    private String normalizeLanguage(String language) {
        return language == null ? "" : language.trim().toLowerCase();
    }

    private void validateLanguage(String language) {
        if (!LANGUAGE_VERSION_MAP.containsKey(language)) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "Language must be one of: java, python, javascript, cpp.");
        }
    }

    private void validateCode(String code) {
        if (code == null || code.isBlank()) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "Code must not be blank.");
        }
        if (code.length() > 20000) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "Code must not exceed 20000 characters.");
        }
    }

    private String chooseOutput(PistonResponse response, String stdout) {
        if (hasText(response.run().output())) {
            return response.run().output();
        }
        if (hasText(stdout)) {
            return stdout;
        }
        if (response.compile() != null && hasText(response.compile().output())) {
            return response.compile().output();
        }
        return "";
    }

    private String chooseStderr(PistonResponse response) {
        if (hasText(response.run().stderr())) {
            return response.run().stderr();
        }
        if (response.compile() != null && hasText(response.compile().stderr())) {
            return response.compile().stderr();
        }
        return "";
    }

    private Boolean compareExpectedOutput(String expectedOutput, String actualOutput) {
        if (expectedOutput == null) {
            return null;
        }
        return actualOutput.trim().equals(expectedOutput.trim());
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }
}
