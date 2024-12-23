package com.tien.truyen247be.security.services;

import com.tien.truyen247be.Exception.ResourceNotFoundException;
import com.tien.truyen247be.models.Comic;
import com.tien.truyen247be.models.Favorite;
import com.tien.truyen247be.models.User;
import com.tien.truyen247be.payload.response.FavoriteResponse;
import com.tien.truyen247be.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteService {
    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private ComicRepository comicRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ViewRepository viewRepository;

    @Autowired
    private CommentRepository commentRepository;


    public ResponseEntity<List<FavoriteResponse>> getFavorites(Long userId) {
        List<Favorite> favorites = favoriteRepository.findByUserId(userId);

        return ResponseEntity.ok(favorites.stream().map(Favorite -> {
            Comic comic = Favorite.getComic();
            return new FavoriteResponse(comic.getId(), comic.getName(), comic.getThumbnail(), viewRepository.findViewsCountByComicId(comic.getId()), favoriteRepository.countByComicId(comic.getId()), commentRepository.countByComicId(comic.getId()));
        }).collect(Collectors.toList()));
    }

    public void addFavorite(Long userId, Long comicId) {
        if (favoriteRepository.findByUserIdAndComicId(userId, comicId).isEmpty()) {
            Comic comic = comicRepository.findById(comicId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy truyện!"));
            User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Bạn phải đăng nhập để sử dụng tính năng này!"));
            Favorite favorite = new Favorite();
            favorite.setComic(comic);
            favorite.setUser(user);
            favoriteRepository.save(favorite);
        }
    }

    public void remoteFavorite(Long userId, Long comicId) {
        favoriteRepository.findByUserIdAndComicId(userId, comicId).ifPresent(favoriteRepository::delete);
    }

    public boolean isFavorite(Long userId, Long comicId) {
        return favoriteRepository.findByUserIdAndComicId(userId, comicId).isPresent();
    }
}
