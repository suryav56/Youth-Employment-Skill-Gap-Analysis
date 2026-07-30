package com.youthportal.skillgapportal.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "course_skills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private TrainingCourse course;

    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;
}