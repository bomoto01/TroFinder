import { api } from './api';
import { AdminDashboard, ApiResponse, OwnerDashboard } from '../types';

export const dashboardService = {
  getOwnerDashboard: async () => {
    const res = await api.get<ApiResponse<OwnerDashboard>>('/api/dashboard/owner');
    return res.data.data;
  },

  getAdminDashboard: async () => {
    const res = await api.get<ApiResponse<AdminDashboard>>('/api/dashboard/admin');
    return res.data.data;
  },
};
