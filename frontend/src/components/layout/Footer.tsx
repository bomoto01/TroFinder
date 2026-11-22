import React from 'react';
import { Link } from 'react-router-dom';

export const Footer: React.FC = () => {
  return (
    <footer className="bg-slate-900 text-slate-400 pt-12 pb-8 border-t border-slate-800">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 grid grid-cols-1 md:grid-cols-4 gap-8 mb-8">
        <div>
          <div className="flex items-center gap-2 mb-3">
            <div className="w-8 h-8 rounded-lg bg-blue-600 flex items-center justify-center text-white font-bold">
              T
            </div>
            <span className="text-lg font-bold text-white">TroFinder</span>
          </div>
          <p className="text-sm text-slate-400 leading-relaxed mb-4">
            Nền tảng tìm kiếm, cho thuê và quản lý phòng trọ thế hệ mới tích hợp trí tuệ nhân tạo (AI).
          </p>
        </div>

        <div>
          <h4 className="text-sm font-semibold text-white uppercase tracking-wider mb-4">Về TroFinder</h4>
          <ul className="space-y-2 text-sm">
            <li><Link to="/rooms" className="hover:text-white transition-colors">Tìm kiếm phòng</Link></li>
            <li><Link to="/map" className="hover:text-white transition-colors">Bản đồ phòng trọ</Link></li>
            <li><Link to="/rooms?ai=true" className="hover:text-white transition-colors">Tìm phòng bằng AI</Link></li>
            <li><Link to="/landlord/rooms/new" className="hover:text-white transition-colors">Đăng tin cho thuê</Link></li>
          </ul>
        </div>

        <div>
          <h4 className="text-sm font-semibold text-white uppercase tracking-wider mb-4">Khu vực phổ biến</h4>
          <ul className="space-y-2 text-sm">
            <li><Link to="/rooms?keyword=Cầu Giấy" className="hover:text-white transition-colors">Phòng trọ Cầu Giấy</Link></li>
            <li><Link to="/rooms?keyword=Đống Đa" className="hover:text-white transition-colors">Phòng trọ Đống Đa</Link></li>
            <li><Link to="/rooms?keyword=Bình Thạnh" className="hover:text-white transition-colors">Phòng trọ Bình Thạnh</Link></li>
            <li><Link to="/rooms?keyword=Thủ Đức" className="hover:text-white transition-colors">Phòng trọ TP. Thủ Đức</Link></li>
          </ul>
        </div>

        <div>
          <h4 className="text-sm font-semibold text-white uppercase tracking-wider mb-4">Hỗ trợ & Liên hệ</h4>
          <p className="text-sm mb-2">Hotline: <span className="text-white font-medium">1900 8888</span></p>
          <p className="text-sm mb-2">Email: <span className="text-white font-medium">support@trofinder.vn</span></p>
          <p className="text-sm">Giờ làm việc: 8:00 - 21:00 (Hàng ngày)</p>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-6 border-t border-slate-800 text-center text-xs text-slate-500">
        © {new Date().getFullYear()} TroFinder. Nền tảng tìm kiếm phòng trọ thông minh tích hợp AI. Bảo lưu mọi quyền.
      </div>
    </footer>
  );
};
