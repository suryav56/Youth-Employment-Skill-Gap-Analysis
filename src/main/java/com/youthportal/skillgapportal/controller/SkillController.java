package com.youthportal.skillgapportal.controller;

import com.youthportal.skillgapportal.entity.Skill;
import com.youthportal.skillgapportal.service.SkillService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.youthportal.skillgapportal.entity.User;
import com.youthportal.skillgapportal.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class SkillController {

    private final SkillService skillService;
    private final UserRepository userRepository;

    public SkillController(SkillService skillService,
                           UserRepository userRepository) {
        this.skillService = skillService;
        this.userRepository = userRepository;
    }

    @GetMapping("/skills")
    public String skills(Authentication authentication,
                         Model model) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("skill", new Skill());
        model.addAttribute("skills", skillService.getSkillsByUser(user));

        return "skills";
    }



    @PostMapping("/skills")
    public String saveSkill(@ModelAttribute Skill skill,
                            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        skill.setUser(user);

        skillService.saveSkill(skill);

        return "redirect:/skills";
    }

    @GetMapping("/skills/edit/{id}")
    public String editSkill(@PathVariable Long id, Model model) {

        Skill skill = skillService.getSkillById(id);

        model.addAttribute("skill", skill);

        return "edit-skill";
    }

    @PostMapping("/skills/update")
    public String updateSkill(@ModelAttribute Skill skill,
                              Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        skill.setUser(user);

        skillService.saveSkill(skill);

        return "redirect:/skills";
    }

    @GetMapping("/skills/delete/{id}")
    public String deleteSkill(@PathVariable Long id) {

        skillService.deleteSkill(id);

        return "redirect:/skills";
    }

}