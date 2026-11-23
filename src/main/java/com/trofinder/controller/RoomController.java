package com.trofinder.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trofinder.dto.request.RoomCreateRequest;
import com.trofinder.dto.request.RoomSearchRequest;
import com.trofinder.dto.response.ApiResponse;
import com.trofinder.dto.response.RoomResponse;
import com.trofinder.dto.response.RoomSummaryResponse;
import com.trofinder.service.interfaces.RoomService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Validated
public class RoomController {

    private final RoomService roomService;

    /**
     * Đăng phòng
     */
    @PostMapping
    public ResponseEntity<ApiResponse<RoomResponse>> create(
            @Valid @RequestBody RoomCreateRequest request) {

        RoomResponse response = roomService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<RoomResponse>builder()
                        .success(true)
                        .message("Đăng phòng thành công")
                        .data(response)
                        .build());
    }

    /**
     * Lấy chi tiết phòng theo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomResponse>> getById(
            @PathVariable Long id) {

        RoomResponse response = roomService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<RoomResponse>builder()
                        .success(true)
                        .message("Lấy thông tin phòng thành công")
                        .data(response)
                        .build());
    }

    /**
     * Lấy danh sách phòng
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<RoomResponse>>> getAll(
            @PageableDefault(size = 10) Pageable pageable) {

        Page<RoomResponse> response = roomService.getAll(pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<RoomResponse>>builder()
                        .success(true)
                        .message("Lấy danh sách phòng thành công")
                        .data(response)
                        .build());
    }

    /**
     * Tìm kiếm phòng (Chi tiết)
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<RoomResponse>>> search(
            @ModelAttribute RoomSearchRequest request,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<RoomResponse> response = roomService.search(request, pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<RoomResponse>>builder()
                        .success(true)
                        .message("Tìm kiếm phòng thành công")
                        .data(response)
                        .build());
    }

    /**
     * Tìm kiếm phòng dạng danh sách tóm tắt (Card view)
     */
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<Page<RoomSummaryResponse>>> searchSummary(
            @ModelAttribute RoomSearchRequest request,
            @PageableDefault(size = 12) Pageable pageable) {

        Page<RoomSummaryResponse> response = roomService.searchSummary(request, pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<RoomSummaryResponse>>builder()
                        .success(true)
                        .message("Lấy danh sách phòng rút gọn thành công")
                        .data(response)
                        .build());
    }

    /**
     * Cập nhật phòng
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody RoomCreateRequest request) {

        RoomResponse response = roomService.update(id, request);

        return ResponseEntity.ok(
                ApiResponse.<RoomResponse>builder()
                        .success(true)
                        .message("Cập nhật phòng thành công")
                        .data(response)
                        .build());
    }

    /**
     * Xóa mềm phòng
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id) {

        roomService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Xóa phòng thành công")
                        .build());
    }

    /**
     * Lấy danh sách phòng của chủ trọ hiện tại (kèm số lượt xem, yêu thích, lịch hẹn)
     */
    @GetMapping("/my-rooms")
    public ResponseEntity<ApiResponse<java.util.List<RoomSummaryResponse>>> getMyRooms() {
        java.util.List<RoomSummaryResponse> response = roomService.getMyRooms();
        return ResponseEntity.ok(
                ApiResponse.<java.util.List<RoomSummaryResponse>>builder()
                        .success(true)
                        .message("Lấy danh sách phòng của tôi thành công")
                        .data(response)
                        .build());
    }

    /**
     * Cập nhật nhanh trạng thái phòng (AVAILABLE / FULL / HIDDEN)
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<RoomResponse>> updateStatus(
            @PathVariable Long id,
            @RequestParam com.trofinder.enums.RoomStatus status) {
        RoomResponse response = roomService.updateStatus(id, status);
        return ResponseEntity.ok(
                ApiResponse.<RoomResponse>builder()
                        .success(true)
                        .message("Cập nhật trạng thái phòng thành công")
                        .data(response)
                        .build());
    }
}
