package com.example.Bibliotech_backend.controller;

import com.example.Bibliotech_backend.exception.ErrorResponse;
import com.example.Bibliotech_backend.dto.ProfileData;
import com.example.Bibliotech_backend.model.UserProfile;
import com.example.Bibliotech_backend.model.Users;
import com.example.Bibliotech_backend.security.JwtTokenProvider;
import com.example.Bibliotech_backend.service.AuthService;
import com.example.Bibliotech_backend.service.UserProfileService;
import com.example.Bibliotech_backend.service.UserRegistrationStatusService;
import com.example.Bibliotech_backend.util.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

/**
 * Lớp UserController định nghĩa các API liên quan đến người dùng.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    /**
     * Provider để xử lý JWT.
     */
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Dịch vụ xử lý trạng thái đăng ký người dùng.
     */
    private final UserRegistrationStatusService registrationStatusService;

    /**
     * Dịch vụ xử lý xác thực người dùng.
     */
    private final AuthService authService;

    /**
     * Dịch vụ xử lý thông tin profile người dùng.
     */
    private final UserProfileService userProfileService;

    /**
     * Tiện ích xử lý token.
     */
    private final TokenUtils tokenUtils;

    /**
     * Logger để ghi log thông tin debug và lỗi.
     */
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * Constructor khởi tạo UserController với các dependency cần thiết.
     *
     * @param jwtTokenProvider Provider xử lý JWT.
     * @param registrationStatusService Dịch vụ quản lý trạng thái đăng ký.
     * @param tokenUtils Tiện ích xử lý token.
     */
    public UserController(
            JwtTokenProvider jwtTokenProvider,
            AuthService authService,
            UserRegistrationStatusService registrationStatusService,
            UserProfileService userProfileService,
            TokenUtils tokenUtils) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authService = authService;
        this.registrationStatusService = registrationStatusService;
        this.userProfileService = userProfileService;
        this.tokenUtils = tokenUtils;
    }

    /**
     * API cho phép người dùng hoàn thành hồ sơ của họ.
     *
     * @param token JWT của người dùng, được truyền trong header "Authorization".
     * @param profileData Dữ liệu hồ sơ người dùng cần hoàn thành.
     * @return ResponseEntity phản hồi thành công nếu hoàn thành hồ sơ thành công.
     */
    @PostMapping("/complete-profile")
    public ResponseEntity<?> completeProfile(
            @RequestHeader("Authorization") String token,
            @RequestBody ProfileData profileData
    ) {
        // Xác thực token và lấy userId
        Integer userId = tokenUtils.validateTokenAndGetUserId(token);
        logger.debug("User ID extracted: {}", userId);
        logger.debug("Profile data received: {}", profileData);

        // Gọi service để hoàn thành hồ sơ người dùng
        registrationStatusService.completeProfile(userId, profileData);

        return ResponseEntity.ok().build();
    }

    /**
     * API lấy thông tin tài khoản người dùng hiện tại
     * @return Thông tin tài khoản của người dùng đang đăng nhập
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            String username = tokenUtils.getCurrentUsername();
            Users currentUser = authService.getUserByUsername(username);

            if (currentUser == null) {
                logger.error("Không tìm thấy người dùng hiện tại với username: {}", username);
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(currentUser);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy thông tin người dùng hiện tại", e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Không thể lấy thông tin người dùng"));
        }
    }

    /**
     * API lấy thông tin profile của người dùng hiện tại
     * @return Thông tin profile của người dùng đang đăng nhập
     */
    @GetMapping("/me/profile")
    public ResponseEntity<?> getCurrentUserProfile() {
        try {
            String username = tokenUtils.getCurrentUsername();
            Users currentUser = authService.getUserByUsername(username);

            if (currentUser == null) {
                logger.error("Không tìm thấy người dùng hiện tại với username: {}", username);
                return ResponseEntity.notFound().build();
            }

            UserProfile profile = userProfileService.getUserProfileById(currentUser.getUserId());
            if (profile == null) {
                logger.debug("Chưa có thông tin profile cho người dùng: {}", username);
                // Tạo profile rỗng với userId
                profile = new UserProfile();
                profile.setUserId(currentUser.getUserId());
            }

            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy thông tin profile người dùng hiện tại", e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Không thể lấy thông tin profile người dùng"));
        }
    }

    /**
     * API cập nhật thông tin profile người dùng hiện tại
     * @param profileData Dữ liệu cập nhật profile
     * @return Thông tin profile đã được cập nhật
     */
    @PutMapping("/me/profile")
    public ResponseEntity<?> updateCurrentUserProfile(@Valid @RequestBody ProfileData profileData) {
        try {
            String username = tokenUtils.getCurrentUsername();
            Users currentUser = authService.getUserByUsername(username);

            if (currentUser == null) {
                logger.error("Không tìm thấy người dùng hiện tại với username: {}", username);
                return ResponseEntity.notFound().build();
            }

            UserProfile updatedProfile = userProfileService.updateUserProfile(currentUser.getUserId(), profileData);

            if (updatedProfile == null) {
                logger.error("Không thể cập nhật thông tin profile cho người dùng: {}", username);
                return ResponseEntity.badRequest().body(new ErrorResponse("Không thể cập nhật thông tin profile người dùng"));
            }

            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            logger.error("Lỗi khi cập nhật thông tin profile người dùng", e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Lỗi cập nhật: " + e.getMessage()));
        }
    }

    /**
     * API lấy thông tin tài khoản của một người dùng cụ thể
     * @param userId ID của người dùng cần lấy thông tin
     * @return Thông tin tài khoản của người dùng
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable Integer userId) {
        try {
            Users user = authService.getUserById(userId);

            if (user == null) {
                logger.error("Không tìm thấy người dùng với ID: {}", userId);
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy thông tin người dùng có ID: {}", userId, e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Không thể lấy thông tin người dùng"));
        }
    }

    /**
     * API lấy thông tin profile của một người dùng cụ thể
     * @param userId ID của người dùng cần lấy thông tin
     * @return Thông tin profile của người dùng
     */
    @GetMapping("/{userId}/profile")
    public ResponseEntity<?> getUserProfile(@PathVariable Integer userId) {
        try {
            // Kiểm tra người dùng có tồn tại không
            Users user = authService.getUserById(userId);
            if (user == null) {
                logger.error("Không tìm thấy người dùng với ID: {}", userId);
                return ResponseEntity.notFound().build();
            }

            // Lấy thông tin profile
            UserProfile profile = userProfileService.getUserProfileById(userId);
            if (profile == null) {
                logger.debug("Chưa có thông tin profile cho người dùng có ID: {}", userId);
                // Tạo profile rỗng với userId
                profile = new UserProfile();
                profile.setUserId(userId);
            }

            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy thông tin profile người dùng có ID: {}", userId, e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Không thể lấy thông tin profile người dùng"));
        }
    }

    /**
     * API cập nhật thông tin profile của một người dùng cụ thể
     * @param userId ID của người dùng cần cập nhật
     * @param profileData Dữ liệu cập nhật profile
     * @return Thông tin profile đã được cập nhật
     */
    @PutMapping("/{userId}/profile")
    public ResponseEntity<?> updateUserProfile(@PathVariable Integer userId, @Valid @RequestBody ProfileData profileData) {
        try {
            // Kiểm tra quyền truy cập
            String currentUsername = tokenUtils.getCurrentUsername();
            Users currentUser = authService.getUserByUsername(currentUsername);

            // Chỉ admin hoặc chính người dùng đó mới có thể cập nhật
            if (currentUser == null || (!currentUser.getIsAdmin() && !currentUser.getUserId().equals(userId))) {
                logger.error("Người dùng {} không có quyền cập nhật thông tin của người dùng {}", currentUsername, userId);
                return ResponseEntity.status(403).body(new ErrorResponse("Không có quyền thực hiện thao tác này"));
            }

            // Kiểm tra người dùng có tồn tại không
            Users user = authService.getUserById(userId);
            if (user == null) {
                logger.error("Không tìm thấy người dùng với ID: {}", userId);
                return ResponseEntity.notFound().build();
            }

            UserProfile updatedProfile = userProfileService.updateUserProfile(userId, profileData);

            if (updatedProfile == null) {
                logger.error("Không thể cập nhật thông tin profile cho người dùng có ID: {}", userId);
                return ResponseEntity.badRequest().body(new ErrorResponse("Không thể cập nhật thông tin profile người dùng"));
            }

            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            logger.error("Lỗi khi cập nhật thông tin profile người dùng có ID: {}", userId, e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Lỗi cập nhật: " + e.getMessage()));
        }
    }

    /**
     * API cập nhật trạng thái đăng ký của người dùng
     * @param registrationStatusRequest Dữ liệu cập nhật trạng thái đăng ký
     * @return Kết quả cập nhật trạng thái
     */
    @PatchMapping("/registration-status")
    public ResponseEntity<?> updateRegistrationStatus(@RequestBody Map<String, Object> registrationStatusRequest) {
        try {
            String username = tokenUtils.getCurrentUsername();
            Users currentUser = authService.getUserByUsername(username);

            if (currentUser == null) {
                logger.error("Không tìm thấy người dùng với username: {}", username);
                return ResponseEntity.notFound().build();
            }

            Boolean isProfileCompleted = (Boolean) registrationStatusRequest.get("isProfileCompleted");

            if (isProfileCompleted == null) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Thiếu thông tin cập nhật trạng thái"));
            }

            Boolean result = registrationStatusService.updateRegistrationStatus(currentUser.getUserId(), isProfileCompleted);

            if (result) {
                return ResponseEntity.ok(Map.of("success", true, "message", "Cập nhật trạng thái đăng ký thành công"));
            } else {
                return ResponseEntity.badRequest().body(new ErrorResponse("Không thể cập nhật trạng thái đăng ký"));
            }
        } catch (Exception e) {
            logger.error("Lỗi khi cập nhật trạng thái đăng ký", e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Lỗi cập nhật: " + e.getMessage()));
        }
    }
}
