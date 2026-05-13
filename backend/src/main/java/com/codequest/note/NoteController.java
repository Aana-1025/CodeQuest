package com.codequest.note;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codequest.common.security.CurrentUserPrincipal;
import com.codequest.note.dto.NoteResponse;
import com.codequest.note.dto.SaveNoteRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

import java.util.UUID;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping
    @Operation(summary = "Save or update lesson note", description = "Save or update the authenticated user's note for a level")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Note saved successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
            @ApiResponse(responseCode = "404", description = "Level not found")
    })
    public ResponseEntity<NoteResponse> saveNote(
            @AuthenticationPrincipal CurrentUserPrincipal currentUser,
            @Valid @RequestBody SaveNoteRequest request
    ) {
        NoteResponse response = noteService.saveNote(currentUser.userId(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/levels/{levelId}")
    @Operation(summary = "Get lesson note", description = "Get the authenticated user's note for a level")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Note returned successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
            @ApiResponse(responseCode = "404", description = "Level or note not found")
    })
    public ResponseEntity<NoteResponse> getNoteForLevel(
            @AuthenticationPrincipal CurrentUserPrincipal currentUser,
            @PathVariable("levelId") UUID levelId
    ) {
        NoteResponse response = noteService.getNoteForCurrentUser(currentUser.userId(), levelId);
        return ResponseEntity.ok(response);
    }
}
