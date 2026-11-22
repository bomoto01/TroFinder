package com.trofinder.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trofinder.dto.response.AdminDashboardResponse;
import com.trofinder.dto.response.OwnerDashboardResponse;
import com.trofinder.entity.Room;
import com.trofinder.entity.User;
import com.trofinder.enums.AppointmentStatus;
import com.trofinder.enums.ApprovalStatus;
import com.trofinder.enums.ReportStatus;
import com.trofinder.enums.RoomStatus;
import com.trofinder.repository.AppointmentRepository;
import com.trofinder.repository.ConversationRepository;
import com.trofinder.repository.ReportRepository;
import com.trofinder.repository.RoomRepository;
import com.trofinder.repository.UserRepository;
import com.trofinder.service.interfaces.CurrentUserService;
import com.trofinder.service.interfaces.DashboardService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final RoomRepository roomRepository;
    private final AppointmentRepository appointmentRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional(readOnly = true)
    public OwnerDashboardResponse getOwnerDashboard() {
        User currentUser = currentUserService.getCurrentUser();
        Long ownerId = currentUser.getId();

        List<Room> ownerRooms = roomRepository.findByOwnerIdAndDeletedFalse(ownerId);

        long totalRooms = ownerRooms.size();
        long availableRooms = ownerRooms.stream()
                .filter(r -> r.getStatus() == RoomStatus.AVAILABLE)
                .count();
        long rentedRooms = ownerRooms.stream()
                .filter(r -> r.getStatus() == RoomStatus.FULL)
                .count();

        long totalViews = ownerRooms.stream()
                .mapToLong(r -> r.getViewCount() != null ? r.getViewCount() : 0L)
                .sum();

        long totalFavorites = ownerRooms.stream()
                .mapToLong(r -> r.getFavoriteCount() != null ? r.getFavoriteCount() : 0L)
                .sum();

        long totalAppointments = appointmentRepository.findByOwnerAndDeletedFalseOrderByCreatedAtDesc(currentUser).size();
        long pendingAppointments = appointmentRepository.countByOwnerAndStatusAndDeletedFalse(currentUser, AppointmentStatus.PENDING);
        long totalConversations = conversationRepository.findByUserId(ownerId).size();

        return OwnerDashboardResponse.builder()
                .totalRooms(totalRooms)
                .availableRooms(availableRooms)
                .rentedRooms(rentedRooms)
                .totalViews(totalViews)
                .totalFavorites(totalFavorites)
                .totalAppointments(totalAppointments)
                .pendingAppointments(pendingAppointments)
                .totalConversations(totalConversations)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardResponse getAdminDashboard() {
        long totalUsers = userRepository.count();
        long totalLandlords = userRepository.countByRole_Name("LANDLORD");
        long totalTenants = userRepository.countByRole_Name("TENANT");

        long totalRooms = roomRepository.countByDeletedFalse();
        long pendingRooms = roomRepository.countByApprovalStatusAndDeletedFalse(ApprovalStatus.PENDING);
        long approvedRooms = roomRepository.countByApprovalStatusAndDeletedFalse(ApprovalStatus.APPROVED);
        long rejectedRooms = roomRepository.countByApprovalStatusAndDeletedFalse(ApprovalStatus.REJECTED);

        long totalReports = reportRepository.count();
        long pendingReports = reportRepository.countByStatus(ReportStatus.PENDING);

        return AdminDashboardResponse.builder()
                .totalUsers(totalUsers)
                .totalLandlords(totalLandlords)
                .totalTenants(totalTenants)
                .totalRooms(totalRooms)
                .pendingRooms(pendingRooms)
                .approvedRooms(approvedRooms)
                .rejectedRooms(rejectedRooms)
                .totalReports(totalReports)
                .pendingReports(pendingReports)
                .build();
    }
}
