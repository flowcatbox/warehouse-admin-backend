package com.warehouse.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DeliveryItemDTO {
    private Long id;
    private ItemDTO item;
    private int quantity;
    private BigDecimal price;
    private String note;
}