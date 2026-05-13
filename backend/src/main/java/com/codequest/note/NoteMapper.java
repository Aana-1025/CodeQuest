package com.codequest.note;

import org.springframework.stereotype.Component;

import com.codequest.note.dto.NoteResponse;

@Component
public class NoteMapper {

    public NoteResponse toResponse(Note note) {
        return new NoteResponse(
                note.getId(),
                note.getLevel().getId(),
                note.getContent(),
                note.getCreatedAt(),
                note.getUpdatedAt()
        );
    }
}
