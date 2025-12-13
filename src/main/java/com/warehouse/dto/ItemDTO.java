package com.warehouse.dto;

import lombok.Data;

@Data
public class ItemDTO {
    private Long id;
    private String itemId;
    private String itemDescription;
    private double unitOfPrice;
    private String unit;
    private String itemGraph;
}