package com.example.Bibliotech_backend.controller;

import com.example.Bibliotech_backend.model.CultivationLevel;
import com.example.Bibliotech_backend.service.CultivationLevelService;
import com.example.Bibliotech_backend.service.IdGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/cultivation/levels")
public class CultivationController {

    @Autowired
    private CultivationLevelService cultivationLevelService;

    @Autowired
    private IdGeneratorService idGeneratorService;

    @GetMapping
    public List<CultivationLevel> getAllCultivationLevels() {
        return cultivationLevelService.getAllCultivationLevels();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CultivationLevel> getCultivationLevelById(@PathVariable Integer id) {
        return cultivationLevelService.getCultivationLevelById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public CultivationLevel createCultivationLevel(@RequestBody CultivationLevel cultivationLevel) {
        // Đặt levelId là null để cơ sở dữ liệu tự động tạo
        cultivationLevel.setLevelId(null);

        // Sử dụng phương thức save() thông thường
        return cultivationLevelService.saveCultivationLevel(cultivationLevel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCultivationLevel(@PathVariable Integer id) {
        cultivationLevelService.deleteCultivationLevel(id);
        return ResponseEntity.noContent().build();
    }
}