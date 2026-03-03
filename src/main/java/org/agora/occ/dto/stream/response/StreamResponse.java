package org.agora.occ.dto.stream.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for stream control operations.
 *
 * <p>
 * The {@code type} field indicates the nature of the message:
 * <ul>
 * <li>{@code stream_status} — carries state, cameraCount, and playbackUrls</li>
 * <li>{@code ack} — carries a status field on success</li>
 * <li>{@code error} — carries an error message</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@RegisterForReflection
public class StreamResponse {

    @JsonProperty("type")
    private String type;

    @JsonProperty("messageId")
    private UUID messageId;

    @JsonProperty("jplId")
    private UUID jplId;

    @JsonProperty("ts")
    private Instant ts;

    // --- stream_status fields ---

    @JsonProperty("state")
    private String state;

    @JsonProperty("cameraCount")
    private Integer cameraCount;

    @JsonProperty("playbackUrls")
    private List<String> playbackUrls;

    // --- ack fields ---

    @JsonProperty("status")
    private String status;

    // --- error fields ---

    @JsonProperty("error")
    private String error;
}
