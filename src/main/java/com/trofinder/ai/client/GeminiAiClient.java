package com.trofinder.ai.client;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Primary
public class GeminiAiClient implements AiClient {

    private final LocalRuleBasedAiClient fallbackClient;
    private final RestClient restClient;

    @Value("${trofinder.ai.gemini.api-key:}")
    private String apiKey;

    @Value("${trofinder.ai.gemini.model:gemini-3.5-flash-lite}")
    private String primaryModel;

    public GeminiAiClient(LocalRuleBasedAiClient fallbackClient) {
        this.fallbackClient = fallbackClient;
        org.springframework.http.client.SimpleClientHttpRequestFactory requestFactory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(java.time.Duration.ofSeconds(3));
        requestFactory.setReadTimeout(java.time.Duration.ofSeconds(7));
        this.restClient = RestClient.builder().requestFactory(requestFactory).build();
    }

    @Override
    public String generateResponse(String systemPrompt, String userMessage) {
        if (apiKey == null || apiKey.isBlank()) {
            return fallbackClient.generateResponse(systemPrompt, userMessage);
        }

        List<String> candidateModels = List.of(
                primaryModel != null && !primaryModel.isBlank() ? primaryModel : "gemini-3.5-flash-lite",
                "gemini-flash-lite-latest",
                "gemini-3.8-flash"
        );

        for (String model : candidateModels) {
            try {
                String url = String.format(
                        "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
                        model, apiKey);

                Map<String, Object> requestBody = Map.of(
                        "system_instruction", Map.of(
                                "parts", List.of(Map.of("text", systemPrompt != null ? systemPrompt : ""))),
                        "contents", List.of(
                                Map.of(
                                        "role", "user",
                                        "parts", List.of(Map.of("text", userMessage != null ? userMessage : "")))));

                Map<?, ?> response = restClient.post()
                        .uri(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(requestBody)
                        .retrieve()
                        .body(Map.class);

                if (response != null && response.get("candidates") instanceof List<?> candidates && !candidates.isEmpty()) {
                    Object firstCandidate = candidates.get(0);
                    if (firstCandidate instanceof Map<?, ?> candMap) {
                        Object contentObj = candMap.get("content");
                        if (contentObj instanceof Map<?, ?> contentMap) {
                            Object partsObj = contentMap.get("parts");
                            if (partsObj instanceof List<?> partsList && !partsList.isEmpty()) {
                                Object firstPart = partsList.get(0);
                                if (firstPart instanceof Map<?, ?> partMap) {
                                    Object text = partMap.get("text");
                                    if (text != null && !text.toString().isBlank()) {
                                        return text.toString().trim();
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("Gemini model {} failed, trying next fallback: {}", model, e.getMessage());
            }
        }

        return fallbackClient.generateResponse(systemPrompt, userMessage);
    }

    @Override
    public String getProviderName() {
        if (apiKey != null && !apiKey.isBlank()) {
            return "GOOGLE_GEMINI";
        }
        return fallbackClient.getProviderName();
    }
}
