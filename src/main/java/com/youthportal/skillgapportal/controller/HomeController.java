package com.youthportal.skillgapportal.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Youth Employment Skill Gap Portal is Running!";
    }
}
