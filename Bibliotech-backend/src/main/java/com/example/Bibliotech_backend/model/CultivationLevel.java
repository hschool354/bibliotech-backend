package com.example.Bibliotech_backend.model;


import jakarta.persistence.*;

@Entity
@Table(name = "CultivationLevels")
public class CultivationLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "level_id")
    private Integer levelId;

    @Column(name = "level_name",nullable = false, length = 50)
    private String levelName;

    @Column(name = "level_description",columnDefinition = "TEXT")
    private String levelDescription;

    @Column(name = "books_required",nullable = false)
    private Integer booksRequired;

    @Column(name = "reading_time_required",nullable = false)
    private Integer readingTimeRequired;

    @Column(name = "icon_url",nullable = false)
    private String iconUrl;

    public CultivationLevel() {
    }

    public CultivationLevel(Integer levelId, String levelName, String levelDescription, Integer booksRequired, Integer readingTimeRequired, String iconUrl) {
        this.levelId = levelId;
        this.levelName = levelName;
        this.levelDescription = levelDescription;
        this.booksRequired = booksRequired;
        this.readingTimeRequired = readingTimeRequired;
        this.iconUrl = iconUrl;
    }

    public Integer getLevelId() {
        return levelId;
    }

    public void setLevelId(Integer levelId) {
        this.levelId = levelId;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getLevelDescription() {
        return levelDescription;
    }

    public void setLevelDescription(String levelDescription) {
        this.levelDescription = levelDescription;
    }

    public Integer getBooksRequired() {
        return booksRequired;
    }

    public void setBooksRequired(Integer booksRequired) {
        this.booksRequired = booksRequired;
    }

    public Integer getReadingTimeRequired() {
        return readingTimeRequired;
    }

    public void setReadingTimeRequired(Integer readingTimeRequired) {
        this.readingTimeRequired = readingTimeRequired;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}