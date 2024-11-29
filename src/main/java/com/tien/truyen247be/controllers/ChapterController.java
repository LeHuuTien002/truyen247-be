package com.tien.truyen247be.controllers;

import com.tien.truyen247be.payload.request.ChapterRequest;
import com.tien.truyen247be.security.services.ChapterService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ChapterController {
    @Autowired
    private ChapterService chapterService;

    // Tạo một chương mới
    @PostMapping("/admin/comic/{id}/chapters/create")
    public ResponseEntity<?> createChapter(@Valid @RequestBody ChapterRequest chapterRequest, @PathVariable Long id) {
        return ResponseEntity.ok(chapterService.createChapter(id, chapterRequest));
    }

    @PutMapping("/admin/comic/{comicId}/chapters/{chapterId}")
    public ResponseEntity<?> updateChapter(@PathVariable Long comicId, @PathVariable Long chapterId, @Valid @RequestBody ChapterRequest chapterRequest) {
        return ResponseEntity.ok(chapterService.updateChapter(comicId, chapterId, chapterRequest));
    }

    @DeleteMapping("/admin/comic/{comicId}/chapters/{chapterId}")
    public ResponseEntity<?> deleteChapter(@PathVariable Long comicId, @PathVariable Long chapterId) {
        return ResponseEntity.ok(chapterService.deleteChapter(comicId, chapterId));
    }

    @GetMapping("/comic/chapters/{id}")
    public ResponseEntity<?> getChapterById(@PathVariable Long id) {
        return chapterService.getChapterById(id);
    }

    @GetMapping("/admin/comic/{id}/chapters/list")
    public ResponseEntity<?> getAllChapters(@PathVariable Long id) {
        return chapterService.getAllChapters(id);
    }

    @GetMapping("/chapters/{comicId}")
    public ResponseEntity<?> getChaptersByComicId(@PathVariable Long comicId, @RequestParam Long userId) {
        return chapterService.getChaptersByComicId(comicId, userId);
    }
}
