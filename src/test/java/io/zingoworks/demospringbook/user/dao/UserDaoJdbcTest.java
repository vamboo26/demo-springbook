package io.zingoworks.demospringbook.user.dao;

import io.zingoworks.demospringbook.user.domain.Level;
import io.zingoworks.demospringbook.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class) // replace junit4 @Runwith(SpringJUnit4ClassRunner.class)
@SpringBootTest
class UserDaoJdbcTest {
	
	@Autowired
	private UserDao dao;
	
	private User user1;
	private User user2;
	private User user3;
	
	@BeforeEach
	void setUp() {
		this.user1 = new User("1", "one", "1234", Level.BASIC, 1, 0);
		this.user2 = new User("2", "two", "1234", Level.SILVER, 55, 10);
		this.user3 = new User("3", "three", "1234", Level.GOLD, 100, 40);
	}
	
	@Test
	void duplicateKey() {
		assertThrows(DuplicateKeyException.class,
				() -> {
					dao.deleteAll();
					dao.add(user1);
					dao.add(user1); // key 중복 발생
				});
	}
	
	@Test
	void dataAccessException() {
		assertThrows(DataAccessException.class,
				() -> {
					dao.deleteAll();
					dao.add(user1);
					dao.add(user1); // key 중복 발생
				});
	}
	
	@Test
	void addAndGet() {
		dao.deleteAll();
		assertThat(dao.getCount()).isEqualTo(0);
		
		dao.add(user1);
		dao.add(user2);
		assertThat(dao.getCount()).isEqualTo(2);
		
		User userget1 = dao.get(user1.getId());
		assertThat(userget1.getName()).isEqualTo(user1.getName());
		assertThat(userget1.getPassword()).isEqualTo(user1.getPassword());
		
		User userget2 = dao.get(user2.getId());
		assertThat(userget2.getName()).isEqualTo(user2.getName());
		assertThat(userget2.getPassword()).isEqualTo(user2.getPassword());
	}
	
	@Test
	void getUserFailure() {
		dao.deleteAll();
		assertThat(dao.getCount()).isEqualTo(0);
		
		assertThrows(EmptyResultDataAccessException.class, () -> dao.get("unknown_id"));
	}
	
	@Test
	void count() {
		dao.deleteAll();
		assertThat(dao.getCount()).isEqualTo(0);
		
		dao.add(user1);
		assertThat(dao.getCount()).isEqualTo(1);
		
		dao.add(user2);
		assertThat(dao.getCount()).isEqualTo(2);
		
		dao.add(user3);
		assertThat(dao.getCount()).isEqualTo(3);
	}
	
	@Test
	void getAll() {
		dao.deleteAll();
		
		List<User> users0 = dao.getAll();
		assertThat(users0.size()).isEqualTo(0);
		
		dao.add(user1);
		List<User> users1 = dao.getAll();
		assertThat(users1.size()).isEqualTo(1);
		checkSameUser(user1, users1.get(0));
		
		dao.add(user2);
		List<User> users2 = dao.getAll();
		assertThat(users2.size()).isEqualTo(2);
		checkSameUser(user1, users2.get(0));
		checkSameUser(user2, users2.get(1));
		
		dao.add(user3);
		List<User> users3 = dao.getAll();
		assertThat(users3.size()).isEqualTo(3);
		checkSameUser(user1, users3.get(0));
		checkSameUser(user2, users3.get(1));
		checkSameUser(user3, users3.get(2));
	}
	
	@Test
	void update() {
		dao.deleteAll();
		
		dao.add(user1);
		dao.add(user2);
		
		user1.setName("빠니보틀");
		user1.setPassword("ppap");
		user1.setLevel(Level.GOLD);
		user1.setLoginSequence(1000);
		user1.setRecommendationCount(999);
		
		dao.update(user1);
		
		User user1update = dao.get(user1.getId());
		checkSameUser(user1, user1update);
		User user2same = dao.get(user2.getId());
		checkSameUser(user2, user2same);
	}
	
	private void checkSameUser(User user1, User user2) {
		assertThat(user1.getId()).isEqualTo(user2.getId());
		assertThat(user1.getName()).isEqualTo(user2.getName());
		assertThat(user1.getPassword()).isEqualTo(user2.getPassword());
		assertThat(user1.getLevel()).isEqualTo(user2.getLevel());
		assertThat(user1.getLoginSequence()).isEqualTo(user2.getLoginSequence());
		assertThat(user1.getRecommendationCount()).isEqualTo(user2.getRecommendationCount());
	}
}
