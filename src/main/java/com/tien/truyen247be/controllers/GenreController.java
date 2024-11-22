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

    // Lấy danh sách của tất cả thể loại truyện
    @GetMapping("/admin/genres")
    public ResponseEntity<?> getAllGenre() {
        return genreService.getAllGenre();
    }

    // Lấy danh sách của tất cả thể loại truyện
    @GetMapping("/genres")
    public ResponseEntity<?> getAllGenreName() {
        return genreService.getAllGenreName();
    }

    // Tạo thể loại truyện mới
    @PostMapping("/admin/genres")
    public ResponseEntity<?> createGenre(@Valid @RequestBody GenreRequest genreRequest) {
        return ResponseEntity.ok(genreService.createGenre(genreRequest));
    }

    // Cập nhật thể loại truyện theo id
    @PutMapping("/admin/genres/{id}")
    public ResponseEntity<?> updateGenre(@Valid @PathVariable Long id, @RequestBody GenreRequest genreRequest) {
        return ResponseEntity.ok(genreService.updateGenre(id, genreRequest));
    }

    // Xóa thể loại theo id
    @DeleteMapping("/admin/genres/{id}")
    public ResponseEntity<?> deleteGenre(@PathVariable Long id) {
        return ResponseEntity.ok(genreService.deleteGenre(id));
    }
}
