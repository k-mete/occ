package org.agora.occ.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.agora.occ.dto.common.PagedResult;
import org.agora.occ.dto.scheduleplan.request.SchedulePlanRequest;
import org.agora.occ.dto.scheduleplan.response.SchedulePlanResponse;
import org.agora.occ.entity.JplEntity;
import org.agora.occ.entity.JplSchedulePlanEntity;
import org.agora.occ.entity.TrainEntity;
import org.agora.occ.mapper.JplSchedulePlanMapper;
import org.agora.occ.repository.JplRepository;
import org.agora.occ.repository.JplSchedulePlanRepository;
import org.agora.occ.repository.TrainRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class JplSchedulePlanService {

    @Inject
    JplSchedulePlanRepository repository;

    @Inject
    JplSchedulePlanMapper mapper;

    @Inject
    TrainRepository trainRepository;

    @Inject
    JplRepository jplRepository;

    @Transactional
    public SchedulePlanResponse create(SchedulePlanRequest request) {
        TrainEntity train = trainRepository.findByIdOptional(request.getTrainId())
                .orElseThrow(() -> new WebApplicationException("Train not found", Response.Status.NOT_FOUND));

        if (request.getJplId() == null) {
            throw new WebApplicationException("jplId is required for JPL_SCHEDULE", Response.Status.BAD_REQUEST);
        }

        JplEntity jpl = jplRepository.findByIdOptional(request.getJplId())
                .orElseThrow(() -> new WebApplicationException("JPL not found", Response.Status.NOT_FOUND));

        JplSchedulePlanEntity entity = mapper.toEntity(request, train, jpl);
        entity.setPlanId(UUID.randomUUID());
        repository.persist(entity);

        return mapper.toResponse(entity);
    }

    @Transactional
    public SchedulePlanResponse update(UUID planId, SchedulePlanRequest request) {
        JplSchedulePlanEntity entity = repository.findByPlanId(planId)
                .orElseThrow(
                        () -> new WebApplicationException("JPL Schedule Plan not found", Response.Status.NOT_FOUND));

        TrainEntity train = trainRepository.findByIdOptional(request.getTrainId())
                .orElseThrow(() -> new WebApplicationException("Train not found", Response.Status.NOT_FOUND));

        JplEntity jpl = null;
        if (request.getJplId() != null) {
            jpl = jplRepository.findByIdOptional(request.getJplId())
                    .orElseThrow(() -> new WebApplicationException("JPL not found", Response.Status.NOT_FOUND));
        }

        mapper.updateEntity(entity, request, train, jpl);
        return mapper.toResponse(entity);
    }

    public SchedulePlanResponse findById(UUID planId) {
        JplSchedulePlanEntity entity = repository.findByPlanId(planId)
                .orElseThrow(
                        () -> new WebApplicationException("JPL Schedule Plan not found", Response.Status.NOT_FOUND));
        return mapper.toResponse(entity);
    }

    public PagedResult<SchedulePlanResponse> findAll(UUID trainId, LocalDate date, int page, int size) {
        List<JplSchedulePlanEntity> entities;
        long total;

        if (trainId != null && date != null) {
            if (size == -1) {
                entities = repository.findByTrainIdAndDate(trainId, date);
            } else {
                entities = repository.findByTrainIdAndDate(trainId, date, page, size);
            }
            total = repository.countByTrainIdAndDate(trainId, date);
        } else if (trainId != null) {
            if (size == -1) {
                entities = repository.findByTrainId(trainId);
            } else {
                entities = repository.findByTrainId(trainId, page, size);
            }
            total = repository.countByTrainId(trainId);
        } else if (date != null) {
            if (size == -1) {
                entities = repository.findByDate(date);
            } else {
                entities = repository.findByDate(date, page, size);
            }
            total = repository.countByDate(date);
        } else {
            if (size == -1) {
                entities = repository.listAll();
            } else {
                entities = repository.findAll(page, size);
            }
            total = repository.count();
        }

        List<SchedulePlanResponse> data = entities.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());

        return new PagedResult<>(data, page, size, total);
    }

    @Transactional
    public void delete(UUID planId) {
        if (!repository.deleteById(planId)) {
            throw new WebApplicationException("JPL Schedule Plan not found", Response.Status.NOT_FOUND);
        }
    }
}
