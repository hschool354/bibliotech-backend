package com.example.Bibliotech_backend.controller;

import com.example.Bibliotech_backend.dto.CategoryDTO;
import com.example.Bibliotech_backend.dto.CategoryRequest;
import com.example.Bibliotech_backend.exception.ErrorResponse;
import com.example.Bibliotech_backend.model.Category;
import com.example.Bibliotech_backend.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoriesController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        try {
            List<CategoryDTO> categories = categoryService.getAllCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Cannot fetch categories: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Integer id) {
        try {
            Category category = categoryService.getCategoryById(id);
            if (category == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(category);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Cannot fetch category: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> addCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
        try {
            Category category = categoryService.addCategory(categoryRequest);
            return ResponseEntity.status(201).body(category);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Cannot add category: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Integer id, @Valid @RequestBody CategoryRequest categoryRequest) {
        try {
            Category category = categoryService.updateCategory(id, categoryRequest);
            if (category == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(category);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Cannot update category: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Integer id) {
        try {
            boolean isDeleted = categoryService.deleteCategory(id);
            if (!isDeleted) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Cannot delete category: " + e.getMessage()));
        }
    }
}