package com.youthportal.skillgapportal.service;

import com.youthportal.skillgapportal.entity.Career;
import com.youthportal.skillgapportal.entity.Skill;
import com.youthportal.skillgapportal.entity.User;
import com.youthportal.skillgapportal.repository.CareerRepository;
import com.youthportal.skillgapportal.repository.SkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SkillService {

    private final SkillRepository skillRepository;
    private final CareerRepository careerRepository;

    public SkillService(SkillRepository skillRepository,
                        CareerRepository careerRepository) {
        this.skillRepository = skillRepository;
        this.careerRepository = careerRepository;
    }

    public Skill saveSkill(Skill skill) {
        return skillRepository.save(skill);
    }

    public List<Skill> getSkillsByUser(User user) {
        return skillRepository.findByUser(user);
    }

    @Transactional
    public void deleteSkill(Long id) {

        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        List<Career> careers = careerRepository.findAll();

        for (Career career : careers) {
            career.getRequiredSkills().remove(skill);
        }

        careerRepository.saveAll(careers);

        skillRepository.delete(skill);
    }

    public Skill getSkillById(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill not found"));
    }
}