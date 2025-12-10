package com.warehouse.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    private String deliveryListId;

    @Column(name = "department_id")
    private String departmentId;

    @Column(name = "delivery_date")
    private String deliveryDate;

    @Column(name = "note", nullable = true)
    private String note;

    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL)
    private List<DeliveryItem> items;

}
