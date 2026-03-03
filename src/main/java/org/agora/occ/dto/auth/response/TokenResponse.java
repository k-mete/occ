package org.agora.occ.dto.auth.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * Inner DTO representing token details like access token, refresh token, and
 * expiration.
 */
@Data
@Builder
public class TokenResponse {

    private String accessToken;

    // Only present on initial login, not during refresh
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String refreshToken;

    private String tokenType;

    private Instant expiresAt;
}
