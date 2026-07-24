package com.trofinder.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.trofinder.enums.AppointmentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {

    /*
     * ==========================================================
     * ID
     * ==========================================================
     */

    private Long id;

    /*
     * ==========================================================
     * ROOM
     * ==========================================================
     */

    private Long roomId;

    private String roomTitle;

    private String roomAddress;

    private BigDecimal roomPrice;

    private BigDecimal roomArea;

    /*
     * ==========================================================
     * OWNER
     * ==========================================================
     */

    private Long ownerId;

    private String ownerName;

    private String ownerPhone;

    /*
     * ==========================================================
     * TENANT
     * ==========================================================
     */

    private Long tenantId;

    private String tenantName;

    private String tenantPhone;

    /*
     * ==========================================================
     * APPOINTMENT
     * ==========================================================
     */

    private LocalDate appointmentDate;

    private LocalTime appointmentTime;

    private AppointmentStatus status;

    private String note;

    /*
     * ==========================================================
     * CONTACT SETTING
     * ==========================================================
     */

    private Boolean allowCall;

    private Boolean allowChat;

    /*
     * ==========================================================
     * PROCESS TIME
     * ==========================================================
     */

    private LocalDateTime confirmedAt;

    private LocalDateTime cancelledAt;

    private LocalDateTime completedAt;

    /*
     * ==========================================================
     * SOFT DELETE
     * ==========================================================
     */

    private Boolean deleted;

    /*
     * ==========================================================
     * AUDIT
     * ==========================================================
     */

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}