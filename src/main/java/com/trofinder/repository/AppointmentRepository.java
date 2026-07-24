package com.trofinder.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trofinder.entity.Appointment;
import com.trofinder.entity.Room;
import com.trofinder.entity.User;
import com.trofinder.enums.AppointmentStatus;

public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {

    /*
     * ==========================================================
     * CRUD
     * ==========================================================
     */

    /**
     * Lấy lịch hẹn theo ID (chưa bị xóa).
     */
    Optional<Appointment> findByIdAndDeletedFalse(Long id);

    /**
     * Kiểm tra lịch hẹn còn tồn tại.
     */
    boolean existsByIdAndDeletedFalse(Long id);

    /*
     * ==========================================================
     * NGƯỜI THUÊ
     * ==========================================================
     */

    /**
     * Danh sách lịch hẹn của người thuê.
     */
    List<Appointment> findByTenantAndDeletedFalseOrderByCreatedAtDesc(
            User tenant);

    /**
     * Danh sách lịch hẹn của người thuê theo trạng thái.
     */
    List<Appointment> findByTenantAndStatusAndDeletedFalseOrderByAppointmentDateAscAppointmentTimeAsc(
            User tenant,
            AppointmentStatus status);

    /*
     * ==========================================================
     * CHỦ TRỌ
     * ==========================================================
     */

    /**
     * Danh sách lịch hẹn của chủ trọ.
     */
    List<Appointment> findByOwnerAndDeletedFalseOrderByCreatedAtDesc(
            User owner);

    /**
     * Danh sách lịch hẹn của chủ trọ theo trạng thái.
     */
    List<Appointment> findByOwnerAndStatusAndDeletedFalseOrderByAppointmentDateAscAppointmentTimeAsc(
            User owner,
            AppointmentStatus status);

    /*
     * ==========================================================
     * PHÒNG
     * ==========================================================
     */

    /**
     * Danh sách lịch hẹn của một phòng.
     */
    List<Appointment> findByRoomAndDeletedFalseOrderByAppointmentDateAscAppointmentTimeAsc(
            Room room);

    /**
     * Đếm số lịch hẹn của một phòng.
     */
    long countByRoomAndDeletedFalse(
            Room room);

    /*
     * ==========================================================
     * TRẠNG THÁI
     * ==========================================================
     */

    /**
     * Danh sách theo một trạng thái.
     */
    List<Appointment> findByStatusAndDeletedFalse(
            AppointmentStatus status);

    /**
     * Danh sách theo nhiều trạng thái.
     */
    List<Appointment> findByStatusInAndDeletedFalse(
            List<AppointmentStatus> statuses);

    /*
     * ==========================================================
     * KIỂM TRA TRÙNG LỊCH
     * ==========================================================
     */

    /**
     * Kiểm tra người thuê đã có lịch hẹn đang hoạt động.
     */
    boolean existsByRoomAndTenantAndAppointmentDateAndAppointmentTimeAndStatusInAndDeletedFalse(
            Room room,
            User tenant,
            LocalDate appointmentDate,
            LocalTime appointmentTime,
            List<AppointmentStatus> statuses);

    /**
     * Kiểm tra phòng đã có lịch hẹn đang hoạt động
     * trong cùng thời gian.
     */
    boolean existsByRoomAndAppointmentDateAndAppointmentTimeAndStatusInAndDeletedFalse(
            Room room,
            LocalDate appointmentDate,
            LocalTime appointmentTime,
            List<AppointmentStatus> statuses);

    /*
     * ==========================================================
     * THỐNG KÊ
     * ==========================================================
     */

    /**
     * Đếm số lịch theo trạng thái.
     */
    long countByStatusAndDeletedFalse(
            AppointmentStatus status);

    /**
     * Đếm số lịch của chủ trọ theo trạng thái.
     */
    long countByOwnerAndStatusAndDeletedFalse(
            User owner,
            AppointmentStatus status);

    /**
     * Đếm số lịch của người thuê theo trạng thái.
     */
    long countByTenantAndStatusAndDeletedFalse(
            User tenant,
            AppointmentStatus status);

}