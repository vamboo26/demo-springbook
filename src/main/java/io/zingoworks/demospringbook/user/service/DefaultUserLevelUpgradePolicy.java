package io.zingoworks.demospringbook.user.service;

import io.zingoworks.demospringbook.user.domain.Level;
import io.zingoworks.demospringbook.user.domain.User;
import org.springframework.stereotype.Component;

@Component
public class DefaultUserLevelUpgradePolicy implements UserLevelUpgradePolicy {
	
	@Override
	public boolean canUpgradeLevel(User user) {
		Level currentLevel = user.getLevel();
		
		switch (currentLevel) {
			case BASIC:
				return user.getLoginSequence() >= UserServiceImpl.MIN_LOGIN_SEQUENCE_FOR_SILVER;
			case SILVER:
				return user.getRecommendationCount() >= UserServiceImpl.MIN_RECOMMEND_FOR_GOLD;
			case GOLD:
				return false;
			default:
				throw new IllegalArgumentException("Unknown Level: " + currentLevel);
		}
	}
}
