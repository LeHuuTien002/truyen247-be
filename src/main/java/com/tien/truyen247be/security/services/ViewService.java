package com.tien.truyen247be.security.services;

import com.tien.truyen247be.models.View;
import com.tien.truyen247be.repository.ViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ViewService {
    @Autowired
    private ViewRepository viewRepository;

    public void incrementViewCount(Long comicId) {
        View view = viewRepository.findByComicId(comicId);
        if (view != null) {
            view.setViewsCount(view.getViewsCount() + 1);
            viewRepository.save(view);
        }
    }

    public Long getViewsByComicId(Long comicId) {
        View view = viewRepository.findByComicId(comicId);
        if (view != null) {
            return view.getViewsCount();
        }
        return null;
    }
}
