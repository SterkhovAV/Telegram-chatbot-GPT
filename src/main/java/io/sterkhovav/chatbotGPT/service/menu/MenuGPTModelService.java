package io.sterkhovav.chatbotGPT.service.menu;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

public interface MenuGPTModelService {
    SendMessage getGPTMenu(Long chatId, String username);
    EditMessageText updateGPTMenu(String callbackData, Integer messageId, Long chatId, String username);
}
