package com.youthportal.skillgapportal.service;

import com.youthportal.skillgapportal.entity.Career;
import com.youthportal.skillgapportal.entity.Skill;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class SkillGapService {

    public Set<Skill> findMissingSkills(Career career,
                                        Set<Skill> userSkills) {

        Set<Skill> missingSkills = new HashSet<>();

        for (Skill requiredSkill : career.getRequiredSkills()) {

            boolean found = false;

            for (Skill userSkill : userSkills) {

                if (requiredSkill.getName().equalsIgnoreCase(userSkill.getName())) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                missingSkills.add(requiredSkill);
            }
        }

        return missingSkills;
    }
}