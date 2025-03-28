package com.example.Bibliotech_backend.service;

import com.example.Bibliotech_backend.model.Wishlist;
import com.example.Bibliotech_backend.model.Users;
import com.example.Bibliotech_backend.model.Book;
import com.example.Bibliotech_backend.repository.WishlistRepository;
import com.example.Bibliotech_backend.repository.UserRepository;
import com.example.Bibliotech_backend.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    private static final Logger logger = LoggerFactory.getLogger(WishlistService.class);

    private final IdGeneratorService idGeneratorService;

    public WishlistService(IdGeneratorService idGeneratorService) {
        this.idGeneratorService = idGeneratorService;
    }

    /**
     * Lấy danh sách sách trong wishlist của người dùng
     */
    @Transactional(readOnly = true)
    public List<Wishlist> getUserWishlist(Integer userId) {
        logger.debug("Getting wishlist for user ID: {}", userId);

        // Kiểm tra xem người dùng có tồn tại không
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        List<Wishlist> wishlist = wishlistRepository.getUserWishlist(userId);
        if (wishlist.isEmpty()) {
            logger.debug("No wishlist items found for user ID: {}", userId);
        }
        return wishlist;
    }

    /**
     * Thêm sách vào wishlist của người dùng
     */
    @Transactional
    public Wishlist addBookToWishlist(Integer userId, Integer bookId) {
        logger.debug("Adding book ID: {} to wishlist for user ID: {}", bookId, userId);

        // Kiểm tra người dùng
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // Kiểm tra sách
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + bookId));

        // Kiểm tra xem sách đã có trong wishlist chưa
        Wishlist existingEntry = wishlistRepository.findByUserIdAndBookId(userId, bookId);
        if (existingEntry != null) {
            throw new IllegalStateException("Book ID: " + bookId + " is already in the wishlist of user ID: " + userId);
        }

        // Tạo mới một mục wishlist
        Wishlist wishlistEntry = new Wishlist();
        wishlistEntry.setWishlistId(idGeneratorService.generateWishlistId());
        wishlistEntry.setUserId(userId);
        wishlistEntry.setBookId(bookId);
        wishlistEntry.setAddedDate(Timestamp.valueOf(LocalDateTime.now()));

        // Lưu vào cơ sở dữ liệu
        Wishlist savedWishlist = wishlistRepository.save(wishlistEntry);
        logger.info("Book ID: {} added to wishlist for user ID: {}", bookId, userId);
        return savedWishlist;
    }

    /**
     * Xóa sách khỏi wishlist của người dùng
     */
    @Transactional
    public boolean removeBookFromWishlist(Integer userId, Integer bookId) {
        logger.debug("Removing book ID: {} from wishlist for user ID: {}", bookId, userId);

        // Kiểm tra người dùng
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // Kiểm tra sách
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + bookId));

        // Xóa mục wishlist và kiểm tra kết quả
        int deletedCount = wishlistRepository.deleteByUserIdAndBookId(userId, bookId);
        if (deletedCount > 0) {
            logger.info("Book ID: {} removed from wishlist for user ID: {}", bookId, userId);
            return true;
        } else {
            logger.warn("Book ID: {} not found in wishlist for user ID: {}", bookId, userId);
            return false;
        }
    }
}