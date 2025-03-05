package com.example.Bibliotech_backend.model;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "UserLibrary")
@IdClass(UserLibraryId.class)
public class UserLibrary {

    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Id
    @Column(name = "book_id")
    private Integer bookId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "progress_percentage", nullable = false)
    private Integer progressPercentage;

    @Column(name = "last_read_date")
    private Timestamp lastReadDate;

    @Column(name = "reading_streak", nullable = false)
    private Integer readingStreak;

    @Column(name = "last_streak_date")
    private Date lastStreakDate;

    @Column(name = "total_reading_time", nullable = false)
    private Integer totalReadingTime;

    @Column(name = "bookmarks", columnDefinition = "json")
    private String bookmarks;

    @Column(name = "notes")
    private String notes;

    // Getters and setters
    public UserLibrary() {
    }

    public UserLibrary(Integer userId, Integer bookId, Status status, Integer progressPercentage, Timestamp lastReadDate, Integer readingStreak, Date lastStreakDate, Integer totalReadingTime, String bookmarks, String notes) {
        this.userId = userId;
        this.bookId = bookId;
        this.status = status;
        this.progressPercentage = progressPercentage;
        this.lastReadDate = lastReadDate;
        this.readingStreak = readingStreak;
        this.lastStreakDate = lastStreakDate;
        this.totalReadingTime = totalReadingTime;
        this.bookmarks = bookmarks;
        this.notes = notes;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(Integer progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public Timestamp getLastReadDate() {
        return lastReadDate;
    }

    public void setLastReadDate(Timestamp lastReadDate) {
        this.lastReadDate = lastReadDate;
    }

    public Integer getReadingStreak() {
        return readingStreak;
    }

    public void setReadingStreak(Integer readingStreak) {
        this.readingStreak = readingStreak;
    }

    public Date getLastStreakDate() {
        return lastStreakDate;
    }

    public void setLastStreakDate(Date lastStreakDate) {
        this.lastStreakDate = lastStreakDate;
    }

    public Integer getTotalReadingTime() {
        return totalReadingTime;
    }

    public void setTotalReadingTime(Integer totalReadingTime) {
        this.totalReadingTime = totalReadingTime;
    }

    public String getBookmarks() {
        return bookmarks;
    }

    public void setBookmarks(String bookmarks) {
        this.bookmarks = bookmarks;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public enum Status {
        CURRENTLY_READING,
        NEXT_UP,
        FINISHED,
        ON_HOLD
    }
}