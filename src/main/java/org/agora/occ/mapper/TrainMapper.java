package org.agora.occ.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.agora.occ.dto.train.request.CreateTrainRequest;
import org.agora.occ.dto.train.response.TrainResponse;
import org.agora.occ.entity.RouteEntity;
import org.agora.occ.entity.TrainEntity;

@ApplicationScoped
public class TrainMapper {

    public TrainEntity toEntity(CreateTrainRequest request, RouteEntity route) {
        if (request == null) {
            return null;
        }

        TrainEntity entity = new TrainEntity();
        entity.setTrainName(request.getTrainName());
        entity.setTrainCode(request.getTrainCode());
        entity.setTrainNetworkIp(request.getTrainNetworkIp());
        entity.setTrainStatus(request.getTrainStatus());
        entity.setTrainOnline(false); // Default logic
        entity.setCategory(request.getCategory());
        entity.setRoute(route);

        return entity;
    }

    public TrainResponse toResponse(TrainEntity entity) {
        if (entity == null) {
            return null;
        }

        TrainResponse response = new TrainResponse();
        response.setId(entity.getId());
        response.setTrainName(entity.getTrainName());
        response.setTrainCode(entity.getTrainCode());
        response.setTrainNetworkIp(entity.getTrainNetworkIp());
        response.setTrainStatus(entity.getTrainStatus());
        response.setTrainOnline(entity.getTrainOnline());
        response.setCategory(entity.getCategory());
        response.setTrainLastKnownLatitude(entity.getTrainLastKnownLatitude());
        response.setTrainLastKnownLongitude(entity.getTrainLastKnownLongitude());

        if (entity.getRoute() != null) {
            response.setRouteId(entity.getRoute().getId());
        }

        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());

        return response;
    }

    public void updateEntity(TrainEntity entity, CreateTrainRequest request, RouteEntity route) {
        if (entity != null && request != null) {
            entity.setTrainName(request.getTrainName());
            entity.setTrainCode(request.getTrainCode());
            entity.setTrainNetworkIp(request.getTrainNetworkIp());
            entity.setTrainStatus(request.getTrainStatus());
            entity.setCategory(request.getCategory());
            entity.setRoute(route);
        }
    }
}
