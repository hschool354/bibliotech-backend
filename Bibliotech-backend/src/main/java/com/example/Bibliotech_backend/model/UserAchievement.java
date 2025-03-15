package com.example.Bibliotech_backend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "UserAchievements")
@IdClass(UserAchievementId.class)
public class UserAchievement {

    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "achievement_type")
    private AchievementType achievementType;

    @Column(name = "achievement_level")
    private Integer achievementLevel;

    @Column(name = "achieved_date")
    private LocalDateTime achievedDate;

    // Constructors, getters, and setters

    public UserAchievement() {
    }

    public UserAchievement(Integer userId, AchievementType achievementType, Integer achievementLevel, LocalDateTime achievedDate) {
        this.userId = userId;
        this.achievementType = achievementType;
        this.achievementLevel = achievementLevel;
        this.achievedDate = achievedDate;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public AchievementType getAchievementType() {
        return achievementType;
    }

    public void setAchievementType(AchievementType achievementType) {
        this.achievementType = achievementType;
    }

    public Integer getAchievementLevel() {
        return achievementLevel;
    }

    public void setAchievementLevel(Integer achievementLevel) {
        this.achievementLevel = achievementLevel;
    }

    public LocalDateTime getAchievedDate() {
        return achievedDate;
    }

    public void setAchievedDate(LocalDateTime achievedDate) {
        this.achievedDate = achievedDate;
    }
}