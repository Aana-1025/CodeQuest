CREATE TABLE quiz_attempts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    quiz_id UUID NOT NULL REFERENCES quizzes(id) ON DELETE CASCADE,
    selected_answer VARCHAR(1) NOT NULL,
    is_correct BOOLEAN NOT NULL,
    attempted_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT chk_quiz_attempts_selected_answer_valid CHECK (selected_answer IN ('A', 'B', 'C', 'D'))
);

CREATE INDEX idx_quiz_attempts_user_id
    ON quiz_attempts(user_id);

CREATE INDEX idx_quiz_attempts_quiz_id
    ON quiz_attempts(quiz_id);

CREATE INDEX idx_quiz_attempts_attempted_at
    ON quiz_attempts(attempted_at);
