package com.tien.truyen247be.controllers;


import com.tien.truyen247be.payload.response.ComicResponse;
import com.tien.truyen247be.security.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private ViewService viewService;

    @Autowired
    private QRPaymentService qrPaymentService;

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

    @GetMapping("/public/comics/list")
    public ResponseEntity<?> getAllComicsIsActive() {
        return comicService.getAllComicIsActive();
    }

    @GetMapping("/public/comic/{id}/genres")
    public ResponseEntity<?> getAllGenreByComicId(@PathVariable Long id) {
        return comicService.getAllGenreByComicId(id);
    }

    @GetMapping("/public/comics/{id}")
    public ResponseEntity<?> getComicById(@PathVariable Long id) {
        return comicService.getComicById(id);
    }

    @GetMapping("/public/comics/{id}/chapters")
    public ResponseEntity<?> getInfoComicForChapterList(@PathVariable Long id) {
        return comicService.getInfoComicForChapterList(id);
    }

    @GetMapping("/public/comics/genre")
    public ResponseEntity<?> getComicsByGenre(@RequestParam String genreName) {
        return comicService.getComicsByGenreName(genreName);
    }

    @GetMapping("/public/comics/search")
    public ResponseEntity<List<ComicResponse>> searchComics(@RequestParam("name") String name) {
        List<ComicResponse> comicResponses = comicService.searchComicsByName(name);
        return ResponseEntity.ok(comicResponses);
    }

    @GetMapping("/public/chapters/{comicId}")
    public ResponseEntity<?> getChaptersByComicId(@PathVariable Long comicId) {
        return chapterService.getChaptersByComicId(comicId);
    }

    @GetMapping("/public/QRPayment")
    public ResponseEntity<?> getQRPayment() {
        return qrPaymentService.getQRPayments();
    }

    @GetMapping("/public/top-comics-view")
    public ResponseEntity<?> getTopComicsView() {
        return ResponseEntity.ok(viewService.getTop5Views());
    }
}
