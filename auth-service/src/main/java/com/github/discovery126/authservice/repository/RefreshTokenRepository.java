package com.github.discovery126.authservice.repository;

import com.github.discovery126.authservice.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findAllByUserIdAndRevoked(UUID userId, boolean revoked);

    @Modifying
    @Query("""
            UPDATE RefreshToken t
            SET t.revoked = true,
                t.revokedAt = :now,
                t.revokeReason = :reason
            WHERE t.user.id = :userId
              AND t.revoked = false
            """)
    int revokeAllByUserId(@Param("userId") UUID userId,
                          @Param("now") Instant now,
                          @Param("reason") String reason);
}