package com.warehouse.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;
import javax.validation.constraints.*;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank
    private String password;

    @Email
    @Column(nullable = false)
    private String email;

    private String role;
    private String department;
    private String phone;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    private String avatar;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    public enum UserStatus {
        ACTIVE, INACTIVE
    }
}