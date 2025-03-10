package com.example.Bibliotech_backend.repository;

import com.example.Bibliotech_backend.model.CultivationLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface CultivationLevelRepository extends JpaRepository<CultivationLevel, Integer> {
    @Query("SELECT cl FROM CultivationLevel cl WHERE cl.levelName = :levelName")
    Optional<CultivationLevel> findByLevelName(@Param("levelName") String levelName);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO CultivationLevels (level_id, level_name, level_description, books_required, reading_time_required, icon_url) " +
            "VALUES (:levelId, :levelName, :levelDescription, :booksRequired, :readingTimeRequired, :iconUrl)", nativeQuery = true)
    void insertWithId(@Param("levelId") Integer levelId,
                      @Param("levelName") String levelName,
                      @Param("levelDescription") String levelDescription,
                      @Param("booksRequired") Integer booksRequired,
                      @Param("readingTimeRequired") Integer readingTimeRequired,
                      @Param("iconUrl") String iconUrl);}