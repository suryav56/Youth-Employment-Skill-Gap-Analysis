package com.skillgapportal.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class AssessmentSubmitDto {
    private List<AnswerDto> answers;

    @Data
    public static class AnswerDto {
        private Long questionId;
        private String selectedOption; // A, B, C, or D
    }
}
