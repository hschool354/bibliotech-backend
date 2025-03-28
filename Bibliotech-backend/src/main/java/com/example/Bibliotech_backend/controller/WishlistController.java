package com.example.Bibliotech_backend.controller;

import com.example.Bibliotech_backend.model.Wishlist;
import com.example.Bibliotech_backend.service.WishlistService;
import com.example.Bibliotech_backend.service.AuthService;
import com.example.Bibliotech_backend.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private AuthService authService;

    /**
     * Lấy danh sách wishlist của người dùng hiện tại
     */
    @GetMapping
    public ResponseEntity<List<Wishlist>> getMyWishlist() {
        // Lấy thông tin người dùng từ JWT token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // 401 nếu chưa đăng nhập
        }

        String username = authentication.getName();
        Users user = authService.getUserByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 404 nếu không tìm thấy user
        }

        Integer authenticatedUserId = user.getUserId();
        List<Wishlist> wishlist = wishlistService.getUserWishlist(authenticatedUserId);
        return ResponseEntity.ok(wishlist);
    }

    /**
     * Lấy danh sách wishlist của một người dùng cụ thể (chỉ dành cho admin)
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<Wishlist>> getUserWishlist(@PathVariable Integer userId) {
        // Lấy thông tin người dùng từ JWT token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // 401 nếu chưa đăng nhập
        }

        // Kiểm tra quyền admin
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // 403 nếu không phải admin
        }

        List<Wishlist> wishlist = wishlistService.getUserWishlist(userId);
        return ResponseEntity.ok(wishlist);
    }

    /**
     * Thêm sách vào wishlist của người dùng hiện tại
     */
    @PostMapping("/add")
    public ResponseEntity<?> addBookToWishlist(@RequestParam Integer bookId) {
        try {
            // Lấy thông tin người dùng từ JWT token
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized"); // 401 nếu chưa đăng nhập
            }

            String username = authentication.getName();
            Users user = authService.getUserByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"); // 404 nếu không tìm thấy user
            }

            Integer authenticatedUserId = user.getUserId();
            Wishlist wishlistEntry = wishlistService.addBookToWishlist(authenticatedUserId, bookId);
            return ResponseEntity.ok(wishlistEntry);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Failed to add book to wishlist: " + e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Book already in wishlist: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    /**
     * Xóa sách khỏi wishlist của người dùng hiện tại
     */
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeBookFromWishlist(@RequestParam Integer bookId) {
        try {
            // Lấy thông tin người dùng từ JWT token
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized"); // 401 nếu chưa đăng nhập
            }

            String username = authentication.getName();
            Users user = authService.getUserByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"); // 404 nếu không tìm thấy user
            }

            Integer authenticatedUserId = user.getUserId();
            boolean removed = wishlistService.removeBookFromWishlist(authenticatedUserId, bookId);
            if (removed) {
                return ResponseEntity.ok("Book removed from wishlist successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found in wishlist");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Failed to remove book from wishlist: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }
}