import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { roomService, RoomSearchParams } from '../../services/room.service';
import { aiService } from '../../services/ai.service';
import { RoomSummary } from '../../types';
import { RoomCard } from '../../components/common/RoomCard';
import { Search, Filter, Sparkles, Loader2, RefreshCw } from 'lucide-react';

export const RoomListPage: React.FC = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const initialKeyword = searchParams.get('keyword') || '';
  const initialAiPrompt = searchParams.get('aiPrompt') || '';

  const [rooms, setRooms] = useState<RoomSummary[]>([]);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);
  const [isLoading, setIsLoading] = useState(true);

  // Filters state
  const [keyword, setKeyword] = useState(initialKeyword);
  const [aiPrompt, setAiPrompt] = useState(initialAiPrompt);
  const [isAiMode, setIsAiMode] = useState(!!initialAiPrompt || searchParams.get('ai') === 'true');
  const [roomType, setRoomType] = useState(searchParams.get('roomType') || '');
  const [minPrice, setMinPrice] = useState<number | undefined>();
  const [maxPrice, setMaxPrice] = useState<number | undefined>();
  const [sortBy, setSortBy] = useState(searchParams.get('sortBy') || 'newest');

  // React to URL changes (e.g. clicking "Tìm phòng" vs "Tìm bằng AI" in Navbar)
  useEffect(() => {
    const aiParam = searchParams.get('ai') === 'true';
    const promptParam = searchParams.get('aiPrompt') || '';
    const kwParam = searchParams.get('keyword') || '';

    const newAiMode = aiParam || !!promptParam;
    setIsAiMode(newAiMode);
    setAiPrompt(promptParam);
    setKeyword(kwParam);
    setCurrentPage(0);
  }, [searchParams]);

  const fetchRooms = async () => {
    setIsLoading(true);
    try {
      if (isAiMode && aiPrompt.trim()) {
        const res = await aiService.searchByPrompt(aiPrompt, currentPage, 12);
        setRooms(res?.content || []);
        setTotalPages(res?.totalPages || 1);
        setTotalElements(res?.totalElements || 0);
      } else {
        const params: RoomSearchParams = {
          keyword: keyword.trim() || undefined,
          roomType: roomType || undefined,
          minPrice,
          maxPrice,
          sortBy,
          page: currentPage,
          size: 12,
        };
        const res = await roomService.getSummaryRooms(params);
        setRooms(res?.content || []);
        setTotalPages(res?.totalPages || 1);
        setTotalElements(res?.totalElements || 0);
      }
    } catch (err) {
      console.error('Failed to search rooms:', err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchRooms();
  }, [currentPage, sortBy, isAiMode, roomType, maxPrice, minPrice]);

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setCurrentPage(0);
    fetchRooms();
  };

  const handleToggleAiMode = () => {
    const nextMode = !isAiMode;
    setIsAiMode(nextMode);
    setCurrentPage(0);
    if (nextMode) {
      setSearchParams({ ai: 'true' });
    } else {
      setSearchParams({});
    }
  };

  const handleResetFilters = () => {
    setKeyword('');
    setAiPrompt('');
    setRoomType('');
    setMinPrice(undefined);
    setMaxPrice(undefined);
    setSortBy('newest');
    setIsAiMode(false);
    setSearchParams({});
    setCurrentPage(0);
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
      {/* Search Header */}
      <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-sm space-y-4">
        <div className="flex items-center justify-between">
          <h1 className="text-xl sm:text-2xl font-bold text-slate-900">
            {isAiMode ? 'Tìm kiếm thông minh bằng AI' : 'Danh sách phòng cho thuê'}
          </h1>
          <button
            onClick={handleToggleAiMode}
            className={`text-xs font-semibold px-3 py-1.5 rounded-full flex items-center gap-1.5 transition-colors ${
              isAiMode ? 'bg-indigo-600 text-white' : 'bg-indigo-50 text-indigo-700 hover:bg-indigo-100'
            }`}
          >
            <Sparkles className="w-3.5 h-3.5" />
            {isAiMode ? 'Đang dùng chế độ AI' : 'Bật tìm kiếm AI'}
          </button>
        </div>

        <form onSubmit={handleSearchSubmit} className="flex gap-2">
          {isAiMode ? (
            <div className="relative flex-1">
              <input
                type="text"
                value={aiPrompt}
                onChange={(e) => setAiPrompt(e.target.value)}
                placeholder="Nhập mô tả tự nhiên (VD: Cần tìm phòng trọ Cầu Giấy dưới 4 triệu có điều hòa gác lửng)..."
                className="w-full pl-10 pr-4 py-2.5 text-sm border border-indigo-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-indigo-50/20"
              />
              <Sparkles className="w-4 h-4 text-indigo-500 absolute left-3.5 top-3" />
            </div>
          ) : (
            <div className="relative flex-1">
              <input
                type="text"
                value={keyword}
                onChange={(e) => setKeyword(e.target.value)}
                placeholder="Tìm theo tiêu đề, địa chỉ, quận huyện..."
                className="w-full pl-10 pr-4 py-2.5 text-sm border border-slate-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
              <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-3" />
            </div>
          )}
          <button
            type="submit"
            className="px-5 py-2.5 bg-blue-600 hover:bg-blue-700 text-white text-sm font-semibold rounded-xl shadow-sm transition-colors shrink-0"
          >
            Tìm kiếm
          </button>
        </form>

        {/* Filter controls */}
        {!isAiMode && (
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 pt-2 text-xs">
            <select
              value={roomType}
              onChange={(e) => { setRoomType(e.target.value); setCurrentPage(0); }}
              className="px-3 py-2 border border-slate-200 rounded-lg text-slate-700 bg-white"
            >
              <option value="">Tất cả loại phòng</option>
              <option value="BOARDING_HOUSE">Phòng trọ</option>
              <option value="MINI_APARTMENT">Chung cư mini</option>
              <option value="APARTMENT">Chung cư</option>
              <option value="WHOLE_HOUSE">Nhà nguyên căn</option>
              <option value="HOMESTAY">Homestay</option>
            </select>

            <select
              value={maxPrice || ''}
              onChange={(e) => { setMaxPrice(e.target.value ? Number(e.target.value) : undefined); setCurrentPage(0); }}
              className="px-3 py-2 border border-slate-200 rounded-lg text-slate-700 bg-white"
            >
              <option value="">Khoảng giá</option>
              <option value="2000000">Dưới 2 triệu</option>
              <option value="3500000">Dưới 3.5 triệu</option>
              <option value="5000000">Dưới 5 triệu</option>
              <option value="8000000">Dưới 8 triệu</option>
            </select>

            <select
              value={sortBy}
              onChange={(e) => { setSortBy(e.target.value); setCurrentPage(0); }}
              className="px-3 py-2 border border-slate-200 rounded-lg text-slate-700 bg-white"
            >
              <option value="newest">Mới nhất</option>
              <option value="price_asc">Giá tăng dần</option>
              <option value="price_desc">Giá giảm dần</option>
              <option value="views">Lượt xem nhiều</option>
              <option value="favorites">Yêu thích nhiều</option>
            </select>

            <button
              onClick={handleResetFilters}
              className="px-3 py-2 border border-slate-200 text-slate-600 hover:bg-slate-50 rounded-lg font-medium flex items-center justify-center gap-1"
            >
              <RefreshCw className="w-3.5 h-3.5" />
              Đặt lại bộ lọc
            </button>
          </div>
        )}
      </div>

      {/* Results Header */}
      <div className="flex items-center justify-between text-sm text-slate-600">
        <span>Tìm thấy <strong className="text-slate-900">{totalElements}</strong> kết quả</span>
      </div>

      {/* Rooms Grid */}
      {isLoading ? (
        <div className="flex flex-col items-center justify-center py-20 text-slate-400">
          <Loader2 className="w-8 h-8 animate-spin text-blue-600 mb-2" />
          <span className="text-sm">Đang tải danh sách phòng...</span>
        </div>
      ) : rooms.length === 0 ? (
        <div className="text-center py-16 bg-white rounded-2xl border border-slate-200 p-8">
          <p className="text-slate-600 font-medium mb-2">Không tìm thấy phòng trọ phù hợp với tiêu chí.</p>
          <p className="text-xs text-slate-400 mb-4">Bạn có thể thử nới rộng khoảng giá hoặc sử dụng tính năng tìm kiếm bằng AI.</p>
          <button
            onClick={handleResetFilters}
            className="px-4 py-2 bg-blue-600 text-white text-xs font-semibold rounded-lg hover:bg-blue-700"
          >
            Xem tất cả phòng có sẵn
          </button>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          {rooms.map((room) => (
            <RoomCard key={room.id} room={room} />
          ))}
        </div>
      )}

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="flex justify-center items-center gap-2 pt-6">
          <button
            disabled={currentPage === 0}
            onClick={() => setCurrentPage((prev) => Math.max(0, prev - 1))}
            className="px-4 py-2 text-xs font-medium rounded-lg border border-slate-300 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-slate-50"
          >
            Trang trước
          </button>
          <span className="text-xs text-slate-600 font-medium">
            Trang {currentPage + 1} / {totalPages}
          </span>
          <button
            disabled={currentPage >= totalPages - 1}
            onClick={() => setCurrentPage((prev) => prev + 1)}
            className="px-4 py-2 text-xs font-medium rounded-lg border border-slate-300 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-slate-50"
          >
            Trang sau
          </button>
        </div>
      )}
    </div>
  );
};
