package org.agora.occ.dto.auth.response;

import lombok.Builder;
import lombok.Data;

/**
 * Payload returned upon successful token refresh.
 * Unlike LoginResponse, it only contains the new access token.
 */
@Data
@Builder
public class TokenRefreshResponse {

    private TokenResponse tokens;
}
