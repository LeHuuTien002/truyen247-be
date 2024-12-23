package com.tien.truyen247be.controllers;

import com.tien.truyen247be.payload.request.HistoryRequest;
import com.tien.truyen247be.security.services.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/history")
public class HistoryController {
    @Autowired
    private HistoryService historyService;

    @PostMapping("/create")
    public ResponseEntity<?> createHistory(@RequestBody HistoryRequest request) {
        try {
            historyService.createHistory(request.getUserId(), request.getComicId(), request.getChapterId());
            return ResponseEntity.ok("Lịch sử đọc đã được tạo mới.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi tạo mới lịch sử: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateHistory(@RequestBody HistoryRequest request) {
        try {
            historyService.updateHistory(request.getUserId(), request.getComicId(), request.getChapterId());
            return ResponseEntity.ok("Lịch sử đọc đã được cập nhật.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi cập nhật lịch sử: " + e.getMessage());
        }
    }

    @GetMapping("/exists")
    public ResponseEntity<?> checkIfExists(
            @RequestParam Long userId,
            @RequestParam Long comicId) {
        try {
            boolean exists = historyService.exists(userId, comicId);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi kiểm tra lịch sử: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getReadingHistoryByUser(@PathVariable Long userId) {
        try {
            return historyService.getHistoriesByUserId(userId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi lấy danh sách lịch sử đọc: " + e.getMessage());
        }
    }

    @GetMapping("/recent-logs-by-user")
    public ResponseEntity<?> getRecentLogsByUser(@RequestParam Long userId) {
        return historyService.getTop3RecentLogsByUser(userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReadingHistory(@PathVariable Long id) {
        try {
            historyService.deleteHistoryById(id);
            return ResponseEntity.ok("Xóa lịch sử đọc thành công với ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi xóa lịch sử đọc: " + e.getMessage());
        }
    }
}
