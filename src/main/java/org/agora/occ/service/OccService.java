package org.agora.occ.service;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.agora.occ.dto.common.PagedResult;
import org.agora.occ.dto.occ.request.CreateOccRequest;
import org.agora.occ.dto.occ.response.OccDetailResponse;
import org.agora.occ.dto.occ.response.OccResponse;
import org.agora.occ.entity.OccEntity;
import org.agora.occ.mapper.OccMapper;
import org.agora.occ.repository.OccRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class OccService {

    private final OccRepository occRepository;
    private final OccMapper occMapper;
    private final StationService stationService;

    @Inject
    public OccService(OccRepository occRepository, OccMapper occMapper, StationService stationService) {
        this.occRepository = occRepository;
        this.occMapper = occMapper;
        this.stationService = stationService;
    }

    @Transactional
    public OccResponse create(CreateOccRequest request) {
        OccEntity entity = occMapper.toEntity(request);
        occRepository.persist(entity);
        return occMapper.toResponse(entity);
    }

    public OccResponse findById(UUID id) {
        OccEntity entity = getEntity(id);
        return occMapper.toResponse(entity);
    }

    public PagedResult<OccResponse> findAll(int page, int size) {
        PanacheQuery<OccEntity> query = occRepository.findAll();
        if (size > 0) {
            query.page(page, size);
        }
        List<OccResponse> data = query.list().stream()
                .map(occMapper::toResponse)
                .collect(Collectors.toList());
        return new PagedResult<>(data, page, size, query.count());
    }

    public OccDetailResponse getDetail(UUID id) {
        OccEntity entity = getEntity(id);
        return occMapper.toDetailResponse(entity, stationService.findByOccId(id));
    }

    @Transactional
    public OccResponse update(UUID id, CreateOccRequest request) {
        OccEntity entity = getEntity(id);
        occMapper.updateEntity(entity, request);
        entity.setUpdatedAt(Instant.now());
        return occMapper.toResponse(entity);
    }

    @Transactional
    public void delete(UUID id) {
        if (!occRepository.deleteById(id)) {
            throw new NotFoundException("OCC with ID " + id + " not found");
        }
    }

    private OccEntity getEntity(UUID id) {
        return occRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("OCC with ID " + id + " not found"));
    }
}
