package com.trofinder.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.trofinder.dto.request.ReviewRequest;
import com.trofinder.dto.response.ReviewResponse;

public interface ReviewService {

    ReviewResponse createReview(ReviewRequest request);

    Page<ReviewResponse> getReviewsByRoom(Long roomId, Pageable pageable);

    Double getAverageRating(Long roomId);

    Long getReviewCount(Long roomId);

    void deleteReview(Long reviewId);
}
