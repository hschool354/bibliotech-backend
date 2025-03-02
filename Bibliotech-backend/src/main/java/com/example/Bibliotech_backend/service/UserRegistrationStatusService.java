package com.example.Bibliotech_backend.service;

import com.example.Bibliotech_backend.dto.ProfileData;
import com.example.Bibliotech_backend.exception.BadRequestException;
import com.example.Bibliotech_backend.model.UserProfile;
import com.example.Bibliotech_backend.model.UserRegistrationStatus;
import com.example.Bibliotech_backend.repository.UserProfileRepository;
import com.example.Bibliotech_backend.repository.UserRegistrationStatusRepository;
import com.example.Bibliotech_backend.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * Service quản lý trạng thái đăng ký của người dùng.
 * Xử lý logic liên quan đến lần đăng nhập đầu tiên, hoàn thành hồ sơ và trạng thái đăng ký.
 */
@Service
public class UserRegistrationStatusService {

    @Autowired
    private UserRegistrationStatusRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Kiểm tra xem đây có phải lần đăng nhập đầu tiên của người dùng không.
     *
     * @param userId ID của người dùng.
     * @return true nếu đây là lần đăng nhập đầu tiên, ngược lại false.
     * @throws BadRequestException nếu người dùng không tồn tại.
     */
    public boolean isFirstLogin(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new BadRequestException("User not found");
        }

        return repository.findById(userId)
                .map(status -> !status.isProfileCompleted() || status.getProfileCompletionDate() == null)
                .orElseGet(() -> {
                    UserRegistrationStatus newStatus = new UserRegistrationStatus();
                    newStatus.setUserId(userId);
                    newStatus.setProfileCompleted(false);
                    repository.save(newStatus);
                    return true;
                });
    }

    /**
     * Tạo một bản ghi trạng thái đăng ký mới cho người dùng.
     *
     * @param userId ID của người dùng.
     * @return Đối tượng UserRegistrationStatus mới được tạo.
     */
    public UserRegistrationStatus createStatus(Integer userId) {
        UserRegistrationStatus status = new UserRegistrationStatus();
        status.setUserId(userId);
        status.setProfileCompleted(false);
        return repository.save(status);
    }

    /**
     * Đánh dấu hồ sơ của người dùng là đã hoàn thành.
     *
     * @param userId ID của người dùng.
     */
    public void markProfileCompleted(Integer userId) {
        repository.findById(userId).ifPresent(status -> {
            status.setProfileCompleted(true);
            status.setProfileCompletionDate(LocalDateTime.now());
            repository.save(status);
        });
    }

    /**
     * Hoàn thành hồ sơ của người dùng bằng cách lưu dữ liệu vào UserProfile
     * và cập nhật trạng thái đăng ký.
     *
     * @param userId ID của người dùng.
     * @param profileData Dữ liệu hồ sơ của người dùng.
     */
    public void completeProfile(Integer userId, ProfileData profileData) {
        UserProfile profile = userProfileRepository.findById(userId)
                .orElse(new UserProfile());
        profile.setUserId(userId);

        modelMapper.map(profileData, profile);
        userProfileRepository.save(profile);
        markProfileCompleted(userId);
    }

    /**
     * Cập nhật trạng thái đăng ký của người dùng
     *
     * @param userId ID của người dùng
     * @param isProfileCompleted Trạng thái hoàn thành profile
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public Boolean updateRegistrationStatus(Integer userId, Boolean isProfileCompleted) {
        try {
            UserRegistrationStatus status = repository.findById(userId)
                    .orElse(new UserRegistrationStatus());

            status.setUserId(userId);
            status.setProfileCompleted(isProfileCompleted);

            if (isProfileCompleted && status.getProfileCompletionDate() == null) {
                status.setProfileCompletionDate(LocalDateTime.from(Instant.now()));
            }

            repository.save(status);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}