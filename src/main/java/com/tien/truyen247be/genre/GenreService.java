package com.tien.truyen247be.genre;

import com.tien.truyen247be.exception.GenreAlreadyExistsException;
import com.tien.truyen247be.comic.Comic;
import com.tien.truyen247be.genre.dto.GenreRequest;
import com.tien.truyen247be.genre.dto.GenreNameResponse;
import com.tien.truyen247be.genre.dto.GenreResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Transactional
@Service
public class GenreService {
    @Autowired
    private GenreRepository genreRepository;

    public ResponseEntity<List<GenreResponse>> getAllGenre() {
        List<Genre> theLoaiList = genreRepository.findAll();
        if (!theLoaiList.isEmpty()) {
            List<GenreResponse> genreResponseList = new ArrayList<>();
            for (Genre genre : theLoaiList) {
                GenreResponse genreResponse = new GenreResponse();

                genreResponse.setId(genre.getId());
                genreResponse.setName(genre.getName());
                genreResponse.setDescription(genre.getDescription());
                genreResponse.setCreateAt(genre.getCreateAt());
                genreResponse.setUpdateAt(genre.getUpdateAt());

                genreResponseList.add(genreResponse);
            }
            return ResponseEntity.ok(genreResponseList);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    public ResponseEntity<List<GenreNameResponse>> getAllGenreName() {
        List<Genre> theLoaiList = genreRepository.findAll();
        if (!theLoaiList.isEmpty()) {
            List<GenreNameResponse> genreNameResponseList = new ArrayList<>();
            for (Genre genre : theLoaiList) {
                GenreNameResponse genreNameResponse = new GenreNameResponse();

                genreNameResponse.setGenreId(genre.getId());
                genreNameResponse.setGenreName(genre.getName());

                genreNameResponseList.add(genreNameResponse);
            }
            return ResponseEntity.ok(genreNameResponseList);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    public ResponseEntity<?> createGenre(GenreRequest genreRequest) {
        if (!genreRepository.existsByName(genreRequest.getName())) {
            Genre genre = new Genre();
            genre.setName(genreRequest.getName());
            genre.setDescription(genreRequest.getDescription());
            genre.setCreateAt(LocalDateTime.now());
            genre.setUpdateAt(LocalDateTime.now());
            genreRepository.save(genre);
            return ResponseEntity.ok("Tạo thể loại truyện thành công!");
        } else {
            throw new GenreAlreadyExistsException("Tên thể loại đã tồn tại. Vui lòng chọn tên khác.");
        }
    }

    public ResponseEntity<?> updateGenre(Long genreId, GenreRequest genreRequest) {
        Optional<Genre> genreOptional = genreRepository.findById(genreId);
        if (genreOptional.isPresent()) {
            Genre genre = genreOptional.get();
            if (genreRepository.existsByName(genreRequest.getName()) && !Objects.equals(genreOptional.get().getName(), genreRequest.getName())) {
                throw new GenreAlreadyExistsException("Tên thể loại truyện đã tồn tại!");
            } else {
                genre.setName(genreRequest.getName());
                genre.setDescription(genreRequest.getDescription());
                genre.setUpdateAt(LocalDateTime.now());
                genreRepository.save(genre);
                return ResponseEntity.ok("Cập nhật thể loại truyện thành công!");
            }
        } else {
            throw new GenreAlreadyExistsException("Id thể loại truyện này không tồn tại!");
        }
    }

    public ResponseEntity<?> deleteGenre(Long theLoaiId) {
        Genre genre = genreRepository.findById(theLoaiId)
                .orElseThrow(() -> new GenreAlreadyExistsException("Thể loại không tồn tại với ID: " + theLoaiId));

        for (Comic comic : genre.getComics()) {
            comic.getGenres().remove(genre);
        }

        genreRepository.delete(genre);
        return ResponseEntity.ok("Đã xóa thành công!");
    }
}
