/**
 * Lớp JwtAuthenticationFilter chịu trách nhiệm lọc và xác thực JWT trong mỗi yêu cầu HTTP.
 * Lớp này mở rộng OncePerRequestFilter để đảm bảo JWT chỉ được xử lý một lần mỗi request.
 */
package com.example.Bibliotech_backend.security;

import com.example.Bibliotech_backend.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Bộ lọc xác thực JWT để xử lý token trong mỗi request HTTP.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Đối tượng hỗ trợ xử lý JWT, cung cấp các phương thức tạo và kiểm tra token.
     */
    @Autowired
    private JwtTokenProvider tokenProvider;

    /**
     * Dịch vụ tải thông tin người dùng từ cơ sở dữ liệu.
     */
    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * Xử lý lọc request để trích xuất và xác thực JWT.
     * Nếu token hợp lệ, thiết lập thông tin xác thực trong SecurityContext.
     *
     * @param request      Yêu cầu HTTP
     * @param response     Phản hồi HTTP
     * @param filterChain  Chuỗi bộ lọc tiếp theo
     * @throws ServletException Nếu có lỗi xảy ra trong quá trình xử lý servlet
     * @throws IOException Nếu có lỗi IO xảy ra
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // Lấy token JWT từ request
            String jwt = getJwtFromRequest(request);
            logger.debug("Raw Authorization header: " + request.getHeader("Authorization"));
            logger.debug("Extracted JWT: " + jwt);

            // Kiểm tra nếu token hợp lệ
            if (StringUtils.hasText(jwt)) {
                // Giải mã JWT để lấy userId
                Integer userId = tokenProvider.getUserIdFromJWT(jwt);
                logger.debug("Extracted userId: " + userId);

                // Tải thông tin người dùng từ cơ sở dữ liệu
                UserDetails userDetails = userDetailsService.loadUserById(userId);
                logger.debug("Loaded userDetails: " + userDetails.getUsername());

                // Tạo đối tượng xác thực và đặt vào SecurityContext
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
            logger.error("Full exception details:", ex);
        }

        // Tiếp tục xử lý request với bộ lọc tiếp theo
        filterChain.doFilter(request, response);
    }

    /**
     * Trích xuất token JWT từ tiêu đề Authorization của request.
     *
     * @param request Yêu cầu HTTP
     * @return Token JWT nếu hợp lệ, null nếu không tìm thấy hoặc không hợp lệ.
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // Kiểm tra token có hợp lệ và bắt đầu với "Bearer " không
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
