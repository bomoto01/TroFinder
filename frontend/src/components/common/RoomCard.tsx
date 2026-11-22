import React from 'react';
import { Link } from 'react-router-dom';
import { RoomSummary } from '../../types';
import { MapPin, Eye, Heart, CheckCircle2, Sparkles } from 'lucide-react';

interface RoomCardProps {
  room: RoomSummary;
}

export const RoomCard: React.FC<RoomCardProps> = ({ room }) => {
  const formatPrice = (price: number) => {
    if (price >= 1000000) {
      return `${(price / 1000000).toLocaleString('vi-VN')} triệu/tháng`;
    }
    return `${price.toLocaleString('vi-VN')} đ/tháng`;
  };

  const getRoomTypeLabel = (type: string) => {
    switch (type) {
      case 'APARTMENT': return 'Chung cư';
      case 'MINI_APARTMENT': return 'Chung cư mini';
      case 'WHOLE_HOUSE': return 'Nhà nguyên căn';
      case 'HOMESTAY': return 'Homestay';
      case 'BOARDING_HOUSE':
      default:
        return 'Phòng trọ';
    }
  };

  return (
    <div className="group bg-white rounded-2xl overflow-hidden border border-slate-200/80 shadow-sm hover:shadow-md hover:border-blue-300 transition-all duration-200 flex flex-col h-full">
      {/* Thumbnail */}
      <div className="relative aspect-[16/10] bg-slate-100 overflow-hidden">
        {room.thumbnail ? (
          <img
            src={room.thumbnail.startsWith('http') ? room.thumbnail : `http://localhost:8080${room.thumbnail}`}
            alt={room.title}
            className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
            onError={(e) => {
              (e.target as HTMLImageElement).src = 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=600&q=80';
            }}
          />
        ) : (
          <div className="w-full h-full flex items-center justify-center text-slate-400 bg-slate-100">
            <span className="text-xs">Chưa có ảnh</span>
          </div>
        )}

        {/* Badges */}
        <div className="absolute top-2.5 left-2.5 flex flex-wrap gap-1.5">
          <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-slate-900/70 text-white backdrop-blur-sm">
            {getRoomTypeLabel(room.roomType)}
          </span>
          {room.verified && (
            <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-emerald-600/90 text-white backdrop-blur-sm flex items-center gap-1">
              <CheckCircle2 className="w-3 h-3" />
              Đã xác thực
            </span>
          )}
          {room.featured && (
            <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-amber-500/90 text-white backdrop-blur-sm flex items-center gap-1">
              <Sparkles className="w-3 h-3" />
              Nổi bật
            </span>
          )}
        </div>
      </div>

      {/* Body */}
      <div className="p-4 flex-1 flex flex-col justify-between">
        <div>
          <div className="flex items-baseline justify-between mb-1.5">
            <span className="text-lg font-bold text-red-600">
              {formatPrice(room.price)}
            </span>
            <span className="text-xs font-medium text-slate-500">
              {room.area} m²
            </span>
          </div>

          <Link to={`/rooms/${room.id}`}>
            <h3 className="font-semibold text-slate-900 text-sm line-clamp-2 hover:text-blue-600 transition-colors mb-2">
              {room.title}
            </h3>
          </Link>

          <p className="text-xs text-slate-500 flex items-center gap-1 line-clamp-1 mb-3">
            <MapPin className="w-3.5 h-3.5 shrink-0 text-slate-400" />
            {room.address}
          </p>
        </div>

        {/* Footer Metrics */}
        <div className="pt-2.5 border-t border-slate-100 flex items-center justify-between text-xs text-slate-400">
          <div className="flex items-center gap-1">
            <div className="w-5 h-5 rounded-full bg-slate-200 flex items-center justify-center font-bold text-[10px] text-slate-700">
              {room.ownerName ? room.ownerName.charAt(0) : 'U'}
            </div>
            <span className="text-slate-600 truncate max-w-[100px]">{room.ownerName}</span>
          </div>

          <div className="flex items-center gap-3">
            <span className="flex items-center gap-1" title="Lượt xem">
              <Eye className="w-3.5 h-3.5" />
              {room.viewCount || 0}
            </span>
            <span className="flex items-center gap-1" title="Yêu thích">
              <Heart className="w-3.5 h-3.5" />
              {room.favoriteCount || 0}
            </span>
          </div>
        </div>
      </div>
    </div>
  );
};
