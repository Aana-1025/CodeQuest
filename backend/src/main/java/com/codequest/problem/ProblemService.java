package com.codequest.problem;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codequest.common.exception.ApiException;
import com.codequest.common.exception.ErrorCode;
import com.codequest.problem.dto.CodeSubmissionHistoryItemResponse;
import com.codequest.problem.dto.CodeSubmissionHistoryResponse;
import com.codequest.problem.dto.PistonRequest;
import com.codequest.problem.dto.PistonResponse;
import com.codequest.problem.dto.RunCodeRequest;
import com.codequest.problem.dto.RunCodeResponse;
import com.codequest.problem.dto.SubmitCodeRequest;
import com.codequest.problem.dto.SubmitCodeResponse;
import com.codequest.progress.XPService;
import com.codequest.user.User;
import com.codequest.user.UserRepository;

@Service
public class ProblemService {

    private static final Map<String, String> LANGUAGE_VERSION_MAP = Map.of(
            "java", "15.0.2",
            "python", "3.10.0",
            "javascript", "18.15.0",
            "cpp", "10.2.0"
    );
    private static final int DEFAULT_CODING_PROBLEM_XP = 100;
    private static final int MVP_TOTAL_TEST_CASES = 1;
    private static final int DEFAULT_HISTORY_PAGE = 0;
    private static final int DEFAULT_HISTORY_SIZE = 20;
    private static final int MAX_HISTORY_SIZE = 50;

    private final PistonClient pistonClient;
    private final CodeSubmissionRepository codeSubmissionRepository;
    private final UserRepository userRepository;
    private final XPService xpService;

    public ProblemService(
            PistonClient pistonClient,
            CodeSubmissionRepository codeSubmissionRepository,
            UserRepository userRepository,
            XPService xpService
    ) {
        this.pistonClient = pistonClient;
        this.codeSubmissionRepository = codeSubmissionRepository;
        this.userRepository = userRepository;
        this.xpService = xpService;
    }

    public RunCodeResponse runCode(UUID problemId, RunCodeRequest request) {
        ExecutionResult executionResult = executeAndEvaluate(
                request.language(),
                request.code(),
                request.stdin(),
                request.expectedOutput()
        );

        String message = executionResult.exitCode() != null && executionResult.exitCode() == 0
                ? "Code executed successfully."
                : "Code execution finished with errors.";

        return new RunCodeResponse(
                problemId,
                executionResult.language(),
                executionResult.stdout(),
                executionResult.stderr(),
                executionResult.output(),
                executionResult.exitCode(),
                executionResult.runtimeMs(),
                executionResult.passed(),
                message
        );
    }

