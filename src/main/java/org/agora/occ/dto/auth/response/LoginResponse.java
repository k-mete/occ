package org.agora.occ.dto.auth.response;

import lombok.Builder;
import lombok.Data;
import org.agora.occ.enums.UserRole;

import java.time.Instant;
import java.util.UUID;

/**
 * Payload returned upon successful login, containing tokens and user data.
 */
@Data
@Builder
public class LoginResponse {

    private TokenResponse tokens;
    private UserData user;
    private CheckinData checkin;

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
    public static class CheckinData {
        private boolean isCheckedIn;
        private Instant checkInTime;
        private UUID attendanceId;
    }
}
