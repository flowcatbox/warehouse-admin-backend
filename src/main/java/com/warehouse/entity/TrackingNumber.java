package com.warehouse.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tracking_numbers")
public class TrackingNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tracking_number", nullable = false, unique = true)
    private String trackingNumber;

    @Column(name = "carrier_type", nullable = false)
    private String carrierType;

    /**
     * Status values are free text here (e.g. "Active", "Delivered", "Cancelled").
     * Frontend can decide the allowed values.
     */
    @Column(name = "status")
    private String status;

    @Column(name = "note")
    private String note;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
