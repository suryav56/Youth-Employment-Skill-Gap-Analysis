package com.skillgapportal.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillDto {
    private Long id;
    private Long skillId; // Reference to core skill library
    private String name;
    private String category;
    private String level; // BEGINNER, INTERMEDIATE, ADVANCED
    private String status; // LEARNING, ACQUIRED
}
