package com.skillgapportal.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentResultDto {
    private Long id;
    private Integer score;
    private String details;
    private LocalDateTime takenAt;
}
