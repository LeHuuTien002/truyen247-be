package com.tien.truyen247be.page;

import com.tien.truyen247be.exception.ResourceNotFoundException;
import com.tien.truyen247be.exception.GenreAlreadyExistsException;
import com.tien.truyen247be.chapter.Chapter;
import com.tien.truyen247be.page.dto.PageRequest;
import com.tien.truyen247be.page.dto.PageResponse;
import com.tien.truyen247be.chapter.ChapterRepository;
import com.tien.truyen247be.comic.S3Service;
import com.tien.truyen247be.view.ViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PageService {
    @Autowired
    private S3Service s3Service;

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private ViewService viewService;

    public ResponseEntity<?> createPages(Long id, List<MultipartFile> files) throws IOException {
        if (files.isEmpty()) {
            throw new IllegalArgumentException("Danh sách file tải lên không được để trống.");
        }

        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chương với id: " + id));

        List<Page> pages = new ArrayList<>();

        for (long i = 0L; i < files.size(); i++) {
            MultipartFile file = files.get(Math.toIntExact(i));
            Long pageNumber = i + 1;

            if (pageRepository.existsByPageNumberAndChapterId(pageNumber, id)) {
                throw new GenreAlreadyExistsException("Số trang " + pageNumber + " đã tồn tại. Vui lòng kiểm tra lại.");
            }

            String imgUrl = s3Service.uploadFile(file);

            Page page = new Page();
            page.setPageNumber(pageNumber);
            page.setImageUrl(imgUrl);
            page.setCreateAt(LocalDateTime.now());
            page.setUpdateAt(LocalDateTime.now());
            page.setChapter(chapter);

            pages.add(page);
        }

        pageRepository.saveAll(pages);

        return ResponseEntity.ok("Tạo các trang mới thành công!");
    }


    public ResponseEntity<?> updatePage(Long idPage, PageRequest pageRequest, MultipartFile file) throws IOException {
        Optional<Page> pageOptional = pageRepository.findById(idPage);
        if (pageOptional.isEmpty()) {
            throw new GenreAlreadyExistsException("Id trang này không tồn tại!");
        } else {
            Page page = pageOptional.get();
            if (pageRepository.existsByPageNumber(pageRequest.getPageNumber()) && !Objects.equals(pageRequest.getPageNumber(), page.getPageNumber())) {
                throw new GenreAlreadyExistsException("Số chương đã tồn tại. Vui lòng nhập số chương khác.");
            } else {
                page.setPageNumber(pageRequest.getPageNumber());
                page.setImageUrl(s3Service.updateFile(file));
                page.setUpdateAt(LocalDateTime.now());

                pageRepository.save(page);

                return ResponseEntity.ok("Cập trang thành công");
            }
        }
    }

    public ResponseEntity<?> deletePage(Long chapterId, Long pageId) {

        Page page = pageRepository.findByIdAndChapterId(pageId, chapterId).orElseThrow(() -> new ResourceNotFoundException("Không tìm chương với id: " + chapterId));

        pageRepository.delete(page);

        return ResponseEntity.ok("Xóa trang thành công!");
    }

    public ResponseEntity<List<PageResponse>> getPagesByChapterId(Long id) {
        Chapter chapter = chapterRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chương với id: " + id));
        viewService.incrementViewCount(chapter.getComic().getId());
        List<PageResponse> pageResponses = chapter.getPages().stream()
                .map(page -> new PageResponse(
                        page.getId(),
                        page.getPageNumber(),
                        page.getImageUrl(),
                        page.getCreateAt(),
                        page.getUpdateAt()
                )).toList();
        return ResponseEntity.ok(pageResponses);
    }

    public ResponseEntity<List<PageResponse>> getAllPageByChapterId(Long id) {
        Chapter chapter = chapterRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chương với id: " + id));
        List<PageResponse> pageResponses = chapter.getPages().stream()
                .map(page -> new PageResponse(
                        page.getId(),
                        page.getPageNumber(),
                        page.getImageUrl(),
                        page.getCreateAt(),
                        page.getUpdateAt()
                )).toList();
        return ResponseEntity.ok(pageResponses);
    }
}
