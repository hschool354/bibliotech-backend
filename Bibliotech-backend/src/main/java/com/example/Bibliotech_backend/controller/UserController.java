package com.example.Bibliotech_backend.controller;

import com.example.Bibliotech_backend.dto.ProfileData;
import com.example.Bibliotech_backend.security.JwtTokenProvider;
import com.example.Bibliotech_backend.service.UserRegistrationStatusService;
import com.example.Bibliotech_backend.util.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            UserRegistrationStatusService registrationStatusService,
            TokenUtils tokenUtils) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.registrationStatusService = registrationStatusService;
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
}
