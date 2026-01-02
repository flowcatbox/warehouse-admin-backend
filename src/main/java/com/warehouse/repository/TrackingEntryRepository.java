package com.warehouse.repository;

import com.warehouse.entity.TrackingEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.warehouse.entity.TrackingCarrier;
import com.warehouse.entity.TrackingEntryStatus;

@Repository
public interface TrackingEntryRepository extends JpaRepository<TrackingEntry, Long> {

    Page<TrackingEntry> findAll(Specification<TrackingEntry> spec, Pageable pageable);

    boolean existsByCarrierAndTrackingNumberAndStatus(
            TrackingCarrier carrier,
            String trackingNumber,
            TrackingEntryStatus status
    );
}
