package com.trofinder.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.trofinder.dto.request.ReportCreateRequest;
import com.trofinder.dto.request.ReportProcessRequest;
import com.trofinder.dto.response.ReportResponse;
import com.trofinder.entity.Report;
import com.trofinder.entity.ReportHistory;
import com.trofinder.entity.Role;
import com.trofinder.entity.Room;
import com.trofinder.entity.User;
import com.trofinder.enums.ReportReason;
import com.trofinder.enums.ReportStatus;
import com.trofinder.repository.ReportHistoryRepository;
import com.trofinder.repository.ReportRepository;
import com.trofinder.repository.RoomRepository;
import com.trofinder.service.impl.ReportServiceImpl;
import com.trofinder.service.interfaces.CurrentUserService;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private ReportHistoryRepository reportHistoryRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private ReportServiceImpl reportService;

    private User user;
    private User admin;
    private Room room;
    private Report report;

    @BeforeEach
    void setUp() {
        Role userRole = Role.builder().id(1L).name("TENANT").build();
        user = User.builder().id(1L).username("reporter").fullName("Tran Van C").role(userRole).build();

        Role adminRole = Role.builder().id(3L).name("ADMIN").build();
        admin = User.builder().id(99L).username("admin").fullName("Admin System").role(adminRole).build();

        room = Room.builder().id(50L).title("Phòng có dấu hiệu lừa đảo").deleted(false).build();

        report = Report.builder()
                .id(10L)
                .room(room)
                .reporter(user)
                .reason(ReportReason.SCAM)
                .description("Phòng này yêu cầu cọc trước qua Zalo ảo")
                .status(ReportStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("Người dùng gửi báo cáo vi phạm phòng trọ")
    void createReport_Success() {
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(roomRepository.findByIdAndDeletedFalse(50L)).thenReturn(Optional.of(room));
        when(reportRepository.save(any(Report.class))).thenReturn(report);

        ReportCreateRequest request = new ReportCreateRequest();
        request.setRoomId(50L);
        request.setReason(ReportReason.SCAM);
        request.setDescription("Phòng này yêu cầu cọc trước qua Zalo ảo");

        ReportResponse response = reportService.createReport(request);

        assertNotNull(response);
        assertEquals(ReportReason.SCAM, response.getReason());
        assertEquals(ReportStatus.PENDING, response.getStatus());
        verify(reportRepository).save(any(Report.class));
    }

    @Test
    @DisplayName("Admin xử lý và phê duyệt báo cáo vi phạm")
    void processReport_Success() {
        when(currentUserService.getCurrentUser()).thenReturn(admin);
        when(reportRepository.findById(10L)).thenReturn(Optional.of(report));
        when(reportRepository.save(any(Report.class))).thenReturn(report);
        when(reportHistoryRepository.save(any(ReportHistory.class))).thenReturn(new ReportHistory());

        ReportProcessRequest request = new ReportProcessRequest();
        request.setStatus(ReportStatus.APPROVED);
        request.setAction("HIDE_ROOM_AND_WARN_OWNER");
        request.setAdminNote("Xác minh phản ánh đúng sự thật");

        ReportResponse response = reportService.processReport(10L, request);

        assertNotNull(response);
        assertEquals(ReportStatus.APPROVED, response.getStatus());
        verify(reportHistoryRepository).save(any(ReportHistory.class));
    }
}
