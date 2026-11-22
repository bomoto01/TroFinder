package com.trofinder.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponse {

    private Long totalUsers;
    private Long totalLandlords;
    private Long totalTenants;
    private Long totalRooms;
    private Long pendingRooms;
    private Long approvedRooms;
    private Long rejectedRooms;
    private Long totalReports;
    private Long pendingReports;
}
