CREATE TABLE quizzes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    level_id UUID NOT NULL REFERENCES levels(id) ON DELETE CASCADE,
    order_number INTEGER NOT NULL,
    question TEXT NOT NULL,
    option_a TEXT NOT NULL,
    option_b TEXT NOT NULL,
    option_c TEXT NOT NULL,
    option_d TEXT NOT NULL,
    correct_answer VARCHAR(1) NOT NULL,
    explanation TEXT,
    concept_tag VARCHAR(120),
    xp_reward INTEGER NOT NULL DEFAULT 20,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT chk_quizzes_correct_answer_valid CHECK (correct_answer IN ('A', 'B', 'C', 'D')),
    CONSTRAINT chk_quizzes_xp_reward_non_negative CHECK (xp_reward >= 0),
    CONSTRAINT chk_quizzes_order_number_positive CHECK (order_number > 0)
);

CREATE UNIQUE INDEX ux_quizzes_level_id_order_number
    ON quizzes(level_id, order_number);

CREATE INDEX idx_quizzes_level_id
    ON quizzes(level_id);