    @Transactional(readOnly = true)
    public CodeSubmissionHistoryResponse getSubmissionHistory(UUID userId, UUID problemId, int page, int size) {
        validatePagination(page, size);

        Page<CodeSubmission> submissionsPage = codeSubmissionRepository.findByUser_IdAndProblemId(
                userId,
                problemId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "submittedAt"))
        );

        List<CodeSubmissionHistoryItemResponse> items = submissionsPage.getContent().stream()
                .map(this::toCodeSubmissionHistoryItemResponse)
                .toList();

        return new CodeSubmissionHistoryResponse(
                problemId,
                page,
                size,
                submissionsPage.getTotalElements(),
                calculateTotalPages(submissionsPage.getTotalElements(), size),
                items
        );
    }

    @Transactional
    public SubmitCodeResponse submitCode(UUID userId, UUID problemId, SubmitCodeRequest request) {
        validateExpectedOutput(request.expectedOutput());

        ExecutionResult executionResult = executeAndEvaluate(
                request.language(),
                request.code(),
                request.stdin(),
                request.expectedOutput()
        );

        boolean passed = Boolean.TRUE.equals(executionResult.passed());
        boolean alreadyAccepted = passed && codeSubmissionRepository.existsByUser_IdAndProblemIdAndPassedTrue(userId, problemId);
        boolean firstAccepted = passed && !alreadyAccepted;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "User not found."));

        int xpAwarded = 0;
        if (firstAccepted) {
            xpAwarded = DEFAULT_CODING_PROBLEM_XP;
            xpService.addXpAndRecalculateRank(user, xpAwarded);
        }

        Instant now = Instant.now();
        codeSubmissionRepository.save(new CodeSubmission(
                UUID.randomUUID(),
                user,
                problemId,
                executionResult.language(),
                request.code(),
                passed,
                passed ? MVP_TOTAL_TEST_CASES : 0,
                MVP_TOTAL_TEST_CASES,
                executionResult.runtimeMs(),
                null,
                null,
                now,
                now,
                now
        ));

        return new SubmitCodeResponse(
                problemId,
                executionResult.language(),
                executionResult.stdout(),
                executionResult.stderr(),
                executionResult.output(),
                executionResult.exitCode(),
                executionResult.runtimeMs(),
                passed,
                xpAwarded,
                firstAccepted,
                buildSubmitMessage(passed, firstAccepted)
        );
    }

    private ExecutionResult executeAndEvaluate(String language, String code, String stdin, String expectedOutput) {
        String normalizedLanguage = normalizeLanguage(language);
        validateLanguage(normalizedLanguage);
        validateCode(code);

        PistonResponse pistonResponse = executeWithPiston(new PistonRequest(
                normalizedLanguage,
                LANGUAGE_VERSION_MAP.get(normalizedLanguage),
                List.of(new PistonRequest.PistonFile(code)),
                stdin == null ? "" : stdin
        ));

        String stdout = defaultString(pistonResponse.run().stdout());
        String stderr = chooseStderr(pistonResponse);
        String output = chooseOutput(pistonResponse, stdout);
        Integer exitCode = pistonResponse.run().code();

        return new ExecutionResult(
                normalizedLanguage,
                stdout,
                stderr,
                output,
                exitCode,
                null,
                compareExpectedOutput(expectedOutput, output)
        );
    }

    private PistonResponse executeWithPiston(PistonRequest pistonRequest) {
        try {
            return pistonClient.execute(pistonRequest);
        } catch (PistonException ex) {
            throw new ApiException(ErrorCode.CODE_RUNNER_UNAVAILABLE, "Code runner is currently unavailable. Please try again later.");
        }
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

    private void validateExpectedOutput(String expectedOutput) {
        if (expectedOutput == null || expectedOutput.isBlank()) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "Expected output must not be blank.");
        }
    }

    private void validatePagination(int page, int size) {
        if (page < DEFAULT_HISTORY_PAGE) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "Page must be greater than or equal to 0.");
        }
        if (size < 1) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "Size must be at least 1.");
        }
        if (size > MAX_HISTORY_SIZE) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "Size must be less than or equal to 50.");
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

    private String buildSubmitMessage(boolean passed, boolean firstAccepted) {
        if (!passed) {
            return "Solution did not match the expected output.";
        }
        if (firstAccepted) {
            return "Solution accepted. XP awarded.";
        }
        return "Solution accepted. XP was already awarded for this problem.";
    }

    private CodeSubmissionHistoryItemResponse toCodeSubmissionHistoryItemResponse(CodeSubmission codeSubmission) {
        return new CodeSubmissionHistoryItemResponse(
                codeSubmission.getId(),
                codeSubmission.getProblemId(),
                codeSubmission.getLanguage(),
                codeSubmission.getCode(),
                codeSubmission.isPassed(),
                codeSubmission.getPassedTestCases(),
                codeSubmission.getTotalTestCases(),
                codeSubmission.getRuntimeMs(),
                codeSubmission.getMemoryKb(),
                codeSubmission.getAiReview(),
                codeSubmission.getSubmittedAt()
        );
    }

    private int calculateTotalPages(long totalItems, int size) {
        if (totalItems == 0) {
            return 0;
        }
        return (int) Math.ceil((double) totalItems / size);
    }

    private record ExecutionResult(
            String language,
            String stdout,
            String stderr,
            String output,
            Integer exitCode,
            Integer runtimeMs,
            Boolean passed
    ) {
    }
}
