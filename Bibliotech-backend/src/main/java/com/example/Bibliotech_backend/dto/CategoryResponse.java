package com.example.Bibliotech_backend.dto;

public class CategoryResponse {
    private Integer categoryId;
    private String categoryName;
    private String description;
    private CategoryResponse parentCategory;

    // Getters and setters
    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CategoryResponse getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(CategoryResponse parentCategory) {
        this.parentCategory = parentCategory;
    }
}
