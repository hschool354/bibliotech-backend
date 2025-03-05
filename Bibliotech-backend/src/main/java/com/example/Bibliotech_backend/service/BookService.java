package com.example.Bibliotech_backend.service;

import com.example.Bibliotech_backend.dto.BookRequest;
import com.example.Bibliotech_backend.dto.BookResponse;
import com.example.Bibliotech_backend.dto.CategoryResponse;
import com.example.Bibliotech_backend.dto.DealResponse;
import com.example.Bibliotech_backend.model.Book;
import com.example.Bibliotech_backend.model.BookCategory;
import com.example.Bibliotech_backend.model.Category;
import com.example.Bibliotech_backend.model.Deal;
import com.example.Bibliotech_backend.repository.BookRepository;
import com.example.Bibliotech_backend.repository.BookCategoryRepository;
import com.example.Bibliotech_backend.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final IdGeneratorService idGeneratorService;

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    @Autowired
    public BookService(BookRepository bookRepository,
                       CategoryRepository categoryRepository,
                       BookCategoryRepository bookCategoryRepository, IdGeneratorService idGeneratorService) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.bookCategoryRepository = bookCategoryRepository;
        this.idGeneratorService = idGeneratorService;
    }

    /**f
     * Lấy danh sách sách với các bộ lọc
     */
    public List<BookResponse> getAllBooks(
            String title, String author, Integer categoryId, String language,
            Double minPrice, Double maxPrice, Boolean hasDiscount,
            int page, int size, String sortBy, String direction) {

        logger.debug("Getting books with filters - title: {}, author: {}, category: {}, language: {}",
                title, author, categoryId, language);

        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<Book> bookPage;

        if (categoryId != null) {
            bookPage = bookRepository.findBooksByFiltersAndCategory(
                    title, author, categoryId, language, minPrice, maxPrice, hasDiscount, pageable);
        } else {
            bookPage = bookRepository.findBooksByFilters(
                    title, author, language, minPrice, maxPrice, hasDiscount, pageable);
        }

        return bookPage.stream()
                .map(this::convertToBookResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy thông tin chi tiết của một cuốn sách
     */
    @Transactional(readOnly = true)
    public BookResponse getBookById(Integer bookId) {
        logger.debug("Getting book with ID: {}", bookId);

        // Use the new method to fetch book with categories pre-loaded
        Optional<Book> bookOptional = bookRepository.findByIdWithCategories(bookId);

        return bookOptional.map(this::convertToBookResponse).orElse(null);
    }

    /**
     * Thêm mới một cuốn sách
     */
    @Transactional
    public BookResponse addBook(BookRequest bookRequest) {
        logger.debug("Adding new book: {}", bookRequest.getTitle());

        Book book = new Book();

        // Set the generated ID
        int bookId = idGeneratorService.generateBookId();
        book.setBookId(bookId);

        try {
            updateBookFromRequest(book, bookRequest);
        } catch (Exception e) {
            logger.error("Error updating book from request", e);
            throw new RuntimeException("Error updating book from request", e);
        }

        Book savedBook = bookRepository.save(book);

        if (bookRequest.getCategoryIds() != null && !bookRequest.getCategoryIds().isEmpty()) {
            saveBookCategories(savedBook.getBookId(), bookRequest.getCategoryIds());
        }

        return getFullBookResponse(savedBook.getBookId());
    }

    /**
     * Cập nhật thông tin sách
     */
    @Transactional
    public BookResponse updateBook(Integer bookId, BookRequest bookRequest) {
        logger.debug("Updating book with ID: {}", bookId);

        Optional<Book> bookOptional = bookRepository.findById(bookId);

        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();

            // First, handle the basic book information updates
            try {
                updateBookFromRequest(book, bookRequest);
            } catch (Exception e) {
                logger.error("Error updating book from request", e);
                throw new RuntimeException("Error updating book from request", e);
            }

            // Fix for the language issue
            if (bookRequest.getLanguage() != null) {
                try {
                    book.setLanguage(Book.Language.valueOf(bookRequest.getLanguage().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    logger.warn("Invalid language value: {}. Setting to OTHER.", bookRequest.getLanguage());
                    book.setLanguage(Book.Language.Other);
                }
            }

            Book updatedBook = bookRepository.save(book);

            // Handle categories in a separate transaction to prevent rolling back the book update
            updateBookCategories(bookId, bookRequest.getCategoryIds());

            // Reload the book to get the updated categories
            Book refreshedBook = bookRepository.findById(bookId).orElse(updatedBook);

            return convertToBookResponse(refreshedBook);
        }

        return null;
    }

    /**
     * Separate method to handle book categories with its own transaction
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateBookCategories(Integer bookId, Set<Integer> categoryIds) {
        if (categoryIds == null) {
            return; // Don't modify categories if not provided
        }

        try {
            // First, get existing categories for this book to compare
            List<Integer> existingCategoryIds = bookCategoryRepository.findCategoryIdsByBookId(bookId);
            logger.debug("Existing categories for book {}: {}", bookId, existingCategoryIds);

            // If we need to delete all categories
            if (categoryIds.isEmpty()) {
                bookCategoryRepository.deleteAllByBookId(bookId);
                logger.debug("Deleted all categories for book {}", bookId);
                return;
            }

            // Handle additions and removals separately
            Set<Integer> toAdd = new HashSet<>(categoryIds);
            toAdd.removeAll(existingCategoryIds); // Keep only new categories

            Set<Integer> toRemove = new HashSet<>(existingCategoryIds);
            toRemove.removeAll(categoryIds); // Keep only categories to remove

            // Remove categories that are no longer needed
            if (!toRemove.isEmpty()) {
                for (Integer categoryId : toRemove) {
                    bookCategoryRepository.deleteBookCategory(bookId, categoryId);
                    logger.debug("Removed category {} from book {}", categoryId, bookId);
                }
            }

            // Add new categories
            if (!toAdd.isEmpty()) {
                for (Integer categoryId : toAdd) {
                    if (categoryRepository.existsById(categoryId)) {
                        try {
                            // Double-check again to avoid constraint violation
                            if (!bookCategoryRepository.existsByBookIdAndCategoryId(bookId, categoryId)) {
                                bookCategoryRepository.saveBookCategory(bookId, categoryId);
                                logger.debug("Added category {} to book {}", categoryId, bookId);
                            } else {
                                logger.debug("Category {} already exists for book {} (skipped)", categoryId, bookId);
                            }
                        } catch (DataIntegrityViolationException e) {
                            logger.debug("Category {} already exists for book {} (caught exception)", categoryId, bookId);
                        }
                    } else {
                        logger.warn("Category with ID {} does not exist", categoryId);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error updating book categories for book ID: {}", bookId, e);
        }
    }

    /**
     * Xóa sách
     */
    @Transactional
    public boolean deleteBook(Integer bookId) {
        logger.debug("Deleting book with ID: {}", bookId);

        if (bookRepository.existsById(bookId)) {
            bookCategoryRepository.deleteAllByBookId(bookId);
            bookRepository.deleteById(bookId);
            return true;
        }

        return false;
    }

    /**
     * Cập nhật thông tin sách từ request
     */
    private void updateBookFromRequest(Book book, BookRequest bookRequest) {
        book.setTitle(bookRequest.getTitle());
        book.setAuthor(bookRequest.getAuthor());
        book.setIsbn(bookRequest.getIsbn());
        book.setOriginalPrice(bookRequest.getOriginalPrice());
        book.setDiscountedPrice(bookRequest.getDiscountedPrice());
        book.setPublicationYear(bookRequest.getPublicationYear());

        // Convert String to Language enum if needed
        if (bookRequest.getLanguage() != null) {
            try {
                // First letter uppercase, rest lowercase
                String formattedLanguage = bookRequest.getLanguage().substring(0, 1).toUpperCase()
                        + bookRequest.getLanguage().substring(1).toLowerCase();
                book.setLanguage(Book.Language.valueOf(formattedLanguage));
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid language value: {}", bookRequest.getLanguage());
                // Set a default value
                book.setLanguage(Book.Language.Other);
            }
        }

        book.setPageCount(bookRequest.getPageCount());
        book.setDescription(bookRequest.getDescription());
        book.setCoverImageUrl(bookRequest.getCoverImageUrl());
        book.setStockQuantity(bookRequest.getStockQuantity());

        // Set dealId if it exists
        if (bookRequest.getDealId() != null) {
            Deal deal = new Deal();
            deal.setDealId(bookRequest.getDealId());
            book.setDeal(deal);
        } else {
            book.setDeal(null);
        }

        // Convert String to ReadingDifficulty enum if needed
        if (bookRequest.getReadingDifficulty() != null) {
            try {
                book.setReadingDifficulty(Book.ReadingDifficulty.valueOf(bookRequest.getReadingDifficulty().toUpperCase()));
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid reading difficulty value: {}", bookRequest.getReadingDifficulty());
                // Set a default value or handle error as appropriate
            }
        }

        book.setEstimatedReadingTime(bookRequest.getEstimatedReadingTime());

        // Convert String to ContentRating enum if needed
        if (bookRequest.getContentRating() != null) {
            try {
                book.setContentRating(Book.ContentRating.valueOf(bookRequest.getContentRating().toUpperCase()));
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid content rating value: {}", bookRequest.getContentRating());
                // Set a default value or handle error as appropriate
            }
        }
    }

    /**
     * Lưu danh mục sách
     */
    private void saveBookCategories(Integer bookId, Set<Integer> categoryIds) {
        logger.debug("Saving categories for book ID: {}, Categories: {}", bookId, categoryIds);
        for (Integer categoryId : categoryIds) {
            if (categoryRepository.existsById(categoryId)) {
                logger.debug("Saving category {} for book {}", categoryId, bookId);
                bookCategoryRepository.saveBookCategory(bookId, categoryId);
            } else {
                logger.warn("Category with ID {} does not exist", categoryId);
            }
        }
    }

    /**
     * Chuyển đổi từ Book sang BookResponse
     */
    private BookResponse convertToBookResponse(Book book) {
        BookResponse response = new BookResponse();

        response.setBookId(book.getBookId());
        response.setTitle(book.getTitle());
        response.setAuthor(book.getAuthor());
        response.setIsbn(book.getIsbn());
        response.setOriginalPrice(book.getOriginalPrice());
        response.setDiscountedPrice(book.getDiscountedPrice());
        response.setPublicationYear(book.getPublicationYear());

        // Convert Language enum to String if needed
        if (book.getLanguage() != null) {
            response.setLanguage(book.getLanguage().name());
        }

        response.setPageCount(book.getPageCount());
        response.setAverageRating(book.getAverageRating());
        response.setRatingCount(book.getRatingCount());
        response.setDescription(book.getDescription());
        response.setCoverImageUrl(book.getCoverImageUrl());
        response.setStockQuantity(book.getStockQuantity());

        // Fix the dealId issue
        if (book.getDeal() != null) {
            DealResponse dealResponse = new DealResponse();
            dealResponse.setDealId(book.getDeal().getDealId());
            // Populate other deal fields as needed
            response.setDeal(dealResponse);
        }

        // Convert ReadingDifficulty enum to String if needed
        if (book.getReadingDifficulty() != null) {
            response.setReadingDifficulty(book.getReadingDifficulty().name());
        }

        response.setEstimatedReadingTime(book.getEstimatedReadingTime());

        // Convert ContentRating enum to String if needed
        if (book.getContentRating() != null) {
            response.setContentRating(book.getContentRating().name());
        }

        response.setCreatedAt(book.getCreatedAt());
        response.setUpdatedAt(book.getUpdatedAt());

        // Log category information for debugging
        Set<BookCategory> bookCategories = book.getBookCategories();
        logger.debug("Book {} has {} book categories", book.getBookId(), bookCategories.size());

        // Get categories directly from database as a fallback method in case relationship mapping isn't loading properly
        List<Category> categories;
        if (bookCategories == null || bookCategories.isEmpty()) {
            categories = categoryRepository.findCategoriesByBookId(book.getBookId());
            logger.debug("Categories fetched directly from repository: {}", categories.size());
        } else {
            categories = new ArrayList<>(book.getCategories());
            logger.debug("Categories fetched from book entity: {}", categories.size());
        }

        // Convert categories to response objects
        List<CategoryResponse> categoryResponses = categories.stream()
                .map(this::convertToCategoryResponse)
                .collect(Collectors.toList());

        response.setCategories(categoryResponses);

        return response;
    }

    private CategoryResponse convertToCategoryResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setCategoryId(category.getCategoryId());
        response.setCategoryName(category.getName());
        response.setDescription(category.getDescription());

        // Handle parent category if needed
        if (category.getParentCategory() != null) {
            CategoryResponse parentResponse = new CategoryResponse();
            parentResponse.setCategoryId(category.getParentCategory().getCategoryId());
            parentResponse.setCategoryName(category.getParentCategory().getName());
            // You may want to limit the depth of parent categories to avoid circular references
            response.setParentCategory(parentResponse);
        }

        return response;
    }

    private BookResponse getFullBookResponse(Integer bookId) {
        // Use the new method to fetch book with categories pre-loaded
        Book book = bookRepository.findByIdWithCategories(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found after save"));

        // Force initialization if needed
        int categoryCount = book.getBookCategories().size();
        logger.debug("Book {} has {} categories loaded", bookId, categoryCount);

        return convertToBookResponse(book);
    }
}