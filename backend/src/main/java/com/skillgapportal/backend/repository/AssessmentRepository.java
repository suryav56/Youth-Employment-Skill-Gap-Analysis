package com.skillgapportal.backend.repository;

import com.skillgapportal.backend.entity.Assessment;
import com.skillgapportal.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    List<Assessment> findByUserOrderByTakenAtDesc(User user);
    List<Assessment> findByUserIdOrderByTakenAtDesc(Long userId);
}
