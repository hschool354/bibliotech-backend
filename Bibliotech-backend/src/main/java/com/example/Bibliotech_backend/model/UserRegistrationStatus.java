package com.example.Bibliotech_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "UserRegistrationStatus")
public class UserRegistrationStatus {
    @Id
    private Integer userId;

    @Column(name = "is_profile_completed", nullable = false)
    private boolean isProfileCompleted = false;

    @Column(name = "profile_completion_date")
    private LocalDateTime profileCompletionDate;

    // Getters and setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public boolean isProfileCompleted() {
        return isProfileCompleted;
    }

    public void setProfileCompleted(boolean profileCompleted) {
        isProfileCompleted = profileCompleted;
    }

    public LocalDateTime getProfileCompletionDate() {
        return profileCompletionDate;
    }

    public void setProfileCompletionDate(LocalDateTime profileCompletionDate) {
        this.profileCompletionDate = profileCompletionDate;
    }
}
