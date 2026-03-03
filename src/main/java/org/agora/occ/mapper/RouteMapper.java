package org.agora.occ.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.agora.occ.dto.route.request.CreateRouteRequest;
import org.agora.occ.dto.route.response.RouteDetailResponse;
import org.agora.occ.dto.route.response.RouteResponse;
import org.agora.occ.dto.route.response.RouteSegmentDetailResponse;
import org.agora.occ.entity.RouteEntity;

import java.util.List;

@ApplicationScoped
public class RouteMapper {

    public RouteEntity toEntity(CreateRouteRequest request) {
        if (request == null) {
            return null;
        }

        RouteEntity entity = new RouteEntity();
        entity.setRouteCode(request.getRouteCode());
        entity.setRouteDistance(request.getRouteDistance());
        entity.setCategory(request.getCategory());
        entity.setIsActive(request.getIsActive());
        entity.setFromStationName(request.getFromStationName());
        entity.setToStationName(request.getToStationName());

        return entity;
    }

    public RouteResponse toResponse(RouteEntity entity) {
        if (entity == null) {
            return null;
        }

        RouteResponse response = new RouteResponse();
        response.setRouteId(entity.getId());
        response.setRouteCode(entity.getRouteCode());
        response.setRouteDistance(entity.getRouteDistance());
        response.setCategory(entity.getCategory());
        response.setIsActive(entity.getIsActive());
        response.setFromStationName(entity.getFromStationName());
        response.setToStationName(entity.getToStationName());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());

        return response;
    }

    public RouteDetailResponse toDetailResponse(RouteEntity entity, List<RouteSegmentDetailResponse> segments) {
        if (entity == null) {
            return null;
        }

        RouteDetailResponse response = new RouteDetailResponse();
        response.setRouteId(entity.getId());
        response.setRouteCode(entity.getRouteCode());
        response.setRouteDistance(entity.getRouteDistance());
        response.setCategory(entity.getCategory());
        response.setIsActive(entity.getIsActive());
        response.setFromStationName(entity.getFromStationName());
        response.setToStationName(entity.getToStationName());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        response.setSegments(segments);

        return response;
    }
}
