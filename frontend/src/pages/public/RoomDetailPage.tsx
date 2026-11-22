import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { roomService } from '../../services/room.service';
import { reviewService } from '../../services/review.service';
import { chatService } from '../../services/chat.service';
import { appointmentService } from '../../services/appointment.service';
import { aiService } from '../../services/ai.service';
import { authService } from '../../services/auth.service';
import { RoomDetail, Review } from '../../types';
import {
  MapPin, Phone, MessageSquare, Calendar, CheckCircle2,
  Sparkles, Star, Send, Loader2, Zap, Droplet, ShieldAlert
} from 'lucide-react';

export const RoomDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const currentUser = authService.getCurrentUser();

  const [room, setRoom] = useState<RoomDetail | null>(null);
  const [reviews, setReviews] = useState<Review[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  // Review form
  const [rating, setRating] = useState(5);
  const [comment, setComment] = useState('');
  const [isSubmittingReview, setIsSubmittingReview] = useState(false);

  // Booking modal
  const [showBookingModal, setShowBookingModal] = useState(false);
  const [bookingDate, setBookingDate] = useState('');
  const [bookingTime, setBookingTime] = useState('');
  const [bookingNote, setBookingNote] = useState('');
  const [isBooking, setIsBooking] = useState(false);

  // AI assistant question
  const [aiQuestion, setAiQuestion] = useState('');
  const [aiReply, setAiReply] = useState('');
  const [isAskingAi, setIsAskingAi] = useState(false);

  useEffect(() => {
    if (!id) return;
    const loadData = async () => {
      try {
        const [roomData, reviewData] = await Promise.all([
          roomService.getRoomById(Number(id)),
          reviewService.getReviewsByRoom(Number(id), 0, 10),
        ]);
        setRoom(roomData);
        setReviews(reviewData?.content || []);
      } catch (err) {
        console.error('Failed to load room details:', err);
      } finally {
        setIsLoading(false);
      }
    };
    loadData();
  }, [id]);

  const handleStartChat = async () => {
    if (!currentUser) {
      navigate('/login');
      return;
    }
    if (!room) return;
    try {
      const conv = await chatService.getOrCreateConversation(room.id);
      navigate(`/chat?conversationId=${conv.id}`);
    } catch (err: any) {
      alert(err.response?.data?.message || 'Không thể tạo cuộc trò chuyện.');
    }
  };

  const handleCreateAppointment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!currentUser) {
      navigate('/login');
      return;
    }
    if (!room || !bookingDate || !bookingTime) return;
    setIsBooking(true);
    try {
      await appointmentService.createAppointment({
        roomId: room.id,
        appointmentDate: bookingDate,
        appointmentTime: bookingTime,
        note: bookingNote,
      });
      alert('Đặt lịch hẹn xem phòng thành công! Chủ trọ sẽ liên hệ xác nhận.');
      setShowBookingModal(false);
    } catch (err: any) {
      alert(err.response?.data?.message || 'Đặt lịch hẹn thất bại.');
    } finally {
      setIsBooking(false);
    }
  };

  const handleSubmitReview = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!currentUser) {
      navigate('/login');
      return;
    }
    if (!room || !comment.trim()) return;
    setIsSubmittingReview(true);
    try {
      const newReview = await reviewService.createReview(room.id, rating, comment);
      setReviews([newReview, ...reviews]);
      setComment('');
      alert('Cảm ơn bạn đã gửi đánh giá!');
    } catch (err: any) {
      alert(err.response?.data?.message || 'Không thể gửi đánh giá.');
    } finally {
      setIsSubmittingReview(false);
    }
  };

  const handleAskAiAboutRoom = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!aiQuestion.trim() || !room) return;
    setIsAskingAi(true);
    try {
      const res = await aiService.chatWithAssistant(aiQuestion, room.id);
      setAiReply(res.reply);
    } catch {
      setAiReply('Hệ thống AI đang bận. Vui lòng thử lại sau.');
    } finally {
      setIsAskingAi(false);
    }
  };

  if (isLoading) {
    return (
      <div className="flex justify-center items-center py-24">
        <Loader2 className="w-8 h-8 animate-spin text-blue-600" />
      </div>
    );
  }

  if (!room) {
    return <div className="text-center py-20 text-slate-500">Không tìm thấy thông tin phòng.</div>;
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      {/* Title & Location Header */}
      <div>
        <div className="flex flex-wrap items-center gap-2 mb-2">
          {room.verified && (
            <span className="text-xs font-semibold px-2.5 py-0.5 rounded-full bg-emerald-50 text-emerald-700 flex items-center gap-1 border border-emerald-200">
              <CheckCircle2 className="w-3.5 h-3.5" /> Đã xác thực
            </span>
          )}
          {room.featured && (
            <span className="text-xs font-semibold px-2.5 py-0.5 rounded-full bg-amber-50 text-amber-700 flex items-center gap-1 border border-amber-200">
              <Sparkles className="w-3.5 h-3.5" /> Nổi bật
            </span>
          )}
        </div>
        <h1 className="text-2xl sm:text-3xl font-bold text-slate-900 mb-2">{room.title}</h1>
        <p className="text-sm text-slate-500 flex items-center gap-1.5">
          <MapPin className="w-4 h-4 text-slate-400" />
          {room.address}
        </p>
      </div>

      {/* Image Gallery */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 rounded-2xl overflow-hidden max-h-[460px]">
        <div className="sm:col-span-2 aspect-[16/10] bg-slate-100 overflow-hidden">
          <img
            src={room.imageUrls?.[0] || room.thumbnail || 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=1200&q=80'}
            alt={room.title}
            className="w-full h-full object-cover"
          />
        </div>
        <div className="hidden sm:grid grid-rows-2 gap-3">
          <div className="bg-slate-100 overflow-hidden">
            <img
              src={room.imageUrls?.[1] || 'https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?auto=format&fit=crop&w=600&q=80'}
              alt="Gallery 2"
              className="w-full h-full object-cover"
            />
          </div>
          <div className="bg-slate-100 overflow-hidden">
            <img
              src={room.imageUrls?.[2] || 'https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?auto=format&fit=crop&w=600&q=80'}
              alt="Gallery 3"
              className="w-full h-full object-cover"
            />
          </div>
        </div>
      </div>

      {/* Main Content & Sidebar */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Left Column: Details */}
        <div className="lg:col-span-2 space-y-8">
          {/* Price & Specs Box */}
          <div className="p-6 rounded-2xl bg-white border border-slate-200/80 shadow-sm grid grid-cols-2 sm:grid-cols-4 gap-4 text-center">
            <div>
              <span className="text-xs text-slate-400 block mb-1">Giá thuê</span>
              <span className="text-lg font-bold text-red-600">
                {(room.price / 1000000).toLocaleString('vi-VN')} tr/tháng
              </span>
            </div>
            <div>
              <span className="text-xs text-slate-400 block mb-1">Diện tích</span>
              <span className="text-lg font-bold text-slate-800">{room.area} m²</span>
            </div>
            <div>
              <span className="text-xs text-slate-400 block mb-1">Tiền cọc</span>
              <span className="text-lg font-bold text-slate-800">
                {room.deposit ? `${(room.deposit / 1000000).toLocaleString('vi-VN')} tr` : 'Không'}
              </span>
            </div>
            <div>
              <span className="text-xs text-slate-400 block mb-1">Số phòng trống</span>
              <span className="text-lg font-bold text-slate-800">{room.availableQuantity || 1}</span>
            </div>
          </div>

          {/* Utility Rates */}
          <div className="p-6 rounded-2xl bg-white border border-slate-200/80 shadow-sm space-y-4">
            <h3 className="font-bold text-slate-900 text-base">Chi phí dịch vụ</h3>
            <div className="grid grid-cols-2 gap-4 text-sm">
              <div className="flex items-center gap-2">
                <Zap className="w-5 h-5 text-amber-500" />
                <div>
                  <span className="text-slate-500 text-xs block">Giá điện</span>
                  <span className="font-semibold text-slate-800">
                    {room.electricPrice ? `${room.electricPrice.toLocaleString('vi-VN')} đ/kWh` : 'Theo giá nhà nước'}
                  </span>
                </div>
              </div>
              <div className="flex items-center gap-2">
                <Droplet className="w-5 h-5 text-blue-500" />
                <div>
                  <span className="text-slate-500 text-xs block">Giá nước</span>
                  <span className="font-semibold text-slate-800">
                    {room.waterPrice ? `${room.waterPrice.toLocaleString('vi-VN')} đ/m³` : 'Thoả thuận'}
                  </span>
                </div>
              </div>
            </div>
          </div>

          {/* Description */}
          <div className="p-6 rounded-2xl bg-white border border-slate-200/80 shadow-sm space-y-3">
            <h3 className="font-bold text-slate-900 text-base">Mô tả chi tiết</h3>
            <p className="text-sm text-slate-700 whitespace-pre-line leading-relaxed">
              {room.description || 'Chưa có mô tả chi tiết cho phòng này.'}
            </p>
          </div>

          {/* Amenities */}
          <div className="p-6 rounded-2xl bg-white border border-slate-200/80 shadow-sm space-y-3">
            <h3 className="font-bold text-slate-900 text-base">Tiện ích phòng</h3>
            <div className="flex flex-wrap gap-2">
              {room.amenities && room.amenities.length > 0 ? (
                room.amenities.map((item, idx) => (
                  <span
                    key={idx}
                    className="text-xs font-medium px-3 py-1.5 rounded-xl bg-slate-100 text-slate-700"
                  >
                    {item}
                  </span>
                ))
              ) : (
                <span className="text-xs text-slate-400">Tiện ích cơ bản</span>
              )}
            </div>
          </div>

          {/* AI Grounded Assistant for this Room */}
          <div className="p-6 rounded-2xl bg-gradient-to-r from-indigo-50 to-blue-50 border border-indigo-200 shadow-sm space-y-4">
            <div className="flex items-center gap-2">
              <Sparkles className="w-5 h-5 text-indigo-600" />
              <h3 className="font-bold text-slate-900 text-base">Hỏi đáp AI về căn phòng này</h3>
            </div>
            <p className="text-xs text-slate-600">
              Trợ lý ảo TroFinder trả lời ngay dựa trên thông tin thực tế của phòng (hợp đồng, giá cọc, quy định).
            </p>

            <form onSubmit={handleAskAiAboutRoom} className="flex gap-2">
              <input
                type="text"
                value={aiQuestion}
                onChange={(e) => setAiQuestion(e.target.value)}
                placeholder="VD: Căn này tiền cọc bao nhiêu, có chỗ để xe máy không?..."
                className="flex-1 px-3 py-2 text-sm bg-white border border-indigo-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
              <button
                type="submit"
                disabled={isAskingAi}
                className="px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white text-xs font-semibold rounded-xl flex items-center gap-1 shrink-0"
              >
                {isAskingAi ? <Loader2 className="w-4 h-4 animate-spin" /> : <Send className="w-4 h-4" />}
                Hỏi AI
              </button>
            </form>

            {aiReply && (
              <div className="p-4 rounded-xl bg-white border border-indigo-100 text-sm text-slate-800 leading-relaxed shadow-xs">
                <span className="text-xs font-semibold text-indigo-600 block mb-1">Câu trả lời từ TroFinder AI:</span>
                {aiReply}
              </div>
            )}
          </div>

          {/* Reviews & Ratings */}
          <div className="p-6 rounded-2xl bg-white border border-slate-200/80 shadow-sm space-y-6">
            <h3 className="font-bold text-slate-900 text-base">Đánh giá từ người thuê ({reviews.length})</h3>

            {/* Submit form */}
            <form onSubmit={handleSubmitReview} className="space-y-3 pb-6 border-b border-slate-100">
              <div className="flex items-center gap-2">
                <span className="text-xs font-medium text-slate-600">Chấm điểm:</span>
                {[1, 2, 3, 4, 5].map((s) => (
                  <button
                    type="button"
                    key={s}
                    onClick={() => setRating(s)}
                    className="p-1 hover:scale-110 transition-transform"
                  >
                    <Star
                      className={`w-5 h-5 ${s <= rating ? 'text-amber-400 fill-amber-400' : 'text-slate-300'}`}
                    />
                  </button>
                ))}
              </div>

              <textarea
                value={comment}
                onChange={(e) => setComment(e.target.value)}
                placeholder="Chia sẻ trải nghiệm xem phòng hoặc môi trường xung quanh..."
                rows={3}
                className="w-full p-3 text-sm border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500"
              />

              <button
                type="submit"
                disabled={isSubmittingReview}
                className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white text-xs font-semibold rounded-xl flex items-center gap-1.5"
              >
                {isSubmittingReview && <Loader2 className="w-4 h-4 animate-spin" />}
                Gửi đánh giá
              </button>
            </form>

            {/* List */}
            <div className="space-y-4">
              {reviews.length === 0 ? (
                <p className="text-xs text-slate-400 text-center py-4">Chưa có đánh giá nào cho phòng này. Hãy là người đầu tiên!</p>
              ) : (
                reviews.map((rev) => (
                  <div key={rev.id} className="space-y-1">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-2">
                        <div className="w-7 h-7 rounded-full bg-slate-200 flex items-center justify-center font-bold text-xs text-slate-700">
                          {rev.userName ? rev.userName.charAt(0) : 'U'}
                        </div>
                        <span className="text-xs font-semibold text-slate-800">{rev.userName}</span>
                      </div>
                      <div className="flex text-amber-400">
                        {Array.from({ length: rev.rating }).map((_, i) => (
                          <Star key={i} className="w-3.5 h-3.5 fill-amber-400" />
                        ))}
                      </div>
                    </div>
                    <p className="text-xs text-slate-700 pl-9">{rev.comment}</p>
                  </div>
                ))
              )}
            </div>
          </div>
        </div>

        {/* Right Column: Contact & Booking */}
        <div className="space-y-6">
          <div className="p-6 rounded-2xl bg-white border border-slate-200/80 shadow-md sticky top-24 space-y-6">
            {/* Host card */}
            <div className="flex items-center gap-3 pb-4 border-b border-slate-100">
              <div className="w-12 h-12 rounded-full bg-blue-100 flex items-center justify-center font-bold text-lg text-blue-700">
                {room.ownerName ? room.ownerName.charAt(0) : 'U'}
              </div>
              <div>
                <span className="text-xs text-slate-400 block">Chủ bài đăng</span>
                <h4 className="font-bold text-slate-900 text-sm">{room.ownerName}</h4>
              </div>
            </div>

            {/* Actions */}
            <div className="space-y-3">
              {room.contactPhone && (
                <a
                  href={`tel:${room.contactPhone}`}
                  className="w-full py-3 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl font-semibold text-sm flex items-center justify-center gap-2 shadow-sm transition-colors"
                >
                  <Phone className="w-4 h-4" />
                  Gọi điện ({room.contactPhone})
                </a>
              )}

              <button
                onClick={handleStartChat}
                className="w-full py-3 bg-blue-600 hover:bg-blue-700 text-white rounded-xl font-semibold text-sm flex items-center justify-center gap-2 shadow-sm transition-colors"
              >
                <MessageSquare className="w-4 h-4" />
                Nhắn tin với chủ trọ
              </button>

              <button
                onClick={() => setShowBookingModal(true)}
                className="w-full py-3 border-2 border-blue-600 text-blue-600 hover:bg-blue-50 rounded-xl font-semibold text-sm flex items-center justify-center gap-2 transition-colors"
              >
                <Calendar className="w-4 h-4" />
                Đặt lịch xem phòng
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Booking Modal */}
      {showBookingModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/50 backdrop-blur-xs p-4">
          <div className="bg-white rounded-2xl p-6 max-w-md w-full shadow-2xl space-y-4">
            <h3 className="font-bold text-lg text-slate-900">Đặt lịch xem phòng</h3>
            <p className="text-xs text-slate-500">
              Chọn thời gian phù hợp để chủ trọ tiếp đón bạn trực tiếp.
            </p>

            <form onSubmit={handleCreateAppointment} className="space-y-3">
              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Ngày xem phòng</label>
                <input
                  type="date"
                  required
                  value={bookingDate}
                  onChange={(e) => setBookingDate(e.target.value)}
                  className="w-full p-2.5 text-sm border border-slate-300 rounded-xl"
                />
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Giờ xem phòng</label>
                <input
                  type="time"
                  required
                  value={bookingTime}
                  onChange={(e) => setBookingTime(e.target.value)}
                  className="w-full p-2.5 text-sm border border-slate-300 rounded-xl"
                />
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-700 block mb-1">Ghi chú (tuỳ chọn)</label>
                <textarea
                  value={bookingNote}
                  onChange={(e) => setBookingNote(e.target.value)}
                  rows={2}
                  placeholder="VD: Mình xem phòng vào giờ nghỉ trưa..."
                  className="w-full p-2.5 text-sm border border-slate-300 rounded-xl"
                />
              </div>

              <div className="flex gap-2 pt-2">
                <button
                  type="button"
                  onClick={() => setShowBookingModal(false)}
                  className="flex-1 py-2.5 border border-slate-300 rounded-xl text-sm font-semibold text-slate-700 hover:bg-slate-50"
                >
                  Hủy
                </button>
                <button
                  type="submit"
                  disabled={isBooking}
                  className="flex-1 py-2.5 bg-blue-600 hover:bg-blue-700 text-white rounded-xl text-sm font-semibold flex items-center justify-center gap-1"
                >
                  {isBooking && <Loader2 className="w-4 h-4 animate-spin" />}
                  Xác nhận đặt
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
