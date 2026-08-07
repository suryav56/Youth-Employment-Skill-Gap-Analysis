package com.youthportal.skillgapportal.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "training_courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainingCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    private String provider;

    private String duration;

    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;
}