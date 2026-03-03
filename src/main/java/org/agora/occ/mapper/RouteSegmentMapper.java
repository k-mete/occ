package org.agora.occ.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.agora.occ.dto.jpl.response.JplResponse;
import org.agora.occ.dto.route.response.RouteSegmentDetailResponse;
import org.agora.occ.entity.RouteSegmentEntity;

import java.util.List;

@ApplicationScoped
public class RouteSegmentMapper {

    public RouteSegmentDetailResponse toDetailResponse(RouteSegmentEntity entity, int segmentIndex,
            List<JplResponse> jpls) {
        if (entity == null) {
            return null;
        }

        RouteSegmentDetailResponse response = new RouteSegmentDetailResponse();
        response.setRouteSegmentId(entity.getId());
        response.setRouteSegmentCode(entity.getRouteSegmentCode());

        if (entity.getFromStation() != null) {
            response.setFromStationId(entity.getFromStation().getId());
            response.setFromStationName(entity.getFromStation().getStationName());
            response.setFromStationLatitude(entity.getFromStation().getStationLatitude());
            response.setFromStationLongitude(entity.getFromStation().getStationLongitude());
            response.setFromStationStatus(entity.getFromStation().getStationStatus());
        }

        if (entity.getToStation() != null) {
            response.setToStationId(entity.getToStation().getId());
            response.setToStationName(entity.getToStation().getStationName());
            response.setToStationLatitude(entity.getToStation().getStationLatitude());
            response.setToStationLongitude(entity.getToStation().getStationLongitude());
            response.setToStationStatus(entity.getToStation().getStationStatus());
        }

        response.setRouteDuration(entity.getRouteDuration());
        response.setRouteDistance(entity.getRouteDistance());
        response.setRouteStatus(entity.getRouteStatus());
        response.setSegmentIndex(segmentIndex);
        response.setJpls(jpls);

        return response;
    }
}
