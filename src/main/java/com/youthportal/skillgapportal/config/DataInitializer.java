package com.youthportal.skillgapportal.config;

import com.youthportal.skillgapportal.entity.Skill;
import com.youthportal.skillgapportal.entity.TrainingCourse;
import com.youthportal.skillgapportal.repository.SkillRepository;
import com.youthportal.skillgapportal.repository.TrainingCourseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initializeTrainingCourses(
            SkillRepository skillRepository,
            TrainingCourseRepository trainingCourseRepository) {

        return args -> {

            addCourse(
                    "Spring Boot for Beginners",
                    "Learn Spring Boot, REST APIs and backend development.",
                    "Spring Academy",
                    "6 Weeks",
                    "Spring Boot",
                    skillRepository,
                    trainingCourseRepository
            );

            addCourse(
                    "Git and GitHub Fundamentals",
                    "Learn Git version control and GitHub workflow.",
                    "Git Training",
                    "2 Weeks",
                    "Git",
                    skillRepository,
                    trainingCourseRepository
            );

            addCourse(
                    "REST API Development",
                    "Learn how to design and develop REST APIs using Spring Boot.",
                    "Backend Academy",
                    "4 Weeks",
                    "REST API",
                    skillRepository,
                    trainingCourseRepository
            );
        };
    }

    private void addCourse(
            String title,
            String description,
            String provider,
            String duration,
            String skillName,
            SkillRepository skillRepository,
            TrainingCourseRepository trainingCourseRepository) {

        Skill skill = skillRepository.findByNameIgnoreCase(skillName)
                .orElse(null);

        if (skill != null &&
                trainingCourseRepository.findBySkill(skill).isEmpty()) {

            TrainingCourse course = new TrainingCourse();

            course.setTitle(title);
            course.setDescription(description);
            course.setProvider(provider);
            course.setDuration(duration);
            course.setSkill(skill);

            trainingCourseRepository.save(course);
        }
    }
}