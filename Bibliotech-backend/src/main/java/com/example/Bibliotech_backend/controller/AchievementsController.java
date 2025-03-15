package com.example.Bibliotech_backend.controller;

import com.example.Bibliotech_backend.model.UserAchievement;
import com.example.Bibliotech_backend.service.UserAchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/achievements")
public class AchievementsController {

    private final UserAchievementService userAchievementService;

    @Autowired
    public AchievementsController(UserAchievementService userAchievementService) {
        this.userAchievementService = userAchievementService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserAchievement>> getUserAchievements(@PathVariable Integer userId) {
        return ResponseEntity.ok(userAchievementService.getUserAchievements(userId));
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<UserAchievement> addUserAchievement(
            @PathVariable Integer userId,
            @RequestBody UserAchievement achievement) {
        return ResponseEntity.ok(userAchievementService.saveUserAchievement(achievement));
    }
}
