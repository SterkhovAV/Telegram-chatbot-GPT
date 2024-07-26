package io.sterkhovav.chatbotGPT.service.user;

public interface AccessControlService {
    boolean checkUserAuthorities(String username);

    boolean processFirstChatUser(String username);
}
