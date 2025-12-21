package com.warehouse.security;

import com.warehouse.entity.User;
import com.warehouse.repository.UserRepository;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

@UtilityClass
public class AuthorizationUtils {

    public static User requireLogin(HttpServletRequest request, UserRepository userRepository) {
        Object userIdAttr = request.getAttribute("currentUserId");
        if (userIdAttr == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        Long userId;
        try {
            if (userIdAttr instanceof Long) {
                userId = (Long) userIdAttr;
            } else {
                userId = Long.valueOf(userIdAttr.toString());
            }
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }
        return userOpt.get();
    }

    public static User requireRole(HttpServletRequest request,
                                   UserRepository userRepository,
                                   String... roles) {
        User user = requireLogin(request, userRepository);
        String userRole = user.getRole() != null ? user.getRole().toUpperCase(Locale.ROOT) : "";
        boolean allowed = Arrays.stream(roles)
                .filter(r -> r != null)
                .map(r -> r.toUpperCase(Locale.ROOT))
                .anyMatch(r -> r.equals(userRole));

        if (!allowed) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }
        return user;
    }
}
