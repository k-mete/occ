package org.agora.occ.service;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.agora.occ.dto.common.PagedResult;
import org.agora.occ.dto.jpl.request.CreateJplRequest;
import org.agora.occ.dto.jpl.response.JplResponse;
import org.agora.occ.entity.JplEntity;
import org.agora.occ.enums.ActiveStatus;
import org.agora.occ.mapper.JplMapper;
import org.agora.occ.repository.JplRepository;
import org.agora.occ.repository.StationRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class JplService {

    private final JplRepository jplRepository;
    private final StationRepository stationRepository;
    private final JplMapper jplMapper;

    @Inject
    public JplService(JplRepository jplRepository, StationRepository stationRepository, JplMapper jplMapper) {
        this.jplRepository = jplRepository;
        this.stationRepository = stationRepository;
        this.jplMapper = jplMapper;
    }

    @Transactional
    public JplResponse create(CreateJplRequest request) {
        if (stationRepository.findByIdOptional(request.getStationId()).isEmpty()) {
            throw new NotFoundException("Station with ID " + request.getStationId() + " not found");
        }
        JplEntity entity = jplMapper.toEntity(request);
        jplRepository.persist(entity);
        return jplMapper.toResponse(entity);
    }

    public JplResponse findById(UUID id) {
        JplEntity entity = getEntity(id);
        return jplMapper.toResponse(entity);
    }

    public PagedResult<JplResponse> findAll(UUID stationId, UUID occId, ActiveStatus status, int page, int size) {
        List<JplEntity> entities;
        long total;
        if (stationId != null || occId != null || status != null) {
            entities = jplRepository.findFiltered(stationId, occId, status, page, size);
            total = jplRepository.countFiltered(stationId, occId, status);
        } else {
            PanacheQuery<JplEntity> query = jplRepository.findAll();
            if (size > 0) {
                query.page(page, size);
            }
            entities = query.list();
            total = query.count();
        }

        List<JplResponse> data = entities.stream()
                .map(jplMapper::toResponse)
                .collect(Collectors.toList());

        return new PagedResult<>(data, page, size, total);
    }

    public List<JplResponse> findByStationId(UUID stationId) {
        return jplRepository.findByStationId(stationId).stream()
                .map(jplMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public JplResponse update(UUID id, CreateJplRequest request) {
        JplEntity entity = getEntity(id);
        if (request.getStationId() != null && stationRepository.findByIdOptional(request.getStationId()).isEmpty()) {
            throw new NotFoundException("Station with ID " + request.getStationId() + " not found");
        }
        jplMapper.updateEntity(entity, request);
        entity.setUpdatedAt(Instant.now());
        return jplMapper.toResponse(entity);
    }

    @Transactional
    public void delete(UUID id) {
        if (!jplRepository.deleteById(id)) {
            throw new NotFoundException("JPL with ID " + id + " not found");
        }
    }

    private JplEntity getEntity(UUID id) {
        return jplRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("JPL with ID " + id + " not found"));
    }
}
