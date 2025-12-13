package com.warehouse.repository;

import com.warehouse.entity.TrackingNumber;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackingNumberRepository extends JpaRepository<TrackingNumber, Long> {

    Page<TrackingNumber> findAll(Specification<TrackingNumber> spec, Pageable pageable);

    TrackingNumber findByTrackingNumber(String trackingNumber);
}
