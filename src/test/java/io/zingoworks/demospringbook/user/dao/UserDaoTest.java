package io.zingoworks.demospringbook.user.dao;

import io.zingoworks.demospringbook.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class) // replace junit4 @Runwith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext.xml")
class UserDaoTest {
	
	@Autowired
	private ApplicationContext context;
	
	@Autowired
	private UserDao dao;
	
	private User user1;
	private User user2;
	private User user3;
	
	@BeforeEach
	void setUp() {
		System.out.println("this.context = " + this.context); // context는 동일 오브젝트 재사용
		System.out.println("this = " + this); // UserDaoTest는 매번 새로운 오브젝트
		
		this.user1 = new User("1", "one", "1234");
		this.user2 = new User("2", "two", "1234");
		this.user3 = new User("3", "three", "1234");
	}
	
	@Test
	void addAndGet() throws SQLException {
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
	void getUserFailure() throws SQLException {
		dao.deleteAll();
		assertThat(dao.getCount()).isEqualTo(0);
		
		assertThrows(EmptyResultDataAccessException.class, () -> dao.get("unknown_id"));
	}
	
	@Test
	void count() throws SQLException {
		dao.deleteAll();
		assertThat(dao.getCount()).isEqualTo(0);
		
		dao.add(user1);
		assertThat(dao.getCount()).isEqualTo(1);
		
		dao.add(user2);
		assertThat(dao.getCount()).isEqualTo(2);
		
		dao.add(user3);
		assertThat(dao.getCount()).isEqualTo(3);
	}
}
