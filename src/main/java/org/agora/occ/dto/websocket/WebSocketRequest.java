package org.agora.occ.dto.websocket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Common incoming WebSocket message used for subscribe/unsubscribe actions
 * across all WebSocket channels.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebSocketRequest {

    private String action;
    private String type;
    private UUID trainId;
    private UUID jplId;
    private UUID occId;
    private UUID stationId;
}
