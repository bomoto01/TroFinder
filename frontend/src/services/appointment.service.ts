import { api } from './api';
import { ApiResponse, Appointment } from '../types';

export const appointmentService = {
  createAppointment: async (data: { roomId: number; appointmentDate: string; appointmentTime: string; note?: string }) => {
    const res = await api.post<ApiResponse<Appointment>>('/api/appointments', data);
    return res.data.data;
  },

  getTenantAppointments: async () => {
    const res = await api.get<ApiResponse<Appointment[]>>('/api/appointments/tenant');
    return res.data.data;
  },

  getOwnerAppointments: async () => {
    const res = await api.get<ApiResponse<Appointment[]>>('/api/appointments/owner');
    return res.data.data;
  },

  updateStatus: async (id: number, status: string) => {
    const res = await api.put<ApiResponse<Appointment>>(`/api/appointments/${id}`, { status });
    return res.data.data;
  },
};
