import { api } from './api';
import { ApiResponse, User } from '../types';

export const authService = {
  login: async (loginData: any) => {
    const res = await api.post<ApiResponse<any>>('/api/auth/login', loginData);
    const data = res.data.data;
    const token = data?.accessToken || data?.token;
    if (token) {
      localStorage.setItem('access_token', token);
      localStorage.setItem('user_info', JSON.stringify(data.user));
      window.dispatchEvent(new Event('auth-change'));
    }
    return data;
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

  getUserRole: (): string => {
    const user = authService.getCurrentUser();
    if (!user || !user.role) return '';
    const rawRole = typeof user.role === 'string' ? user.role : (user.role as any).name || '';
    return rawRole.replace(/^ROLE_/, '').toUpperCase();
  },

  logout: () => {
    localStorage.removeItem('access_token');
    localStorage.removeItem('user_info');
    window.dispatchEvent(new Event('auth-change'));
  },
};
