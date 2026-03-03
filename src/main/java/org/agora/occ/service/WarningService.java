package org.agora.occ.service;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.agora.occ.dto.common.PagedResult;
import org.agora.occ.dto.warning.WarningResponse;
import org.agora.occ.entity.WarningEntity;
import org.agora.occ.entity.JplEntity;
import org.agora.occ.entity.event.WarningReceivedEvent;
import org.agora.occ.enums.WarningLevel;
import org.agora.occ.repository.WarningRepository;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing safety warning records.
 */
@ApplicationScoped
public class WarningService {

        private static final Logger LOG = Logger.getLogger(WarningService.class);

        private final WarningRepository warningRepository;

        @Inject
        public WarningService(WarningRepository warningRepository) {
                this.warningRepository = warningRepository;
        }

        /**
         * Persists a newly received warning event.
         *
         * @param event the CDI event containing warning data
         */
        @Transactional
        public void save(WarningReceivedEvent event) {
                LOG.debugv("Persisting warning alert {0} for Train {1} at JPL {2}",
                                event.alertId(), event.trainId(), event.jplId());

                WarningEntity entity = WarningEntity.builder()
                                .alertId(event.alertId())
                                .jpl(JplEntity.builder().id(event.jplId()).build())
                                .cameraId(event.cameraId())
                                .jplCode(event.jplCode())
                                .jplName(event.jplName())
                                .trainId(event.trainId())
                                .trainName(event.trainName())
                                .trainCode(event.trainCode())
                                .crowdLevel(event.crowdLevel())
                                .warningLevel(event.warningLevel())
                                .distanceKm(event.distanceKm())
                                .speedKmh(event.speedKmh())
                                .objectDetected(event.objectDetected())
                                .alertTimestamp(event.alertTimestamp())
                                .actionRequired(event.actionRequired())
                                .colorIndicator(event.colorIndicator())
                                .build();

                warningRepository.persist(entity);
        }

        /**
         * Retrieves paginated warning records based on optional filters.
         *
         * @param jplId        optional JPL ID filter
         * @param trainId      optional Train ID filter
         * @param warningLevel optional severity filter
         * @param dateFrom     optional start time filter
         * @param dateTo       optional end time filter
         * @param page         the 0-indexed page number
         * @param size         the page size
         * @return a paged result containing the mapped warning DTOs
         */
        public PagedResult<WarningResponse> getWarningsPaginated(
                        UUID jplId, UUID trainId, WarningLevel warningLevel,
                        Instant dateFrom, Instant dateTo, int page, int size) {

                PanacheQuery<WarningEntity> query = warningRepository.findFiltered(
                                jplId, trainId, warningLevel, dateFrom, dateTo);

                List<WarningResponse> data = query.page(page, size).list()
                                .stream().map(WarningResponse::from).collect(Collectors.toList());

                return new PagedResult<>(data, page, size, query.count());
        }

        /**
         * Retrieves all warning records (unpaginated) based on optional filters.
         *
         * @param jplId        optional JPL ID filter
         * @param trainId      optional Train ID filter
         * @param warningLevel optional severity filter
         * @param dateFrom     optional start time filter
         * @param dateTo       optional end time filter
         * @return a list of all matching warning DTOs
         */
        public List<WarningResponse> getWarningsUnpaginated(
                        UUID jplId, UUID trainId, WarningLevel warningLevel,
                        Instant dateFrom, Instant dateTo) {

                PanacheQuery<WarningEntity> query = warningRepository.findFiltered(
                                jplId, trainId, warningLevel, dateFrom, dateTo);

                return query.list().stream().map(WarningResponse::from).collect(Collectors.toList());
        }
}
