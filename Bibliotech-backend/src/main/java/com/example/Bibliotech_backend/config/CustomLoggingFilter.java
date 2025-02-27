package com.example.Bibliotech_backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Bộ lọc tuỳ chỉnh để ghi log thông tin của tất cả các request đến hệ thống.
 * <p>
 * Lớp này mở rộng {@link OncePerRequestFilter}, đảm bảo rằng bộ lọc này chỉ chạy một lần cho mỗi request.
 * Nó ghi log URL, phương thức HTTP và các header của request trước khi chuyển tiếp request đến các bộ lọc khác.
 * </p>
 */
public class CustomLoggingFilter extends OncePerRequestFilter {

    /**
     * Ghi log thông tin của request trước khi request được xử lý tiếp theo.
     *
     * @param request      Đối tượng {@link HttpServletRequest} chứa thông tin của request.
     * @param response     Đối tượng {@link HttpServletResponse} chứa thông tin của response.
     * @param filterChain  Chuỗi filter để tiếp tục xử lý request.
     * @throws ServletException Nếu có lỗi xảy ra trong quá trình lọc request.
     * @throws IOException      Nếu có lỗi I/O xảy ra.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Ghi log URL của request
        System.out.println("Request URL: " + request.getRequestURL());

        // Ghi log phương thức HTTP của request (GET, POST, PUT, DELETE, ...)
        System.out.println("Request Method: " + request.getMethod());

        // Ghi log danh sách các header trong request
        System.out.println("Request Headers: " + Collections.list(request.getHeaderNames()));

        // Tiếp tục xử lý request qua các filter khác trong chuỗi lọc
        filterChain.doFilter(request, response);
    }
}
