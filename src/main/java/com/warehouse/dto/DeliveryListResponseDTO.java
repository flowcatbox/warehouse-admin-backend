package com.warehouse.dto;

import com.warehouse.entity.Department;
import lombok.Data;

import java.util.List;

@Data
public class DeliveryListResponseDTO {
    private List<DeliveryListDTO> deliveryList;
    private List<DeliveryItemDTO> deliveryItem;
    private List<ItemDTO> item;
    private List<Department>  department;

}
