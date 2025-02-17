package com.example.Bibliotech_backend.service;

import com.example.Bibliotech_backend.dto.ProfileData;
import com.example.Bibliotech_backend.exception.BadRequestException;
import com.example.Bibliotech_backend.model.UserProfile;
import com.example.Bibliotech_backend.model.UserRegistrationStatus;
import com.example.Bibliotech_backend.repository.UserProfileRepository;
import com.example.Bibliotech_backend.repository.UserRegistrationStatusRepository;
import com.example.Bibliotech_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserRegistrationStatusService {
    @Autowired
    private UserRegistrationStatusRepository repository;

    @Autowired
    private UserRepository userRepository;

    public boolean isFirstLogin(Integer userId) {
        // First check if user exists
        if (!userRepository.existsById(userId)) {
            throw new BadRequestException("User not found");
        }

        // Check registration status
        return repository.findById(userId)
                .map(status -> {
                    // Consider it first login if either:
                    // 1. Profile is not completed
                    // 2. Profile completion date is null
                    return !status.isProfileCompleted() || status.getProfileCompletionDate() == null;
                })
                .orElseGet(() -> {
                    // If no status record exists, create one and return true (it's first login)
                    UserRegistrationStatus newStatus = new UserRegistrationStatus();
                    newStatus.setUserId(userId);
                    newStatus.setProfileCompleted(false);
                    repository.save(newStatus);
                    return true;
                });
    }

    public UserRegistrationStatus createStatus(Integer userId) {
        UserRegistrationStatus status = new UserRegistrationStatus();
        status.setUserId(userId);
        status.setProfileCompleted(false);
        return repository.save(status);
    }


    public void markProfileCompleted(Integer userId) {
        repository.findById(userId).ifPresent(status -> {
            status.setProfileCompleted(true);
            status.setProfileCompletionDate(LocalDateTime.now());
            repository.save(status);
        });
    }

    @Autowired
    private UserProfileRepository userProfileRepository;

    public void completeProfile(Integer userId, ProfileData profileData) {
        // Create or update user profile
        UserProfile profile = userProfileRepository.findById(userId)
                .orElse(new UserProfile());

        profile.setUserId(userId);
        if (profileData.getFullName() != null) profile.setFullName(profileData.getFullName());
        if (profileData.getPhone() != null) profile.setPhone(profileData.getPhone());
        if (profileData.getDob() != null) profile.setDob(profileData.getDob());
        if (profileData.getGender() != null) {
            try {
                profile.setGender(UserProfile.Gender.valueOf(profileData.getGender()));
            } catch (IllegalArgumentException e) {
                // Handle invalid gender value
            }
        }
        if (profileData.getAddress() != null) profile.setAddress(profileData.getAddress());
        if (profileData.getNationality() != null) profile.setNationality(profileData.getNationality());
        if (profileData.getBio() != null) profile.setBio(profileData.getBio());
        if (profileData.getProfilePictureUrl() != null) profile.setProfilePictureUrl(profileData.getProfilePictureUrl());

        userProfileRepository.save(profile);

        // Mark profile as completed
        markProfileCompleted(userId);
    }
}