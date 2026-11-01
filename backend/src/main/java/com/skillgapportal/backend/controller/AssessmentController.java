package com.skillgapportal.backend.controller;

import com.skillgapportal.backend.dto.AssessmentResultDto;
import com.skillgapportal.backend.dto.AssessmentSubmitDto;
import com.skillgapportal.backend.entity.Question;
import com.skillgapportal.backend.service.AssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assessments")
@RequiredArgsConstructor
public class AssessmentController {

    private final AssessmentService assessmentService;

    @GetMapping("/questions")
    public ResponseEntity<List<Question>> getQuestions(Authentication authentication) {
        return ResponseEntity.ok(assessmentService.getQuestionsForUser(authentication.getName()));
    }

    @PostMapping("/submit")
    public ResponseEntity<AssessmentResultDto> submitAssessment(
            Authentication authentication,
            @RequestBody AssessmentSubmitDto submission
    ) {
        return ResponseEntity.ok(assessmentService.submitAssessment(authentication.getName(), submission));
    }

    @GetMapping("/history")
    public ResponseEntity<List<AssessmentResultDto>> getHistory(Authentication authentication) {
        return ResponseEntity.ok(assessmentService.getAssessmentHistory(authentication.getName()));
    }
}
