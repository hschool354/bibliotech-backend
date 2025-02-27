package com.example.Bibliotech_backend.service;

import com.example.Bibliotech_backend.model.Users;
import com.example.Bibliotech_backend.repository.UserRepository;
import com.example.Bibliotech_backend.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service tùy chỉnh để xử lý logic xác thực người dùng trong hệ thống.
 * Triển khai giao diện {@link UserDetailsService} của Spring Security.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    /**
     * Repository để truy vấn thông tin người dùng từ cơ sở dữ liệu.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Tải thông tin người dùng dựa trên tên đăng nhập.
     *
     * @param username Tên đăng nhập của người dùng.
     * @return Đối tượng {@link UserDetails} chứa thông tin người dùng.
     * @throws UsernameNotFoundException Nếu không tìm thấy người dùng với tên đăng nhập đã cho.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Tìm kiếm người dùng theo tên đăng nhập trong cơ sở dữ liệu
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Trả về một đối tượng UserPrincipal được tạo từ thông tin của người dùng
        return UserPrincipal.create(user);
    }

    /**
     * Tải thông tin người dùng dựa trên ID.
     *
     * @param id ID của người dùng.
     * @return Đối tượng {@link UserDetails} chứa thông tin người dùng.
     * @throws UsernameNotFoundException Nếu không tìm thấy người dùng với ID đã cho.
     */
    public UserDetails loadUserById(Integer id) {
        // Tìm kiếm người dùng theo ID trong cơ sở dữ liệu
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        // Trả về một đối tượng UserPrincipal được tạo từ thông tin của người dùng
        return UserPrincipal.create(user);
    }
}