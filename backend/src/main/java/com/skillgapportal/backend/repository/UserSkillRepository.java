package com.skillgapportal.backend.repository;

import com.skillgapportal.backend.entity.UserSkill;
import com.skillgapportal.backend.entity.User;
import com.skillgapportal.backend.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {
    List<UserSkill> findByUser(User user);
    List<UserSkill> findByUserId(Long userId);
    Optional<UserSkill> findByUserAndSkill(User user, Skill skill);
    Optional<UserSkill> findByUserIdAndSkillId(Long userId, Long skillId);
}
