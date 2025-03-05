package com.example.Bibliotech_backend.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity class đại diện cho bảng Books trong cơ sở dữ liệu
 */
@Entity
@Table(name = "Books")
public class Book {
    @Id
    @Column(name = "book_id")
    private Integer bookId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "isbn", unique = true)
    private String isbn;

    @Column(name = "original_price", nullable = false)
    private BigDecimal originalPrice;

    @Column(name = "discounted_price")
    private BigDecimal discountedPrice;

    @Column(name = "publication_year")
    private Integer publicationYear;

    @Column(name = "language", nullable = false)
    @Enumerated(EnumType.STRING)
    private Language language;

    @Column(name = "page_count")
    private Integer pageCount;

    @Column(name = "average_rating")
    private BigDecimal averageRating;

    @Column(name = "rating_count")
    private Integer ratingCount;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @ManyToOne
    @JoinColumn(name = "deal_id")
    private Deal deal;

    @Column(name = "reading_difficulty")
    @Enumerated(EnumType.STRING)
    private ReadingDifficulty readingDifficulty;

    @Column(name = "estimated_reading_time")
    private Integer estimatedReadingTime;

    @Column(name = "content_rating")
    @Enumerated(EnumType.STRING)
    private ContentRating contentRating;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BookCategory> bookCategories = new HashSet<>();

    // Enum for Language
    public enum Language {
        Vietnamese, English, French, German, Spanish, Chinese, Japanese, Korean, Other
    }

    // Enum for ReadingDifficulty
    public enum ReadingDifficulty {
        EASY, MEDIUM, HARD
    }

    // Enum for ContentRating
    public enum ContentRating {
        EVERYONE, TEEN, MATURE
    }

    // Getters and Setters
    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

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

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public BigDecimal getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
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

    public Deal getDeal() {
        return deal;
    }

    public void setDeal(Deal deal) {
        this.deal = deal;
    }

    public ReadingDifficulty getReadingDifficulty() {
        return readingDifficulty;
    }

    public void setReadingDifficulty(ReadingDifficulty readingDifficulty) {
        this.readingDifficulty = readingDifficulty;
    }

    public Integer getEstimatedReadingTime() {
        return estimatedReadingTime;
    }

    public void setEstimatedReadingTime(Integer estimatedReadingTime) {
        this.estimatedReadingTime = estimatedReadingTime;
    }

    public ContentRating getContentRating() {
        return contentRating;
    }

    public void setContentRating(ContentRating contentRating) {
        this.contentRating = contentRating;
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

//    public Set<Category> getCategories() {
//        return categories;
//    }
//
//    public void setCategories(Set<Category> categories) {
//        this.categories = categories;
//    }

    // Add this method to your Book class
    public Set<BookCategory> getBookCategories() {
        return bookCategories;
    }

    public void setBookCategories(Set<BookCategory> bookCategories) {
        this.bookCategories = bookCategories;
    }

    // Helper
    public Set<Category> getCategories() {
        return bookCategories.stream()
                .map(BookCategory::getCategory)
                .collect(java.util.stream.Collectors.toSet());
    }

    public void addCategory(Category category) {
        BookCategory bookCategory = new BookCategory();
        bookCategory.setBook(this);
        bookCategory.setCategory(category);
        bookCategory.setBookId(this.bookId);
        bookCategory.setCategoryId(category.getCategoryId());
        this.bookCategories.add(bookCategory);
    }

    public void removeCategory(Category category) {
        bookCategories.removeIf(bc -> bc.getCategory().equals(category));
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
