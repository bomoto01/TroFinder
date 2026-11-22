import { api } from './api';
import { ApiResponse, Conversation, Message, PageResponse } from '../types';

export const chatService = {
  getOrCreateConversation: async (roomId: number) => {
    const res = await api.post<ApiResponse<Conversation>>('/api/chat/conversations', null, {
      params: { roomId },
    });
    return res.data.data;
  },

  getUserConversations: async () => {
    const res = await api.get<ApiResponse<Conversation[]>>('/api/chat/conversations');
    return res.data.data;
  },

  getMessages: async (conversationId: number, page = 0, size = 30) => {
    const res = await api.get<ApiResponse<PageResponse<Message>>>(`/api/chat/conversations/${conversationId}/messages`, {
      params: { page, size },
    });
    return res.data.data;
  },

  sendMessage: async (conversationId: number, content?: string, imageUrl?: string) => {
    const res = await api.post<ApiResponse<Message>>('/api/chat/messages', {
      conversationId,
      content,
      imageUrl,
    });
    return res.data.data;
  },

  markAsRead: async (conversationId: number) => {
    const res = await api.patch<ApiResponse<void>>(`/api/chat/conversations/${conversationId}/read`);
    return res.data;
  },
};
