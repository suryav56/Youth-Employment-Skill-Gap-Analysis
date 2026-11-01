package com.skillgapportal.backend.repository;

import com.skillgapportal.backend.entity.UserCourse;
import com.skillgapportal.backend.entity.User;
import com.skillgapportal.backend.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserCourseRepository extends JpaRepository<UserCourse, Long> {
    List<UserCourse> findByUser(User user);
    List<UserCourse> findByUserId(Long userId);
    Optional<UserCourse> findByUserAndCourse(User user, Course course);
    Optional<UserCourse> findByUserIdAndCourseId(Long userId, Long courseId);
}
