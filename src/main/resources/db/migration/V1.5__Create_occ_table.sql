CREATE TABLE occ (
    id            UUID        NOT NULL PRIMARY KEY,
    occ_name      TEXT        NOT NULL,
    occ_latitude  DOUBLE PRECISION NOT NULL,
    occ_longitude DOUBLE PRECISION NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ
);
