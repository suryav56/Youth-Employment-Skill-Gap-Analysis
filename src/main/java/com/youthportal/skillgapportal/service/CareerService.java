package com.youthportal.skillgapportal.service;

import com.youthportal.skillgapportal.entity.Career;
import com.youthportal.skillgapportal.repository.CareerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CareerService {

    private final CareerRepository careerRepository;

    public CareerService(CareerRepository careerRepository) {
        this.careerRepository = careerRepository;
    }

    public List<Career> getAllCareers() {
        return careerRepository.findAll();
    }

    public Career saveCareer(Career career) {
        return careerRepository.save(career);
    }

    public Career getCareerById(Long id) {
        return careerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Career not found"));
    }

    public void save(Career career) {
        careerRepository.save(career);
    }
}