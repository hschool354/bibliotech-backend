package com.example.Bibliotech_backend.dto;

import java.math.BigDecimal;

public class BookSaleInfoDTO {
    private Integer bookId;
    private String title;
    private String coverImageUrl;
    private BigDecimal originalPrice;
    private BigDecimal discountedPrice;

    public BookSaleInfoDTO(Integer bookId, String title, String coverImageUrl, BigDecimal originalPrice, BigDecimal discountedPrice) {
        this.bookId = bookId;
        this.title = title;
        this.coverImageUrl = coverImageUrl;
        this.originalPrice = originalPrice;
        this.discountedPrice = discountedPrice;
    }

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

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
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
}
