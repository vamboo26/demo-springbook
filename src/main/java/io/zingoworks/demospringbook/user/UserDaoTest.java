package io.zingoworks.demospringbook.user;

import io.zingoworks.demospringbook.user.dao.UserDao;
import io.zingoworks.demospringbook.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import java.sql.SQLException;
import java.time.LocalTime;

@Slf4j
public class UserDaoTest {
	
	public static void main(String[] args) throws SQLException {
		ApplicationContext applicationContext = new GenericXmlApplicationContext("application-context.xml");
		
		UserDao dao = applicationContext.getBean("userDao", UserDao.class);
		
		User user = new User();
		user.setId(String.valueOf(LocalTime.now()).substring(0, 10));
		user.setName("징고");
		user.setPassword("1234");
		
		dao.add(user);
		
		User user2 = dao.get(user.getId());
		if (!user.getName().equals(user2.getName())) {
			System.out.println("테스트 실패 (name)");
		} else if (!user.getPassword().equals(user2.getPassword())) {
			System.out.println("테스트 실패 (password)");
		} else {
			System.out.println("조회 테스트 성공");
		}
	}
}
