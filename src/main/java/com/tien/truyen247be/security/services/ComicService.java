package com.tien.truyen247be.security.services;

import com.tien.truyen247be.Exception.ResourceNotFoundException;
import com.tien.truyen247be.Exception.GenreAlreadyExistsException;
import com.tien.truyen247be.models.Comic;
import com.tien.truyen247be.models.View;
import com.tien.truyen247be.models.Genre;
import com.tien.truyen247be.payload.request.ComicRequest;
import com.tien.truyen247be.payload.response.ComicResponse;
import com.tien.truyen247be.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
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

    // Tạo truyện tranh
    public ResponseEntity<?> createComic(ComicRequest comicRequest, MultipartFile file) throws IOException {
        if (!comicRepository.existsByName(comicRequest.getName())) {

            // Tìm danh sách thể loại từ database theo danh sách ID
            Set<Genre> genres = new HashSet<>(genreRepository.findAllById(comicRequest.getGenreIds()));
            // Kiểm tra nếu không tìm thấy thể loại nào
            if (genres.isEmpty()) {
                throw new GenreAlreadyExistsException("Không tìm thấy thể loại nào với các ID đã cung cấp.");
            }

            String thumbnail = s3Service.uploadFile(file);

            // Tạo một đối tượng Comic
            Comic comic = new Comic();
            comic.setName(comicRequest.getName());
            comic.setOtherName(comicRequest.getOtherName());
            comic.setAuthor(comicRequest.getAuthor());
            comic.setContent(comicRequest.getContent());
            comic.setStatus(comicRequest.getStatus());
            comic.setActivate(comicRequest.isActivate());
            comic.setUpdateAt(LocalDateTime.now());
            comic.setThumbnail(thumbnail);

            // Gán danh sách thể loại vào Comic
            comic.setGenres(genres);
            Comic savedComic = comicRepository.save(comic);

            // Tạo view
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

    // Cập nhật truyện
    public ResponseEntity<?> updateComic(Long idComic, ComicRequest comicRequest, MultipartFile file) throws IOException {
        Optional<Comic> comicOptional = comicRepository.findById(idComic);
        if (comicOptional.isEmpty()) {
            throw new GenreAlreadyExistsException("Id truyện này không tồn tại!");
        } else {

            Comic comic = comicOptional.get();
            if (comicRepository.existsByName(comicRequest.getName()) && !Objects.equals(comicRequest.getName(), comic.getName())) {
                throw new GenreAlreadyExistsException("Tên truyện đã tồn tại. Vui lòng chọn tên khác.");
            } else {
                // Tìm danh sách thể loại từ database theo danh sách ID
                Set<Genre> genres = new HashSet<>(genreRepository.findAllById(comicRequest.getGenreIds()));
                // Kiểm tra nếu không tìm thấy thể loại nào
                if (genres.isEmpty()) {
                    throw new GenreAlreadyExistsException("Không tìm thấy thể loại nào với các ID đã cung cấp.");
                }


                comic.setName(comicRequest.getName());
                comic.setOtherName(comicRequest.getOtherName());
                comic.setStatus(comicRequest.getStatus());
                comic.setContent(comicRequest.getContent());
                comic.setAuthor(comicRequest.getAuthor());
                comic.setActivate(comicRequest.isActivate());
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

    // Xóa truyện
    public ResponseEntity<?> deleteComic(Long idComic) {
        // Kiểm tra xem ID có tồn tại trong cơ sở dữ liệu không
        if (!comicRepository.existsById(idComic)) {
            // Nếu ID không tồn tại, trả về phản hồi lỗi
            throw new GenreAlreadyExistsException("Id thể truyện này không tồn tại!");
        } else {
            comicRepository.deleteById(idComic);
        }
        return ResponseEntity.ok("Đã xóa thành công!");
    }

    // Lấy ds truyện
    public ResponseEntity<List<ComicResponse>> getAllComic() {
        List<Comic> comicList = comicRepository.findAll();
        if (!comicList.isEmpty()) {
            List<ComicResponse> comicResponseList = new ArrayList<>();
            for (Comic comic : comicList) {
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

                comicResponseList.add(comicResponse);
            }
            return ResponseEntity.ok(comicResponseList);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    // Lấy ds truyện
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

    // Lấy chi tiết truyện tranh theo ID
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

    // Lấy chi tiết truyện tranh theo ID cho ADMIN
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