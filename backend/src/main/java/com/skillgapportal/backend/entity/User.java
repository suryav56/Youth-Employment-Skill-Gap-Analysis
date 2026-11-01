package com.skillgapportal.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String role; // e.g., ROLE_USER, ROLE_ADMIN

    private String fullName;

    private String education;

    @Column(columnDefinition = "TEXT")
    private String experience; // JSON or text detailing work experience

    private String careerGoal; // Target career role, e.g., Frontend Developer

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Builder.Default
    private boolean profileCompleted = false;
}
