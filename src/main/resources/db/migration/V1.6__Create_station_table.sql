CREATE TABLE station (
    id               UUID        NOT NULL PRIMARY KEY,
    station_name     TEXT        NOT NULL,
    station_code     TEXT,
    station_address  TEXT,
    station_latitude DOUBLE PRECISION NOT NULL,
    station_longitude DOUBLE PRECISION NOT NULL,
    station_status   TEXT        NOT NULL DEFAULT 'ACTIVE',
    heading          INTEGER,
    occ_id           UUID        NOT NULL REFERENCES occ(id),
    station_index    INTEGER,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ
);
CREATE INDEX idx_station_occ_id ON station(occ_id);
