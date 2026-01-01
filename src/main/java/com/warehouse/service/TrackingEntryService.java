package com.warehouse.service;

import com.warehouse.entity.TrackingCarrier;
import com.warehouse.entity.TrackingEntry;
import com.warehouse.repository.TrackingEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrackingEntryService {

    private final TrackingEntryRepository trackingEntryRepository;

    @Transactional
    public TrackingEntry createEntry(
            TrackingCarrier carrier,
            String trackingNumber,
            String scanSource,
            LocalDateTime scannedAt,
            String createdBy
    ) {
        LocalDateTime now = LocalDateTime.now();

        TrackingEntry entry = TrackingEntry.builder()
                .carrier(carrier)
                .trackingNumber(trackingNumber)
                .scanSource(scanSource)
                .scannedAt(scannedAt != null ? scannedAt : now)
                .createdBy(createdBy)
                .build();

        return trackingEntryRepository.save(entry);
    }

    @Transactional
    public List<TrackingEntry> createBatch(List<TrackingEntry> entries) {
        return trackingEntryRepository.saveAll(entries);
    }

    @Transactional(readOnly = true)
    public Page<TrackingEntry> search(
            Pageable pageable,
            TrackingCarrier carrier,
            String trackingNumber,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        return trackingEntryRepository.search(carrier, trackingNumber, startTime, endTime, pageable);
    }
}
