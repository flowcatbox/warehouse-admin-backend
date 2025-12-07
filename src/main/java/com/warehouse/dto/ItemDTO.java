package com.warehouse.dto;

import lombok.Data;

@Data
public class ItemDTO {
    private Long id;
    private String item_id;
    private String item_description;
    private double unit_of_price;
    private String unit;
    private String item_graph;
}