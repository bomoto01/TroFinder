package com.trofinder.dto.request;

import com.trofinder.enums.ReportStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReportProcessRequest {

    @NotNull(message = "Trạng thái xử lý không được để trống")
    private ReportStatus status;

    @NotBlank(message = "Hành động xử lý không được để trống")
    private String action;

    private String adminNote;
}
