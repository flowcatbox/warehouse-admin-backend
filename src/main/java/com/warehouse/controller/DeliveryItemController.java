package com.warehouse.controller;

import com.warehouse.entity.DeliveryItem;
import com.warehouse.service.DeliveryItemService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/delivery-items")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DeliveryItemController {
    private final DeliveryItemService deliveryItemService;


    @GetMapping("/deliveryId/{deliveryId}")
    public ResponseEntity<?> getDeliveryItemByDeliveryId(@PathVariable Long deliveryId,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page = page > 1? page - 1 : page, size);
        Page<DeliveryItem>  deliveryItemsPage = deliveryItemService.getAllDeliveryItemsByDeliveryId(pageable, deliveryId);

        Map<String, Object> response = new HashMap<>();
        response.put("list", deliveryItemsPage.getContent());
        response.put("total", deliveryItemsPage.getTotalElements());
        response.put("page", page);
        response.put("size", size);
        response.put("totalPages", deliveryItemsPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/itemId/{itemId}")
    public ResponseEntity<?> getDeliveryItemByItemId(@PathVariable Long itemId,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page = page > 1? page - 1 : page, size);
        Page<DeliveryItem>  deliveryItemsPage = deliveryItemService.getAllDeliveryItemByItemId(pageable, itemId);

        Map<String, Object> response = new HashMap<>();
        response.put("list", deliveryItemsPage.getContent());
        response.put("total", deliveryItemsPage.getTotalElements());
        response.put("page", page);
        response.put("size", size);
        response.put("totalPages", deliveryItemsPage.getTotalPages());

        return ResponseEntity.ok(response);
    }
}
