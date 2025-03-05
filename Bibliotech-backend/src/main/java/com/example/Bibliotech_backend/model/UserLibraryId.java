package com.example.Bibliotech_backend.model;

import java.io.Serializable;
import java.util.Objects;

public class UserLibraryId implements Serializable {
    private Integer userId;
    private Integer bookId;

    // Default constructor
    public UserLibraryId() {}

    // Parameterized constructor
    public UserLibraryId(Integer userId, Integer bookId) {
        this.userId = userId;
        this.bookId = bookId;
    }

    // Getters and setters
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

    // hashCode and equals methods
    @Override
    public int hashCode() {
        return Objects.hash(userId, bookId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserLibraryId that = (UserLibraryId) obj;
        return Objects.equals(userId, that.userId) && Objects.equals(bookId, that.bookId);
    }
}