package com.example.Bibliotech_backend.repository;

import com.example.Bibliotech_backend.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Integer> {
}