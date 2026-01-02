package com.warehouse.controller;

import com.warehouse.entity.TrackingCarrier;
import com.warehouse.entity.TrackingEntry;
import com.warehouse.entity.TrackingEntryStatus;
import com.warehouse.entity.TrackingNumber;
import com.warehouse.service.TrackingEntryService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/tracking-entries")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class TrackingEntryController {

    private final TrackingEntryService trackingEntryService;

    @Data
    public static class CreateTrackingEntryRequest {
        private String carrier;
        private String trackingNumber;
        private String scanSource;
        private String scannedAt;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createEntry(
            @RequestBody CreateTrackingEntryRequest req,
            HttpServletRequest request
    ) {
        if (!StringUtils.hasText(req.getCarrier())) {
            throw new IllegalArgumentException("Carrier is required.");
        }
        if (!StringUtils.hasText(req.getTrackingNumber())) {
            throw new IllegalArgumentException("Tracking number is required.");
        }

        TrackingCarrier carrierEnum = TrackingCarrier.fromString(req.getCarrier());

        LocalDateTime scannedTime = null;
        if (StringUtils.hasText(req.getScannedAt())) {
            try {
                Instant instant = Instant.parse(req.getScannedAt());
                scannedTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            } catch (DateTimeParseException ex) {
                try {
                    scannedTime = LocalDateTime.parse(req.getScannedAt());
                } catch (DateTimeParseException ignore) {
                    scannedTime = null;
                }
            }
        }

        String currentUsername = (String) request.getAttribute("currentUsername");
        if (!StringUtils.hasText(currentUsername)) {
            currentUsername = "system";
        }

        TrackingEntry entry = trackingEntryService.createEntry(
                carrierEnum,
                req.getTrackingNumber().trim(),
                req.getScanSource(),
                scannedTime,
                currentUsername
        );

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("data", entry);
        return ResponseEntity.ok(resp);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getTrackingEntries(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String carrier,
            @RequestParam(required = false) String trackingNumber,
            @RequestParam(required = false) String status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startTime,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endTime
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);

        TrackingCarrier carrierEnum = null;
        if (StringUtils.hasText(carrier)) {
            carrierEnum = TrackingCarrier.fromString(carrier);
        }

        TrackingEntryStatus statusEnum = null;
        if (StringUtils.hasText(status)) {
            try {
                statusEnum = TrackingEntryStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ignore) {
                statusEnum = null;
            }
        }

        Page<TrackingEntry> entryPage = trackingEntryService.getTrackingEntriesWithPagination(
                pageable,
                carrierEnum,
                trackingNumber,
                statusEnum,
                startTime,
                endTime
        );

        Map<String, Object> resp = new HashMap<>();
        resp.put("list", entryPage.getContent());
        resp.put("total", entryPage.getTotalElements());
        resp.put("page", page);
        resp.put("size", size);
        resp.put("totalPages", entryPage.getTotalPages());

        return ResponseEntity.ok(resp);
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Map<String, Object>> confirmEntry(@PathVariable Long id) {
        TrackingNumber number = trackingEntryService.confirmEntry(id);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("trackingNumberId", number.getId());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Map<String, Object>> rejectEntry(@PathVariable Long id) {
        trackingEntryService.rejectEntry(id);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        return ResponseEntity.ok(resp);
    }
}
