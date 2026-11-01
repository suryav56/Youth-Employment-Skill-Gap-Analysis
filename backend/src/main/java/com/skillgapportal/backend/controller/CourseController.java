package com.skillgapportal.backend.controller;

import com.skillgapportal.backend.dto.CourseDto;
import com.skillgapportal.backend.entity.Course;
import com.skillgapportal.backend.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/catalog")
    public ResponseEntity<List<Course>> getCatalog() {
        return ResponseEntity.ok(courseService.getCatalog());
    }

    @GetMapping
    public ResponseEntity<List<CourseDto>> getUserCourses(Authentication authentication) {
        return ResponseEntity.ok(courseService.getUserCourses(authentication.getName()));
    }

    @PostMapping("/enroll/{courseId}")
    public ResponseEntity<CourseDto> enroll(
            Authentication authentication,
            @PathVariable Long courseId
    ) {
        return ResponseEntity.ok(courseService.enrollInCourse(authentication.getName(), courseId));
    }

    @PutMapping("/progress/{courseId}")
    public ResponseEntity<CourseDto> updateProgress(
            Authentication authentication,
            @PathVariable Long courseId,
            @RequestParam String status
    ) {
        return ResponseEntity.ok(courseService.updateCourseStatus(authentication.getName(), courseId, status));
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<CourseDto>> getRecommendations(Authentication authentication) {
        return ResponseEntity.ok(courseService.getRecommendations(authentication.getName()));
    }
}
