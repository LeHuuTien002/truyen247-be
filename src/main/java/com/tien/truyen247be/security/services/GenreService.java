package com.tien.truyen247be.security.services;

import com.tien.truyen247be.Exception.GenreAlreadyExistsException;
import com.tien.truyen247be.models.Comic;
import com.tien.truyen247be.models.Genre;
import com.tien.truyen247be.payload.request.GenreRequest;
import com.tien.truyen247be.payload.response.GenreNameResponse;
import com.tien.truyen247be.payload.response.GenreResponse;
import com.tien.truyen247be.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    public Optional<Genre> getGenreById(Long genreId) {
        return genreRepository.findById(genreId);
    }

    public ResponseEntity<?> createGenre(GenreRequest genreRequest) {
        if (!genreRepository.existsByName(genreRequest.getName())) {
            Genre genre = new Genre();
            genre.setName(genreRequest.getName());
            genre.setDescription(genreRequest.getDescription());
            genre.setUpdateAt(LocalDateTime.now());
            genreRepository.save(genre);
            return ResponseEntity.ok("Tạo thể loại truyện thành công!");
        } else {
            throw new GenreAlreadyExistsException("Tên thể loại đã tồn tại. Vui lòng chọn tên khác.");
        }
    }

    public ResponseEntity<?> updateGenre(Long genreId, GenreRequest genreRequest) {
        // Kiểm tra xem id có tồn tại trong cơ sở dữ liệu hay không
        Optional<Genre> genreOptional = genreRepository.findById(genreId);
        if (genreRepository.existsByName(genreRequest.getName())) {
            throw new GenreAlreadyExistsException("Tên thể loại truyện đã tồn tại!");
        }
        if (genreOptional.isEmpty()) {
            // Nếu id không tồn tại, trả về mã lỗi 404 (Not Found) cùng thông báo lỗi
            throw new GenreAlreadyExistsException("Id thể loại truyện này không tồn tại!");
        } else {
            // Nếu id tồn tại, tiến hành cập nhật
            Genre genre = genreOptional.get();
            genre.setName(genreRequest.getName());
            genre.setDescription(genreRequest.getDescription());
            genreRepository.save(genre);
            return ResponseEntity.ok("Cập nhật thể loại truyện thành công!");
        }
    }

    public ResponseEntity<?> deleteGenre(Long theLoaiId) {
        // Tìm thể loại theo ID
        Genre genre = genreRepository.findById(theLoaiId)
                .orElseThrow(() -> new GenreAlreadyExistsException("Thể loại không tồn tại với ID: " + theLoaiId));

        // Gỡ thể loại ra khỏi các truyện liên quan
        for (Comic comic : genre.getComics()) {
            comic.getGenres().remove(genre);
        }

        // Xóa thể loại
        genreRepository.delete(genre);
        return ResponseEntity.ok("Đã xóa thành công!");
    }
}
