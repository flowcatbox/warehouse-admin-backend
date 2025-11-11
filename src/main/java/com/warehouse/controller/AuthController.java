package com.warehouse.controller;

import com.warehouse.entity.User;
import com.warehouse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // 1. Search user by username
        User user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }

        // 2. Validate password
        if (!Objects.equals(user.getPassword(), request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }

        // 3. Make userinfo which has to be matched with the front-end
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId().toString());
        userInfo.put("username", user.getUsername());
        userInfo.put("role", user.getRole());
        userInfo.put("email", user.getEmail());

        // user.permissions
        String[] permissions;
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            permissions = new String[]{"user:add", "user:edit", "user:delete", "linen:manage"};
        } else {
            permissions = new String[]{"linen:view"};
        }
        userInfo.put("permissions", permissions);

        // 4. Return token
        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", UUID.randomUUID().toString());
        response.put("refreshToken", UUID.randomUUID().toString());
        response.put("tokenType", "Bearer");
        response.put("expiresIn", 3600);
        response.put("user", userInfo);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", UUID.randomUUID().toString());
        response.put("refreshToken", UUID.randomUUID().toString());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        // Retrieve the admin
        User user = userRepository.findByUsername("admin");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User not found");
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId().toString());
        userInfo.put("username", user.getUsername());
        userInfo.put("role", user.getRole());
        userInfo.put("email", user.getEmail());

        String[] permissions;
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            permissions = new String[]{"user:add", "user:edit", "user:delete", "linen:manage"};
        } else {
            permissions = new String[]{"linen:view"};
        }
        userInfo.put("permissions", permissions);

        return ResponseEntity.ok(userInfo);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // 目前只是前端清 token，这里直接 200 就行
        return ResponseEntity.ok().build();
    }

    // ====== 请求体 DTO（保持你之前的内部类写法）======

    public static class LoginRequest {
        private String username;
        private String password;
        private boolean rememberMe;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public boolean isRememberMe() {
            return rememberMe;
        }

        public void setRememberMe(boolean rememberMe) {
            this.rememberMe = rememberMe;
        }
    }

    public static class RefreshRequest {
        private String refreshToken;

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }
}
