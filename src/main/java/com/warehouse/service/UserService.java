package com.warehouse.service;

import com.warehouse.entity.User;
import com.warehouse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
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
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}