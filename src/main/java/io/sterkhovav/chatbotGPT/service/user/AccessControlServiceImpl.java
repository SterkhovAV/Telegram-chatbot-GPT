package io.sterkhovav.chatbotGPT.service.user;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@AllArgsConstructor
public class AccessControlServiceImpl implements AccessControlService {

    private final ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

    private final UserService userService;

    @Override
    public boolean checkUserAuthorities(String username) {
        var user = userService.findUserByUsername(username);
        if (user != null) {
            if (user.getActive())
                return true;
        }
        return false;
    }

    @SneakyThrows
    @Override
    public boolean processFirstChatUser(String username) {
            Future<Boolean> result = singleThreadExecutor.submit(() -> executeFirstChatUser(username));
            return result.get();
    }


    @SneakyThrows
    private boolean executeFirstChatUser(String username) {
        if (userService.countUsers() == 0) {
            userService.addNewOrActivateUser(username);
            return true;
        }

        return false;
    }


}