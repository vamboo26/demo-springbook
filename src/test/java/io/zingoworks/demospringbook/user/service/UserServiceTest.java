package io.zingoworks.demospringbook.user.service;

import io.zingoworks.demospringbook.DemoSpringbookApplication;
import io.zingoworks.demospringbook.user.dao.UserDao;
import io.zingoworks.demospringbook.user.domain.Level;
import io.zingoworks.demospringbook.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = DemoSpringbookApplication.class)
class UserServiceTest {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserDao userDao;
	
	private List<User> users;
	
	@BeforeEach
	void setUp() {
		users = Arrays.asList(
				new User("a", "에이", "p1", Level.BASIC, 49, 0),
				new User("b", "비", "p2", Level.BASIC, 50, 0),
				new User("c", "시", "p3", Level.SILVER, 60, 29),
				new User("d", "디", "p4", Level.SILVER, 60, 30),
				new User("e", "이", "p5", Level.GOLD, 100, 100)
		);
	}
	
	@Test
	void bean() {
		assertThat(this.userService).isNotNull();
	}
	
	@Test
	void upgradeLevels() {
		userDao.deleteAll();
		for (User user : users) {
			userDao.add(user);
		}
		
		userService.upgradeLevels();
		
		checkLevel(users.get(0), Level.BASIC);
		checkLevel(users.get(1), Level.SILVER);
		checkLevel(users.get(2), Level.SILVER);
		checkLevel(users.get(3), Level.GOLD);
		checkLevel(users.get(4), Level.GOLD);
	}
	
	private void checkLevel(User user, Level expectedLevel) {
		User userUpdate = userDao.get(user.getId());
		assertThat(userUpdate.getLevel()).isEqualTo(expectedLevel);
	}
}
