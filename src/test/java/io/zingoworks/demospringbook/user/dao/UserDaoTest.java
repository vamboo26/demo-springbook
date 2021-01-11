package io.zingoworks.demospringbook.user.dao;

import io.zingoworks.demospringbook.user.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.sql.SQLException;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserDaoTest {
	
	@Test
	void addAndGet() throws SQLException {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		
		UserDao dao = context.getBean("userDao", UserDao.class);
		User user = new User();
		user.setId(String.valueOf(LocalTime.now()).substring(0, 10));
		user.setName("징고");
		user.setPassword("1234");
		
		dao.deleteAll();
		assertThat(dao.getCount()).isEqualTo(0);
		
		dao.add(user);
		assertThat(dao.getCount()).isEqualTo(1);
		
		User user2 = dao.get(user.getId());
		
		assertThat(user2.getName()).isEqualTo(user.getName());
		assertThat(user2.getPassword()).isEqualTo(user.getPassword());
	}
}