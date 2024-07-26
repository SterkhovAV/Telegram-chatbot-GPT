package io.sterkhovav.chatbotGPT.service.user;

import io.sterkhovav.chatbotGPT.enums.ModelGPTEnum;
import io.sterkhovav.chatbotGPT.models.User;

public interface UserService {
    User findUserByUsername(String username);

    void addNewOrActivateUser(String username);

    Long countUsers();

    ModelGPTEnum getUserModelGPT(String username);

    void updateUserModelGPT(String username, ModelGPTEnum modelGPTEnum);
}
