package io.sterkhovav.chatbotGPT.service.user;

import io.sterkhovav.chatbotGPT.models.User;

public interface AccessControlService {
    User getUserIfHeHasAuthorities(String username);

    boolean processFirstChatUser(String username);
}
