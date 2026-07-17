package com.trofinder.service.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

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
    /**
     * Upload ảnh phòng
     */
    List<String> uploadImages(
            Long roomId,
            List<MultipartFile> files);

        /**
         * Xóa ảnh
         */
    void deleteImage(Long imageId);
    /**
     * Đặt ảnh đại diện
     */
    void setThumbnail(Long imageId);

}