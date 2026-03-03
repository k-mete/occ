package org.agora.occ.dto.websocket;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * WebSocket message payload for the reports channel.
 * Used for outgoing notifications (new reports, read status updates).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReportWebSocketMessage {

    private String action;
    private List<UUID> ids;
    private Boolean isRead;
    private Object data;
}
