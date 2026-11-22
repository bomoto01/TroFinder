package com.trofinder.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.trofinder.dto.request.SendMessageRequest;
import com.trofinder.dto.response.ConversationResponse;
import com.trofinder.dto.response.MessageResponse;
import com.trofinder.entity.Conversation;
import com.trofinder.entity.Message;
import com.trofinder.entity.Role;
import com.trofinder.entity.Room;
import com.trofinder.entity.User;
import com.trofinder.repository.ConversationRepository;
import com.trofinder.repository.MessageRepository;
import com.trofinder.repository.RoomRepository;
import com.trofinder.service.impl.ChatServiceImpl;
import com.trofinder.service.interfaces.CurrentUserService;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private ChatServiceImpl chatService;

    private User tenant;
    private User landlord;
    private Room room;
    private Conversation conversation;

    @BeforeEach
    void setUp() {
        Role roleTenant = Role.builder().id(1L).name("TENANT").build();
        tenant = User.builder().id(1L).username("tenant").fullName("Nguyen Van A").role(roleTenant).build();

        Role roleLandlord = Role.builder().id(2L).name("LANDLORD").build();
        landlord = User.builder().id(2L).username("landlord").fullName("Tran Thi B").role(roleLandlord).build();

        room = Room.builder().id(10L).title("Phòng trọ đẹp").owner(landlord).deleted(false).build();

        conversation = Conversation.builder()
                .id(100L)
                .room(room)
                .tenant(tenant)
                .landlord(landlord)
                .build();
    }

    @Test
    @DisplayName("Tạo hoặc lấy cuộc trò chuyện thành công")
    void getOrCreateConversation_Success() {
        when(currentUserService.getCurrentUser()).thenReturn(tenant);
        when(roomRepository.findByIdAndDeletedFalse(10L)).thenReturn(Optional.of(room));
        when(conversationRepository.findByRoomIdAndTenantId(10L, 1L)).thenReturn(Optional.of(conversation));

        ConversationResponse response = chatService.getOrCreateConversation(10L);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("Phòng trọ đẹp", response.getRoomTitle());
    }

    @Test
    @DisplayName("Gửi tin nhắn và lưu vào database")
    void sendMessage_Success() {
        when(currentUserService.getCurrentUser()).thenReturn(tenant);
        when(conversationRepository.findByIdAndParticipant(100L, 1L)).thenReturn(Optional.of(conversation));

        Message savedMessage = Message.builder()
                .id(500L)
                .conversation(conversation)
                .sender(tenant)
                .content("Chào bạn, phòng còn trống không?")
                .isRead(false)
                .build();
        when(messageRepository.save(any(Message.class))).thenReturn(savedMessage);

        SendMessageRequest request = new SendMessageRequest();
        request.setConversationId(100L);
        request.setContent("Chào bạn, phòng còn trống không?");

        MessageResponse response = chatService.sendMessage(request);

        assertNotNull(response);
        assertEquals("Chào bạn, phòng còn trống không?", response.getContent());
        verify(messageRepository).save(any(Message.class));
    }
}
