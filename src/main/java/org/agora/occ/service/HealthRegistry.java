package org.agora.occ.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.agora.occ.enums.ConnectionStatus;
import org.agora.occ.enums.HealthCategory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central in-memory registry tracking the real-time WebSocket connection count
 * of tracked entities (JPL, Train, Station).
 *
 * <p>
 * Evaluates entities as {@link ConnectionStatus#ONLINE} if their connection
 * count is > 0, and {@link ConnectionStatus#OFFLINE} if exactly 0.
 */
@ApplicationScoped
public class HealthRegistry {

    private final Map<UUID, Integer> jplConnections = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> trainConnections = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> stationConnections = new ConcurrentHashMap<>();

    /**
     * Records or updates the current connection count for an entity.
     *
     * @param category the type of entity
     * @param id       the UUID of the entity
     * @param count    the total number of active WebSocket connections
     */
    public void recordConnection(HealthCategory category, UUID id, int count) {
        if (id == null)
            return;
        getMapForCategory(category).put(id, Math.max(0, count));
    }

    /**
     * Resolves the current health status of a specific tracking ID.
     *
     * @param category the type of entity
     * @param id       the UUID of the entity
     * @return {@link ConnectionStatus#ONLINE} if count > 0, else OFFLINE
     */
    public ConnectionStatus getStatus(HealthCategory category, UUID id) {
        Integer count = getMapForCategory(category).get(id);
        return (count != null && count > 0) ? ConnectionStatus.ONLINE : ConnectionStatus.OFFLINE;
    }

    /**
     * Returns the exact connection count for an entity.
     *
     * @param category the type of entity
     * @param id       the UUID of the entity
     * @return the number of active connections (0 if never connected)
     */
    public int getConnectionCount(HealthCategory category, UUID id) {
        return getMapForCategory(category).getOrDefault(id, 0);
    }

    /**
     * Returns a snapshot map of all tracked entities for a given category and their
     * current status.
     *
     * @param category the type of entity
     * @return a map of UUID to ConnectionStatus
     */
    public Map<UUID, ConnectionStatus> getAllByCategory(HealthCategory category) {
        Map<UUID, Integer> sourceMap = getMapForCategory(category);
        Map<UUID, ConnectionStatus> result = new HashMap<>(sourceMap.size());
        for (Map.Entry<UUID, Integer> entry : sourceMap.entrySet()) {
            result.put(entry.getKey(), entry.getValue() > 0 ? ConnectionStatus.ONLINE : ConnectionStatus.OFFLINE);
        }
        return result;
    }

    private Map<UUID, Integer> getMapForCategory(HealthCategory category) {
        return switch (category) {
            case JPL -> jplConnections;
            case TRAIN -> trainConnections;
            case STATION -> stationConnections;
        };
    }
}
