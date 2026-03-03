package org.agora.occ.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.agora.occ.entity.UserAttendanceEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing UserAttendanceEntity instances.
 */
@ApplicationScoped
public class UserAttendanceRepository implements PanacheRepositoryBase<UserAttendanceEntity, UUID> {

    /**
     * Finds the most recent open attendance record (check_out is null) for a given
     * user.
     * Orders by check_in descending so we get the latest login session.
     *
     * @param userId the UUID of the user
     * @return the Optional containing the open UserAttendanceEntity if found
     */
    public Optional<UserAttendanceEntity> findLatestOpenByUserId(UUID userId) {
        return find("user.userId = ?1 and checkOut is null order by checkIn desc", userId)
                .firstResultOptional();
    }
}
