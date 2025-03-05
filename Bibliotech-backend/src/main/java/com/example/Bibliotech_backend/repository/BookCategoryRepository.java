package com.example.Bibliotech_backend.repository;

import com.example.Bibliotech_backend.model.BookCategory;
import com.example.Bibliotech_backend.model.BookCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface BookCategoryRepository extends JpaRepository<BookCategory, BookCategoryId> {

    /**
     * Xóa tất cả liên kết danh mục của một cuốn sách
     */
    @Modifying
    @Query("DELETE FROM BookCategory bc WHERE bc.bookId = :bookId")
    void deleteAllByBookId(@Param("bookId") Integer bookId);

    /**
     * Xóa một liên kết cụ thể giữa sách và danh mục
     */
    @Modifying
    @Query("DELETE FROM BookCategory bc WHERE bc.bookId = :bookId AND bc.categoryId = :categoryId")
    void deleteBookCategory(@Param("bookId") Integer bookId, @Param("categoryId") Integer categoryId);

//    /**
//     * Lưu liên kết giữa sách và danh mục
//     */
//    @Modifying
//    @Query(value = "INSERT INTO book_category (book_id, category_id) VALUES (:bookId, :categoryId)", nativeQuery = true)
//    void saveBookCategory(@Param("bookId") Integer bookId, @Param("categoryId") Integer categoryId);


    @Modifying
    @Transactional
    @Query(value = "INSERT INTO book_categories (book_id, category_id) VALUES (:bookId, :categoryId)", nativeQuery = true)
    void saveBookCategory(@Param("bookId") Integer bookId, @Param("categoryId") Integer categoryId);

    /**
     * Kiểm tra xem liên kết giữa sách và danh mục đã tồn tại chưa
     */
    @Query("SELECT COUNT(bc) > 0 FROM BookCategory bc WHERE bc.bookId = :bookId AND bc.categoryId = :categoryId")
    boolean existsByBookIdAndCategoryId(@Param("bookId") Integer bookId, @Param("categoryId") Integer categoryId);

    /**
     * Lấy danh sách category IDs của một cuốn sách
     */
    @Query("SELECT bc.categoryId FROM BookCategory bc WHERE bc.bookId = :bookId")
    List<Integer> findCategoryIdsByBookId(@Param("bookId") Integer bookId);
}