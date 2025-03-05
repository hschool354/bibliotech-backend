package com.example.Bibliotech_backend.service;

import com.example.Bibliotech_backend.dto.CategoryDTO;
import com.example.Bibliotech_backend.dto.CategoryRequest;
import com.example.Bibliotech_backend.model.Category;
import com.example.Bibliotech_backend.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);
    private final IdGeneratorService idGeneratorService;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository,IdGeneratorService idGeneratorService) {
        this.categoryRepository = categoryRepository;
        this.idGeneratorService = idGeneratorService;
    }

    /**
     * Lấy danh sách tất cả danh mục sách dưới dạng DTO
     */
    public List<CategoryDTO> getAllCategories() {
        logger.debug("Getting all book categories");

        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(category -> new CategoryDTO(
                        category.getCategoryId(),
                        category.getName(),
                        category.getDescription(),
                        category.getParentCategory() != null ? category.getParentCategory().getCategoryId() : null
                ))
                .collect(Collectors.toList());
    }

    // Các phương thức khác giữ nguyên, chỉ điều chỉnh nếu cần trả về DTO
    public Category getCategoryById(Integer categoryId) {
        logger.debug("Getting category with ID: {}", categoryId);
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        return categoryOptional.orElse(null);
    }

    /**
     * Thêm mới danh mục sách
     */
    @Transactional
    public Category addCategory(CategoryRequest categoryRequest) {
        logger.debug("Adding new category: {}", categoryRequest.getCategoryName());

        if (categoryRepository.existsByCategoryName(categoryRequest.getCategoryName())) {
            logger.error("Category with name {} already exists", categoryRequest.getCategoryName());
            throw new IllegalArgumentException("Danh mục với tên này đã tồn tại");
        }

        Category category = new Category();
        int categoryID = idGeneratorService.generateCategoryId();
        category.setCategoryId(categoryID);

        // Explicitly set categoryName instead of name
        category.setName(categoryRequest.getCategoryName());
        category.setDescription(categoryRequest.getDescription());

        // Fix for setParentCategoryId method - assuming this field exists in the Category entity
        Integer parentId = categoryRequest.getParentCategoryId();

        // Check if parent category exists
        if (parentId != null && !categoryRepository.existsById(parentId)) {
            logger.error("Parent category with ID {} does not exist", parentId);
            throw new IllegalArgumentException("Danh mục cha không tồn tại");
        }

        try {
            // Assuming there's a method to set parent category ID
            if (parentId != null) {
                Category parentCategory = categoryRepository.findById(parentId)
                        .orElseThrow(() -> new IllegalArgumentException("Parent category not found"));
                category.setParentCategory(parentCategory);
            }
        } catch (Exception e) {
            logger.error("Error setting parent category ID", e);
            // Handle field/method not found or other issues
        }

        return categoryRepository.save(category);
    }

    /**
     * Cập nhật thông tin danh mục
     */
    @Transactional
    public Category updateCategory(Integer categoryId, CategoryRequest categoryRequest) {
        logger.debug("Updating category with ID: {}", categoryId);

        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);

        if (categoryOptional.isPresent()) {
            Category category = categoryOptional.get();

            if (!category.getName().equals(categoryRequest.getCategoryName()) &&
                    categoryRepository.existsByCategoryName(categoryRequest.getCategoryName())) {
                logger.error("Category with name {} already exists", categoryRequest.getCategoryName());
                throw new IllegalArgumentException("Danh mục với tên này đã tồn tại");
            }

            category.setName(categoryRequest.getCategoryName());
            category.setDescription(categoryRequest.getDescription());

            Integer parentId = categoryRequest.getParentCategoryId();

            if (parentId != null) {
                if (!categoryRepository.existsById(parentId)) {
                    logger.error("Parent category with ID {} does not exist", parentId);
                    throw new IllegalArgumentException("Danh mục cha không tồn tại");
                }

                if (categoryId.equals(parentId)) {
                    logger.error("Category cannot be its own parent");
                    throw new IllegalArgumentException("Danh mục không thể là cha của chính nó");
                }

                try {
                    // Assuming there's a method to set parent category ID
                    Category parentCategory = categoryRepository.findById(parentId)
                            .orElseThrow(() -> new IllegalArgumentException("Parent category not found"));
                    category.setParentCategory(parentCategory);
                } catch (Exception e) {
                    logger.error("Error setting parent category ID", e);
                    // Handle field/method not found or other issues
                }
            } else {
                try {
                    // If parentId is null, set to null
                    category.setParentCategory(null);
                } catch (Exception e) {
                    logger.error("Error setting parent category ID to null", e);
                }
            }

            return categoryRepository.save(category);
        }

        return null;
    }

    /**
     * Xóa danh mục
     */
    @Transactional
    public boolean deleteCategory(Integer categoryId) {
        logger.debug("Deleting category with ID: {}", categoryId);

        if (categoryRepository.existsById(categoryId)) {
            if (categoryRepository.existsByParentCategoryId(categoryId)) {
                logger.error("Category with ID {} is used as parent category", categoryId);
                throw new IllegalArgumentException("Không thể xóa danh mục đang được sử dụng làm danh mục cha");
            }

            categoryRepository.deleteById(categoryId);
            return true;
        }

        return false;
    }

    /**
     * Lấy danh sách danh mục con của một danh mục
     */
    public List<Category> getChildCategories(Integer parentCategoryId) {
        logger.debug("Getting child categories for parent ID: {}", parentCategoryId);

        return categoryRepository.findByParentCategoryCategoryId(parentCategoryId);
    }
}