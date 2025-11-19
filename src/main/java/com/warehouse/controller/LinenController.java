package com.warehouse.controller;

import com.warehouse.entity.LinenItem;
import com.warehouse.repository.LinenRepository;
import com.warehouse.service.LinenService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/linen")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LinenController {
    private final LinenRepository linenRepository;
    private final LinenService linenService;

    @GetMapping
    public ResponseEntity<?> getLinenItems(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String itemId,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String status) {

        List<LinenItem> items = linenRepository.findAll();

        Map<String, Object> response = new HashMap<>();
        response.put("list", items);
        response.put("total", items.size());
        response.put("page", page);
        response.put("size", size);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<LinenItem> createLinen(@RequestBody LinenItem item) {
        LinenItem linenItem = linenService.createLinen(item);
        return ResponseEntity.ok(linenItem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LinenItem> updateLinen(@PathVariable Long id, @RequestBody LinenItem itemDetails) {
        return linenRepository.findById(id)
                .map(item -> {
                    if (itemDetails.getDescription() != null)
                        item.setDescription(itemDetails.getDescription());
                    if (itemDetails.getOnHand() != null)
                        item.setOnHand(itemDetails.getOnHand());
                    if (itemDetails.getMinStock() != null)
                        item.setMinStock(itemDetails.getMinStock());
                    if (itemDetails.getMaxStock() != null)
                        item.setMaxStock(itemDetails.getMaxStock());
                    if (itemDetails.getCategory() != null)
                        item.setCategory(itemDetails.getCategory());
                    if (itemDetails.getLocation() != null)
                        item.setLocation(itemDetails.getLocation());
                    if (itemDetails.getStatus() != null)
                        item.setStatus(itemDetails.getStatus());

                    item.setLastUpdated(LocalDateTime.now());
                    return ResponseEntity.ok(linenRepository.save(item));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLinen(@PathVariable Long id) {
        if(linenService.deleteLinen(id)) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Linen item has been deleted");
            return ResponseEntity.ok(response);
        }else{
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Linen item hasn't been deleted");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping("/{id}/inbound")
    public ResponseEntity<?> inbound(@PathVariable Long id, @RequestBody OperationRequest request) {
        return linenRepository.findById(id)
                .map(item -> {
                    item.setOnHand(item.getOnHand() + request.getQuantity());
                    item.setLastUpdated(LocalDateTime.now());
                    LinenItem saved = linenRepository.save(item);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/outbound")
    public ResponseEntity<?> outbound(@PathVariable Long id, @RequestBody OperationRequest request) {
        return linenRepository.findById(id)
                .map(item -> {
                    int newQuantity = item.getOnHand() - request.getQuantity();
                    if (newQuantity >= 0) {
                        item.setOnHand(newQuantity);
                        item.setLastUpdated(LocalDateTime.now());
                        LinenItem saved = linenRepository.save(item);
                        return ResponseEntity.ok(saved);
                    } else {
                        // 返回错误信息而不是 LinenItem
                        Map<String, String> errorResponse = new HashMap<>();
                        errorResponse.put("error", "Insufficient stock");
                        errorResponse.put("message", "Cannot outbound more than current stock");
                        return ResponseEntity.badRequest().body(errorResponse);
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Data
    public static class OperationRequest {
        private Integer quantity;
        private String notes;
    }
}