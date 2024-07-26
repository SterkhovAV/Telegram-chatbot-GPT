package io.sterkhovav.chatbotGPT.service.user;

import io.sterkhovav.chatbotGPT.enums.ModelGPTEnum;
import io.sterkhovav.chatbotGPT.models.User;
import io.sterkhovav.chatbotGPT.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findUserByUsername(String username) {
        var user = userRepository.findByUsername(username).orElse(null);
        return user;
    }


    @Override
    public void addNewOrActivateUser(String username) {
        var userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            var user = userOptional.get();
            user.setActive(true);
            userRepository.save(user);
        } else {
            User newUser = User.builder()
                    .username(username)
                    .modelGPT(ModelGPTEnum.GPT_4_TURBO)
                    .amountSpent(0.0)
                    .active(true)
                    .build();
            userRepository.save(newUser);
        }
    }

    @Override
    public Long countUsers(){
        return userRepository.count();
    }

    @Override
    public ModelGPTEnum getUserModelGPT(String username) {
        return userRepository.findModelGPTByUsername(username);
    }

    @Override
    public void updateUserModelGPT(String username, ModelGPTEnum modelGPTEnum) {
        var user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            user.setModelGPT(modelGPTEnum);
            userRepository.save(user);
        }
    }
}
