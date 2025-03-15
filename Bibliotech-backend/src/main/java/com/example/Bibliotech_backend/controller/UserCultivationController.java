package com.example.Bibliotech_backend.controller;

import com.example.Bibliotech_backend.model.UserCultivation;
import com.example.Bibliotech_backend.service.UserCultivationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user-cultivation")
public class UserCultivationController {

    @Autowired
    private UserCultivationService userCultivationService;

    @GetMapping
    public List<UserCultivation> getAllUserCultivations() {
        return userCultivationService.getAllUserCultivations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserCultivation> getUserCultivationById(@PathVariable Integer id) {
        Optional<UserCultivation> userCultivation = userCultivationService.getUserCultivationById(id);
        return userCultivation.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public UserCultivation createUserCultivation(@RequestBody UserCultivation userCultivation) {
        return userCultivationService.saveUserCultivation(userCultivation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserCultivation(@PathVariable Integer id) {
        userCultivationService.deleteUserCultivation(id);
        return ResponseEntity.noContent().build();
    }
}