import { api } from './api';
import { ApiResponse, PageResponse, Review } from '../types';

export const reviewService = {
  getReviewsByRoom: async (roomId: number, page = 0, size = 10) => {
    const res = await api.get<ApiResponse<PageResponse<Review>>>(`/api/reviews/room/${roomId}`, {
      params: { page, size },
    });
    return res.data.data;
  },

  createReview: async (roomId: number, rating: number, comment: string) => {
    const res = await api.post<ApiResponse<Review>>('/api/reviews', { roomId, rating, comment });
    return res.data.data;
  },

  deleteReview: async (reviewId: number) => {
    const res = await api.delete<ApiResponse<void>>(`/api/reviews/${reviewId}`);
    return res.data;
  },
};
