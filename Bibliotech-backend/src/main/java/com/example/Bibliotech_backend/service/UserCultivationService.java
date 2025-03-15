package com.example.Bibliotech_backend.service;

import com.example.Bibliotech_backend.model.CultivationLevel;
import com.example.Bibliotech_backend.model.UserCultivation;
import com.example.Bibliotech_backend.repository.CultivationLevelRepository;
import com.example.Bibliotech_backend.repository.UserCultivationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserCultivationService {

    @Autowired
    private UserCultivationRepository userCultivationRepository;

    @Autowired
    private CultivationLevelRepository cultivationLevelRepository;

    public List<UserCultivation> getAllUserCultivations() {
        return userCultivationRepository.findAll();
    }

    public Optional<UserCultivation> getUserCultivationById(Integer id) {
        return userCultivationRepository.findById(id);
    }

    public UserCultivation saveUserCultivation(UserCultivation userCultivation) {
        // Đảm bảo userId đã được gán
        if (userCultivation.getUserId() == null) {
            throw new IllegalArgumentException("userId must not be null");
        }

        // Tải CultivationLevel từ database
        if (userCultivation.getCurrentLevel() != null &&
                userCultivation.getCurrentLevel().getLevelId() != null) {

            Integer levelId = userCultivation.getCurrentLevel().getLevelId();
            CultivationLevel existingLevel = cultivationLevelRepository.findById(levelId)
                    .orElseThrow(() -> new EntityNotFoundException("CultivationLevel not found with id: " + levelId));

            userCultivation.setCurrentLevel(existingLevel);
        }

        return userCultivationRepository.save(userCultivation);
    }

    public void deleteUserCultivation(Integer id) {
        userCultivationRepository.deleteById(id);
    }
}