package org.agora.occ.dto.attendance.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Data Transfer Object representing a user's attendance log
 * (check-in/check-out).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponse {

    private UUID id;

    private UUID userId;

    private String fullName;

    private String role;

    private Instant checkIn;

    private Instant checkOut;

    private Instant createdAt;
}
