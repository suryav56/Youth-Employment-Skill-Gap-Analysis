package com.skillgapportal.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "recommendations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String analysis; // AI generated gap interpretation

    @Column(columnDefinition = "TEXT")
    private String recommendedCareers; // JSON/CSV list of recommended roles

    @Column(columnDefinition = "TEXT")
    private String recommendedSkills; // JSON/CSV list of missing/weak skills

    @Column(columnDefinition = "TEXT", nullable = false)
    private String learningPath; // Detailed step-by-step roadmap (markdown formatted)

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
