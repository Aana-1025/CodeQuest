CREATE TABLE code_submissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    problem_id UUID NOT NULL,
    language VARCHAR(20) NOT NULL,
    code TEXT NOT NULL,
    passed BOOLEAN NOT NULL,
    passed_test_cases INTEGER NOT NULL,
    total_test_cases INTEGER NOT NULL,
    runtime_ms INTEGER,
    memory_kb INTEGER,
    ai_review TEXT,
    submitted_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX idx_code_submissions_user_id
    ON code_submissions(user_id);

CREATE INDEX idx_code_submissions_user_id_problem_id
    ON code_submissions(user_id, problem_id);

CREATE INDEX idx_code_submissions_problem_id
    ON code_submissions(problem_id);

CREATE INDEX idx_code_submissions_submitted_at
    ON code_submissions(submitted_at);
