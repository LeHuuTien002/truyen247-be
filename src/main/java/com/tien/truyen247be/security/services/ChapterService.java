package com.tien.truyen247be.security.services;

import com.tien.truyen247be.Exception.ResourceNotFoundException;
import com.tien.truyen247be.Exception.GenreAlreadyExistsException;
import com.tien.truyen247be.models.Chapter;
import com.tien.truyen247be.models.Comic;
import com.tien.truyen247be.payload.request.ChapterRequest;
import com.tien.truyen247be.payload.response.ChapterResponse;
import com.tien.truyen247be.repository.ChapterRepository;
import com.tien.truyen247be.repository.ComicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChapterService {
    @Autowired
    private ComicRepository comicRepository;

    @Autowired
    private ChapterRepository chapterRepository;

    // Tạo chương truyện
    public ResponseEntity<?> createChapter(Long id, ChapterRequest chapterRequest) {
        if (chapterRepository.existsByComicId(id) && chapterRepository.existsByChapterNumber(chapterRequest.getChapterNumber())) {
            throw new GenreAlreadyExistsException("Số chương đã tồn tại. Vui lòng chọn số chương khác.");
        } else {
            Comic comic = comicRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy truyện với id: " + id));
            Chapter chapter = new Chapter();

            chapter.setTitle(chapterRequest.getTitle());
            chapter.setChapterNumber(chapterRequest.getChapterNumber());
            chapter.setUpdateAt(LocalDateTime.now());
            chapter.setComic(comic);

            chapterRepository.save(chapter);

            return ResponseEntity.ok("Tạo chương mới thành công!");
        }
    }

    public ResponseEntity<?> updateChapter(Long comicId, Long chapterId, ChapterRequest chapterRequest) {
        if (!chapterRepository.existsByTitle(chapterRequest.getTitle())) {

            Chapter chapter = chapterRepository.findByIdAndComicId(chapterId, comicId).orElseThrow(() -> new ResourceNotFoundException("Không tìm chương với id: " + chapterId));

            chapter.setTitle(chapterRequest.getTitle());
            chapter.setChapterNumber(chapterRequest.getChapterNumber());

            chapterRepository.save(chapter);
            return ResponseEntity.ok("Cập nhật chương thành công!");
        } else {
            throw new GenreAlreadyExistsException("Tiêu đề đã tồn tại. Vui lòng nhập tiêu đề khác.");
        }
    }

    public ResponseEntity<?> deleteChapter(Long comicId, Long chapterId) {

        Chapter chapter = chapterRepository.findByIdAndComicId(chapterId, comicId).orElseThrow(() -> new ResourceNotFoundException("Không tìm chương với id: " + chapterId));

        chapterRepository.delete(chapter);

        return ResponseEntity.ok("Xóa chương thành công!");
    }

    public ResponseEntity<List<ChapterResponse>> getAllChapters(Long id) {
        Comic comic = comicRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy truyện với id: " + id));

        List<ChapterResponse> chapterResponses = comic.getChapters().stream()
                .map(chapter -> new ChapterResponse(
                        chapter.getId(),
                        chapter.getTitle(),
                        chapter.getChapterNumber(),
                        chapter.getCreateAt(),
                        chapter.getUpdateAt()
                )).toList();
        return ResponseEntity.ok(chapterResponses);
    }

    // Lấy truyện tranh theo ID
    public ResponseEntity<ChapterResponse> getChapterById(Long id) {
        Optional<Chapter> chapterOptional = chapterRepository.findById(id);
        if (chapterOptional.isEmpty()) {
            throw new GenreAlreadyExistsException("Id chương này không tồn tại!");
        } else {
            Chapter chapter = chapterOptional.get();
            ChapterResponse chapterResponse = new ChapterResponse();

            chapterResponse.setId(chapter.getId());
            chapterResponse.setChapterNumber(chapter.getChapterNumber());
            chapterResponse.setTitle(chapter.getTitle());
            chapterResponse.setCreateAt(chapter.getCreateAt());
            chapterResponse.setUpdateAt(chapter.getUpdateAt());

            return ResponseEntity.ok(chapterResponse);
        }
    }
}
