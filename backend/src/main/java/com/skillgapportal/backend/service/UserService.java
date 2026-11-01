package com.skillgapportal.backend.service;

import com.skillgapportal.backend.dto.ProfileDto;
import com.skillgapportal.backend.entity.Assessment;
import com.skillgapportal.backend.entity.User;
import com.skillgapportal.backend.entity.UserSkill;
import com.skillgapportal.backend.repository.AssessmentRepository;
import com.skillgapportal.backend.repository.UserRepository;
import com.skillgapportal.backend.repository.UserSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserSkillRepository userSkillRepository;
    private final AssessmentRepository assessmentRepository;

    private static final Map<String, List<String>> ROLE_REQUIRED_SKILLS = new HashMap<>();

    static {
        ROLE_REQUIRED_SKILLS.put("frontend developer", List.of("html", "css", "javascript", "react", "typescript"));
        ROLE_REQUIRED_SKILLS.put("backend developer", List.of("java", "spring boot", "mysql", "git", "rest apis"));
        ROLE_REQUIRED_SKILLS.put("full stack developer", List.of("javascript", "react", "java", "spring boot", "mysql"));
        ROLE_REQUIRED_SKILLS.put("data scientist", List.of("python", "sql", "machine learning", "data analysis", "statistics"));
        ROLE_REQUIRED_SKILLS.put("project manager", List.of("agile", "scrum", "communication", "risk management", "jira"));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    }

    public ProfileDto getProfile(String username) {
        User user = getUserByUsername(username);
        return ProfileDto.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .education(user.getEducation())
                .experience(user.getExperience())
                .careerGoal(user.getCareerGoal())
                .bio(user.getBio())
                .profileCompleted(user.isProfileCompleted())
                .build();
    }

    public ProfileDto updateProfile(String username, ProfileDto profileDto) {
        User user = getUserByUsername(username);
        
        user.setFullName(profileDto.getFullName());
        user.setEducation(profileDto.getEducation());
        user.setExperience(profileDto.getExperience());
        user.setCareerGoal(profileDto.getCareerGoal());
        user.setBio(profileDto.getBio());
        
        // Calculate profile completion
        int completion = calculateCompletionPercentage(user);
        user.setProfileCompleted(completion == 100);
        
        userRepository.save(user);
        
        return getProfile(username);
    }

    public Map<String, Object> getDashboardStats(String username) {
        User user = getUserByUsername(username);
        List<UserSkill> userSkills = userSkillRepository.findByUserId(user.getId());
        List<Assessment> assessments = assessmentRepository.findByUserIdOrderByTakenAtDesc(user.getId());
        
        int profileCompletion = calculateCompletionPercentage(user);
        int skillsCount = userSkills.size();
        
        int latestAssessmentScore = 0;
        if (!assessments.isEmpty()) {
            latestAssessmentScore = assessments.get(0).getScore();
        }
        
        int skillMatchPercentage = calculateSkillMatch(user, userSkills);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("profileCompletion", profileCompletion);
        stats.put("skillsCount", skillsCount);
        stats.put("latestAssessmentScore", latestAssessmentScore);
        stats.put("skillMatchPercentage", skillMatchPercentage);
        stats.put("careerGoal", user.getCareerGoal());
        
        return stats;
    }

    private int calculateCompletionPercentage(User user) {
        int fieldsFilled = 0;
        int totalFields = 5;
        
        if (user.getFullName() != null && !user.getFullName().trim().isEmpty()) fieldsFilled++;
        if (user.getEducation() != null && !user.getEducation().trim().isEmpty()) fieldsFilled++;
        if (user.getExperience() != null && !user.getExperience().trim().isEmpty()) fieldsFilled++;
        if (user.getCareerGoal() != null && !user.getCareerGoal().trim().isEmpty()) fieldsFilled++;
        if (user.getBio() != null && !user.getBio().trim().isEmpty()) fieldsFilled++;
        
        return (fieldsFilled * 100) / totalFields;
    }

    private int calculateSkillMatch(User user, List<UserSkill> userSkills) {
        String goal = user.getCareerGoal();
        if (goal == null || goal.trim().isEmpty()) {
            return 0;
        }
        
        String cleanGoal = goal.toLowerCase().trim();
        List<String> requiredSkills = null;
        
        // Match exact or contains
        for (Map.Entry<String, List<String>> entry : ROLE_REQUIRED_SKILLS.entrySet()) {
            if (cleanGoal.contains(entry.getKey()) || entry.getKey().contains(cleanGoal)) {
                requiredSkills = entry.getValue();
                break;
            }
        }
        
        if (requiredSkills == null) {
            // Default required skills if career goal is unknown
            requiredSkills = List.of("communication", "problem solving", "git", "project management");
        }
        
        long acquiredCount = userSkills.stream()
                .filter(us -> "acquired".equalsIgnoreCase(us.getStatus()))
                .map(us -> us.getSkill().getName().toLowerCase().trim())
                .filter(requiredSkills::contains)
                .count();
        
        return (int) ((acquiredCount * 100) / requiredSkills.size());
    }

    public List<String> getRequiredSkillsForRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String cleanRole = role.toLowerCase().trim();
        for (Map.Entry<String, List<String>> entry : ROLE_REQUIRED_SKILLS.entrySet()) {
            if (cleanRole.contains(entry.getKey()) || entry.getKey().contains(cleanRole)) {
                return entry.getValue();
            }
        }
        return List.of("communication", "problem solving", "git", "project management");
    }
}
