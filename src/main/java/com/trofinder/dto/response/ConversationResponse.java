package com.trofinder.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponse {

    private Long id;
    private Long roomId;
    private String roomTitle;
    private String roomThumbnail;
    private Long tenantId;
    private String tenantName;
    private String tenantAvatar;
    private Long landlordId;
    private String landlordName;
    private String landlordAvatar;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private Long unreadCount;
    private LocalDateTime createdAt;
}
