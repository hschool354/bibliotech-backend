package com.example.Bibliotech_backend.repository;

import com.example.Bibliotech_backend.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    /**
     * Tìm đánh giá theo ID sách với phân trang
     */
    Page<Review> findByBookId(Integer bookId, Pageable pageable);

    /**
     * Tìm đánh giá theo ID người dùng với phân trang
     */
    Page<Review> findByUserId(Integer userId, Pageable pageable);

    /**
     * Tìm đánh giá của một người dùng cho một cuốn sách cụ thể
     */
    Optional<Review> findByBookIdAndUserId(Integer bookId, Integer userId);

    /**
     * Đếm số lượng đánh giá của một cuốn sách
     */
    Integer countByBookId(Integer bookId);

    /**
     * Tính điểm đánh giá trung bình cho một cuốn sách
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.bookId = :bookId")
    Double calculateAverageRatingByBookId(@Param("bookId") Integer bookId);
}