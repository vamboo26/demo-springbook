package io.zingoworks.demospringbook.user.service;

import io.zingoworks.demospringbook.user.domain.User;

@FunctionalInterface
public interface UserLevelUpgradePolicy {
	
	boolean canUpgradeLevel(User user);
}
