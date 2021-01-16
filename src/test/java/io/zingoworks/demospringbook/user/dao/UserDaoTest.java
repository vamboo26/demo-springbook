package io.zingoworks.demospringbook.user.dao;

import io.zingoworks.demospringbook.user.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserDaoTest {
	
	@Test
	void addAndGet() throws SQLException {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		
		UserDao dao = context.getBean("userDao", UserDao.class);
		User user1 = new User("1", "one", "1234");
		User user2 = new User("2", "two", "1234");
		
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
		GenericXmlApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
		
		UserDao dao = context.getBean("userDao", UserDao.class);
		dao.deleteAll();
		assertThat(dao.getCount()).isEqualTo(0);
		
		assertThrows(EmptyResultDataAccessException.class, () -> dao.get("unknown_id"));
	}
	
	@Test
	void count() throws SQLException {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		
		UserDao dao = context.getBean("userDao", UserDao.class);
		User user1 = new User("1", "one", "1234");
		User user2 = new User("2", "two", "1234");
		User user3 = new User("3", "three", "1234");
		
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
