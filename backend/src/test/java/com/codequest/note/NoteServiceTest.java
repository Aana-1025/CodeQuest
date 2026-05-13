package com.codequest.note;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
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
import com.codequest.level.Level;
import com.codequest.level.LevelRepository;
import com.codequest.note.dto.NoteResponse;
import com.codequest.note.dto.SaveNoteRequest;
import com.codequest.user.User;
import com.codequest.user.UserRank;
import com.codequest.user.UserRepository;
import com.codequest.user.UserRole;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private LevelRepository levelRepository;

    @Mock
    private UserRepository userRepository;

    private NoteService noteService;

    @BeforeEach
    void setUp() {
        noteService = new NoteService(noteRepository, levelRepository, userRepository, new NoteMapper());
    }

    @Test
    void saveNote_shouldCreateNewNoteForAuthenticatedUser() {
        User user = createUser("note-owner@example.com");
        Level level = createLevel();
        SaveNoteRequest request = new SaveNoteRequest(level.getId(), "My first note for this level");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(levelRepository.findById(level.getId())).thenReturn(Optional.of(level));
        when(noteRepository.findByUserIdAndLevelId(user.getId(), level.getId())).thenReturn(Optional.empty());

        ArgumentCaptor<Note> noteCaptor = ArgumentCaptor.forClass(Note.class);
        when(noteRepository.save(noteCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        NoteResponse response = noteService.saveNote(user.getId(), request);

        assertEquals(level.getId(), response.levelId());
        assertEquals("My first note for this level", response.content());
        assertEquals(noteCaptor.getValue().getId(), response.noteId());
        assertNotNull(response.createdAt());
        assertNotNull(response.updatedAt());
        verify(noteRepository).save(any(Note.class));
    }

    @Test
    void saveNote_shouldUpdateExistingNoteForSameUserAndLevel() {
        User user = createUser("update-owner@example.com");
        Level level = createLevel();
        Instant createdAt = Instant.now().minusSeconds(60);
        Instant firstUpdatedAt = createdAt.plusSeconds(10);
        Note existingNote = new Note(
                UUID.randomUUID(),
                user,
                level,
                "Old content",
                createdAt,
                firstUpdatedAt
        );

        SaveNoteRequest request = new SaveNoteRequest(level.getId(), "Updated note content");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(levelRepository.findById(level.getId())).thenReturn(Optional.of(level));
        when(noteRepository.findByUserIdAndLevelId(user.getId(), level.getId())).thenReturn(Optional.of(existingNote));
        when(noteRepository.save(existingNote)).thenAnswer(invocation -> invocation.getArgument(0));

        NoteResponse response = noteService.saveNote(user.getId(), request);

        assertEquals(existingNote.getId(), response.noteId());
        assertEquals("Updated note content", response.content());
        assertEquals(createdAt, response.createdAt());
        assertNotEquals(firstUpdatedAt, response.updatedAt());
    }

    @Test
    void saveNote_shouldThrowNotFoundWhenLevelMissing() {
        User user = createUser("missing-level@example.com");
        UUID missingLevelId = UUID.randomUUID();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(levelRepository.findById(missingLevelId)).thenReturn(Optional.empty());

        ApiException exception = assertThrows(
                ApiException.class,
                () -> noteService.saveNote(user.getId(), new SaveNoteRequest(missingLevelId, "Content"))
        );

        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
        assertEquals("Level not found.", exception.getMessage());
    }

    @Test
    void saveNote_shouldKeepSeparateNotesForDifferentUsersOnSameLevel() {
        User firstUser = createUser("first-note-user@example.com");
        User secondUser = createUser("second-note-user@example.com");
        Level level = createLevel();

        when(userRepository.findById(firstUser.getId())).thenReturn(Optional.of(firstUser));
        when(userRepository.findById(secondUser.getId())).thenReturn(Optional.of(secondUser));
        when(levelRepository.findById(level.getId())).thenReturn(Optional.of(level));
        when(noteRepository.findByUserIdAndLevelId(firstUser.getId(), level.getId())).thenReturn(Optional.empty());
        when(noteRepository.findByUserIdAndLevelId(secondUser.getId(), level.getId())).thenReturn(Optional.empty());
        when(noteRepository.save(any(Note.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NoteResponse firstResponse = noteService.saveNote(firstUser.getId(), new SaveNoteRequest(level.getId(), "First user note"));
        NoteResponse secondResponse = noteService.saveNote(secondUser.getId(), new SaveNoteRequest(level.getId(), "Second user note"));

        assertNotEquals(firstResponse.noteId(), secondResponse.noteId());
        assertEquals("First user note", firstResponse.content());
        assertEquals("Second user note", secondResponse.content());
        verify(noteRepository, times(2)).save(any(Note.class));
    }

    @Test
    void getNoteForCurrentUser_shouldReturnCurrentUsersNoteWhenItExists() {
        User user = createUser("fetch-note@example.com");
        Level level = createLevel();
        Instant createdAt = Instant.now().minusSeconds(60);
        Instant updatedAt = Instant.now();
        Note note = new Note(
                UUID.randomUUID(),
                user,
                level,
                "Saved note content",
                createdAt,
                updatedAt
        );

        when(levelRepository.findById(level.getId())).thenReturn(Optional.of(level));
        when(noteRepository.findByUserIdAndLevelId(user.getId(), level.getId())).thenReturn(Optional.of(note));

        NoteResponse response = noteService.getNoteForCurrentUser(user.getId(), level.getId());

        assertEquals(note.getId(), response.noteId());
        assertEquals(level.getId(), response.levelId());
        assertEquals("Saved note content", response.content());
        assertEquals(createdAt, response.createdAt());
        assertEquals(updatedAt, response.updatedAt());
    }

    @Test
    void getNoteForCurrentUser_shouldThrowNotFoundWhenLevelDoesNotExist() {
        User user = createUser("missing-note-level@example.com");
        UUID missingLevelId = UUID.randomUUID();

        when(levelRepository.findById(missingLevelId)).thenReturn(Optional.empty());

        ApiException exception = assertThrows(
                ApiException.class,
                () -> noteService.getNoteForCurrentUser(user.getId(), missingLevelId)
        );

        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
        assertEquals("Level not found.", exception.getMessage());
    }

    @Test
    void getNoteForCurrentUser_shouldThrowNotFoundWhenNoteDoesNotExistForCurrentUser() {
        User user = createUser("note-missing@example.com");
        Level level = createLevel();

        when(levelRepository.findById(level.getId())).thenReturn(Optional.of(level));
        when(noteRepository.findByUserIdAndLevelId(user.getId(), level.getId())).thenReturn(Optional.empty());

        ApiException exception = assertThrows(
                ApiException.class,
                () -> noteService.getNoteForCurrentUser(user.getId(), level.getId())
        );

        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
        assertEquals("Note not found.", exception.getMessage());
    }

    @Test
    void getNoteForCurrentUser_shouldNotReturnAnotherUsersNoteForSameLevel() {
        User firstUser = createUser("note-owner-fetch@example.com");
        User secondUser = createUser("note-requester-fetch@example.com");
        Level level = createLevel();

        when(levelRepository.findById(level.getId())).thenReturn(Optional.of(level));
        when(noteRepository.findByUserIdAndLevelId(secondUser.getId(), level.getId())).thenReturn(Optional.empty());

        ApiException exception = assertThrows(
                ApiException.class,
                () -> noteService.getNoteForCurrentUser(secondUser.getId(), level.getId())
        );

        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
        assertEquals("Note not found.", exception.getMessage());
        verify(noteRepository).findByUserIdAndLevelId(secondUser.getId(), level.getId());
    }

    private User createUser(String email) {
        Instant now = Instant.now();
        User user = new User(UUID.randomUUID(), "Note User", email, "hashed-password");
        user.setRank(UserRank.BEGINNER);
        user.setRole(UserRole.STUDENT);
        user.setXp(0);
        user.setStreak(0);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        return user;
    }

    private Level createLevel() {
        Instant now = Instant.now();
        Level level = new Level(
                UUID.randomUUID(),
                null,
                "Binary Search Basics",
                "# Binary Search Basics",
                1,
                false,
                50,
                now,
                now
        );
        return level;
    }
}
