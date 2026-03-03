package org.agora.occ.dto.stream.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for starting or stopping a JPL live stream.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StreamRequest {

    /** ID of the JPL whose camera stream should be started or stopped. */
    @NotNull(message = "JPL ID is required")
    @JsonProperty("jplId")
    private UUID jplId;

    /**
     * Requested video quality. Defaults to {@code "low"}.
     * Accepted values depend on the JPL device (e.g. "low", "medium", "high").
     */
    @JsonProperty("quality")
    private String quality = "low";

    /**
     * Token time-to-live in seconds. Defaults to 300 (5 minutes).
     */
    @JsonProperty("ttlSeconds")
    private Integer ttlSeconds = 300;
}
