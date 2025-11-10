package com.warehouse.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "linen_items")
public class LinenItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_id", unique = true)
    private String itemId;

    private String description;

    @Column(name = "on_hand")
    private Integer onHand;

    @Column(name = "min_stock")
    private Integer minStock;

    @Column(name = "max_stock")
    private Integer maxStock;

    private String category;
    private String location;

    @Enumerated(EnumType.STRING)
    private LinenStatus status;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum LinenStatus {
        ACTIVE, INACTIVE, LOW_STOCK
    }
}