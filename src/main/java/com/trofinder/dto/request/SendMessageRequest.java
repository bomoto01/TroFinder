package com.trofinder.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendMessageRequest {

    @NotNull(message = "Mã cuộc trò chuyện không được để trống")
    private Long conversationId;

    private String content;

    private String imageUrl;
}
