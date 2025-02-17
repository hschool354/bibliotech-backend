package com.example.Bibliotech_backend.repository;

import com.example.Bibliotech_backend.model.UserRegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRegistrationStatusRepository extends JpaRepository<UserRegistrationStatus, Integer> {
}
