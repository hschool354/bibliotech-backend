package com.example.Bibliotech_backend.repository;

import com.example.Bibliotech_backend.model.ReadingHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReadingHistoryRepository extends JpaRepository<ReadingHistory, Integer> {
    List<ReadingHistory> findByUserIdOrderBySessionStartDesc(Integer userId);
}