package com.youthportal.skillgapportal.controller;

import com.youthportal.skillgapportal.entity.Career;
import com.youthportal.skillgapportal.entity.Skill;
import com.youthportal.skillgapportal.repository.SkillRepository;
import com.youthportal.skillgapportal.service.CareerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Controller
public class CareerController {

    private final CareerService careerService;

    @GetMapping("/careers")
    public String careerPage(Model model) {

        model.addAttribute("career", new Career());
        model.addAttribute("careers", careerService.getAllCareers());

        return "careers";
    }

    @PostMapping("/careers")
    public String saveCareer(@ModelAttribute Career career) {

        careerService.saveCareer(career);

        return "redirect:/careers";
    }

    private final SkillRepository skillRepository;

    public CareerController(CareerService careerService,
                            SkillRepository skillRepository) {
        this.careerService = careerService;
        this.skillRepository = skillRepository;
    }

    @GetMapping("/careers/{id}/skills")
    public String assignSkillsPage(@PathVariable Long id,
                                   Model model) {

        Career career = careerService.getCareerById(id);

        model.addAttribute("career", career);
        model.addAttribute("skills", skillRepository.findAll());

        return "career-skills";
    }

    @PostMapping("/careers/{id}/skills")
    public String assignSkills(@PathVariable Long id,
                               @RequestParam(required = false) List<Long> skillIds) {

        Career career = careerService.getCareerById(id);

        career.getRequiredSkills().clear();

        if (skillIds != null) {
            List<Skill> skills = skillRepository.findAllById(skillIds);
            career.getRequiredSkills().addAll(skills);
        }

        careerService.save(career);

        return "redirect:/careers";
    }
}