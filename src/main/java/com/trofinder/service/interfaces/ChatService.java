package com.trofinder.service.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.trofinder.dto.request.SendMessageRequest;
import com.trofinder.dto.response.ConversationResponse;
import com.trofinder.dto.response.MessageResponse;

public interface ChatService {

    ConversationResponse getOrCreateConversation(Long roomId);

    List<ConversationResponse> getUserConversations();

    Page<MessageResponse> getMessages(Long conversationId, Pageable pageable);

    MessageResponse sendMessage(SendMessageRequest request);

    void markAsRead(Long conversationId);
}
