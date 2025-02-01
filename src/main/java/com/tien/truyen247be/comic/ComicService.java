package com.tien.truyen247be.comic;

import com.tien.truyen247be.exception.ResourceNotFoundException;
import com.tien.truyen247be.exception.GenreAlreadyExistsException;
import com.tien.truyen247be.comment.CommentRepository;
import com.tien.truyen247be.favorite.FavoriteRepository;
import com.tien.truyen247be.genre.GenreRepository;
import com.tien.truyen247be.view.View;
import com.tien.truyen247be.genre.Genre;
import com.tien.truyen247be.comic.dto.ComicRequest;
import com.tien.truyen247be.comic.dto.ComicResponse;
import com.tien.truyen247be.view.ViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional
public class ComicService {
    @Autowired
    private ComicRepository comicRepository;

    @Autowired
    S3Service s3Service;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private ViewRepository viewRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private CommentRepository commentRepository;

    public List<ComicResponse> searchComicsByName(String name) {
        List<Comic> comics = comicRepository.findByNameContainingIgnoreCase(name);
        List<ComicResponse> comicResponses = new ArrayList<>();
        for (Comic comic : comics) {
            ComicResponse comicResponse = new ComicResponse();
            comicResponse.setId(comic.getId());
            comicResponse.setName(comic.getName());
            comicResponse.setThumbnail(comic.getThumbnail());
            comicResponse.setViews(viewRepository.findViewsCountByComicId(comic.getId()));
            comicResponse.setNumberOfComment(commentRepository.countByComicId(comic.getId()));
            comicResponse.setFavorites(favoriteRepository.countByComicId(comic.getId()));
            comicResponses.add(comicResponse);
        }
        return comicResponses;
    }

    public ResponseEntity<?> createComic(ComicRequest comicRequest, MultipartFile file) throws IOException {
        if (!comicRepository.existsByName(comicRequest.getName())) {

            Set<Genre> genres = new HashSet<>(genreRepository.findAllById(comicRequest.getGenreIds()));
            if (genres.isEmpty()) {
                throw new GenreAlreadyExistsException("Không tìm thấy thể loại nào với các ID đã cung cấp.");
            }

            String thumbnail = s3Service.uploadFile(file);

            Comic comic = new Comic();
            comic.setName(comicRequest.getName());
            comic.setOtherName(comicRequest.getOtherName());
            comic.setAuthor(comicRequest.getAuthor());
            comic.setContent(comicRequest.getContent());
            comic.setStatus(comicRequest.getStatus());
            comic.setActivate(comicRequest.isActivate());
            comic.setCreateAt(LocalDateTime.now());
            comic.setUpdateAt(LocalDateTime.now());
            comic.setThumbnail(thumbnail);

            comic.setGenres(genres);
            Comic savedComic = comicRepository.save(comic);

            View view = new View();
            view.setViewsCount(0L);
            view.setComic(savedComic);
            view.setLastUpdated(LocalDateTime.now());
            viewRepository.save(view);

            return ResponseEntity.ok("Tạo truyện mới thành công!");
        } else {
            throw new GenreAlreadyExistsException("Tên truyện đã tồn tại. Vui lòng chọn tên khác.");
        }
    }

    public ResponseEntity<?> updateComic(Long idComic, ComicRequest comicRequest, MultipartFile file) throws IOException {
        Optional<Comic> comicOptional = comicRepository.findById(idComic);
        if (comicOptional.isEmpty()) {
            throw new GenreAlreadyExistsException("Id truyện này không tồn tại!");
        } else {

            Comic comic = comicOptional.get();
            if (comicRepository.existsByName(comicRequest.getName()) && !Objects.equals(comicRequest.getName(), comic.getName())) {
                throw new GenreAlreadyExistsException("Tên truyện đã tồn tại. Vui lòng chọn tên khác.");
            } else {
                Set<Genre> genres = new HashSet<>(genreRepository.findAllById(comicRequest.getGenreIds()));
                if (genres.isEmpty()) {
                    throw new GenreAlreadyExistsException("Không tìm thấy thể loại nào với các ID đã cung cấp.");
                }

                comic.setName(comicRequest.getName());
                comic.setOtherName(comicRequest.getOtherName());
                comic.setStatus(comicRequest.getStatus());
                comic.setContent(comicRequest.getContent());
                comic.setAuthor(comicRequest.getAuthor());
                comic.setActivate(comicRequest.isActivate());
                comic.setUpdateAt(LocalDateTime.now());
                if (file != null && !file.isEmpty()) {
                    comic.setThumbnail(s3Service.updateFile(file));
                }
                comic.setGenres(genres);

                comicRepository.save(comic);

                return ResponseEntity.ok("Cập nhật truyện thành công");
            }
        }
    }

