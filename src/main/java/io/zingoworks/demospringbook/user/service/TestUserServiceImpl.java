package io.zingoworks.demospringbook.user.service;

import io.zingoworks.demospringbook.user.domain.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service(value = "testUserService")
public class TestUserServiceImpl extends UserServiceImpl {

    @Override
    protected void upgradeLevel(User user) {
        if (user.getId().equals("d")) {
            throw new TestUserServiceException();
        }
        super.upgradeLevel(user);
    }

    static class TestUserServiceException extends RuntimeException {

    }
}
