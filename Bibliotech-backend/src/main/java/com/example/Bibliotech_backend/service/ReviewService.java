package com.example.Bibliotech_backend.service;

import com.example.Bibliotech_backend.dto.ReviewRequest;
import com.example.Bibliotech_backend.model.Book;
import com.example.Bibliotech_backend.model.Review;
import com.example.Bibliotech_backend.repository.BookRepository;
import com.example.Bibliotech_backend.repository.ReviewRepository;
import com.example.Bibliotech_backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

    @Autowired
    public ReviewService(ReviewRepository reviewRepository,
                         BookRepository bookRepository,
                         UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    /**
     * Lấy danh sách đánh giá của một cuốn sách
     */
    public List<Review> getBookReviews(Integer bookId, int page, int size) {
        logger.debug("Getting reviews for book with ID: {}", bookId);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "reviewDate"));

        return reviewRepository.findByBookId(bookId, pageable).getContent();
    }

    /**
     * Lấy đánh giá của người dùng cho một cuốn sách
     */
    public Review getUserReviewForBook(Integer bookId, Integer userId) {
        logger.debug("Getting review for book ID: {} by user ID: {}", bookId, userId);

        return reviewRepository.findByBookIdAndUserId(bookId, userId).orElse(null);
    }

    /**
     * Thêm đánh giá mới hoặc cập nhật đánh giá hiện có
     */
    @Transactional
    public Review addReview(Integer bookId, Integer userId, ReviewRequest reviewRequest) {
        logger.debug("Adding review for book with ID: {} by user with ID: {}", bookId, userId);

        if (!bookRepository.existsById(bookId)) {
            logger.error("Book with ID {} does not exist", bookId);
            throw new IllegalArgumentException("Sách không tồn tại");
        }

        if (!userRepository.existsById(userId)) {
            logger.error("User with ID {} does not exist", userId);
            throw new IllegalArgumentException("Người dùng không tồn tại");
        }

        Optional<Review> existingReview = reviewRepository.findByBookIdAndUserId(bookId, userId);

        Review review;

        if (existingReview.isPresent()) {
            review = existingReview.get();
            review.setRating(reviewRequest.getRating());

            // Fix getReviewText method issue
            try {
                // Assuming reviewRequest has a getReviewText method
                String reviewText = reviewRequest.getComment(); // Assuming the method is called getComment()
                review.setReviewText(reviewText);
            } catch (Exception e) {
                logger.error("Error getting review text", e);
                // Handle field/method not found or other issues
                // If getReviewText doesn't exist, try getComment or other likely methods
//                if (reviewRequest.getClass().getDeclaredMethod("getComment") != null) {
//                    review.setReviewText((String) reviewRequest.getClass().getDeclaredMethod("getComment").invoke(reviewRequest));
//                }
                review.setReviewText(reviewRequest.getComment());

            }

            review.setReviewDate(Timestamp.from(Instant.now()));

            logger.debug("Updating existing review with ID: {}", review.getReviewId());
        } else {
            review = new Review();
            review.setBookId(bookId);
            review.setUserId(userId);
            review.setRating(reviewRequest.getRating());

            // Fix getReviewText method issue
            try {
                // Assuming reviewRequest has a getReviewText method
                String reviewText = reviewRequest.getComment(); // Assuming the method is called getComment()
                review.setReviewText(reviewText);
            } catch (Exception e) {
                logger.error("Error getting review text", e);
                // Handle field/method not found or other issues
                // If getReviewText doesn't exist, try getComment or other likely methods
//                if (reviewRequest.getClass().getDeclaredMethod("getComment") != null) {
//                    review.setReviewText((String) reviewRequest.getClass().getDeclaredMethod("getComment").invoke(reviewRequest));
//                }

                review.setReviewText(reviewRequest.getComment());

            }

            review.setReviewDate(Timestamp.from(Instant.now()));

            logger.debug("Creating new review for book ID: {} by user ID: {}", bookId, userId);
        }

        Review savedReview = reviewRepository.save(review);

        updateBookRatings(bookId);

        return savedReview;
    }

    /**
     * Xóa đánh giá
     */
    @Transactional
    public boolean deleteReview(Integer reviewId, Integer userId) {
        logger.debug("Deleting review with ID: {} by user ID: {}", reviewId, userId);

        Optional<Review> reviewOptional = reviewRepository.findById(reviewId);

        if (reviewOptional.isPresent()) {
            Review review = reviewOptional.get();

            if (!review.getUserId().equals(userId)) {
                logger.error("User with ID {} is not authorized to delete review with ID {}", userId, reviewId);
                return false;
            }

            Integer bookId = review.getBookId();

            reviewRepository.deleteById(reviewId);

            updateBookRatings(bookId);

            return true;
        }

        return false;
    }

    /**
     * Lấy danh sách đánh giá của người dùng
     */
    public List<Review> getUserReviews(Integer userId, int page, int size) {
        logger.debug("Getting reviews by user with ID: {}", userId);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "reviewDate"));

        return reviewRepository.findByUserId(userId, pageable).getContent();
    }

    /**
     * Cập nhật trung bình đánh giá và số lượng đánh giá của sách
     */
    private void updateBookRatings(Integer bookId) {
        logger.debug("Updating ratings for book with ID: {}", bookId);

        Double averageRating = reviewRepository.calculateAverageRatingByBookId(bookId);
        Integer ratingCount = reviewRepository.countByBookId(bookId);

        if (averageRating == null) {
            averageRating = 0.0;
        }

        Optional<Book> bookOptional = bookRepository.findById(bookId);

        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();

            // Fix Double to BigDecimal conversion if needed
            try {
                // If setAverageRating accepts BigDecimal
                book.setAverageRating(new BigDecimal(averageRating));
            } catch (Exception e) {
                // If setAverageRating accepts Double
                try {
                    book.getClass().getDeclaredMethod("setAverageRating", Double.class)
                            .invoke(book, averageRating);
                } catch (Exception ex) {
                    logger.error("Error setting average rating", ex);
                }
            }

            book.setRatingCount(ratingCount);

            bookRepository.save(book);

            logger.debug("Updated book ID: {} with average rating: {} and rating count: {}",
                    bookId, averageRating, ratingCount);
        }
    }
}