    public ResponseEntity<ComicResponse> getAllGenreByComicId(Long comicId) {
        Comic comic = comicRepository.findById(comicId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy truyện với ID: " + comicId));

        ComicResponse comicResponse = new ComicResponse();

        Set<ComicResponse.GenreListByComicIdResponse> genreListByComicIdResponses = comic.getGenres().stream().map(genre -> {
            ComicResponse.GenreListByComicIdResponse genreListByComicIdResponse = new ComicResponse.GenreListByComicIdResponse();
            genreListByComicIdResponse.setId(genre.getId());
            genreListByComicIdResponse.setName(genre.getName());
            return genreListByComicIdResponse;
        }).collect(Collectors.toSet());
        comicResponse.setGenres(genreListByComicIdResponses);
        return ResponseEntity.ok(comicResponse);
    }

    public ResponseEntity<?> deleteComic(Long idComic) {
        if (!comicRepository.existsById(idComic)) {
            throw new GenreAlreadyExistsException("Id thể truyện này không tồn tại!");
        } else {
            comicRepository.deleteById(idComic);
        }
        return ResponseEntity.ok("Đã xóa thành công!");
    }

    public Page<ComicResponse> getAllComic(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comic> comicPage = comicRepository.findAll(pageable);
        if (!comicPage.isEmpty()) {
            Page<ComicResponse> comicResponsePage = comicPage.map(comic -> {
                ComicResponse comicResponse = new ComicResponse();

                comicResponse.setId(comic.getId());
                comicResponse.setName(comic.getName());
                comicResponse.setOtherName(comic.getOtherName());
                comicResponse.setAuthor(comic.getAuthor());
                comicResponse.setContent(comic.getContent());
                comicResponse.setStatus(comic.getStatus());
                comicResponse.setActivate(comic.isActivate());
                comicResponse.setThumbnail(comic.getThumbnail());
                comicResponse.setCreateAt(comic.getCreateAt());
                comicResponse.setUpdateAt(comic.getUpdateAt());
                return comicResponse;
            });
            return comicResponsePage;
        } else {
            return null;
        }
    }

    public ResponseEntity<List<ComicResponse>> getAllComicIsActive() {
        List<Comic> comicList = comicRepository.findAll();
        if (!comicList.isEmpty()) {
            List<ComicResponse> comicResponseList = new ArrayList<>();
            for (Comic comic : comicList) {
                if (comic.isActivate()) {
                    ComicResponse comicResponse = new ComicResponse();

                    comicResponse.setId(comic.getId());
                    comicResponse.setName(comic.getName());
                    comicResponse.setOtherName(comic.getOtherName());
                    comicResponse.setAuthor(comic.getAuthor());
                    comicResponse.setContent(comic.getContent());
                    comicResponse.setViews(viewRepository.findViewsCountByComicId(comic.getId()));
                    comicResponse.setFavorites(favoriteRepository.countByComicId(comic.getId()));
                    comicResponse.setNumberOfComment(commentRepository.countByComicId(comic.getId()));
                    comicResponse.setStatus(comic.getStatus());
                    comicResponse.setActivate(comic.isActivate());
                    comicResponse.setThumbnail(comic.getThumbnail());
                    comicResponse.setCreateAt(comic.getCreateAt());
                    comicResponse.setUpdateAt(comic.getUpdateAt());

                    comicResponseList.add(comicResponse);
                }
            }
            return ResponseEntity.ok(comicResponseList);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    public ResponseEntity<ComicResponse> getComicById(Long id) {
        Optional<Comic> comicOptional = comicRepository.findById(id);
        if (comicOptional.isEmpty()) {
            throw new GenreAlreadyExistsException("Id truyện này không tồn tại!");
        } else {
            Comic comic = comicOptional.get();
            ComicResponse comicResponse = new ComicResponse();

            comicResponse.setId(comic.getId());
            comicResponse.setName(comic.getName());
            comicResponse.setOtherName(comic.getOtherName());
            comicResponse.setAuthor(comic.getAuthor());
            comicResponse.setContent(comic.getContent());
            comicResponse.setViews(viewRepository.findViewsCountByComicId(id));
            comicResponse.setFavorites(favoriteRepository.countByComicId(id));
            comicResponse.setStatus(comic.getStatus());
            comicResponse.setActivate(comic.isActivate());
            comicResponse.setThumbnail(comic.getThumbnail());
            comicResponse.setCreateAt(comic.getCreateAt());
            comicResponse.setUpdateAt(comic.getUpdateAt());

            return ResponseEntity.ok(comicResponse);
        }
    }

    public ResponseEntity<ComicResponse> getInfoComicForChapterList(Long id) {
        Optional<Comic> comicOptional = comicRepository.findById(id);
        if (comicOptional.isEmpty()) {
            throw new GenreAlreadyExistsException("Id truyện này không tồn tại!");
        } else {
            Comic comic = comicOptional.get();
            ComicResponse comicResponse = new ComicResponse();

            comicResponse.setId(comic.getId());
            comicResponse.setName(comic.getName());
            comicResponse.setUpdateAt(comic.getUpdateAt());

            return ResponseEntity.ok(comicResponse);
        }
    }

    public ResponseEntity<List<ComicResponse>> getComicsByGenreName(String genreName) {
        Genre genre = genreRepository.findByName(genreName);
        if (genre == null) {
            throw new RuntimeException("Thể loại không tìm thấy");
        }

        Set<Comic> comicList = genre.getComics();

        if (!comicList.isEmpty()) {
            List<ComicResponse> comicResponseList = new ArrayList<>();
            for (Comic comic : comicList) {
                ComicResponse comicResponse = new ComicResponse();

                comicResponse.setId(comic.getId());
                comicResponse.setName(comic.getName());
                comicResponse.setViews(viewRepository.findViewsCountByComicId(comic.getId()));
                comicResponse.setFavorites(favoriteRepository.countByComicId(comic.getId()));
                comicResponse.setThumbnail(comic.getThumbnail());
                comicResponse.setNumberOfComment(commentRepository.countByComicId(comic.getId()));

                comicResponseList.add(comicResponse);
            }
            return ResponseEntity.ok(comicResponseList);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }
}