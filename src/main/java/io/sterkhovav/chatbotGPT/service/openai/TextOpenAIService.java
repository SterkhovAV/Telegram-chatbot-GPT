package io.sterkhovav.chatbotGPT.service.openai;

import io.sterkhovav.chatbotGPT.models.User;
import lombok.SneakyThrows;

public interface TextOpenAIService {

    @SneakyThrows
    void executeGPTTextResponse(String request, Long chatId, User user);
}
