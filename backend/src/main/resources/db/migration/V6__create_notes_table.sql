CREATE TABLE notes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    level_id UUID NOT NULL REFERENCES levels(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT chk_notes_content_length CHECK (length(content) <= 5000)
);

CREATE UNIQUE INDEX ux_notes_user_id_level_id
    ON notes(user_id, level_id);

CREATE INDEX idx_notes_user_id
    ON notes(user_id);

CREATE INDEX idx_notes_level_id
    ON notes(level_id);
