import React, { useState, useEffect } from 'react';
import { appointmentService } from '../../services/appointment.service';
import { Appointment } from '../../types';
import { Calendar, Clock, MapPin, Loader2, CheckCircle2, XCircle } from 'lucide-react';

export const MyBookingsPage: React.FC = () => {
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    loadAppointments();
  }, []);

  const loadAppointments = async () => {
    setIsLoading(true);
    try {
      const data = await appointmentService.getTenantAppointments();
      setAppointments(data || []);
    } catch (err) {
      console.error('Failed to load appointments:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'CONFIRMED':
        return <span className="text-[11px] font-semibold px-2.5 py-0.5 rounded-full bg-emerald-50 text-emerald-700 border border-emerald-200">Đã xác nhận</span>;
      case 'REJECTED':
        return <span className="text-[11px] font-semibold px-2.5 py-0.5 rounded-full bg-red-50 text-red-700 border border-red-200">Đã từ chối</span>;
      case 'COMPLETED':
        return <span className="text-[11px] font-semibold px-2.5 py-0.5 rounded-full bg-blue-50 text-blue-700 border border-blue-200">Hoàn thành</span>;
      case 'PENDING':
      default:
        return <span className="text-[11px] font-semibold px-2.5 py-0.5 rounded-full bg-amber-50 text-amber-700 border border-amber-200">Chờ xác nhận</span>;
    }
  };

  return (
    <div className="max-w-4xl mx-auto px-4 py-8 space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-slate-900">Lịch hẹn xem phòng của bạn</h1>
        <p className="text-xs text-slate-500 mt-1">Theo dõi trạng thái các cuộc hẹn với chủ trọ</p>
      </div>

      {isLoading ? (
        <div className="flex justify-center items-center py-20">
          <Loader2 className="w-8 h-8 animate-spin text-blue-600" />
        </div>
      ) : appointments.length === 0 ? (
        <div className="text-center py-16 bg-white rounded-2xl border border-slate-200 p-8">
          <Calendar className="w-10 h-10 text-slate-300 mx-auto mb-2" />
          <p className="text-slate-600 font-medium mb-1">Bạn chưa có lịch hẹn xem phòng nào.</p>
          <p className="text-xs text-slate-400">Hãy tìm kiếm căn phòng phù hợp và đặt lịch trực tiếp!</p>
        </div>
      ) : (
        <div className="space-y-4">
          {appointments.map((apt) => (
            <div key={apt.id} className="p-5 rounded-2xl bg-white border border-slate-200/80 shadow-sm flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
              <div className="space-y-1.5">
                <div className="flex items-center gap-2">
                  <h3 className="font-bold text-sm text-slate-900">{apt.roomTitle}</h3>
                  {getStatusBadge(apt.status)}
                </div>
                <p className="text-xs text-slate-500 flex items-center gap-1">
                  <MapPin className="w-3.5 h-3.5" /> {apt.roomAddress}
                </p>
                <div className="flex items-center gap-4 text-xs text-slate-600 pt-1">
                  <span className="flex items-center gap-1">
                    <Calendar className="w-3.5 h-3.5 text-blue-600" /> Ngày: {apt.appointmentDate}
                  </span>
                  <span className="flex items-center gap-1">
                    <Clock className="w-3.5 h-3.5 text-blue-600" /> Giờ: {apt.appointmentTime}
                  </span>
                </div>
                {apt.note && (
                  <p className="text-xs text-slate-400 italic">Ghi chú: {apt.note}</p>
                )}
              </div>

              <div className="text-right text-xs text-slate-500 shrink-0">
                <span className="block font-medium text-slate-700">Chủ trọ: {apt.ownerName}</span>
                <span>SĐT: {apt.ownerPhone}</span>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
