package com.example.Bibliotech_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "UserRegistrationStatus")
public class UserRegistrationStatus {

    @Id
    @Column(name = "user_id")  // This should match exactly what's in your database
    private Integer userId;

    @Column(name = "is_profile_completed", nullable = false)
    private boolean isProfileCompleted = false;

    @Column(name = "profile_completion_date")
    private LocalDateTime profileCompletionDate;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private Users user;

    public UserRegistrationStatus() {
    }

    public UserRegistrationStatus(Integer userId, boolean isProfileCompleted, LocalDateTime profileCompletionDate) {
        this.userId = userId;
        this.isProfileCompleted = isProfileCompleted;
        this.profileCompletionDate = profileCompletionDate;
    }

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

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }
}