package com.trofinder.service.interfaces;

import java.util.List;

import com.trofinder.dto.response.FavoriteResponse;

public interface FavoriteService {

    /**
     * Thêm phòng vào yêu thích.
     */
    void addFavorite(Long roomId);

    /**
     * Xóa phòng khỏi yêu thích.
     */
    void removeFavorite(Long roomId);

    /**
     * Danh sách phòng yêu thích của người dùng.
     */
    List<FavoriteResponse> getMyFavorites();

    /**
     * Kiểm tra đã yêu thích chưa.
     */
    boolean isFavorite(Long roomId);

}