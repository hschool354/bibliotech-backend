package com.example.Bibliotech_backend.util;

import com.example.Bibliotech_backend.exception.BadRequestException;
import com.example.Bibliotech_backend.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class TokenUtils {
    private final JwtTokenProvider jwtTokenProvider;
    private static final Logger logger = LoggerFactory.getLogger(TokenUtils.class);

    public TokenUtils(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Xác thực định dạng token và trích xuất ID người dùng
     *
     * @param token Chuỗi token đầy đủ từ header Authorization
     * @return ID của người dùng được trích xuất từ token
     * @throws BadRequestException nếu token không hợp lệ
     */
    public Integer validateTokenAndGetUserId(String token) {
        // Kiểm tra xem token có giá trị và có bắt đầu bằng "Bearer " không
        if (token == null || !token.startsWith("Bearer ")) {
            logger.error("Định dạng token không hợp lệ");
            throw new BadRequestException("Định dạng token không hợp lệ");
        }

        try {
            // Loại bỏ tiền tố "Bearer " để lấy phần token thực sự
            String rawToken = token.substring(7);
            logger.debug("Token nhận được: {}", rawToken);

            // Trích xuất ID người dùng từ token
            Integer userId = jwtTokenProvider.getUserIdFromJWT(rawToken);
            logger.debug("ID người dùng được trích xuất: {}", userId);

            return userId;
        } catch (Exception e) {
            logger.error("Lỗi khi xử lý token", e);
            throw new BadRequestException("Lỗi khi xử lý token: " + e.getMessage());
        }
    }

    /**
     * Lấy tên người dùng từ context bảo mật hiện tại
     *
     * @return Tên người dùng đã xác thực
     * @throws BadRequestException nếu không tìm thấy thông tin xác thực
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            logger.error("Không tìm thấy thông tin xác thực người dùng");
            throw new BadRequestException("Không tìm thấy thông tin xác thực người dùng");
        }

        String username = authentication.getName();
        logger.debug("Tên người dùng hiện tại: {}", username);

        return username;
    }

    /**
     * Lấy ID người dùng từ token trong context bảo mật hiện tại
     *
     * @return ID của người dùng đã xác thực
     * @throws BadRequestException nếu không tìm thấy thông tin xác thực
     */
    public Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            logger.error("Không tìm thấy thông tin xác thực người dùng");
            throw new BadRequestException("Không tìm thấy thông tin xác thực người dùng");
        }

        try {
            return jwtTokenProvider.getUserIdFromAuthentication(authentication);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy ID người dùng từ thông tin xác thực", e);
            throw new BadRequestException("Lỗi khi lấy thông tin người dùng: " + e.getMessage());
        }
    }
}
