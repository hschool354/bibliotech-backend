package com.example.Bibliotech_backend.model;

import java.io.Serializable;
import java.util.Objects;

public class UserAchievementId implements Serializable {
    private Integer userId;
    private AchievementType achievementType;

    // Constructors
    public UserAchievementId() {
    }

    public UserAchievementId(Integer userId, AchievementType achievementType) {
        this.userId = userId;
        this.achievementType = achievementType;
    }

    // Getters and Setters
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

    // equals method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAchievementId that = (UserAchievementId) o;
        return Objects.equals(userId, that.userId) &&
                achievementType == that.achievementType;
    }

    // hashCode method
    @Override
    public int hashCode() {
        return Objects.hash(userId, achievementType);
    }
}