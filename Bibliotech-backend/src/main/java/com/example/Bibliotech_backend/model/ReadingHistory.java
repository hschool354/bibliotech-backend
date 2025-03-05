package com.example.Bibliotech_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity
@Table(name = "ReadingHistory")
public class ReadingHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Integer historyId;

    @NotNull
    @Column(name = "user_id")
    private Integer userId;

    @NotNull
    @Column(name = "book_id")
    private Integer bookId;

    @NotNull
    @Min(0)
    @Column(name = "reading_duration")
    private Integer readingDuration;

    @NotNull
    @Column(name = "session_start")
    private Timestamp sessionStart;

    @NotNull
    @Column(name = "session_end")
    private Timestamp sessionEnd;

    @NotNull
    @Min(0)
    @Column(name = "pages_read")
    private Integer pagesRead;

    // Getters and setters
    public Integer getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Integer historyId) {
        this.historyId = historyId;
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

    public Integer getReadingDuration() {
        return readingDuration;
    }

    public void setReadingDuration(Integer readingDuration) {
        this.readingDuration = readingDuration;
    }

    public Timestamp getSessionStart() {
        return sessionStart;
    }

    public void setSessionStart(Timestamp sessionStart) {
        this.sessionStart = sessionStart;
    }

    public Timestamp getSessionEnd() {
        return sessionEnd;
    }

    public void setSessionEnd(Timestamp sessionEnd) {
        this.sessionEnd = sessionEnd;
    }

    public Integer getPagesRead() {
        return pagesRead;
    }

    public void setPagesRead(Integer pagesRead) {
        this.pagesRead = pagesRead;
    }
}