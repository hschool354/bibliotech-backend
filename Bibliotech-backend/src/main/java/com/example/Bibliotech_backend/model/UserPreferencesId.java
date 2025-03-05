package com.example.Bibliotech_backend.model;

import java.io.Serializable;
import java.util.Objects;

public class UserPreferencesId implements Serializable {
    private Integer userId;
    private Integer preferredCategoryId;

    // Default constructor
    public UserPreferencesId() {}

    // Constructor
    public UserPreferencesId(Integer userId, Integer preferredCategoryId) {
        this.userId = userId;
        this.preferredCategoryId = preferredCategoryId;
    }

    // Implement equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPreferencesId that = (UserPreferencesId) o;
        return userId.equals(that.userId) && preferredCategoryId.equals(that.preferredCategoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, preferredCategoryId);
    }
}