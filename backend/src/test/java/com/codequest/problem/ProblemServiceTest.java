package com.codequest.problem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
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

@ExtendWith(MockitoExtension.class)
class ProblemServiceTest {

    @Mock
    private PistonClient pistonClient;

    private ProblemService problemService;

    @BeforeEach
    void setUp() {
        problemService = new ProblemService(pistonClient);
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

    private PistonResponse successfulResponse(String stdout, String stderr, String output, Integer exitCode) {
        return new PistonResponse(
                "java",
                "15.0.2",
                null,
                new PistonResponse.PistonStage(stdout, stderr, output, exitCode)
        );
    }
}
