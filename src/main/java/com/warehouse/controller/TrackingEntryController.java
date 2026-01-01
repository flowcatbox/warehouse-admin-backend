package com.warehouse.controller;

import com.warehouse.entity.TrackingCarrier;
import com.warehouse.entity.TrackingEntry;
import com.warehouse.entity.TrackingNumber;
import com.warehouse.service.TrackingEntryService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tracking-entries")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TrackingEntryController {

    private final TrackingEntryService trackingEntryService;

    @PostMapping
    public ResponseEntity<TrackingEntry> createEntry(
            @RequestBody TrackingEntryRequest request,
            HttpServletRequest httpRequest
    ) {
        String currentUser = null;

        TrackingCarrier carrier = TrackingCarrier.fromString(request.getCarrier());
        TrackingEntry saved = trackingEntryService.createEntry(
                carrier,
                request.getTrackingNumber(),
                request.getScanSource(),
                request.getScannedAt(),
                currentUser
        );
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> createBatch(
            @RequestBody BatchTrackingEntryRequest batchRequest,
            HttpServletRequest httpRequest
    ) {
        String currentUser = null;

        if (batchRequest.getEntries() == null || batchRequest.getEntries().isEmpty()) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "entries must not be empty");
            return ResponseEntity.badRequest().body(resp);
        }

        List<TrackingEntry> entries = batchRequest.getEntries().stream()
                .map(req -> TrackingEntry.builder()
                        .carrier(TrackingCarrier.fromString(req.getCarrier()))
                        .trackingNumber(req.getTrackingNumber())
                        .scanSource(req.getScanSource())
                        .scannedAt(
                                req.getScannedAt() != null
                                        ? req.getScannedAt()
                                        : LocalDateTime.now()
                        )
                        .createdBy(currentUser)
                        .build())
                .collect(Collectors.toList());

        List<TrackingEntry> saved = trackingEntryService.createBatch(entries);

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("count", saved.size());
        return ResponseEntity.ok(resp);
    }


    @GetMapping
    public ResponseEntity<Map<String, Object>> getTrackingEntries(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String carrier,
            @RequestParam(required = false) String trackingNumber,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startTime,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endTime
    ) {

        Pageable pageable = PageRequest.of(page - 1, size);
        TrackingCarrier carrierEnum = carrier != null && !carrier.isBlank()
                ? TrackingCarrier.fromString(carrier)
                : null;

        Page<TrackingEntry> pageResult = trackingEntryService.search(
                pageable,
                carrierEnum,
                trackingNumber,
                startTime,
                endTime
        );

        Map<String, Object> resp = new HashMap<>();
        resp.put("list", pageResult.getContent());
        resp.put("total", pageResult.getTotalElements());
        resp.put("page", page);
        resp.put("size", size);
        resp.put("totalPages", pageResult.getTotalPages());

        return ResponseEntity.ok(resp);
    }


    @PostMapping("/{id}/confirm")
    public ResponseEntity<?> confirmEntry(@PathVariable Long id) {
        TrackingNumber number = trackingEntryService.confirmEntry(id);
        Map<String,Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("trackingNumberId", number.getId());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectEntry(@PathVariable Long id) {
        trackingEntryService.rejectEntry(id);
        Map<String,Object> resp = new HashMap<>();
        resp.put("success", true);
        return ResponseEntity.ok(resp);
    }


    // ===== DTOs =====

    @Data
    public static class TrackingEntryRequest {

        private String carrier;

        private String trackingNumber;

        private String scanSource;

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime scannedAt;
    }

    @Data
    public static class BatchTrackingEntryRequest {
        private List<TrackingEntryRequest> entries;
    }
}
