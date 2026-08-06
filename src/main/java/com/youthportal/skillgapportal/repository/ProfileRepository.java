package com.youthportal.skillgapportal.repository;

import com.youthportal.skillgapportal.entity.Profile;
import com.youthportal.skillgapportal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUser(User user);

}