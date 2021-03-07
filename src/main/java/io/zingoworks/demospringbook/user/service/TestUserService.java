package io.zingoworks.demospringbook.user.service;

import io.zingoworks.demospringbook.user.domain.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class TestUserService extends UserServiceImpl {

    @Override
    protected void upgradeLevel(User user) {
        if (user.getId().equals("d")) {
            throw new TestUserServiceException();
        }
        super.upgradeLevel(user);
    }
    
    @Override
    public List<User> getAll() {
        for (User user : super.getAll()) {
            super.update(user);
        }
        return null;
    }
    
    static class TestUserServiceException extends RuntimeException {

    }
}
