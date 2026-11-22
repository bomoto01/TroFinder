package com.trofinder.ai.client;

public interface AiClient {
    String generateResponse(String systemPrompt, String userMessage);
    String getProviderName();
}
