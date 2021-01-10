package io.zingoworks.demospringbook.user;

import io.zingoworks.demospringbook.user.dao.CountingConnectionMaker;
import io.zingoworks.demospringbook.user.dao.UserDao;
import io.zingoworks.demospringbook.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import java.sql.SQLException;
import java.time.LocalTime;

@Slf4j
public class UserDaoTest {
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
//		ApplicationContext applicationContext = new AnnotationConfigApplicationContext(CountingDaoFactory.class);
		ApplicationContext applicationContext = new GenericXmlApplicationContext("application-context.xml");
		
		UserDao dao = applicationContext.getBean("userDao", UserDao.class);
		UserDao dao2 = applicationContext.getBean("userDao", UserDao.class);
		UserDao dao3 = applicationContext.getBean("userDao", UserDao.class);
		
		System.out.println("dao2 = " + dao2);
		System.out.println("dao3 = " + dao3);
		
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
		
		dao.get(user.getId());
		dao.get(user.getId());
		dao.get(user.getId());
		dao.get(user.getId());
		dao.get(user.getId());
		
		CountingConnectionMaker ccm = applicationContext.getBean("connectionMaker", CountingConnectionMaker.class);
		System.out.println("ccm.getCounter() = " + ccm.getCounter());
	}
}
