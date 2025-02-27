package com.example.Bibliotech_backend.controller;

import com.example.Bibliotech_backend.dto.AuthResponse;
import com.example.Bibliotech_backend.dto.LoginRequest;
import com.example.Bibliotech_backend.dto.SignUpRequest;
import com.example.Bibliotech_backend.model.Users;
import com.example.Bibliotech_backend.security.JwtTokenProvider;
import com.example.Bibliotech_backend.service.AuthService;
import com.example.Bibliotech_backend.service.UserRegistrationStatusService;
import com.example.Bibliotech_backend.util.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Controller quản lý các API liên quan đến xác thực người dùng.
 */
@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRegistrationStatusService registrationStatusService;
    private final TokenUtils tokenUtils;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    /**
     * Constructor khởi tạo AuthController với các service cần thiết.
     *
     * @param authService Dịch vụ xác thực người dùng
     * @param jwtTokenProvider Provider cho JWT token
     * @param registrationStatusService Dịch vụ kiểm tra trạng thái đăng ký
     * @param tokenUtils Tiện ích xử lý token
     */
    public AuthController(
            AuthService authService,
            JwtTokenProvider jwtTokenProvider,
            UserRegistrationStatusService registrationStatusService,
            TokenUtils tokenUtils) {
        this.authService = authService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.registrationStatusService = registrationStatusService;
        this.tokenUtils = tokenUtils;
    }

    /**
     * API đăng ký tài khoản mới.
     *
     * @param request Dữ liệu đăng ký từ người dùng
     * @return AuthResponse chứa thông tin xác thực
     */
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignUpRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    /**
     * API đăng nhập.
     *
     * @param request Dữ liệu đăng nhập từ người dùng
     * @return AuthResponse chứa thông tin xác thực
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Kiểm tra xem người dùng có phải lần đầu đăng nhập hay không.
     *
     * @param token JWT token từ header Authorization
     * @return true nếu đây là lần đầu đăng nhập, ngược lại false
     */
    @GetMapping("/check-first-login")
    public ResponseEntity<Boolean> checkFirstLogin(@RequestHeader("Authorization") String token) {
        Integer userId = tokenUtils.validateTokenAndGetUserId(token);

        logger.debug("Checking first login status for user ID: {}", userId);
        boolean isFirstLogin = registrationStatusService.isFirstLogin(userId);
        return ResponseEntity.ok(isFirstLogin);
    }

    /**
     * Kiểm tra xem người dùng có phải admin hay không.
     *
     * @param token JWT token từ header Authorization
     * @return true nếu người dùng là admin, ngược lại false
     */
    @GetMapping("/check-admin")
    public ResponseEntity<Boolean> checkAdminStatus(@RequestHeader("Authorization") String token) {
        Integer userId = tokenUtils.validateTokenAndGetUserId(token);
        logger.debug("Checking admin status for user ID: {}", userId);

        Users user = authService.getUserById(userId);
        logger.debug("Admin status for user {}: {}", userId, user.getIsAdmin());

        return ResponseEntity.ok(user.getIsAdmin());
    }

    /**
     * API đăng xuất.
     *
     * @param token JWT token từ header Authorization
     * @return Phản hồi thành công nếu đăng xuất thành công
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        Integer userId = tokenUtils.validateTokenAndGetUserId(token);
        logger.debug("User ID {} logging out", userId);

        // Tùy chọn: Thêm logic xử lý đăng xuất phía server nếu cần
        return ResponseEntity.ok().body("Logged out successfully");
    }
}
