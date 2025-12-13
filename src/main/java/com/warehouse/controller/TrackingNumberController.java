package com.warehouse.controller;

import com.warehouse.entity.TrackingNumber;
import com.warehouse.service.TrackingNumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/tracking-numbers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TrackingNumberController {

    private final TrackingNumberService trackingNumberService;

    @GetMapping
    public ResponseEntity<?> getTrackingNumbers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String trackingNumber,
            @RequestParam(required = false) String carrierType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, name = "startDate") String startDate,
            @RequestParam(required = false, name = "endDate") String endDate
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<TrackingNumber> trackingPage = trackingNumberService.getTrackingNumbersWithPagination(
                pageable,
                trackingNumber,
                carrierType,
                status,
                startDate,
                endDate
        );

        Map<String, Object> response = new HashMap<>();
        response.put("list", trackingPage.getContent());
        response.put("total", trackingPage.getTotalElements());
        response.put("page", page);
        response.put("size", size);
        response.put("totalPages", trackingPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTrackingNumberById(@PathVariable Long id) {
        Optional<TrackingNumber> trackingNumberOpt = trackingNumberService.getTrackingNumberById(id);
        return trackingNumberOpt
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Tracking number not found"));
    }

    @PostMapping
    public ResponseEntity<TrackingNumber> createTrackingNumber(
            @RequestBody TrackingNumber trackingNumber
    ) {
        TrackingNumber created = trackingNumberService.createTrackingNumber(trackingNumber);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTrackingNumber(
            @PathVariable Long id,
            @RequestBody TrackingNumber trackingNumber
    ) {
        TrackingNumber updated = trackingNumberService.updateTrackingNumber(id, trackingNumber);
        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Tracking number not found");
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTrackingNumber(@PathVariable Long id) {
        if (trackingNumberService.deleteTrackingNumber(id)) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tracking number has been deleted");
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Tracking number has not been deleted");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
