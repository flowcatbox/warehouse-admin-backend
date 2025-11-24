package com.warehouse.service;

import com.warehouse.entity.User;
import com.warehouse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Page<User> getUsersWithPagination(Pageable pageable,
                                             String username,
                                             String role,
                                             String department,
                                             String status,
                                             String createTimeStart,
                                             String createTimeEnd) {

        return userRepository.findAll((Specification<User>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(username)) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("username")),
                        "%" + username.toLowerCase() + "%"
                ));
            }

            if (StringUtils.hasText(role)) {
                predicates.add(criteriaBuilder.equal(root.get("role"), role));
            }

            if (StringUtils.hasText(department)) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("Department")),
                        "%" + department.toLowerCase() + "%"
                ));
            }

            if (StringUtils.hasText(status)) {
                User.UserStatus userStatus = User.UserStatus.valueOf(status.toUpperCase());
                predicates.add(criteriaBuilder.equal(root.get("status"), userStatus));
            }

            if (StringUtils.hasText(createTimeStart)) {
                LocalDateTime start = LocalDateTime.parse(createTimeStart + "T00:00:00");
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), start));
            }

            if (StringUtils.hasText(createTimeEnd)) {
                LocalDateTime end = LocalDateTime.parse(createTimeEnd + "T23:59:59");
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createTime"), end));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User createUser(User user) {
        user.setCreateTime(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User updateUser(Long id, User userDetails) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setEmail(userDetails.getEmail());
                    user.setRole(userDetails.getRole());
                    user.setDepartment(userDetails.getDepartment());
                    user.setPhone(userDetails.getPhone());
                    user.setStatus(userDetails.getStatus());
                    return userRepository.save(user);
                })
                .orElse(null);
    }

    public boolean deleteUser(Long id) {
        if (userRepository.findById(id).isPresent()) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}