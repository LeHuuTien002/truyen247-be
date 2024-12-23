package com.tien.truyen247be.security.services;

import com.tien.truyen247be.models.View;
import com.tien.truyen247be.payload.response.TopComicViewResponse;
import com.tien.truyen247be.repository.CommentRepository;
import com.tien.truyen247be.repository.FavoriteRepository;
import com.tien.truyen247be.repository.ViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ViewService {
    @Autowired
    private ViewRepository viewRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private CommentRepository commentRepository;

    public void incrementViewCount(Long comicId) {
        View view = viewRepository.findByComicId(comicId);
        if (view != null) {
            view.setViewsCount(view.getViewsCount() + 1);
            viewRepository.save(view);
        }
    }


    public List<TopComicViewResponse> getTop10Views() {
        Pageable pageable = PageRequest.of(0, 10); // Trang đầu tiên, 10 kết quả
        List<View> viewList = viewRepository.findTop10ByOrderByViewsCountDesc(pageable);
        List<TopComicViewResponse> responses = new ArrayList<>();
        for (View view : viewList) {
            TopComicViewResponse viewResponse = new TopComicViewResponse();
            viewResponse.setId(view.getComic().getId());
            viewResponse.setName(view.getComic().getName());
            viewResponse.setThumbnail(view.getComic().getThumbnail());
            viewResponse.setViews(viewRepository.findViewsCountByComicId(view.getComic().getId()));
            viewResponse.setFavorites(favoriteRepository.countByComicId(view.getComic().getId()));
            viewResponse.setNumberOfComment(commentRepository.countByComicId(view.getComic().getId()));
            responses.add(viewResponse);
        }
        return responses;
    }
}
