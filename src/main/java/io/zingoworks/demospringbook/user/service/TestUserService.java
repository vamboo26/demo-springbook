package io.zingoworks.demospringbook.user.service;

import io.zingoworks.demospringbook.user.domain.User;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TestUserService extends UserService {
	
	private String id;
	
	@Override
	protected void upgradeLevel(User user) {
		if (user.getId().equals(this.id)) {
			throw new TestUserServiceException();
		}
		super.upgradeLevel(user);
	}
	
	static class TestUserServiceException extends RuntimeException {
	
	}
}
