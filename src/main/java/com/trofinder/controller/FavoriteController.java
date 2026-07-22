package com.trofinder.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trofinder.dto.response.ApiResponse;
import com.trofinder.dto.response.FavoriteResponse;
import com.trofinder.service.interfaces.FavoriteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@Validated
public class FavoriteController {

    private final FavoriteService favoriteService;

    /**
     * Thêm phòng vào danh sách yêu thích
     */
    @PostMapping("/{roomId}")
    public ResponseEntity<ApiResponse<Void>> addFavorite(
            @PathVariable Long roomId) {

        favoriteService.addFavorite(roomId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Đã thêm vào danh sách yêu thích.")
                        .build());
    }

    /**
     * Xóa phòng khỏi danh sách yêu thích
     */
    @DeleteMapping("/{roomId}")
    public ResponseEntity<ApiResponse<Void>> removeFavorite(
            @PathVariable Long roomId) {

        favoriteService.removeFavorite(roomId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Đã xóa khỏi danh sách yêu thích.")
                        .build());
    }

    /**
     * Danh sách phòng yêu thích
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<FavoriteResponse>>> getMyFavorites() {

        List<FavoriteResponse> response =
                favoriteService.getMyFavorites();

        return ResponseEntity.ok(
                ApiResponse.<List<FavoriteResponse>>builder()
                        .success(true)
                        .message("Lấy danh sách yêu thích thành công.")
                        .data(response)
                        .build());
    }

    /**
     * Kiểm tra phòng đã được yêu thích chưa
     */
    @GetMapping("/{roomId}/exists")
    public ResponseEntity<ApiResponse<Boolean>> isFavorite(
            @PathVariable Long roomId) {

        Boolean response =
                favoriteService.isFavorite(roomId);

        return ResponseEntity.ok(
                ApiResponse.<Boolean>builder()
                        .success(true)
                        .message("Kiểm tra thành công.")
                        .data(response)
                        .build());
    }

}