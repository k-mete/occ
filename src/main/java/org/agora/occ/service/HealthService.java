package org.agora.occ.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.agora.occ.dto.health.HealthDetailResponse;
import org.agora.occ.dto.health.HealthItemResponse;
import org.agora.occ.dto.health.HealthSummaryResponse;
import org.agora.occ.enums.ConnectionStatus;
import org.agora.occ.enums.HealthCategory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service orchestrating health queries against the central
 * {@link HealthRegistry}.
 */
@ApplicationScoped
public class HealthService {

    private final HealthRegistry healthRegistry;

    @Inject
    public HealthService(HealthRegistry healthRegistry) {
        this.healthRegistry = healthRegistry;
    }

    /**
     * Retrieves detailed health state for a single entity.
     *
     * @param category the entity category
     * @param id       the entity's UUID
     * @return the detail response DTO
     */
    public HealthDetailResponse getEntityHealth(HealthCategory category, UUID id) {
        ConnectionStatus status = healthRegistry.getStatus(category, id);
        int connections = healthRegistry.getConnectionCount(category, id);

        return HealthDetailResponse.builder()
                .id(id)
                .category(category)
                .status(status)
                .activeConnections(connections)
                .checkedAt(Instant.now())
                .build();
    }

    /**
     * Retrieves a list of all tracked entities for a category, optionally filtered
     * by status.
     *
     * @param category the entity category
     * @param status   optional filter (if null, both ONLINE and OFFLINE are
     *                 returned)
     * @return list of lightweight health items
     */
    public List<HealthItemResponse> getListByStatus(HealthCategory category, ConnectionStatus status) {
        Map<UUID, ConnectionStatus> all = healthRegistry.getAllByCategory(category);
        List<HealthItemResponse> result = new ArrayList<>(all.size());

        for (Map.Entry<UUID, ConnectionStatus> entry : all.entrySet()) {
            if (status == null || entry.getValue() == status) {
                result.add(new HealthItemResponse(entry.getKey(), entry.getValue()));
            }
        }
        return result;
    }

    /**
     * Returns an aggregate count of ONLINE vs OFFLINE across all entity categories.
     *
     * @return the summary DTO
     */
    public HealthSummaryResponse getOverallSummary() {
        int jplOn = 0, jplOff = 0;
        for (ConnectionStatus s : healthRegistry.getAllByCategory(HealthCategory.JPL).values()) {
            if (s == ConnectionStatus.ONLINE)
                jplOn++;
            else
                jplOff++;
        }

        int trainOn = 0, trainOff = 0;
        for (ConnectionStatus s : healthRegistry.getAllByCategory(HealthCategory.TRAIN).values()) {
            if (s == ConnectionStatus.ONLINE)
                trainOn++;
            else
                trainOff++;
        }

        int stationOn = 0, stationOff = 0;
        for (ConnectionStatus s : healthRegistry.getAllByCategory(HealthCategory.STATION).values()) {
            if (s == ConnectionStatus.ONLINE)
                stationOn++;
            else
                stationOff++;
        }

        return HealthSummaryResponse.builder()
                .jplOnline(jplOn)
                .jplOffline(jplOff)
                .trainOnline(trainOn)
                .trainOffline(trainOff)
                .stationOnline(stationOn)
                .stationOffline(stationOff)
                .totalOnline(jplOn + trainOn + stationOn)
                .totalOffline(jplOff + trainOff + stationOff)
                .checkedAt(Instant.now())
                .build();
    }
}
