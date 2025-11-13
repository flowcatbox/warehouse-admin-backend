package com.warehouse.repository;

import com.warehouse.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
    Page<User> findByRole(String role, Pageable pageable);
    Page<User> findByStatus(User.UserStatus status, Pageable pageable);
    Page<User> findByUsernameContainingIgnoreCaseAndRoleAndStatus(
            String username, String role, User.UserStatus status, Pageable pageable);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    Page<User> findAll(Specification<User> userSpecification, Pageable pageable);
}