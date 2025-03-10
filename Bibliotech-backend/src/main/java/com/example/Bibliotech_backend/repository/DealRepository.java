package com.example.Bibliotech_backend.repository;

import com.example.Bibliotech_backend.model.Deal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DealRepository extends JpaRepository<Deal, Integer> {
    List<Deal> findByIsActiveTrue();
}