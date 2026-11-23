import React, { useState, useEffect } from 'react';
import { dashboardService } from '../../services/dashboard.service';
import { roomService } from '../../services/room.service';
import { appointmentService } from '../../services/appointment.service';
import { OwnerDashboard, RoomSummary, Appointment } from '../../types';
import {
  Home,
  Eye,
  Heart,
  Calendar,
  MessageSquare,
  Loader2,
  PlusCircle,
  ExternalLink,
  Phone,
  CheckCircle2,
  XCircle,
  Clock,
  MapPin,
  Search,
  Filter,
  AlertTriangle,
  RefreshCw,
  Trash2,
} from 'lucide-react';
import { Link } from 'react-router-dom';

export const LandlordDashboardPage: React.FC = () => {
  const [dashboardData, setDashboardData] = useState<OwnerDashboard | null>(null);
  const [rooms, setRooms] = useState<RoomSummary[]>([]);
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  // Filter & tab states
  const [activeTab, setActiveTab] = useState<'rooms' | 'appointments'>('rooms');
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState<'ALL' | 'AVAILABLE' | 'FULL' | 'HAS_APPOINTMENT'>('ALL');
  const [selectedRoomId, setSelectedRoomId] = useState<number | null>(null);
  const [actionLoadingId, setActionLoadingId] = useState<number | null>(null);

  useEffect(() => {
    loadAllData();
  }, []);

  const loadAllData = async () => {
    setIsLoading(true);
    try {
      const [dashRes, myRoomsRes, apptRes] = await Promise.all([
        dashboardService.getOwnerDashboard().catch(() => null),
        roomService.getMyRooms().catch(() => []),
        appointmentService.getOwnerAppointments().catch(() => []),
      ]);

      if (dashRes) setDashboardData(dashRes);
      if (myRoomsRes) setRooms(myRoomsRes);
      if (apptRes) setAppointments(apptRes);
    } catch (err) {
      console.error('Failed to load landlord data:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleToggleRoomStatus = async (room: RoomSummary) => {
    const nextStatus = room.status === 'AVAILABLE' ? 'FULL' : 'AVAILABLE';
    const statusText = nextStatus === 'AVAILABLE' ? 'Còn trống' : 'Đã cho thuê';
    if (!window.confirm(`Bạn muốn chuyển trạng thái phòng sang "${statusText}"?`)) return;

    setActionLoadingId(room.id);
    try {
      await roomService.updateRoomStatus(room.id, nextStatus);
      setRooms((prev) =>
        prev.map((r) => (r.id === room.id ? { ...r, status: nextStatus as any } : r))
      );
    } catch (err) {
      alert('Không thể đổi trạng thái phòng. Vui lòng thử lại.');
    } finally {
      setActionLoadingId(null);
    }
  };

  const handleDeleteRoom = async (roomId: number) => {
    if (!window.confirm('Bạn có chắc chắn muốn xóa phòng này?')) return;
    setActionLoadingId(roomId);
    try {
      await roomService.deleteRoom(roomId);
      setRooms((prev) => prev.filter((r) => r.id !== roomId));
      alert('Đã xóa phòng thành công.');
    } catch (err) {
      alert('Không thể xóa phòng. Vui lòng thử lại.');
    } finally {
      setActionLoadingId(null);
    }
  };

  const handleUpdateAppointment = async (apptId: number, status: string) => {
    try {
      await appointmentService.updateStatus(apptId, status);
      setAppointments((prev) =>
        prev.map((a) => (a.id === apptId ? { ...a, status: status as any } : a))
      );
      // Reload dashboard stats to keep counts accurate
      const dashRes = await dashboardService.getOwnerDashboard().catch(() => null);
      if (dashRes) setDashboardData(dashRes);
    } catch (err) {
      alert('Không thể cập nhật lịch hẹn. Vui lòng thử lại.');
    }
  };

  const formatPrice = (price: number) => {
    if (price >= 1000000) {
      return (price / 1000000).toLocaleString('vi-VN', { maximumFractionDigits: 1 }) + ' triệu';
    }
    return price.toLocaleString('vi-VN') + ' đ';
  };

  // Filtered rooms
  const filteredRooms = rooms.filter((room) => {
    const matchesSearch =
      room.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
      room.address.toLowerCase().includes(searchQuery.toLowerCase());

    if (!matchesSearch) return false;

    if (statusFilter === 'AVAILABLE') return room.status === 'AVAILABLE';
    if (statusFilter === 'FULL') return room.status === 'FULL';
    if (statusFilter === 'HAS_APPOINTMENT') return (room.appointmentCount || 0) > 0;
    return true;
  });

  // Filtered appointments
  const filteredAppointments = appointments.filter((appt) => {
    if (selectedRoomId && appt.roomId !== selectedRoomId) return false;
    return true;
  });

  const pendingAppointmentsCount = appointments.filter((a) => a.status === 'PENDING').length;

  if (isLoading) {
    return (
      <div className="flex flex-col justify-center items-center py-28 space-y-3">
        <Loader2 className="w-9 h-9 animate-spin text-blue-600" />
        <p className="text-sm text-slate-500 font-medium">Đang tải dữ liệu quản lý phòng...</p>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Bảng điều khiển chủ trọ</h1>
          <p className="text-xs text-slate-500 mt-1">
            Quản lý phòng trọ, theo dõi lượt xem, yêu thích và duyệt lịch hẹn xem phòng
          </p>
        </div>
        <div className="flex items-center gap-2">
          <button
            onClick={loadAllData}
            title="Làm mới dữ liệu"
            className="p-2.5 rounded-xl border border-slate-200 text-slate-600 hover:bg-slate-50 transition-colors"
          >
            <RefreshCw className="w-4 h-4" />
          </button>
          <Link
            to="/landlord/rooms/new"
            className="px-4 py-2.5 bg-blue-600 hover:bg-blue-700 text-white text-xs font-semibold rounded-xl shadow-sm transition-colors flex items-center gap-1.5"
          >
            <PlusCircle className="w-4 h-4" />
            Đăng phòng mới
          </Link>
        </div>
      </div>

      {/* Metrics Cards */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
        {/* Total Rooms */}
        <div
          onClick={() => {
            setActiveTab('rooms');
            setStatusFilter('ALL');
          }}
          className="p-5 rounded-2xl bg-white border border-slate-200/80 shadow-xs hover:border-blue-400 cursor-pointer transition-all"
        >
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-medium text-slate-500">Tổng phòng của tôi</span>
            <Home className="w-4 h-4 text-blue-600" />
          </div>
          <span className="text-2xl font-bold text-slate-900">{dashboardData?.totalRooms || rooms.length}</span>
          <div className="mt-2 text-[11px] text-slate-400">
            <span className="text-emerald-600 font-semibold">{dashboardData?.availableRooms || 0}</span> trống •{' '}
            <span className="text-slate-600 font-semibold">{dashboardData?.rentedRooms || 0}</span> đã thuê
          </div>
        </div>

        {/* Total Views */}
        <div className="p-5 rounded-2xl bg-white border border-slate-200/80 shadow-xs">
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-medium text-slate-500">Tổng lượt xem</span>
            <Eye className="w-4 h-4 text-indigo-600" />
          </div>
          <span className="text-2xl font-bold text-slate-900">{dashboardData?.totalViews || 0}</span>
          <p className="mt-2 text-[11px] text-slate-400">Lượt khách ghé xem phòng</p>
        </div>

        {/* Total Favorites */}
        <div className="p-5 rounded-2xl bg-white border border-slate-200/80 shadow-xs">
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-medium text-slate-500">Lượt yêu thích</span>
            <Heart className="w-4 h-4 text-red-500" />
          </div>
          <span className="text-2xl font-bold text-slate-900">{dashboardData?.totalFavorites || 0}</span>
          <p className="mt-2 text-[11px] text-slate-400">Được khách lưu vào yêu thích</p>
        </div>

        {/* Total Appointments */}
        <div
          onClick={() => {
            setActiveTab('appointments');
            setSelectedRoomId(null);
          }}
          className={`p-5 rounded-2xl bg-white border shadow-xs hover:border-amber-400 cursor-pointer transition-all ${
            pendingAppointmentsCount > 0 ? 'border-amber-300 ring-2 ring-amber-100' : 'border-slate-200/80'
          }`}
        >
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-medium text-slate-500">Lịch hẹn xem phòng</span>
            <Calendar className="w-4 h-4 text-amber-500" />
          </div>
          <div className="flex items-baseline gap-2">
            <span className="text-2xl font-bold text-slate-900">{dashboardData?.totalAppointments || appointments.length}</span>
            {pendingAppointmentsCount > 0 && (
              <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-amber-100 text-amber-700 animate-pulse">
                {pendingAppointmentsCount} chờ duyệt
              </span>
            )}
          </div>
          <p className="mt-2 text-[11px] text-amber-600 font-semibold">
            Bấm để xem danh sách lịch hẹn
          </p>
        </div>
      </div>

      {/* Tabs */}
      <div className="border-b border-slate-200 flex items-center justify-between">
        <div className="flex items-center gap-6">
          <button
            onClick={() => setActiveTab('rooms')}
            className={`pb-3 text-sm font-semibold relative transition-colors ${
              activeTab === 'rooms' ? 'text-blue-600' : 'text-slate-500 hover:text-slate-900'
            }`}
          >
            Danh sách phòng ({rooms.length})
            {activeTab === 'rooms' && (
              <span className="absolute bottom-0 left-0 right-0 h-0.5 bg-blue-600 rounded-full" />
            )}
          </button>
          <button
            onClick={() => setActiveTab('appointments')}
            className={`pb-3 text-sm font-semibold relative transition-colors flex items-center gap-1.5 ${
              activeTab === 'appointments' ? 'text-blue-600' : 'text-slate-500 hover:text-slate-900'
            }`}
          >
            Lịch hẹn xem phòng ({appointments.length})
            {pendingAppointmentsCount > 0 && (
              <span className="w-2 h-2 rounded-full bg-amber-500 animate-ping" />
            )}
            {activeTab === 'appointments' && (
              <span className="absolute bottom-0 left-0 right-0 h-0.5 bg-blue-600 rounded-full" />
            )}
          </button>
        </div>
      </div>

      {/* TAB 1: ROOMS LIST WITH IMAGES & METRICS */}
      {activeTab === 'rooms' && (
        <div className="space-y-6">
          {/* Search & Filters */}
          <div className="flex flex-col sm:flex-row items-stretch sm:items-center justify-between gap-3">
            <div className="relative flex-1 max-w-md">
              <input
                type="text"
                placeholder="Tìm phòng theo tên, địa chỉ..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="w-full pl-9 pr-4 py-2 text-xs border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
              />
              <Search className="w-4 h-4 text-slate-400 absolute left-3 top-2.5" />
            </div>

            <div className="flex items-center gap-2 overflow-x-auto pb-1 sm:pb-0">
              <span className="text-xs text-slate-400 flex items-center gap-1 shrink-0">
                <Filter className="w-3.5 h-3.5" /> Lọc:
              </span>
              <button
                onClick={() => setStatusFilter('ALL')}
                className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-all ${
                  statusFilter === 'ALL'
                    ? 'bg-blue-600 text-white'
                    : 'bg-white border border-slate-200 text-slate-600 hover:bg-slate-50'
                }`}
              >
                Tất cả ({rooms.length})
              </button>
              <button
                onClick={() => setStatusFilter('HAS_APPOINTMENT')}
                className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-all flex items-center gap-1 ${
                  statusFilter === 'HAS_APPOINTMENT'
                    ? 'bg-amber-600 text-white'
                    : 'bg-white border border-slate-200 text-amber-700 hover:bg-amber-50'
                }`}
              >
                📅 Có lịch hẹn ({rooms.filter((r) => (r.appointmentCount || 0) > 0).length})
              </button>
              <button
                onClick={() => setStatusFilter('AVAILABLE')}
                className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-all ${
                  statusFilter === 'AVAILABLE'
                    ? 'bg-emerald-600 text-white'
                    : 'bg-white border border-slate-200 text-slate-600 hover:bg-slate-50'
                }`}
              >
                Còn trống
              </button>
              <button
                onClick={() => setStatusFilter('FULL')}
                className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-all ${
                  statusFilter === 'FULL'
                    ? 'bg-slate-700 text-white'
                    : 'bg-white border border-slate-200 text-slate-600 hover:bg-slate-50'
                }`}
              >
                Đã thuê
              </button>
            </div>
          </div>

          {/* Room Cards Grid */}
          {filteredRooms.length === 0 ? (
            <div className="text-center py-16 bg-white rounded-3xl border border-slate-200/80 space-y-3">
              <div className="w-12 h-12 rounded-2xl bg-slate-100 flex items-center justify-center mx-auto text-slate-400">
                <Home className="w-6 h-6" />
              </div>
              <h3 className="text-sm font-semibold text-slate-800">Chưa có phòng nào phù hợp</h3>
              <p className="text-xs text-slate-500 max-w-sm mx-auto">
                {rooms.length === 0
                  ? 'Bạn chưa đăng phòng trọ nào. Hãy bấm nút "Đăng phòng mới" để bắt đầu tiếp cận người thuê trọ.'
                  : 'Không tìm thấy phòng khớp với bộ lọc tìm kiếm hiện tại.'}
              </p>
              {rooms.length === 0 && (
                <Link
                  to="/landlord/rooms/new"
                  className="inline-flex items-center gap-1.5 px-4 py-2 bg-blue-600 text-white text-xs font-semibold rounded-xl hover:bg-blue-700 shadow-sm transition-colors mt-2"
                >
                  <PlusCircle className="w-4 h-4" /> Đăng phòng ngay
                </Link>
              )}
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {filteredRooms.map((room) => {
                const isFull = room.status === 'FULL';
                const hasPending = (room.pendingAppointmentCount || 0) > 0;
                const totalAppts = room.appointmentCount || 0;

                return (
                  <div
                    key={room.id}
                    className="bg-white rounded-2xl border border-slate-200/80 shadow-xs hover:shadow-md transition-all flex flex-col overflow-hidden group"
                  >
                    {/* ROOM IMAGE WITH OVERLAYS */}
                    <div className="relative aspect-video w-full bg-slate-100 overflow-hidden">
                      <img
                        src={
                          room.thumbnail ||
                          'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=600&q=80'
                        }
                        alt={room.title}
                        className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                        onError={(e) => {
                          (e.target as HTMLImageElement).src =
                            'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=600&q=80';
                        }}
                      />

                      {/* Status Badges */}
                      <div className="absolute top-2.5 left-2.5 flex items-center gap-1.5">
                        <span
                          className={`px-2.5 py-1 rounded-lg text-[11px] font-bold backdrop-blur-md shadow-xs ${
                            isFull
                              ? 'bg-slate-800/90 text-slate-200'
                              : 'bg-emerald-600/90 text-white'
                          }`}
                        >
                          {isFull ? 'Đã cho thuê' : 'Còn trống'}
                        </span>
                        {room.approvalStatus === 'PENDING' && (
                          <span className="px-2 py-1 rounded-lg text-[10px] font-semibold bg-amber-500/90 text-white backdrop-blur-xs">
                            Chờ duyệt
                          </span>
                        )}
                      </div>

                      {/* Pending Appointments Alert Badge on Top-Right */}
                      {hasPending && (
                        <div className="absolute top-2.5 right-2.5">
                          <span className="px-2.5 py-1 rounded-lg text-[11px] font-bold bg-amber-500 text-white shadow-md flex items-center gap-1 animate-pulse">
                            <Clock className="w-3 h-3" />
                            {room.pendingAppointmentCount} hẹn chờ duyệt!
                          </span>
                        </div>
                      )}

                      {/* BOTTOM METRICS BAR ON IMAGE */}
                      <div className="absolute bottom-0 inset-x-0 bg-gradient-to-t from-black/85 via-black/50 to-transparent p-3 pt-6 flex items-center justify-between text-white text-xs">
                        {/* Lượt xem */}
                        <div className="flex items-center gap-1" title="Lượt xem phòng">
                          <Eye className="w-3.5 h-3.5 text-blue-300" />
                          <span className="font-semibold">{room.viewCount || 0}</span>
                          <span className="text-[10px] text-slate-300">xem</span>
                        </div>

                        {/* Lượt thích */}
                        <div className="flex items-center gap-1" title="Lượt người dùng yêu thích">
                          <Heart className="w-3.5 h-3.5 text-red-400" />
                          <span className="font-semibold">{room.favoriteCount || 0}</span>
                          <span className="text-[10px] text-slate-300">thích</span>
                        </div>

                        {/* Lịch hẹn xem */}
                        <div
                          className={`flex items-center gap-1 px-1.5 py-0.5 rounded ${
                            hasPending ? 'bg-amber-500 text-white font-bold' : ''
                          }`}
                          title="Tổng số lịch hẹn xem phòng"
                        >
                          <Calendar className="w-3.5 h-3.5 text-amber-300" />
                          <span className="font-semibold">{totalAppts}</span>
                          <span className="text-[10px] text-slate-300">hẹn</span>
                        </div>
                      </div>
                    </div>

                    {/* ROOM CONTENT */}
                    <div className="p-4 flex-1 flex flex-col justify-between space-y-3">
                      <div>
                        <div className="flex items-baseline justify-between gap-2">
                          <span className="text-base font-bold text-blue-600">
                            {formatPrice(room.price)}
                            <span className="text-xs font-normal text-slate-500">/tháng</span>
                          </span>
                          <span className="text-xs font-medium text-slate-500 bg-slate-100 px-2 py-0.5 rounded">
                            {room.area} m²
                          </span>
                        </div>

                        <Link
                          to={`/rooms/${room.id}`}
                          className="font-bold text-sm text-slate-800 hover:text-blue-600 line-clamp-1 mt-1 block transition-colors"
                          title={room.title}
                        >
                          {room.title}
                        </Link>

                        <div className="flex items-center gap-1 text-[11px] text-slate-400 mt-1">
                          <MapPin className="w-3 h-3 shrink-0" />
                          <span className="truncate">{room.address}</span>
                        </div>
                      </div>

                      {/* ACTION BUTTONS */}
                      <div className="pt-3 border-t border-slate-100 flex items-center justify-between gap-2">
                        {/* Xem lịch hẹn */}
                        <button
                          onClick={() => {
                            setSelectedRoomId(room.id);
                            setActiveTab('appointments');
                          }}
                          className={`flex-1 py-1.5 px-2.5 rounded-lg text-xs font-semibold flex items-center justify-center gap-1 transition-all ${
                            hasPending
                              ? 'bg-amber-50 text-amber-700 border border-amber-300 hover:bg-amber-100 font-bold'
                              : 'bg-slate-50 text-slate-700 border border-slate-200 hover:bg-slate-100'
                          }`}
                        >
                          <Calendar className="w-3.5 h-3.5" />
                          Lịch hẹn ({totalAppts})
                        </button>

                        {/* Đổi trạng thái còn trống / đã thuê */}
                        <button
                          disabled={actionLoadingId === room.id}
                          onClick={() => handleToggleRoomStatus(room)}
                          className="py-1.5 px-2.5 rounded-lg text-xs font-semibold border border-slate-200 text-slate-600 hover:bg-slate-50 transition-colors"
                          title="Đổi trạng thái Còn trống / Đã cho thuê"
                        >
                          {actionLoadingId === room.id ? (
                            <Loader2 className="w-3.5 h-3.5 animate-spin" />
                          ) : isFull ? (
                            'Mở lại phòng'
                          ) : (
                            'Đã cho thuê'
                          )}
                        </button>

                        {/* Xem trang chi tiết */}
                        <Link
                          to={`/rooms/${room.id}`}
                          target="_blank"
                          rel="noreferrer"
                          className="p-1.5 rounded-lg border border-slate-200 text-slate-500 hover:text-blue-600 hover:bg-blue-50 transition-colors"
                          title="Xem trang hiển thị phòng"
                        >
                          <ExternalLink className="w-3.5 h-3.5" />
                        </Link>

                        {/* Xóa phòng */}
                        <button
                          disabled={actionLoadingId === room.id}
                          onClick={() => handleDeleteRoom(room.id)}
                          className="p-1.5 rounded-lg border border-slate-200 text-slate-400 hover:text-red-600 hover:bg-red-50 transition-colors"
                          title="Xóa phòng"
                        >
                          <Trash2 className="w-3.5 h-3.5" />
                        </button>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      )}

      {/* TAB 2: APPOINTMENTS MANAGER */}
      {activeTab === 'appointments' && (
        <div className="space-y-6">
          {/* Appointment Filter Bar */}
          <div className="flex flex-col sm:flex-row items-stretch sm:items-center justify-between gap-3 bg-white p-4 rounded-2xl border border-slate-200/80">
            <div>
              <h2 className="text-sm font-bold text-slate-800">Quản lý lịch hẹn xem phòng của khách</h2>
              {selectedRoomId ? (
                <div className="flex items-center gap-2 mt-1">
                  <span className="text-xs text-blue-600 font-semibold">
                    Đang lọc theo phòng #{selectedRoomId}
                  </span>
                  <button
                    onClick={() => setSelectedRoomId(null)}
                    className="text-xs text-slate-400 hover:text-slate-600 underline"
                  >
                    Xem tất cả phòng
                  </button>
                </div>
              ) : (
                <p className="text-xs text-slate-500 mt-0.5">
                  Tổng cộng {appointments.length} lượt đặt lịch hẹn từ người tìm trọ
                </p>
              )}
            </div>

            <div className="flex items-center gap-2">
              <span className="text-xs font-semibold text-slate-600">
                Chờ duyệt:{' '}
                <span className="text-amber-600 font-bold">{pendingAppointmentsCount}</span>
              </span>
            </div>
          </div>

          {/* Appointments List */}
          {filteredAppointments.length === 0 ? (
            <div className="text-center py-16 bg-white rounded-3xl border border-slate-200/80 space-y-2">
              <Calendar className="w-10 h-10 text-slate-300 mx-auto" />
              <h3 className="text-sm font-semibold text-slate-800">Không có lịch hẹn nào</h3>
              <p className="text-xs text-slate-400">
                {selectedRoomId
                  ? 'Phòng này chưa có lịch hẹn nào được đặt.'
                  : 'Hiện tại chưa có khách hẹn xem phòng.'}
              </p>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {filteredAppointments.map((appt) => {
                const isPending = appt.status === 'PENDING';
                const isConfirmed = appt.status === 'CONFIRMED';
                const isRejected = appt.status === 'REJECTED';

                return (
                  <div
                    key={appt.id}
                    className={`bg-white rounded-2xl border p-5 shadow-xs space-y-4 transition-all ${
                      isPending
                        ? 'border-amber-300 ring-2 ring-amber-50'
                        : isConfirmed
                        ? 'border-emerald-200'
                        : 'border-slate-200/80'
                    }`}
                  >
                    {/* Header info */}
                    <div className="flex items-start justify-between gap-3">
                      <div>
                        <span
                          className={`inline-block px-2.5 py-0.5 rounded-full text-[11px] font-bold mb-1.5 ${
                            isPending
                              ? 'bg-amber-100 text-amber-700'
                              : isConfirmed
                              ? 'bg-emerald-100 text-emerald-700'
                              : isRejected
                              ? 'bg-red-100 text-red-700'
                              : 'bg-slate-100 text-slate-600'
                          }`}
                        >
                          {isPending
                            ? '⏳ Chờ bạn xác nhận'
                            : isConfirmed
                            ? '✅ Đã xác nhận'
                            : isRejected
                            ? '❌ Đã từ chối'
                            : 'Đã hoàn tất'}
                        </span>
                        <h4 className="font-bold text-sm text-slate-900 line-clamp-1">
                          {appt.roomTitle || `Phòng #${appt.roomId}`}
                        </h4>
                        <p className="text-xs text-slate-400 truncate">{appt.roomAddress}</p>
                      </div>

                      <div className="text-right shrink-0">
                        <div className="flex items-center gap-1 text-xs font-bold text-slate-800">
                          <Calendar className="w-3.5 h-3.5 text-blue-600" />
                          {appt.appointmentDate}
                        </div>
                        <div className="flex items-center justify-end gap-1 text-xs font-medium text-slate-500 mt-0.5">
                          <Clock className="w-3.5 h-3.5 text-blue-500" />
                          {appt.appointmentTime}
                        </div>
                      </div>
                    </div>

                    {/* Customer info */}
                    <div className="p-3 rounded-xl bg-slate-50 border border-slate-100 space-y-1.5 text-xs">
                      <div className="flex items-center justify-between">
                        <span className="text-slate-500">Khách hẹn:</span>
                        <span className="font-bold text-slate-800">{appt.tenantName || 'Khách tìm phòng'}</span>
                      </div>
                      <div className="flex items-center justify-between">
                        <span className="text-slate-500">Số điện thoại:</span>
                        <a
                          href={`tel:${appt.tenantPhone}`}
                          className="font-bold text-blue-600 flex items-center gap-1 hover:underline"
                        >
                          <Phone className="w-3 h-3" />
                          {appt.tenantPhone || 'Chưa cung cấp'}
                        </a>
                      </div>
                      {appt.note && (
                        <div className="pt-1.5 border-t border-slate-200/60 text-slate-600 italic">
                          "{appt.note}"
                        </div>
                      )}
                    </div>

                    {/* Actions */}
                    <div className="flex items-center justify-end gap-2 pt-1">
                      {isPending && (
                        <>
                          <button
                            onClick={() => handleUpdateAppointment(appt.id, 'REJECTED')}
                            className="px-3 py-1.5 rounded-xl border border-red-200 text-red-600 hover:bg-red-50 text-xs font-semibold flex items-center gap-1 transition-colors"
                          >
                            <XCircle className="w-3.5 h-3.5" /> Từ chối
                          </button>
                          <button
                            onClick={() => handleUpdateAppointment(appt.id, 'CONFIRMED')}
                            className="px-4 py-1.5 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold flex items-center gap-1 shadow-sm transition-colors"
                          >
                            <CheckCircle2 className="w-3.5 h-3.5" /> Xác nhận hẹn
                          </button>
                        </>
                      )}

                      {isConfirmed && (
                        <button
                          onClick={() => handleUpdateAppointment(appt.id, 'COMPLETED')}
                          className="px-3 py-1.5 rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-semibold transition-colors"
                        >
                          Đánh dấu đã xem phòng
                        </button>
                      )}

                      <Link
                        to={`/rooms/${appt.roomId}`}
                        target="_blank"
                        className="p-1.5 rounded-lg border border-slate-200 text-slate-400 hover:text-blue-600 transition-colors"
                        title="Xem phòng"
                      >
                        <ExternalLink className="w-3.5 h-3.5" />
                      </Link>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      )}
    </div>
  );
};
