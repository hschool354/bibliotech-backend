package com.example.Bibliotech_backend.repository;

import com.example.Bibliotech_backend.model.UserAchievement;
import com.example.Bibliotech_backend.model.UserAchievementId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, UserAchievementId> {
    List<UserAchievement> findByUserId(Integer userId);
}