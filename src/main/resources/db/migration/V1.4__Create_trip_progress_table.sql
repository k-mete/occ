CREATE TABLE trip_progress
(
    id         UUID        NOT NULL PRIMARY KEY,
    trip_id    UUID        NOT NULL REFERENCES trip (id) ON DELETE CASCADE,
    jpl_id     UUID,
    station_id UUID,
    timestamp  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_trip_progress_trip_id ON trip_progress (trip_id);
CREATE INDEX idx_trip_progress_timestamp ON trip_progress (timestamp);
