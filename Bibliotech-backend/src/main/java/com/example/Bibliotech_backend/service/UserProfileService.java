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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
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

    @Autowired
    private JdbcTemplate jdbcTemplate;

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

            // Check if profile exists
            boolean profileExists = userProfileRepository.existsById(userId);

            // Convert gender from string to enum, safely handling empty strings
            String genderStr = profileData.getGender();
            UserProfile.Gender gender = null;
            if (genderStr != null && !genderStr.trim().isEmpty()) {
                try {
                    gender = UserProfile.Gender.valueOf(genderStr);
                } catch (IllegalArgumentException e) {
                    logger.warn("Invalid gender value: {}", genderStr);
                }
            }

            // Properly handle empty strings
            String phone = (profileData.getPhone() != null && !profileData.getPhone().trim().isEmpty())
                    ? profileData.getPhone() : null;

            String fullName = (profileData.getFullName() != null && !profileData.getFullName().trim().isEmpty())
                    ? profileData.getFullName() : null;

            String address = (profileData.getAddress() != null && !profileData.getAddress().trim().isEmpty())
                    ? profileData.getAddress() : null;

            String nationality = (profileData.getNationality() != null && !profileData.getNationality().trim().isEmpty())
                    ? profileData.getNationality() : null;

            String bio = (profileData.getBio() != null && !profileData.getBio().trim().isEmpty())
                    ? profileData.getBio() : null;

            String profilePictureUrl = (profileData.getProfilePictureUrl() != null && !profileData.getProfilePictureUrl().trim().isEmpty())
                    ? profileData.getProfilePictureUrl() : null;

            // Handle empty date
            LocalDate dob = profileData.getDob() != null ? profileData.getDob() : null;

            if (profileExists) {
                // Update existing profile using UPDATE instead of INSERT
                jdbcTemplate.update(
                        "UPDATE UserProfiles SET " +
                                "full_name = ?, phone = ?, dob = ?, gender = ?, address = ?, " +
                                "nationality = ?, bio = ?, profile_picture_url = ? " +
                                "WHERE user_id = ?",
                        fullName,
                        phone,
                        dob,
                        gender != null ? gender.name() : null,
                        address,
                        nationality,
                        bio,
                        profilePictureUrl,
                        userId
                );
            } else {
                // Insert new profile
                jdbcTemplate.update(
                        "INSERT INTO UserProfiles (" +
                                "user_id, full_name, phone, dob, gender, address, nationality, bio, profile_picture_url" +
                                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        userId,
                        fullName,
                        phone,
                        dob,
                        gender != null ? gender.name() : null,
                        address,
                        nationality,
                        bio,
                        profilePictureUrl
                );
            }

            // Fetch and return the updated or newly created profile
            return userProfileRepository.findById(userId).orElse(null);
        }

        return null;
    }
}