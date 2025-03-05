package com.example.Bibliotech_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "UserPreferences")
@IdClass(UserPreferencesId.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UserPreferences {

    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Id
    @Column(name = "preferred_category_id")
    private Integer preferredCategoryId;

    @Column(name = "preference_weight", precision = 3, scale = 2)
    private BigDecimal preferenceWeight;

    @Column(name = "last_interaction_date")
    private LocalDateTime lastInteractionDate;

    public UserPreferences() {
    }

    public UserPreferences(Integer userId, Integer preferredCategoryId, BigDecimal preferenceWeight, LocalDateTime lastInteractionDate) {
        this.userId = userId;
        this.preferredCategoryId = preferredCategoryId;
        this.preferenceWeight = preferenceWeight;
        this.lastInteractionDate = lastInteractionDate;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getPreferredCategoryId() {
        return preferredCategoryId;
    }

    public void setPreferredCategoryId(Integer preferredCategoryId) {
        this.preferredCategoryId = preferredCategoryId;
    }

    public BigDecimal getPreferenceWeight() {
        return preferenceWeight;
    }

    public void setPreferenceWeight(BigDecimal preferenceWeight) {
        this.preferenceWeight = preferenceWeight;
    }

    public LocalDateTime getLastInteractionDate() {
        return lastInteractionDate;
    }

    public void setLastInteractionDate(LocalDateTime lastInteractionDate) {
        this.lastInteractionDate = lastInteractionDate;
    }
}