import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authService } from '../../services/auth.service';
import { User } from '../../types';
import { Home, MapPin, MessageSquare, Calendar, User as UserIcon, LogOut, PlusCircle, ShieldAlert, Sparkles } from 'lucide-react';

export const Navbar: React.FC = () => {
  const navigate = useNavigate();
  const [currentUser, setCurrentUser] = useState<User | null>(() => authService.getCurrentUser());

  useEffect(() => {
    const handleAuthChange = () => {
      setCurrentUser(authService.getCurrentUser());
    };
    window.addEventListener('auth-change', handleAuthChange);
    window.addEventListener('storage', handleAuthChange);
    return () => {
      window.removeEventListener('auth-change', handleAuthChange);
      window.removeEventListener('storage', handleAuthChange);
    };
  }, []);

  const rawRole = typeof currentUser?.role === 'string' ? currentUser.role : currentUser?.role?.name || '';
  const role = rawRole.replace(/^ROLE_/, '').toUpperCase();

  const handleLogout = () => {
    authService.logout();
    navigate('/login');
  };

  return (
    <header className="sticky top-0 z-50 bg-white/95 backdrop-blur-md border-b border-slate-200 shadow-sm">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
        {/* Logo */}
        <Link to="/" className="flex items-center gap-2 group">
          <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-blue-600 to-indigo-600 flex items-center justify-center text-white font-bold text-xl shadow-md group-hover:scale-105 transition-transform">
            T
          </div>
          <div>
            <span className="text-xl font-bold bg-gradient-to-r from-blue-600 to-indigo-700 bg-clip-text text-transparent">TroFinder</span>
            <span className="hidden sm:inline-block ml-1 text-xs text-blue-500 font-semibold px-1.5 py-0.5 bg-blue-50 rounded">AI</span>
          </div>
        </Link>

        {/* Navigation Links */}
        <nav className="hidden md:flex items-center gap-6 text-sm font-medium text-slate-700">
          <Link to="/rooms" className="hover:text-blue-600 flex items-center gap-1.5 transition-colors">
            <Home className="w-4 h-4 text-slate-400" />
            Tìm phòng
          </Link>
          <Link to="/map" className="hover:text-blue-600 flex items-center gap-1.5 transition-colors">
            <MapPin className="w-4 h-4 text-slate-400" />
            Bản đồ
          </Link>
          <Link to="/rooms?ai=true" className="text-indigo-600 hover:text-indigo-700 flex items-center gap-1.5 font-semibold bg-indigo-50/80 px-2.5 py-1 rounded-full transition-colors">
            <Sparkles className="w-4 h-4 text-indigo-500" />
            Tìm bằng AI
          </Link>
          {currentUser && (
            <>
              <Link to="/chat" className="hover:text-blue-600 flex items-center gap-1.5 transition-colors">
                <MessageSquare className="w-4 h-4 text-slate-400" />
                Tin nhắn
              </Link>
              <Link to="/bookings" className="hover:text-blue-600 flex items-center gap-1.5 transition-colors">
                <Calendar className="w-4 h-4 text-slate-400" />
                Lịch hẹn
              </Link>
            </>
          )}
        </nav>

        {/* Action & User Profile */}
        <div className="flex items-center gap-3">
          {currentUser ? (
            <div className="flex items-center gap-2">
              {(role === 'LANDLORD' || role === 'ADMIN') && (
                <Link
                  to="/landlord/rooms/new"
                  className="hidden sm:inline-flex items-center gap-1.5 text-xs font-semibold px-3 py-2 rounded-lg bg-blue-600 text-white hover:bg-blue-700 shadow-sm transition-colors"
                >
                  <PlusCircle className="w-4 h-4" />
                  Đăng tin
                </Link>
              )}

              {role === 'LANDLORD' && (
                <Link
                  to="/landlord/dashboard"
                  className="text-xs font-semibold px-3 py-2 rounded-lg border border-slate-300 text-slate-700 hover:bg-slate-50 transition-colors"
                >
                  Quản lý phòng
                </Link>
              )}

              {role === 'ADMIN' && (
                <Link
                  to="/admin/dashboard"
                  className="text-xs font-semibold px-3 py-2 rounded-lg bg-purple-100 text-purple-700 hover:bg-purple-200 transition-colors flex items-center gap-1"
                >
                  <ShieldAlert className="w-3.5 h-3.5" />
                  Admin
                </Link>
              )}

              <div className="flex items-center gap-2 pl-2 border-l border-slate-200">
                <div className="w-8 h-8 rounded-full bg-slate-200 flex items-center justify-center font-bold text-xs text-slate-700">
                  {currentUser.fullName ? currentUser.fullName.charAt(0) : 'U'}
                </div>
                <span className="text-xs font-medium text-slate-800 hidden sm:inline-block max-w-[120px] truncate">
                  {currentUser.fullName}
                </span>
                <button
                  onClick={handleLogout}
                  title="Đăng xuất"
                  className="p-1.5 rounded-lg text-slate-400 hover:text-red-600 hover:bg-red-50 transition-colors"
                >
                  <LogOut className="w-4 h-4" />
                </button>
              </div>
            </div>
          ) : (
            <div className="flex items-center gap-2">
              <Link
                to="/login"
                className="text-sm font-semibold px-4 py-2 text-slate-700 hover:text-blue-600 transition-colors"
              >
                Đăng nhập
              </Link>
              <Link
                to="/register"
                className="text-sm font-semibold px-4 py-2 rounded-xl bg-blue-600 text-white hover:bg-blue-700 shadow-sm transition-colors"
              >
                Đăng ký
              </Link>
            </div>
          )}
        </div>
      </div>
    </header>
  );
};
