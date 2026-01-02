package com.warehouse.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import com.warehouse.entity.TrackingEntryStatus;

@Entity
@Table(name = "tracking_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackingEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private TrackingCarrier carrier;

    @Column(name = "tracking_number", nullable = false, length = 128)
    private String trackingNumber;

    @Column(name = "scan_source", length = 50)
    private String scanSource;

    @Column(name = "scanned_at")
    private LocalDateTime scannedAt;

    @Column(name = "created_by", length = 64)
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name="status", length = 20)
    private TrackingEntryStatus status;

    @Column(name="tracking_number_id")
    private Long trackingNumberId;
}
