package org.agora.occ.entity.event;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * CDI event fired when a new report is persisted.
 * Observed by {@link org.agora.occ.controller.websocket.ReportWebSocket}
 * to push notifications to all connected clients.
 *
 * @param id          the unique identifier of the created report
 * @param type        the report category type
 * @param title       the report title
 * @param description the report description
 * @param filePaths   the MinIO object URLs of the uploaded images
 * @param latitude    the GPS latitude of the report location
 * @param longitude   the GPS longitude of the report location
 * @param trainId     the train associated with the report
 * @param jplId       the JPL station associated with the report
 * @param createdAt   the creation timestamp
 * @param updatedAt   the last-updated timestamp
 * @param isRead      initial read status (always false on creation)
 */
public record ReportCreatedEvent(
                UUID id,
                String type,
                String title,
                String description,
                List<String> filePaths,
                Double latitude,
                Double longitude,
                UUID trainId,
                UUID jplId,
                Instant createdAt,
                Instant updatedAt,
                Boolean isRead) {
}
