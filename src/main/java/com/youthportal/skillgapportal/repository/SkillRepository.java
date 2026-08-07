package com.youthportal.skillgapportal.repository;

import com.youthportal.skillgapportal.entity.Skill;
import com.youthportal.skillgapportal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    List<Skill> findByUser(User user);

    Optional<Skill> findByNameIgnoreCase(String name);
}