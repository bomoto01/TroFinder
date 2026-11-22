import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { chatService } from '../../services/chat.service';
import { authService } from '../../services/auth.service';
import { Conversation, Message } from '../../types';
import { Send, Loader2, MessageSquare, Image, CheckCheck } from 'lucide-react';

export const ChatPage: React.FC = () => {
  const [searchParams] = useSearchParams();
  const targetConvId = searchParams.get('conversationId');
  const currentUser = authService.getCurrentUser();

  const [conversations, setConversations] = useState<Conversation[]>([]);
  const [activeConv, setActiveConv] = useState<Conversation | null>(null);
  const [messages, setMessages] = useState<Message[]>([]);
  const [inputMessage, setInputMessage] = useState('');
  const [isLoadingConv, setIsLoadingConv] = useState(true);
  const [isLoadingMessages, setIsLoadingMessages] = useState(false);
  const [isSending, setIsSending] = useState(false);

  useEffect(() => {
    loadConversations();
  }, []);

  const loadConversations = async () => {
    setIsLoadingConv(true);
    try {
      const list = await chatService.getUserConversations();
      setConversations(list || []);

      if (targetConvId) {
        const found = list.find((c) => c.id === Number(targetConvId));
        if (found) {
          selectConversation(found);
          return;
        }
      }

      if (list.length > 0 && !activeConv) {
        selectConversation(list[0]);
      }
    } catch (err) {
      console.error('Failed to load conversations:', err);
    } finally {
      setIsLoadingConv(false);
    }
  };

  const selectConversation = async (conv: Conversation) => {
    setActiveConv(conv);
    setIsLoadingMessages(true);
    try {
      const res = await chatService.getMessages(conv.id, 0, 40);
      setMessages((res?.content || []).reverse());
      chatService.markAsRead(conv.id);
    } catch (err) {
      console.error('Failed to load messages:', err);
    } finally {
      setIsLoadingMessages(false);
    }
  };

  const handleSendMessage = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!activeConv || !inputMessage.trim() || isSending) return;

    setIsSending(true);
    try {
      const sent = await chatService.sendMessage(activeConv.id, inputMessage.trim());
      setMessages((prev) => [...prev, sent]);
      setInputMessage('');
    } catch (err: any) {
      alert(err.response?.data?.message || 'Không thể gửi tin nhắn.');
    } finally {
      setIsSending(false);
    }
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
      <div className="bg-white rounded-3xl border border-slate-200/80 shadow-md overflow-hidden flex h-[78vh]">
        {/* Left: Conversations List */}
        <div className="w-full sm:w-80 md:w-96 border-r border-slate-200 flex flex-col bg-slate-50/50">
          <div className="p-4 border-b border-slate-200 bg-white">
            <h2 className="text-base font-bold text-slate-900 flex items-center gap-2">
              <MessageSquare className="w-5 h-5 text-blue-600" />
              Tin nhắn ({conversations.length})
            </h2>
          </div>

          <div className="flex-1 overflow-y-auto divide-y divide-slate-100">
            {isLoadingConv ? (
              <div className="flex justify-center items-center py-12">
                <Loader2 className="w-6 h-6 animate-spin text-blue-600" />
              </div>
            ) : conversations.length === 0 ? (
              <div className="text-center py-12 px-4 text-xs text-slate-400">
                Chưa có cuộc trò chuyện nào. Hãy nhấn "Nhắn tin với chủ trọ" tại trang chi tiết phòng!
              </div>
            ) : (
              conversations.map((conv) => {
                const partnerName =
                  currentUser?.id === conv.tenantId ? conv.landlordName : conv.tenantName;
                const isSelected = activeConv?.id === conv.id;

                return (
                  <button
                    key={conv.id}
                    onClick={() => selectConversation(conv)}
                    className={`w-full text-left p-3.5 flex items-start gap-3 transition-colors ${
                      isSelected ? 'bg-blue-50/80 border-r-4 border-blue-600' : 'hover:bg-slate-100/60'
                    }`}
                  >
                    <div className="w-10 h-10 rounded-full bg-slate-200 flex items-center justify-center font-bold text-sm text-slate-700 shrink-0">
                      {partnerName ? partnerName.charAt(0) : 'U'}
                    </div>
                    <div className="flex-1 min-w-0">
                      <div className="flex justify-between items-baseline mb-0.5">
                        <span className="font-semibold text-xs text-slate-900 truncate">
                          {partnerName}
                        </span>
                        {conv.unreadCount > 0 && (
                          <span className="text-[10px] font-bold px-1.5 py-0.5 rounded-full bg-blue-600 text-white">
                            {conv.unreadCount}
                          </span>
                        )}
                      </div>
                      <p className="text-[11px] text-slate-500 truncate mb-0.5 font-medium">
                        {conv.roomTitle}
                      </p>
                      <p className="text-xs text-slate-400 truncate">
                        {conv.lastMessage || 'Chưa có tin nhắn mới'}
                      </p>
                    </div>
                  </button>
                );
              })
            )}
          </div>
        </div>

        {/* Right: Active Chat Messages */}
        <div className="flex-1 flex flex-col bg-white">
          {activeConv ? (
            <>
              {/* Header */}
              <div className="px-6 py-3.5 border-b border-slate-200 flex items-center justify-between">
                <div>
                  <h3 className="font-bold text-sm text-slate-900">
                    {currentUser?.id === activeConv.tenantId ? activeConv.landlordName : activeConv.tenantName}
                  </h3>
                  <span className="text-xs text-slate-400 truncate max-w-sm block">
                    Đang trao đổi về: {activeConv.roomTitle}
                  </span>
                </div>
              </div>

              {/* Message Feed */}
              <div className="flex-1 p-6 overflow-y-auto space-y-3 bg-slate-50/30">
                {isLoadingMessages ? (
                  <div className="flex justify-center items-center py-12">
                    <Loader2 className="w-6 h-6 animate-spin text-blue-600" />
                  </div>
                ) : messages.length === 0 ? (
                  <div className="text-center py-12 text-xs text-slate-400">
                    Hãy gửi lời chào mở đầu cuộc trò chuyện!
                  </div>
                ) : (
                  messages.map((msg) => {
                    const isMe = msg.senderId === currentUser?.id;
                    return (
                      <div
                        key={msg.id}
                        className={`flex flex-col ${isMe ? 'items-end' : 'items-start'}`}
                      >
                        <div
                          className={`max-w-md px-4 py-2.5 rounded-2xl text-xs leading-relaxed shadow-xs ${
                            isMe
                              ? 'bg-blue-600 text-white rounded-br-none'
                              : 'bg-white border border-slate-200 text-slate-800 rounded-bl-none'
                          }`}
                        >
                          {msg.content}
                        </div>
                      </div>
                    );
                  })
                )}
              </div>

              {/* Input Form */}
              <form onSubmit={handleSendMessage} className="p-3 border-t border-slate-200 flex gap-2">
                <input
                  type="text"
                  value={inputMessage}
                  onChange={(e) => setInputMessage(e.target.value)}
                  placeholder="Nhập nội dung tin nhắn..."
                  className="flex-1 px-4 py-2.5 text-xs border border-slate-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                <button
                  type="submit"
                  disabled={isSending || !inputMessage.trim()}
                  className="px-5 py-2.5 bg-blue-600 hover:bg-blue-700 disabled:opacity-50 text-white text-xs font-semibold rounded-xl flex items-center gap-1.5 transition-colors"
                >
                  {isSending ? <Loader2 className="w-3.5 h-3.5 animate-spin" /> : <Send className="w-3.5 h-3.5" />}
                  Gửi
                </button>
              </form>
            </>
          ) : (
            <div className="flex-1 flex items-center justify-center text-slate-400 text-sm">
              Chọn cuộc trò chuyện bên trái để bắt đầu nhắn tin.
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
