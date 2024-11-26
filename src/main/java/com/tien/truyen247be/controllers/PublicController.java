package com.tien.truyen247be.controllers;

import com.tien.truyen247be.payload.response.ComicResponse;
import com.tien.truyen247be.payload.response.UserResponse;
import com.tien.truyen247be.security.services.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PublicController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private PageService pageService;

    @Autowired
    private GenreService genreService;

    @Autowired
    private ComicService comicService;

    @Autowired
    private ChapterService chapterService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @GetMapping("/public/comic/chapter/{id}/pages")
    public ResponseEntity<?> getPagesByChapterId(@PathVariable Long id) {
        return pageService.getPagesByChapterId(id);
    }

    @GetMapping("/public/genres")
    public ResponseEntity<?> getAllGenre() {
        return genreService.getAllGenre();
    }

    @GetMapping("/public/genres-name")
    public ResponseEntity<?> getAllGenreName() {
        return genreService.getAllGenreName();
    }

    @GetMapping("/public/comments/{comicId}")
    public ResponseEntity<?> getAllComments(@PathVariable Long comicId) {
        return commentService.getComments(comicId);
    }

    // Lấy danh sách truyện tranh được kích hoạt
    @GetMapping("/public/comics/list")
    public ResponseEntity<?> getAllComicsIsActive() {
        return comicService.getAllComicIsActive();
    }

    // Lấy danh sách thể loại truyện theo id truyện
    @GetMapping("/public/comic/{id}/genres")
    public ResponseEntity<?> getAllGenreByComicId(@PathVariable Long id) {
        return comicService.getAllGenreByComicId(id);
    }

    // Lấy truyện theo id
    @GetMapping("/public/comics/{id}")
    public ResponseEntity<?> getComicById(@PathVariable Long id) {
        return comicService.getComicById(id);
    }

    @GetMapping("/public/comics/genre")
    public ResponseEntity<?> getComicsByGenre(@RequestParam String genreName) {
        return comicService.getComicsByGenreName(genreName);
    }

    // API tìm kiếm truyện theo tên
    @GetMapping("/public/comics/search")
    public ResponseEntity<List<ComicResponse>> searchComics(@RequestParam("name") String name) {
        List<ComicResponse> comicResponses = comicService.searchComicsByName(name);
        return ResponseEntity.ok(comicResponses);
    }

    @GetMapping("/public/comic/{id}/chapters/list")
    public ResponseEntity<?> getAllChapters(@PathVariable Long id) {
        return chapterService.getAllChapters(id);
    }

    @GetMapping("/public/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse userResponse = userDetailsService.getUserById(id);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/public/users/{id}/avatar")
    public ResponseEntity<?> createPages(
            @Valid @PathVariable Long id,
            @RequestParam("file") MultipartFile files
    ) throws IOException {
        return ResponseEntity.ok(userDetailsService.createAvatar(id, files));
    }
}
