package com.example.Bibliotech_backend.controller;

import com.example.Bibliotech_backend.dto.*;
import com.example.Bibliotech_backend.exception.ErrorResponse;
import com.example.Bibliotech_backend.model.Book;
import com.example.Bibliotech_backend.model.Category;
import com.example.Bibliotech_backend.model.Review;
import com.example.Bibliotech_backend.model.Users;
import com.example.Bibliotech_backend.service.*;
import com.example.Bibliotech_backend.util.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * BookController định nghĩa các API liên quan đến sách
 */
@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;
    private final CategoryService categoryService;
    private final ReviewService reviewService;
    private final AuthService authService;
    private final TokenUtils tokenUtils;
    private final CloudinaryService cloudinaryService;


    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    /**
     * Constructor khởi tạo BookController với các service cần thiết.
     *
     * @param bookService Service xử lý dữ liệu sách
     * @param categoryService Service xử lý dữ liệu danh mục
     * @param reviewService Service xử lý dữ liệu đánh giá
     * @param authService Service xử lý thông tin người dùng
     * @param tokenUtils Tiện ích xử lý token
     */
    public BookController(BookService bookService,
                          CategoryService categoryService,
                          ReviewService reviewService,
                          AuthService authService,
                          TokenUtils tokenUtils,
                          CloudinaryService cloudinaryService) {
        this.bookService = bookService;
        this.categoryService = categoryService;
        this.reviewService = reviewService;
        this.authService = authService;
        this.tokenUtils = tokenUtils;
        this.cloudinaryService = cloudinaryService;
    }

    /**
     * API lấy danh sách sách với các bộ lọc
     *
     * @param title Tiêu đề sách (tùy chọn)
     * @param author Tác giả (tùy chọn)
     * @param category Danh mục (tùy chọn)
     * @param language Ngôn ngữ (tùy chọn)
     * @param minPrice Giá tối thiểu (tùy chọn)
     * @param maxPrice Giá tối đa (tùy chọn)
     * @param hasDiscount Có khuyến mãi hay không (tùy chọn)
     * @param page Số trang (mặc định là 0)
     * @param size Kích thước trang (mặc định là 10)
     * @param sortBy Trường cần sắp xếp (mặc định là title)
     * @param direction Hướng sắp xếp (mặc định là asc)
     * @return Danh sách sách đáp ứng các tiêu chí lọc
     */
    @GetMapping
    public ResponseEntity<?> getAllBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Integer category,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean hasDiscount,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        logger.info("API called: getAllBooks");
        try {
            logger.debug("Getting books with filters - title: {}, author: {}, category: {}, language: {}",
                    title, author, category, language);

            List<BookResponse> books = bookService.getAllBooks(
                    title, author, category, language, minPrice, maxPrice, hasDiscount,
                    page, size, sortBy, direction);

            return ResponseEntity.ok(books);
        } catch (Exception e) {
            logger.error("Error getting books", e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Không thể lấy danh sách sách: " + e.getMessage()));
        }
    }

    /**
     * API lấy thông tin chi tiết của một cuốn sách
     *
     * @param bookId ID của sách cần lấy thông tin
     * @return Thông tin chi tiết của sách
     */
    @GetMapping("/{bookId}")
    public ResponseEntity<?> getBookById(@PathVariable Integer bookId) {
        logger.info("API called: get Book By ID");
        try {
            logger.debug("Getting book with ID: {}", bookId);

            BookResponse book = bookService.getBookById(bookId);
            if (book == null) {
                logger.error("Book not found with ID: {}", bookId);
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(book);
        } catch (Exception e) {
            logger.error("Error getting book with ID: {}", bookId, e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Không thể lấy thông tin sách: " + e.getMessage()));
        }
    }

    /**
     * API thêm mới một cuốn sách (chỉ admin mới có quyền)
     *
     * @param bookRequest Thông tin sách cần thêm
     * @return Thông tin sách đã được thêm
     */
    @PostMapping(produces = "application/json")
    public ResponseEntity<?> addBook(@Valid @RequestBody BookRequest bookRequest) {
        logger.info("API called: addBook");
        try {
            String username = tokenUtils.getCurrentUsername();
            Users currentUser = authService.getUserByUsername(username);

            // Kiểm tra quyền admin
            if (currentUser == null || !currentUser.getIsAdmin()) {
                logger.error("Unauthorized attempt to add book by user: {}", username);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("Bạn không có quyền thực hiện thao tác này"));
            }

            logger.debug("Adding new book: {}", bookRequest.getTitle());
            BookResponse addedBook = bookService.addBook(bookRequest);

            return ResponseEntity.status(HttpStatus.CREATED).body(addedBook);
        } catch (Exception e) {
            logger.error("Error adding book", e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Không thể thêm sách: " + e.getMessage()));
        }
    }

    /**
     * API cập nhật thông tin sách (chỉ admin mới có quyền)
     *
     * @param bookId ID của sách cần cập nhật
     * @param bookRequest Thông tin sách cần cập nhật
     * @return Thông tin sách đã được cập nhật
     */
    @PutMapping("/{bookId}")
    public ResponseEntity<?> updateBook(@PathVariable Integer bookId, @Valid @RequestBody BookRequest bookRequest) {
        logger.info("API called: uodateBook");
        try {
            String username = tokenUtils.getCurrentUsername();
            Users currentUser = authService.getUserByUsername(username);

            // Kiểm tra quyền admin
            if (currentUser == null || !currentUser.getIsAdmin()) {
                logger.error("Unauthorized attempt to update book by user: {}", username);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("Bạn không có quyền thực hiện thao tác này"));
            }

            logger.debug("Updating book with ID: {}", bookId);
            BookResponse updatedBook = bookService.updateBook(bookId, bookRequest);

            if (updatedBook == null) {
                logger.error("Book not found with ID: {}", bookId);
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(updatedBook);
        } catch (Exception e) {
            logger.error("Error updating book with ID: {}", bookId, e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Không thể cập nhật sách: " + e.getMessage()));
        }
    }

    /**
     * API xóa sách (chỉ admin mới có quyền)
     *
     * @param bookId ID của sách cần xóa
     * @return Thông báo xác nhận đã xóa
     */
    @DeleteMapping("/{bookId}")
    public ResponseEntity<?> deleteBook(@PathVariable Integer bookId) {
        try {
            String username = tokenUtils.getCurrentUsername();
            Users currentUser = authService.getUserByUsername(username);

            // Kiểm tra quyền admin
            if (currentUser == null || !currentUser.getIsAdmin()) {
                logger.error("Unauthorized attempt to delete book by user: {}", username);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("Bạn không có quyền thực hiện thao tác này"));
            }

            logger.debug("Deleting book with ID: {}", bookId);
            boolean isDeleted = bookService.deleteBook(bookId);

            if (!isDeleted) {
                logger.error("Book not found with ID: {}", bookId);
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(Map.of("message", "Xóa sách thành công"));
        } catch (Exception e) {
            logger.error("Error deleting book with ID: {}", bookId, e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Không thể xóa sách: " + e.getMessage()));
        }
    }

    /**
     * API lấy danh sách đánh giá của sách
     *
     * @param bookId ID của sách cần lấy đánh giá
     * @param page Số trang (mặc định là 0)
     * @param size Kích thước trang (mặc định là 10)
     * @return Danh sách đánh giá của sách
     */
    @GetMapping("/{bookId}/reviews")
    public ResponseEntity<?> getBookReviews(
            @PathVariable Integer bookId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            logger.debug("Getting reviews for book with ID: {}", bookId);

            List<Review> reviews = reviewService.getBookReviews(bookId, page, size);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            logger.error("Error getting reviews for book with ID: {}", bookId, e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Không thể lấy đánh giá sách: " + e.getMessage()));
        }
    }

    /**
     * API thêm đánh giá cho sách
     *
     * @param bookId ID của sách cần đánh giá
     * @param reviewRequest Thông tin đánh giá
     * @return Thông tin đánh giá đã được thêm
     */
    @PostMapping("/{bookId}/reviews")
    public ResponseEntity<?> addBookReview(
            @PathVariable Integer bookId,
            @Valid @RequestBody ReviewRequest reviewRequest) {
        try {
            String username = tokenUtils.getCurrentUsername();
            Users currentUser = authService.getUserByUsername(username);

            if (currentUser == null) {
                logger.error("Unauthorized attempt to add review");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Bạn cần đăng nhập để đánh giá sách"));
            }

            logger.debug("Adding review for book with ID: {} by user: {}", bookId, currentUser.getUserId());

            Review addedReview = reviewService.addReview(bookId, currentUser.getUserId(), reviewRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(addedReview);
        } catch (Exception e) {
            logger.error("Error adding review for book with ID: {}", bookId, e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Không thể thêm đánh giá: " + e.getMessage()));
        }
    }

    @GetMapping("/sale-books-info")
    public ResponseEntity<?> getSaleBooksCoverInfo() {
        try {
            List<BookSaleInfoDTO> saleBooks = bookService.getSaleBooksCoverInfo();
            return ResponseEntity.ok(saleBooks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Không thể lấy thông tin sách sale"));
        }
    }

    @GetMapping("/top-rated")
    public ResponseEntity<?> getTopRatedBooks(
            @RequestParam(defaultValue = "5") int limit) {
        try {
            logger.info("API called: getTopRatedBooks");
            List<BookResponse> topRatedBooks = bookService.getTopRatedBooks(limit);
            return ResponseEntity.ok(topRatedBooks);
        } catch (Exception e) {
            logger.error("Error getting top rated books", e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Không thể lấy sách có đánh giá cao nhất: " + e.getMessage()));
        }
    }

    /**
     * API lấy danh sách danh mục sách
     *
     * @return Danh sách danh mục sách dưới dạng DTO
     */
    @GetMapping("/categories")
    public ResponseEntity<?> getAllCategories() {
        try {
            logger.debug("Getting all book categories");
            List<CategoryDTO> categories = categoryService.getAllCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            logger.error("Error getting book categories", e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Không thể lấy danh sách danh mục: " + e.getMessage()));
        }
    }

    /**
     * API thêm danh mục sách mới (chỉ admin mới có quyền)
     *
     * @param categoryRequest Thông tin danh mục cần thêm
     * @return Thông tin danh mục đã được thêm
     */
    @PostMapping("/categories")
    public ResponseEntity<?> addCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
        try {
            String username = tokenUtils.getCurrentUsername();
            Users currentUser = authService.getUserByUsername(username);

            // Kiểm tra quyền admin
            if (currentUser == null || !currentUser.getIsAdmin()) {
                logger.error("Unauthorized attempt to add category by user: {}", username);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("Bạn không có quyền thực hiện thao tác này"));
            }

            logger.debug("Adding new category: {}", categoryRequest.getCategoryName());

            Category addedCategory = categoryService.addCategory(categoryRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(addedCategory);
        } catch (Exception e) {
            logger.error("Error adding category", e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Không thể thêm danh mục: " + e.getMessage()));
        }
    }

    @PostMapping("/upload-cover")
    public ResponseEntity<?> uploadBookCover(@RequestParam("file") MultipartFile file) {
        try {
            String username = tokenUtils.getCurrentUsername();
            Users currentUser = authService.getUserByUsername(username);

            // Check admin rights
            if (currentUser == null || !currentUser.getIsAdmin()) {
                logger.error("Unauthorized attempt to upload image by user: {}", username);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("Bạn không có quyền thực hiện thao tác này"));
            }

            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Vui lòng chọn file"));
            }

            // Check file type
            if (!file.getContentType().startsWith("image/")) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Chỉ hỗ trợ file hình ảnh"));
            }

            String imageUrl = cloudinaryService.uploadFile(file);
            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
        } catch (Exception e) {
            logger.error("Error uploading book cover", e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Không thể tải lên ảnh bìa: " + e.getMessage()));
        }
    }
}