-- Create user table containing authentication and profile info
CREATE TABLE "user" (
    "userId" UUID PRIMARY KEY,
    "nrp" VARCHAR(50) NOT NULL UNIQUE,
    "fullName" VARCHAR(255) NOT NULL,
    "password" VARCHAR(255) NOT NULL,
    "role" VARCHAR(50) NOT NULL,
    "createdAt" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP WITH TIME ZONE,
    "createdBy" UUID,
    "updatedBy" UUID
);

CREATE INDEX idx_user_nrp ON "user"("nrp");
CREATE INDEX idx_user_role ON "user"("role");

COMMENT ON TABLE "user" IS 'Stores user account information for authentication';
COMMENT ON COLUMN "user"."nrp" IS 'Nomor Registrasi Pegawai (Unique identifier for login)';
COMMENT ON COLUMN "user"."role" IS 'User role: PETUGAS_OCC, PETUGAS_JPL, MASINIS, ADMINISTRASI';
COMMENT ON COLUMN "user"."password" IS 'Bcrypt hashed password';

-- Create refresh tokens table
CREATE TABLE "refresh_token" (
    "tokenId" UUID PRIMARY KEY,
    "userId" UUID NOT NULL REFERENCES "user"("userId") ON DELETE CASCADE,
    "tokenStr" VARCHAR(255) NOT NULL UNIQUE,
    "expiresAt" TIMESTAMP WITH TIME ZONE NOT NULL,
    "revoked" BOOLEAN NOT NULL DEFAULT FALSE,
    "createdAt" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_refresh_token_user ON "refresh_token"("userId");
CREATE INDEX idx_refresh_token_str ON "refresh_token"("tokenStr");

COMMENT ON TABLE "refresh_token" IS 'Stores long-lived refresh tokens for acquiring new JWTs';

-- Insert Default Admin User
-- NRP: 123456
-- Password: admin123 (bcrypt hashed)
-- Role: ADMINISTRASI
INSERT INTO "user" ("userId", "nrp", "fullName", "password", "role")
VALUES (
    '550e8400-e29b-41d4-a716-446655440000',
    '123456',
    'Admin OCC',
    '$2a$10$wT8m9o3/XvQzj5YvD5176ea3Zk.g9XN15TInwXG3HInR5DXYKOhH2',
    'ADMINISTRASI'
);
