package com.trofinder.enums;

/**
 * Trạng thái lịch hẹn xem phòng.
 */
public enum AppointmentStatus {

    /**
     * Chờ chủ trọ xác nhận.
     */
    PENDING,

    /**
     * Đã xác nhận.
     */
    CONFIRMED,

    /**
     * Đã hủy.
     */
    CANCELLED,

    /**
     * Đã hoàn thành.
     */
    COMPLETED

}