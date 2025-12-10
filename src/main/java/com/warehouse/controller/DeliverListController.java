package com.warehouse.controller;

import com.warehouse.entity.DeliveryList;
import com.warehouse.service.DeliveryListService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/deliverylist")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DeliverListController {
    private final DeliveryListService deliveryListService;

    @GetMapping
    public ResponseEntity<?> getDeliverylist(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String deliveryListId,
            @RequestParam(required = false) String departmentId,
            @RequestParam(required = false) String deliveryDateStart,
            @RequestParam(required = false) String deliveryDateEnd
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<DeliveryList> deliveryListPage = deliveryListService.getDeliveryListsPagination(
                pageable,
                deliveryListId,
                deliveryDateStart,
                deliveryDateEnd,
                departmentId
        );

        Map<String, Object> response = new HashMap<>();
        response.put("list", deliveryListPage.getContent());
        response.put("total", deliveryListPage.getTotalElements());
        response.put("page", page);
        response.put("size", size);
        response.put("totalPages", deliveryListPage.getTotalPages());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<DeliveryList> createDeliveryList(@RequestBody DeliveryList deliveryListInfo) {
        DeliveryList deliveryList = deliveryListService.createDeliveryList(deliveryListInfo);
        return ResponseEntity.ok(deliveryList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeliveryList> updateDeliveryList(@PathVariable Long id, @RequestBody DeliveryList DeliveryListInfo) {
        DeliveryList deliveryList = deliveryListService.updateDeliveryList(id, DeliveryListInfo);
        if (deliveryList != null) {
            return ResponseEntity.ok(deliveryList);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDeliveryList(@PathVariable Long id) {
        if (deliveryListService.deleteDeliveryList(id)) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Delivery list has been deleted");
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Delivery list hasn't been deleted");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}