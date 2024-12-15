package com.tien.truyen247be.controllers;

import com.tien.truyen247be.payload.request.PageRequest;
import com.tien.truyen247be.security.services.PageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PageController {
    @Autowired
    private PageService pageService;

    @PostMapping("/admin/comic/chapter/{id}/create")
    public ResponseEntity<?> createPages(
            @Valid @PathVariable Long id,
            @RequestPart("files") List<MultipartFile> files
    ) throws IOException {
        return ResponseEntity.ok(pageService.createPages(id, files));
    }

    @PutMapping("/admin/comic/chapter/pages/{id}/update")
    public ResponseEntity<?> updatePage(@Valid @PathVariable Long id, @RequestPart("pageRequest") PageRequest pageRequest, @RequestPart("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(pageService.updatePage(id, pageRequest, file));
    }

    @DeleteMapping("/admin/comic/chapter/{chapterId}/pages/{pageId}")
    public ResponseEntity<?> deletePage(@PathVariable Long chapterId, @PathVariable Long pageId) {
        return ResponseEntity.ok(pageService.deletePage(chapterId, pageId));
    }

    @GetMapping("/admin/comic/chapter/{id}/pages")
    public ResponseEntity<?> getPagesByChapterId(@PathVariable Long id) {
        return pageService.getAllPageByChapterId(id);
    }
}
