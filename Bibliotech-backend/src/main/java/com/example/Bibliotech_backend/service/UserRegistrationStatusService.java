package com.example.Bibliotech_backend.service;

import com.example.Bibliotech_backend.dto.ProfileData;
import com.example.Bibliotech_backend.exception.BadRequestException;
import com.example.Bibliotech_backend.exception.ResourceNotFoundException;
import com.example.Bibliotech_backend.model.UserProfile;
import com.example.Bibliotech_backend.model.UserRegistrationStatus;
import com.example.Bibliotech_backend.model.Users;
import com.example.Bibliotech_backend.repository.UserProfileRepository;
import com.example.Bibliotech_backend.repository.UserRegistrationStatusRepository;
import com.example.Bibliotech_backend.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service managing user registration status.
 * Handles logic related to first login, profile completion, and registration status.
 */
@Service
public class UserRegistrationStatusService {
    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationStatusService.class);

    private final UserRegistrationStatusRepository statusRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final ModelMapper modelMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public UserRegistrationStatusService(
            UserRegistrationStatusRepository statusRepository,
            UserRepository userRepository,
            UserProfileRepository userProfileRepository,
            ModelMapper modelMapper) {
        this.statusRepository = statusRepository;
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Checks if this is the user's first login.
     *
     * @param userId The user ID.
     * @return true if this is the first login, otherwise false.
     * @throws ResourceNotFoundException if the user doesn't exist.
     */
    @Transactional(readOnly = true)
    public boolean isFirstLogin(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }

        return statusRepository.findById(userId)
                .map(status -> !status.isProfileCompleted())
                .orElse(true); // If no status exists, it's considered first login
    }

    /**
     * Creates a new registration status record for a user.
     * This should be called right after user creation.
     *
     * @param userId The user ID.
     * @return The newly created UserRegistrationStatus.
     * @throws BadRequestException if the user doesn't exist or if the userId is null.
     */
    @Transactional
    public UserRegistrationStatus createStatus(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        try {
            // Change 'user_id' to 'userId' to match your database column name
            jdbcTemplate.update(
                    "INSERT INTO UserRegistrationStatus (user_id, is_profile_completed) VALUES (?, ?)",
                    userId, false
            );

            // Now fetch what we just created
            Optional<UserRegistrationStatus> created = statusRepository.findById(userId);
            if (created.isPresent()) {
                return created.get();
            } else {
                throw new RuntimeException("Failed to retrieve created status record");
            }
        } catch (Exception e) {
            System.err.println("Error creating status: " + e.getMessage());
            throw new RuntimeException("Failed to create status: " + e.getMessage(), e);
        }
    }


    /**
     * Marks a user's profile as completed.
     *
     * @param userId The user ID.
     * @throws ResourceNotFoundException if the status record doesn't exist.
     */
    @Transactional
    public void markProfileCompleted(Integer userId) {
        UserRegistrationStatus status = statusRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration status not found for user ID: " + userId));

        status.setProfileCompleted(true);
        status.setProfileCompletionDate(LocalDateTime.now());
        statusRepository.save(status);

        // Also update user's registration status if needed
        userRepository.findById(userId).ifPresent(user -> {
            if (user.getRegistrationStatus() != Users.RegistrationStatus.COMPLETED) {
                user.setRegistrationStatus(Users.RegistrationStatus.COMPLETED);
                userRepository.save(user);
            }
        });

        logger.info("Marked profile as completed for user ID: {}", userId);
    }

    /**
     * Completes a user's profile by saving data to UserProfile and updating registration status.
     *
     * @param userId The user ID.
     * @param profileData The user's profile data.
     * @throws BadRequestException if profile data is invalid.
     */
    @Transactional
    public void completeProfile(Integer userId, ProfileData profileData) {
        if (profileData == null) {
            throw new BadRequestException("Profile data cannot be null");
        }

        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }

        // Get or create user profile
        UserProfile profile = userProfileRepository.findById(userId)
                .orElse(new UserProfile());

        profile.setUserId(userId);

        // Map and validate profile data
        modelMapper.map(profileData, profile);

        // Save profile
        try {
            userProfileRepository.save(profile);
            markProfileCompleted(userId);
            logger.info("Completed profile for user ID: {}", userId);
        } catch (Exception e) {
            logger.error("Failed to complete profile for user ID: {}", userId, e);
            throw new BadRequestException("Failed to save profile: " + e.getMessage());
        }
    }

    /**
     * Updates a user's registration status.
     *
     * @param userId The user ID.
     * @param isProfileCompleted The profile completion status.
     * @return true if update was successful, false otherwise.
     */
    @Transactional
    public boolean updateRegistrationStatus(Integer userId, Boolean isProfileCompleted) {
        try {
            if (userId == null || isProfileCompleted == null) {
                logger.warn("Invalid parameters for updating registration status");
                return false;
            }

            // Get or create status
            UserRegistrationStatus status = statusRepository.findById(userId)
                    .orElseGet(() -> {
                        UserRegistrationStatus newStatus = new UserRegistrationStatus();
                        newStatus.setUserId(userId);
                        return newStatus;
                    });

            // Update status
            status.setProfileCompleted(isProfileCompleted);

            // Set completion date if completing for the first time
            if (isProfileCompleted && status.getProfileCompletionDate() == null) {
                status.setProfileCompletionDate(LocalDateTime.now());
            }

            statusRepository.save(status);

            // Also update user's registration status if needed
            userRepository.findById(userId).ifPresent(user -> {
                Users.RegistrationStatus newStatus = isProfileCompleted
                        ? Users.RegistrationStatus.COMPLETED
                        : Users.RegistrationStatus.PENDING;

                if (user.getRegistrationStatus() != newStatus) {
                    user.setRegistrationStatus(newStatus);
                    userRepository.save(user);
                }
            });

            logger.info("Updated registration status for user ID: {}", userId);
            return true;
        } catch (Exception e) {
            logger.error("Failed to update registration status for user ID: {}", userId, e);
            return false;
        }
    }

    /**
     * Retrieves a user's registration status.
     *
     * @param userId The user ID.
     * @return The user's registration status, or null if not found.
     */
    @Transactional(readOnly = true)
    public UserRegistrationStatus getStatus(Integer userId) {
        return statusRepository.findById(userId).orElse(null);
    }
}