package com.example.Bibliotech_backend.repository;

import com.example.Bibliotech_backend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    /**
     * Kiểm tra xem danh mục có tồn tại theo tên hay không
     */
    boolean existsByCategoryName(String categoryName);

    /**
     * Kiểm tra xem có danh mục nào sử dụng một danh mục làm danh mục cha không
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Category c WHERE c.parentCategory.categoryId = :parentCategoryId")
    boolean existsByParentCategoryId(@Param("parentCategoryId") Integer parentCategoryId);

    /**
     * Tìm tất cả danh mục con của một danh mục cha
     */
    // Fix this method name to match the property name in Category entity
    List<Category> findByParentCategoryCategoryId(Integer parentCategoryId);

    // Add this query to fetch categories for a specific book
    @Query("SELECT c FROM Category c JOIN BookCategory bc ON c.categoryId = bc.category.categoryId WHERE bc.book.bookId = :bookId")
    List<Category> findCategoriesByBookId(@Param("bookId") Integer bookId);

    // Or if you have a BookCategory entity, you might need this instead
    @Query("SELECT c FROM Category c JOIN BookCategory bc ON c.categoryId = bc.categoryId WHERE bc.bookId = :bookId")
    List<Category> findCategoriesByBookIdWithJoin(@Param("bookId") Integer bookId);
}