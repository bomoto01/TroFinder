package com.trofinder.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trofinder.dto.request.ReviewRequest;
import com.trofinder.dto.response.ApiResponse;
import com.trofinder.dto.response.ReviewResponse;
import com.trofinder.service.interfaces.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Lấy danh sách đánh giá của phòng (Công khai)
     */
    @GetMapping("/room/{roomId}")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getReviewsByRoom(
            @PathVariable Long roomId,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<ReviewResponse> response = reviewService.getReviewsByRoom(roomId, pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<ReviewResponse>>builder()
                        .success(true)
                        .message("Lấy danh sách đánh giá thành công")
                        .data(response)
                        .build());
    }

    /**
     * Gửi đánh giá phòng (Yêu cầu đăng nhập)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @Valid @RequestBody ReviewRequest request) {

        ReviewResponse response = reviewService.createReview(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<ReviewResponse>builder()
                        .success(true)
                        .message("Gửi đánh giá thành công")
                        .data(response)
                        .build());
    }

    /**
     * Xóa đánh giá (Chính chủ hoặc ADMIN)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Long id) {

        reviewService.deleteReview(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Xóa đánh giá thành công")
                        .build());
    }
}
