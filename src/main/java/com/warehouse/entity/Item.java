package com.warehouse.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String itemId;

    @Column(nullable = false)
    private String itemDescription;

    @Column(nullable = false)
    private double unitOfPrice;

    @Column(nullable = false)
    private String unit;

    @Column(nullable = true)
    private String itemGraph;

    @OneToMany(mappedBy = "item")
    @JsonIgnore
    private List<DeliveryItem> deliveries;

}
