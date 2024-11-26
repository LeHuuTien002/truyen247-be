package com.tien.truyen247be.controllers;

import com.tien.truyen247be.payload.request.GenreRequest;
import com.tien.truyen247be.security.services.GenreService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class GenreController {
    @Autowired
    private GenreService genreService;

    @PostMapping("/admin/genres")
    public ResponseEntity<?> createGenre(@Valid @RequestBody GenreRequest genreRequest) {
        return ResponseEntity.ok(genreService.createGenre(genreRequest));
    }

    @PutMapping("/admin/genres/{id}")
    public ResponseEntity<?> updateGenre(@Valid @PathVariable Long id, @RequestBody GenreRequest genreRequest) {
        return ResponseEntity.ok(genreService.updateGenre(id, genreRequest));
    }

    @DeleteMapping("/admin/genres/{id}")
    public ResponseEntity<?> deleteGenre(@PathVariable Long id) {
        return ResponseEntity.ok(genreService.deleteGenre(id));
    }
}
