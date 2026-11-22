package com.trofinder.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.trofinder.dto.response.AdminDashboardResponse;
import com.trofinder.enums.ApprovalStatus;
import com.trofinder.enums.ReportStatus;
import com.trofinder.repository.AppointmentRepository;
import com.trofinder.repository.ConversationRepository;
import com.trofinder.repository.ReportRepository;
import com.trofinder.repository.RoomRepository;
import com.trofinder.repository.UserRepository;
import com.trofinder.service.impl.DashboardServiceImpl;
import com.trofinder.service.interfaces.CurrentUserService;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @Test
    @DisplayName("Lấy thống kê dashboard dành cho quản trị viên")
    void getAdminDashboard_Success() {
        when(userRepository.count()).thenReturn(150L);
        when(userRepository.countByRole_Name("LANDLORD")).thenReturn(40L);
        when(userRepository.countByRole_Name("TENANT")).thenReturn(105L);
        when(roomRepository.countByDeletedFalse()).thenReturn(300L);
        when(roomRepository.countByApprovalStatusAndDeletedFalse(ApprovalStatus.PENDING)).thenReturn(12L);
        when(roomRepository.countByApprovalStatusAndDeletedFalse(ApprovalStatus.APPROVED)).thenReturn(280L);
        when(roomRepository.countByApprovalStatusAndDeletedFalse(ApprovalStatus.REJECTED)).thenReturn(8L);
        when(reportRepository.count()).thenReturn(5L);
        when(reportRepository.countByStatus(ReportStatus.PENDING)).thenReturn(2L);

        AdminDashboardResponse response = dashboardService.getAdminDashboard();

        assertNotNull(response);
        assertEquals(150L, response.getTotalUsers());
        assertEquals(40L, response.getTotalLandlords());
        assertEquals(300L, response.getTotalRooms());
        assertEquals(280L, response.getApprovedRooms());
        assertEquals(5L, response.getTotalReports());
        assertEquals(2L, response.getPendingReports());
    }
}
