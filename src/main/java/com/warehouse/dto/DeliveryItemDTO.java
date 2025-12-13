package com.warehouse.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DeliveryItemDTO {
    private Long id;
    private String itemID;
    private String deliveryListId;
    private int quantity;
    private BigDecimal price;
    private String note;
}