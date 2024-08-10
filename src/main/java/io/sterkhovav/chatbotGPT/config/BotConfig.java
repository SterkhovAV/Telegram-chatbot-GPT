package io.sterkhovav.chatbotGPT.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource("classpath:application.yaml")
public class BotConfig {

    @Value("${bot.name}")
    String botName;

    @Value("${bot.token}")
    String botToken;

    @Value("${spring.ai.openai.api-key}")
    String apiToken;

    @Value("${bot.buffer.threshold}")
    int bufferThreshold;

    @Value("${bot.dialog.dialog-timeout}")
    int dialogTimeout;

    @Value("${bot.dialog.previous-messages-limit}")
    int previousMessagesLimit;
}