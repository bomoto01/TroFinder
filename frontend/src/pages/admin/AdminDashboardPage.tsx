import React, { useState, useEffect } from 'react';
import { dashboardService } from '../../services/dashboard.service';
import { AdminDashboard } from '../../types';
import { Users, Home, ShieldAlert, CheckCircle2, Clock, Loader2 } from 'lucide-react';

export const AdminDashboardPage: React.FC = () => {
  const [data, setData] = useState<AdminDashboard | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    loadDashboard();
  }, []);

  const loadDashboard = async () => {
    try {
      const res = await dashboardService.getAdminDashboard();
      setData(res);
    } catch (err) {
      console.error('Failed to load admin dashboard:', err);
    } finally {
      setIsLoading(false);
    }
  };

  if (isLoading) {
    return (
      <div className="flex justify-center items-center py-20">
        <Loader2 className="w-8 h-8 animate-spin text-purple-600" />
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      <div>
        <h1 className="text-2xl font-bold text-slate-900">Bảng điều khiển Quản trị viên (Admin)</h1>
        <p className="text-xs text-slate-500 mt-1">Giám sát toàn bộ hệ thống TroFinder</p>
      </div>

      <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
        <div className="p-5 rounded-2xl bg-white border border-slate-200/80 shadow-xs">
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-medium text-slate-500">Người dùng</span>
            <Users className="w-4 h-4 text-blue-600" />
          </div>
          <span className="text-2xl font-bold text-slate-900">{data?.totalUsers || 0}</span>
          <div className="mt-2 text-[11px] text-slate-400">
            {data?.totalLandlords || 0} chủ trọ • {data?.totalTenants || 0} người thuê
          </div>
        </div>

        <div className="p-5 rounded-2xl bg-white border border-slate-200/80 shadow-xs">
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-medium text-slate-500">Tổng phòng</span>
            <Home className="w-4 h-4 text-indigo-600" />
          </div>
          <span className="text-2xl font-bold text-slate-900">{data?.totalRooms || 0}</span>
          <p className="mt-2 text-[11px] text-emerald-600 font-semibold">{data?.approvedRooms || 0} đã duyệt</p>
        </div>

        <div className="p-5 rounded-2xl bg-white border border-slate-200/80 shadow-xs">
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-medium text-slate-500">Phòng chờ duyệt</span>
            <Clock className="w-4 h-4 text-amber-500" />
          </div>
          <span className="text-2xl font-bold text-slate-900">{data?.pendingRooms || 0}</span>
          <p className="mt-2 text-[11px] text-slate-400">Cần admin phê duyệt</p>
        </div>

        <div className="p-5 rounded-2xl bg-white border border-slate-200/80 shadow-xs">
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-medium text-slate-500">Báo cáo vi phạm</span>
            <ShieldAlert className="w-4 h-4 text-red-500" />
          </div>
          <span className="text-2xl font-bold text-slate-900">{data?.totalReports || 0}</span>
          <p className="mt-2 text-[11px] text-red-600 font-semibold">{data?.pendingReports || 0} chưa xử lý</p>
        </div>
      </div>
    </div>
  );
};
