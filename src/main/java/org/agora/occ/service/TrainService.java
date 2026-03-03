package org.agora.occ.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.agora.occ.dto.common.PagedResult;
import org.agora.occ.dto.train.request.CreateTrainRequest;
import org.agora.occ.dto.train.response.TrainResponse;
import org.agora.occ.entity.RouteEntity;
import org.agora.occ.entity.TrainEntity;
import org.agora.occ.enums.ActiveStatus;
import org.agora.occ.enums.TransportCategory;
import org.agora.occ.mapper.TrainMapper;
import org.agora.occ.repository.RouteRepository;
import org.agora.occ.repository.TrainRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class TrainService {

    @Inject
    TrainRepository trainRepository;

    @Inject
    RouteRepository routeRepository;

    @Inject
    TrainMapper trainMapper;

    @Transactional
    public TrainResponse create(CreateTrainRequest request) {
        RouteEntity route = null;
        if (request.getRouteId() != null) {
            route = routeRepository.findByIdOptional(request.getRouteId()).orElse(null);
        }

        TrainEntity train = trainMapper.toEntity(request, route);
        train.setId(UUID.randomUUID());

        trainRepository.persist(train);
        return trainMapper.toResponse(train);
    }

    @Transactional
    public TrainResponse update(UUID id, CreateTrainRequest request) {
        TrainEntity train = trainRepository.findByIdOptional(id)
                .orElseThrow(() -> new WebApplicationException("Train not found", Response.Status.NOT_FOUND));

        RouteEntity route = null;
        if (request.getRouteId() != null) {
            route = routeRepository.findByIdOptional(request.getRouteId()).orElse(null);
        }

        trainMapper.updateEntity(train, request, route);
        return trainMapper.toResponse(train);
    }

    public TrainResponse findById(UUID id) {
        TrainEntity train = trainRepository.findByIdOptional(id)
                .orElseThrow(() -> new WebApplicationException("Train not found", Response.Status.NOT_FOUND));
        return trainMapper.toResponse(train);
    }

    public PagedResult<TrainResponse> findAll(TransportCategory category, ActiveStatus status, int page, int size) {
        List<TrainResponse> data = trainRepository.findFiltered(category, status, page, size)
                .stream()
                .map(trainMapper::toResponse)
                .collect(Collectors.toList());

        long totalElements = trainRepository.countFiltered(category, status);

        return new PagedResult<>(data, page, size, totalElements);
    }

    @Transactional
    public void delete(UUID id) {
        if (!trainRepository.deleteById(id)) {
            throw new WebApplicationException("Train not found", Response.Status.NOT_FOUND);
        }
    }
}
