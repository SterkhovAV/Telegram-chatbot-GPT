package io.sterkhovav.chatbotGPT.service.openai;

import io.sterkhovav.chatbotGPT.config.BotConfig;
import io.sterkhovav.chatbotGPT.models.User;
import io.sterkhovav.chatbotGPT.service.TelegramBot;
import io.sterkhovav.chatbotGPT.service.user.UserDialogService;
import io.sterkhovav.chatbotGPT.utils.CustomUtils;
import lombok.AllArgsConstructor;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static io.sterkhovav.chatbotGPT.utils.Constants.*;


@Service
@AllArgsConstructor
@Slf4j
public class TextOpenAIServiceImpl implements TextOpenAIService {

    private final BotConfig botConfig;
    private final TelegramBot telegramBot;
    private final UserDialogService userDialogService;
    private final ConcurrentMap<Long, Boolean> userBusyMap = new ConcurrentHashMap<>();

    @SneakyThrows
    @Override
    public void executeGPTTextResponse(String request, Long chatId, User user) {
        userBusyMap.putIfAbsent(chatId, false);

        var initMessage = SendMessage.builder()
                .chatId(chatId)
                .text(GPT_RESPONSE_INIT_MESSAGE)
                .build();

        if (userBusyMap.get(chatId)) {
            initMessage.setText(ANOTHER_MESSAGE_IS_PROCESSING);
            telegramBot.execute(initMessage);
            return;
        } else {
            userBusyMap.put(chatId, true);
        }

        var messageId = telegramBot.execute(initMessage).getMessageId();

        CompletableFuture.runAsync(() -> {
            var response = requestToOpenAi(request, user);

            StringBuilder fullText = new StringBuilder();
            StringBuffer currentMessageBuffer = new StringBuffer();

            response.doOnNext(chatResponse -> processChatResponse(chatResponse, messageId, chatId, fullText, currentMessageBuffer))
                    .doOnError(error -> log.error("Error: {}", error.getMessage()))
                    .doOnComplete(() -> {
                        userBusyMap.put(chatId, false);
                        userDialogService.saveDialog(request, fullText.toString(), user);
                    })
                    .blockLast();
        });
    }


    public Flux<ChatResponse> requestToOpenAi(String message, User user) {
        var openAiApi = new OpenAiApi(botConfig.getApiToken());

        List<Message> messages = userDialogService.getPreviousDialogMessages(user);
        //new SystemMessage("Strongly use MARKDOWN formatting for telegram and don't use #."),
        messages.add(new UserMessage(message));

        var chatModel = new OpenAiChatModel(openAiApi);

        return chatModel.stream(
                new Prompt(
                        messages,
                        OpenAiChatOptions.builder()
                                .withModel(user.getModelGPT().getName())
                                .build()
                ));

    }


    @SneakyThrows
    private void processChatResponse(ChatResponse chatResponse, Integer messageId, Long chatId, StringBuilder fullText, StringBuffer currentMessageBuffer) {
        Thread.sleep(100);
        String finishReason = chatResponse.getResult().getMetadata().getFinishReason();
        if (finishReason != null && finishReason.equals(STOP)) {
            fullText.append(currentMessageBuffer);
            updateTelegramMessage(chatId, messageId, fullText.toString(), true);
            return;
        }

        String responseText = chatResponse.getResult().getOutput().getContent();
        if (!responseText.isEmpty()) {
            currentMessageBuffer.append(responseText);
            if (currentMessageBuffer.length() >= botConfig.getBufferThreshold()) {
                fullText.append(currentMessageBuffer);
                updateTelegramMessage(chatId, messageId, fullText.toString(), false);
                currentMessageBuffer.setLength(0);
            }
        }
    }


    @SneakyThrows
    private void updateTelegramMessage(Long chatId, Integer messageId, String text, Boolean parseMarkdown) {
        EditMessageText editMessageMarkdown = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(text)
                .build();
        if (parseMarkdown) {
            editMessageMarkdown.setParseMode(ParseMode.MARKDOWNV2);
            editMessageMarkdown.setText(CustomUtils.escapeMarkdown(text));
        }
        try {
            telegramBot.execute(editMessageMarkdown);
        } catch (TelegramApiException e) {
            log.error("TelegramApiException: {}", e.getMessage());
            log.error(text);
        }
    }
}
