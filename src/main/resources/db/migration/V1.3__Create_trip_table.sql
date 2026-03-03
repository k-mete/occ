CREATE TABLE trip
(
    id         UUID        NOT NULL PRIMARY KEY,
    train_id   UUID        NOT NULL,
    route_id   UUID        NOT NULL,
    is_flow    BOOLEAN     NOT NULL DEFAULT TRUE,
    start_time TIMESTAMPTZ NOT NULL,
    end_time   TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_trip_train_id ON trip (train_id);
CREATE INDEX idx_trip_route_id ON trip (route_id);
CREATE INDEX idx_trip_start_time ON trip (start_time);
