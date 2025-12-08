package com.warehouse.service;


import com.warehouse.entity.DeliveryItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryListCombineService {

    private final DeliveryListService deliveryListService;
    private final DeliveryItemService deliveryItemService;
    private final ItemService itemService;
    private final DepartmentService departmentService;


}
