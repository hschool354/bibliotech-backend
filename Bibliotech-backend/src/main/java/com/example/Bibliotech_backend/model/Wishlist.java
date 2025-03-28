package com.example.Bibliotech_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "Wishlist")
public class Wishlist {

    @Id
    @Column(name = "wishlist_id")
    private int wishlistId;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "book_id")
    private int bookId;

    @Column(name = "added_date", nullable = false)
    private Timestamp addedDate;

    public Wishlist() {
    }

    public Wishlist(Timestamp addedDate, int bookId, int userId, int wishlistId) {
        this.addedDate = addedDate;
        this.bookId = bookId;
        this.userId = userId;
        this.wishlistId = wishlistId;
    }

    // Getters and Setters
    public int getWishlistId() {
        return wishlistId;
    }

    public void setWishlistId(int wishlistId) {
        this.wishlistId = wishlistId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public Timestamp getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Timestamp addedDate) {
        this.addedDate = addedDate;
    }
}