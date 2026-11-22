package com.trofinder.dto.response;

import java.time.LocalDateTime;

import com.trofinder.enums.ReportReason;
import com.trofinder.enums.ReportStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {

    private Long id;
    private Long roomId;
    private String roomTitle;
    private Long reporterId;
    private String reporterName;
    private ReportReason reason;
    private String description;
    private String adminNote;
    private ReportStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
}
