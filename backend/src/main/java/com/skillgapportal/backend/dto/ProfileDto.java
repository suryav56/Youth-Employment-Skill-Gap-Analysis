package com.skillgapportal.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileDto {
    private String fullName;
    private String email;
    private String education;
    private String experience;
    private String careerGoal;
    private String bio;
    private boolean profileCompleted;
}
