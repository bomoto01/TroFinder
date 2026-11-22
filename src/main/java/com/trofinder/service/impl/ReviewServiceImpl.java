package com.trofinder.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trofinder.dto.request.ReviewRequest;
import com.trofinder.dto.response.ReviewResponse;
import com.trofinder.entity.Review;
import com.trofinder.entity.Room;
import com.trofinder.entity.User;
import com.trofinder.exception.BadRequestException;
import com.trofinder.exception.ResourceNotFoundException;
import com.trofinder.exception.UnauthorizedException;
import com.trofinder.repository.ReviewRepository;
import com.trofinder.repository.RoomRepository;
import com.trofinder.service.interfaces.CurrentUserService;
import com.trofinder.service.interfaces.ReviewService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final RoomRepository roomRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional
    public ReviewResponse createReview(ReviewRequest request) {
        User currentUser = currentUserService.getCurrentUser();

        Room room = roomRepository.findByIdAndDeletedFalse(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng để đánh giá."));

        if (room.getOwner().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Chủ nhà không thể tự đánh giá phòng của chính mình.");
        }

        if (reviewRepository.existsByRoomIdAndUserId(request.getRoomId(), currentUser.getId())) {
            throw new BadRequestException("Bạn đã đánh giá phòng này rồi.");
        }

        Review review = Review.builder()
                .room(room)
                .user(currentUser)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Review saved = reviewRepository.save(review);
        return mapToResponse(saved);
    }

    @Override
    public Page<ReviewResponse> getReviewsByRoom(Long roomId, Pageable pageable) {
        return reviewRepository.findByRoomIdOrderByCreatedAtDesc(roomId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public Double getAverageRating(Long roomId) {
        Double avg = reviewRepository.getAverageRatingByRoomId(roomId);
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
    }

    @Override
    public Long getReviewCount(Long roomId) {
        Long count = reviewRepository.countByRoomId(roomId);
        return count != null ? count : 0L;
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đánh giá."));

        User currentUser = currentUserService.getCurrentUser();
        boolean isAdmin = currentUser.getRole() != null &&
                (currentUser.getRole().getName().equalsIgnoreCase("ROLE_ADMIN")
                        || currentUser.getRole().getName().equalsIgnoreCase("ADMIN"));

        if (!review.getUser().getId().equals(currentUser.getId()) && !isAdmin) {
            throw new UnauthorizedException("Bạn không có quyền xóa đánh giá này.");
        }

        reviewRepository.delete(review);
    }

    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .roomId(review.getRoom().getId())
                .userId(review.getUser().getId())
                .userName(review.getUser().getFullName())
                .userAvatar(review.getUser().getAvatar())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
