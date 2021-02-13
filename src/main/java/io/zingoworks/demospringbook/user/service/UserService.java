package io.zingoworks.demospringbook.user.service;

import io.zingoworks.demospringbook.user.dao.UserDao;
import io.zingoworks.demospringbook.user.domain.Level;
import io.zingoworks.demospringbook.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
	
	public static final int MIN_LOGIN_SEQUENCE_FOR_SILVER = 50;
	public static final int MIN_RECOMMEND_FOR_GOLD = 30;
	
	private UserDao userDao;
	
	@Autowired
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	public void upgradeLevels() {
		List<User> users = userDao.getAll();
		
		for (User user : users) {
			if (canUpgradeLevel(user)) {
				upgradeLevel(user);
			}
		}
	}
	
	private void upgradeLevel(User user) {
		user.upgradeLevel();
		userDao.update(user);
	}
	
	private boolean canUpgradeLevel(User user) {
		Level currentLevel = user.getLevel();
		
		switch (currentLevel) {
			case BASIC:
				return user.getLoginSequence() >= MIN_LOGIN_SEQUENCE_FOR_SILVER;
			case SILVER:
				return user.getRecommendationCount() >= MIN_RECOMMEND_FOR_GOLD;
			case GOLD:
				return false;
			default:
				throw new IllegalArgumentException("Unknown Level: " + currentLevel);
		}
	}
	
	public void add(User user) {
		if (user.getLevel() == null) {
			user.setLevel(Level.BASIC);
		}
		userDao.add(user);
	}
}
