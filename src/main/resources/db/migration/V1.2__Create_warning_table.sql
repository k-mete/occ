CREATE TABLE warning (
    alert_id UUID PRIMARY KEY,
    jpl_id UUID NOT NULL,
    camera_id VARCHAR(100),
    jpl_code VARCHAR(100),
    jpl_name VARCHAR(255),
    train_id UUID NOT NULL,
    train_name VARCHAR(255),
    train_code VARCHAR(100),
    crowd_level VARCHAR(50),
    warning_level VARCHAR(50),
    distance_km DOUBLE PRECISION,
    speed_kmh DOUBLE PRECISION,
    object_detected INTEGER,
    alert_timestamp TIMESTAMPTZ NOT NULL,
    action_required TEXT,
    color_indicator VARCHAR(50),
    is_health BOOLEAN,
    is_siren_on BOOLEAN,
    is_gate_open BOOLEAN,
    is_any_obstacle BOOLEAN,
    is_installed BOOLEAN,
    camera_stream TEXT[]
);

CREATE INDEX idx_warning_jpl_id ON warning(jpl_id);
CREATE INDEX idx_warning_train_id ON warning(train_id);
CREATE INDEX idx_warning_warning_level ON warning(warning_level);
CREATE INDEX idx_warning_alert_timestamp ON warning(alert_timestamp);
