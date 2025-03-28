package com.example.Bibliotech_backend.repository;

import com.example.Bibliotech_backend.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {

    @Query("SELECT w FROM Wishlist w WHERE w.userId = :userId")
    List<Wishlist> getUserWishlist(@Param("userId") int userId);

    @Query("SELECT w FROM Wishlist w WHERE w.userId = :userId AND w.bookId = :bookId")
    Wishlist findByUserIdAndBookId(@Param("userId") int userId, @Param("bookId") int bookId);

    @Modifying
    @Query("DELETE FROM Wishlist w WHERE w.userId = :userId AND w.bookId = :bookId")
    int deleteByUserIdAndBookId(@Param("userId") int userId, @Param("bookId") int bookId);
}