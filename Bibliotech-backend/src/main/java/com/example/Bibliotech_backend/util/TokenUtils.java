package com.example.Bibliotech_backend.util;

import com.example.Bibliotech_backend.exception.BadRequestException;
import com.example.Bibliotech_backend.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
}
