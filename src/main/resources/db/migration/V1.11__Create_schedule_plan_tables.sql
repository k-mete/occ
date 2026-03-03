CREATE TABLE station_schedule_plan (
    plan_id         UUID        NOT NULL PRIMARY KEY,
    train_id        UUID        NOT NULL REFERENCES train(id) ON DELETE CASCADE,
    station_id      UUID        NOT NULL REFERENCES station(id) ON DELETE CASCADE,
    arrival_plan    TIMESTAMPTZ,
    departure_plan  TIMESTAMPTZ NOT NULL,
    description     TEXT,
    direction       TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ
);

CREATE TABLE jpl_schedule_plan (
    plan_id             UUID        NOT NULL PRIMARY KEY,
    train_id            UUID        NOT NULL REFERENCES train(id) ON DELETE CASCADE,
    jpl_id              UUID        NOT NULL REFERENCES jpl(id) ON DELETE CASCADE,
    estimated_pass_time TIMESTAMPTZ NOT NULL,
    direction           TEXT        NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ
);
