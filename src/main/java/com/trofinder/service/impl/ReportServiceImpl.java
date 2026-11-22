package com.trofinder.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trofinder.dto.request.ReportCreateRequest;
import com.trofinder.dto.request.ReportProcessRequest;
import com.trofinder.dto.response.ReportHistoryResponse;
import com.trofinder.dto.response.ReportResponse;
import com.trofinder.entity.Report;
import com.trofinder.entity.ReportHistory;
import com.trofinder.entity.Room;
import com.trofinder.entity.User;
import com.trofinder.enums.ReportStatus;
import com.trofinder.enums.RoomStatus;
import com.trofinder.exception.ResourceNotFoundException;
import com.trofinder.repository.ReportHistoryRepository;
import com.trofinder.repository.ReportRepository;
import com.trofinder.repository.RoomRepository;
import com.trofinder.service.interfaces.CurrentUserService;
import com.trofinder.service.interfaces.ReportService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final ReportHistoryRepository reportHistoryRepository;
    private final RoomRepository roomRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional
    public ReportResponse createReport(ReportCreateRequest request) {
        User currentUser = currentUserService.getCurrentUser();

        Room room = roomRepository.findByIdAndDeletedFalse(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng để báo cáo."));

        Report report = Report.builder()
                .room(room)
                .reporter(currentUser)
                .reason(request.getReason())
                .description(request.getDescription())
                .status(ReportStatus.PENDING)
                .build();

        Report saved = reportRepository.save(report);
        return mapToReportResponse(saved);
    }

    @Override
    public List<ReportResponse> getMyReports() {
        User currentUser = currentUserService.getCurrentUser();
        return reportRepository.findByReporterIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(this::mapToReportResponse)
                .toList();
    }

    @Override
    public Page<ReportResponse> getAllReports(ReportStatus status, Pageable pageable) {
        if (status != null) {
            return reportRepository.findByStatusOrderByCreatedAtDesc(status, pageable)
                    .map(this::mapToReportResponse);
        }
        return reportRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::mapToReportResponse);
    }

    @Override
    public ReportResponse getReportById(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy báo cáo."));
        return mapToReportResponse(report);
    }

    @Override
    public List<ReportHistoryResponse> getReportHistories(Long reportId) {
        return reportHistoryRepository.findByReportIdOrderByProcessedAtDesc(reportId)
                .stream()
                .map(h -> ReportHistoryResponse.builder()
                        .id(h.getId())
                        .reportId(h.getReport().getId())
                        .adminId(h.getAdmin().getId())
                        .adminName(h.getAdmin().getFullName())
                        .action(h.getAction())
                        .note(h.getNote())
                        .processedAt(h.getProcessedAt())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public ReportResponse processReport(Long id, ReportProcessRequest request) {
        User admin = currentUserService.getCurrentUser();

        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy báo cáo."));

        report.setStatus(request.getStatus());
        report.setAdminNote(request.getAdminNote());

        if (request.getStatus() == ReportStatus.RESOLVED || request.getStatus() == ReportStatus.APPROVED) {
            report.setResolvedAt(LocalDateTime.now());
            // If report is approved as severe violation, hide the room
            if (request.getStatus() == ReportStatus.APPROVED) {
                Room room = report.getRoom();
                room.setStatus(RoomStatus.HIDDEN);
                roomRepository.save(room);
            }
        }

        Report savedReport = reportRepository.save(report);

        ReportHistory history = ReportHistory.builder()
                .report(savedReport)
                .admin(admin)
                .action(request.getAction())
                .note(request.getAdminNote())
                .build();
        reportHistoryRepository.save(history);

        return mapToReportResponse(savedReport);
    }

    private ReportResponse mapToReportResponse(Report report) {
        return ReportResponse.builder()
                .id(report.getId())
                .roomId(report.getRoom().getId())
                .roomTitle(report.getRoom().getTitle())
                .reporterId(report.getReporter().getId())
                .reporterName(report.getReporter().getFullName())
                .reason(report.getReason())
                .description(report.getDescription())
                .adminNote(report.getAdminNote())
                .status(report.getStatus())
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .resolvedAt(report.getResolvedAt())
                .build();
    }
}
