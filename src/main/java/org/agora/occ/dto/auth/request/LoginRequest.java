package org.agora.occ.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Payload for user authentication via NRP and password.
 */
@Data
public class LoginRequest {

    @NotBlank(message = "NRP is required")
    private String nrp;

    @NotBlank(message = "Password is required")
    private String password;
}
