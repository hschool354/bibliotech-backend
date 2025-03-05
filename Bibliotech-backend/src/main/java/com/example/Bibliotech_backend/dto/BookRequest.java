package com.example.Bibliotech_backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * DTO cho dữ liệu yêu cầu tạo/cập nhật sách
 */
public class BookRequest {
    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 255, message = "Tiêu đề không vượt quá 255 ký tự")
    private String title;

    @NotBlank(message = "Tác giả không được để trống")
    @Size(max = 255, message = "Tên tác giả không vượt quá 255 ký tự")
    private String author;

    @Size(max = 13, message = "ISBN không vượt quá 13 ký tự")
    private String isbn;

    @NotNull(message = "Giá gốc không được để trống")
    @DecimalMin(value = "0.0", message = "Giá gốc phải lớn hơn hoặc bằng 0")
    private BigDecimal originalPrice;

    private BigDecimal discountedPrice;

    @Min(value = 1600, message = "Năm xuất bản phải từ 1600 trở lên")
    private Integer publicationYear;

    @NotNull(message = "Ngôn ngữ không được để trống")
    private String language;

    @Min(value = 1, message = "Số trang phải lớn hơn 0")
    private Integer pageCount;

    private String description;

    private String coverImageUrl;

    @NotNull(message = "Số lượng tồn kho không được để trống")
    @Min(value = 0, message = "Số lượng tồn kho không được âm")
    private Integer stockQuantity;

    private Integer dealId;

    private String readingDifficulty;

    private Integer estimatedReadingTime;

    private String contentRating;

    @NotNull(message = "Danh sách danh mục không được để trống")
    private Set<Integer> categoryIds;

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public BigDecimal getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(BigDecimal discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Integer getDealId() {
        return dealId;
    }

    public void setDealId(Integer dealId) {
        this.dealId = dealId;
    }

    public String getReadingDifficulty() {
        return readingDifficulty;
    }

    public void setReadingDifficulty(String readingDifficulty) {
        this.readingDifficulty = readingDifficulty;
    }

    public Integer getEstimatedReadingTime() {
        return estimatedReadingTime;
    }

    public void setEstimatedReadingTime(Integer estimatedReadingTime) {
        this.estimatedReadingTime = estimatedReadingTime;
    }

    public String getContentRating() {
        return contentRating;
    }

    public void setContentRating(String contentRating) {
        this.contentRating = contentRating;
    }

    public Set<Integer> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(Set<Integer> categoryIds) {
        this.categoryIds = categoryIds;
    }
}