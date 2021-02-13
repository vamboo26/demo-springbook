package io.zingoworks.demospringbook.user.service;

import io.zingoworks.demospringbook.DemoSpringbookApplication;
import io.zingoworks.demospringbook.user.dao.UserDao;
import io.zingoworks.demospringbook.user.domain.Level;
import io.zingoworks.demospringbook.user.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

import static io.zingoworks.demospringbook.user.service.UserService.MIN_LOGIN_SEQUENCE_FOR_SILVER;
import static io.zingoworks.demospringbook.user.service.UserService.MIN_RECOMMEND_FOR_GOLD;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = DemoSpringbookApplication.class)
class UserServiceTest {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private UserLevelUpgradePolicy userLevelUpgradePolicy;
	
	@Autowired
	private DataSource dataSource;
	
	private List<User> users;
	
	@BeforeEach
	void setUp() {
		users = Arrays.asList(
				new User("a", "에이", "p1", Level.BASIC, MIN_LOGIN_SEQUENCE_FOR_SILVER - 1, 0),
				new User("b", "비", "p2", Level.BASIC, MIN_LOGIN_SEQUENCE_FOR_SILVER, 0),
				new User("c", "시", "p3", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD - 1),
				new User("d", "디", "p4", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD),
				new User("e", "이", "p5", Level.GOLD, 100, Integer.MAX_VALUE)
		);
	}
	
	@Test
	void bean() {
		assertThat(this.userService).isNotNull();
	}
	
	@Test
	void add() {
		userDao.deleteAll();
		
		User userWithGoldLevel = users.get(4);
		User userWithoutLevel = users.get(0);
		userWithoutLevel.setLevel(null);
		
		userService.add(userWithGoldLevel);
		userService.add(userWithoutLevel);
		
		User userWithGoldLevelRead = userDao.get(userWithGoldLevel.getId());
		User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());
		
		assertThat(userWithGoldLevelRead.getLevel()).isEqualTo(userWithGoldLevel.getLevel());
		assertThat(userWithoutLevelRead.getLevel()).isEqualTo(userWithoutLevel.getLevel());
	}
	
	@Test
	void upgradeLevels() {
		userDao.deleteAll();
		for (User user : users) {
			userDao.add(user);
		}
		
		userService.upgradeLevels();
		
		checkLevelUpgraded(users.get(0), false);
		checkLevelUpgraded(users.get(1), true);
		checkLevelUpgraded(users.get(2), false);
		checkLevelUpgraded(users.get(3), true);
		checkLevelUpgraded(users.get(4), false);
	}
	
	@Test
	void upgradeAllOrNothing() {
		UserService testUserService = new TestUserService(users.get(3).getId());
		testUserService.setUserDao(this.userDao);
		testUserService.setUserLevelUpgradePolicy(this.userLevelUpgradePolicy);
		testUserService.setDataSource(this.dataSource);
		
		userDao.deleteAll();
		for (User user : users) {
			userDao.add(user);
		}
		
		try {
			testUserService.upgradeLevels();
			Assertions.fail("TestUserServiceException expected");
		} catch (TestUserService.TestUserServiceException e) {
		
		}
		
		checkLevelUpgraded(users.get(1), false);
	}
	
	private void checkLevelUpgraded(User user, boolean upgraded) {
		User userUpdate = userDao.get(user.getId());
		
		if (upgraded) {
			assertThat(userUpdate.getLevel()).isEqualTo(user.getLevel().getNext());
		} else {
			assertThat(userUpdate.getLevel()).isEqualTo(user.getLevel());
		}
	}
}
