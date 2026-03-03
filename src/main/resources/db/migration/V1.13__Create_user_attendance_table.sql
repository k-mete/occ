CREATE TABLE user_attendance (
    id          UUID        PRIMARY KEY,
    user_id     UUID        NOT NULL REFERENCES "user"("userId"),
    check_in    TIMESTAMP WITH TIME ZONE NOT NULL,
    check_out   TIMESTAMP WITH TIME ZONE,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL
);
