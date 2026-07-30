package com.youthportal.skillgapportal.controller;

import com.youthportal.skillgapportal.entity.User;
import com.youthportal.skillgapportal.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String registerPage(Model model) {

        model.addAttribute("user", new User());

        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user,
                               Model model) {

        String message = userService.registerUser(user);

        if (message.equals("Registration Successful")) {
            return "redirect:/login";
        }

        model.addAttribute("message", message);
        model.addAttribute("user", user);

        return "register";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

}