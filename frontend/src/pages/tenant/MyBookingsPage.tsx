import React, { useState, useEffect } from 'react';
import { appointmentService } from '../../services/appointment.service';
import { authService } from '../../services/auth.service';
import { Appointment } from '../../types';
import {
  Calendar,
  Clock,
  MapPin,
  Loader2,
  CheckCircle2,
  XCircle,
  Phone,
  User,
  ExternalLink,
} from 'lucide-react';
import { Link } from 'react-router-dom';

export const MyBookingsPage: React.FC = () => {
  const currentUser = authService.getCurrentUser();
  const rawRole = typeof currentUser?.role === 'string' ? currentUser.role : currentUser?.role?.name || '';
  const role = rawRole.replace(/^ROLE_/, '').toUpperCase();
  const isLandlord = role === 'LANDLORD' || role === 'ADMIN';

  const [activeTab, setActiveTab] = useState<'received' | 'sent'>(isLandlord ? 'received' : 'sent');
  const [receivedAppointments, setReceivedAppointments] = useState<Appointment[]>([]);
  const [sentAppointments, setSentAppointments] = useState<Appointment[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [actionLoadingId, setActionLoadingId] = useState<number | null>(null);

  useEffect(() => {
    loadAppointments();
  }, []);

  const loadAppointments = async () => {
    setIsLoading(true);
    try {
      if (isLandlord) {
        const [ownerAppts, tenantAppts] = await Promise.all([
          appointmentService.getOwnerAppointments().catch(() => []),
          appointmentService.getTenantAppointments().catch(() => []),
        ]);
        setReceivedAppointments(ownerAppts || []);
        setSentAppointments(tenantAppts || []);
      } else {
        const tenantAppts = await appointmentService.getTenantAppointments().catch(() => []);
        setSentAppointments(tenantAppts || []);
      }
    } catch (err) {
      console.error('Failed to load appointments:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleUpdateStatus = async (apptId: number, status: string) => {
    setActionLoadingId(apptId);
    try {
      await appointmentService.updateStatus(apptId, status);
      setReceivedAppointments((prev) =>
        prev.map((a) => (a.id === apptId ? { ...a, status: status as any } : a))
      );
    } catch (err) {
      alert('Không thể cập nhật lịch hẹn. Vui lòng thử lại.');
    } finally {
      setActionLoadingId(null);
    }
  };

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'CONFIRMED':
        return (
          <span className="text-[11px] font-semibold px-2.5 py-0.5 rounded-full bg-emerald-50 text-emerald-700 border border-emerald-200 flex items-center gap-1">
            <CheckCircle2 className="w-3 h-3" /> Đã xác nhận
          </span>
        );
      case 'REJECTED':
        return (
          <span className="text-[11px] font-semibold px-2.5 py-0.5 rounded-full bg-red-50 text-red-700 border border-red-200 flex items-center gap-1">
            <XCircle className="w-3 h-3" /> Đã từ chối
          </span>
        );
      case 'COMPLETED':
        return (
          <span className="text-[11px] font-semibold px-2.5 py-0.5 rounded-full bg-blue-50 text-blue-700 border border-blue-200">
            Đã hoàn tất
          </span>
        );
      case 'PENDING':
      default:
        return (
          <span className="text-[11px] font-semibold px-2.5 py-0.5 rounded-full bg-amber-50 text-amber-700 border border-amber-200 flex items-center gap-1">
            <Clock className="w-3 h-3" /> Chờ xác nhận
          </span>
        );
    }
  };

  const currentList = activeTab === 'received' ? receivedAppointments : sentAppointments;
  const pendingReceivedCount = receivedAppointments.filter((a) => a.status === 'PENDING').length;

  return (
    <div className="max-w-4xl mx-auto px-4 py-8 space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-slate-900">
          {isLandlord && activeTab === 'received'
            ? 'Khách hẹn xem phòng của bạn'
            : 'Lịch hẹn xem phòng'}
        </h1>
        <p className="text-xs text-slate-500 mt-1">
          {isLandlord && activeTab === 'received'
            ? 'Quản lý và duyệt các yêu cầu đặt lịch hẹn từ người tìm trọ'
            : 'Theo dõi trạng thái các cuộc hẹn xem phòng thực tế'}
        </p>
      </div>

      {/* Tabs for Landlord */}
      {isLandlord && (
        <div className="flex border-b border-slate-200 gap-6">
          <button
            onClick={() => setActiveTab('received')}
            className={`pb-3 text-sm font-semibold relative transition-colors flex items-center gap-2 ${
              activeTab === 'received' ? 'text-blue-600' : 'text-slate-500 hover:text-slate-900'
            }`}
          >
            Khách hẹn tôi ({receivedAppointments.length})
            {pendingReceivedCount > 0 && (
              <span className="px-1.5 py-0.2 rounded-full text-[10px] font-bold bg-amber-100 text-amber-700">
                {pendingReceivedCount} mới
              </span>
            )}
            {activeTab === 'received' && (
              <span className="absolute bottom-0 left-0 right-0 h-0.5 bg-blue-600 rounded-full" />
            )}
          </button>
          <button
            onClick={() => setActiveTab('sent')}
            className={`pb-3 text-sm font-semibold relative transition-colors ${
              activeTab === 'sent' ? 'text-blue-600' : 'text-slate-500 hover:text-slate-900'
            }`}
          >
            Lịch tôi đã đặt ({sentAppointments.length})
            {activeTab === 'sent' && (
              <span className="absolute bottom-0 left-0 right-0 h-0.5 bg-blue-600 rounded-full" />
            )}
          </button>
        </div>
      )}

      {isLoading ? (
        <div className="flex flex-col justify-center items-center py-20 space-y-2">
          <Loader2 className="w-8 h-8 animate-spin text-blue-600" />
          <p className="text-xs text-slate-400">Đang tải danh sách lịch hẹn...</p>
        </div>
      ) : currentList.length === 0 ? (
        <div className="text-center py-16 bg-white rounded-3xl border border-slate-200/80 p-8 space-y-2">
          <Calendar className="w-10 h-10 text-slate-300 mx-auto mb-1" />
          <p className="text-slate-700 font-semibold text-sm">
            {activeTab === 'received'
              ? 'Chưa có khách nào đặt lịch hẹn xem phòng của bạn.'
              : 'Bạn chưa có lịch hẹn xem phòng nào.'}
          </p>
          <p className="text-xs text-slate-400">
            {activeTab === 'received'
              ? 'Khi có người tìm trọ đăng ký xem phòng, cuộc hẹn sẽ hiển thị tại đây để bạn duyệt.'
              : 'Hãy tìm kiếm căn phòng phù hợp và đặt lịch trực tiếp!'}
          </p>
          {activeTab === 'sent' && (
            <Link
              to="/rooms"
              className="inline-block mt-3 px-4 py-2 bg-blue-600 text-white text-xs font-semibold rounded-xl hover:bg-blue-700 transition-colors"
            >
              Khám phá phòng trọ
            </Link>
          )}
        </div>
      ) : (
        <div className="space-y-4">
          {currentList.map((apt) => {
            const isReceived = activeTab === 'received';
            const isPending = apt.status === 'PENDING';
            const isConfirmed = apt.status === 'CONFIRMED';

            return (
              <div
                key={apt.id}
                className={`p-5 rounded-2xl bg-white border shadow-xs flex flex-col md:flex-row justify-between items-start md:items-center gap-4 transition-all ${
                  isPending && isReceived
                    ? 'border-amber-300 ring-2 ring-amber-50'
                    : 'border-slate-200/80 hover:border-slate-300'
                }`}
              >
                <div className="space-y-2 flex-1">
                  <div className="flex flex-wrap items-center gap-2">
                    <Link
                      to={`/rooms/${apt.roomId}`}
                      className="font-bold text-sm text-slate-900 hover:text-blue-600 transition-colors"
                    >
                      {apt.roomTitle || `Phòng #${apt.roomId}`}
                    </Link>
                    {getStatusBadge(apt.status)}
                  </div>

                  <p className="text-xs text-slate-500 flex items-center gap-1">
                    <MapPin className="w-3.5 h-3.5 text-slate-400 shrink-0" /> {apt.roomAddress}
                  </p>

                  <div className="flex flex-wrap items-center gap-4 text-xs text-slate-700 pt-1">
                    <span className="flex items-center gap-1.5 font-medium bg-slate-50 px-2.5 py-1 rounded-lg border border-slate-100">
                      <Calendar className="w-3.5 h-3.5 text-blue-600" /> Ngày: {apt.appointmentDate}
                    </span>
                    <span className="flex items-center gap-1.5 font-medium bg-slate-50 px-2.5 py-1 rounded-lg border border-slate-100">
                      <Clock className="w-3.5 h-3.5 text-blue-600" /> Giờ: {apt.appointmentTime}
                    </span>
                  </div>

                  {apt.note && (
                    <p className="text-xs text-slate-500 bg-amber-50/50 p-2 rounded-lg border border-amber-100/60 italic">
                      Ghi chú: "{apt.note}"
                    </p>
                  )}
                </div>

                {/* Contact info & Action Buttons */}
                <div className="w-full md:w-auto flex flex-col md:items-end justify-between gap-3 pt-3 md:pt-0 border-t md:border-t-0 border-slate-100 shrink-0">
                  {isReceived ? (
                    /* Info for Landlord viewing Tenant */
                    <div className="text-xs text-slate-600 space-y-1">
                      <div className="flex items-center gap-1 md:justify-end">
                        <User className="w-3.5 h-3.5 text-slate-400" />
                        <span className="font-semibold text-slate-800">
                          Khách: {apt.tenantName || 'Người tìm trọ'}
                        </span>
                      </div>
                      <div className="flex items-center gap-1 md:justify-end">
                        <Phone className="w-3.5 h-3.5 text-blue-600" />
                        <a
                          href={`tel:${apt.tenantPhone}`}
                          className="font-bold text-blue-600 hover:underline"
                        >
                          {apt.tenantPhone || 'Chưa cung cấp'}
                        </a>
                      </div>
                    </div>
                  ) : (
                    /* Info for Tenant viewing Landlord */
                    <div className="text-xs text-slate-600 space-y-1">
                      <div className="flex items-center gap-1 md:justify-end">
                        <User className="w-3.5 h-3.5 text-slate-400" />
                        <span className="font-semibold text-slate-800">
                          Chủ trọ: {apt.ownerName}
                        </span>
                      </div>
                      <div className="flex items-center gap-1 md:justify-end">
                        <Phone className="w-3.5 h-3.5 text-blue-600" />
                        <a
                          href={`tel:${apt.ownerPhone}`}
                          className="font-bold text-blue-600 hover:underline"
                        >
                          SĐT: {apt.ownerPhone}
                        </a>
                      </div>
                    </div>
                  )}

                  {/* Actions for Landlord */}
                  {isReceived && (
                    <div className="flex items-center gap-2">
                      {isPending && (
                        <>
                          <button
                            disabled={actionLoadingId === apt.id}
                            onClick={() => handleUpdateStatus(apt.id, 'REJECTED')}
                            className="px-3 py-1.5 rounded-xl border border-red-200 text-red-600 hover:bg-red-50 text-xs font-semibold flex items-center gap-1 transition-colors"
                          >
                            <XCircle className="w-3.5 h-3.5" /> Từ chối
                          </button>
                          <button
                            disabled={actionLoadingId === apt.id}
                            onClick={() => handleUpdateStatus(apt.id, 'CONFIRMED')}
                            className="px-4 py-1.5 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold flex items-center gap-1 shadow-sm transition-colors"
                          >
                            <CheckCircle2 className="w-3.5 h-3.5" /> Xác nhận
                          </button>
                        </>
                      )}

                      {isConfirmed && (
                        <button
                          disabled={actionLoadingId === apt.id}
                          onClick={() => handleUpdateStatus(apt.id, 'COMPLETED')}
                          className="px-3 py-1.5 rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-semibold transition-colors"
                        >
                          Đánh dấu đã xem
                        </button>
                      )}
                    </div>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};
