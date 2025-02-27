package com.example.Bibliotech_backend.config;

import com.example.Bibliotech_backend.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Cấu hình bảo mật cho ứng dụng.
 * Quản lý xác thực, quyền truy cập, CORS và session policy.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * Bộ lọc xác thực JWT để kiểm tra token trong request.
     */
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Cung cấp một bean để mã hóa mật khẩu sử dụng thuật toán BCrypt.
     *
     * @return PasswordEncoder sử dụng BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Cấu hình AuthenticationManager để quản lý xác thực người dùng.
     *
     * @param authConfig Cấu hình xác thực của Spring Security
     * @return AuthenticationManager để quản lý xác thực
     * @throws Exception Nếu có lỗi xảy ra trong quá trình lấy AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Cấu hình chuỗi bộ lọc bảo mật.
     * Thiết lập CORS, CSRF, session management, xác thực và đăng ký bộ lọc JWT.
     *
     * @param http Cấu hình bảo mật của Spring Security
     * @return SecurityFilterChain để Spring Security sử dụng
     * @throws Exception Nếu có lỗi xảy ra trong quá trình cấu hình
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Cấu hình CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Tắt CSRF để hỗ trợ API stateless
                .csrf(csrf -> csrf.disable())
                // Đặt chính sách session là STATELESS vì ứng dụng sử dụng JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Cấu hình quyền truy cập API
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/signup", "/api/auth/login", "/api/auth/complete-profile").permitAll()
                        .anyRequest().authenticated()
                )
                // Thêm bộ lọc JWT trước bộ lọc UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Cấu hình chính sách CORS để cho phép truy cập từ frontend.
     *
     * @return CorsConfigurationSource chứa thông tin cấu hình CORS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Chỉ cho phép request từ frontend chạy trên localhost:5173
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        // Cho phép các phương thức HTTP cụ thể
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Cho phép các header cụ thể
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        // Cho phép gửi cookie cùng request
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}