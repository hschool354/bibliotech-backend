package com.example.Bibliotech_backend.service;

import com.example.Bibliotech_backend.model.CultivationLevel;
import com.example.Bibliotech_backend.repository.CultivationLevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CultivationLevelService {

    @Autowired
    private CultivationLevelRepository cultivationLevelRepository;

    public List<CultivationLevel> getAllCultivationLevels() {
        return cultivationLevelRepository.findAll();
    }

    public Optional<CultivationLevel> getCultivationLevelById(Integer id) {
        return cultivationLevelRepository.findById(id);
    }

    public CultivationLevel saveCultivationLevel(CultivationLevel cultivationLevel) {
        return cultivationLevelRepository.save(cultivationLevel);
    }

    public void deleteCultivationLevel(Integer id) {
        cultivationLevelRepository.deleteById(id);
    }

    @Transactional
    public void saveCultivationLevelWithId(CultivationLevel level) {
        cultivationLevelRepository.insertWithId(
                level.getLevelId(),
                level.getLevelName(),
                level.getLevelDescription(),
                level.getBooksRequired(),
                level.getReadingTimeRequired(),
                level.getIconUrl()
        );
    }
}