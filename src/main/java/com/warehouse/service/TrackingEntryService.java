package com.warehouse.service;

import com.warehouse.entity.TrackingCarrier;
import com.warehouse.entity.TrackingEntry;
import com.warehouse.entity.TrackingNumber;
import com.warehouse.repository.TrackingEntryRepository;
import com.warehouse.repository.TrackingNumberRepository;
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
        return trackingEntryRepository.search(
                carrier,
                trackingNumber,
                startTime,
                endTime,
                pageable
        );
    }

    @Transactional
    public TrackingNumber confirmEntry(Long entryId) {
        TrackingEntry entry = trackingEntryRepository.findById(entryId)
                .orElseThrow(() -> new IllegalArgumentException("Tracking entry not found: " + entryId));

        String numberValue = entry.getTrackingNumber();
        if (numberValue == null || numberValue.trim().isEmpty()) {
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

        return saved;
    }

    @Transactional
    public void rejectEntry(Long entryId) {
        if (trackingEntryRepository.existsById(entryId)) {
            trackingEntryRepository.deleteById(entryId);
        }
    }
}
