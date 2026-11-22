import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { roomService } from '../../services/room.service';
import { authService } from '../../services/auth.service';
import { Loader2, PlusCircle, ArrowLeft } from 'lucide-react';

export const RoomCreatePage: React.FC = () => {
  const navigate = useNavigate();
  const currentUser = authService.getCurrentUser();

  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [roomType, setRoomType] = useState('BOARDING_HOUSE');
  const [price, setPrice] = useState<number>(3000000);
  const [deposit, setDeposit] = useState<number>(3000000);
  const [electricPrice, setElectricPrice] = useState<number>(3500);
  const [waterPrice, setWaterPrice] = useState<number>(25000);
  const [area, setArea] = useState<number>(25);
  const [quantity, setQuantity] = useState<number>(1);
  const [address, setAddress] = useState('');
  const [wardId, setWardId] = useState<number>(1); // Default ward ID
  const [contactName, setContactName] = useState(currentUser?.fullName || '');
  const [contactPhone, setContactPhone] = useState(currentUser?.phone || '');
  const [zaloPhone, setZaloPhone] = useState(currentUser?.phone || '');
  const [selectedFiles, setSelectedFiles] = useState<File[]>([]);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    try {
      const room = await roomService.createRoom({
        title,
        description,
        roomType,
        price,
        deposit,
        electricPrice,
        waterPrice,
        area,
        quantity,
        address,
        wardId,
        contactName,
        contactPhone,
        zaloPhone,
        allowBooking: true,
        allowCall: true,
        allowChat: true,
        amenityIds: [],
      });

      if (selectedFiles.length > 0 && room.id) {
        try {
          await roomService.uploadImages(room.id, selectedFiles);
        } catch {
          // ignore upload error
        }
      }

      alert('Đăng bài thành công! Bài đăng sẽ hiển thị sau khi hệ thống duyệt.');
      navigate(`/rooms/${room.id}`);
    } catch (err: any) {
      alert(err.response?.data?.message || 'Không thể đăng phòng. Vui lòng kiểm tra lại thông tin.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="max-w-3xl mx-auto px-4 py-8 space-y-6">
      <button
        onClick={() => navigate(-1)}
        className="text-xs font-semibold text-slate-500 hover:text-slate-800 flex items-center gap-1"
      >
        <ArrowLeft className="w-3.5 h-3.5" /> Quay lại
      </button>

      <div>
        <h1 className="text-2xl font-bold text-slate-900">Đăng tin cho thuê phòng</h1>
        <p className="text-xs text-slate-500 mt-1">Cung cấp đầy đủ thông tin để thu hút người thuê nhanh chóng</p>
      </div>

      <form onSubmit={handleSubmit} className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/80 shadow-md space-y-6">
        <div>
          <label className="text-xs font-semibold text-slate-700 block mb-1">Tiêu đề bài đăng *</label>
          <input
            type="text"
            required
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="VD: Phòng trọ khép kín full đồ ban công thoáng mát gần ĐH Quốc Gia..."
            className="w-full p-3 text-sm border border-slate-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:outline-none"
          />
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className="text-xs font-semibold text-slate-700 block mb-1">Loại phòng *</label>
            <select
              value={roomType}
              onChange={(e) => setRoomType(e.target.value)}
              className="w-full p-3 text-sm border border-slate-300 rounded-xl bg-white focus:ring-2 focus:ring-blue-500 focus:outline-none"
            >
              <option value="BOARDING_HOUSE">Phòng trọ</option>
              <option value="MINI_APARTMENT">Chung cư mini</option>
              <option value="APARTMENT">Chung cư</option>
              <option value="WHOLE_HOUSE">Nhà nguyên căn</option>
              <option value="HOMESTAY">Homestay</option>
            </select>
          </div>

          <div>
            <label className="text-xs font-semibold text-slate-700 block mb-1">Diện tích (m²) *</label>
            <input
              type="number"
              required
              min={5}
              value={area}
              onChange={(e) => setArea(Number(e.target.value))}
              className="w-full p-3 text-sm border border-slate-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:outline-none"
            />
          </div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className="text-xs font-semibold text-slate-700 block mb-1">Giá thuê (VNĐ/tháng) *</label>
            <input
              type="number"
              required
              min={100000}
              step={100000}
              value={price}
              onChange={(e) => setPrice(Number(e.target.value))}
              className="w-full p-3 text-sm border border-slate-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:outline-none"
            />
          </div>

          <div>
            <label className="text-xs font-semibold text-slate-700 block mb-1">Tiền cọc (VNĐ)</label>
            <input
              type="number"
              min={0}
              step={100000}
              value={deposit}
              onChange={(e) => setDeposit(Number(e.target.value))}
              className="w-full p-3 text-sm border border-slate-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:outline-none"
            />
          </div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className="text-xs font-semibold text-slate-700 block mb-1">Giá điện (VNĐ/kWh)</label>
            <input
              type="number"
              value={electricPrice}
              onChange={(e) => setElectricPrice(Number(e.target.value))}
              className="w-full p-3 text-sm border border-slate-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:outline-none"
            />
          </div>

          <div>
            <label className="text-xs font-semibold text-slate-700 block mb-1">Giá nước (VNĐ/m³ hoặc tháng)</label>
            <input
              type="number"
              value={waterPrice}
              onChange={(e) => setWaterPrice(Number(e.target.value))}
              className="w-full p-3 text-sm border border-slate-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:outline-none"
            />
          </div>
        </div>

        <div>
          <label className="text-xs font-semibold text-slate-700 block mb-1">Địa chỉ chi tiết *</label>
          <input
            type="text"
            required
            value={address}
            onChange={(e) => setAddress(e.target.value)}
            placeholder="Số nhà, ngõ ngách, tên đường..."
            className="w-full p-3 text-sm border border-slate-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:outline-none"
          />
        </div>

        <div>
          <label className="text-xs font-semibold text-slate-700 block mb-1">Mô tả phòng</label>
          <textarea
            rows={4}
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="Mô tả nội thất, giờ giấc, an ninh, tiện ích xung quanh..."
            className="w-full p-3 text-sm border border-slate-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:outline-none"
          />
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <div>
            <label className="text-xs font-semibold text-slate-700 block mb-1">Tên liên hệ *</label>
            <input
              type="text"
              required
              value={contactName}
              onChange={(e) => setContactName(e.target.value)}
              className="w-full p-3 text-sm border border-slate-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:outline-none"
            />
          </div>

          <div>
            <label className="text-xs font-semibold text-slate-700 block mb-1">Số điện thoại *</label>
            <input
              type="tel"
              required
              value={contactPhone}
              onChange={(e) => setContactPhone(e.target.value)}
              className="w-full p-3 text-sm border border-slate-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:outline-none"
            />
          </div>

          <div>
            <label className="text-xs font-semibold text-slate-700 block mb-1">Zalo</label>
            <input
              type="tel"
              value={zaloPhone}
              onChange={(e) => setZaloPhone(e.target.value)}
              className="w-full p-3 text-sm border border-slate-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:outline-none"
            />
          </div>
        </div>

        <div>
          <label className="text-xs font-semibold text-slate-700 block mb-1">Hình ảnh phòng</label>
          <input
            type="file"
            multiple
            accept="image/*"
            onChange={(e) => {
              if (e.target.files) {
                setSelectedFiles(Array.from(e.target.files));
              }
            }}
            className="w-full p-2.5 text-xs border border-slate-300 rounded-xl bg-slate-50"
          />
          <span className="text-[11px] text-slate-400 mt-1 block">Chọn tối đa 20 ảnh (JPG, PNG)</span>
        </div>

        <button
          type="submit"
          disabled={isSubmitting}
          className="w-full py-3.5 bg-blue-600 hover:bg-blue-700 text-white font-semibold text-sm rounded-xl shadow-md transition-colors flex items-center justify-center gap-2"
        >
          {isSubmitting ? <Loader2 className="w-4 h-4 animate-spin" /> : <PlusCircle className="w-4 h-4" />}
          Đăng bài ngay
        </button>
      </form>
    </div>
  );
};
