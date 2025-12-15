package com.warehouse.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.warehouse.entity.AuthSession;
import com.warehouse.service.AuthSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthSessionService authSessionService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    // Paths that do NOT require auth
    private static final String[] EXCLUDE_PATHS = new String[]{
            "/auth/login",
            "/auth/refresh",
            "/error",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/h2-console/**",
            "/public/**"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if("OPTIONS".equalsIgnoreCase(request.getMethod())){
            return true;
        }

        String path = request.getServletPath();

        // Allow excluded paths
        for (String pattern : EXCLUDE_PATHS) {
            if (PATH_MATCHER.match(pattern, path)) {
                return true;
            }
        }

        String authHeader = request.getHeader("Authorization");
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            writeUnauthorized(response, "Missing Authorization header");
            return false;
        }

        String token = authHeader.substring(7);
        Optional<AuthSession> sessionOpt = authSessionService.findValidByAccessToken(token);
        if (sessionOpt.isEmpty()) {
            writeUnauthorized(response, "Invalid or expired token");
            return false;
        }

        // Optionally put current user info into request attributes for controllers
        AuthSession session = sessionOpt.get();
        request.setAttribute("currentUserId", session.getUserId());
        request.setAttribute("currentUsername", session.getUsername());

        return true;
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        Map<String, Object> body = Map.of(
                "success", false,
                "message", message
        );
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
