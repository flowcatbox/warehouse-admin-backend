package com.warehouse.repository;

import com.warehouse.entity.AuthSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthSessionRepository extends JpaRepository<AuthSession, Long> {

    Optional<AuthSession> findByAccessToken(String accessToken);

    Optional<AuthSession> findByRefreshToken(String refreshToken);

    void deleteByAccessToken(String accessToken);

    void deleteByRefreshToken(String refreshToken);

    void deleteByUserId(Long userId);
}
