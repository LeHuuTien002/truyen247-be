package com.tien.truyen247be.chapter;

import com.tien.truyen247be.Exception.ResourceNotFoundException;
import com.tien.truyen247be.Exception.GenreAlreadyExistsException;
import com.tien.truyen247be.chapter.dto.ChapterResponse;
import com.tien.truyen247be.comic.Comic;
import com.tien.truyen247be.user.User;
import com.tien.truyen247be.chapter.dto.ChapterRequest;
import com.tien.truyen247be.comic.ComicRepository;
import com.tien.truyen247be.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ChapterService {
    @Autowired
    private ComicRepository comicRepository;

    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> createChapter(Long id, ChapterRequest chapterRequest) {
        if (chapterRepository.existsByComicIdAndChapterNumber(id, chapterRequest.getChapterNumber())) {
            throw new GenreAlreadyExistsException("Số chương đã tồn tại. Vui lòng chọn số chương khác.");
        } else {
            Comic comic = comicRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy truyện với id: " + id));
            Chapter chapter = new Chapter();

            chapter.setTitle(chapterRequest.getTitle());
            chapter.setChapterNumber(chapterRequest.getChapterNumber());
            chapter.setUpdateAt(LocalDateTime.now());
            chapter.setCreateAt(LocalDateTime.now());
            chapter.setComic(comic);

            chapterRepository.save(chapter);

            return ResponseEntity.ok("Tạo chương mới thành công!");
        }
    }

    public ResponseEntity<?> updateChapter(Long comicId, Long chapterId, ChapterRequest chapterRequest) {
        Chapter chapter = chapterRepository.findByIdAndComicId(chapterId, comicId).orElseThrow(() -> new ResourceNotFoundException("Không tìm chương với id: " + chapterId));
        if (chapterRepository.existsByComicIdAndChapterNumber(comicId, chapterRequest.getChapterNumber()) && !Objects.equals(chapter.getChapterNumber(), chapterRequest.getChapterNumber())) {
            throw new GenreAlreadyExistsException("Số chương đã tồn tại. Vui lòng nhập số chương khác.");
        } else {
            chapter.setTitle(chapterRequest.getTitle());
            chapter.setChapterNumber(chapterRequest.getChapterNumber());
            chapter.setUpdateAt(LocalDateTime.now());
            chapterRepository.save(chapter);
            return ResponseEntity.ok("Cập nhật chương thành công!");
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

    public ResponseEntity<List<ChapterResponse>> getChaptersByComicId(Long comicId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<Chapter> chapters = chapterRepository.findByComicId(comicId);

        List<ChapterResponse> chapterResponses = chapters.stream()
                .map(chapter -> new ChapterResponse(
                        chapter.getId(),
                        chapter.getTitle(),
                        chapter.getChapterNumber(),
                        chapter.getCreateAt(),
                        chapter.getUpdateAt()
                ))
                .toList();

        if (user.isPremium() && user.getPremiumExpiryDate().isAfter(LocalDate.now())) {
            return ResponseEntity.ok(chapterResponses);
        } else {
            List<ChapterResponse> limitedChapters = chapterResponses.stream()
                    .sorted(Comparator.comparingLong(ChapterResponse::getChapterNumber))
                    .limit(5)
                    .toList();
            return ResponseEntity.ok(limitedChapters);
        }
    }

    public ResponseEntity<List<ChapterResponse>> getChaptersByComicId(Long comicId) {

        List<Chapter> chapters = chapterRepository.findByComicId(comicId);

        List<ChapterResponse> chapterResponses = chapters.stream()
                .map(chapter -> new ChapterResponse(
                        chapter.getId(),
                        chapter.getTitle(),
                        chapter.getChapterNumber(),
                        chapter.getCreateAt(),
                        chapter.getUpdateAt()
                ))
                .toList();

        List<ChapterResponse> limitedChapters = chapterResponses.stream()
                .sorted(Comparator.comparingLong(ChapterResponse::getChapterNumber))
                .limit(5)
                .toList();
        return ResponseEntity.ok(limitedChapters);
    }

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
