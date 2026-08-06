package com.youthportal.skillgapportal.controller;

import com.youthportal.skillgapportal.entity.Profile;
import com.youthportal.skillgapportal.service.ProfileService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/profile")
    public String profile(Model model) {

        model.addAttribute("profile", new Profile());

        return "profile";
    }

    @PostMapping("/profile")
    public String saveProfile(@ModelAttribute Profile profile,
                              Model model) {

        profileService.save(profile);

        model.addAttribute("profile", new Profile());
        model.addAttribute("message", "Profile saved successfully!");

        return "profile";
    }
}