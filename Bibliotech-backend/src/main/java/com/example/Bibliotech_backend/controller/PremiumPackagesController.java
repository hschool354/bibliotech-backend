package com.example.Bibliotech_backend.controller;

import com.example.Bibliotech_backend.model.PremiumPackage;
import com.example.Bibliotech_backend.model.Users;
import com.example.Bibliotech_backend.service.AuthService;
import com.example.Bibliotech_backend.service.PremiumPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/premium-packages")
public class PremiumPackagesController {

    @Autowired
    private PremiumPackageService premiumPackageService;

    @Autowired
    private AuthService authService;

    /**
     * Lấy danh sách tất cả các gói Premium đang hoạt động.
     *
     * @return Danh sách các gói Premium đang hoạt động.
     */
    @GetMapping("/active")
    public ResponseEntity<List<PremiumPackage>> getAllActivePackages() {
        List<PremiumPackage> packages = premiumPackageService.getAllActivePackages();
        return ResponseEntity.ok(packages);
    }

    /**
     * Lấy danh sách tất cả các gói Premium (chỉ admin).
     *
     * @return Danh sách tất cả các gói Premium.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PremiumPackage>> getAllPackages() {
        List<PremiumPackage> packages = premiumPackageService.getAllPackages();
        return ResponseEntity.ok(packages);
    }

    /**
     * Lấy thông tin gói Premium theo ID.
     *
     * @param packageId ID của gói Premium.
     * @return Thông tin gói Premium.
     */
    @GetMapping("/{packageId}")
    public ResponseEntity<PremiumPackage> getPackageById(@PathVariable Integer packageId) {
        PremiumPackage premiumPackage = premiumPackageService.getPackageById(packageId);
        return ResponseEntity.ok(premiumPackage);
    }

    /**
     * Thêm gói Premium mới (chỉ admin).
     *
     * @param premiumPackage Thông tin gói Premium mới.
     * @return Thông tin gói Premium đã thêm.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PremiumPackage> addPremiumPackage(@Valid @RequestBody PremiumPackage premiumPackage) {
        // Lấy thông tin người dùng hiện tại (admin)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Users user = authService.getUserByUsername(username);

        PremiumPackage savedPackage = premiumPackageService.addPremiumPackage(premiumPackage);
        return ResponseEntity.ok(savedPackage);
    }

    /**
     * Cập nhật thông tin gói Premium (chỉ admin).
     *
     * @param packageId ID của gói Premium cần cập nhật.
     * @param premiumPackage Thông tin cập nhật.
     * @return Thông tin gói Premium đã cập nhật.
     */
    @PutMapping("/{packageId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PremiumPackage> updatePackage(
            @PathVariable Integer packageId,
            @Valid @RequestBody PremiumPackage premiumPackage) {

        // Lấy thông tin người dùng hiện tại (admin)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Users user = authService.getUserByUsername(username);

        PremiumPackage updatedPackage = premiumPackageService.updatePackage(packageId, premiumPackage);
        return ResponseEntity.ok(updatedPackage);
    }

    /**
     * Thay đổi trạng thái hoạt động của gói Premium (chỉ admin).
     *
     * @param packageId ID của gói Premium.
     * @param isActive Trạng thái hoạt động mới.
     * @return Thông tin gói Premium đã cập nhật.
     */
    @PatchMapping("/{packageId}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PremiumPackage> toggleActiveStatus(
            @PathVariable Integer packageId,
            @RequestParam boolean isActive) {

        // Lấy thông tin người dùng hiện tại (admin)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Users user = authService.getUserByUsername(username);

        PremiumPackage updatedPackage = premiumPackageService.toggleActiveStatus(packageId, isActive);
        return ResponseEntity.ok(updatedPackage);
    }

    /**
     * Xóa gói Premium (chỉ admin).
     *
     * @param packageId ID của gói Premium cần xóa.
     * @return Thông báo xóa thành công.
     */
    @DeleteMapping("/{packageId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePackage(@PathVariable Integer packageId) {
        // Lấy thông tin người dùng hiện tại (admin)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Users user = authService.getUserByUsername(username);

        premiumPackageService.deletePackage(packageId);
        return ResponseEntity.ok().body("Premium package deleted successfully");
    }
}