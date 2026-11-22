import React, { useState, useEffect } from 'react';
import { dashboardService } from '../../services/dashboard.service';
import { OwnerDashboard } from '../../types';
import { Home, Eye, Heart, Calendar, MessageSquare, Loader2 } from 'lucide-react';
import { Link } from 'react-router-dom';

export const LandlordDashboardPage: React.FC = () => {
  const [data, setData] = useState<OwnerDashboard | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    loadDashboard();
  }, []);

  const loadDashboard = async () => {
    try {
      const res = await dashboardService.getOwnerDashboard();
      setData(res);
    } catch (err) {
      console.error('Failed to load owner dashboard:', err);
    } finally {
      setIsLoading(false);
    }
  };

  if (isLoading) {
    return (
      <div className="flex justify-center items-center py-20">
        <Loader2 className="w-8 h-8 animate-spin text-blue-600" />
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Bảng điều khiển chủ trọ</h1>
          <p className="text-xs text-slate-500 mt-1">Tổng quan số liệu kinh doanh phòng trọ của bạn</p>
        </div>
        <Link
          to="/landlord/rooms/new"
          className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white text-xs font-semibold rounded-xl shadow-sm transition-colors"
        >
          + Đăng phòng mới
        </Link>
      </div>

      {/* Metrics Cards */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
        <div className="p-5 rounded-2xl bg-white border border-slate-200/80 shadow-xs">
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-medium text-slate-500">Tổng phòng</span>
            <Home className="w-4 h-4 text-blue-600" />
          </div>
          <span className="text-2xl font-bold text-slate-900">{data?.totalRooms || 0}</span>
          <div className="mt-2 text-[11px] text-slate-400">
            <span className="text-emerald-600 font-semibold">{data?.availableRooms || 0}</span> trống •{' '}
            <span className="text-slate-600 font-semibold">{data?.rentedRooms || 0}</span> đã thuê
          </div>
        </div>

        <div className="p-5 rounded-2xl bg-white border border-slate-200/80 shadow-xs">
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-medium text-slate-500">Tổng lượt xem</span>
            <Eye className="w-4 h-4 text-indigo-600" />
          </div>
          <span className="text-2xl font-bold text-slate-900">{data?.totalViews || 0}</span>
          <p className="mt-2 text-[11px] text-slate-400">Lượt quan tâm từ khách</p>
        </div>

        <div className="p-5 rounded-2xl bg-white border border-slate-200/80 shadow-xs">
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-medium text-slate-500">Yêu thích</span>
            <Heart className="w-4 h-4 text-red-500" />
          </div>
          <span className="text-2xl font-bold text-slate-900">{data?.totalFavorites || 0}</span>
          <p className="mt-2 text-[11px] text-slate-400">Được lưu vào danh sách</p>
        </div>

        <div className="p-5 rounded-2xl bg-white border border-slate-200/80 shadow-xs">
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-medium text-slate-500">Lịch hẹn xem</span>
            <Calendar className="w-4 h-4 text-amber-500" />
          </div>
          <span className="text-2xl font-bold text-slate-900">{data?.totalAppointments || 0}</span>
          <p className="mt-2 text-[11px] text-amber-600 font-semibold">{data?.pendingAppointments || 0} lịch chờ duyệt</p>
        </div>
      </div>
    </div>
  );
};
