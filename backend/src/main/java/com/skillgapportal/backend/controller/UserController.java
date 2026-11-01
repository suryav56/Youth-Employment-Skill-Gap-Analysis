package com.skillgapportal.backend.controller;

import com.skillgapportal.backend.dto.ProfileDto;
import com.skillgapportal.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ProfileDto> getProfile(Authentication authentication) {
        return ResponseEntity.ok(userService.getProfile(authentication.getName()));
    }

    @PutMapping("/profile")
    public ResponseEntity<ProfileDto> updateProfile(
            Authentication authentication,
            @RequestBody ProfileDto profileDto
    ) {
        return ResponseEntity.ok(userService.updateProfile(authentication.getName(), profileDto));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats(Authentication authentication) {
        return ResponseEntity.ok(userService.getDashboardStats(authentication.getName()));
    }
}
