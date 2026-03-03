package org.agora.occ.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.agora.occ.entity.UserEntity;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserRepository implements PanacheRepositoryBase<UserEntity, UUID> {

    /**
     * Finds a user by their NRP (Nomor Registrasi Pegawai).
     *
     * @param nrp the NRP to search for
     * @return optional containing the user if found
     */
    public Optional<UserEntity> findByNrp(String nrp) {
        return find("nrp", nrp).firstResultOptional();
    }

    /**
     * Checks if a user exists with the given NRP.
     *
     * @param nrp the NRP to check
     * @return true if NRP exists, false otherwise
     */
    public boolean existsByNrp(String nrp) {
        return count("nrp", nrp) > 0;
    }
}
