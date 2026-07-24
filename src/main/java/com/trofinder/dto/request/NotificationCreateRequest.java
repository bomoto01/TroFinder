package com.trofinder.dto.request;

import com.trofinder.enums.NotificationType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class NotificationCreateRequest {

    /*
     * ==========================================================
     * RECEIVER
     * ==========================================================
     */

    @NotNull(message = "User ID không được để trống.")
    private Long userId;

    /*
     * ==========================================================
     * CONTENT
     * ==========================================================
     */

    @NotBlank(message = "Tiêu đề không được để trống.")
    private String title;

    @NotBlank(message = "Nội dung không được để trống.")
    private String content;

    @NotNull(message = "Loại thông báo không được để trống.")
    private NotificationType type;

    /*
     * ==========================================================
     * REFERENCE
     * ==========================================================
     */

    private Long referenceId;

}