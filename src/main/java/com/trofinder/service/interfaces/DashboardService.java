package com.trofinder.service.interfaces;

import com.trofinder.dto.response.AdminDashboardResponse;
import com.trofinder.dto.response.OwnerDashboardResponse;

public interface DashboardService {

    OwnerDashboardResponse getOwnerDashboard();

    AdminDashboardResponse getAdminDashboard();
}
