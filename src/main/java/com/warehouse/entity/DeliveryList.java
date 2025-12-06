package com.warehouse.entity;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


@Data
@Entity
@Table(name = "deliverylist")
public class DeliveryList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "delivery_list_id", nullable = false, unique = true)
    private String delivery_list_id;

    @Column(name = "department_id")
    private String department_id;

    @Column(name = "delivery_date")
    private String delivery_date;

    @Column(name = "note", nullable = true)
    private String note;

    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL)
    private List<DeliveryItem> items;

}
