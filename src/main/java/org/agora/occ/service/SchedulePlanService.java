package org.agora.occ.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.agora.occ.dto.common.PagedResult;
import org.agora.occ.dto.scheduleplan.request.SchedulePlanRequest;
import org.agora.occ.dto.scheduleplan.response.SchedulePlanResponse;
import org.agora.occ.enums.SchedulePlanType;
import org.agora.occ.repository.JplSchedulePlanRepository;
import org.agora.occ.repository.StationSchedulePlanRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class SchedulePlanService {

    @Inject
    JplSchedulePlanService jplService;

    @Inject
    StationSchedulePlanService stationService;

    @Inject
    JplSchedulePlanRepository jplRepository;

    @Inject
    StationSchedulePlanRepository stationRepository;

    public SchedulePlanResponse findById(UUID planId) {
        if (jplRepository.findByPlanId(planId).isPresent()) {
            return jplService.findById(planId);
        } else if (stationRepository.findByPlanId(planId).isPresent()) {
            return stationService.findById(planId);
        }
        throw new WebApplicationException("Schedule Plan not found", Response.Status.NOT_FOUND);
    }

    public PagedResult<SchedulePlanResponse> findAll(UUID trainId, String dateStr, SchedulePlanType type, int page,
            int size) {
        LocalDate date = parseDate(dateStr);

        if (type == SchedulePlanType.JPL_SCHEDULE) {
            return jplService.findAll(trainId, date, page, size);
        } else if (type == SchedulePlanType.STATION_SCHEDULE) {
            return stationService.findAll(trainId, date, page, size);
        }

        // Aggregate Both
        PagedResult<SchedulePlanResponse> jplResult = jplService.findAll(trainId, date, page, size);
        PagedResult<SchedulePlanResponse> stationResult = stationService.findAll(trainId, date, page, size);

        List<SchedulePlanResponse> combinedData = new ArrayList<>();
        combinedData.addAll(jplResult.getData());
        combinedData.addAll(stationResult.getData());

        long totalElements = jplResult.getTotalElements() + stationResult.getTotalElements();

        return new PagedResult<>(combinedData, page, size, totalElements);
    }

    @Transactional
    public SchedulePlanResponse update(UUID planId, SchedulePlanRequest request) {
        if (jplRepository.findByPlanId(planId).isPresent()) {
            return jplService.update(planId, request);
        } else if (stationRepository.findByPlanId(planId).isPresent()) {
            return stationService.update(planId, request);
        }
        throw new WebApplicationException("Schedule Plan not found", Response.Status.NOT_FOUND);
    }

    @Transactional
    public void delete(UUID planId) {
        if (jplRepository.findByPlanId(planId).isPresent()) {
            jplService.delete(planId);
            return;
        } else if (stationRepository.findByPlanId(planId).isPresent()) {
            stationService.delete(planId);
            return;
        }
        throw new WebApplicationException("Schedule Plan not found", Response.Status.NOT_FOUND);
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr != null && !dateStr.isBlank()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                return LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException e) {
                // Also support the dash format from the reference project just in case
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    return LocalDate.parse(dateStr, formatter);
                } catch (DateTimeParseException ex) {
                    throw new WebApplicationException("Invalid date format. Please use dd/MM/yyyy",
                            Response.Status.BAD_REQUEST);
                }
            }
        }
        return null;
    }
}
