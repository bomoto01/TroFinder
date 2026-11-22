package com.trofinder.dto.request;

import com.trofinder.enums.ReportReason;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReportCreateRequest {

    @NotNull(message = "Mã phòng không được để trống")
    private Long roomId;

    @NotNull(message = "Lý do báo cáo không được để trống")
    private ReportReason reason;

    @NotBlank(message = "Mô tả chi tiết vi phạm không được để trống")
    private String description;
}
