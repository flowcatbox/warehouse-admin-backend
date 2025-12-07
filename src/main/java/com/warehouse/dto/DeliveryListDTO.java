package com.warehouse.dto;

import lombok.Data;
import java.util.List;

@Data
public class DeliveryListDTO {
    private Long id;
    private String delivery_list_id;
    private String department_id;
    private String delivery_date;
    private String note;
    private List<DeliveryItemDTO> items;
}