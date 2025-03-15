package com.example.Bibliotech_backend.service;

import com.example.Bibliotech_backend.model.UserAchievement;
import com.example.Bibliotech_backend.repository.UserAchievementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserAchievementService {

    private final UserAchievementRepository userAchievementRepository;

    @Autowired
    public UserAchievementService(UserAchievementRepository userAchievementRepository) {
        this.userAchievementRepository = userAchievementRepository;
    }

    public List<UserAchievement> getUserAchievements(Integer userId) {
        return userAchievementRepository.findByUserId(userId);
    }

    public UserAchievement saveUserAchievement(UserAchievement achievement) {
        achievement.setAchievedDate(LocalDateTime.now());
        return userAchievementRepository.save(achievement);
    }
}