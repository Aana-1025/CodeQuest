CREATE TABLE coding_problems (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    level_id UUID NOT NULL REFERENCES levels(id) ON DELETE CASCADE,
    title VARCHAR(160) NOT NULL,
    description TEXT NOT NULL,
    starter_code_json JSONB NOT NULL,
    test_cases_json JSONB NOT NULL,
    hidden_tests_json JSONB NOT NULL,
    difficulty VARCHAR(20) NOT NULL,
    xp_reward INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX idx_coding_problems_level_id
    ON coding_problems(level_id);

CREATE INDEX idx_coding_problems_difficulty
    ON coding_problems(difficulty);
