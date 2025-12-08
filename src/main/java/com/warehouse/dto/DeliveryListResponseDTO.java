package com.warehouse.dto;

import com.warehouse.entity.Department;
import lombok.Data;

import java.util.List;

@Data
public class DeliveryListResponseDTO {
    private List<DeliveryListDTO> delivery_list;
    private List<DeliveryItemDTO> delivery_item;
    private List<ItemDTO> item;
    private List<Department>  department;

}
