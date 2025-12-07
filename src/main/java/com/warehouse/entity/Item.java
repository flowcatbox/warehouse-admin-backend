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
    private String item_id;

    @Column(nullable = false)
    private String item_description;

    @Column(nullable = false)
    private double unit_of_price;

    @Column(nullable = false)
    private String unit;

    @Column(nullable = true)
    private String item_graph;

    @OneToMany(mappedBy = "item")
    @JsonIgnore
    private List<DeliveryItem> deliveries;

}
