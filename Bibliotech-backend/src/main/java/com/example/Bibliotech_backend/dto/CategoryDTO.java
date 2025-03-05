package com.example.Bibliotech_backend.dto;

public class CategoryDTO {
    private Integer categoryId;
    private String categoryName;
    private String description;
    private Integer parentCategoryId;

    // Constructors
    public CategoryDTO() {}

    public CategoryDTO(Integer categoryId, String categoryName, String description, Integer parentCategoryId) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.description = description;
        this.parentCategoryId = parentCategoryId;
    }

    // Getters and Setters
    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String name) {
        this.categoryName = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(Integer parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }
}