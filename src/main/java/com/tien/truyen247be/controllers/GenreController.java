package com.tien.truyen247be.controllers;

import com.tien.truyen247be.payload.request.GenreRequest;
import com.tien.truyen247be.security.services.GenreService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/theloais")
public class GenreController {
    @Autowired
    private GenreService genreService;

    // Lấy danh sách của tất cả thể loại truyện
    @GetMapping
    public ResponseEntity<?> getAllTheLoai() {
        return genreService.getAllGenre();
    }

    // Tạo thể loại truyện mới
    @PostMapping
    public ResponseEntity<?> createTheLoai(@Valid @RequestBody GenreRequest genreRequest) {
        return ResponseEntity.ok(genreService.createGenre(genreRequest));
    }

    // Cập nhật thể loại truyện theo id
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTheLoai(@Valid @PathVariable Long id, @RequestBody GenreRequest genreRequest) {
        return ResponseEntity.ok(genreService.updateGenre(id, genreRequest));
    }

    // Xóa thể loại theo id
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTheLoai(@PathVariable Long id) {
        return ResponseEntity.ok(genreService.deleteGenre(id));
    }
}
