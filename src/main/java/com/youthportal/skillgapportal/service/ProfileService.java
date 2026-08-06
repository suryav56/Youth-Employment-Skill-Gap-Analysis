package com.youthportal.skillgapportal.service;

import com.youthportal.skillgapportal.entity.Profile;
import com.youthportal.skillgapportal.entity.User;
import com.youthportal.skillgapportal.repository.ProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public Profile save(Profile profile) {
        return profileRepository.save(profile);
    }

    public Optional<Profile> getProfile(User user) {
        return profileRepository.findByUser(user);
    }
}