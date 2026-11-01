package com.skillgapportal.backend.service;

import com.skillgapportal.backend.dto.CourseDto;
import com.skillgapportal.backend.entity.Course;
import com.skillgapportal.backend.entity.User;
import com.skillgapportal.backend.entity.UserCourse;
import com.skillgapportal.backend.entity.UserSkill;
import com.skillgapportal.backend.repository.CourseRepository;
import com.skillgapportal.backend.repository.UserRepository;
import com.skillgapportal.backend.repository.UserCourseRepository;
import com.skillgapportal.backend.repository.UserSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserCourseRepository userCourseRepository;
    private final UserRepository userRepository;
    private final UserSkillRepository userSkillRepository;
    private final UserService userService;

    public List<Course> getCatalog() {
        return courseRepository.findAll();
    }

    public List<CourseDto> getUserCourses(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return userCourseRepository.findByUserId(user.getId()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public CourseDto enrollInCourse(String username, Long courseId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        Optional<UserCourse> existing = userCourseRepository.findByUserAndCourse(user, course);
        if (existing.isPresent()) {
            return mapToDto(existing.get());
        }

        UserCourse userCourse = UserCourse.builder()
                .user(user)
                .course(course)
                .status("ENROLLED")
                .enrolledAt(LocalDateTime.now())
                .build();

        return mapToDto(userCourseRepository.save(userCourse));
    }

    public CourseDto updateCourseStatus(String username, Long courseId, String status) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        UserCourse userCourse = userCourseRepository.findByUserAndCourse(user, course)
                .orElseThrow(() -> new IllegalArgumentException("User is not enrolled in this course"));

        userCourse.setStatus(status.toUpperCase());
        if ("COMPLETED".equalsIgnoreCase(status)) {
            userCourse.setCompletedAt(LocalDateTime.now());
        }

        return mapToDto(userCourseRepository.save(userCourse));
    }

    public List<CourseDto> getRecommendations(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<UserSkill> userSkills = userSkillRepository.findByUserId(user.getId());
        List<UserCourse> userEnrollments = userCourseRepository.findByUserId(user.getId());
        Set<Long> enrolledCourseIds = userEnrollments.stream()
                .map(ue -> ue.getCourse().getId())
                .collect(Collectors.toSet());

        String careerGoal = user.getCareerGoal() != null ? user.getCareerGoal() : "";
        String education = user.getEducation() != null ? user.getEducation() : "";
        String experience = user.getExperience() != null ? user.getExperience() : "";

        // Determine user level based on experience & education
        String userExpLower = experience.toLowerCase();
        String userEduLower = education.toLowerCase();
        String expectedDifficulty = "BEGINNER";
        if (userExpLower.contains("senior") || userExpLower.contains("lead") || userExpLower.contains("advanced") || 
            userExpLower.matches(".*[5-9]\\s*year.*") || userExpLower.matches(".*[1-9][0-9]\\s*year.*") || 
            userEduLower.contains("phd") || userEduLower.contains("doctorate")) {
            expectedDifficulty = "ADVANCED";
        } else if (userExpLower.contains("mid") || userExpLower.contains("experienced") || 
                   userExpLower.matches(".*[2-4]\\s*year.*") || 
                   userEduLower.contains("bachelor") || userEduLower.contains("degree") || 
                   userEduLower.contains("b.tech") || userEduLower.contains("btech") || 
                   userEduLower.contains("mca") || userEduLower.contains("master")) {
            expectedDifficulty = "INTERMEDIATE";
        }

        final String userLevel = expectedDifficulty;
        final String targetCat = getCategoryForRole(careerGoal);

        // Required skills for the target career goal
        List<String> requiredSkills = userService.getRequiredSkillsForRole(careerGoal);

        // Identify:
        // - Weak/learning skills (status is LEARNING)
        // - Acquired skills (status is ACQUIRED)
        // - Missing required skills (required but not in profile at all)
        Set<String> learningSkills = userSkills.stream()
                .filter(us -> "LEARNING".equalsIgnoreCase(us.getStatus()))
                .map(us -> us.getSkill().getName().toLowerCase())
                .collect(Collectors.toSet());

        Set<String> acquiredSkills = userSkills.stream()
                .filter(us -> "ACQUIRED".equalsIgnoreCase(us.getStatus()))
                .map(us -> us.getSkill().getName().toLowerCase())
                .collect(Collectors.toSet());

        List<String> missingRequiredSkills = requiredSkills.stream()
                .filter(req -> userSkills.stream().noneMatch(us -> us.getSkill().getName().equalsIgnoreCase(req)))
                .collect(Collectors.toList());

        List<Course> allCourses = courseRepository.findAll();

        // Calculate a recommendation score for each course
        class ScoredCourse {
            Course course;
            int score;

            ScoredCourse(Course course, int score) {
                this.course = course;
                this.score = score;
            }
        }

        List<ScoredCourse> scoredCourses = allCourses.stream()
                .filter(c -> !enrolledCourseIds.contains(c.getId()))
                .map(c -> {
                    int score = 0;
                    String titleLower = c.getTitle().toLowerCase();

                    // 1. Category Alignment
                    if (c.getCategory() != null && c.getCategory().equalsIgnoreCase(targetCat)) {
                        score += 50;
                    }

                    // 2. Skill Alignment
                    // If course title mentions a skill that the user is currently learning (weak/gap skill)
                    for (String lSkill : learningSkills) {
                        if (titleLower.contains(lSkill)) {
                            score += 40;
                        }
                    }

                    // If course title mentions a skill that is completely missing from user profile but required
                    for (String mSkill : missingRequiredSkills) {
                        if (titleLower.contains(mSkill)) {
                            score += 30;
                        }
                    }

                    // If course title mentions an acquired skill, we give it lower priority
                    for (String aSkill : acquiredSkills) {
                        if (titleLower.contains(aSkill)) {
                            score -= 10;
                        }
                    }

                    // 3. Difficulty Alignment
                    if (c.getDifficulty() != null) {
                        if (c.getDifficulty().equalsIgnoreCase(userLevel)) {
                            score += 20;
                        } else if (userLevel.equals("INTERMEDIATE") && (c.getDifficulty().equalsIgnoreCase("BEGINNER") || c.getDifficulty().equalsIgnoreCase("ADVANCED"))) {
                            score += 10;
                        } else if (userLevel.equals("ADVANCED") && c.getDifficulty().equalsIgnoreCase("INTERMEDIATE")) {
                            score += 10;
                        } else if (userLevel.equals("BEGINNER") && c.getDifficulty().equalsIgnoreCase("INTERMEDIATE")) {
                            score += 5;
                        }
                    }

                    return new ScoredCourse(c, score);
                })
                .sorted((sc1, sc2) -> Integer.compare(sc2.score, sc1.score))
                .collect(Collectors.toList());

        // Return up to 6 recommended courses
        return scoredCourses.stream()
                .limit(6)
                .map(sc -> CourseDto.builder()
                        .courseId(sc.course.getId())
                        .title(sc.course.getTitle())
                        .provider(sc.course.getProvider())
                        .url(sc.course.getUrl())
                        .category(sc.course.getCategory())
                        .difficulty(sc.course.getDifficulty())
                        .build())
                .collect(Collectors.toList());
    }

    private String getCategoryForRole(String role) {
        if (role == null) return "General";
        String r = role.toLowerCase();
        if (r.contains("frontend") || r.contains("ui") || r.contains("web")) return "Frontend";
        if (r.contains("backend") || r.contains("api") || r.contains("server") || r.contains("full stack") || r.contains("fullstack")) return "Backend";
        if (r.contains("data") || r.contains("science") || r.contains("ml") || r.contains("ai") || r.contains("machine")) return "Data Science";
        if (r.contains("project") || r.contains("manage") || r.contains("scrum") || r.contains("agile")) return "Management";
        return "General";
    }

    private CourseDto mapToDto(UserCourse uc) {
        return CourseDto.builder()
                .id(uc.getId())
                .courseId(uc.getCourse().getId())
                .title(uc.getCourse().getTitle())
                .provider(uc.getCourse().getProvider())
                .url(uc.getCourse().getUrl())
                .category(uc.getCourse().getCategory())
                .difficulty(uc.getCourse().getDifficulty())
                .status(uc.getStatus())
                .build();
    }
}
