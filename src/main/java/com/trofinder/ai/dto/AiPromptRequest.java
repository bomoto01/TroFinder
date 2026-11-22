package com.trofinder.ai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AiPromptRequest {

    @NotBlank(message = "Câu hỏi hoặc yêu cầu tìm kiếm không được để trống")
    private String prompt;

    private Long roomId;
}
