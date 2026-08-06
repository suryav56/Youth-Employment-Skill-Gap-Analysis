package com.youthportal.skillgapportal.service;

import com.youthportal.skillgapportal.entity.Skill;
import com.youthportal.skillgapportal.entity.User;
import com.youthportal.skillgapportal.repository.SkillRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Skill saveSkill(Skill skill) {
        return skillRepository.save(skill);
    }

    public List<Skill> getSkillsByUser(User user) {
        return skillRepository.findByUser(user);
    }

    public void deleteSkill(Long id) {
        skillRepository.deleteById(id);
    }

    public Skill getSkillById(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill not found"));
    }


}