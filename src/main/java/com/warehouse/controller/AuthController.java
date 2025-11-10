package com.warehouse.controller;

import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // 简单模拟认证
        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", "mock-jwt-token-" + System.currentTimeMillis());
        response.put("refreshToken", "mock-refresh-token-" + System.currentTimeMillis());

        Map<String, Object> user = new HashMap<>();
        user.put("id", "1");
        user.put("username", request.getUsername());
        user.put("role", "admin");
        user.put("email", request.getUsername() + "@example.com");
        user.put("permissions", new String[]{"user:add", "user:edit", "user:delete", "linen:manage"});

        response.put("user", user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", "new-mock-jwt-token-" + System.currentTimeMillis());
        response.put("refreshToken", "new-mock-refresh-token-" + System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Map<String, Object> user = new HashMap<>();
        user.put("id", "1");
        user.put("username", "admin");
        user.put("role", "admin");
        user.put("email", "admin@example.com");
        user.put("permissions", new String[]{"user:add", "user:edit", "user:delete", "linen:manage"});
        return ResponseEntity.ok(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok().build();
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
        private Boolean rememberMe;
    }

    @Data
    public static class RefreshRequest {
        private String refreshToken;
    }
}