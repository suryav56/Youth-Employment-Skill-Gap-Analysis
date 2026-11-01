package com.skillgapportal.backend.service;

import com.skillgapportal.backend.entity.Recommendation;
import com.skillgapportal.backend.entity.User;
import com.skillgapportal.backend.entity.UserSkill;
import com.skillgapportal.backend.repository.RecommendationRepository;
import com.skillgapportal.backend.repository.UserRepository;
import com.skillgapportal.backend.repository.UserSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;
    private final UserSkillRepository userSkillRepository;
    private final UserService userService;
    private final GroqService groqService;

    public Recommendation getLatestRecommendation(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Optional<Recommendation> latest = recommendationRepository.findFirstByUserIdOrderByCreatedAtDesc(user.getId());
        if (latest.isPresent()) {
            return latest.get();
        }

        // Generate a new one if none exists
        return generateNewRecommendation(username);
    }

    public Recommendation generateNewRecommendation(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<UserSkill> userSkills = userSkillRepository.findByUserId(user.getId());

        List<String> acquiredSkills = userSkills.stream()
                .filter(us -> "acquired".equalsIgnoreCase(us.getStatus()))
                .map(us -> us.getSkill().getName() + " (" + us.getLevel() + ")")
                .collect(Collectors.toList());

        List<String> learningSkills = userSkills.stream()
                .filter(us -> "learning".equalsIgnoreCase(us.getStatus()))
                .map(us -> us.getSkill().getName() + " (" + us.getLevel() + ")")
                .collect(Collectors.toList());

        String careerGoal = user.getCareerGoal() != null ? user.getCareerGoal() : "Unspecified Tech Career";
        List<String> requiredSkills = userService.getRequiredSkillsForRole(careerGoal);

        // Build analysis prompt
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("You are an expert career counselor for young professionals.\n\n");
        promptBuilder.append("Please analyze the career gap and build a personalized learning roadmap for the following user:\n");
        promptBuilder.append("- Career Goal: ").append(careerGoal).append("\n");
        promptBuilder.append("- Education: ").append(user.getEducation() != null ? user.getEducation() : "None listed").append("\n");
        promptBuilder.append("- Experience: ").append(user.getExperience() != null ? user.getExperience() : "None listed").append("\n");
        promptBuilder.append("- Current Acquired Skills: ").append(acquiredSkills.isEmpty() ? "None listed" : String.join(", ", acquiredSkills)).append("\n");
        promptBuilder.append("- Skills Currently Learning: ").append(learningSkills.isEmpty() ? "None listed" : String.join(", ", learningSkills)).append("\n");
        promptBuilder.append("- Standard Required Skills for Target Goal: ").append(String.join(", ", requiredSkills)).append("\n\n");
        
        promptBuilder.append("Please respond with a structured career advisory report in Markdown. Include:\n");
        promptBuilder.append("1. **Career Gap Analysis**: Evaluate where the user stands relative to their target career goal (list missing or weak skills).\n");
        promptBuilder.append("2. **Recommended Careers**: Confirm if their career goal is highly suitable or suggest adjacent roles.\n");
        promptBuilder.append("3. **Step-by-Step Learning Path**: Provide a timeline-based roadmap (e.g. Weeks 1-4, Weeks 5-8) describing which skills to learn, how to practice, and recommended actions.\n\n");
        promptBuilder.append("Keep the tone professional, encouraging, and clear.");

        String rawAiResponse = groqService.generateRecommendation(promptBuilder.toString());

        // Extract key segments for metadata lists (basic parsing)
        String recommendedCareers = careerGoal;
        if (rawAiResponse.toLowerCase().contains("recommended careers")) {
            recommendedCareers = careerGoal + ", Adjacent Roles";
        }

        List<String> missingSkills = requiredSkills.stream()
                .filter(req -> userSkills.stream().noneMatch(us -> us.getSkill().getName().equalsIgnoreCase(req) && "acquired".equalsIgnoreCase(us.getStatus())))
                .collect(Collectors.toList());

        Recommendation rec = Recommendation.builder()
                .user(user)
                .analysis("Gap analysis for " + careerGoal + ": " + (missingSkills.isEmpty() ? "All core skills listed!" : "Missing core skills: " + String.join(", ", missingSkills)))
                .recommendedCareers(recommendedCareers)
                .recommendedSkills(missingSkills.isEmpty() ? "None" : String.join(", ", missingSkills))
                .learningPath(rawAiResponse)
                .createdAt(LocalDateTime.now())
                .build();

        return recommendationRepository.save(rec);
    }
}
