package io.zingoworks.demospringbook;

import io.zingoworks.demospringbook.user.dao.UserDao;
import io.zingoworks.demospringbook.user.domain.User;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;

@Slf4j
public class DemoSpringbookApplication {
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		UserDao dao = new UserDao();
		
		User user = new User();
		user.setId("zingoworks");
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
