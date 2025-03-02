package com.example.Bibliotech_backend.service;

import com.example.Bibliotech_backend.dto.ProfileData;
import com.example.Bibliotech_backend.model.UserProfile;
import com.example.Bibliotech_backend.model.Users;
import com.example.Bibliotech_backend.repository.UserProfileRepository;
import com.example.Bibliotech_backend.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

/**
 * Service để xử lý thông tin profile người dùng.
 */
@Service
public class UserProfileService {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileService.class);

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Lấy thông tin profile của người dùng theo ID
     *
     * @param userId ID của người dùng
     * @return Đối tượng UserProfile, null nếu không tìm thấy
     */
    public UserProfile getUserProfileById(Integer userId) {
        return userProfileRepository.findById(userId).orElse(null);
    }

    /**
     * Cập nhật thông tin profile của người dùng theo ID
     *
     * @param userId      ID của người dùng
     * @param profileData Dữ liệu cập nhật profile
     * @return Đối tượng UserProfile đã cập nhật, null nếu không tìm thấy người dùng
     */
    @Transactional
    public UserProfile updateUserProfile(Integer userId, ProfileData profileData) {
        Optional<Users> userOpt = userRepository.findById(userId);

        if (userOpt.isPresent()) {
            Users user = userOpt.get();

            // Update user timestamp
            user.setUpdatedAt(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));
            userRepository.save(user);

            // Update and return the profile
            UserProfile profile = userProfileRepository.findById(userId)
                    .orElse(new UserProfile());

            profile.setUserId(userId);
            modelMapper.map(profileData, profile);
            return userProfileRepository.save(profile);
        }

        return null;
    }
}