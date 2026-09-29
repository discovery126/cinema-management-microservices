package org.example.movieservice.repository;

import org.example.movieservice.model.Screening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ScreeningRepository extends JpaRepository<Screening, UUID> {
    @Query("""
    SELECT COUNT(s) > 0 FROM Screening s
    WHERE s.hall = :hall
      AND s.startsAt < :end
      AND :start < s.endsAt
    """)
    boolean existsOverlapping(
            @Param("hall") String hall,
            @Param("start") Instant start,
            @Param("end") Instant end);

    List<Screening> findAllByMovieId(UUID movieId);
}
