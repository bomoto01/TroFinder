package com.trofinder.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trofinder.ai.dto.AiChatResponse;
import com.trofinder.ai.dto.AiMapResponse;
import com.trofinder.ai.dto.AiPromptRequest;
import com.trofinder.ai.service.AiAssistantService;
import com.trofinder.ai.service.AiRecommendationService;
import com.trofinder.ai.service.AiSearchService;
import com.trofinder.dto.response.ApiResponse;
import com.trofinder.dto.response.RoomSummaryResponse;
import com.trofinder.entity.User;
import com.trofinder.service.interfaces.CurrentUserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiSearchService aiSearchService;
    private final AiRecommendationService aiRecommendationService;
    private final AiAssistantService aiAssistantService;
    private final CurrentUserService currentUserService;

    /**
     * Tìm kiếm phòng bằng ngôn ngữ tự nhiên (AI Search)
     */
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<Page<RoomSummaryResponse>>> searchByAi(
            @Valid @RequestBody AiPromptRequest request,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<RoomSummaryResponse> result = aiSearchService.searchByNaturalLanguage(request.getPrompt(), pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<RoomSummaryResponse>>builder()
                        .success(true)
                        .message("Tìm kiếm AI thành công")
                        .data(result)
                        .build());
    }

    /**
     * Tìm kiếm và hiển thị markers bản đồ bằng câu lệnh tự nhiên (AI Map)
     */
    @PostMapping("/map")
    public ResponseEntity<ApiResponse<AiMapResponse>> searchMapByAi(
            @Valid @RequestBody AiPromptRequest request) {

        AiMapResponse result = aiSearchService.searchMapByNaturalLanguage(request.getPrompt());

        return ResponseEntity.ok(
                ApiResponse.<AiMapResponse>builder()
                        .success(true)
                        .message("Tìm kiếm bản đồ AI thành công")
                        .data(result)
                        .build());
    }

    /**
     * Gợi ý phòng thông minh cá nhân hóa
     */
    @GetMapping("/recommendations")
    public ResponseEntity<ApiResponse<List<RoomSummaryResponse>>> getRecommendations(
            @RequestParam(defaultValue = "6") int limit) {

        Long userId = null;
        try {
            User currentUser = currentUserService.getCurrentUser();
            if (currentUser != null) {
                userId = currentUser.getId();
            }
        } catch (Exception ignored) {
            // Guest user
        }

        List<RoomSummaryResponse> recommendations = aiRecommendationService.getRecommendations(userId, limit);

        return ResponseEntity.ok(
                ApiResponse.<List<RoomSummaryResponse>>builder()
                        .success(true)
                        .message("Gợi ý phòng thành công")
                        .data(recommendations)
                        .build());
    }

    /**
     * Trợ lý ảo AI giải đáp thắc mắc về phòng trọ
     */
    @PostMapping("/chat")
    public ResponseEntity<ApiResponse<AiChatResponse>> chatWithAssistant(
            @Valid @RequestBody AiPromptRequest request) {

        String reply = aiAssistantService.ask(request.getPrompt(), request.getRoomId());

        return ResponseEntity.ok(
                ApiResponse.<AiChatResponse>builder()
                        .success(true)
                        .message("Trả lời từ AI")
                        .data(AiChatResponse.builder()
                                .reply(reply)
                                .modelProvider(aiAssistantService.getProviderName())
                                .build())
                        .build());
    }
}
