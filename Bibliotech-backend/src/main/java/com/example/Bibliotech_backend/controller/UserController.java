package com.example.Bibliotech_backend.controller;

import com.example.Bibliotech_backend.exception.ErrorResponse;
import com.example.Bibliotech_backend.dto.ProfileData;
import com.example.Bibliotech_backend.model.UserPreferences;
import com.example.Bibliotech_backend.model.UserProfile;
import com.example.Bibliotech_backend.model.Users;
import com.example.Bibliotech_backend.model.UserLibrary;
import com.example.Bibliotech_backend.model.ReadingHistory;
import com.example.Bibliotech_backend.security.JwtTokenProvider;
import com.example.Bibliotech_backend.service.*;
import com.example.Bibliotech_backend.util.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
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

    private final UserPreferencesService userPreferencesService;

    private final UserLibraryService userLibraryService;

    private final ReadingHistoryService readingHistoryService;

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
            TokenUtils tokenUtils,
            UserPreferencesService userPreferencesService,
            UserLibraryService userLibraryService,
            ReadingHistoryService readingHistoryService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authService = authService;
        this.registrationStatusService = registrationStatusService;
        this.userProfileService = userProfileService;
        this.tokenUtils = tokenUtils;
        this.userPreferencesService = userPreferencesService;
        this.userLibraryService = userLibraryService;
        this.readingHistoryService = readingHistoryService;
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

    /**
     * API lấy danh sách sở thích của người dùng
     * @return Danh sách sở thích của người dùng
     */
    @GetMapping("/preferences")
    public ResponseEntity<?> getUserPreferences() {
        try {
            String username = tokenUtils.getCurrentUsername();
            Users currentUser = authService.getUserByUsername(username);

            if (currentUser == null) {
                logger.error("Không tìm thấy người dùng với username: {}", username);
                return ResponseEntity.status(404).body(new ErrorResponse("Người dùng không tồn tại"));
            }

            List<UserPreferences> userPreferences = userPreferencesService.getUserPreferences(currentUser.getUserId());

            // Log chi tiết để debug
            logger.debug("Số lượng preferences: {}", userPreferences.size());
            userPreferences.forEach(pref ->
                    logger.debug("Preference - User ID: {}, Category ID: {}, Weight: {}",
                            pref.getUserId(), pref.getPreferredCategoryId(), pref.getPreferenceWeight())
            );

            if (userPreferences.isEmpty()) {
                return ResponseEntity.status(404).body(new ErrorResponse("Không tìm thấy preferences cho người dùng này"));
            }

            return ResponseEntity.ok(userPreferences);
        } catch (Exception e) {
            logger.error("Chi tiết lỗi khi lấy sở thích người dùng", e);
            return ResponseEntity.status(500).body(new ErrorResponse("Lỗi hệ thống khi lấy sở thích"));
        }
    }

    @PutMapping("/preferences")
    public ResponseEntity<?> updateUserPreferences(@RequestBody List<UserPreferences> preferences) {
        try {
            String username = tokenUtils.getCurrentUsername();
            Users currentUser = authService.getUserByUsername(username);

            if (currentUser == null) {
                logger.error("Không tìm thấy người dùng với username: {}", username);
                return ResponseEntity.status(404).body(new ErrorResponse("Người dùng không tồn tại"));
            }

            // Log chi tiết preferences nhận được
            logger.debug("Số lượng preferences nhận được: {}", preferences.size());
            preferences.forEach(pref ->
                    logger.debug("Received Preference - Category ID: {}, Weight: {}",
                            pref.getPreferredCategoryId(), pref.getPreferenceWeight())
            );

            List<UserPreferences> updatedPreferences = userPreferencesService.updateUserPreferences(currentUser.getUserId(), preferences);

            // Log chi tiết preferences đã lưu
            logger.debug("Số lượng preferences đã lưu: {}", updatedPreferences.size());
            updatedPreferences.forEach(pref ->
                    logger.debug("Saved Preference - User ID: {}, Category ID: {}, Weight: {}",
                            pref.getUserId(), pref.getPreferredCategoryId(), pref.getPreferenceWeight())
            );

            return ResponseEntity.ok(updatedPreferences);
        } catch (Exception e) {
            logger.error("Chi tiết lỗi khi cập nhật sở thích người dùng", e);
            return ResponseEntity.status(500).body(new ErrorResponse("Lỗi hệ thống khi cập nhật sở thích"));
        }
    }
    /**
     * API lấy thư viện sách của người dùng
     * @return Danh sách sách trong thư viện
     */
    @GetMapping("/library")
    public ResponseEntity<?> getUserLibrary() {
        try {
            String username = tokenUtils.getCurrentUsername();
            Users currentUser = authService.getUserByUsername(username);

            if (currentUser == null) {
                logger.error("Không tìm thấy người dùng với username: {}", username);
                return ResponseEntity.notFound().build();
            }

            List<UserLibrary> userLibrary = userLibraryService.getUserLibrary(currentUser.getUserId());

            return ResponseEntity.ok(userLibrary);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy thư viện người dùng", e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Không thể lấy thư viện người dùng"));
        }
    }

    /**
     * API thêm sách vào thư viện
     * @param bookId ID của sách cần thêm
     * @return Thông tin sách vừa được thêm vào thư viện
     */
    @PostMapping("/library")
    public ResponseEntity<?> addBookToLibrary(@RequestBody Integer bookId) {
        try {
            String username = tokenUtils.getCurrentUsername();
            Users currentUser = authService.getUserByUsername(username);

            if (currentUser == null) {
                logger.error("Không tìm thấy người dùng với username: {}", username);
                return ResponseEntity.notFound().build();
            }

            UserLibrary libraryEntry = userLibraryService.addBookToLibrary(currentUser.getUserId(), bookId);

            return ResponseEntity.ok(libraryEntry);
        } catch (Exception e) {
            logger.error("Lỗi khi thêm sách vào thư viện", e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Không thể thêm sách vào thư viện"));
        }
    }

    @PatchMapping("/library/{bookId}")
    public ResponseEntity<?> updateBookStatus(
            @PathVariable Integer bookId,
            @RequestBody Map<String, Object> updateData
    ) {
        try {
            String username = tokenUtils.getCurrentUsername();
            Users currentUser = authService.getUserByUsername(username);

            if (currentUser == null) {
                logger.error("Không tìm thấy người dùng với username: {}", username);
                return ResponseEntity.notFound().build();
            }

            UserLibrary userLibrary = new UserLibrary();

            // Convert String to enum
            if (updateData.containsKey("status")) {
                String statusString = (String) updateData.get("status");
                userLibrary.setStatus(UserLibrary.Status.valueOf(statusString.toUpperCase()));
            }

            // Set progress if provided
            if (updateData.containsKey("progress")) {
                userLibrary.setProgressPercentage((Integer) updateData.get("progress"));
            }

            UserLibrary updatedLibraryEntry = userLibraryService.updateBookStatus(
                    currentUser.getUserId(),
                    bookId,
                    userLibrary
            );

            return ResponseEntity.ok(updatedLibraryEntry);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid status provided", e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Trạng thái không hợp lệ"));
        } catch (Exception e) {
            logger.error("Lỗi khi cập nhật trạng thái sách", e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Không thể cập nhật trạng thái sách"));
        }
    }

    /**
     * API lấy lịch sử đọc sách của người dùng
     * @return Danh sách lịch sử đọc sách
     */
    @GetMapping("/reading-history")
    public ResponseEntity<?> getReadingHistory() {
        try {
            String username = tokenUtils.getCurrentUsername();
            Users currentUser = authService.getUserByUsername(username);

            if (currentUser == null) {
                logger.error("Không tìm thấy người dùng với username: {}", username);
                return ResponseEntity.notFound().build();
            }

            List<ReadingHistory> readingHistory = readingHistoryService.getUserReadingHistory(currentUser.getUserId());

            return ResponseEntity.ok(readingHistory);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy lịch sử đọc", e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Không thể lấy lịch sử đọc"));
        }
    }

    /**
     * API ghi nhận phiên đọc sách
     * @param readingSession Thông tin phiên đọc
     * @return Thông tin phiên đọc đã được ghi nhận
     */
    @PostMapping("/reading-history")
    public ResponseEntity<?> recordReadingSession(@RequestBody ReadingHistory readingSession) {
        try {
            String username = tokenUtils.getCurrentUsername();
            Users currentUser = authService.getUserByUsername(username);

            if (currentUser == null) {
                logger.error("Không tìm thấy người dùng với username: {}", username);
                return ResponseEntity.notFound().build();
            }

            // Đảm bảo session được gắn với user hiện tại
            readingSession.setUserId(currentUser.getUserId());

            ReadingHistory recordedSession = readingHistoryService.recordReadingSession(readingSession);

            return ResponseEntity.ok(recordedSession);
        } catch (Exception e) {
            logger.error("Lỗi khi ghi nhận phiên đọc", e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Không thể ghi nhận phiên đọc"));
        }
    }
}
