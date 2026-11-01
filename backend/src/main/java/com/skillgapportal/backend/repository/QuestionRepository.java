package com.skillgapportal.backend.repository;

import com.skillgapportal.backend.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByCategoryIgnoreCase(String category);
    List<Question> findBySkillId(Long skillId);
}
