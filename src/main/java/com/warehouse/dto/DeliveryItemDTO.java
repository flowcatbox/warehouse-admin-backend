package com.warehouse.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DeliveryItemDTO {
    private Long id;
    private String item_iD;
    private String delivery_list_id;
    private int quantity;
    private BigDecimal price;
    private String note;
}