package com.example.Bibliotech_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "categories")
public class Category {
    @Id
    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "category_name", nullable = false)
    private String categoryName;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

    @OneToMany(mappedBy = "category")
    private Set<BookCategory> bookCategories = new HashSet<>();

    // Getters and setters
    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return categoryName;
    }

    public void setName(String name) {
        this.categoryName = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Category getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(Category parentCategory) {
        this.parentCategory = parentCategory;
    }

    public Set<BookCategory> getBookCategories() {
        return bookCategories;
    }

    public void setBookCategories(Set<BookCategory> bookCategories) {
        this.bookCategories = bookCategories;
    }

    // Helper methods
    public Set<Book> getBooks() {
        return bookCategories.stream()
                .map(BookCategory::getBook)
                .collect(Collectors.toSet());
    }

    public void addBook(Book book) {
        BookCategory bookCategory = new BookCategory();
        bookCategory.setCategory(this);
        bookCategory.setBook(book);
        bookCategory.setCategoryId(this.categoryId);
        bookCategory.setBookId(book.getBookId());
        this.bookCategories.add(bookCategory);
    }

    public void removeBook(Book book) {
        bookCategories.removeIf(bc -> bc.getBook().equals(book));
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}