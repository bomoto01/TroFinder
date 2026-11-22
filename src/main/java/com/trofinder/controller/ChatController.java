package com.trofinder.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trofinder.dto.request.SendMessageRequest;
import com.trofinder.dto.response.ApiResponse;
import com.trofinder.dto.response.ConversationResponse;
import com.trofinder.dto.response.MessageResponse;
import com.trofinder.service.interfaces.ChatService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    /**
     * Bắt đầu hoặc lấy cuộc trò chuyện với chủ phòng
     */
    @PostMapping("/conversations")
    public ResponseEntity<ApiResponse<ConversationResponse>> getOrCreateConversation(
            @RequestParam Long roomId) {

        ConversationResponse response = chatService.getOrCreateConversation(roomId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<ConversationResponse>builder()
                        .success(true)
                        .message("Tạo hoặc lấy cuộc trò chuyện thành công")
                        .data(response)
                        .build());
    }

    /**
     * Lấy danh sách các cuộc trò chuyện của người dùng hiện tại
     */
    @GetMapping("/conversations")
    public ResponseEntity<ApiResponse<List<ConversationResponse>>> getUserConversations() {

        List<ConversationResponse> response = chatService.getUserConversations();

        return ResponseEntity.ok(
                ApiResponse.<List<ConversationResponse>>builder()
                        .success(true)
                        .message("Lấy danh sách cuộc trò chuyện thành công")
                        .data(response)
                        .build());
    }

    /**
     * Lấy lịch sử tin nhắn trong cuộc trò chuyện (Có phân trang)
     */
    @GetMapping("/conversations/{id}/messages")
    public ResponseEntity<ApiResponse<Page<MessageResponse>>> getMessages(
            @PathVariable Long id,
            @PageableDefault(size = 30) Pageable pageable) {

        Page<MessageResponse> response = chatService.getMessages(id, pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<MessageResponse>>builder()
                        .success(true)
                        .message("Lấy tin nhắn thành công")
                        .data(response)
                        .build());
    }

    /**
     * Gửi tin nhắn qua REST API
     */
    @PostMapping("/messages")
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            @Valid @RequestBody SendMessageRequest request) {

        MessageResponse response = chatService.sendMessage(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<MessageResponse>builder()
                        .success(true)
                        .message("Gửi tin nhắn thành công")
                        .data(response)
                        .build());
    }

    /**
     * Đánh dấu đã đọc tin nhắn trong cuộc trò chuyện
     */
    @PatchMapping("/conversations/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long id) {

        chatService.markAsRead(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Đã đánh dấu đọc tin nhắn")
                        .build());
    }

    /**
     * STOMP Message Handler: client gửi tới /app/chat.send
     */
    @MessageMapping("/chat.send")
    public void handleStompMessage(@Payload SendMessageRequest request) {
        chatService.sendMessage(request);
    }
}
