package com.warehouse.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "Department")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String departmentCode;

    @Column(nullable = false)
    private String departmentName;

}
