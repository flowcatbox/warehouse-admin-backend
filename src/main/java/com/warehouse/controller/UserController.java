package com.warehouse.controller;

import com.warehouse.entity.User;
import com.warehouse.repository.UserRepository;
import com.warehouse.security.AuthorizationUtils;
import com.warehouse.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getUsers(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String createTimeStart,
            @RequestParam(required = false) String createTimeEnd
    ) {

        AuthorizationUtils.requireRole(request, userRepository, "ADMIN");

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<User> userPage = userService.getUsersWithPagination(
                pageable, username, role, department, status, createTimeStart, createTimeEnd);

        Map<String, Object> response = new HashMap<>();
        response.put("list", userPage.getContent());
        response.put("total", userPage.getTotalElements());
        response.put("page", page);
        response.put("size", size);
        response.put("totalPages", userPage.getTotalPages());

        return ResponseEntity.ok(response);
    }


    @PostMapping
    public ResponseEntity<User> createUser(
            HttpServletRequest request,
            @RequestBody User user
    ) {
        AuthorizationUtils.requireRole(request, userRepository, "ADMIN");

        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }


    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody User user
    ) {
        AuthorizationUtils.requireRole(request, userRepository, "ADMIN");

        User updatedUser = userService.updateUser(id, user);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        }
        return ResponseEntity.notFound().build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            HttpServletRequest request,
            @PathVariable Long id
    ) {
        AuthorizationUtils.requireRole(request, userRepository, "ADMIN");

        if (userService.deleteUser(id)) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User has been deleted");
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping("/batch-delete")
    public ResponseEntity<?> batchDeleteUsers(
            HttpServletRequest request,
            @RequestBody BatchDeleteRequest batchRequest
    ) {
        AuthorizationUtils.requireRole(request, userRepository, "ADMIN");

        int successCount = 0;
        for (Long id : batchRequest.getIds()) {
            if (userService.deleteUser(id)) {
                successCount++;
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Successfully deleted " + successCount + " users");
        response.put("deletedCount", successCount);
        return ResponseEntity.ok(response);
    }

    @Data
    public static class BatchDeleteRequest {
        private List<Long> ids;
    }
}
