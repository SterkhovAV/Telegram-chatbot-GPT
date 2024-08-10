package io.sterkhovav.chatbotGPT.service.user;


import io.sterkhovav.chatbotGPT.config.BotConfig;
import io.sterkhovav.chatbotGPT.models.User;
import io.sterkhovav.chatbotGPT.models.UserDialog;
import io.sterkhovav.chatbotGPT.repository.UserDialogRepository;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class UserDialogServiceImpl implements UserDialogService {

    private final UserDialogRepository userDialogRepository;
    private final BotConfig botConfig;

    @Override
    public void saveDialog(String userRequest, String gptResponse, User user) {
        var userMessage = UserDialog.builder()
                .userId(user.getId())
                .userRequest(userRequest)
                .gptResponse(gptResponse)
                .createDate(LocalDateTime.now())
                .build();
        userDialogRepository.save(userMessage);
    }

    @Override
    public List<Message> getPreviousDialogMessages(User user) {
        //TODO(time can be moved to configs)
        var userPreviousDialogs = userDialogRepository.findByUserIdWithLimit(
                user.getId(),
                botConfig.getDialogTimeout()
        );

        List<Message> userMessagesList = new ArrayList<>();
        userPreviousDialogs.forEach(dialog -> {
            var userMessage = new UserMessage(dialog.getUserRequest());
            var assistantMessage = new AssistantMessage(dialog.getGptResponse());
            userMessagesList.add(userMessage);
            userMessagesList.add(assistantMessage);
        });
        return userMessagesList;
    }
}




