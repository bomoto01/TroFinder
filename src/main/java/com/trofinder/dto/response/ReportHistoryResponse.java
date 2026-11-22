package com.trofinder.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportHistoryResponse {

    private Long id;
    private Long reportId;
    private Long adminId;
    private String adminName;
    private String action;
    private String note;
    private LocalDateTime processedAt;
}
