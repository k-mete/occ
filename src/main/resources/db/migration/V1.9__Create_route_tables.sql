CREATE TABLE route (
    id                  UUID        NOT NULL PRIMARY KEY,
    route_code          TEXT        NOT NULL UNIQUE,
    route_distance      DOUBLE PRECISION,
    category            TEXT        NOT NULL DEFAULT 'LOCAL',
    is_active           BOOLEAN     NOT NULL DEFAULT TRUE,
    from_station_name   TEXT,
    to_station_name     TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ
);

CREATE TABLE route_segment (
    id                  UUID        NOT NULL PRIMARY KEY,
    route_segment_code  TEXT        NOT NULL,
    from_station_id     UUID        REFERENCES station(id),
    to_station_id       UUID        REFERENCES station(id),
    route_duration      INTEGER,
    route_distance      DOUBLE PRECISION,
    route_status        TEXT        NOT NULL DEFAULT 'ACTIVE',
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ
);

CREATE TABLE route_segment_order (
    route_id          UUID    NOT NULL REFERENCES route(id) ON DELETE CASCADE,
    route_segment_id  UUID    NOT NULL REFERENCES route_segment(id) ON DELETE CASCADE,
    segment_index     INTEGER NOT NULL,
    PRIMARY KEY (route_id, route_segment_id)
);

CREATE TABLE route_segment_jpl (
    route_segment_id  UUID NOT NULL REFERENCES route_segment(id) ON DELETE CASCADE,
    jpl_id            UUID NOT NULL REFERENCES jpl(id) ON DELETE CASCADE,
    PRIMARY KEY (route_segment_id, jpl_id)
);
