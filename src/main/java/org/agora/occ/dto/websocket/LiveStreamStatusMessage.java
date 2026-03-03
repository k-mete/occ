package org.agora.occ.dto.websocket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Message DTO for live stream state changes pushed from JPL devices.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LiveStreamStatusMessage {

    private String type;
    private String state;
    private String status;
}
