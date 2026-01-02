package com.warehouse.service;

import com.warehouse.entity.TrackingCarrier;
import com.warehouse.entity.TrackingEntry;
import com.warehouse.entity.TrackingEntryStatus;
import com.warehouse.entity.TrackingNumber;
import com.warehouse.repository.TrackingEntryRepository;
import com.warehouse.repository.TrackingNumberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrackingEntryService {

    private final TrackingEntryRepository trackingEntryRepository;
    private final TrackingNumberRepository trackingNumberRepository;

    @Transactional
    public TrackingEntry createEntry(
            TrackingCarrier carrier,
            String trackingNumber,
            String scanSource,
            LocalDateTime scannedAt,
            String createdBy
    ) {
        LocalDateTime now = LocalDateTime.now();

        TrackingEntryStatus status = TrackingEntryStatus.NEW;

        boolean existsInNumber =
                trackingNumberRepository.findByTrackingNumber(trackingNumber) != null;

        boolean existsNewEntry =
                trackingEntryRepository.existsByCarrierAndTrackingNumberAndStatus(
                        carrier,
                        trackingNumber,
                        TrackingEntryStatus.NEW
                );

        if (existsInNumber || existsNewEntry) {
            status = TrackingEntryStatus.DUPLICATE;
        }

        TrackingEntry entry = TrackingEntry.builder()
                .carrier(carrier)
                .trackingNumber(trackingNumber)
                .scanSource(scanSource)
                .scannedAt(scannedAt != null ? scannedAt : now)
                .createdBy(createdBy)
                .status(status)
                .build();

        return trackingEntryRepository.save(entry);
    }

    @Transactional
    public List<TrackingEntry> createBatch(List<TrackingEntry> entries) {
        return trackingEntryRepository.saveAll(entries);
    }

    @Transactional(readOnly = true)
    public Page<TrackingEntry> getTrackingEntriesWithPagination(
            Pageable pageable,
            TrackingCarrier carrier,
            String trackingNumber,
            TrackingEntryStatus status,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        return trackingEntryRepository.findAll(
                (Specification<TrackingEntry>) (root, query, cb) -> {
                    List<Predicate> predicates = new ArrayList<>();

                    if (carrier != null) {
                        predicates.add(cb.equal(root.get("carrier"), carrier));
                    }

                    if (StringUtils.hasText(trackingNumber)) {
                        predicates.add(
                                cb.like(
                                        cb.lower(root.get("trackingNumber")),
                                        "%" + trackingNumber.toLowerCase() + "%"
                                )
                        );
                    }

                    if (status != null) {
                        predicates.add(cb.equal(root.get("status"), status));
                    }

                    if (startTime != null) {
                        Predicate p1 = cb.and(
                                cb.isNotNull(root.get("scannedAt")),
                                cb.greaterThanOrEqualTo(root.get("scannedAt"), startTime)
                        );
                        Predicate p2 = cb.and(
                                cb.isNull(root.get("scannedAt")),
                                cb.greaterThanOrEqualTo(root.get("createdAt"), startTime)
                        );
                        predicates.add(cb.or(p1, p2));
                    }

                    if (endTime != null) {
                        Predicate p1 = cb.and(
                                cb.isNotNull(root.get("scannedAt")),
                                cb.lessThanOrEqualTo(root.get("scannedAt"), endTime)
                        );
                        Predicate p2 = cb.and(
                                cb.isNull(root.get("scannedAt")),
                                cb.lessThanOrEqualTo(root.get("createdAt"), endTime)
                        );
                        predicates.add(cb.or(p1, p2));
                    }

                    return cb.and(predicates.toArray(new Predicate[0]));
                },
                pageable
        );
    }

    @Transactional
    public TrackingNumber confirmEntry(Long entryId) {
        TrackingEntry entry = trackingEntryRepository.findById(entryId)
                .orElseThrow(() -> new IllegalArgumentException("Tracking entry not found: " + entryId));

        String numberValue = entry.getTrackingNumber();
        if (!StringUtils.hasText(numberValue)) {
            throw new IllegalArgumentException("Tracking number is empty for entry: " + entryId);
        }

        LocalDateTime now = LocalDateTime.now();

        TrackingNumber trackingNumber = trackingNumberRepository.findByTrackingNumber(numberValue);
        if (trackingNumber == null) {
            trackingNumber = new TrackingNumber();
            trackingNumber.setTrackingNumber(numberValue);

            TrackingCarrier carrier = entry.getCarrier();
            trackingNumber.setCarrierType(
                    carrier != null ? carrier.name() : "UNKNOWN"
            );
            trackingNumber.setStatus("Active");
            trackingNumber.setCreatedAt(now);
        }

        trackingNumber.setUpdatedAt(now);
        TrackingNumber saved = trackingNumberRepository.save(trackingNumber);

        entry.setStatus(TrackingEntryStatus.CONFIRMED);
        trackingEntryRepository.save(entry);

        return saved;
    }

    /**
     * Reject：status=REJECTED
     */
    @Transactional
    public void rejectEntry(Long entryId) {
        TrackingEntry entry = trackingEntryRepository.findById(entryId)
                .orElseThrow(() -> new IllegalArgumentException("Tracking entry not found: " + entryId));

        entry.setStatus(TrackingEntryStatus.REJECTED);
        trackingEntryRepository.save(entry);
    }
}
