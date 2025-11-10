package com.warehouse.controller;

import com.warehouse.entity.User;
import com.warehouse.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status) {

        // 模拟分页响应
        Map<String, Object> response = new HashMap<>();
        List<User> users = userService.getAllUsers();

        response.put("list", users);
        response.put("total", users.size());
        response.put("page", page);
        response.put("size", size);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (userService.deleteUser(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/batch-delete")
    public ResponseEntity<?> batchDeleteUsers(@RequestBody BatchDeleteRequest request) {
        for (Long id : request.getIds()) {
            userService.deleteUser(id);
        }
        return ResponseEntity.ok().build();
    }

    @Data
    public static class BatchDeleteRequest {
        private List<Long> ids;
    }
}