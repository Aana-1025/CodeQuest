package com.codequest.note;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.codequest.auth.dto.LoginRequest;
import com.codequest.auth.dto.LoginResponse;
import com.codequest.auth.dto.RegisterRequest;
import com.codequest.course.CourseDifficulty;
import com.codequest.course.dto.GenerateCourseRequest;
import com.codequest.course.dto.GenerateCourseResponse;
import com.codequest.level.Level;
import com.codequest.level.LevelRepository;
import com.codequest.user.User;
import com.codequest.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LevelRepository levelRepository;

    @Test
    void shouldSaveNewNoteForAuthenticatedUser() throws Exception {
        LoginResponse loginResponse = registerAndLogin("newnote-" + System.currentTimeMillis() + "@example.com");
        Level level = createLevelForAuthenticatedUser(loginResponse.accessToken(), "Notes Binary Search");

        mockMvc.perform(post("/api/notes")
                        .header("Authorization", "Bearer " + loginResponse.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "levelId": "%s",
                                  "content": "My first note for this level"
                                }
                                """.formatted(level.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.noteId").exists())
                .andExpect(jsonPath("$.levelId").value(level.getId().toString()))
                .andExpect(jsonPath("$.content").value("My first note for this level"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void shouldUpdateExistingNoteForSameUserAndLevelWithoutCreatingDuplicate() throws Exception {
        LoginResponse loginResponse = registerAndLogin("updatenote-" + System.currentTimeMillis() + "@example.com");
        User user = userRepository.findByEmail(loginResponse.email()).orElseThrow();
        Level level = createLevelForAuthenticatedUser(loginResponse.accessToken(), "Update Note Level");

        String firstResponse = mockMvc.perform(post("/api/notes")
                        .header("Authorization", "Bearer " + loginResponse.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "levelId": "%s",
                                  "content": "First note content"
                                }
                                """.formatted(level.getId())))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String secondResponse = mockMvc.perform(post("/api/notes")
                        .header("Authorization", "Bearer " + loginResponse.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "levelId": "%s",
                                  "content": "Updated note content"
                                }
                                """.formatted(level.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated note content"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        com.codequest.note.dto.NoteResponse firstNoteResponse = objectMapper.readValue(firstResponse, com.codequest.note.dto.NoteResponse.class);
        com.codequest.note.dto.NoteResponse secondNoteResponse = objectMapper.readValue(secondResponse, com.codequest.note.dto.NoteResponse.class);

        assertEquals(1L, noteRepository.countByUserIdAndLevelId(user.getId(), level.getId()));
        assertEquals(firstNoteResponse.noteId(), secondNoteResponse.noteId());
    }

    @Test
    void shouldAllowDifferentUsersToSaveSeparateNotesForSameLevel() throws Exception {
        LoginResponse firstLoginResponse = registerAndLogin("noteuserone-" + System.currentTimeMillis() + "@example.com");
        LoginResponse secondLoginResponse = registerAndLogin("noteusertwo-" + System.currentTimeMillis() + "@example.com");
        User firstUser = userRepository.findByEmail(firstLoginResponse.email()).orElseThrow();
        User secondUser = userRepository.findByEmail(secondLoginResponse.email()).orElseThrow();
        Level level = createLevelForAuthenticatedUser(firstLoginResponse.accessToken(), "Shared Note Level");

        mockMvc.perform(post("/api/notes")
                        .header("Authorization", "Bearer " + firstLoginResponse.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "levelId": "%s",
                                  "content": "First user note"
                                }
                                """.formatted(level.getId())))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/notes")
                        .header("Authorization", "Bearer " + secondLoginResponse.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "levelId": "%s",
                                  "content": "Second user note"
                                }
                                """.formatted(level.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Second user note"));

        assertEquals(1L, noteRepository.countByUserIdAndLevelId(firstUser.getId(), level.getId()));
        assertEquals(1L, noteRepository.countByUserIdAndLevelId(secondUser.getId(), level.getId()));
    }

    @Test
    void shouldReturn404WhenLevelMissing() throws Exception {
        LoginResponse loginResponse = registerAndLogin("missinglevelnote-" + System.currentTimeMillis() + "@example.com");
        UUID missingLevelId = UUID.randomUUID();

        mockMvc.perform(post("/api/notes")
                        .header("Authorization", "Bearer " + loginResponse.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "levelId": "%s",
                                  "content": "This level does not exist"
                                }
                                """.formatted(missingLevelId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Level not found."))
                .andExpect(jsonPath("$.path").value("/api/notes"));
    }

    @Test
    void shouldReturn400WhenContentBlank() throws Exception {
        LoginResponse loginResponse = registerAndLogin("blanknote-" + System.currentTimeMillis() + "@example.com");
        Level level = createLevelForAuthenticatedUser(loginResponse.accessToken(), "Blank Note Level");

        mockMvc.perform(post("/api/notes")
                        .header("Authorization", "Bearer " + loginResponse.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "levelId": "%s",
                                  "content": "   "
                                }
                                """.formatted(level.getId())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void shouldReturn400WhenContentTooLong() throws Exception {
        LoginResponse loginResponse = registerAndLogin("longnote-" + System.currentTimeMillis() + "@example.com");
        Level level = createLevelForAuthenticatedUser(loginResponse.accessToken(), "Long Note Level");
        String longContent = "a".repeat(5001);

        mockMvc.perform(post("/api/notes")
                        .header("Authorization", "Bearer " + loginResponse.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new com.codequest.note.dto.SaveNoteRequest(level.getId(), longContent))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void shouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "levelId": "%s",
                                  "content": "Unauthenticated note"
                                }
                                """.formatted(UUID.randomUUID())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldIgnoreUnknownUserIdFieldInRequestBodyAndUseAuthenticatedUser() throws Exception {
        LoginResponse loginResponse = registerAndLogin("nouseridoverride-" + System.currentTimeMillis() + "@example.com");
        User authenticatedUser = userRepository.findByEmail(loginResponse.email()).orElseThrow();
        Level level = createLevelForAuthenticatedUser(loginResponse.accessToken(), "No Override Level");

        mockMvc.perform(post("/api/notes")
                        .header("Authorization", "Bearer " + loginResponse.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": "%s",
                                  "levelId": "%s",
                                  "content": "Authenticated user note"
                                }
                                """.formatted(UUID.randomUUID(), level.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.levelId").value(level.getId().toString()))
                .andExpect(jsonPath("$.content").value("Authenticated user note"));

        assertEquals(1L, noteRepository.countByUserIdAndLevelId(authenticatedUser.getId(), level.getId()));
    }

    @Test
    void shouldReturnCurrentUsersNoteForLevelWhenItExists() throws Exception {
        LoginResponse loginResponse = registerAndLogin("getnote-" + System.currentTimeMillis() + "@example.com");
        Level level = createLevelForAuthenticatedUser(loginResponse.accessToken(), "Fetch Note Level");

        mockMvc.perform(post("/api/notes")
                        .header("Authorization", "Bearer " + loginResponse.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "levelId": "%s",
                                  "content": "Fetch this saved note"
                                }
                                """.formatted(level.getId())))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/notes/levels/{levelId}", level.getId())
                        .header("Authorization", "Bearer " + loginResponse.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.noteId").exists())
                .andExpect(jsonPath("$.levelId").value(level.getId().toString()))
                .andExpect(jsonPath("$.content").value("Fetch this saved note"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists())
                .andExpect(jsonPath("$.userId").doesNotExist())
                .andExpect(jsonPath("$.token").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.role").doesNotExist())
                .andExpect(jsonPath("$.refreshToken").doesNotExist());
    }

    @Test
    void shouldReturn404WhenCurrentUserHasNoNoteForExistingLevel() throws Exception {
        LoginResponse firstLoginResponse = registerAndLogin("noteowner-" + System.currentTimeMillis() + "@example.com");
        LoginResponse secondLoginResponse = registerAndLogin("notenotowner-" + System.currentTimeMillis() + "@example.com");
        Level level = createLevelForAuthenticatedUser(firstLoginResponse.accessToken(), "No Note For Requester Level");

        mockMvc.perform(post("/api/notes")
                        .header("Authorization", "Bearer " + firstLoginResponse.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "levelId": "%s",
                                  "content": "Only the first user owns this note"
                                }
                                """.formatted(level.getId())))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/notes/levels/{levelId}", level.getId())
                        .header("Authorization", "Bearer " + secondLoginResponse.accessToken()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Note not found."))
                .andExpect(jsonPath("$.path").value("/api/notes/levels/" + level.getId()));
    }

    @Test
    void shouldReturn404WhenFetchingNoteForMissingLevel() throws Exception {
        LoginResponse loginResponse = registerAndLogin("missinggetnote-" + System.currentTimeMillis() + "@example.com");
        UUID missingLevelId = UUID.randomUUID();

        mockMvc.perform(get("/api/notes/levels/{levelId}", missingLevelId)
                        .header("Authorization", "Bearer " + loginResponse.accessToken()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Level not found."))
                .andExpect(jsonPath("$.path").value("/api/notes/levels/" + missingLevelId));
    }

    @Test
    void shouldReturn401WhenFetchingNoteWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/notes/levels/{levelId}", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    private Level createLevelForAuthenticatedUser(String accessToken, String topic) throws Exception {
        String generateResponseBody = mockMvc.perform(post("/api/courses/generate")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GenerateCourseRequest(topic, CourseDifficulty.BEGINNER, "Notes foundation"))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        GenerateCourseResponse generatedCourse = objectMapper.readValue(generateResponseBody, GenerateCourseResponse.class);
        return levelRepository.findByCourseIdOrderByOrderNumberAsc(generatedCourse.courseId()).get(0);
    }

    private LoginResponse registerAndLogin(String email) throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("Note Test", email, "NotePass123");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        LoginRequest loginRequest = new LoginRequest(email, "NotePass123");
        String loginResponseStr = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        return objectMapper.readValue(loginResponseStr, LoginResponse.class);
    }
}
