import { api } from './api';
import { ApiResponse, PageResponse, RoomSummary, AiMapResponse } from '../types';

export const aiService = {
  searchMapByAi: async (prompt: string) => {
    const res = await api.post<ApiResponse<AiMapResponse>>('/api/ai/map', { prompt });
    return res.data.data;
  },

  searchByPrompt: async (prompt: string, page = 0, size = 12) => {
    const res = await api.post<ApiResponse<PageResponse<RoomSummary>>>('/api/ai/search', { prompt }, {
      params: { page, size },
    });
    return res.data.data;
  },

  getRecommendations: async (limit = 6) => {
    const res = await api.get<ApiResponse<RoomSummary[]>>('/api/ai/recommendations', {
      params: { limit },
    });
    return res.data.data;
  },

  chatWithAssistant: async (prompt: string, roomId?: number) => {
    const res = await api.post<ApiResponse<{ reply: string; modelProvider: string }>>('/api/ai/chat', {
      prompt,
      roomId,
    });
    return res.data.data;
  },
};
