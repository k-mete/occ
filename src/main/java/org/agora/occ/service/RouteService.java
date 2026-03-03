package org.agora.occ.service;

import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.agora.occ.dto.common.PagedResult;
import org.agora.occ.dto.jpl.response.JplResponse;
import org.agora.occ.dto.route.request.CreateRouteRequest;
import org.agora.occ.dto.route.request.JplRefItem;
import org.agora.occ.dto.route.request.SegmentItem;
import org.agora.occ.dto.route.response.RouteDetailResponse;
import org.agora.occ.dto.route.response.RouteResponse;
import org.agora.occ.dto.route.response.RouteSegmentDetailResponse;
import org.agora.occ.entity.JplEntity;
import org.agora.occ.entity.RouteEntity;
import org.agora.occ.entity.RouteSegmentEntity;
import org.agora.occ.entity.RouteSegmentOrderEntity;
import org.agora.occ.entity.StationEntity;
import org.agora.occ.enums.TransportCategory;
import org.agora.occ.mapper.JplMapper;
import org.agora.occ.mapper.RouteMapper;
import org.agora.occ.mapper.RouteSegmentMapper;
import org.agora.occ.repository.JplRepository;
import org.agora.occ.repository.RouteRepository;
import org.agora.occ.repository.RouteSegmentOrderRepository;
import org.agora.occ.repository.RouteSegmentRepository;
import org.agora.occ.repository.StationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class RouteService {

    @Inject
    RouteRepository routeRepository;

    @Inject
    RouteSegmentRepository segmentRepository;

    @Inject
    RouteSegmentOrderRepository segmentOrderRepository;

    @Inject
    StationRepository stationRepository;

    @Inject
    JplRepository jplRepository;

    @Inject
    RouteMapper routeMapper;

    @Inject
    RouteSegmentMapper segmentMapper;

    @Inject
    JplMapper jplMapper;

    @Transactional
    public RouteDetailResponse create(CreateRouteRequest request) {
        if (routeRepository.find("routeCode", request.getRouteCode()).firstResultOptional().isPresent()) {
            throw new WebApplicationException("Route with code " + request.getRouteCode() + " already exists",
                    Response.Status.CONFLICT);
        }

        RouteEntity route = routeMapper.toEntity(request);
        route.setId(UUID.randomUUID());
        routeRepository.persist(route);

        return processSegments(route, request.getSegments());
    }

    @Transactional
    public RouteDetailResponse update(UUID id, CreateRouteRequest request) {
        RouteEntity route = routeRepository.findByIdOptional(id)
                .orElseThrow(() -> new WebApplicationException("Route not found", Response.Status.NOT_FOUND));

        // Check if code changes and conflicts
        if (!route.getRouteCode().equals(request.getRouteCode()) &&
                routeRepository.find("routeCode", request.getRouteCode()).firstResultOptional().isPresent()) {
            throw new WebApplicationException("Route with code " + request.getRouteCode() + " already exists",
                    Response.Status.CONFLICT);
        }

        // Update basic info
        route.setRouteCode(request.getRouteCode());
        route.setRouteDistance(request.getRouteDistance());
        route.setCategory(request.getCategory());
        route.setIsActive(request.getIsActive());
        route.setFromStationName(request.getFromStationName());
        route.setToStationName(request.getToStationName());

        // Delete all old segment orders
        segmentOrderRepository.deleteByRouteId(route.getId());

        // We do *not* delete segments themselves to preserve history for old trips,
        // just their order in this route.
        // Actually to implement full replacement purely, we process new segments from
        // scratch.
        return processSegments(route, request.getSegments());
    }

    private RouteDetailResponse processSegments(RouteEntity route, List<SegmentItem> segments) {
        List<RouteSegmentDetailResponse> segmentResponses = new ArrayList<>();
        int index = 1;

        for (SegmentItem item : segments) {
            StationEntity fromStation = stationRepository.findByStationName(item.getFromStationId())
                    .orElse(null);
            StationEntity toStation = stationRepository.findByStationName(item.getToStationId())
                    .orElse(null);

            RouteSegmentEntity segment = new RouteSegmentEntity();
            segment.setId(UUID.randomUUID());
            segment.setRouteSegmentCode(item.getRouteSegmentCode());
            segment.setFromStation(fromStation);
            segment.setToStation(toStation);
            segment.setRouteDuration(item.getRouteDuration());
            segment.setRouteDistance(item.getRouteDistance());
            segment.setRouteStatus(item.getRouteStatus());

            // Deduplicate JPLs
            List<UUID> uniqueJplIds = item.getJpls() == null ? new ArrayList<>()
                    : item.getJpls().stream().map(JplRefItem::getJplId).distinct().collect(Collectors.toList());

            List<JplEntity> mappedJpls = new ArrayList<>();
            List<JplResponse> jplResponses = new ArrayList<>();
            for (UUID jplId : uniqueJplIds) {
                jplRepository.findByIdOptional(jplId).ifPresent(jpl -> {
                    mappedJpls.add(jpl);
                    jplResponses.add(jplMapper.toResponse(jpl));
                });
            }
            segment.setJpls(mappedJpls);
            segmentRepository.persist(segment);

            RouteSegmentOrderEntity order = new RouteSegmentOrderEntity();
            order.setRoute(route);
            order.setRouteSegment(segment);
            order.setSegmentIndex(index);
            segmentOrderRepository.persist(order);

            segmentResponses.add(segmentMapper.toDetailResponse(segment, index, jplResponses));
            index++;
        }

        return routeMapper.toDetailResponse(route, segmentResponses);
    }

    public RouteResponse findById(UUID id) {
        RouteEntity route = routeRepository.findByIdOptional(id)
                .orElseThrow(() -> new WebApplicationException("Route not found", Response.Status.NOT_FOUND));
        return routeMapper.toResponse(route);
    }

    public RouteDetailResponse getDetail(UUID id) {
        RouteEntity route = routeRepository.findByIdOptional(id)
                .orElseThrow(() -> new WebApplicationException("Route not found", Response.Status.NOT_FOUND));

        List<RouteSegmentOrderEntity> orders = segmentOrderRepository.findByRouteIdOrderedByIndex(id);

        List<RouteSegmentDetailResponse> segmentResponses = orders.stream().map(order -> {
            RouteSegmentEntity segment = order.getRouteSegment();
            List<JplResponse> jpls = segment.getJpls().stream()
                    .map(jplMapper::toResponse)
                    .collect(Collectors.toList());
            return segmentMapper.toDetailResponse(segment, order.getSegmentIndex(), jpls);
        }).collect(Collectors.toList());

        return routeMapper.toDetailResponse(route, segmentResponses);
    }

    public PagedResult<RouteResponse> findAll(TransportCategory category, Boolean isActive, int page, int size) {
        List<RouteResponse> data = routeRepository.findFiltered(category, isActive, page, size)
                .stream()
                .map(routeMapper::toResponse)
                .collect(Collectors.toList());

        long totalElements = routeRepository.countFiltered(category, isActive);

        return new PagedResult<>(data, page, size, totalElements);
    }

    @Transactional
    public void delete(UUID id) {
        if (!routeRepository.deleteById(id)) {
            throw new WebApplicationException("Route not found", Response.Status.NOT_FOUND);
        }
    }
}
