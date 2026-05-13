CREATE TABLE flashcards (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    level_id UUID NOT NULL REFERENCES levels(id) ON DELETE CASCADE,
    order_number INTEGER NOT NULL,
    front TEXT NOT NULL,
    back TEXT NOT NULL,
    concept_tag VARCHAR(120),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT chk_flashcards_order_number_positive CHECK (order_number > 0)
);

CREATE UNIQUE INDEX ux_flashcards_level_id_order_number
    ON flashcards(level_id, order_number);

CREATE INDEX idx_flashcards_level_id
    ON flashcards(level_id);
