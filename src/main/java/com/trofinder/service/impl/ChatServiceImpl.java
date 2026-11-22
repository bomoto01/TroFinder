package com.trofinder.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trofinder.dto.request.SendMessageRequest;
import com.trofinder.dto.response.ConversationResponse;
import com.trofinder.dto.response.MessageResponse;
import com.trofinder.entity.Conversation;
import com.trofinder.entity.Message;
import com.trofinder.entity.Room;
import com.trofinder.entity.RoomImage;
import com.trofinder.entity.User;
import com.trofinder.exception.BadRequestException;
import com.trofinder.exception.ResourceNotFoundException;
import com.trofinder.exception.UnauthorizedException;
import com.trofinder.repository.ConversationRepository;
import com.trofinder.repository.MessageRepository;
import com.trofinder.repository.RoomRepository;
import com.trofinder.service.interfaces.ChatService;
import com.trofinder.service.interfaces.CurrentUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final RoomRepository roomRepository;
    private final CurrentUserService currentUserService;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public ConversationResponse getOrCreateConversation(Long roomId) {
        User currentUser = currentUserService.getCurrentUser();

        Room room = roomRepository.findByIdAndDeletedFalse(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng."));

        if (room.getOwner().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Bạn không thể tạo cuộc trò chuyện với chính mình.");
        }

        Conversation conversation = conversationRepository.findByRoomIdAndTenantId(roomId, currentUser.getId())
                .orElseGet(() -> {
                    Conversation newConv = Conversation.builder()
                            .room(room)
                            .tenant(currentUser)
                            .landlord(room.getOwner())
                            .build();
                    return conversationRepository.save(newConv);
                });

        return mapToConversationResponse(conversation, currentUser.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponse> getUserConversations() {
        User currentUser = currentUserService.getCurrentUser();
        List<Conversation> conversations = conversationRepository.findByUserId(currentUser.getId());

        return conversations.stream()
                .map(c -> mapToConversationResponse(c, currentUser.getId()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageResponse> getMessages(Long conversationId, Pageable pageable) {
        User currentUser = currentUserService.getCurrentUser();

        Conversation conversation = conversationRepository.findByIdAndParticipant(conversationId, currentUser.getId())
                .orElseThrow(() -> new UnauthorizedException("Bạn không có quyền xem cuộc trò chuyện này."));

        return messageRepository.findByConversationIdOrderByCreatedAtDesc(conversation.getId(), pageable)
                .map(this::mapToMessageResponse);
    }

    @Override
    @Transactional
    public MessageResponse sendMessage(SendMessageRequest request) {
        User currentUser = currentUserService.getCurrentUser();

        Conversation conversation = conversationRepository.findByIdAndParticipant(request.getConversationId(), currentUser.getId())
                .orElseThrow(() -> new UnauthorizedException("Bạn không phải thành viên của cuộc trò chuyện này."));

        if ((request.getContent() == null || request.getContent().isBlank())
                && (request.getImageUrl() == null || request.getImageUrl().isBlank())) {
            throw new BadRequestException("Nội dung tin nhắn hoặc hình ảnh không được để trống.");
        }

        Message message = Message.builder()
                .conversation(conversation)
                .sender(currentUser)
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .isRead(false)
                .build();

        Message saved = messageRepository.save(message);
        MessageResponse response = mapToMessageResponse(saved);

        // Realtime publish via WebSocket STOMP
        try {
            messagingTemplate.convertAndSend("/topic/conversations/" + conversation.getId(), response);
        } catch (Exception ignored) {
            // Logged in debug, don't fail transactional save if WS client isn't active
        }

        return response;
    }

    @Override
    @Transactional
    public void markAsRead(Long conversationId) {
        User currentUser = currentUserService.getCurrentUser();

        Conversation conversation = conversationRepository.findByIdAndParticipant(conversationId, currentUser.getId())
                .orElseThrow(() -> new UnauthorizedException("Bạn không có quyền thao tác cuộc trò chuyện này."));

        List<Message> unreadMessages = messageRepository.findByConversationIdAndSenderIdNotAndIsReadFalse(
                conversation.getId(), currentUser.getId());

        for (Message msg : unreadMessages) {
            msg.setIsRead(true);
        }

        messageRepository.saveAll(unreadMessages);
    }

    private ConversationResponse mapToConversationResponse(Conversation conv, Long currentUserId) {
        Optional<Message> lastMsg = messageRepository.findTopByConversationIdOrderByCreatedAtDesc(conv.getId());
        long unreadCount = messageRepository.countByConversationIdAndSenderIdNotAndIsReadFalse(conv.getId(), currentUserId);

        String thumbnail = null;
        if (conv.getRoom() != null && conv.getRoom().getImages() != null && !conv.getRoom().getImages().isEmpty()) {
            thumbnail = conv.getRoom().getImages().stream()
                    .filter(img -> Boolean.TRUE.equals(img.getThumbnail()))
                    .map(RoomImage::getImageUrl)
                    .findFirst()
                    .orElseGet(() -> conv.getRoom().getImages().get(0).getImageUrl());
        }

        return ConversationResponse.builder()
                .id(conv.getId())
                .roomId(conv.getRoom().getId())
                .roomTitle(conv.getRoom().getTitle())
                .roomThumbnail(thumbnail)
                .tenantId(conv.getTenant().getId())
                .tenantName(conv.getTenant().getFullName())
                .tenantAvatar(conv.getTenant().getAvatar())
                .landlordId(conv.getLandlord().getId())
                .landlordName(conv.getLandlord().getFullName())
                .landlordAvatar(conv.getLandlord().getAvatar())
                .lastMessage(lastMsg.map(Message::getContent).orElse(null))
                .lastMessageAt(lastMsg.map(Message::getCreatedAt).orElse(conv.getCreatedAt()))
                .unreadCount(unreadCount)
                .createdAt(conv.getCreatedAt())
                .build();
    }

    private MessageResponse mapToMessageResponse(Message msg) {
        return MessageResponse.builder()
                .id(msg.getId())
                .conversationId(msg.getConversation().getId())
                .senderId(msg.getSender().getId())
                .senderName(msg.getSender().getFullName())
                .senderAvatar(msg.getSender().getAvatar())
                .content(msg.getContent())
                .imageUrl(msg.getImageUrl())
                .isRead(msg.getIsRead())
                .createdAt(msg.getCreatedAt())
                .build();
    }
}
