package com.example.Bibliotech_backend.service;

import com.example.Bibliotech_backend.model.UserPreferences;
import com.example.Bibliotech_backend.repository.UserPreferencesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserPreferencesService {
    private static final Logger logger = LoggerFactory.getLogger(UserPreferencesService.class);

    @Autowired
    private UserPreferencesRepository userPreferencesRepository;

    @Transactional(readOnly = true)
    public List<UserPreferences> getUserPreferences(Integer userId) {
        logger.debug("Fetching preferences for userId: {}", userId);
        List<UserPreferences> preferences = userPreferencesRepository.findByUserId(userId);
        logger.debug("Number of preferences found: {}", preferences.size());
        return preferences;
    }

    @Transactional
    public List<UserPreferences> updateUserPreferences(Integer userId, List<UserPreferences> preferences) {
        logger.debug("Updating preferences for userId: {}", userId);

        // Validate input
        if (preferences == null || preferences.isEmpty()) {
            logger.warn("No preferences provided for update");
            return Collections.emptyList();
        }

        // Ensure all preferences are associated with the current user
        preferences.forEach(pref -> {
            pref.setUserId(userId);
            if (pref.getLastInteractionDate() == null) {
                pref.setLastInteractionDate(LocalDateTime.now());
            }
        });

        // Delete existing preferences
        userPreferencesRepository.deleteAllByUserId(userId);
        logger.debug("Existing preferences deleted for userId: {}", userId);

        // Save new preferences
        List<UserPreferences> savedPreferences = userPreferencesRepository.saveAll(preferences);
        logger.debug("Number of preferences saved: {}", savedPreferences.size());

        return savedPreferences;
    }
}