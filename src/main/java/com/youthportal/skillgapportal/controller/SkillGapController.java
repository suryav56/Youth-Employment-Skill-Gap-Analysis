package com.youthportal.skillgapportal.controller;

import com.youthportal.skillgapportal.entity.Career;
import com.youthportal.skillgapportal.entity.Skill;
import com.youthportal.skillgapportal.entity.TrainingCourse;
import com.youthportal.skillgapportal.entity.User;
import com.youthportal.skillgapportal.repository.UserRepository;
import com.youthportal.skillgapportal.service.CareerService;
import com.youthportal.skillgapportal.service.SkillGapService;
import com.youthportal.skillgapportal.service.TrainingRecommendationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/skill-gap")
public class SkillGapController {

    private final CareerService careerService;
    private final UserRepository userRepository;
    private final SkillGapService skillGapService;
    private final TrainingRecommendationService trainingRecommendationService;

    public SkillGapController(
            CareerService careerService,
            UserRepository userRepository,
            SkillGapService skillGapService,
            TrainingRecommendationService trainingRecommendationService) {

        this.careerService = careerService;
        this.userRepository = userRepository;
        this.skillGapService = skillGapService;
        this.trainingRecommendationService = trainingRecommendationService;
    }

    @GetMapping("/{careerId}")
    public String analyzeSkillGap(
            @PathVariable Long careerId,
            Authentication authentication,
            Model model) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Career career = careerService.getCareerById(careerId);

        Set<Skill> userSkills =
                new HashSet<>(user.getSkills());

        Set<Skill> missingSkills =
                skillGapService.findMissingSkills(
                        career,
                        userSkills
                );

        Set<Skill> matchedSkills = new HashSet<>();

        for (Skill userSkill : userSkills) {

            for (Skill requiredSkill : career.getRequiredSkills()) {

                if (userSkill.getName()
                        .equalsIgnoreCase(requiredSkill.getName())) {

                    matchedSkills.add(userSkill);
                }
            }
        }

        // Find training courses for missing skills
        List<TrainingCourse> recommendedCourses =
                trainingRecommendationService
                        .recommendCourses(missingSkills);

        model.addAttribute("career", career);
        model.addAttribute("userSkills", userSkills);
        model.addAttribute("matchedSkills", matchedSkills);
        model.addAttribute("missingSkills", missingSkills);
        model.addAttribute("recommendedCourses", recommendedCourses);

        return "skill-gap";
    }
}