package com.skillgapportal.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillgapportal.backend.dto.AssessmentSubmitDto;
import com.skillgapportal.backend.dto.AssessmentResultDto;
import com.skillgapportal.backend.entity.*;
import com.skillgapportal.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssessmentService {

    private final QuestionRepository questionRepository;
    private final AssessmentRepository assessmentRepository;
    private final UserRepository userRepository;
    private final UserSkillRepository userSkillRepository;
    private final UserService userService;
    private final SkillRepository skillRepository;
    private final GroqService groqService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Question> getQuestionsForUser(String username) {
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

        String careerGoal = user.getCareerGoal() != null ? user.getCareerGoal() : "General Developer";
        String education = user.getEducation() != null ? user.getEducation() : "Not specified";
        String experience = user.getExperience() != null ? user.getExperience() : "Not specified";

        List<Question> questions = new ArrayList<>();

        // 1. Try to generate questions dynamically using Groq Service
        try {
            StringBuilder promptBuilder = new StringBuilder();
            promptBuilder.append("Generate a JSON array of exactly 10 unique, multiple-choice questions for a technical quiz tailored to this user profile:\n");
            promptBuilder.append("- Career Goal: ").append(careerGoal).append("\n");
            promptBuilder.append("- Education: ").append(education).append("\n");
            promptBuilder.append("- Experience: ").append(experience).append("\n");
            promptBuilder.append("- Acquired Skills: ").append(String.join(", ", acquiredSkills)).append("\n");
            promptBuilder.append("- Skills Currently Learning: ").append(String.join(", ", learningSkills)).append("\n\n");
            promptBuilder.append("Rules:\n");
            promptBuilder.append("1. Respond ONLY with a valid JSON array of objects. Do not include markdown code block syntax (like ```json). No introductory or trailing text.\n");
            promptBuilder.append("2. Each question object must contain these fields:\n");
            promptBuilder.append("   - \"skillName\": name of the skill (e.g. \"React\", \"Spring Boot\", \"MySQL\", \"Python\", \"Agile\", \"Jira\", etc.)\n");
            promptBuilder.append("   - \"category\": one of \"Frontend\", \"Backend\", \"Data Science\", \"Management\"\n");
            promptBuilder.append("   - \"questionText\": the question itself\n");
            promptBuilder.append("   - \"optionA\": option A text\n");
            promptBuilder.append("   - \"optionB\": option B text\n");
            promptBuilder.append("   - \"optionC\": option C text\n");
            promptBuilder.append("   - \"optionD\": option D text\n");
            promptBuilder.append("   - \"correctOption\": exactly \"A\", \"B\", \"C\", or \"D\"\n");
            promptBuilder.append("   - \"difficulty\": \"BEGINNER\", \"INTERMEDIATE\", or \"ADVANCED\" matching the user's background\n");

            String response = groqService.generateRecommendation(promptBuilder.toString());
            if (response != null && !response.trim().isEmpty()) {
                String cleanJson = response.trim();
                if (cleanJson.startsWith("```json")) {
                    cleanJson = cleanJson.substring(7);
                } else if (cleanJson.startsWith("```")) {
                    cleanJson = cleanJson.substring(3);
                }
                if (cleanJson.endsWith("```")) {
                    cleanJson = cleanJson.substring(0, cleanJson.length() - 3);
                }
                cleanJson = cleanJson.trim();

                List<Map<String, Object>> parsedList = objectMapper.readValue(cleanJson, List.class);
                List<Skill> allDbSkills = skillRepository.findAll();
                
                for (Map<String, Object> qMap : parsedList) {
                    String skillName = (String) qMap.get("skillName");
                    String category = (String) qMap.get("category");
                    String questionText = (String) qMap.get("questionText");
                    String optionA = (String) qMap.get("optionA");
                    String optionB = (String) qMap.get("optionB");
                    String optionC = (String) qMap.get("optionC");
                    String optionD = (String) qMap.get("optionD");
                    String correctOption = (String) qMap.get("correctOption");
                    String difficulty = (String) qMap.get("difficulty");

                    if (questionText != null && correctOption != null) {
                        // Find matching skill in DB
                        Skill matchedSkill = allDbSkills.stream()
                                .filter(s -> s.getName().equalsIgnoreCase(skillName))
                                .findFirst()
                                .orElse(null);

                        Question q = Question.builder()
                                .skill(matchedSkill)
                                .category(category != null ? category : "General")
                                .questionText(questionText)
                                .optionA(optionA != null ? optionA : "Option A")
                                .optionB(optionB != null ? optionB : "Option B")
                                .optionC(optionC != null ? optionC : "Option C")
                                .optionD(optionD != null ? optionD : "Option D")
                                .correctOption(correctOption.trim().toUpperCase())
                                .difficulty(difficulty != null ? difficulty.toUpperCase() : "BEGINNER")
                                .build();

                        questions.add(questionRepository.save(q));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to generate questions using Groq API: " + e.getMessage());
        }

        // 2. If dynamic generation yielded less than 10 questions, use the database-based fallback scoring
        if (questions.size() < 10) {
            List<Question> allDbQuestions = questionRepository.findAll();
            List<String> requiredSkills = userService.getRequiredSkillsForRole(careerGoal);
            
            // Determine user level based on education & experience
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

            Map<Question, Integer> scoreMap = new HashMap<>();
            Random random = new Random();

            for (Question q : allDbQuestions) {
                // Avoid adding duplicate questions that were already generated dynamically in this call
                if (questions.contains(q)) {
                    continue;
                }

                int score = 0;

                // Category alignment
                if (q.getCategory() != null && q.getCategory().equalsIgnoreCase(targetCat)) {
                    score += 50;
                }

                // Skill alignment
                if (q.getSkill() != null) {
                    String skillName = q.getSkill().getName().toLowerCase();
                    boolean hasSkillInProfile = userSkills.stream()
                            .anyMatch(us -> us.getSkill().getName().equalsIgnoreCase(skillName));
                    if (hasSkillInProfile) {
                        score += 30;
                    }
                    if (requiredSkills.contains(skillName)) {
                        score += 20;
                    }
                }

                // Difficulty alignment
                if (q.getDifficulty() != null) {
                    if (q.getDifficulty().equalsIgnoreCase(userLevel)) {
                        score += 20;
                    } else {
                        score += 10;
                    }
                }

                // Add random noise/offset to ensure uniqueness/randomness across quizzes
                score += random.nextInt(16);

                scoreMap.put(q, score);
            }

            // Sort questions by relevance score descending
            List<Question> sortedFallback = scoreMap.entrySet().stream()
                    .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            int needed = 10 - questions.size();
            List<Question> fallbackBatch = sortedFallback.stream()
                    .limit(needed)
                    .collect(Collectors.toList());

            questions.addAll(fallbackBatch);
        }

        // Return the final list shuffled so option ordering is random in quiz UI
        Collections.shuffle(questions);
        return questions.stream().limit(10).collect(Collectors.toList());
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

    public AssessmentResultDto submitAssessment(String username, AssessmentSubmitDto submission) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<AssessmentSubmitDto.AnswerDto> answers = submission.getAnswers();
        if (answers == null || answers.isEmpty()) {
            throw new IllegalArgumentException("No answers provided");
        }

        int correctCount = 0;
        int totalQuestions = answers.size();
        
        // Track correct counts by skill/category for breakdown
        Map<String, int[]> categoryStats = new HashMap<>(); // key -> [correct, total]

        for (AssessmentSubmitDto.AnswerDto ans : answers) {
            Question q = questionRepository.findById(ans.getQuestionId())
                    .orElse(null);
            
            if (q != null) {
                String cat = q.getCategory() != null ? q.getCategory() : "General";
                categoryStats.putIfAbsent(cat, new int[]{0, 0});
                categoryStats.get(cat)[1]++; // Increment total for category
                
                if (q.getCorrectOption().trim().equalsIgnoreCase(ans.getSelectedOption().trim())) {
                    correctCount++;
                    categoryStats.get(cat)[0]++; // Increment correct for category
                    
                    // Award user skill status update if they answer correctly for a specific skill
                    if (q.getSkill() != null) {
                        updateUserSkillPerformance(user, q.getSkill(), true);
                    }
                } else {
                    if (q.getSkill() != null) {
                        updateUserSkillPerformance(user, q.getSkill(), false);
                    }
                }
            }
        }

        int finalScore = (correctCount * 100) / totalQuestions;
        
        // Structure the details breakdown
        Map<String, String> breakdown = new HashMap<>();
        for (Map.Entry<String, int[]> entry : categoryStats.entrySet()) {
            int correct = entry.getValue()[0];
            int total = entry.getValue()[1];
            int percent = total > 0 ? (correct * 100) / total : 0;
            breakdown.put(entry.getKey(), correct + "/" + total + " (" + percent + "%)");
        }

        String detailsJson = "";
        try {
            detailsJson = objectMapper.writeValueAsString(breakdown);
        } catch (Exception e) {
            detailsJson = breakdown.toString();
        }

        Assessment assessment = Assessment.builder()
                .user(user)
                .score(finalScore)
                .takenAt(LocalDateTime.now())
                .details(detailsJson)
                .build();

        assessmentRepository.save(assessment);

        return AssessmentResultDto.builder()
                .id(assessment.getId())
                .score(assessment.getScore())
                .details(detailsJson)
                .takenAt(assessment.getTakenAt())
                .build();
    }

    private void updateUserSkillPerformance(User user, Skill skill, boolean answeredCorrectly) {
        Optional<UserSkill> optUserSkill = userSkillRepository.findByUserAndSkill(user, skill);
        if (optUserSkill.isPresent()) {
            UserSkill us = optUserSkill.get();
            if (answeredCorrectly) {
                // If they were learning, mark as acquired.
                if ("LEARNING".equalsIgnoreCase(us.getStatus())) {
                    us.setStatus("ACQUIRED");
                }
                // Upgrade level
                if ("BEGINNER".equalsIgnoreCase(us.getLevel())) {
                    us.setLevel("INTERMEDIATE");
                } else if ("INTERMEDIATE".equalsIgnoreCase(us.getLevel())) {
                    us.setLevel("ADVANCED");
                }
            } else {
                // If incorrect, make sure level is not immediately advanced
                if ("ADVANCED".equalsIgnoreCase(us.getLevel())) {
                    us.setLevel("INTERMEDIATE");
                }
                // Weak skill identified!
                us.setStatus("LEARNING");
            }
            userSkillRepository.save(us);
        } else {
            if (answeredCorrectly) {
                // Add skill automatically as ACQUIRED/INTERMEDIATE if they score correctly but didn't list it
                UserSkill newUs = UserSkill.builder()
                        .user(user)
                        .skill(skill)
                        .level("INTERMEDIATE")
                        .status("ACQUIRED")
                        .build();
                userSkillRepository.save(newUs);
            } else {
                // Add skill automatically as LEARNING/BEGINNER if they score incorrectly
                UserSkill newUs = UserSkill.builder()
                        .user(user)
                        .skill(skill)
                        .level("BEGINNER")
                        .status("LEARNING")
                        .build();
                userSkillRepository.save(newUs);
            }
        }
    }

    public List<AssessmentResultDto> getAssessmentHistory(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return assessmentRepository.findByUserIdOrderByTakenAtDesc(user.getId()).stream()
                .map(a -> AssessmentResultDto.builder()
                        .id(a.getId())
                        .score(a.getScore())
                        .details(a.getDetails())
                        .takenAt(a.getTakenAt())
                        .build())
                .collect(Collectors.toList());
    }
}
