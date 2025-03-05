package com.example.Bibliotech_backend.repository;

import com.example.Bibliotech_backend.dto.BookSaleInfoDTO;
import com.example.Bibliotech_backend.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    /**
     * Tìm kiếm sách theo ID với danh mục đã được nạp sẵn
     */
    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.bookCategories WHERE b.bookId = :bookId")
    Optional<Book> findByIdWithCategories(@Param("bookId") Integer bookId);

    /**
     * Tìm kiếm sách với các bộ lọc và danh mục
     */
    @Query("SELECT DISTINCT b FROM Book b " +
            "JOIN BookCategory bc ON b.bookId = bc.bookId " +
            "WHERE (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
            "AND (:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) " +
            "AND bc.categoryId = :categoryId " +
            "AND (:language IS NULL OR LOWER(b.language) = LOWER(:language)) " +
            "AND (:minPrice IS NULL OR b.originalPrice >= :minPrice) " +
            "AND (:maxPrice IS NULL OR b.originalPrice <= :maxPrice) " +
            "AND (:hasDiscount IS NULL OR " +
            "(:hasDiscount = true AND b.discountedPrice IS NOT NULL AND b.discountedPrice < b.originalPrice) OR " +
            "(:hasDiscount = false AND (b.discountedPrice IS NULL OR b.discountedPrice >= b.originalPrice)))")
    Page<Book> findBooksByFiltersAndCategory(
            @Param("title") String title,
            @Param("author") String author,
            @Param("categoryId") Integer categoryId,
            @Param("language") String language,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("hasDiscount") Boolean hasDiscount,
            Pageable pageable);

    /**
     * Tìm kiếm sách với các bộ lọc không bao gồm danh mục
     */
    @Query("SELECT b FROM Book b " +
            "WHERE (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
            "AND (:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) " +
            "AND (:language IS NULL OR LOWER(b.language) = LOWER(:language)) " +
            "AND (:minPrice IS NULL OR b.originalPrice >= :minPrice) " +
            "AND (:maxPrice IS NULL OR b.originalPrice <= :maxPrice) " +
            "AND (:hasDiscount IS NULL OR " +
            "(:hasDiscount = true AND b.discountedPrice IS NOT NULL AND b.discountedPrice < b.originalPrice) OR " +
            "(:hasDiscount = false AND (b.discountedPrice IS NULL OR b.discountedPrice >= b.originalPrice)))")
    Page<Book> findBooksByFilters(
            @Param("title") String title,
            @Param("author") String author,
            @Param("language") String language,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("hasDiscount") Boolean hasDiscount,
            Pageable pageable);

    @Query("SELECT new com.example.Bibliotech_backend.dto.BookSaleInfoDTO(" +
            "b.bookId, b.title, b.coverImageUrl, b.originalPrice, b.discountedPrice) " +
            "FROM Book b " +
            "WHERE b.coverImageUrl IS NOT NULL " +
            "AND (b.discountedPrice IS NOT NULL AND b.discountedPrice < b.originalPrice)")
    List<BookSaleInfoDTO> findSaleBooksCoverInfo();

    @Query("SELECT b FROM Book b " +
            "ORDER BY b.averageRating DESC")
    Page<Book> findTopRatedBooks(Pageable pageable);
}