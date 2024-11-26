package com.tien.truyen247be.controllers;

import com.tien.truyen247be.payload.request.ComicRequest;
import com.tien.truyen247be.security.services.ComicService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ComicController {
    @Autowired
    private ComicService comicService;

    // Tạo một truyện tranh mới
    @PostMapping("/admin/comics/create")
    public ResponseEntity<?> createComic(@Valid @RequestPart("comicRequest") ComicRequest comicRequest, @RequestPart("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(comicService.createComic(comicRequest, file));
    }

    // Cập nhật truyện tranh
    @PutMapping("/admin/comics/{id}/update")
    public ResponseEntity<?> updateComic(@Valid @PathVariable Long id, @RequestPart("comicRequest") ComicRequest comicRequest, @RequestPart("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(comicService.updateComic(id, comicRequest, file));
    }

    // Xóa truyện
    @DeleteMapping("/admin/comics/{id}/delete")
    public ResponseEntity<?> deleteComic(@PathVariable Long id) {
        return ResponseEntity.ok(comicService.deleteComic(id));
    }

    // Lấy danh sách truyện tranh
    @GetMapping("/admin/comics/list")
    public ResponseEntity<?> getAllComics() {
        return comicService.getAllComic();
    }
}
