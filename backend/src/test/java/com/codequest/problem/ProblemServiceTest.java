package com.codequest.problem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.codequest.common.exception.ApiException;
import com.codequest.common.exception.ErrorCode;
import com.codequest.problem.dto.PistonRequest;
import com.codequest.problem.dto.PistonResponse;
import com.codequest.problem.dto.RunCodeRequest;
import com.codequest.problem.dto.RunCodeResponse;
import com.codequest.problem.dto.SubmitCodeRequest;
import com.codequest.problem.dto.SubmitCodeResponse;
import com.codequest.progress.XPService;
import com.codequest.user.User;
import com.codequest.user.UserRank;
import com.codequest.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class ProblemServiceTest {

    @Mock
    private PistonClient pistonClient;

    @Mock
    private CodeSubmissionRepository codeSubmissionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private XPService xpService;

    private ProblemService problemService;

    @BeforeEach
    void setUp() {
        problemService = new ProblemService(pistonClient, codeSubmissionRepository, userRepository, xpService);
    }

    @Test
    void runCode_shouldDelegateValidJavaExecutionToPistonAndReturnSafeResponse() {
        UUID problemId = UUID.randomUUID();
        RunCodeRequest request = new RunCodeRequest("java", "public class Main {}", "", "hello");
        when(pistonClient.execute(any(PistonRequest.class))).thenReturn(successfulResponse("hello\n", "", "hello\n", 0));

        RunCodeResponse response = problemService.runCode(problemId, request);

        ArgumentCaptor<PistonRequest> requestCaptor = ArgumentCaptor.forClass(PistonRequest.class);
        verify(pistonClient).execute(requestCaptor.capture());
        assertEquals("java", requestCaptor.getValue().language());
        assertEquals("15.0.2", requestCaptor.getValue().version());
        assertEquals(1, requestCaptor.getValue().files().size());
        assertEquals("public class Main {}", requestCaptor.getValue().files().get(0).content());

        assertEquals(problemId, response.problemId());
        assertEquals("java", response.language());
        assertEquals("hello\n", response.stdout());
        assertEquals("", response.stderr());
        assertEquals("hello\n", response.output());
        assertEquals(0, response.exitCode());
        assertNull(response.runtimeMs());
        assertTrue(response.passed());
        assertEquals("Code executed successfully.", response.message());
    }

    @Test
    void runCode_shouldReturnPassedTrueWhenTrimmedExpectedOutputMatches() {
        RunCodeRequest request = new RunCodeRequest("python", "print('Hello')", "", "Hello");
        when(pistonClient.execute(any(PistonRequest.class))).thenReturn(successfulResponse("Hello\n", "", "Hello\n", 0));

        RunCodeResponse response = problemService.runCode(UUID.randomUUID(), request);

        assertTrue(response.passed());
    }

    @Test
    void runCode_shouldReturnPassedFalseWhenExpectedOutputDoesNotMatch() {
        RunCodeRequest request = new RunCodeRequest("python", "print('Hello')", "", "Different");
        when(pistonClient.execute(any(PistonRequest.class))).thenReturn(successfulResponse("Hello\n", "", "Hello\n", 0));

        RunCodeResponse response = problemService.runCode(UUID.randomUUID(), request);

        assertFalse(response.passed());
    }

    @Test
    void runCode_shouldReturnPassedNullWhenExpectedOutputMissing() {
        RunCodeRequest request = new RunCodeRequest("javascript", "console.log('Hi')", "", null);
        when(pistonClient.execute(any(PistonRequest.class))).thenReturn(successfulResponse("Hi\n", "", "Hi\n", 0));

        RunCodeResponse response = problemService.runCode(UUID.randomUUID(), request);

        assertNull(response.passed());
    }

    @Test
    void runCode_shouldRejectDisallowedLanguageSafely() {
        RunCodeRequest request = new RunCodeRequest("ruby", "puts 'hi'", "", "hi");

        ApiException exception = assertThrows(ApiException.class, () -> problemService.runCode(UUID.randomUUID(), request));

        assertEquals(ErrorCode.BAD_REQUEST, exception.getErrorCode());
        assertEquals("Language must be one of: java, python, javascript, cpp.", exception.getMessage());
        verify(pistonClient, never()).execute(any(PistonRequest.class));
    }

    @Test
    void runCode_shouldRejectBlankCodeSafely() {
        RunCodeRequest request = new RunCodeRequest("java", "   ", "", "");

        ApiException exception = assertThrows(ApiException.class, () -> problemService.runCode(UUID.randomUUID(), request));

        assertEquals(ErrorCode.BAD_REQUEST, exception.getErrorCode());
        assertEquals("Code must not be blank.", exception.getMessage());
        verify(pistonClient, never()).execute(any(PistonRequest.class));
    }

    @Test
    void runCode_shouldRejectCodeLongerThan20000CharsSafely() {
        String oversizedCode = "a".repeat(20001);
        RunCodeRequest request = new RunCodeRequest("cpp", oversizedCode, "", "");

        ApiException exception = assertThrows(ApiException.class, () -> problemService.runCode(UUID.randomUUID(), request));

        assertEquals(ErrorCode.BAD_REQUEST, exception.getErrorCode());
        assertEquals("Code must not exceed 20000 characters.", exception.getMessage());
        verify(pistonClient, never()).execute(any(PistonRequest.class));
    }

    @Test
    void runCode_shouldMapPistonFailureToSafeCodeRunnerUnavailableException() {
        RunCodeRequest request = new RunCodeRequest("java", "public class Main {}", "", "");
        when(pistonClient.execute(any(PistonRequest.class))).thenThrow(
                new PistonException(PistonException.Category.REQUEST_FAILURE, "boom")
        );

        ApiException exception = assertThrows(ApiException.class, () -> problemService.runCode(UUID.randomUUID(), request));

        assertEquals(ErrorCode.CODE_RUNNER_UNAVAILABLE, exception.getErrorCode());
        assertEquals("Code runner is currently unavailable. Please try again later.", exception.getMessage());
    }

    @Test
    void runCode_shouldUseCompileOutputWhenRunOutputMissing() {
        RunCodeRequest request = new RunCodeRequest("java", "public class Main {}", "", "compile failed");
        when(pistonClient.execute(any(PistonRequest.class))).thenReturn(new PistonResponse(
                "java",
                "15.0.2",
                new PistonResponse.PistonStage("", "compile error", "compile failed\n", 1),
                new PistonResponse.PistonStage("", "", "", 1)
        ));

        RunCodeResponse response = problemService.runCode(UUID.randomUUID(), request);

        assertEquals("compile failed\n", response.output());
        assertEquals("compile error", response.stderr());
        assertTrue(response.passed());
    }

    @Test
    void submitCode_shouldPersistPassedSubmissionAndAwardXpOnFirstAcceptedAttempt() {
        UUID userId = UUID.randomUUID();
        UUID problemId = UUID.randomUUID();
        User user = createUser(userId);
        SubmitCodeRequest request = new SubmitCodeRequest("java", "public class Main {}", "", "hello");
        when(pistonClient.execute(any(PistonRequest.class))).thenReturn(successfulResponse("hello\n", "", "hello\n", 0));
        when(codeSubmissionRepository.existsByUser_IdAndProblemIdAndPassedTrue(userId, problemId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        SubmitCodeResponse response = problemService.submitCode(userId, problemId, request);

        assertTrue(response.passed());
        assertEquals(100, response.xpAwarded());
        assertTrue(response.firstAccepted());
        assertEquals("Solution accepted. XP awarded.", response.message());
        verify(xpService).addXpAndRecalculateRank(user, 100);

        ArgumentCaptor<CodeSubmission> submissionCaptor = ArgumentCaptor.forClass(CodeSubmission.class);
        verify(codeSubmissionRepository).save(submissionCaptor.capture());
        assertEquals(problemId, submissionCaptor.getValue().getProblemId());
        assertEquals("java", submissionCaptor.getValue().getLanguage());
        assertTrue(submissionCaptor.getValue().isPassed());
        assertEquals(1, submissionCaptor.getValue().getPassedTestCases());
        assertEquals(1, submissionCaptor.getValue().getTotalTestCases());
    }

    @Test
    void submitCode_shouldPersistRepeatedAcceptedSubmissionWithoutAwardingXpAgain() {
        UUID userId = UUID.randomUUID();
        UUID problemId = UUID.randomUUID();
        User user = createUser(userId);
        SubmitCodeRequest request = new SubmitCodeRequest("python", "print('Hello')", "", "Hello");
        when(pistonClient.execute(any(PistonRequest.class))).thenReturn(successfulResponse("Hello\n", "", "Hello\n", 0));
        when(codeSubmissionRepository.existsByUser_IdAndProblemIdAndPassedTrue(userId, problemId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        SubmitCodeResponse response = problemService.submitCode(userId, problemId, request);

        assertTrue(response.passed());
        assertEquals(0, response.xpAwarded());
        assertFalse(response.firstAccepted());
        assertEquals("Solution accepted. XP was already awarded for this problem.", response.message());
        verify(xpService, never()).addXpAndRecalculateRank(any(User.class), eq(100));
        verify(codeSubmissionRepository).save(any(CodeSubmission.class));
    }

    @Test
    void submitCode_shouldPersistFailedSubmissionWithoutAwardingXp() {
        UUID userId = UUID.randomUUID();
        UUID problemId = UUID.randomUUID();
        User user = createUser(userId);
        SubmitCodeRequest request = new SubmitCodeRequest("javascript", "console.log('Hi')", "", "Different");
        when(pistonClient.execute(any(PistonRequest.class))).thenReturn(successfulResponse("Hi\n", "", "Hi\n", 0));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        SubmitCodeResponse response = problemService.submitCode(userId, problemId, request);

        assertFalse(response.passed());
        assertEquals(0, response.xpAwarded());
        assertFalse(response.firstAccepted());
        assertEquals("Solution did not match the expected output.", response.message());
        verify(codeSubmissionRepository, never()).existsByUser_IdAndProblemIdAndPassedTrue(any(UUID.class), any(UUID.class));
        verify(xpService, never()).addXpAndRecalculateRank(any(User.class), anyInt());

        ArgumentCaptor<CodeSubmission> submissionCaptor = ArgumentCaptor.forClass(CodeSubmission.class);
        verify(codeSubmissionRepository).save(submissionCaptor.capture());
        assertFalse(submissionCaptor.getValue().isPassed());
        assertEquals(0, submissionCaptor.getValue().getPassedTestCases());
        assertEquals(1, submissionCaptor.getValue().getTotalTestCases());
    }

    @Test
    void submitCode_shouldRejectDisallowedLanguageSafely() {
        SubmitCodeRequest request = new SubmitCodeRequest("ruby", "puts 'hi'", "", "hi");

        ApiException exception = assertThrows(ApiException.class, () -> problemService.submitCode(UUID.randomUUID(), UUID.randomUUID(), request));

        assertEquals(ErrorCode.BAD_REQUEST, exception.getErrorCode());
        assertEquals("Language must be one of: java, python, javascript, cpp.", exception.getMessage());
        verify(pistonClient, never()).execute(any(PistonRequest.class));
        verify(codeSubmissionRepository, never()).save(any(CodeSubmission.class));
    }

    @Test
    void submitCode_shouldRejectBlankCodeSafely() {
        SubmitCodeRequest request = new SubmitCodeRequest("java", " ", "", "hi");

        ApiException exception = assertThrows(ApiException.class, () -> problemService.submitCode(UUID.randomUUID(), UUID.randomUUID(), request));

        assertEquals(ErrorCode.BAD_REQUEST, exception.getErrorCode());
        assertEquals("Code must not be blank.", exception.getMessage());
        verify(pistonClient, never()).execute(any(PistonRequest.class));
        verify(codeSubmissionRepository, never()).save(any(CodeSubmission.class));
    }

    @Test
    void submitCode_shouldRejectCodeLongerThan20000CharsSafely() {
        SubmitCodeRequest request = new SubmitCodeRequest("cpp", "a".repeat(20001), "", "hi");

        ApiException exception = assertThrows(ApiException.class, () -> problemService.submitCode(UUID.randomUUID(), UUID.randomUUID(), request));

        assertEquals(ErrorCode.BAD_REQUEST, exception.getErrorCode());
        assertEquals("Code must not exceed 20000 characters.", exception.getMessage());
        verify(pistonClient, never()).execute(any(PistonRequest.class));
        verify(codeSubmissionRepository, never()).save(any(CodeSubmission.class));
    }

    @Test
    void submitCode_shouldMapPistonFailureToSafeCodeRunnerUnavailableExceptionWithoutPersistingOrAwardingXp() {
        SubmitCodeRequest request = new SubmitCodeRequest("java", "public class Main {}", "", "hi");
        when(pistonClient.execute(any(PistonRequest.class))).thenThrow(
                new PistonException(PistonException.Category.REQUEST_FAILURE, "boom")
        );

        ApiException exception = assertThrows(ApiException.class, () -> problemService.submitCode(UUID.randomUUID(), UUID.randomUUID(), request));

        assertEquals(ErrorCode.CODE_RUNNER_UNAVAILABLE, exception.getErrorCode());
        assertEquals("Code runner is currently unavailable. Please try again later.", exception.getMessage());
        verify(codeSubmissionRepository, never()).save(any(CodeSubmission.class));
        verify(xpService, never()).addXpAndRecalculateRank(any(User.class), anyInt());
    }

    private User createUser(UUID userId) {
        User user = new User();
        user.setId(userId);
        user.setName("Problem Test");
        user.setEmail("problem@example.com");
        user.setPasswordHash("hash");
        user.setXp(0);
        user.setRank(UserRank.BEGINNER);
        return user;
    }

    private PistonResponse successfulResponse(String stdout, String stderr, String output, Integer exitCode) {
        return new PistonResponse(
                "java",
                "15.0.2",
                null,
                new PistonResponse.PistonStage(stdout, stderr, output, exitCode)
        );
    }
}
