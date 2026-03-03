package org.agora.occ.service;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.agora.occ.dto.attendance.response.AttendanceResponse;
import org.agora.occ.dto.common.PagedResult;
import org.agora.occ.entity.UserAttendanceEntity;
import org.agora.occ.entity.UserEntity;
import org.agora.occ.repository.UserAttendanceRepository;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service handling check-in and check-out logic for User Attendance.
 */
@ApplicationScoped
public class UserAttendanceService {

    private static final Logger LOG = Logger.getLogger(UserAttendanceService.class);

    private final UserAttendanceRepository userAttendanceRepository;

    @Inject
    public UserAttendanceService(UserAttendanceRepository userAttendanceRepository) {
        this.userAttendanceRepository = userAttendanceRepository;
    }

    /**
     * Checks in a user, returning the newly created attendance entity.
     * This marks the start of a user session.
     *
     * @param user the authenticated UserEntity
     * @return the saved UserAttendanceEntity
     */
    @Transactional
    public UserAttendanceEntity checkIn(UserEntity user) {
        LOG.debugv("Checking in user: {0} ({1})", user.getFullName(), user.getUserId());

        Optional<UserAttendanceEntity> existingOpen = userAttendanceRepository.findLatestOpenByUserId(user.getUserId());
        if (existingOpen.isPresent()) {
            LOG.warnv("User {0} checked in without checking out of a previous session. Closing old session implicitly.",
                    user.getUserId());
            UserAttendanceEntity oldSession = existingOpen.get();
            oldSession.setCheckOut(Instant.now());
            userAttendanceRepository.persist(oldSession);
        }

        UserAttendanceEntity attendance = new UserAttendanceEntity();
        attendance.setId(UUID.randomUUID());
        attendance.setUser(user);
        attendance.setCheckIn(Instant.now());
        attendance.setCreatedAt(Instant.now());

        userAttendanceRepository.persist(attendance);
        LOG.infov("User check-in created with ID: {0}", attendance.getId());

        return attendance;
    }

    /**
     * Checks out a user by finalizing their latest open attendance record.
     *
     * @param userId the UUID of the user logging out
     */
    @Transactional
    public void checkOut(UUID userId) {
        LOG.debugv("Checking out user: {0}", userId);

        Optional<UserAttendanceEntity> openSession = userAttendanceRepository.findLatestOpenByUserId(userId);
        if (openSession.isPresent()) {
            UserAttendanceEntity attendance = openSession.get();
            attendance.setCheckOut(Instant.now());
            userAttendanceRepository.persist(attendance);
            LOG.infov("Checked out user: {0} for attendance ID: {1}", userId, attendance.getId());
        } else {
            LOG.debugv("No open check-in session found for user: {0} to check out.", userId);
        }
    }

    /**
     * Retrieves paginated attendance logs for a specific user.
     *
     * @param userId the UUID of the user
     * @param page   the page number (0-indexed)
     * @param size   the page size
     * @return a PagedResult containing AttendanceResponse objects
     */
    public PagedResult<AttendanceResponse> getUserAttendanceHistory(UUID userId, int page, int size) {
        PanacheQuery<UserAttendanceEntity> query = userAttendanceRepository
                .find("user.userId", io.quarkus.panache.common.Sort.descending("checkIn"), userId)
                .page(page, size);

        List<AttendanceResponse> responses = query.list().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return new PagedResult<>(responses, page, size, query.count());
    }

    /**
     * Retrieves paginated attendance logs for all users.
     *
     * @param page the page number (0-indexed)
     * @param size the page size
     * @return a PagedResult containing AttendanceResponse objects
     */
    public PagedResult<AttendanceResponse> getAllAttendanceHistory(int page, int size) {
        PanacheQuery<UserAttendanceEntity> query = userAttendanceRepository
                .findAll(io.quarkus.panache.common.Sort.descending("checkIn"))
                .page(page, size);

        List<AttendanceResponse> responses = query.list().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return new PagedResult<>(responses, page, size, query.count());
    }

    private AttendanceResponse toResponse(UserAttendanceEntity entity) {
        return AttendanceResponse.builder()
                .id(entity.getId())
                .userId(entity.getUser().getUserId())
                .fullName(entity.getUser().getFullName())
                .role(entity.getUser().getRole().name())
                .checkIn(entity.getCheckIn())
                .checkOut(entity.getCheckOut())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
