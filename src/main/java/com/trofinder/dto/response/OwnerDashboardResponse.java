package com.trofinder.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OwnerDashboardResponse {

    private Long totalRooms;
    private Long availableRooms;
    private Long rentedRooms;
    private Long totalViews;
    private Long totalFavorites;
    private Long totalAppointments;
    private Long pendingAppointments;
    private Long totalConversations;
}
