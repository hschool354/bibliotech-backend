package com.example.Bibliotech_backend.security;

import com.example.Bibliotech_backend.model.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    /**
     * Chuỗi bí mật được sử dụng để ký và xác minh token JWT.
     * Giá trị này được lấy từ file cấu hình ứng dụng.
     */
    @Value("${app.jwtSecret}")
    private String jwtSecret;

    /**
     * Tạo token JWT cho người dùng.
     *
     * @param user Đối tượng người dùng để tạo token.
     * @return Chuỗi token JWT.
     */
    public String generateToken(Users user) {
        // Chuyển đổi chuỗi bí mật thành mảng byte
        byte[] keyBytes = jwtSecret.getBytes();
        // Tạo khóa bảo mật từ chuỗi bí mật
        Key key = Keys.hmacShaKeyFor(keyBytes);

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 86400000); // Token có hiệu lực trong 1 ngày

        // Lấy userId bằng reflection do thuộc tính có thể là private
        Integer userId = extractUserIdFromUser(user);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Lấy userId từ đối tượng Users bằng cách sử dụng reflection.
     *
     * @param user Đối tượng Users cần lấy userId.
     * @return Giá trị userId của người dùng.
     * @throws RuntimeException Nếu không thể lấy userId từ đối tượng Users.
     */
    private Integer extractUserIdFromUser(Users user) {
        try {
            // Truy cập trường private userId bằng reflection
            Field userIdField = Users.class.getDeclaredField("userId");
            userIdField.setAccessible(true);
            return (Integer) userIdField.get(user);
        } catch (Exception e) {
            throw new RuntimeException("Không thể lấy userId từ đối tượng Users", e);
        }
    }

    /**
     * Trích xuất userId từ token JWT.
     *
     * @param token Chuỗi token JWT cần phân tích.
     * @return Giá trị userId của người dùng.
     */
    public Integer getUserIdFromJWT(String token) {
        // Chuyển đổi chuỗi bí mật thành mảng byte
        byte[] keyBytes = jwtSecret.getBytes();
        // Tạo khóa bảo mật từ chuỗi bí mật
        Key key = Keys.hmacShaKeyFor(keyBytes);

        // Giải mã token để lấy các claims
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Integer.parseInt(claims.getSubject());
    }
}