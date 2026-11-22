import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { roomService } from '../../services/room.service';
import { aiService } from '../../services/ai.service';
import { RoomSummary } from '../../types';
import { RoomCard } from '../../components/common/RoomCard';
import { Search, Sparkles, MapPin, Building, ShieldCheck, ArrowRight, Loader2 } from 'lucide-react';

export const HomePage: React.FC = () => {
  const navigate = useNavigate();
  const [keyword, setKeyword] = useState('');
  const [aiPrompt, setAiPrompt] = useState('');
  const [featuredRooms, setFeaturedRooms] = useState<RoomSummary[]>([]);
  const [recommendedRooms, setRecommendedRooms] = useState<RoomSummary[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [featuredRes, recRes] = await Promise.all([
          roomService.getSummaryRooms({ page: 0, size: 8, sortBy: 'newest' }),
          aiService.getRecommendations(4),
        ]);
        setFeaturedRooms(featuredRes?.content || []);
        setRecommendedRooms(recRes || []);
      } catch (err) {
        console.error('Failed to load home page data:', err);
      } finally {
        setIsLoading(false);
      }
    };
    fetchData();
  }, []);

  const handleStandardSearch = (e: React.FormEvent) => {
    e.preventDefault();
    navigate(`/rooms?keyword=${encodeURIComponent(keyword)}`);
  };

  const handleAiSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (!aiPrompt.trim()) return;
    navigate(`/rooms?aiPrompt=${encodeURIComponent(aiPrompt)}`);
  };

  return (
    <div className="space-y-12 pb-16">
      {/* Hero Section */}
      <section className="relative bg-gradient-to-br from-blue-700 via-indigo-800 to-slate-900 text-white py-16 sm:py-24 px-4 overflow-hidden">
        <div className="absolute inset-0 opacity-10 bg-[radial-gradient(#fff_1px,transparent_1px)] [background-size:16px_16px]" />
        
        <div className="relative max-w-4xl mx-auto text-center space-y-6">
          <div className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-white/10 backdrop-blur-md text-blue-200 text-xs font-semibold border border-white/20">
            <Sparkles className="w-3.5 h-3.5 text-yellow-300" />
            Nền tảng tìm phòng thông minh tích hợp AI
          </div>

          <h1 className="text-3xl sm:text-5xl font-extrabold tracking-tight leading-tight">
            Tìm phòng trọ lý tưởng nhanh chóng, <br className="hidden sm:inline" />
            minh bạch và chính xác với <span className="text-blue-400">TroFinder</span>
          </h1>

          <p className="text-sm sm:text-base text-slate-300 max-w-2xl mx-auto">
            Hàng ngàn phòng trọ, chung cư mini, nhà nguyên căn được xác thực địa chỉ, giá điện nước minh bạch và hỗ trợ tìm kiếm bằng câu lệnh AI tự nhiên.
          </p>

          {/* AI Search Box */}
          <div className="max-w-2xl mx-auto pt-4">
            <form
              onSubmit={handleAiSearch}
              className="relative flex items-center bg-white rounded-2xl shadow-xl p-1.5 border-2 border-indigo-400/40 focus-within:border-indigo-500 transition-all"
            >
              <div className="pl-3 text-indigo-500">
                <Sparkles className="w-5 h-5 animate-pulse" />
              </div>
              <input
                type="text"
                value={aiPrompt}
                onChange={(e) => setAiPrompt(e.target.value)}
                placeholder="Nhập yêu cầu AI (VD: Tìm chung cư mini Cầu Giấy dưới 4tr có điều hòa gác lửng)..."
                className="w-full px-3 py-3 text-sm text-slate-800 placeholder-slate-400 focus:outline-none rounded-xl"
              />
              <button
                type="submit"
                className="bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 text-white text-sm font-semibold px-5 py-3 rounded-xl flex items-center gap-1.5 shadow-md transition-all shrink-0"
              >
                <Sparkles className="w-4 h-4" />
                <span className="hidden sm:inline">Tìm bằng AI</span>
              </button>
            </form>

            <div className="mt-3 flex flex-wrap items-center justify-center gap-2 text-xs text-slate-300">
              <span className="text-slate-400">Gợi ý thử:</span>
              <button
                onClick={() => setAiPrompt('Phòng trọ Cầu Giấy dưới 3.5 triệu có điều hòa')}
                className="hover:text-white underline decoration-dotted"
              >
                Cầu Giấy dưới 3.5tr
              </button>
              <span>•</span>
              <button
                onClick={() => setAiPrompt('Chung cư mini Đống Đa có gác lửng ban công')}
                className="hover:text-white underline decoration-dotted"
              >
                Đống Đa ban công
              </button>
              <span>•</span>
              <button
                onClick={() => setAiPrompt('Nhà nguyên căn Bình Thạnh dưới 8 triệu')}
                className="hover:text-white underline decoration-dotted"
              >
                Bình Thạnh dưới 8tr
              </button>
            </div>
          </div>
        </div>
      </section>

      {/* Feature Highlights */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="p-6 rounded-2xl bg-white border border-slate-200/80 shadow-sm flex items-start gap-4">
            <div className="p-3 rounded-xl bg-blue-50 text-blue-600 shrink-0">
              <ShieldCheck className="w-6 h-6" />
            </div>
            <div>
              <h3 className="font-semibold text-slate-900 mb-1">Tin đăng xác thực</h3>
              <p className="text-xs text-slate-500 leading-relaxed">
                Được kiểm duyệt thực tế về hình ảnh, giá điện nước công khai, không thu phí trung gian.
              </p>
            </div>
          </div>

          <div className="p-6 rounded-2xl bg-white border border-slate-200/80 shadow-sm flex items-start gap-4">
            <div className="p-3 rounded-xl bg-indigo-50 text-indigo-600 shrink-0">
              <Sparkles className="w-6 h-6" />
            </div>
            <div>
              <h3 className="font-semibold text-slate-900 mb-1">Trợ lý ảo AI thông minh</h3>
              <p className="text-xs text-slate-500 leading-relaxed">
                Tìm phòng theo ngôn ngữ tự nhiên, gợi ý phòng dựa trên sở thích và giải đáp thắc mắc 24/7.
              </p>
            </div>
          </div>

          <div className="p-6 rounded-2xl bg-white border border-slate-200/80 shadow-sm flex items-start gap-4">
            <div className="p-3 rounded-xl bg-emerald-50 text-emerald-600 shrink-0">
              <MapPin className="w-6 h-6" />
            </div>
            <div>
              <h3 className="font-semibold text-slate-900 mb-1">Bản đồ trực quan</h3>
              <p className="text-xs text-slate-500 leading-relaxed">
                Tìm phòng theo bán kính xung quanh trường học, nơi làm việc và xem tiện ích lân cận.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* AI Recommendations Section */}
      {recommendedRooms.length > 0 && (
        <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between mb-6">
            <div>
              <div className="flex items-center gap-2">
                <span className="p-1 rounded-md bg-indigo-100 text-indigo-600">
                  <Sparkles className="w-4 h-4" />
                </span>
                <h2 className="text-xl sm:text-2xl font-bold text-slate-900">Gợi ý AI dành cho bạn</h2>
              </div>
              <p className="text-xs sm:text-sm text-slate-500 mt-1">Phòng trọ được đánh giá cao và phù hợp nhất với nhu cầu tìm kiếm</p>
            </div>
            <button
              onClick={() => navigate('/rooms?sortBy=views')}
              className="text-xs sm:text-sm font-semibold text-blue-600 hover:text-blue-700 flex items-center gap-1"
            >
              Xem thêm <ArrowRight className="w-4 h-4" />
            </button>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {recommendedRooms.map((room) => (
              <RoomCard key={room.id} room={room} />
            ))}
          </div>
        </section>
      )}

      {/* Latest Rooms Section */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h2 className="text-xl sm:text-2xl font-bold text-slate-900">Phòng mới đăng tải</h2>
            <p className="text-xs sm:text-sm text-slate-500 mt-1">Cập nhật liên tục từ các chủ trọ uy tín</p>
          </div>
          <button
            onClick={() => navigate('/rooms')}
            className="text-xs sm:text-sm font-semibold text-blue-600 hover:text-blue-700 flex items-center gap-1"
          >
            Tất cả phòng <ArrowRight className="w-4 h-4" />
          </button>
        </div>

        {isLoading ? (
          <div className="flex items-center justify-center py-16 text-slate-400">
            <Loader2 className="w-8 h-8 animate-spin text-blue-600" />
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {featuredRooms.map((room) => (
              <RoomCard key={room.id} room={room} />
            ))}
          </div>
        )}
      </section>
    </div>
  );
};
