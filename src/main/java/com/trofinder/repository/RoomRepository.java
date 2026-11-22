package com.trofinder.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.trofinder.entity.Room;
import com.trofinder.enums.ApprovalStatus;
import com.trofinder.enums.RoomStatus;
import com.trofinder.enums.RoomType;

public interface RoomRepository
        extends JpaRepository<Room, Long>,
        JpaSpecificationExecutor<Room> {

    /*
     * ==========================================================
     * CRUD
     * ==========================================================
     */

    /**
     * Lấy phòng chưa bị xóa theo id.
     */
    Optional<Room> findByIdAndDeletedFalse(Long id);

    /**
     * Danh sách phòng chưa bị xóa.
     */
    Page<Room> findAllByDeletedFalse(Pageable pageable);

    /**
     * Danh sách phòng của chủ trọ.
     */
    List<Room> findByOwnerIdAndDeletedFalse(Long ownerId);

    Page<Room> findByOwnerIdAndDeletedFalse(Long ownerId, Pageable pageable);

    /*
     * ==========================================================
     * STATUS
     * ==========================================================
     */

    /**
     * Danh sách phòng theo trạng thái.
     */
    List<Room> findByStatusAndDeletedFalse(RoomStatus status);

    /**
     * Danh sách phòng theo trạng thái duyệt.
     */
    Page<Room> findByApprovalStatusAndDeletedFalse(
            ApprovalStatus approvalStatus,
            Pageable pageable);

    /*
     * ==========================================================
     * FILTER
     * ==========================================================
     */

    /**
     * Tìm theo loại phòng.
     */
    Page<Room> findByRoomTypeAndDeletedFalse(
            RoomType roomType,
            Pageable pageable);

    /**
     * Tìm theo khoảng giá.
     */
    Page<Room> findByPriceBetweenAndDeletedFalse(
            BigDecimal min,
            BigDecimal max,
            Pageable pageable);

    /**
     * Tìm theo khoảng diện tích.
     */
    Page<Room> findByAreaBetweenAndDeletedFalse(
            BigDecimal min,
            BigDecimal max,
            Pageable pageable);

    /*
     * ==========================================================
     * MAP & GEOLOCATION
     * ==========================================================
     */

    @Query("SELECT r FROM Room r WHERE r.deleted = false AND r.approvalStatus = com.trofinder.enums.ApprovalStatus.APPROVED AND r.status = com.trofinder.enums.RoomStatus.AVAILABLE AND r.latitude BETWEEN :minLat AND :maxLat AND r.longitude BETWEEN :minLng AND :maxLng")
    List<Room> findInBoundingBox(
            @Param("minLat") BigDecimal minLat,
            @Param("maxLat") BigDecimal maxLat,
            @Param("minLng") BigDecimal minLng,
            @Param("maxLng") BigDecimal maxLng);

    @Query("SELECT r FROM Room r WHERE r.deleted = false AND r.approvalStatus = com.trofinder.enums.ApprovalStatus.APPROVED AND r.status = com.trofinder.enums.RoomStatus.AVAILABLE AND r.latitude IS NOT NULL AND r.longitude IS NOT NULL")
    List<Room> findAllAvailableWithLocation();

    /*
     * ==========================================================
     * THỐNG KÊ (DASHBOARD)
     * ==========================================================
     */

    long countByDeletedFalse();

    long countByApprovalStatusAndDeletedFalse(ApprovalStatus approvalStatus);

    long countByOwnerIdAndDeletedFalse(Long ownerId);

    long countByOwnerIdAndStatusAndDeletedFalse(Long ownerId, RoomStatus status);
}
