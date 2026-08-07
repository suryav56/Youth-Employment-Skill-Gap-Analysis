package com.youthportal.skillgapportal.repository;

import com.youthportal.skillgapportal.entity.Skill;
import com.youthportal.skillgapportal.entity.TrainingCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainingCourseRepository
        extends JpaRepository<TrainingCourse, Long> {

    List<TrainingCourse> findBySkill(Skill skill);
}