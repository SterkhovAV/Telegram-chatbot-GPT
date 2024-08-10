package io.sterkhovav.chatbotGPT.service.user;

import io.sterkhovav.chatbotGPT.models.User;
import org.springframework.ai.chat.messages.Message;

import java.util.List;

public interface UserDialogService {
    void saveDialog(String userRequest, String gptResponse, User user);

    List<Message> getPreviousDialogMessages(User user);
}
