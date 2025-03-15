package com.example.Bibliotech_backend.repository;

import com.example.Bibliotech_backend.model.UserCultivation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCultivationRepository extends JpaRepository<UserCultivation, Integer> {
}