package com.warehouse.repository;

import com.warehouse.entity.TrackingCarrier;
import com.warehouse.entity.TrackingEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface TrackingEntryRepository extends JpaRepository<TrackingEntry, Long> {

    @Query(
            "SELECT t FROM TrackingEntry t " +
                    "WHERE (:carrier IS NULL OR t.carrier = :carrier) " +
                    "AND (:trackingNumber IS NULL OR LOWER(t.trackingNumber) LIKE LOWER(CONCAT('%', :trackingNumber, '%'))) " +
                    "AND (:startTime IS NULL OR " +
                    "     (t.scannedAt IS NOT NULL AND t.scannedAt >= :startTime) OR " +
                    "     (t.scannedAt IS NULL AND t.createdAt >= :startTime)) " +
                    "AND (:endTime IS NULL OR " +
                    "     (t.scannedAt IS NOT NULL AND t.scannedAt <= :endTime) OR " +
                    "     (t.scannedAt IS NULL AND t.createdAt <= :endTime))"
    )
    Page<TrackingEntry> search(
            @Param("carrier") TrackingCarrier carrier,
            @Param("trackingNumber") String trackingNumber,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable);
}
