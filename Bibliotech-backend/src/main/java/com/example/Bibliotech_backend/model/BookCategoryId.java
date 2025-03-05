package com.example.Bibliotech_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data

public class BookCategoryId implements Serializable {

    private Integer bookId;
    private Integer categoryId;

    // Default constructor
    public BookCategoryId() {
    }

    public BookCategoryId(Integer bookId, Integer categoryId) {
        this.bookId = bookId;
        this.categoryId = categoryId;
    }

    // equals and hashCode methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookCategoryId that = (BookCategoryId) o;
        return Objects.equals(bookId, that.bookId) &&
                Objects.equals(categoryId, that.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId, categoryId);
    }
}