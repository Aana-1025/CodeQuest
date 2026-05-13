package com.codequest.note;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codequest.common.exception.ApiException;
import com.codequest.common.exception.ErrorCode;
import com.codequest.level.Level;
import com.codequest.level.LevelRepository;
import com.codequest.note.dto.NoteResponse;
import com.codequest.note.dto.SaveNoteRequest;
import com.codequest.user.User;
import com.codequest.user.UserRepository;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final LevelRepository levelRepository;
    private final UserRepository userRepository;
    private final NoteMapper noteMapper;

    public NoteService(NoteRepository noteRepository, LevelRepository levelRepository,
                       UserRepository userRepository, NoteMapper noteMapper) {
        this.noteRepository = noteRepository;
        this.levelRepository = levelRepository;
        this.userRepository = userRepository;
        this.noteMapper = noteMapper;
    }

    @Transactional
    public NoteResponse saveNote(UUID userId, SaveNoteRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "User not found."));
        Level level = levelRepository.findById(request.levelId())
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "Level not found."));

        String content = request.content();
        Instant now = Instant.now();

        Note note = noteRepository.findByUserIdAndLevelId(userId, request.levelId())
                .map(existingNote -> updateExistingNote(existingNote, content, now))
                .orElseGet(() -> createNewNote(user, level, content, now));

        Note savedNote = noteRepository.save(note);
        return noteMapper.toResponse(savedNote);
    }

    private Note createNewNote(User user, Level level, String content, Instant now) {
        return new Note(
                UUID.randomUUID(),
                user,
                level,
                content,
                now,
                now
        );
    }

    private Note updateExistingNote(Note note, String content, Instant now) {
        note.setContent(content);
        note.setUpdatedAt(now);
        return note;
    }
}
