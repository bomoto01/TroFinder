package com.trofinder.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.trofinder.dto.request.ReviewRequest;
import com.trofinder.dto.response.ReviewResponse;
import com.trofinder.entity.Review;
import com.trofinder.entity.Role;
import com.trofinder.entity.Room;
import com.trofinder.entity.User;
import com.trofinder.repository.ReviewRepository;
import com.trofinder.repository.RoomRepository;
import com.trofinder.service.impl.ReviewServiceImpl;
import com.trofinder.service.interfaces.CurrentUserService;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private User tenant;
    private User landlord;
    private Room room;

    @BeforeEach
    void setUp() {
        Role tenantRole = Role.builder().id(1L).name("TENANT").build();
        tenant = User.builder().id(10L).username("tenant").fullName("Nguyen Van Tenant").role(tenantRole).build();

        Role landlordRole = Role.builder().id(2L).name("LANDLORD").build();
        landlord = User.builder().id(20L).username("landlord").fullName("Le Van Landlord").role(landlordRole).build();

        room = Room.builder().id(100L).title("Phòng trọ Cầu Giấy").owner(landlord).deleted(false).build();
    }

    @Test
    @DisplayName("Tạo đánh giá phòng thành công")
    void createReview_Success() {
        when(currentUserService.getCurrentUser()).thenReturn(tenant);
        when(roomRepository.findByIdAndDeletedFalse(100L)).thenReturn(Optional.of(room));
        when(reviewRepository.existsByRoomIdAndUserId(100L, 10L)).thenReturn(false);

        Review savedReview = Review.builder()
                .id(1L)
                .room(room)
                .user(tenant)
                .rating(5)
                .comment("Phòng rất đẹp và thoáng mát")
                .build();
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);

        ReviewRequest request = new ReviewRequest();
        request.setRoomId(100L);
        request.setRating(5);
        request.setComment("Phòng rất đẹp và thoáng mát");

        ReviewResponse response = reviewService.createReview(request);

        assertNotNull(response);
        assertEquals(5, response.getRating());
        assertEquals("Phòng rất đẹp và thoáng mát", response.getComment());
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    @DisplayName("Lấy điểm đánh giá trung bình")
    void getAverageRating_Success() {
        when(reviewRepository.getAverageRatingByRoomId(100L)).thenReturn(4.5);

        Double avg = reviewService.getAverageRating(100L);

        assertEquals(4.5, avg);
    }
}
