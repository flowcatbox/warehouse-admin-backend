package com.warehouse.controller;

import com.warehouse.entity.Item;
import com.warehouse.service.ItemService;
import com.warehouse.service.UserService;
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
@RequestMapping("/items")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<?> getAllItems(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, name = "item_description") String desc,
            @RequestParam(required = false, name = "item_id") String itemId) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Item> itemsPage = itemService.getItemsWithPagination(pageable, desc, itemId);

        Map<String, Object> response = new HashMap<>();
        response.put("list", itemsPage.getContent());
        response.put("total", itemsPage.getTotalElements());
        response.put("page", page);
        response.put("size", size);
        response.put("totalPages", itemsPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Item> createItem(@RequestBody Item item) {
        Item createdItem = itemService.createItem(item);
        return ResponseEntity.ok(createdItem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable long id, @RequestBody Item item) {
        Item updatedItem = itemService.updateItem(id, item);
        if(updatedItem != null) {
            return ResponseEntity.ok(updatedItem);
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable long id) {
        if(itemService.deleteItem(id)){
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Item deleted");
            return ResponseEntity.ok(response);
        }else{
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Item not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
