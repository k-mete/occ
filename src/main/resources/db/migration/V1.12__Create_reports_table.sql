-- V1.12__Create_reports_table.sql
CREATE TABLE IF NOT EXISTS reports
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type        VARCHAR(50),
    title       VARCHAR(255),
    description TEXT,
    file_paths  TEXT,
    latitude    DOUBLE PRECISION,
    longitude   DOUBLE PRECISION,
    train_id    UUID,
    jpl_id      UUID,
    is_read     BOOLEAN          DEFAULT FALSE,
    created_at  TIMESTAMPTZ,
    updated_at  TIMESTAMPTZ
);
