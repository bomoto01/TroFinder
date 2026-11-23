import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Navbar } from './components/layout/Navbar';
import { Footer } from './components/layout/Footer';
import { HomePage } from './pages/public/HomePage';
import { RoomListPage } from './pages/public/RoomListPage';
import { RoomDetailPage } from './pages/public/RoomDetailPage';
import { MapViewPage } from './pages/public/MapViewPage';
import { LoginPage } from './pages/auth/LoginPage';
import { RegisterPage } from './pages/auth/RegisterPage';
import { ChatPage } from './pages/tenant/ChatPage';
import { MyBookingsPage } from './pages/tenant/MyBookingsPage';
import { LandlordDashboardPage } from './pages/landlord/LandlordDashboardPage';
import { RoomCreatePage } from './pages/landlord/RoomCreatePage';
import { AdminDashboardPage } from './pages/admin/AdminDashboardPage';
import { authService } from './services/auth.service';

const ProtectedRoute: React.FC<{ children: React.ReactNode; requiredRole?: string }> = ({
  children,
  requiredRole,
}) => {
  const user = authService.getCurrentUser();
  if (!user) {
    return <Navigate to="/login" replace />;
  }
  if (requiredRole) {
    const rawRole = typeof user.role === 'string' ? user.role : (user.role as any)?.name || '';
    const userRole = rawRole.replace(/^ROLE_/, '').toUpperCase();
    if (userRole !== requiredRole.toUpperCase() && !userRole.includes(requiredRole.toUpperCase())) {
      return <Navigate to="/" replace />;
    }
  }
  return <>{children}</>;
};

export const App: React.FC = () => {
  return (
    <BrowserRouter>
      <div className="min-h-screen flex flex-col bg-slate-50 text-slate-900">
        <Navbar />
        <main className="flex-1">
          <Routes>
            {/* Public Routes */}
            <Route path="/" element={<HomePage />} />
            <Route path="/rooms" element={<RoomListPage />} />
            <Route path="/rooms/:id" element={<RoomDetailPage />} />
            <Route path="/map" element={<MapViewPage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />

            {/* Tenant Protected Routes */}
            <Route
              path="/chat"
              element={
                <ProtectedRoute>
                  <ChatPage />
                </ProtectedRoute>
              }
            />
            <Route
              path="/bookings"
              element={
                <ProtectedRoute>
                  <MyBookingsPage />
                </ProtectedRoute>
              }
            />

            {/* Landlord Protected Routes */}
            <Route
              path="/landlord/dashboard"
              element={
                <ProtectedRoute requiredRole="LANDLORD">
                  <LandlordDashboardPage />
                </ProtectedRoute>
              }
            />
            <Route
              path="/landlord/rooms/new"
              element={
                <ProtectedRoute requiredRole="LANDLORD">
                  <RoomCreatePage />
                </ProtectedRoute>
              }
            />

            {/* Admin Protected Routes */}
            <Route
              path="/admin/dashboard"
              element={
                <ProtectedRoute requiredRole="ADMIN">
                  <AdminDashboardPage />
                </ProtectedRoute>
              }
            />

            {/* Fallback */}
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </main>
        <Footer />
      </div>
    </BrowserRouter>
  );
};

export default App;
