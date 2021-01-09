package io.zingoworks.demospringbook.user;

import io.zingoworks.demospringbook.user.dao.DaoFactory;
import io.zingoworks.demospringbook.user.dao.UserDao;
import io.zingoworks.demospringbook.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.SQLException;
import java.time.LocalTime;

@Slf4j
public class UserDaoTest {
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		ApplicationContext applicationContext = new AnnotationConfigApplicationContext(DaoFactory.class);
		
		UserDao dao = applicationContext.getBean("userDao", UserDao.class);
		
		User user = new User();
		user.setId(String.valueOf(LocalTime.now()).substring(0, 10));
		user.setName("징고");
		user.setPassword("1234");
		
		dao.add(user);
		
		log.info("{} 등록 성공", user.getId());
		
		User user2 = dao.get(user.getId());
		log.debug("이름:{}", user2.getName());
		log.debug("암호:{}", user2.getPassword());
		log.debug("{} 조회 성공", user2.getId());
	}
}
