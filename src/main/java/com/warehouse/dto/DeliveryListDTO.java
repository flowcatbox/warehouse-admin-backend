package com.warehouse.dto;

import lombok.Data;
import java.util.List;

@Data
public class DeliveryListDTO {
    private Long id;
    private String deliveryListId;
    private String departmentId;
    private String deliveryDate;
    private String note;
    private List<DeliveryItemDTO> items;
}