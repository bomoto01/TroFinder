import { api } from './api';
import { ApiResponse, User } from '../types';

export const authService = {
  login: async (loginData: any) => {
    const res = await api.post<ApiResponse<{ token: string; user: User }>>('/api/auth/login', loginData);
    if (res.data.data?.token) {
      localStorage.setItem('access_token', res.data.data.token);
      localStorage.setItem('user_info', JSON.stringify(res.data.data.user));
    }
    return res.data.data;
  },

  register: async (registerData: any) => {
    const res = await api.post<ApiResponse<User>>('/api/auth/register', registerData);
    return res.data.data;
  },

  getCurrentUser: (): User | null => {
    const userStr = localStorage.getItem('user_info');
    if (!userStr) return null;
    try {
      return JSON.parse(userStr);
    } catch {
      return null;
    }
  },

  logout: () => {
    localStorage.removeItem('access_token');
    localStorage.removeItem('user_info');
  },
};
