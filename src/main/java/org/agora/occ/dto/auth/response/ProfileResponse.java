package org.agora.occ.dto.auth.response;

import lombok.Builder;
import lombok.Data;
import org.agora.occ.enums.UserRole;

import java.time.Instant;
import java.util.UUID;

/**
 * Payload returned by the profile endpoint, containing user details and session
 * data.
 */
@Data
@Builder
public class ProfileResponse {

    private UserData user;
    private SessionData session;

    @Data
    @Builder
    public static class UserData {
        private UUID userId;
        private String nrp;
        private String fullName;
        private UserRole role;
    }

    @Data
    @Builder
    public static class SessionData {
        private Instant issuedAt;
        private Instant expiresAt;
        private boolean active;
    }
}
