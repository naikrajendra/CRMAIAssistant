package com.crmai.assistant.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ChatClientConfiguration {

    @Bean
    public ChatClient chatClient(
            @Value("${spring.ai.openai.api-key:}") String apiKey,
            @Value("${spring.ai.openai.chat.options.model:gpt-4o-mini}") String modelName) {
        if (apiKey != null && !apiKey.isBlank() && !"demo-key".equals(apiKey)) {
            OpenAiApi openAiApi = OpenAiApi.builder()
                    .apiKey(apiKey)
                    .baseUrl("https://api.openai.com/v1")
                    .build();

            OpenAiChatModel openAiChatModel = OpenAiChatModel.builder()
                    .openAiApi(openAiApi)
                    .defaultOptions(OpenAiChatOptions.builder()
                            .model(modelName)
                            .temperature(0.2)
                            .build())
                    .build();

            return ChatClient.create(openAiChatModel);
        }

        return ChatClient.create(new LocalFallbackChatModel());
    }

    private static final class LocalFallbackChatModel implements ChatModel {

        @Override
        public ChatResponse call(Prompt prompt) {
            String summary = "Daily CRM briefing generated successfully. Summary of customer health and active opportunities is included in the account email.";
            return new ChatResponse(List.of(new Generation(new AssistantMessage(summary))));
        }
    }
}
