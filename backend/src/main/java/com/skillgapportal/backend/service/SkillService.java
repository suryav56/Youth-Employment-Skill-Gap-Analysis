package com.skillgapportal.backend.service;

import com.skillgapportal.backend.dto.SkillDto;
import com.skillgapportal.backend.entity.Skill;
import com.skillgapportal.backend.entity.User;
import com.skillgapportal.backend.entity.UserSkill;
import com.skillgapportal.backend.repository.SkillRepository;
import com.skillgapportal.backend.repository.UserRepository;
import com.skillgapportal.backend.repository.UserSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final UserSkillRepository userSkillRepository;
    private final UserRepository userRepository;

    public List<Skill> getCoreSkills() {
        return skillRepository.findAll();
    }

    public List<Skill> searchCoreSkills(String query) {
        return skillRepository.findByNameContainingIgnoreCase(query);
    }

    public List<SkillDto> getUserSkills(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return userSkillRepository.findByUserId(user.getId()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public SkillDto addUserSkill(String username, SkillDto dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Retrieve or create Core Skill
        Skill skill = skillRepository.findByNameIgnoreCase(dto.getName())
                .orElseGet(() -> {
                    Skill newSkill = Skill.builder()
                            .name(dto.getName())
                            .category(dto.getCategory() != null ? dto.getCategory() : "Other")
                            .build();
                    return skillRepository.save(newSkill);
                });

        // Check if user already has this skill
        Optional<UserSkill> existing = userSkillRepository.findByUserAndSkill(user, skill);
        if (existing.isPresent()) {
            UserSkill userSkill = existing.get();
            userSkill.setLevel(dto.getLevel() != null ? dto.getLevel() : "BEGINNER");
            userSkill.setStatus(dto.getStatus() != null ? dto.getStatus() : "LEARNING");
            return mapToDto(userSkillRepository.save(userSkill));
        }

        UserSkill userSkill = UserSkill.builder()
                .user(user)
                .skill(skill)
                .level(dto.getLevel() != null ? dto.getLevel() : "BEGINNER")
                .status(dto.getStatus() != null ? dto.getStatus() : "LEARNING")
                .build();

        return mapToDto(userSkillRepository.save(userSkill));
    }

    public void removeUserSkill(String username, Long userSkillId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        UserSkill userSkill = userSkillRepository.findById(userSkillId)
                .orElseThrow(() -> new IllegalArgumentException("Skill not found for user"));

        if (!userSkill.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Unauthorized access to user skill");
        }

        userSkillRepository.delete(userSkill);
    }

    private SkillDto mapToDto(UserSkill userSkill) {
        return SkillDto.builder()
                .id(userSkill.getId())
                .skillId(userSkill.getSkill().getId())
                .name(userSkill.getSkill().getName())
                .category(userSkill.getSkill().getCategory())
                .level(userSkill.getLevel())
                .status(userSkill.getStatus())
                .build();
    }
}
