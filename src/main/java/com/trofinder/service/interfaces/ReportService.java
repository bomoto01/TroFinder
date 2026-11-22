package com.trofinder.service.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.trofinder.dto.request.ReportCreateRequest;
import com.trofinder.dto.request.ReportProcessRequest;
import com.trofinder.dto.response.ReportHistoryResponse;
import com.trofinder.dto.response.ReportResponse;
import com.trofinder.enums.ReportStatus;

public interface ReportService {

    ReportResponse createReport(ReportCreateRequest request);

    List<ReportResponse> getMyReports();

    Page<ReportResponse> getAllReports(ReportStatus status, Pageable pageable);

    ReportResponse getReportById(Long id);

    List<ReportHistoryResponse> getReportHistories(Long reportId);

    ReportResponse processReport(Long id, ReportProcessRequest request);
}
