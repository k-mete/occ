package org.agora.occ.service;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.agora.occ.dto.common.PagedResult;
import org.agora.occ.dto.station.request.CreateStationRequest;
import org.agora.occ.dto.station.response.StationDetailResponse;
import org.agora.occ.dto.station.response.StationResponse;
import org.agora.occ.entity.StationEntity;
import org.agora.occ.enums.ActiveStatus;
import org.agora.occ.mapper.StationMapper;
import org.agora.occ.repository.OccRepository;
import org.agora.occ.repository.StationRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class StationService {

    private final StationRepository stationRepository;
    private final OccRepository occRepository;
    private final StationMapper stationMapper;
    private final JplService jplService;

    @Inject
    public StationService(StationRepository stationRepository, OccRepository occRepository, StationMapper stationMapper,
            JplService jplService) {
        this.stationRepository = stationRepository;
        this.occRepository = occRepository;
        this.stationMapper = stationMapper;
        this.jplService = jplService;
    }

    @Transactional
    public StationResponse create(CreateStationRequest request) {
        if (occRepository.findByIdOptional(request.getOccId()).isEmpty()) {
            throw new NotFoundException("OCC with ID " + request.getOccId() + " not found");
        }
        StationEntity entity = stationMapper.toEntity(request);
        stationRepository.persist(entity);
        return stationMapper.toResponse(entity);
    }

    public StationResponse findById(UUID id) {
        StationEntity entity = getEntity(id);
        return stationMapper.toResponse(entity);
    }

    public PagedResult<StationResponse> findAll(UUID occId, ActiveStatus status, int page, int size) {
        List<StationEntity> entities;
        long total;
        if (occId != null || status != null) {
            entities = stationRepository.findFiltered(occId, status, page, size);
            total = stationRepository.countFiltered(occId, status);
        } else {
            PanacheQuery<StationEntity> query = stationRepository.findAll();
            if (size > 0) {
                query.page(page, size);
            }
            entities = query.list();
            total = query.count();
        }

        List<StationResponse> data = entities.stream()
                .map(stationMapper::toResponse)
                .collect(Collectors.toList());

        return new PagedResult<>(data, page, size, total);
    }

    public List<StationResponse> findByOccId(UUID occId) {
        return stationRepository.findByOccId(occId).stream()
                .map(stationMapper::toResponse)
                .collect(Collectors.toList());
    }

    public StationDetailResponse getDetail(UUID id) {
        StationEntity entity = getEntity(id);
        return stationMapper.toDetailResponse(entity, jplService.findByStationId(id));
    }

    @Transactional
    public StationResponse update(UUID id, CreateStationRequest request) {
        StationEntity entity = getEntity(id);
        if (request.getOccId() != null && occRepository.findByIdOptional(request.getOccId()).isEmpty()) {
            throw new NotFoundException("OCC with ID " + request.getOccId() + " not found");
        }
        stationMapper.updateEntity(entity, request);
        entity.setUpdatedAt(Instant.now());
        return stationMapper.toResponse(entity);
    }

    @Transactional
    public void delete(UUID id) {
        if (!stationRepository.deleteById(id)) {
            throw new NotFoundException("Station with ID " + id + " not found");
        }
    }

    private StationEntity getEntity(UUID id) {
        return stationRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Station with ID " + id + " not found"));
    }
}
