package com.tien.truyen247be.view;

import com.tien.truyen247be.comic.dto.TopComicViewResponse;
import com.tien.truyen247be.comment.CommentRepository;
import com.tien.truyen247be.favorite.FavoriteRepository;
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

    public List<TopComicViewResponse> getTop5Views() {
        Pageable pageable = PageRequest.of(0, 5);
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
