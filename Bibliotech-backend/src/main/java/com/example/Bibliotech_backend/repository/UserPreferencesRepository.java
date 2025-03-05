package com.example.Bibliotech_backend.repository;

import com.example.Bibliotech_backend.model.UserPreferences;
import com.example.Bibliotech_backend.model.UserPreferencesId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, UserPreferencesId> {
    @Query("SELECT up FROM UserPreferences up WHERE up.userId = :userId")
    List<UserPreferences> findByUserId(@Param("userId") Integer userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserPreferences up WHERE up.userId = :userId")
    void deleteAllByUserId(@Param("userId") Integer userId);
}