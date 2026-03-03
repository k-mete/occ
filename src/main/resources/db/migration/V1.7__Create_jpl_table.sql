CREATE TABLE jpl (
    id                       UUID             NOT NULL PRIMARY KEY,
    jpl_name                 TEXT             NOT NULL,
    jpl_address              TEXT,
    jpl_status               TEXT             NOT NULL DEFAULT 'ACTIVE',
    jpl_computer_vision_status TEXT,
    jpl_network              TEXT,
    station_id               UUID             NOT NULL REFERENCES station(id),
    jpl_latitude             DOUBLE PRECISION,
    jpl_longitude            DOUBLE PRECISION,
    heading                  INTEGER          NOT NULL DEFAULT 0,
    is_health                BOOLEAN          NOT NULL DEFAULT TRUE,
    is_siren_on              BOOLEAN          NOT NULL DEFAULT FALSE,
    is_gate_open             BOOLEAN          NOT NULL DEFAULT FALSE,
    is_any_obstacle          BOOLEAN          NOT NULL DEFAULT TRUE,
    is_installed             BOOLEAN          NOT NULL DEFAULT TRUE,
    camera_stream            TEXT[],
    created_at               TIMESTAMPTZ      NOT NULL DEFAULT NOW(),
    updated_at               TIMESTAMPTZ
);
CREATE INDEX idx_jpl_station_id ON jpl(station_id);
