package com.youthportal.skillgapportal.service;

import com.youthportal.skillgapportal.entity.Skill;
import com.youthportal.skillgapportal.entity.TrainingCourse;
import com.youthportal.skillgapportal.repository.TrainingCourseRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class TrainingRecommendationService {

    private final TrainingCourseRepository trainingCourseRepository;

    public TrainingRecommendationService(
            TrainingCourseRepository trainingCourseRepository) {

        this.trainingCourseRepository = trainingCourseRepository;
    }

    public List<TrainingCourse> recommendCourses(
            Set<Skill> missingSkills) {

        List<TrainingCourse> recommendations = new ArrayList<>();

        for (Skill skill : missingSkills) {

            List<TrainingCourse> courses =
                    trainingCourseRepository.findBySkill(skill);

            recommendations.addAll(courses);
        }

        return recommendations;
    }
}