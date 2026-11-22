export interface User {
  id: number;
  username: string;
  fullName: string;
  email: string;
  phone?: string;
  avatar?: string;
  role: {
    id: number;
    name: string;
    description?: string;
  };
}

export type RoomType = 'BOARDING_HOUSE' | 'APARTMENT' | 'MINI_APARTMENT' | 'WHOLE_HOUSE' | 'HOMESTAY';
export type RoomStatus = 'AVAILABLE' | 'FULL' | 'HIDDEN' | 'EXPIRED';
export type ApprovalStatus = 'PENDING' | 'APPROVED' | 'REJECTED';

export interface RoomSummary {
  id: number;
  title: string;
  price: number;
  deposit: number;
  area: number;
  address: string;
  provinceId?: number;
  provinceName?: string;
  districtId?: number;
  districtName?: string;
  wardId?: number;
  wardName?: string;
  latitude?: number;
  longitude?: number;
  thumbnail?: string;
  roomType: RoomType;
  verified: boolean;
  featured: boolean;
  viewCount: number;
  favoriteCount: number;
  averageRating?: number;
  reviewCount?: number;
  status: RoomStatus;
  approvalStatus: ApprovalStatus;
  ownerId: number;
  ownerName: string;
  ownerAvatar?: string;
  createdAt: string;
}

export interface RoomDetail extends RoomSummary {
  description: string;
  contactName: string;
  contactPhone: string;
  zaloPhone?: string;
  zaloQrUrl?: string;
  electricPrice: number;
  waterPrice: number;
  quantity: number;
  availableQuantity: number;
  allowBooking: boolean;
  allowCall: boolean;
  allowChat: boolean;
  amenities: string[];
  additionalAmenities?: string;
  imageUrls: string[];
  videoUrls: string[];
}

export interface MapMarker {
  id: number;
  title: string;
  price: number;
  area: number;
  latitude: number;
  longitude: number;
  address: string;
  thumbnail?: string;
  roomType: RoomType;
  verified: boolean;
  featured: boolean;
}

export interface AiMapResponse {
  anchorName: string;
  centerLat: number;
  centerLng: number;
  zoom: number;
  totalFound: number;
  markers: MapMarker[];
  aiAnalysis?: string;
  distanceKm?: number;
  recommendedAreas?: string[];
}

export interface Review {
  id: number;
  roomId: number;
  userId: number;
  userName: string;
  userAvatar?: string;
  rating: number;
  comment: string;
  createdAt: string;
}

export interface Conversation {
  id: number;
  roomId: number;
  roomTitle: string;
  roomThumbnail?: string;
  tenantId: number;
  tenantName: string;
  tenantAvatar?: string;
  landlordId: number;
  landlordName: string;
  landlordAvatar?: string;
  lastMessage?: string;
  lastMessageAt?: string;
  unreadCount: number;
  createdAt: string;
}

export interface Message {
  id: number;
  conversationId: number;
  senderId: number;
  senderName: string;
  senderAvatar?: string;
  content?: string;
  imageUrl?: string;
  isRead: boolean;
  createdAt: string;
}

export interface Appointment {
  id: number;
  roomId: number;
  roomTitle: string;
  roomAddress: string;
  tenantId: number;
  tenantName: string;
  tenantPhone: string;
  ownerId: number;
  ownerName: string;
  ownerPhone: string;
  appointmentDate: string;
  appointmentTime: string;
  note?: string;
  status: 'PENDING' | 'CONFIRMED' | 'REJECTED' | 'CANCELLED' | 'COMPLETED';
  createdAt: string;
}

export interface Report {
  id: number;
  roomId: number;
  roomTitle: string;
  reporterId: number;
  reporterName: string;
  reason: 'SCAM' | 'WRONG_PRICE' | 'WRONG_INFORMATION' | 'SPAM' | 'OTHER';
  description: string;
  adminNote?: string;
  status: 'PENDING' | 'REVIEWING' | 'APPROVED' | 'REJECTED' | 'RESOLVED';
  createdAt: string;
}

export interface OwnerDashboard {
  totalRooms: number;
  availableRooms: number;
  rentedRooms: number;
  totalViews: number;
  totalFavorites: number;
  totalAppointments: number;
  pendingAppointments: number;
  totalConversations: number;
}

export interface AdminDashboard {
  totalUsers: number;
  totalLandlords: number;
  totalTenants: number;
  totalRooms: number;
  pendingRooms: number;
  approvedRooms: number;
  rejectedRooms: number;
  totalReports: number;
  pendingReports: number;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface PageResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
}
