CREATE TABLE train (
    id                          UUID        NOT NULL PRIMARY KEY,
    train_name                  TEXT        NOT NULL,
    train_code                  TEXT,
    train_network_ip            TEXT,
    train_status                TEXT        NOT NULL DEFAULT 'ACTIVE',
    train_online                BOOLEAN     NOT NULL DEFAULT FALSE,
    category                    TEXT        NOT NULL DEFAULT 'LOCAL',
    train_last_known_latitude   DOUBLE PRECISION,
    train_last_known_longitude  DOUBLE PRECISION,
    route_id                    UUID        REFERENCES route(id),
    created_at                  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at                  TIMESTAMPTZ
);
