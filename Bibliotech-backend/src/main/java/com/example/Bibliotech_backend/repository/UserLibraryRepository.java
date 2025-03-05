package com.example.Bibliotech_backend.repository;

import com.example.Bibliotech_backend.model.UserLibrary;
import com.example.Bibliotech_backend.model.UserLibraryId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserLibraryRepository extends JpaRepository<UserLibrary, UserLibraryId> {
    List<UserLibrary> findByUserId(Integer userId);
}