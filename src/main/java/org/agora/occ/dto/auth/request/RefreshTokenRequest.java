package org.agora.occ.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Payload containing the refresh token used for renewing access or logging out.
 */
@Data
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}
