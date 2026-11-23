package com.trofinder.service.interfaces;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.trofinder.dto.request.RoomCreateRequest;
import com.trofinder.dto.request.RoomSearchRequest;
import com.trofinder.dto.response.MapMarkerResponse;
import com.trofinder.dto.response.RoomResponse;
import com.trofinder.dto.response.RoomSummaryResponse;

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
     * Tìm kiếm phòng (Chi tiết)
     */
    Page<RoomResponse> search(
            RoomSearchRequest request,
            Pageable pageable);

    /**
     * Tìm kiếm phòng (Bản thu gọn cho danh sách card)
     */
    Page<RoomSummaryResponse> searchSummary(
            RoomSearchRequest request,
            Pageable pageable);

    /**
     * Lấy markers trên bản đồ theo khung nhìn (Bounding Box)
     */
    List<MapMarkerResponse> getMapMarkers(
            BigDecimal minLat,
            BigDecimal maxLat,
            BigDecimal minLng,
            BigDecimal maxLng);

    /**
     * Tìm phòng lân cận theo bán kính (km)
     */
    List<MapMarkerResponse> getNearbyRooms(
            BigDecimal lat,
            BigDecimal lng,
            Double radiusKm);

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
     * Lấy danh sách phòng của chủ trọ hiện tại kèm số lịch hẹn
     */
    List<RoomSummaryResponse> getMyRooms();

    /**
     * Cập nhật trạng thái phòng (AVAILABLE / FULL / HIDDEN)
     */
    RoomResponse updateStatus(Long id, com.trofinder.enums.RoomStatus status);

    /**
     * Xóa ảnh
     */
    void deleteImage(Long imageId);

    /**
     * Đặt ảnh đại diện
     */
    void setThumbnail(Long imageId);

    String uploadVideo(
            Long roomId,
            MultipartFile file);

    String getVideo(Long roomId);

    void deleteVideo(Long roomId);
}
