package com.skillgapportal.backend.controller;

import com.skillgapportal.backend.entity.Recommendation;
import com.skillgapportal.backend.entity.User;
import com.skillgapportal.backend.entity.UserSkill;
import com.skillgapportal.backend.service.RecommendationService;
import com.skillgapportal.backend.service.UserService;
import com.skillgapportal.backend.repository.UserRepository;
import com.skillgapportal.backend.repository.UserSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final UserSkillRepository userSkillRepository;

    @GetMapping
    public ResponseEntity<Recommendation> getLatest(Authentication authentication) {
        return ResponseEntity.ok(recommendationService.getLatestRecommendation(authentication.getName()));
    }

    @PostMapping("/generate")
    public ResponseEntity<Recommendation> generate(Authentication authentication) {
        return ResponseEntity.ok(recommendationService.generateNewRecommendation(authentication.getName()));
    }

    @GetMapping("/gap")
    public ResponseEntity<List<Map<String, Object>>> getGapAnalysis(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String careerGoal = user.getCareerGoal();
        List<String> requiredSkills = userService.getRequiredSkillsForRole(careerGoal);

        List<UserSkill> userSkills = userSkillRepository.findByUserId(user.getId());
        Map<String, UserSkill> acquiredMap = userSkills.stream()
                .filter(us -> "acquired".equalsIgnoreCase(us.getStatus()))
                .collect(Collectors.toMap(
                        us -> us.getSkill().getName().toLowerCase().trim(),
                        us -> us,
                        (us1, us2) -> us1
                ));

        Map<String, UserSkill> learningMap = userSkills.stream()
                .filter(us -> "learning".equalsIgnoreCase(us.getStatus()))
                .collect(Collectors.toMap(
                        us -> us.getSkill().getName().toLowerCase().trim(),
                        us -> us,
                        (us1, us2) -> us1
                ));

        List<Map<String, Object>> gapList = new ArrayList<>();

        // 1. Process standard required skills
        for (String req : requiredSkills) {
            Map<String, Object> item = new HashMap<>();
            item.put("skill", req.substring(0, 1).toUpperCase() + req.substring(1));
            item.put("required", true);
            
            if (acquiredMap.containsKey(req)) {
                item.put("status", "acquired");
                item.put("level", acquiredMap.get(req).getLevel());
                item.put("matchScore", 100);
            } else if (learningMap.containsKey(req)) {
                item.put("status", "learning");
                item.put("level", learningMap.get(req).getLevel());
                item.put("matchScore", 50);
            } else {
                item.put("status", "missing");
                item.put("level", "NONE");
                item.put("matchScore", 0);
            }
            gapList.add(item);
        }

        // 2. Process extra user skills that are not in the standard list
        Set<String> reqSet = new HashSet<>(requiredSkills);
        for (UserSkill us : userSkills) {
            String name = us.getSkill().getName().toLowerCase().trim();
            if (!reqSet.contains(name)) {
                Map<String, Object> item = new HashMap<>();
                item.put("skill", us.getSkill().getName());
                item.put("required", false);
                item.put("status", us.getStatus().toLowerCase());
                item.put("level", us.getLevel());
                item.put("matchScore", "acquired".equalsIgnoreCase(us.getStatus()) ? 100 : 50);
                gapList.add(item);
            }
        }

        return ResponseEntity.ok(gapList);
    }
}
