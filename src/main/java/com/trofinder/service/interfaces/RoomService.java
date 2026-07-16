package com.trofinder.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.trofinder.dto.request.RoomCreateRequest;
import com.trofinder.dto.request.RoomSearchRequest;
import com.trofinder.dto.response.RoomResponse;

public interface RoomService {

    /**
     * Đăng phòng
     */
    RoomResponse create(RoomCreateRequest request);

    /**
     * Xem chi tiết phòng
     */
    RoomResponse getById(Long id);

    /**
     * Danh sách phòng đã duyệt
     */
    Page<RoomResponse> getAll(Pageable pageable);

    /**
     * Tìm kiếm phòng
     */
    Page<RoomResponse> search(
            RoomSearchRequest request,
            Pageable pageable);

    /**
     * Cập nhật phòng
     */
    RoomResponse update(Long id, RoomCreateRequest request);

    /**
     * Xóa mềm phòng
     */
    void delete(Long id);

}