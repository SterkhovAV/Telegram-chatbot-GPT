package io.sterkhovav.chatbotGPT.service.openai;

import io.sterkhovav.chatbotGPT.models.User;
import lombok.SneakyThrows;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

public interface TextOpenAIService {

    @SneakyThrows
    void executeGPTTextResponse(String request, Long chatId, User user);
}
