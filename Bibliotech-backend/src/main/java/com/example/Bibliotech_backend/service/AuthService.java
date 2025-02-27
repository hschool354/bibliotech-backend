package com.example.Bibliotech_backend.service;

import com.example.Bibliotech_backend.dto.AuthResponse;
import com.example.Bibliotech_backend.dto.LoginRequest;
import com.example.Bibliotech_backend.dto.SignUpRequest;
import com.example.Bibliotech_backend.exception.BadRequestException;
import com.example.Bibliotech_backend.repository.UserRepository;
import com.example.Bibliotech_backend.security.JwtTokenProvider;
import com.example.Bibliotech_backend.model.Users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service để xử lý xác thực và đăng ký người dùng.
 */
@Service
public class AuthService {

    /** Repository để truy vấn thông tin người dùng từ cơ sở dữ liệu. */
    @Autowired
    private UserRepository userRepository;

    /** Mã hóa mật khẩu cho người dùng. */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /** Cung cấp token JWT để xác thực. */
    @Autowired
    private JwtTokenProvider tokenProvider;

    /** Service để tạo ID người dùng duy nhất. */
    @Autowired
    private IdGeneratorService idGeneratorService;

    /** Service để quản lý trạng thái đăng ký của người dùng. */
    @Autowired
    private UserRegistrationStatusService registrationStatusService;

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
}