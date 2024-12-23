package com.tien.truyen247be.security.services;

import com.tien.truyen247be.models.Chapter;
import com.tien.truyen247be.models.Comic;
import com.tien.truyen247be.models.History;
import com.tien.truyen247be.models.User;
import com.tien.truyen247be.payload.response.HistoryResponse;
import com.tien.truyen247be.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class HistoryService {
    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private ComicRepository comicRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private ViewRepository viewRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private CommentRepository commentRepository;

    public void createHistory(Long userId, Long comicId, Long chapterId) {
        // Lấy User, Comic, và Chapter
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        Comic comic = comicRepository.findById(comicId)
                .orElseThrow(() -> new IllegalArgumentException("Comic not found with ID: " + comicId));
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found with ID: " + chapterId));

        if (!historyRepository.existsByUserIdAndComicId(userId, comicId)) {
            // Tạo mới lịch sử đọc
            History history = new History();
            history.setUser(user);
            history.setComic(comic);
            history.setChapter(chapter);
            history.setLastReadTime(LocalDateTime.now());
            historyRepository.save(history);
        } else {
            throw new IllegalArgumentException("History already exists");
        }
    }

    /**
     * Cập nhật lịch sử đọc
     */
    public void updateHistory(Long userId, Long comicId, Long chapterId) {
        // Kiểm tra nếu lịch sử đã tồn tại
        Optional<History> optionalHistory = historyRepository.findByUserIdAndComicId(userId, comicId);
        if (optionalHistory.isPresent()) {
            // Nếu tồn tại, cập nhật chapter và thời gian
            History history = optionalHistory.get();
            Chapter chapter = chapterRepository.findById(chapterId)
                    .orElseThrow(() -> new IllegalArgumentException("Chapter not found with ID: " + chapterId));
            history.setChapter(chapter);
            history.setLastReadTime(LocalDateTime.now());
            historyRepository.save(history);
        } else {
            throw new IllegalArgumentException("Lịch sử không tồn tại để cập nhật.");
        }
    }

    public boolean exists(Long userId, Long comicId) {
        return historyRepository.existsByUserIdAndComicId(userId, comicId);
    }

    public ResponseEntity<List<HistoryResponse>> getHistoriesByUserId(Long userId) {
        List<History> historyList = historyRepository.findByUserId(userId);
        List<HistoryResponse> historyResponseList = new ArrayList<>();

        for (History history : historyList) {
            HistoryResponse historyResponse = new HistoryResponse();

            historyResponse.setId(history.getId());
            historyResponse.setComicId(history.getComic().getId());
            historyResponse.setChapterId(history.getChapter().getId());
            historyResponse.setViews(viewRepository.findViewsCountByComicId(history.getComic().getId()));
            historyResponse.setFavorites(favoriteRepository.countByComicId(history.getComic().getId()));
            historyResponse.setNumberOfComment(commentRepository.countByComicId(history.getComic().getId()));
            historyResponse.setComicName(history.getComic().getName());
            historyResponse.setComicThumbnail(history.getComic().getThumbnail());
            historyResponse.setChapterNumber(history.getChapter().getChapterNumber());

            historyResponseList.add(historyResponse);
        }

        return ResponseEntity.ok(historyResponseList);
    }

    public ResponseEntity<List<HistoryResponse>> getTop3RecentLogsByUser(Long userId) {
        List<History> historyList = historyRepository.findByUserIdOrderByLastReadTimeDesc(userId, PageRequest.of(0, 3));
        List<HistoryResponse> historyResponseList = new ArrayList<>();

        for (History history : historyList) {
            HistoryResponse historyResponse = new HistoryResponse();

            historyResponse.setId(history.getId());
            historyResponse.setComicId(history.getComic().getId());
            historyResponse.setChapterId(history.getChapter().getId());
            historyResponse.setComicName(history.getComic().getName());
            historyResponse.setNumberOfComment(commentRepository.countByComicId(history.getComic().getId()));
            historyResponse.setViews(viewRepository.findViewsCountByComicId(history.getComic().getId()));
            historyResponse.setFavorites(favoriteRepository.countByComicId(history.getComic().getId()));
            historyResponse.setComicThumbnail(history.getComic().getThumbnail());
            historyResponse.setChapterNumber(history.getChapter().getChapterNumber());

            historyResponseList.add(historyResponse);
        }

        return ResponseEntity.ok(historyResponseList);
    }

    public void deleteHistoryById(Long id) {
        if (historyRepository.existsById(id)) {
            historyRepository.deleteById(id);
        } else {
            throw new RuntimeException("Không tìm thấy lịch sử đọc với ID: " + id);
        }
    }
}

