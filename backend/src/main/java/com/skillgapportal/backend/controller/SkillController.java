package com.skillgapportal.backend.controller;

import com.skillgapportal.backend.dto.SkillDto;
import com.skillgapportal.backend.entity.Skill;
import com.skillgapportal.backend.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @GetMapping("/catalog")
    public ResponseEntity<List<Skill>> getCoreSkills() {
        return ResponseEntity.ok(skillService.getCoreSkills());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Skill>> searchCoreSkills(@RequestParam String query) {
        return ResponseEntity.ok(skillService.searchCoreSkills(query));
    }

    @GetMapping
    public ResponseEntity<List<SkillDto>> getUserSkills(Authentication authentication) {
        return ResponseEntity.ok(skillService.getUserSkills(authentication.getName()));
    }

    @PostMapping
    public ResponseEntity<SkillDto> addUserSkill(
            Authentication authentication,
            @RequestBody SkillDto skillDto
    ) {
        return ResponseEntity.ok(skillService.addUserSkill(authentication.getName(), skillDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeUserSkill(
            Authentication authentication,
            @PathVariable Long id
    ) {
        skillService.removeUserSkill(authentication.getName(), id);
        return ResponseEntity.ok().build();
    }
}
