package com.warehouse.controller;

import com.warehouse.dto.DeliveryListResponseDTO;
import com.warehouse.entity.DeliveryList;
import com.warehouse.service.DeliveryListCombineService;
import com.warehouse.service.DeliveryListService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/delivery-lists/combined")
@RequiredArgsConstructor
public class DeliveryListCombineController {

    private final DeliveryListCombineService deliveryListCombineService;

    @GetMapping
    public ResponseEntity<Page<DeliveryListResponseDTO>> getCombined(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, name = "department_code") String departmentCode,
            @RequestParam(required = false) String deliveryListId,
            @RequestParam(required = false) String itemDescription,
            @RequestParam(required = false) String itemId,
            @RequestParam(required = false, name = "deliveryListDateStart") String deliveryListDateStart,
            @RequestParam(required = false, name = "deliveryListDateEnd") String deliveryListDateEnd
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<DeliveryListResponseDTO> result =
                deliveryListCombineService.getDeliveryList(
                        pageable,
                        departmentCode,
                        deliveryListId,
                        itemDescription,
                        itemId,
                        deliveryListDateStart,
                        deliveryListDateEnd
                );
        return ResponseEntity.ok(result);
    }
}
