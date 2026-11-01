package com.skillgapportal.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDto {
    private Long id; // UserCourse mapping ID (nullable if not enrolled)
    private Long courseId; // Course DB ID
    private String title;
    private String provider;
    private String url;
    private String category;
    private String difficulty;
    private String status; // ENROLLED, IN_PROGRESS, COMPLETED (null if not tracked)
}
