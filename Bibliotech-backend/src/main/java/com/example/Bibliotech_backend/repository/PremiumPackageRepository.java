package com.example.Bibliotech_backend.repository;

import com.example.Bibliotech_backend.model.PremiumPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PremiumPackageRepository extends JpaRepository<PremiumPackage, Integer> {
    List<PremiumPackage> findByIsActiveTrue();
    boolean existsByPackageName(String packageName);
}