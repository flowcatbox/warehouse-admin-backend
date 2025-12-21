package com.warehouse.controller;

import com.warehouse.entity.AuthSession;
import com.warehouse.entity.User;
import com.warehouse.repository.UserRepository;
import com.warehouse.service.AuthSessionService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthSessionService authSessionService;

    /**
     * Login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (!StringUtils.hasText(request.getUsername()) || !StringUtils.hasText(request.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Username and password must not be empty");
        }

        User user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }

        // bcrypt + legacy plain-text compatibility
        String storedPassword = user.getPassword();
        String rawPassword = request.getPassword();

        boolean passwordMatches;
        if (isBcryptHash(storedPassword)) {
            passwordMatches = passwordEncoder.matches(rawPassword, storedPassword);
        } else {
            passwordMatches = Objects.equals(storedPassword, rawPassword);
            if (passwordMatches) {
                // upgrade to bcrypt
                String encoded = passwordEncoder.encode(rawPassword);
                user.setPassword(encoded);
                userRepository.save(user);
            }
        }

        if (!passwordMatches) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }

        // check user status if you have it (optional)
        if (user.getStatus() == User.UserStatus.INACTIVE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("User is inactive");
        }

        // create session in DB
        AuthSession session = authSessionService.createSession(user, request.isRememberMe());
        long ttlSeconds = request.isRememberMe()
                ? AuthSessionService.REMEMBER_ME_TTL_SECONDS
                : AuthSessionService.ACCESS_TOKEN_TTL_SECONDS;

        LoginUserInfo userInfo = LoginUserInfo.fromUser(user);

        LoginResponse response = new LoginResponse();
        response.setAccessToken(session.getAccessToken());
        response.setRefreshToken(session.getRefreshToken());
        response.setTokenType("Bearer");
        response.setExpiresIn(ttlSeconds);
        response.setUser(userInfo);

        return ResponseEntity.ok(response);
    }

    /**
     * Refresh token
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshRequest request) {
        if (!StringUtils.hasText(request.getRefreshToken())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("refreshToken is required");
        }

        Optional<AuthSession> sessionOpt = authSessionService.findValidByRefreshToken(request.getRefreshToken());
        if (sessionOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired refresh token");
        }

        AuthSession session = authSessionService.refreshSession(sessionOpt.get());
        long ttlSeconds = Boolean.TRUE.equals(session.getRememberMe())
                ? AuthSessionService.REMEMBER_ME_TTL_SECONDS
                : AuthSessionService.ACCESS_TOKEN_TTL_SECONDS;

        RefreshResponse response = new RefreshResponse();
        response.setAccessToken(session.getAccessToken());
        response.setRefreshToken(session.getRefreshToken());

        return ResponseEntity.ok(response);
    }

    /**
     * Get current user by access token.
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        String token = extractTokenFromHeader(authorizationHeader);
        if (!StringUtils.hasText(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized");
        }

        Optional<AuthSession> sessionOpt = authSessionService.findValidByAccessToken(token);
        if (sessionOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized");
        }

        AuthSession session = sessionOpt.get();
        Optional<User> userOpt = userRepository.findById(session.getUserId());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User not found");
        }

        LoginUserInfo userInfo = LoginUserInfo.fromUser(userOpt.get());
        return ResponseEntity.ok(userInfo);
    }

    /**
     * Logout: delete current session
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        String token = extractTokenFromHeader(authorizationHeader);
        if (StringUtils.hasText(token)) {
            authSessionService.findValidByAccessToken(token)
                    .ifPresent(authSessionService::deleteSession);
        }

        return ResponseEntity.ok("Logged out");
    }

    /**
     * Change password
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
        @RequestBody ChangePasswordRequest request,
        javax.servlet.http.HttpServletRequest httpRequest
    ){
        Object userIdAttr = httpRequest.getAttribute("currentUserId");
        if(userIdAttr == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized");
        }

        Long userId;
        try{
            userId = (userIdAttr instanceof Long)
                    ? (Long) userIdAttr
                    : Long.valueOf(userIdAttr.toString());
        }catch (NumberFormatException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized");
        }

        if(!StringUtils.hasText(request.getOldPassword())
            || !StringUtils.hasText(request.getNewPassword())
        ){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Old password and new password must not be empty.");
        }


        Optional<User> userOpt = userRepository.findById(userId);
        if(userOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User not found");
        }

        User user = userOpt.get();
        String storedPassword = user.getPassword();
        String rawOldPassword = request.getOldPassword();

        boolean oldPasswordMatches;
        if (isBcryptHash(storedPassword)) {
            oldPasswordMatches = passwordEncoder.matches(rawOldPassword, storedPassword);
        } else {
            oldPasswordMatches = Objects.equals(storedPassword, rawOldPassword);
        }

        if (!oldPasswordMatches) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Old password is incorrect");
        }

        String encodedNew = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedNew);
        // user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        //remove all token session
        authSessionService.deleteAllSessionsForUser(user.getId());

        //
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Password has been changed successfully.");

        return ResponseEntity.ok(response);
    }
    // =============== helper methods ===============

    private String extractTokenFromHeader(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }
        return authorizationHeader.substring(7);
    }

    private boolean isBcryptHash(String value) {
        if (!StringUtils.hasText(value)) return false;
        // Typical bcrypt prefixes: $2a$, $2b$, $2y$
        return value.startsWith("$2a$") || value.startsWith("$2b$") || value.startsWith("$2y$");
    }

    // =============== DTO classes  ===============

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
        private boolean rememberMe;
    }

    @Data
    public static class LoginUserInfo {
        private String id;
        private String username;
        private String role;
        private String email;
        private String avatar;
        private String[] permissions;

        public static LoginUserInfo fromUser(User user) {
            LoginUserInfo info = new LoginUserInfo();
            info.setId(user.getId() != null ? user.getId().toString() : null);
            info.setUsername(user.getUsername());
            info.setRole(user.getRole());
            info.setEmail(user.getEmail());
            info.setAvatar(user.getAvatar());

            String role = user.getRole() != null ? user.getRole().toUpperCase() : "";
            String[] perms;
            switch (role) {
                case "ADMIN":
                    perms = new String[]{
                            "dashboard:view",
                            "item:view", "item:manage",
                            "delivery:view", "delivery:manage",
                            "tracking:view", "tracking:manage",
                            "user:manage"
                    };
                    break;
                case "EDITOR":
                    perms = new String[]{
                            "dashboard:view",
                            "item:view", "item:manage",
                            "delivery:view", "delivery:manage",
                            "tracking:view"
                    };
                    break;
                default:
                    perms = new String[]{
                            "dashboard:view",
                            "item:view",
                            "delivery:view",
                            "tracking:view"
                    };
            }
            info.setPermissions(perms);
            return info;
        }

    }

    @Data
    public static class LoginResponse {
        private String accessToken;
        private String refreshToken;
        private String tokenType;
        private long expiresIn;
        private LoginUserInfo user;
    }

    @Data
    public static class RefreshRequest {
        private String refreshToken;
    }

    @Data
    public static class RefreshResponse {
        private String accessToken;
        private String refreshToken;
    }

    @Data
    public static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;
    }
}
