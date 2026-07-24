package com.trofinder.dto.response;

import java.time.LocalDateTime;

import com.trofinder.enums.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    /*
     * ==========================================================
     * ID
     * ==========================================================
     */

    private Long id;

    /*
     * ==========================================================
     * CONTENT
     * ==========================================================
     */

    private String title;

    private String content;

    private NotificationType type;

    /*
     * ==========================================================
     * REFERENCE
     * ==========================================================
     */

    private Long referenceId;

    /*
     * ==========================================================
     * STATUS
     * ==========================================================
     */

    private Boolean isRead;

    /*
     * ==========================================================
     * TIME
     * ==========================================================
     */

    private LocalDateTime createdAt;

}