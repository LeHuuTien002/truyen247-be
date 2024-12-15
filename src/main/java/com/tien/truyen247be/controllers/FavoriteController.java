package com.tien.truyen247be.controllers;

import com.tien.truyen247be.security.services.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {
    @Autowired
    private FavoriteService favoriteService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getFavorites(@PathVariable Long userId) {
        return favoriteService.getFavorites(userId);
    }

    @PostMapping("/{userId}/{comicId}")
    public ResponseEntity<String> addFavorite(@PathVariable Long userId, @PathVariable Long comicId) {
        favoriteService.addFavorite(userId, comicId);
        return ResponseEntity.ok("Đã yêu thích truyện!");
    }

    @DeleteMapping("/{userId}/{comicId}")
    public ResponseEntity<String> removeFavorite(@PathVariable Long userId, @PathVariable Long comicId) {
        favoriteService.remoteFavorite(userId, comicId);
        return ResponseEntity.ok("Đã bỏ yêu thích truyện");
    }

    @GetMapping("/{userId}/{comicId}/is-favorite")
    public ResponseEntity<Boolean> isFavorite(@PathVariable Long userId, @PathVariable Long comicId) {
        boolean isFavorite = favoriteService.isFavorite(userId, comicId);
        return ResponseEntity.ok(isFavorite);
    }
}
