package io.sterkhovav.chatbotGPT.service;

import io.sterkhovav.chatbotGPT.config.BotConfig;
import io.sterkhovav.chatbotGPT.enums.ModelGPTEnum;
import io.sterkhovav.chatbotGPT.service.menu.MenuGPTModelService;
import io.sterkhovav.chatbotGPT.service.openai.TextOpenAIService;
import io.sterkhovav.chatbotGPT.service.user.AccessControlService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;

import java.util.ArrayList;
import java.util.List;

import static io.sterkhovav.chatbotGPT.utils.Constants.ACCESS_DENIED;
import static io.sterkhovav.chatbotGPT.utils.Constants.FIRST_USER_WELCOME_MESSAGE;

@Setter
@Getter
@Service
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final AccessControlService accessControlService;
    private final MenuGPTModelService menuGptModelService;
    private final TextOpenAIService textOpenAIService;

    public TelegramBot(BotConfig botConfig, AccessControlService accessControlService, MenuGPTModelService menuGptModelService, @Lazy TextOpenAIService textOpenAIService) {
        super(botConfig.getBotToken());
        this.botConfig = botConfig;
        this.accessControlService = accessControlService;
        this.menuGptModelService = menuGptModelService;
        this.textOpenAIService = textOpenAIService;
    }

    @SneakyThrows
    @PostConstruct
    public void init() {
        List<BotCommand> commands = new ArrayList<>();
        //TODO(Move string to const)
        commands.add(new BotCommand("/model", "Choose GPT model"));

        this.execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        processUpdate(update);
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @SneakyThrows
    private void processUpdate(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            var username = update.getMessage().getChat().getUserName();
            var chatId = update.getMessage().getChatId();
            var request = update.getMessage().getText();
            var messageId = update.getMessage().getMessageId();

            var user = accessControlService.getUserIfHeHasAuthorities(username);
            if (user == null) {
                processUserAccess(username, chatId);
            }

            switch (request) {
                case "/start": {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(chatId);
                    sendMessage.setText("HI" + username);
                    execute(sendMessage);
                    return;

                }

                case "/model": {
                    execute(menuGptModelService.getGPTModelMenu(chatId, username));
                    return;
                }

                default:
                    textOpenAIService.executeGPTTextResponse(request, chatId, user);
            }
        } else if (update.hasCallbackQuery()) {
            var callbackData = update.getCallbackQuery().getData();
            var messageId = update.getCallbackQuery().getMessage().getMessageId();
            var username = update.getCallbackQuery().getFrom().getUserName();
            var chatId = update.getCallbackQuery().getMessage().getChatId();

            if (ModelGPTEnum.existsByName(callbackData)) {

                execute(menuGptModelService.updateGPTModelMenu(callbackData, messageId, chatId, username));

            }


        }
    }

    private void processUserAccess(String username, Long chatId) {
        var isFirstUserProcessed = accessControlService.processFirstChatUser(username);
        if (isFirstUserProcessed) {
            sendTextMessage(FIRST_USER_WELCOME_MESSAGE, chatId);
        } else {
            sendTextMessage(ACCESS_DENIED, chatId);
        }
    }

    @SneakyThrows
    public void sendTextMessage(String message, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        execute(sendMessage);
    }

}
