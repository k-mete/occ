package org.agora.occ.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.agora.occ.entity.RefreshTokenEntity;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class RefreshTokenRepository implements PanacheRepositoryBase<RefreshTokenEntity, UUID> {

    /**
     * Finds a refresh token entity by the exact token string.
     *
     * @param tokenStr the refresh token string
     * @return optional containing the refresh token entity if found
     */
    public Optional<RefreshTokenEntity> findByTokenStr(String tokenStr) {
        return find("tokenStr", tokenStr).firstResultOptional();
    }
}
