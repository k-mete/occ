package org.agora.occ.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.agora.occ.dto.scheduleplan.request.SchedulePlanRequest;
import org.agora.occ.dto.scheduleplan.response.SchedulePlanResponse;
import org.agora.occ.entity.JplEntity;
import org.agora.occ.entity.JplSchedulePlanEntity;
import org.agora.occ.entity.TrainEntity;
import org.agora.occ.enums.SchedulePlanType;

@ApplicationScoped
public class JplSchedulePlanMapper {

    public JplSchedulePlanEntity toEntity(SchedulePlanRequest request, TrainEntity train, JplEntity jpl) {
        if (request == null) {
            return null;
        }

        JplSchedulePlanEntity entity = new JplSchedulePlanEntity();
        entity.setTrain(train);
        entity.setJpl(jpl);
        entity.setEstimatedPassTime(request.getEstimatedPassTime());
        entity.setDirection(request.getDirection());

        return entity;
    }

    public SchedulePlanResponse toResponse(JplSchedulePlanEntity entity) {
        if (entity == null) {
            return null;
        }

        SchedulePlanResponse response = new SchedulePlanResponse();
        response.setType(SchedulePlanType.JPL_SCHEDULE.name());
        response.setPlanId(entity.getPlanId());

        if (entity.getTrain() != null) {
            response.setTrainId(entity.getTrain().getId());
            response.setTrainCode(entity.getTrain().getTrainCode());
            response.setTrainName(entity.getTrain().getTrainName());
        }

        if (entity.getJpl() != null) {
            response.setJplId(entity.getJpl().getId());
        }

        response.setEstimatedPassTime(entity.getEstimatedPassTime());
        response.setDirection(entity.getDirection());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());

        return response;
    }

    public void updateEntity(JplSchedulePlanEntity entity, SchedulePlanRequest request, TrainEntity train,
            JplEntity jpl) {
        if (entity != null && request != null) {
            if (train != null) {
                entity.setTrain(train);
            }
            if (jpl != null) {
                entity.setJpl(jpl);
            }
            entity.setEstimatedPassTime(request.getEstimatedPassTime());
            entity.setDirection(request.getDirection());
        }
    }
}
