import { api } from './api';
import { ApiResponse, PageResponse, RoomDetail, RoomSummary, MapMarker } from '../types';

export interface RoomSearchParams {
  provinceId?: number;
  districtId?: number;
  wardId?: number;
  minPrice?: number;
  maxPrice?: number;
  minArea?: number;
  maxArea?: number;
  roomType?: string;
  keyword?: string;
  verified?: boolean;
  featured?: boolean;
  amenityIds?: number[];
  sortBy?: string;
  page?: number;
  size?: number;
}

export const roomService = {
  getSummaryRooms: async (params?: RoomSearchParams) => {
    const res = await api.get<ApiResponse<PageResponse<RoomSummary>>>('/api/rooms/summary', { params });
    return res.data.data;
  },

  getRoomById: async (id: number) => {
    const res = await api.get<ApiResponse<RoomDetail>>(`/api/rooms/${id}`);
    return res.data.data;
  },

  getMapMarkers: async (minLat?: number, maxLat?: number, minLng?: number, maxLng?: number) => {
    const res = await api.get<ApiResponse<MapMarker[]>>('/api/map/markers', {
      params: { minLat, maxLat, minLng, maxLng },
    });
    return res.data.data;
  },

  getNearbyRooms: async (lat: number, lng: number, radiusKm = 5.0) => {
    const res = await api.get<ApiResponse<MapMarker[]>>('/api/map/nearby', {
      params: { lat, lng, radiusKm },
    });
    return res.data.data;
  },

  createRoom: async (data: any) => {
    const res = await api.post<ApiResponse<RoomDetail>>('/api/rooms', data);
    return res.data.data;
  },

  updateRoom: async (id: number, data: any) => {
    const res = await api.put<ApiResponse<RoomDetail>>(`/api/rooms/${id}`, data);
    return res.data.data;
  },

  deleteRoom: async (id: number) => {
    const res = await api.delete<ApiResponse<void>>(`/api/rooms/${id}`);
    return res.data;
  },

  getMyRooms: async () => {
    const res = await api.get<ApiResponse<RoomSummary[]>>('/api/rooms/my-rooms');
    return res.data.data;
  },

  updateRoomStatus: async (id: number, status: string) => {
    const res = await api.put<ApiResponse<RoomDetail>>(`/api/rooms/${id}/status`, null, {
      params: { status },
    });
    return res.data.data;
  },

  uploadImages: async (roomId: number, files: File[]) => {
    const formData = new FormData();
    files.forEach((file) => formData.append('files', file));
    const res = await api.post<ApiResponse<string[]>>(`/api/rooms/${roomId}/images`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return res.data.data;
  },
};
