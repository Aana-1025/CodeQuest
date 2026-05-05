CREATE TABLE courses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    normalized_topic VARCHAR(120) NOT NULL,
    title VARCHAR(160) NOT NULL,
    description TEXT NOT NULL,
    created_by UUID NOT NULL REFERENCES users(id),
    difficulty VARCHAR(30) NOT NULL,
    is_public BOOLEAN NOT NULL DEFAULT FALSE,
    total_xp INTEGER NOT NULL DEFAULT 0,
    source_type VARCHAR(30) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT chk_courses_total_xp_non_negative CHECK (total_xp >= 0)
);

CREATE TABLE levels (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    course_id UUID NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    title VARCHAR(160) NOT NULL,
    content_markdown TEXT NOT NULL,
    order_number INTEGER NOT NULL,
    is_boss BOOLEAN NOT NULL DEFAULT FALSE,
    xp_reward INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT chk_levels_xp_reward_non_negative CHECK (xp_reward >= 0),
    CONSTRAINT chk_levels_order_number_positive CHECK (order_number > 0)
);

CREATE UNIQUE INDEX ux_courses_normalized_topic_difficulty
    ON courses(normalized_topic, difficulty);

CREATE INDEX idx_courses_normalized_topic
    ON courses(normalized_topic);

CREATE INDEX idx_courses_created_by
    ON courses(created_by);

CREATE UNIQUE INDEX ux_levels_course_id_order_number
    ON levels(course_id, order_number);
