package org.agora.occ.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.agora.occ.dto.scheduleplan.request.SchedulePlanRequest;
import org.agora.occ.dto.scheduleplan.response.SchedulePlanResponse;
import org.agora.occ.entity.StationEntity;
import org.agora.occ.entity.StationSchedulePlanEntity;
import org.agora.occ.entity.TrainEntity;
import org.agora.occ.enums.SchedulePlanType;

@ApplicationScoped
public class StationSchedulePlanMapper {

    public StationSchedulePlanEntity toEntity(SchedulePlanRequest request, TrainEntity train, StationEntity station) {
        if (request == null) {
            return null;
        }

        StationSchedulePlanEntity entity = new StationSchedulePlanEntity();
        entity.setTrain(train);
        entity.setStation(station);
        entity.setArrivalPlan(request.getArrivalPlan());
        entity.setDeparturePlan(request.getDeparturePlan());
        entity.setDescription(request.getDescription());
        entity.setDirection(request.getDirection());

        return entity;
    }

    public SchedulePlanResponse toResponse(StationSchedulePlanEntity entity) {
        if (entity == null) {
            return null;
        }

        SchedulePlanResponse response = new SchedulePlanResponse();
        response.setType(SchedulePlanType.STATION_SCHEDULE.name());
        response.setPlanId(entity.getPlanId());

        if (entity.getTrain() != null) {
            response.setTrainId(entity.getTrain().getId());
            response.setTrainCode(entity.getTrain().getTrainCode());
            response.setTrainName(entity.getTrain().getTrainName());
        }

        if (entity.getStation() != null) {
            response.setStationId(entity.getStation().getId());
        }

        response.setArrivalPlan(entity.getArrivalPlan());
        response.setDeparturePlan(entity.getDeparturePlan());
        response.setDescription(entity.getDescription());
        response.setDirection(entity.getDirection());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());

        return response;
    }

    public void updateEntity(StationSchedulePlanEntity entity, SchedulePlanRequest request, TrainEntity train,
            StationEntity station) {
        if (entity != null && request != null) {
            if (train != null) {
                entity.setTrain(train);
            }
            if (station != null) {
                entity.setStation(station);
            }
            entity.setArrivalPlan(request.getArrivalPlan());
            entity.setDeparturePlan(request.getDeparturePlan());
            entity.setDescription(request.getDescription());
            entity.setDirection(request.getDirection());
        }
    }
}
