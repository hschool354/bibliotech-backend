package com.example.Bibliotech_backend.service;

import com.example.Bibliotech_backend.dto.AuthResponse;
import com.example.Bibliotech_backend.dto.LoginRequest;
import com.example.Bibliotech_backend.dto.SignUpRequest;
import com.example.Bibliotech_backend.exception.BadRequestException;
import com.example.Bibliotech_backend.model.Users;
import com.example.Bibliotech_backend.repository.UserRepository;
import com.example.Bibliotech_backend.security.JwtTokenProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service để xử lý xác thực và đăng ký người dùng.
 */
@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    /**
     * Repository để truy vấn thông tin người dùng từ cơ sở dữ liệu.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Mã hóa mật khẩu cho người dùng.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Cung cấp token JWT để xác thực.
     */
    @Autowired
    private JwtTokenProvider tokenProvider;

    /**
     * Service để tạo ID người dùng duy nhất.
     */
    @Autowired
    private IdGeneratorService idGeneratorService;

    /**
     * Service để quản lý trạng thái đăng ký của người dùng.
     */
    @Autowired
    private UserRegistrationStatusService registrationStatusService;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Đăng ký người dùng mới.
     *
     * @param request Đối tượng {@link SignUpRequest} chứa thông tin đăng ký.
     * @return {@link AuthResponse} chứa token và thông tin người dùng.
     * @throws BadRequestException Nếu email hoặc username đã tồn tại.
     */
    public AuthResponse signup(SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }

        // Tạo một đối tượng Users mới với thông tin đăng ký
        Users user = new Users();
        user.setUserId(idGeneratorService.generateUserId());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRegistrationStatus(Users.RegistrationStatus.PENDING);
        user.setIsAdmin(false);

        // Lưu người dùng vào cơ sở dữ liệu
        Users savedUser = userRepository.save(user);

        // Tạo token xác thực cho người dùng mới
        String token = tokenProvider.generateToken(savedUser);
        registrationStatusService.createStatus(user.getUserId());

        return new AuthResponse(token, user.getUsername(), user.getEmail(), true);
    }

    /**
     * Đăng nhập người dùng bằng email hoặc username.
     *
     * @param request Đối tượng {@link LoginRequest} chứa thông tin đăng nhập.
     * @return {@link AuthResponse} chứa token và thông tin người dùng.
     * @throws BadRequestException Nếu thông tin đăng nhập không hợp lệ.
     */
    public AuthResponse login(LoginRequest request) {
        Optional<Users> userOptional = userRepository.findByEmail(request.getIdentifier());

        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByUsername(request.getIdentifier());
        }

        if (userOptional.isEmpty()) {
            throw new BadRequestException("Invalid email/username or password");
        }

        if (request.getIdentifier() == null || request.getIdentifier().trim().isEmpty()) {
            throw new BadRequestException("Email/username cannot be empty");
        }

        Users user = userOptional.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid email/username or password");
        }

        // Tạo token xác thực
        String token = tokenProvider.generateToken(user);

        // Kiểm tra xem đây có phải lần đăng nhập đầu tiên không
        boolean isFirstLogin = registrationStatusService.isFirstLogin(user.getUserId());

        return new AuthResponse(token, user.getUsername(), user.getEmail(), isFirstLogin);
    }

    /**
     * Cập nhật thời gian đăng nhập cuối cùng của người dùng
     *
     * @param userId ID của người dùng
     */
    public void updateLastLoginDate(Integer userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setLastLoginDate(LocalDateTime.now());
            userRepository.save(user);
        });
    }

    /**
     * Kiểm tra xác thực người dùng
     *
     * @param username Tên đăng nhập
     * @param password Mật khẩu
     * @return Đối tượng Users nếu xác thực thành công, null nếu xác thực thất bại
     */
    public Users authenticateUser(String username, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            return getUserByUsername(username);
        } catch (AuthenticationException e) {
            logger.error("Xác thực không thành công cho người dùng: {}", username, e);
            return null;
        }
    }

    /**
     * Tìm kiếm người dùng theo ID.
     *
     * @param userId ID của người dùng.
     * @return Đối tượng {@link Users} nếu tìm thấy.
     * @throws BadRequestException Nếu không tìm thấy người dùng với ID đã cho.
     */
    public Users getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found with id: " + userId));
    }

    /**
     * Lấy thông tin người dùng theo tên đăng nhập
     *
     * @param username Tên đăng nhập
     * @return Đối tượng Users, null nếu không tìm thấy
     */
    public Users getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    /**
     * Kiểm tra xem username đã tồn tại chưa
     *
     * @param username Tên đăng nhập cần kiểm tra
     * @return true nếu đã tồn tại, false nếu chưa
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Kiểm tra xem email đã tồn tại chưa
     *
     * @param email Email cần kiểm tra
     * @return true nếu đã tồn tại, false nếu chưa
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}