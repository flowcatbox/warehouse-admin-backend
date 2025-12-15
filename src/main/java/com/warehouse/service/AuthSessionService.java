package com.warehouse.service;

import com.warehouse.entity.AuthSession;
import com.warehouse.entity.User;
import com.warehouse.repository.AuthSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthSessionService {

    // You can reuse these constants in controller for expiresIn
    public static final long ACCESS_TOKEN_TTL_SECONDS = 2 * 60 * 60;      // 2 hours
    public static final long REMEMBER_ME_TTL_SECONDS = 7 * 24 * 60 * 60;  // 7 days

    private final AuthSessionRepository authSessionRepository;

    public AuthSession createSession(User user, boolean rememberMe) {
        long ttlSeconds = rememberMe ? REMEMBER_ME_TTL_SECONDS : ACCESS_TOKEN_TTL_SECONDS;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusSeconds(ttlSeconds);

        AuthSession session = new AuthSession();
        session.setUserId(user.getId());
        session.setUsername(user.getUsername());
        session.setAccessToken(UUID.randomUUID().toString());
        session.setRefreshToken(UUID.randomUUID().toString());
        session.setRememberMe(rememberMe);
        session.setCreatedAt(now);
        session.setUpdatedAt(now);
        session.setExpiresAt(expiresAt);

        return authSessionRepository.save(session);
    }

    public Optional<AuthSession> findValidByAccessToken(String token) {
        if (!StringUtils.hasText(token)) {
            return Optional.empty();
        }
        Optional<AuthSession> opt = authSessionRepository.findByAccessToken(token);
        if (opt.isEmpty()) {
            return Optional.empty();
        }
        AuthSession session = opt.get();
        if (isExpired(session)) {
            authSessionRepository.delete(session);
            return Optional.empty();
        }
        return Optional.of(session);
    }

    public Optional<AuthSession> findValidByRefreshToken(String token) {
        if (!StringUtils.hasText(token)) {
            return Optional.empty();
        }
        Optional<AuthSession> opt = authSessionRepository.findByRefreshToken(token);
        if (opt.isEmpty()) {
            return Optional.empty();
        }
        AuthSession session = opt.get();
        if (isExpired(session)) {
            authSessionRepository.delete(session);
            return Optional.empty();
        }
        return Optional.of(session);
    }

    public AuthSession refreshSession(AuthSession session) {
        boolean rememberMe = Boolean.TRUE.equals(session.getRememberMe());
        long ttlSeconds = rememberMe ? REMEMBER_ME_TTL_SECONDS : ACCESS_TOKEN_TTL_SECONDS;

        LocalDateTime now = LocalDateTime.now();
        session.setAccessToken(UUID.randomUUID().toString());
        session.setRefreshToken(UUID.randomUUID().toString());
        session.setExpiresAt(now.plusSeconds(ttlSeconds));
        session.setUpdatedAt(now);

        return authSessionRepository.save(session);
    }

    public void deleteSession(AuthSession session) {
        if (session == null || session.getId() == null) {
            return;
        }
        authSessionRepository.deleteById(session.getId());
    }

    private boolean isExpired(AuthSession session) {
        LocalDateTime expiresAt = session.getExpiresAt();
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }
}
