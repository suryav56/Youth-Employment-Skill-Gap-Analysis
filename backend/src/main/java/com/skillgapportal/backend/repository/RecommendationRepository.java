package com.skillgapportal.backend.repository;

import com.skillgapportal.backend.entity.Recommendation;
import com.skillgapportal.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findByUserOrderByCreatedAtDesc(User user);
    List<Recommendation> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<Recommendation> findFirstByUserIdOrderByCreatedAtDesc(Long userId);
}
