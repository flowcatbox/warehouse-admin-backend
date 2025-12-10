package com.warehouse.service;


import com.warehouse.dto.DeliveryListDTO;
import com.warehouse.dto.DeliveryListResponseDTO;
import com.warehouse.entity.DeliveryItem;
import com.warehouse.repository.DeliveryListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryListCombineService {

    private final DeliveryListService deliveryListService;
    private final DeliveryItemService deliveryItemService;
    private final ItemService itemService;
    private final DepartmentService departmentService;

    private final DeliveryListRepository deliveryListRepository;

    public Page<DeliveryListResponseDTO> getDeliveryList(Pageable pageable,
                                                         String department_code,
                                                         String delivery_list_id,
                                                         String item_description,
                                                         String item_id,
                                                         String delivery_list_date_start,
                                                         String delivery_list_date_end
    ){
        return null;
    }
}
