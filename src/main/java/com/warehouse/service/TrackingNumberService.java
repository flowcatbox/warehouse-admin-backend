package com.warehouse.service;

import com.warehouse.entity.TrackingNumber;
import com.warehouse.repository.TrackingNumberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TrackingNumberService {

    private final TrackingNumberRepository trackingNumberRepository;

    /**
     * Paginated search with conditions:
     *  - trackingNumber: fuzzy match
     *  - carrierType: exact match or IN when multiple values separated by comma
     *  - status: case-insensitive exact match
     *  - createdAt range: [startDate 00:00:00, endDate 23:59:59]
     */
    public Page<TrackingNumber> getTrackingNumbersWithPagination(
            Pageable pageable,
            String trackingNumber,
            String carrierType,
            String status,
            String createdDateStart,
            String createdDateEnd
    ) {
        return trackingNumberRepository.findAll(
                (Specification<TrackingNumber>) (root, query, criteriaBuilder) -> {
                    List<Predicate> predicates = new ArrayList<>();

                    // trackingNumber LIKE
                    if (StringUtils.hasText(trackingNumber)) {
                        predicates.add(
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(root.get("trackingNumber")),
                                        "%" + trackingNumber.toLowerCase() + "%"
                                )
                        );
                    }

                    // carrierType = ... OR IN (...)
                    if (StringUtils.hasText(carrierType)) {
                        if (carrierType.contains(",")) {
                            String[] types = carrierType.split(",");
                            CriteriaBuilder.In<String> inClause =
                                    criteriaBuilder.in(criteriaBuilder.lower(root.get("carrierType")));
                            for (String type : types) {
                                String trimmed = type.trim();
                                if (!trimmed.isEmpty()) {
                                    inClause.value(trimmed.toLowerCase());
                                }
                            }
                            predicates.add(inClause);
                        } else {
                            predicates.add(
                                    criteriaBuilder.equal(
                                            criteriaBuilder.lower(root.get("carrierType")),
                                            carrierType.toLowerCase()
                                    )
                            );
                        }
                    }

                    // status (case-insensitive)
                    if (StringUtils.hasText(status)) {
                        predicates.add(
                                criteriaBuilder.equal(
                                        criteriaBuilder.lower(root.get("status")),
                                        status.toLowerCase()
                                )
                        );
                    }

                    // createdAt >= start
                    if (StringUtils.hasText(createdDateStart)) {
                        LocalDateTime start = LocalDateTime.parse(createdDateStart + "T00:00:00");
                        predicates.add(
                                criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), start)
                        );
                    }

                    // createdAt <= end
                    if (StringUtils.hasText(createdDateEnd)) {
                        LocalDateTime end = LocalDateTime.parse(createdDateEnd + "T23:59:59");
                        predicates.add(
                                criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), end)
                        );
                    }

                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                },
                pageable
        );
    }

    public Optional<TrackingNumber> getTrackingNumberById(Long id) {
        return trackingNumberRepository.findById(id);
    }

    public TrackingNumber createTrackingNumber(TrackingNumber trackingNumber) {
        LocalDateTime now = LocalDateTime.now();
        if (trackingNumber.getCreatedAt() == null) {
            trackingNumber.setCreatedAt(now);
        }
        trackingNumber.setUpdatedAt(now);
        return trackingNumberRepository.save(trackingNumber);
    }

    public TrackingNumber updateTrackingNumber(Long id, TrackingNumber trackingNumberInfo) {
        return trackingNumberRepository.findById(id)
                .map(existing -> {
                    existing.setTrackingNumber(trackingNumberInfo.getTrackingNumber());
                    existing.setCarrierType(trackingNumberInfo.getCarrierType());
                    existing.setStatus(trackingNumberInfo.getStatus());
                    existing.setNote(trackingNumberInfo.getNote());

                    // Allow overriding createdAt if provided, otherwise keep original
                    if (trackingNumberInfo.getCreatedAt() != null) {
                        existing.setCreatedAt(trackingNumberInfo.getCreatedAt());
                    }

                    existing.setUpdatedAt(LocalDateTime.now());
                    return trackingNumberRepository.save(existing);
                })
                .orElse(null);
    }

    public boolean deleteTrackingNumber(Long id) {
        if (trackingNumberRepository.findById(id).isPresent()) {
            trackingNumberRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
