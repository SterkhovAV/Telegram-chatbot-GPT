package io.sterkhovav.chatbotGPT.service.menu;

import io.sterkhovav.chatbotGPT.enums.ModelGPTEnum;
import io.sterkhovav.chatbotGPT.service.user.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class MenuGPTModelServiceImpl implements MenuGPTModelService {

    private final UserService userService;

    @Override
    public SendMessage getGPTMenu(Long chatId, String username) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        var userActiveGptModel = userService.getUserModelGPT(username);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(formRowsFromModelGPTEnum(userActiveGptModel));
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage.setText(userActiveGptModel.getDescription());
        return sendMessage;
    }

    @Override
    @Transactional
    public EditMessageText updateGPTMenu(String callbackData, Integer messageId, Long chatId, String username) {
        var gptModel = ModelGPTEnum.getByName(callbackData);
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId);
        message.setMessageId(messageId);
        message.setText(gptModel.getDescription());

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(formRowsFromModelGPTEnum(gptModel));
        message.setReplyMarkup(inlineKeyboardMarkup);

        userService.updateUserModelGPT(username,gptModel);

        return message;
    }


    private List<List<InlineKeyboardButton>> formRowsFromModelGPTEnum(ModelGPTEnum userActiveGptModel) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> currentRow = new ArrayList<>();

        for (ModelGPTEnum model : ModelGPTEnum.values()) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            String buttonText = model.getName();

            if (model == userActiveGptModel) {
                buttonText = "✅ " + buttonText;
            }

            button.setText(buttonText);
            button.setCallbackData(model.getName());

            currentRow.add(button);

            if (currentRow.size() == 2) {
                rows.add(currentRow);
                currentRow = new ArrayList<>();
            }
        }

        if (!currentRow.isEmpty()) {
            rows.add(currentRow);
        }

        return rows;
    }
}
