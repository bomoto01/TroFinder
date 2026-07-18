package com.trofinder.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.trofinder.dto.response.ApiResponse;
import com.trofinder.service.interfaces.RoomService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Validated
public class RoomMediaController {

    private final RoomService roomService;

    /**
     * Upload ảnh
     */
    @PostMapping("/{roomId}/images")
    public ResponseEntity<ApiResponse<List<String>>> uploadImages(
            @PathVariable Long roomId,
            @RequestParam("files") List<MultipartFile> files) {

        List<String> imageUrls =
                roomService.uploadImages(roomId, files);

        return ResponseEntity.ok(
                ApiResponse.<List<String>>builder()
                        .success(true)
                        .message("Upload ảnh thành công")
                        .data(imageUrls)
                        .build());
    }

    /**
     * Xóa ảnh
     */
    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<ApiResponse<Void>> deleteImage(
            @PathVariable Long imageId) {

        roomService.deleteImage(imageId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Xóa ảnh thành công")
                        .build());
    }

    /**
     * Đặt ảnh đại diện
     */
    @PutMapping("/images/{imageId}/thumbnail")
    public ResponseEntity<ApiResponse<Void>> setThumbnail(
            @PathVariable Long imageId) {

        roomService.setThumbnail(imageId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Đặt ảnh đại diện thành công")
                        .build());
    }

    /**
     * Upload video
     */
    @PostMapping("/{roomId}/video")
    public ResponseEntity<ApiResponse<String>> uploadVideo(
            @PathVariable Long roomId,
            @RequestParam("file") MultipartFile file) {

        String videoUrl =
                roomService.uploadVideo(roomId, file);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Upload video thành công")
                        .data(videoUrl)
                        .build());
    }

    /**
     * Lấy video
     */
    @GetMapping("/{roomId}/video")
    public ResponseEntity<ApiResponse<String>> getVideo(
            @PathVariable Long roomId) {

        String videoUrl =
                roomService.getVideo(roomId);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Lấy video thành công")
                        .data(videoUrl)
                        .build());
    }

    /**
     * Xóa video
     */
    @DeleteMapping("/{roomId}/video")
    public ResponseEntity<ApiResponse<Void>> deleteVideo(
            @PathVariable Long roomId) {

        roomService.deleteVideo(roomId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Xóa video thành công")
                        .build());
    }

}