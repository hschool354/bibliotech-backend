package com.example.Bibliotech_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "UserCultivation")
public class UserCultivation {

    @Id
    @Column(name = "user_id")
    private Integer userId;

    @ManyToOne
    @JoinColumn(name = "current_level_id")
    private CultivationLevel currentLevel;

    @Column(name = "total_books_read", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer totalBooksRead;

    @Column(name = "total_reading_time", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer totalReadingTime;

    @Column(name = "cultivation_points", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer cultivationPoints;

    @Column(name = "last_level_up_date")
    private LocalDateTime lastLevelUpDate;

    public UserCultivation() {
    }

    public UserCultivation(Integer userId, CultivationLevel currentLevel, Integer totalBooksRead, Integer totalReadingTime, Integer cultivationPoints, LocalDateTime lastLevelUpDate) {
        this.userId = userId;
        this.currentLevel = currentLevel;
        this.totalBooksRead = totalBooksRead;
        this.totalReadingTime = totalReadingTime;
        this.cultivationPoints = cultivationPoints;
        this.lastLevelUpDate = lastLevelUpDate;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public CultivationLevel getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(CultivationLevel currentLevel) {
        this.currentLevel = currentLevel;
    }

    public Integer getTotalBooksRead() {
        return totalBooksRead;
    }

    public void setTotalBooksRead(Integer totalBooksRead) {
        this.totalBooksRead = totalBooksRead;
    }

    public Integer getTotalReadingTime() {
        return totalReadingTime;
    }

    public void setTotalReadingTime(Integer totalReadingTime) {
        this.totalReadingTime = totalReadingTime;
    }

    public Integer getCultivationPoints() {
        return cultivationPoints;
    }

    public void setCultivationPoints(Integer cultivationPoints) {
        this.cultivationPoints = cultivationPoints;
    }

    public LocalDateTime getLastLevelUpDate() {
        return lastLevelUpDate;
    }

    public void setLastLevelUpDate(LocalDateTime lastLevelUpDate) {
        this.lastLevelUpDate = lastLevelUpDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        UserCultivation other = (UserCultivation) obj;

        // Kiểm tra userId (primary key)
        if (userId == null) {
            if (other.userId != null)
                return false;
        } else if (!userId.equals(other.userId))
            return false;

        // Kiểm tra các thuộc tính còn lại
        if (currentLevel == null) {
            if (other.currentLevel != null)
                return false;
        } else if (!currentLevel.equals(other.currentLevel))
            return false;

        if (totalBooksRead == null) {
            if (other.totalBooksRead != null)
                return false;
        } else if (!totalBooksRead.equals(other.totalBooksRead))
            return false;

        if (totalReadingTime == null) {
            if (other.totalReadingTime != null)
                return false;
        } else if (!totalReadingTime.equals(other.totalReadingTime))
            return false;

        if (cultivationPoints == null) {
            if (other.cultivationPoints != null)
                return false;
        } else if (!cultivationPoints.equals(other.cultivationPoints))
            return false;

        if (lastLevelUpDate == null) {
            if (other.lastLevelUpDate != null)
                return false;
        } else if (!lastLevelUpDate.equals(other.lastLevelUpDate))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((userId == null) ? 0 : userId.hashCode());
        result = prime * result + ((currentLevel == null) ? 0 : currentLevel.hashCode());
        result = prime * result + ((totalBooksRead == null) ? 0 : totalBooksRead.hashCode());
        result = prime * result + ((totalReadingTime == null) ? 0 : totalReadingTime.hashCode());
        result = prime * result + ((cultivationPoints == null) ? 0 : cultivationPoints.hashCode());
        result = prime * result + ((lastLevelUpDate == null) ? 0 : lastLevelUpDate.hashCode());
        return result;
    }
}